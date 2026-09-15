package com.example.kbstudio

import android.content.Context

class ThemeManager(context: Context) {
    private val p = context.getSharedPreferences("kb_theme", Context.MODE_PRIVATE)
    private val d = KbTheme.default()

    var bgColor: Int
        get() = p.getInt("bgColor", d.bgColor); set(v) = p.edit().putInt("bgColor", v).apply()
    var bgColorEnd: Int?
        get() = if (p.contains("bgColorEnd")) p.getInt("bgColorEnd", 0) else null
        set(v) = p.edit().apply { if (v == null) remove("bgColorEnd") else putInt("bgColorEnd", v) }.apply()
    var bgImageUri: String?
        get() = p.getString("bgImageUri", null); set(v) = p.edit().putString("bgImageUri", v).apply()
    var keyColor: Int
        get() = p.getInt("keyColor", d.keyColor); set(v) = p.edit().putInt("keyColor", v).apply()
    var keyColorEnd: Int?
        get() = if (p.contains("keyColorEnd")) p.getInt("keyColorEnd", 0) else null
        set(v) = p.edit().apply { if (v == null) remove("keyColorEnd") else putInt("keyColorEnd", v) }.apply()
    var keyPressedColor: Int
        get() = p.getInt("keyPressedColor", d.keyPressedColor); set(v) = p.edit().putInt("keyPressedColor", v).apply()
    var keyTextColor: Int
        get() = p.getInt("keyTextColor", d.keyTextColor); set(v) = p.edit().putInt("keyTextColor", v).apply()
    var keyBorderColor: Int
        get() = p.getInt("keyBorderColor", d.keyBorderColor); set(v) = p.edit().putInt("keyBorderColor", v).apply()
    var keyBorderWidthDp: Float
        get() = p.getFloat("keyBorderWidthDp", d.keyBorderWidthDp); set(v) = p.edit().putFloat("keyBorderWidthDp", v).apply()
    var keyCornerRadiusDp: Float
        get() = p.getFloat("keyCornerRadiusDp", d.keyCornerRadiusDp); set(v) = p.edit().putFloat("keyCornerRadiusDp", v).apply()
    var fontSizeSp: Float
        get() = p.getFloat("fontSizeSp", d.fontSizeSp); set(v) = p.edit().putFloat("fontSizeSp", v).apply()
    var keyboardHeightDp: Int
        get() = p.getInt("keyboardHeightDp", d.keyboardHeightDp); set(v) = p.edit().putInt("keyboardHeightDp", v).apply()
    var keyGapDp: Float
        get() = p.getFloat("keyGapDp", d.keyGapDp); set(v) = p.edit().putFloat("keyGapDp", v).apply()
    var rowGapDp: Float
        get() = p.getFloat("rowGapDp", d.rowGapDp); set(v) = p.edit().putFloat("rowGapDp", v).apply()
    var soundEnabled: Boolean
        get() = p.getBoolean("soundEnabled", d.soundEnabled); set(v) = p.edit().putBoolean("soundEnabled", v).apply()
    var vibrateEnabled: Boolean
        get() = p.getBoolean("vibrateEnabled", d.vibrateEnabled); set(v) = p.edit().putBoolean("vibrateEnabled", v).apply()
    var telexEnabled: Boolean
        get() = p.getBoolean("telexEnabled", d.telexEnabled); set(v) = p.edit().putBoolean("telexEnabled", v).apply()
    var animEnabled: Boolean
        get() = p.getBoolean("animEnabled", d.animEnabled); set(v) = p.edit().putBoolean("animEnabled", v).apply()
    var popupEnabled: Boolean
        get() = p.getBoolean("popupEnabled", d.popupEnabled); set(v) = p.edit().putBoolean("popupEnabled", v).apply()
    var showNumberRow: Boolean
        get() = p.getBoolean("showNumberRow", d.showNumberRow); set(v) = p.edit().putBoolean("showNumberRow", v).apply()
    var showToolbar: Boolean
        get() = p.getBoolean("showToolbar", d.showToolbar); set(v) = p.edit().putBoolean("showToolbar", v).apply()

    fun getKeyImage(label: String): String? = p.getString("img_$label", null)
    fun setKeyImage(label: String, uri: String?) {
        p.edit().apply { if (uri == null) remove("img_$label") else putString("img_$label", uri) }.apply()
    }
    fun clearAllKeyImages() {
        val ed = p.edit()
        p.all.keys.filter { it.startsWith("img_") }.forEach { ed.remove(it) }
        ed.apply()
    }

    fun load(): KbTheme = KbTheme(
        bgColor = bgColor, bgColorEnd = bgColorEnd, bgImageUri = bgImageUri,
        keyColor = keyColor, keyColorEnd = keyColorEnd, keyPressedColor = keyPressedColor,
        keyTextColor = keyTextColor, keyBorderColor = keyBorderColor,
        keyBorderWidthDp = keyBorderWidthDp, keyCornerRadiusDp = keyCornerRadiusDp,
        fontSizeSp = fontSizeSp, keyboardHeightDp = keyboardHeightDp,
        keyGapDp = keyGapDp, rowGapDp = rowGapDp,
        soundEnabled = soundEnabled, vibrateEnabled = vibrateEnabled,
        telexEnabled = telexEnabled, animEnabled = animEnabled,
        popupEnabled = popupEnabled, showNumberRow = showNumberRow,
        showToolbar = showToolbar
    )

    fun applyPreset(t: KbTheme) {
        bgColor = t.bgColor; bgColorEnd = t.bgColorEnd
        keyColor = t.keyColor; keyColorEnd = t.keyColorEnd
        keyPressedColor = t.keyPressedColor; keyTextColor = t.keyTextColor
        keyBorderColor = t.keyBorderColor
        keyBorderWidthDp = t.keyBorderWidthDp; keyCornerRadiusDp = t.keyCornerRadiusDp
        fontSizeSp = t.fontSizeSp; keyboardHeightDp = t.keyboardHeightDp
        keyGapDp = t.keyGapDp; rowGapDp = t.rowGapDp
        soundEnabled = t.soundEnabled; vibrateEnabled = t.vibrateEnabled
        telexEnabled = t.telexEnabled; animEnabled = t.animEnabled
        popupEnabled = t.popupEnabled; showNumberRow = t.showNumberRow
        showToolbar = t.showToolbar
    }

    fun resetAll() = p.edit().clear().apply()
}
