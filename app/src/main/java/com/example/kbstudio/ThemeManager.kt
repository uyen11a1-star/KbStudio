package com.example.kbstudio

import android.content.Context

class ThemeManager(context: Context) {
    private val p = context.getSharedPreferences("kb_theme", Context.MODE_PRIVATE)
    private val d = KbTheme.default()

    var bgColor: Int
        get() = p.getInt("bgColor", d.bgColor)
        set(v) = p.edit().putInt("bgColor", v).apply()

    var bgImageUri: String?
        get() = p.getString("bgImageUri", null)
        set(v) = p.edit().putString("bgImageUri", v).apply()

    var keyColor: Int
        get() = p.getInt("keyColor", d.keyColor)
        set(v) = p.edit().putInt("keyColor", v).apply()

    var keyPressedColor: Int
        get() = p.getInt("keyPressedColor", d.keyPressedColor)
        set(v) = p.edit().putInt("keyPressedColor", v).apply()

    var keyTextColor: Int
        get() = p.getInt("keyTextColor", d.keyTextColor)
        set(v) = p.edit().putInt("keyTextColor", v).apply()

    var keyBorderColor: Int
        get() = p.getInt("keyBorderColor", d.keyBorderColor)
        set(v) = p.edit().putInt("keyBorderColor", v).apply()

    var keyBorderWidthDp: Float
        get() = p.getFloat("keyBorderWidthDp", d.keyBorderWidthDp)
        set(v) = p.edit().putFloat("keyBorderWidthDp", v).apply()

    var keyCornerRadiusDp: Float
        get() = p.getFloat("keyCornerRadiusDp", d.keyCornerRadiusDp)
        set(v) = p.edit().putFloat("keyCornerRadiusDp", v).apply()

    var fontSizeSp: Float
        get() = p.getFloat("fontSizeSp", d.fontSizeSp)
        set(v) = p.edit().putFloat("fontSizeSp", v).apply()

    var keyboardHeightDp: Int
        get() = p.getInt("keyboardHeightDp", d.keyboardHeightDp)
        set(v) = p.edit().putInt("keyboardHeightDp", v).apply()

    var keyGapDp: Float
        get() = p.getFloat("keyGapDp", d.keyGapDp)
        set(v) = p.edit().putFloat("keyGapDp", v).apply()

    var rowGapDp: Float
        get() = p.getFloat("rowGapDp", d.rowGapDp)
        set(v) = p.edit().putFloat("rowGapDp", v).apply()

    var soundEnabled: Boolean
        get() = p.getBoolean("soundEnabled", d.soundEnabled)
        set(v) = p.edit().putBoolean("soundEnabled", v).apply()

    var vibrateEnabled: Boolean
        get() = p.getBoolean("vibrateEnabled", d.vibrateEnabled)
        set(v) = p.edit().putBoolean("vibrateEnabled", v).apply()

    var telexEnabled: Boolean
        get() = p.getBoolean("telexEnabled", d.telexEnabled)
        set(v) = p.edit().putBoolean("telexEnabled", v).apply()

    fun load(): KbTheme = KbTheme(
        bgColor = bgColor,
        bgImageUri = bgImageUri,
        keyColor = keyColor,
        keyPressedColor = keyPressedColor,
        keyTextColor = keyTextColor,
        keyBorderColor = keyBorderColor,
        keyBorderWidthDp = keyBorderWidthDp,
        keyCornerRadiusDp = keyCornerRadiusDp,
        fontSizeSp = fontSizeSp,
        keyboardHeightDp = keyboardHeightDp,
        keyGapDp = keyGapDp,
        rowGapDp = rowGapDp,
        soundEnabled = soundEnabled,
        vibrateEnabled = vibrateEnabled,
        telexEnabled = telexEnabled
    )

    fun resetAll() {
        p.edit().clear().apply()
    }
}
