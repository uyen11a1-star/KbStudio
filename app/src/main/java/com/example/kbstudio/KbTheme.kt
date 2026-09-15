package com.example.kbstudio

data class KbTheme(
    val bgColor: Int,
    val bgColorEnd: Int?,
    val bgImageUri: String?,
    val keyColor: Int,
    val keyColorEnd: Int?,
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
    val telexEnabled: Boolean,
    val animEnabled: Boolean,
    val popupEnabled: Boolean,
    val showNumberRow: Boolean,
    val showToolbar: Boolean
) {
    companion object {
        fun default() = KbTheme(
            bgColor = 0xFF15171C.toInt(),
            bgColorEnd = null,
            bgImageUri = null,
            keyColor = 0xFF2F333D.toInt(),
            keyColorEnd = null,
            keyPressedColor = 0xFFFF9800.toInt(),
            keyTextColor = 0xFFFFFFFF.toInt(),
            keyBorderColor = 0x00000000,
            keyBorderWidthDp = 0f,
            keyCornerRadiusDp = 8f,
            fontSizeSp = 19f,
            keyboardHeightDp = 240,
            keyGapDp = 2f,
            rowGapDp = 4f,
            soundEnabled = true,
            vibrateEnabled = true,
            telexEnabled = true,
            animEnabled = true,
            popupEnabled = true,
            showNumberRow = true,      // Luon hien nhu Laban
            showToolbar = true          // Toolbar tren cung
        )
    }
}
