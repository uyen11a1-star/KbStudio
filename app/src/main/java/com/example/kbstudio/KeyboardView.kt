package com.example.kbstudio

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.SoundEffectConstants
import android.view.View
import android.view.animation.DecelerateInterpolator
import java.util.concurrent.ConcurrentHashMap

class KeyboardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    interface Listener { fun onKey(key: KeyDef) }

    var listener: Listener? = null
    var previewMode = false

    private var theme = KbTheme.default()
    private var themeManager = ThemeManager(context)
    private var layout: List<List<KeyDef>> = KeyLayouts.LETTERS
    private var currentLayoutKind = KIND_LETTERS

    private var shiftState = 0 // 0=off, 1=oneshot, 2=caps
    private var lastShiftTap = 0L
    private var numRowVisible = false

    private val keys = ArrayList<KeyEntry>()
    private var pressedIndex = -1
    private var downIndex = -1

    // Popup state
    private var popupKey: KeyEntry? = null
    private var popupOptions: List<KeyDef> = emptyList()
    private var popupSelected = -1
    private var popupRects = ArrayList<RectF>()

    // Image cache
    private val imageCache = ConcurrentHashMap<String, Bitmap?>()

    private val keyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER; isFakeBoldText = false
    }
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val popupBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL; color = 0xFF2A2A2A.toInt()
    }
    private val popupHiPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL; color = 0xFFFF9800.toInt()
    }
    private val popupBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; color = 0xFF666666.toInt(); strokeWidth = 2f
    }
    private var bgBitmap: Bitmap? = null

    private val repeatHandler = Handler(Looper.getMainLooper())
    private var repeatRunnable: Runnable? = null
    private var longPressRunnable: Runnable? = null
    private var pressStartTime = 0L

    // scale animation
    private val scaleMap = HashMap<Int, Float>()

    private data class KeyEntry(val key: KeyDef, val rect: RectF, val isNumberRow: Boolean = false)

    companion object {
        const val KIND_LETTERS = 0
        const val KIND_SYMBOLS = 1
        const val KIND_EMOJI = 2
    }

    init {
        isHapticFeedbackEnabled = true
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    fun setTheme(t: KbTheme) {
        theme = t
        themeManager = ThemeManager(context)
        loadBgBitmap()
        requestLayout()
        invalidate()
    }

    fun setEmojiLayout(emoji: List<String>) {
        val perRow = 8
        val rows = ArrayList<List<KeyDef>>()
        var i = 0
        while (i < emoji.size) {
            val chunk = emoji.subList(i, minOf(i + perRow, emoji.size))
            rows.add(chunk.map { KeyDef(it, it, KeyDef.CODE_CHAR, 1f) })
            i += perRow
        }
        // Thêm hàng điều khiển cuối
        rows.add(listOf(
            KeyDef("ABC","",KeyDef.CODE_ABC,1.5f),
            KeyDef("?123","",KeyDef.CODE_SYMBOLS,1.5f),
            KeyDef("⌫","",KeyDef.CODE_BACKSPACE,1.5f),
            KeyDef(" "," ",KeyDef.CODE_SPACE,3f),
            KeyDef("⏎","\n",KeyDef.CODE_ENTER,1.5f)
        ))
        layout = rows
        currentLayoutKind = KIND_EMOJI
        numRowVisible = false
        computeLayout(width.toFloat(), height.toFloat())
        invalidate()
    }

    fun resetToLetters() {
        layout = KeyLayouts.LETTERS
        currentLayoutKind = KIND_LETTERS
        numRowVisible = theme.showNumberRow
        computeLayout(width.toFloat(), height.toFloat())
        invalidate()
    }

    private fun loadBgBitmap() {
        val uri = theme.bgImageUri
        if (uri.isNullOrEmpty()) { bgBitmap = null; invalidate(); return }
        Thread {
            try {
                val bmp = context.contentResolver.openInputStream(Uri.parse(uri))?.use {
                    BitmapFactory.decodeStream(it)
                }
                post { bgBitmap = bmp; invalidate() }
            } catch (_: Exception) { post { bgBitmap = null; invalidate() } }
        }.start()
    }

    private fun loadKeyImage(label: String): Bitmap? {
        imageCache[label]?.let { return it }
        val uri = themeManager.getKeyImage(label) ?: return null
        return try {
            val bmp = context.contentResolver.openInputStream(Uri.parse(uri))?.use {
                BitmapFactory.decodeStream(it)
            }
            imageCache[label] = bmp
            bmp
        } catch (_: Exception) { null }
    }

    private fun dp(v: Float) = v * resources.displayMetrics.density
    private fun sp(v: Float) = v * resources.displayMetrics.scaledDensity

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        computeLayout(w.toFloat(), h.toFloat())
    }

    private fun computeLayout(w: Float, h: Float) {
        keys.clear()
        val allRows = ArrayList<Pair<List<KeyDef>, Boolean>>()
        if (numRowVisible && currentLayoutKind != KIND_EMOJI) {
            allRows.add(Pair(KeyLayouts.NUMBER_ROW, true))
        }
        layout.forEach { allRows.add(Pair(it, false)) }

        val pad = dp(4f)
        val rowGap = dp(theme.rowGapDp)
        val keyGap = dp(theme.keyGapDp)
        val innerW = w - pad * 2
        val innerH = h - pad * 2
        if (innerH <= 0f || innerW <= 0f) return
        val rowH = (innerH - rowGap * (allRows.size - 1)) / allRows.size

        allRows.forEachIndexed { ri, (row, isNum) ->
            val totalW = row.sumOf { it.widthWeight.toDouble() }.toFloat()
            val totalGap = keyGap * (row.size - 1)
            val availW = innerW - totalGap
            var x = pad
            val y = pad + ri * (rowH + rowGap)
            row.forEach { key ->
                val kw = availW * key.widthWeight / totalW
                keys.add(KeyEntry(key, RectF(x, y, x + kw, y + rowH), isNum))
                x += kw + keyGap
            }
        }
    }

    private fun displayLabel(key: KeyDef): String = when (key.code) {
        KeyDef.CODE_SHIFT -> if (shiftState == 2) "⇪" else "⇧"
        KeyDef.CODE_CHAR -> {
            if (shiftState > 0 && key.output.length == 1 && key.output[0].isLetter())
                key.output.uppercase() else key.label
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
            val bgEnd = theme.bgColorEnd; if (bgEnd != null) {
                bgPaint.shader = LinearGradient(0f, 0f, 0f, height.toFloat(),
                    theme.bgColor, bgEnd, Shader.TileMode.CLAMP)
            } else bgPaint.shader = null
            bgPaint.color = theme.bgColor
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
        }

        val radius = dp(theme.keyCornerRadiusDp)
        val borderW = dp(theme.keyBorderWidthDp)

        // Draw keys
        keys.forEachIndexed { i, entry ->
            val rect = entry.rect
            val pressed = i == pressedIndex
            val scale = scaleMap[i] ?: 1f
            val cx = rect.centerX(); val cy = rect.centerY()
            val scaled = RectF(
                cx + (rect.left - cx) * scale,
                cy + (rect.top - cy) * scale,
                cx + (rect.right - cx) * scale,
                cy + (rect.bottom - cy) * scale
            )
            if (pressed) {
                keyPaint.shader = null
                keyPaint.color = theme.keyPressedColor
            } else {
                val keyEnd = theme.keyColorEnd; if (keyEnd != null) {
                    keyPaint.shader = LinearGradient(0f, scaled.top, 0f, scaled.bottom,
                        theme.keyColor, keyEnd, Shader.TileMode.CLAMP)
                } else keyPaint.shader = null
                keyPaint.color = theme.keyColor
            }
            canvas.drawRoundRect(scaled, radius, radius, keyPaint)
            if (borderW > 0f) {
                borderPaint.color = theme.keyBorderColor
                borderPaint.strokeWidth = borderW
                canvas.drawRoundRect(scaled, radius, radius, borderPaint)
            }
        }

        // Draw labels / images
        keys.forEach { entry ->
            val rect = entry.rect
            val img = loadKeyImage(entry.key.label)
            if (img != null) {
                val inset = dp(8f)
                val dst = RectF(rect.left + inset, rect.top + inset, rect.right - inset, rect.bottom - inset)
                val side = minOf(dst.width(), dst.height())
                val cx = dst.centerX(); val cy = dst.centerY()
                val square = RectF(cx - side/2, cy - side/2, cx + side/2, cy + side/2)
                canvas.drawBitmap(img, null, square, null)
            } else {
                val label = displayLabel(entry.key)
                var size = sp(theme.fontSizeSp)
                textPaint.textSize = size
                textPaint.color = theme.keyTextColor
                val maxW = rect.width() - dp(6f)
                while (textPaint.measureText(label) > maxW && size > sp(8f)) {
                    size -= sp(0.5f); textPaint.textSize = size
                }
                val ty = rect.centerY() - (textPaint.descent() + textPaint.ascent()) / 2f
                canvas.drawText(label, rect.centerX(), ty, textPaint)
            }
        }

        // Draw popup
        popupKey?.let { drawPopup(canvas, it) }
    }

    private fun drawPopup(canvas: Canvas, entry: KeyEntry) {
        if (popupOptions.isEmpty()) return
        val keyRect = entry.rect
        val cell = dp(44f)
        val n = popupOptions.size
        // Limit 8 per row
        val perRow = 8
        val rows = (n + perRow - 1) / perRow
        val popupW = cell * minOf(n, perRow) + dp(8f)
        val popupH = cell * rows + dp(8f)
        var left = keyRect.centerX() - popupW / 2
        if (left < dp(4f)) left = dp(4f)
        if (left + popupW > width - dp(4f)) left = width - popupW - dp(4f)
        var top = keyRect.top - popupH - dp(6f)
        if (top < dp(4f)) top = keyRect.bottom + dp(6f)

        val popupRect = RectF(left, top, left + popupW, top + popupH)
        canvas.drawRoundRect(popupRect, dp(10f), dp(10f), popupBgPaint)
        canvas.drawRoundRect(popupRect, dp(10f), dp(10f), popupBorderPaint)

        popupRects.clear()
        textPaint.textSize = sp(theme.fontSizeSp + 4f)
        textPaint.color = theme.keyTextColor
        for (i in popupOptions.indices) {
            val r = i / perRow; val c = i % perRow
            val cellRect = RectF(
                popupRect.left + dp(4f) + c * cell,
                popupRect.top + dp(4f) + r * cell,
                popupRect.left + dp(4f) + (c + 1) * cell,
                popupRect.top + dp(4f) + (r + 1) * cell
            )
            popupRects.add(cellRect)
            if (i == popupSelected) {
                canvas.drawRoundRect(cellRect, dp(6f), dp(6f), popupHiPaint)
            }
            val t = popupOptions[i].label
            val ty = cellRect.centerY() - (textPaint.descent() + textPaint.ascent()) / 2f
            canvas.drawText(t, cellRect.centerX(), ty, textPaint)
        }
    }

    private fun hitTest(x: Float, y: Float): Int {
        keys.forEachIndexed { i, e -> if (e.rect.contains(x, y)) return i }
        return -1
    }

    private fun hitPopup(x: Float, y: Float): Int {
        popupRects.forEachIndexed { i, r -> if (r.contains(x, y)) return i }
        return -1
    }

    private fun feedback() {
        if (previewMode) return
        if (theme.soundEnabled) playSoundEffect(SoundEffectConstants.CLICK)
        if (theme.vibrateEnabled) performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    }

    private fun animatePress(idx: Int) {
        if (!theme.animEnabled) return
        val anim = ValueAnimator.ofFloat(0.85f, 1f).apply {
            duration = 120
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                scaleMap[idx] = it.animatedValue as Float
                invalidate()
            }
        }
        anim.start()
    }

    private fun toggleShift() {
        val now = System.currentTimeMillis()
        shiftState = when {
            shiftState == 0 -> 1
            shiftState == 1 && now - lastShiftTap < 400 -> 2
            else -> 0
        }
        lastShiftTap = now
        invalidate()
    }

    private fun fireKey(key: KeyDef, popupChoice: Boolean = false) {
        when (key.code) {
            KeyDef.CODE_SHIFT -> toggleShift()
            KeyDef.CODE_SYMBOLS -> {
                layout = KeyLayouts.SYMBOLS
                currentLayoutKind = KIND_SYMBOLS
                numRowVisible = false
                computeLayout(width.toFloat(), height.toFloat()); invalidate()
            }
            KeyDef.CODE_ABC -> {
                layout = KeyLayouts.LETTERS
                currentLayoutKind = KIND_LETTERS
                numRowVisible = theme.showNumberRow
                computeLayout(width.toFloat(), height.toFloat()); invalidate()
            }
            KeyDef.CODE_EMOJI -> {
                if (currentLayoutKind != KIND_EMOJI) {
                    setEmojiLayout(EmojiData.SMILEYS)
                }
            }
            KeyDef.CODE_CHAR -> {
                val out = if (shiftState > 0) key.output.uppercase() else key.output
                listener?.onKey(key.copy(output = out))
                if (shiftState == 1 && !popupChoice) { shiftState = 0; invalidate() }
            }
            else -> listener?.onKey(key)
        }
        if (key.code == KeyDef.CODE_BACKSPACE && !popupChoice) scheduleRepeat(key)
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

    private fun cancelMyLongPress() {
        longPressRunnable?.let { repeatHandler.removeCallbacks(it) }
        longPressRunnable = null
    }

    private fun showPopup(idx: Int) {
        if (!theme.popupEnabled) return
        val entry = keys[idx]
        if (entry.key.longPress.isEmpty()) return
        popupKey = entry
        popupOptions = entry.key.longPress
        popupSelected = -1
        invalidate()
    }

    private fun hidePopup() {
        popupKey = null
        popupOptions = emptyList()
        popupSelected = -1
        popupRects.clear()
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (previewMode) return false
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                pressStartTime = System.currentTimeMillis()
                downIndex = hitTest(event.x, event.y)
                pressedIndex = downIndex
                if (downIndex >= 0) {
                    feedback()
                    val key = keys[downIndex].key
                    if (theme.popupEnabled && key.longPress.isNotEmpty() && key.code == KeyDef.CODE_CHAR) {
                        val idx = downIndex
                        longPressRunnable = Runnable { showPopup(idx) }
                        repeatHandler.postDelayed(longPressRunnable!!, 400L)
                    }
                }
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (popupKey != null) {
                    val sel = hitPopup(event.x, event.y)
                    if (sel != popupSelected) { popupSelected = sel; invalidate() }
                    return true
                }
                if (downIndex >= 0) {
                    val idx = hitTest(event.x, event.y)
                    if (idx != pressedIndex) { pressedIndex = idx; invalidate() }
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                cancelMyLongPress()
                if (popupKey != null) {
                    val sel = hitPopup(event.x, event.y)
                    if (sel >= 0) {
                        val chosen = popupOptions[sel]
                        fireKey(chosen, popupChoice = true)
                        if (shiftState == 1) { shiftState = 0 }
                    }
                    hidePopup()
                    pressedIndex = -1; downIndex = -1
                    invalidate()
                    return true
                }
                val upIdx = hitTest(event.x, event.y)
                if (upIdx >= 0 && upIdx == downIndex) {
                    animatePress(upIdx)
                    fireKey(keys[upIdx].key)
                }
                cancelRepeat()
                pressedIndex = -1; downIndex = -1
                invalidate()
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                cancelRepeat(); cancelMyLongPress(); hidePopup()
                pressedIndex = -1; downIndex = -1
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}
