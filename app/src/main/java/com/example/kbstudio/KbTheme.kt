package com.example.kbstudio

data class KbTheme(
    val bgColor: Int,
    val bgImageUri: String?,
    val keyColor: Int,
    val keyPressedColor: Int,
    val keyTextColor: Int,
    val keyBorderColor: Int,
    val keyBorderWidthDp: Float,
    val keyCornerRadiusDp: Float,
    val fontSizeSp: Float,
    val keyboardHeightDp: Int,
    val keyGapDp: Float,
    val rowGapDp: Float,
    val soundEnabled: Boolean,
    val vibrateEnabled: Boolean,
    val telexEnabled: Boolean
) {
    companion object {
        fun default() = KbTheme(
            bgColor = 0xFF1E1E1E.toInt(),
            bgImageUri = null,
            keyColor = 0xFF3A3A3A.toInt(),
            keyPressedColor = 0xFFFF9800.toInt(),
            keyTextColor = 0xFFFFFFFF.toInt(),
            keyBorderColor = 0xFF555555.toInt(),
            keyBorderWidthDp = 1f,
            keyCornerRadiusDp = 8f,
            fontSizeSp = 20f,
            keyboardHeightDp = 240,
            keyGapDp = 3f,
            rowGapDp = 6f,
            soundEnabled = true,
            vibrateEnabled = true,
            telexEnabled = true
        )
    }
}
