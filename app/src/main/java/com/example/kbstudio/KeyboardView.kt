package com.example.kbstudio

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.SoundEffectConstants
import android.view.View

class KeyboardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    interface Listener {
        fun onKey(key: KeyDef)
    }

    var listener: Listener? = null
    var previewMode = false
    var showShift = true

    private var theme = KbTheme.default()
    private var layout: List<List<KeyDef>> = KeyLayouts.LETTERS
    private var isSymbols = false

    // shift: 0=off, 1=one-shot, 2=caps lock
    private var shiftState = 0
    private var lastShiftTap = 0L

    private val keys = ArrayList<KeyEntry>()
    private var pressedIndex = -1
    private var downIndex = -1

    private val keyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        isFakeBoldText = false
    }
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private var bgBitmap: Bitmap? = null

    private val repeatHandler = Handler(Looper.getMainLooper())
    private var repeatRunnable: Runnable? = null

    private data class KeyEntry(val key: KeyDef, val rect: RectF)

    init {
        isHapticFeedbackEnabled = true
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    fun setTheme(t: KbTheme) {
        theme = t
        loadBgBitmap()
        requestLayout()
        invalidate()
    }

    fun resetLayout() {
        layout = KeyLayouts.LETTERS
        isSymbols = false
        requestLayout()
        invalidate()
    }

    private fun loadBgBitmap() {
        val uri = theme.bgImageUri
        if (uri.isNullOrEmpty()) {
            bgBitmap = null
            invalidate()
            return
        }
        Thread {
            try {
                val bmp = context.contentResolver.openInputStream(Uri.parse(uri))?.use {
                    BitmapFactory.decodeStream(it)
                }
                post { bgBitmap = bmp; invalidate() }
            } catch (_: Exception) {
                post { bgBitmap = null; invalidate() }
            }
        }.start()
    }

    private fun dp(v: Float) = v * resources.displayMetrics.density
    private fun sp(v: Float) = v * resources.displayMetrics.scaledDensity

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        computeLayout(w.toFloat(), h.toFloat())
    }

    private fun computeLayout(w: Float, h: Float) {
        keys.clear()
        val rows = layout
        val pad = dp(4f)
        val rowGap = dp(theme.rowGapDp)
        val keyGap = dp(theme.keyGapDp)
        val innerW = w - pad * 2
        val innerH = h - pad * 2
        if (innerH <= 0f || innerW <= 0f) return
        val rowH = (innerH - rowGap * (rows.size - 1)) / rows.size

        rows.forEachIndexed { ri, row ->
            val totalW = row.sumOf { it.widthWeight.toDouble() }.toFloat()
            val totalGap = keyGap * (row.size - 1)
            val availW = innerW - totalGap
            var x = pad
            val y = pad + ri * (rowH + rowGap)
            row.forEach { key ->
                val kw = availW * key.widthWeight / totalW
                keys.add(KeyEntry(key, RectF(x, y, x + kw, y + rowH)))
                x += kw + keyGap
            }
        }
    }

    private fun displayLabel(key: KeyDef): String = when (key.code) {
        KeyDef.CODE_SHIFT -> if (shiftState == 2) "⇪" else "⇧"
        KeyDef.CODE_CHAR -> {
            if (shiftState > 0 && key.output.length == 1 && key.output[0].isLetter())
                key.output.uppercase()
            else key.label
        }
        else -> key.label
    }

    override fun onDraw(canvas: Canvas) {
        // Background
        val bmp = bgBitmap
        if (bmp != null) {
            canvas.drawBitmap(bmp, null, RectF(0f, 0f, width.toFloat(), height.toFloat()), null)
            bgPaint.color = Color.argb(120, 0, 0, 0)
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
        } else {
            bgPaint.color = theme.bgColor
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
        }

        val radius = dp(theme.keyCornerRadiusDp)
        val borderW = dp(theme.keyBorderWidthDp)

        keys.forEachIndexed { i, entry ->
            val rect = entry.rect
            val pressed = i == pressedIndex
            keyPaint.color = if (pressed) theme.keyPressedColor else theme.keyColor
            canvas.drawRoundRect(rect, radius, radius, keyPaint)
            if (borderW > 0f) {
                borderPaint.color = theme.keyBorderColor
                borderPaint.strokeWidth = borderW
                canvas.drawRoundRect(rect, radius, radius, borderPaint)
            }
        }

        keys.forEach { entry ->
            val rect = entry.rect
            val label = displayLabel(entry.key)
            var size = sp(theme.fontSizeSp)
            textPaint.textSize = size
            textPaint.color = theme.keyTextColor
            val maxW = rect.width() - dp(6f)
            while (textPaint.measureText(label) > maxW && size > sp(8f)) {
                size -= sp(0.5f)
                textPaint.textSize = size
            }
            val ty = rect.centerY() - (textPaint.descent() + textPaint.ascent()) / 2f
            canvas.drawText(label, rect.centerX(), ty, textPaint)
        }
    }

    private fun hitTest(x: Float, y: Float): Int {
        keys.forEachIndexed { i, e -> if (e.rect.contains(x, y)) return i }
        return -1
    }

    private fun feedback() {
        if (previewMode) return
        if (theme.soundEnabled) playSoundEffect(SoundEffectConstants.CLICK)
        if (theme.vibrateEnabled) performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    }

    private fun toggleShift() {
        val now = System.currentTimeMillis()
        shiftState = when {
            shiftState == 0 -> 1
            shiftState == 1 && now - lastShiftTap < 400 -> 2
            shiftState == 1 -> 0
            else -> 0
        }
        lastShiftTap = now
        invalidate()
    }

    private fun fireKey(key: KeyDef) {
        when (key.code) {
            KeyDef.CODE_SHIFT -> toggleShift()
            KeyDef.CODE_SYMBOLS -> {
                layout = KeyLayouts.SYMBOLS; isSymbols = true
                computeLayout(width.toFloat(), height.toFloat()); invalidate()
            }
            KeyDef.CODE_ABC -> {
                layout = KeyLayouts.LETTERS; isSymbols = false
                computeLayout(width.toFloat(), height.toFloat()); invalidate()
            }
            KeyDef.CODE_CHAR -> {
                val out = if (shiftState > 0) key.output.uppercase() else key.output
                listener?.onKey(key.copy(output = out))
                if (shiftState == 1) { shiftState = 0; invalidate() }
            }
            else -> listener?.onKey(key)
        }
        if (key.code == KeyDef.CODE_BACKSPACE) scheduleRepeat(key)
    }

    private fun scheduleRepeat(key: KeyDef) {
        cancelRepeat()
        val r = object : Runnable {
            var count = 0
            override fun run() {
                count++
                listener?.onKey(key)
                repeatHandler.postDelayed(this, if (count > 3) 45L else 80L)
            }
        }
        repeatRunnable = r
        repeatHandler.postDelayed(r, 400L)
    }

    private fun cancelRepeat() {
        repeatRunnable?.let { repeatHandler.removeCallbacks(it) }
        repeatRunnable = null
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (previewMode) return false
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downIndex = hitTest(event.x, event.y)
                pressedIndex = downIndex
                if (downIndex >= 0) feedback()
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (downIndex >= 0) {
                    val idx = hitTest(event.x, event.y)
                    if (idx != pressedIndex) {
                        pressedIndex = idx
                        invalidate()
                    }
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                val upIdx = hitTest(event.x, event.y)
                if (upIdx >= 0 && upIdx == downIndex) fireKey(keys[upIdx].key)
                cancelRepeat()
                pressedIndex = -1; downIndex = -1
                invalidate()
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                cancelRepeat()
                pressedIndex = -1; downIndex = -1
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}
