package com.example.kbstudio

data class KeyDef(
    val label: String,
    val output: String,
    val code: Int = CODE_CHAR,
    val widthWeight: Float = 1f,
    val longPress: List<KeyDef> = emptyList(),
    val longPressDirect: KeyDef? = null
) {
    companion object {
        const val CODE_CHAR = 0
        const val CODE_SHIFT = 1
        const val CODE_BACKSPACE = 2
        const val CODE_ENTER = 3
        const val CODE_SPACE = 4
        const val CODE_SYMBOLS = 5
        const val CODE_ABC = 6
        const val CODE_LANG = 7
        const val CODE_HIDE = 8
        const val CODE_TAB = 9
        const val CODE_LEFT = 10
        const val CODE_RIGHT = 11
        const val CODE_UP = 12
        const val CODE_DOWN = 13
        const val CODE_EMOJI = 14
        const val CODE_VOICE = 15
        const val CODE_SEARCH = 16
        const val CODE_SETTINGS = 17
    }
}
