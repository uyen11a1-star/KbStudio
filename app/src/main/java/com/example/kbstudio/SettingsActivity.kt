package com.example.kbstudio

import android.app.AlertDialog
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

    private var pendingImageKey: String? = null

    private val pickBgImage = registerForActivityResult(
        ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            tryPersist(uri)
            tm.bgImageUri = uri.toString()
            refresh()
        }
    }

    private val pickKeyImage = registerForActivityResult(
        ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null && pendingImageKey != null) {
            tryPersist(uri)
            tm.setKeyImage(pendingImageKey!!, uri.toString())
            Toast.makeText(this, "Đã gán ảnh cho phím '${pendingImageKey}'", Toast.LENGTH_SHORT).show()
            refresh()
        }
        pendingImageKey = null
    }

    private fun tryPersist(uri: Uri) {
        try {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (_: Exception) {}
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
        root.addView(TextView(this).apply {
            text = "💡 Nhấn giữ 1 phím trong khung xem trước để gán ảnh cho phím đó"
            setTextColor(0xFF888888.toInt()); textSize = 12f
            setPadding(0, 0, 0, dp(10f))
        })

        preview = KeyboardView(this).apply {
            previewMode = true; isFocusable = false
            setOnLongClickListener {
                showKeyPickerDialog()
                true
            }
        }
        // Cho phép long press trên preview -> chọn phím gán ảnh
        preview.setOnTouchListener { v, e ->
            if (e.action == android.view.MotionEvent.ACTION_DOWN) v.performLongClick()
            false
        }
        root.addView(preview, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(tm.keyboardHeightDp.toFloat())
        ).apply { bottomMargin = dp(16f) })
        preview.setTheme(tm.load())
        preview.resetToLetters()

        // Presets
        root.addView(section("🎨 Theme có sẵn"))
        val presetRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 0, 0, dp(8f))
        }
        val presetScroll = HorizontalScrollView(this)
        PresetThemes.ALL.forEach { preset ->
            val b = Button(this).apply {
                text = preset.name
                textSize = 12f
                setOnClickListener {
                    tm.applyPreset(preset.theme)
                    refresh()
                    Toast.makeText(this@SettingsActivity, "Đã áp dụng: ${preset.name}", Toast.LENGTH_SHORT).show()
                }
            }
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT).apply { marginEnd = dp(6f) }
            b.layoutParams = lp
            presetRow.addView(b)
        }
        presetScroll.addView(presetRow)
        root.addView(presetScroll)

        // Colors
        root.addView(section("🎨 Màu sắc"))
        root.addView(colorPicker("Màu nền", tm.bgColor) { tm.bgColor = it; refresh() })
        root.addView(colorPicker("Màu nền (gradient - tùy chọn)", tm.bgColorEnd) {
            tm.bgColorEnd = it; refresh()
        })
        root.addView(rowButton("Bỏ gradient nền") { tm.bgColorEnd = null; refresh() })
        root.addView(colorPicker("Màu phím", tm.keyColor) { tm.keyColor = it; refresh() })
        root.addView(colorPicker("Màu phím (gradient - tùy chọn)", tm.keyColorEnd) {
            tm.keyColorEnd = it; refresh()
        })
        root.addView(rowButton("Bỏ gradient phím") { tm.keyColorEnd = null; refresh() })
        root.addView(colorPicker("Màu phím nhấn", tm.keyPressedColor) { tm.keyPressedColor = it; refresh() })
        root.addView(colorPicker("Màu chữ", tm.keyTextColor) { tm.keyTextColor = it; refresh() })
        root.addView(colorPicker("Màu viền ô", tm.keyBorderColor) { tm.keyBorderColor = it; refresh() })

        // Size
        root.addView(section("📏 Kích thước"))
        root.addView(slider("Cỡ chữ (sp)", 10f, 32f, tm.fontSizeSp) { tm.fontSizeSp = it; refresh() })
        root.addView(slider("Chiều cao bàn phím (dp)", 180f, 420f, tm.keyboardHeightDp.toFloat()) {
            tm.keyboardHeightDp = it.toInt(); refresh()
        })
        root.addView(slider("Bo góc phím (dp)", 0f, 30f, tm.keyCornerRadiusDp) { tm.keyCornerRadiusDp = it; refresh() })
        root.addView(slider("Độ dày viền (dp)", 0f, 6f, tm.keyBorderWidthDp) { tm.keyBorderWidthDp = it; refresh() })
        root.addView(slider("Khoảng cách phím (dp)", 0f, 12f, tm.keyGapDp) { tm.keyGapDp = it; refresh() })
        root.addView(slider("Khoảng cách hàng (dp)", 0f, 20f, tm.rowGapDp) { tm.rowGapDp = it; refresh() })

        // Layout
        root.addView(section("📐 Bố cục"))
        root.addView(toggle("Hiện hàng số trên cùng", tm.showNumberRow) {
            tm.showNumberRow = it; refresh()
        })
        root.addView(toggle("Bật popup khi nhấn giữ", tm.popupEnabled) {
            tm.popupEnabled = it; refresh()
        })

        // Effects
        root.addView(section("🔊 Hiệu ứng"))
        root.addView(toggle("Âm thanh khi bấm", tm.soundEnabled) { tm.soundEnabled = it })
        root.addView(toggle("Rung khi bấm", tm.vibrateEnabled) { tm.vibrateEnabled = it })
        root.addView(toggle("Hiệu ứng phím nhấn (scale)", tm.animEnabled) { tm.animEnabled = it; refresh() })

        // Vietnamese
        root.addView(section("🇻🇳 Tiếng Việt"))
        root.addView(toggle("Bật Telex", tm.telexEnabled) { tm.telexEnabled = it })

        // Background image
        root.addView(section("🖼️ Ảnh nền bàn phím"))
        root.addView(rowButton("Chọn ảnh nền") { pickBgImage.launch(arrayOf("image/*")) })
        root.addView(rowButton("Xóa ảnh nền") { tm.bgImageUri = null; refresh() })

        // Per-key image
        root.addView(section("🖼️ Ảnh trên từng phím"))
        root.addView(TextView(this).apply {
            text = "Chọn phím muốn gán ảnh:"
            setTextColor(0xFFCCCCCC.toInt()); textSize = 13f
            setPadding(0, 0, 0, dp(6f))
        })
        val keyGrid = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        val keyScroll = HorizontalScrollView(this)
        val keysToAssign = listOf("q","w","e","r","t","y","u","i","o","p",
            "a","s","d","f","g","h","j","k","l",
            "z","x","c","v","b","n","m",
            "space","enter","123","emoji","shift","back")
        keysToAssign.forEach { label ->
            val b = Button(this).apply {
                text = label
                textSize = 11f
                setPadding(dp(8f), 0, dp(8f), 0)
                setOnClickListener { showKeyImageDialog(label) }
            }
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT).apply { marginEnd = dp(4f) }
            b.layoutParams = lp
            keyGrid.addView(b)
        }
        keyScroll.addView(keyGrid)
        root.addView(keyScroll)
        root.addView(rowButton("Xóa toàn bộ ảnh trên phím") {
            tm.clearAllKeyImages()
            Toast.makeText(this, "Đã xóa ảnh trên phím", Toast.LENGTH_SHORT).show()
            refresh()
        })

        // Other
        root.addView(section("⚙️ Khác"))
        root.addView(rowButton("Reset về mặc định") {
            tm.resetAll(); refresh()
            Toast.makeText(this, "Đã reset", Toast.LENGTH_SHORT).show()
        })
    }

    private fun showKeyImageDialog(label: String) {
        val options = arrayOf("Chọn ảnh", "Xóa ảnh khỏi phím này")
        AlertDialog.Builder(this)
            .setTitle("Phím '$label'")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> { pendingImageKey = label
                        pickKeyImage.launch(arrayOf("image/*")) }
                    1 -> { tm.setKeyImage(label, null)
                        Toast.makeText(this, "Đã xóa ảnh phím '$label'", Toast.LENGTH_SHORT).show()
                        refresh() }
                }
            }.show()
    }

    private fun showKeyPickerDialog() {
        Toast.makeText(this, "Dùng danh sách 'Ảnh trên từng phím' bên dưới để gán ảnh", Toast.LENGTH_LONG).show()
    }

    private fun refresh() {
        val t = tm.load()
        preview.setTheme(t)
        preview.resetToLetters()
        preview.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(t.keyboardHeightDp.toFloat())
        )
        preview.requestLayout()
    }

    private fun dp(v: Float) = (v * resources.displayMetrics.density).toInt()

    private fun title(t: String) = TextView(this).apply {
        text = t; setTextColor(0xFFFF9800.toInt()); textSize = 24f
        gravity = Gravity.CENTER; setPadding(0, 0, 0, dp(20f))
    }
    private fun section(t: String) = TextView(this).apply {
        text = t; setTextColor(0xFFFFEB3B.toInt()); textSize = 16f
        setPadding(0, dp(20f), 0, dp(8f))
    }
    private fun rowButton(t: String, onClick: () -> Unit) = Button(this).apply {
        text = t; setOnClickListener { onClick() }
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(8f) }
    }

    private val presets = intArrayOf(
        0xFF000000.toInt(), 0xFF1E1E1E.toInt(), 0xFF3A3A3A.toInt(), 0xFF616161.toInt(),
        0xFF9E9E9E.toInt(), 0xFFFFFFFF.toInt(), 0xFFF44336.toInt(), 0xFFE91E63.toInt(),
        0xFF9C27B0.toInt(), 0xFF673AB7.toInt(), 0xFF3F51B5.toInt(), 0xFF2196F3.toInt(),
        0xFF03A9F4.toInt(), 0xFF00BCD4.toInt(), 0xFF009688.toInt(), 0xFF4CAF50.toInt(),
        0xFF8BC34A.toInt(), 0xFFCDDC39.toInt(), 0xFFFFEB3B.toInt(), 0xFFFFC107.toInt(),
        0xFFFF9800.toInt(), 0xFFFF5722.toInt(), 0xFF795548.toInt(), 0xFF607D8B.toInt()
    )

    private fun colorPicker(label: String, current: Int?, onPick: (Int) -> Unit): View {
        val wrapper = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(6f), 0, dp(6f))
        }
        wrapper.addView(TextView(this).apply {
            text = label; setTextColor(0xFFCCCCCC.toInt()); textSize = 13f
        })
        val scroll = HorizontalScrollView(this)
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL; setPadding(0, dp(6f), 0, 0)
        }
        fun rebuild(selected: Int?) {
            row.removeAllViews()
            presets.forEach { c ->
                row.addView(makeCircle(c, c == selected) {
                    onPick(c)
                    rebuild(c)
                })
            }
        }
        rebuild(current)
        scroll.addView(row)
        wrapper.addView(scroll)
        return wrapper
    }

    private fun makeCircle(color: Int, selected: Boolean, onPick: () -> Unit): View {
        val v = View(this)
        val size = dp(36f)
        v.layoutParams = LinearLayout.LayoutParams(size, size).apply { marginEnd = dp(8f) }
        val bg = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
            setStroke(dp(3f), if (selected) Color.WHITE else 0xFF444444.toInt())
        }
        v.background = bg
        v.setOnClickListener { onPick() }
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
            setTextColor(0xFFCCCCCC.toInt()); textSize = 13f
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
        wrapper.addView(tv); wrapper.addView(sb)
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
