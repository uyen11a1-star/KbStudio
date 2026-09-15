package com.example.kbstudio

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var tm: ThemeManager
    private lateinit var preview: KeyboardView

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {}
            tm.bgImageUri = uri.toString()
            refresh()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tm = ThemeManager(this)

        val scroll = ScrollView(this).apply { setBackgroundColor(0xFF111111.toInt()) }
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14f), dp(14f), dp(14f), dp(30f))
        }
        scroll.addView(root)
        setContentView(scroll)

        root.addView(title("⌨️ Tùy chỉnh bàn phím"))

        preview = KeyboardView(this).apply {
            previewMode = true
            isFocusable = false
            isClickable = false
        }
        val previewH = dp(tm.keyboardHeightDp.toFloat())
        root.addView(preview, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, previewH
        ).apply { bottomMargin = dp(20f) })
        preview.setTheme(tm.load())

        root.addView(section("🎨 Màu sắc"))
        root.addView(colorPicker("Màu nền", tm.bgColor) { tm.bgColor = it; refresh() })
        root.addView(colorPicker("Màu phím", tm.keyColor) { tm.keyColor = it; refresh() })
        root.addView(colorPicker("Màu phím nhấn", tm.keyPressedColor) { tm.keyPressedColor = it; refresh() })
        root.addView(colorPicker("Màu chữ", tm.keyTextColor) { tm.keyTextColor = it; refresh() })
        root.addView(colorPicker("Màu viền ô", tm.keyBorderColor) { tm.keyBorderColor = it; refresh() })

        root.addView(section("📏 Kích thước"))
        root.addView(slider("Cỡ chữ (sp)", 10f, 32f, tm.fontSizeSp) {
            tm.fontSizeSp = it; refresh()
        })
        root.addView(slider("Chiều cao bàn phím (dp)", 160f, 400f, tm.keyboardHeightDp.toFloat()) {
            tm.keyboardHeightDp = it.toInt(); refresh()
        })
        root.addView(slider("Bo góc phím (dp)", 0f, 30f, tm.keyCornerRadiusDp) {
            tm.keyCornerRadiusDp = it; refresh()
        })
        root.addView(slider("Độ dày viền (dp)", 0f, 6f, tm.keyBorderWidthDp) {
            tm.keyBorderWidthDp = it; refresh()
        })
        root.addView(slider("Khoảng cách phím (dp)", 0f, 12f, tm.keyGapDp) {
            tm.keyGapDp = it; refresh()
        })
        root.addView(slider("Khoảng cách hàng (dp)", 0f, 20f, tm.rowGapDp) {
            tm.rowGapDp = it; refresh()
        })

        root.addView(section("🔊 Hiệu ứng"))
        root.addView(toggle("Âm thanh khi bấm", tm.soundEnabled) {
            tm.soundEnabled = it
        })
        root.addView(toggle("Rung khi bấm", tm.vibrateEnabled) {
            tm.vibrateEnabled = it
        })

        root.addView(section("🇻🇳 Tiếng Việt"))
        root.addView(toggle("Bật Telex (aa→â, aw→ă, dd→đ...)", tm.telexEnabled) {
            tm.telexEnabled = it
        })

        root.addView(section("🖼️ Ảnh nền"))
        root.addView(button("Chọn ảnh từ máy") {
            pickImage.launch(arrayOf("image/*"))
        })
        root.addView(button("Xóa ảnh nền") {
            tm.bgImageUri = null; refresh()
        })

        root.addView(section("⚙️ Khác"))
        root.addView(button("Reset về mặc định") {
            tm.resetAll(); refresh()
            Toast.makeText(this, "Đã reset", Toast.LENGTH_SHORT).show()
        })
    }

    private fun refresh() {
        preview.setTheme(tm.load())
        preview.requestLayout()
    }

    private fun dp(v: Float) = (v * resources.displayMetrics.density).toInt()

    private fun title(t: String) = TextView(this).apply {
        text = t
        setTextColor(0xFFFF9800.toInt())
        textSize = 24f
        gravity = Gravity.CENTER
        setPadding(0, 0, 0, dp(20f))
    }

    private fun section(t: String) = TextView(this).apply {
        text = t
        setTextColor(0xFFFFEB3B.toInt())
        textSize = 16f
        setPadding(0, dp(20f), 0, dp(8f))
    }

    private fun button(t: String, onClick: () -> Unit) = Button(this).apply {
        text = t
        setOnClickListener { onClick() }
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = dp(8f) }
    }

    private val presets = intArrayOf(
        0xFF000000.toInt(), 0xFF1E1E1E.toInt(), 0xFF3A3A3A.toInt(), 0xFF616161.toInt(),
        0xFF9E9E9E.toInt(), 0xFFFFFFFF.toInt(), 0xFFF44336.toInt(), 0xFFE91E63.toInt(),
        0xFF9C27B0.toInt(), 0xFF673AB7.toInt(), 0xFF3F51B5.toInt(), 0xFF2196F3.toInt(),
        0xFF03A9F4.toInt(), 0xFF00BCD4.toInt(), 0xFF009688.toInt(), 0xFF4CAF50.toInt(),
        0xFF8BC34A.toInt(), 0xFFCDDC39.toInt(), 0xFFFFEB3B.toInt(), 0xFFFFC107.toInt(),
        0xFFFF9800.toInt(), 0xFFFF5722.toInt(), 0xFF795548.toInt(), 0xFF607D8B.toInt()
    )

    private fun colorPicker(label: String, current: Int, onPick: (Int) -> Unit): View {
        val wrapper = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(6f), 0, dp(6f))
        }
        wrapper.addView(TextView(this).apply {
            text = label
            setTextColor(0xFFCCCCCC.toInt())
            textSize = 13f
        })
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, dp(6f), 0, 0)
        }
        val scroll = HorizontalScrollView(this)
        presets.forEach { color ->
            val v = View(this)
            val size = dp(36f)
            val lp = LinearLayout.LayoutParams(size, size).apply { marginEnd = dp(8f) }
            val bg = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(color)
                setStroke(dp(3f), if (color == current) Color.WHITE else 0xFF444444.toInt())
            }
            v.background = bg
            v.layoutParams = lp
            v.setOnClickListener {
                onPick(color)
                // re-render to update stroke
                row.removeAllViews()
                presets.forEach { c2 -> row.addView(makeCircle(c2, c2 == color, onPick)) }
            }
            row.addView(v)
        }
        scroll.addView(row)
        wrapper.addView(scroll)
        return wrapper
    }

    private fun makeCircle(color: Int, selected: Boolean, onPick: (Int) -> Unit): View {
        val v = View(this)
        val size = dp(36f)
        v.layoutParams = LinearLayout.LayoutParams(size, size).apply { marginEnd = dp(8f) }
        val bg = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
            setStroke(dp(3f), if (selected) Color.WHITE else 0xFF444444.toInt())
        }
        v.background = bg
        v.setOnClickListener { onPick(color) }
        return v
    }

    private fun slider(label: String, min: Float, max: Float, current: Float,
                       onChange: (Float) -> Unit): View {
        val wrapper = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(6f), 0, dp(6f))
        }
        val tv = TextView(this).apply {
            text = "$label: ${"%.1f".format(current)}"
            setTextColor(0xFFCCCCCC.toInt())
            textSize = 13f
        }
        val sb = SeekBar(this).apply {
            this.max = ((max - min) * 10).toInt()
            progress = ((current - min) * 10).toInt()
        }
        sb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar?, p: Int, fromUser: Boolean) {
                val value = min + p / 10f
                tv.text = "$label: ${"%.1f".format(value)}"
                onChange(value)
            }
            override fun onStartTrackingTouch(s: SeekBar?) {}
            override fun onStopTrackingTouch(s: SeekBar?) {}
        })
        wrapper.addView(tv)
        wrapper.addView(sb)
        return wrapper
    }

    private fun toggle(label: String, current: Boolean, onChange: (Boolean) -> Unit): View {
        return Switch(this).apply {
            text = label
            setTextColor(0xFFCCCCCC.toInt())
            isChecked = current
            setPadding(0, dp(8f), 0, dp(8f))
            setOnCheckedChangeListener { _, checked -> onChange(checked) }
        }
    }
}
