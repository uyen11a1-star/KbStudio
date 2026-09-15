package com.example.kbstudio

object KeyLayouts {

    private fun c(s: String, w: Float = 1f) = KeyDef(s, s, KeyDef.CODE_CHAR, w)
    private fun shift() = KeyDef("⇧", "", KeyDef.CODE_SHIFT, 1.5f)
    private fun back() = KeyDef("⌫", "", KeyDef.CODE_BACKSPACE, 1.5f)
    private fun enter() = KeyDef("⏎", "\n", KeyDef.CODE_ENTER, 1.5f)
    private fun space() = KeyDef(" ", " ", KeyDef.CODE_SPACE, 5f)
    private fun toSym() = KeyDef("?123", "", KeyDef.CODE_SYMBOLS, 1.5f)
    private fun toAbc() = KeyDef("ABC", "", KeyDef.CODE_ABC, 1.5f)
    private fun lang() = KeyDef("🌐", "", KeyDef.CODE_LANG, 1f)

    val LETTERS: List<List<KeyDef>> = listOf(
        listOf("q","w","e","r","t","y","u","i","o","p").map { c(it) },
        listOf("a","s","d","f","g","h","j","k","l").map { c(it) },
        listOf(shift()) + listOf("z","x","c","v","b","n","m").map { c(it) } + listOf(back()),
        listOf(toSym(), lang(), c(",", 1.3f), space(), c(".", 1.3f), enter())
    )

    val SYMBOLS: List<List<KeyDef>> = listOf(
        listOf("1","2","3","4","5","6","7","8","9","0").map { c(it) },
        listOf("@","#","$","%","&","-","+","(",")","/").map { c(it) },
        listOf(c("*", 1.5f)) + listOf("\"","'",":",";","!","?").map { c(it) } + listOf(back()),
        listOf(toAbc(), lang(), c(",", 1.3f), space(), c(".", 1.3f), enter())
    )
}
