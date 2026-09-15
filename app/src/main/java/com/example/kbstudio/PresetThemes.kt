package com.example.kbstudio

object PresetThemes {

    data class Preset(val name: String, val theme: KbTheme)

    val ALL: List<Preset> = listOf(
        Preset("Mặc định", KbTheme.default()),
        Preset("Dark", KbTheme.default().copy(
            bgColor = 0xFF000000.toInt(), keyColor = 0xFF1F1F1F.toInt(),
            keyPressedColor = 0xFF3D3D3D.toInt(), keyTextColor = 0xFFFFFFFF.toInt(),
            keyBorderColor = 0xFF333333.toInt()
        )),
        Preset("Neon", KbTheme.default().copy(
            bgColor = 0xFF0A0A0A.toInt(),
            keyColor = 0xFF1A0033.toInt(), keyColorEnd = 0xFF330066.toInt(),
            keyPressedColor = 0xFF00FFFF.toInt(), keyTextColor = 0xFF00FFCC.toInt(),
            keyBorderColor = 0xFFFF00FF.toInt(), keyBorderWidthDp = 1.5f,
            keyCornerRadiusDp = 12f
        )),
        Preset("Pastel", KbTheme.default().copy(
            bgColor = 0xFFFDF6E3.toInt(), keyColor = 0xFFFFE0B2.toInt(),
            keyPressedColor = 0xFFFFAB91.toInt(), keyTextColor = 0xFF3E2723.toInt(),
            keyBorderColor = 0xFFBCAAA4.toInt(), keyCornerRadiusDp = 14f
        )),
        Preset("Retro", KbTheme.default().copy(
            bgColor = 0xFF2B1810.toInt(), keyColor = 0xFF6D4C41.toInt(),
            keyColorEnd = 0xFF4E342E.toInt(),
            keyPressedColor = 0xFFFFB300.toInt(), keyTextColor = 0xFFFFF8E1.toInt(),
            keyBorderColor = 0xFF3E2723.toInt(), keyCornerRadiusDp = 4f,
            keyBorderWidthDp = 2f
        )),
        Preset("Ocean", KbTheme.default().copy(
            bgColor = 0xFF001F3F.toInt(), keyColor = 0xFF0074D9.toInt(),
            keyColorEnd = 0xFF00509E.toInt(),
            keyPressedColor = 0xFF7FDBFF.toInt(), keyTextColor = 0xFFFFFFFF.toInt(),
            keyBorderColor = 0xFF003366.toInt()
        )),
        Preset("Sunset", KbTheme.default().copy(
            bgColor = 0xFF2D1B3D.toInt(),
            keyColor = 0xFFFF6B6B.toInt(), keyColorEnd = 0xFFFFA500.toInt(),
            keyPressedColor = 0xFFFFD93D.toInt(), keyTextColor = 0xFFFFFFFF.toInt(),
            keyBorderColor = 0xFF2D1B3D.toInt(), keyCornerRadiusDp = 20f
        )),
        Preset("Mono", KbTheme.default().copy(
            bgColor = 0xFFECEFF1.toInt(), keyColor = 0xFFFFFFFF.toInt(),
            keyPressedColor = 0xFFB0BEC5.toInt(), keyTextColor = 0xFF000000.toInt(),
            keyBorderColor = 0xFF90A4AE.toInt(), keyBorderWidthDp = 2f
        ))
    )
}
