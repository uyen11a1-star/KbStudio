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
    val showNumberRow: Boolean
) {
    companion object {
        // Mac dinh GON, giong Laban Key
        fun default() = KbTheme(
            bgColor = 0xFF1E1E1E.toInt(),
            bgColorEnd = null,
            bgImageUri = null,
            keyColor = 0xFF3A3A3A.toInt(),
            keyColorEnd = null,
            keyPressedColor = 0xFFFF9800.toInt(),
            keyTextColor = 0xFFFFFFFF.toInt(),
            keyBorderColor = 0xFF444444.toInt(),
            keyBorderWidthDp = 0.5f,
            keyCornerRadiusDp = 6f,
            fontSizeSp = 18f,
            keyboardHeightDp = 230,
            keyGapDp = 2f,
            rowGapDp = 4f,
            soundEnabled = true,
            vibrateEnabled = true,
            telexEnabled = true,
            animEnabled = true,
            popupEnabled = true,
            showNumberRow = false
        )
    }
}
