package com.example.kbstudio

data class KeyDef(
    val label: String,
    val output: String,
    val code: Int = CODE_CHAR,
    val widthWeight: Float = 1f
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
    }
}
