package com.example.kbstudio

object PresetThemes {

    data class Preset(val name: String, val theme: KbTheme)

    val ALL: List<Preset> = listOf(
        Preset("Mặc định", KbTheme.default()),

        Preset("Laban sáng", KbTheme.default().copy(
            bgColor = 0xFFDFF5C6.toInt(),
            keyColor = 0xFFFFFFFF.toInt(),
            keyColorEnd = 0xFFF1F8E9.toInt(),
            keyPressedColor = 0xFFB8E986.toInt(),
            keyTextColor = 0xFF1B1B1B.toInt(),
            keyBorderColor = 0x00000000
        )),

        Preset("Dark", KbTheme.default().copy(
            bgColor = 0xFF000000.toInt(),
            keyColor = 0xFF1F1F1F.toInt(),
            keyColorEnd = 0xFF0F0F0F.toInt(),
            keyPressedColor = 0xFF3D3D3D.toInt(),
            keyTextColor = 0xFFFFFFFF.toInt()
        )),

        Preset("Neon", KbTheme.default().copy(
            bgColor = 0xFF0A0A0A.toInt(),
            keyColor = 0xFF1A0033.toInt(), keyColorEnd = 0xFF330066.toInt(),
            keyPressedColor = 0xFF00FFFF.toInt(), keyTextColor = 0xFF00FFCC.toInt(),
            keyBorderColor = 0xFFFF00FF.toInt(), keyBorderWidthDp = 1f,
            keyCornerRadiusDp = 12f
        )),

        Preset("Pastel", KbTheme.default().copy(
            bgColor = 0xFFFDF6E3.toInt(),
            keyColor = 0xFFFFE0B2.toInt(), keyColorEnd = 0xFFFFCC80.toInt(),
            keyPressedColor = 0xFFFFAB91.toInt(), keyTextColor = 0xFF3E2723.toInt(),
            keyCornerRadiusDp = 14f
        )),

        Preset("Retro", KbTheme.default().copy(
            bgColor = 0xFF2B1810.toInt(),
            keyColor = 0xFF6D4C41.toInt(), keyColorEnd = 0xFF4E342E.toInt(),
            keyPressedColor = 0xFFFFB300.toInt(), keyTextColor = 0xFFFFF8E1.toInt(),
            keyCornerRadiusDp = 5f, keyBorderWidthDp = 2f,
            keyBorderColor = 0xFF3E2723.toInt()
        )),

        Preset("Ocean", KbTheme.default().copy(
            bgColor = 0xFF001F3F.toInt(),
            keyColor = 0xFF0074D9.toInt(), keyColorEnd = 0xFF00509E.toInt(),
            keyPressedColor = 0xFF7FDBFF.toInt(), keyTextColor = 0xFFFFFFFF.toInt()
        )),

        Preset("Sunset", KbTheme.default().copy(
            bgColor = 0xFF2D1B3D.toInt(),
            keyColor = 0xFFFF6B6B.toInt(), keyColorEnd = 0xFFFFA500.toInt(),
            keyPressedColor = 0xFFFFD93D.toInt(), keyTextColor = 0xFFFFFFFF.toInt(),
            keyCornerRadiusDp = 16f
        )),

        Preset("Mono", KbTheme.default().copy(
            bgColor = 0xFFECEFF1.toInt(),
            keyColor = 0xFFFFFFFF.toInt(), keyColorEnd = 0xFFF5F5F5.toInt(),
            keyPressedColor = 0xFFB0BEC5.toInt(), keyTextColor = 0xFF000000.toInt(),
            keyBorderColor = 0xFFCFD8DC.toInt(), keyBorderWidthDp = 1f
        )),

        Preset("Cơ khí", KbTheme.default().copy(
            bgColor = 0xFF212121.toInt(),
            keyColor = 0xFF424242.toInt(), keyColorEnd = 0xFF2E2E2E.toInt(),
            keyPressedColor = 0xFF757575.toInt(), keyTextColor = 0xFFFFEB3B.toInt(),
            keyBorderColor = 0xFF616161.toInt(), keyBorderWidthDp = 2f,
            keyCornerRadiusDp = 4f
        ))
    )
}
