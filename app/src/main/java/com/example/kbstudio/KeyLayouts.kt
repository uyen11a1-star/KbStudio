package com.example.kbstudio

object KeyLayouts {

    private fun c(s: String, w: Float = 1f, lp: List<String> = emptyList()) =
        KeyDef(s, s, KeyDef.CODE_CHAR, w, lp.map { KeyDef(it, it) })

    private fun k(label: String, code: Int, w: Float = 1f) =
        KeyDef(label, "", code, w)

    private fun shift() = k("⇧", KeyDef.CODE_SHIFT, 1.5f)
    private fun back() = k("⌫", KeyDef.CODE_BACKSPACE, 1.5f)
    private fun enter() = k("⏎", KeyDef.CODE_ENTER, 1.5f)
    private fun space() = KeyDef(" ", " ", KeyDef.CODE_SPACE, 5f)
    private fun toSym() = k("?123", KeyDef.CODE_SYMBOLS, 1.5f)
    private fun toAbc() = k("ABC", KeyDef.CODE_ABC, 1.5f)
    private fun lang() = k("🌐", KeyDef.CODE_LANG, 1f)
    private fun hide() = k("⌄", KeyDef.CODE_HIDE, 1f)
    private fun emoji() = k("😊", KeyDef.CODE_EMOJI, 1f)
    private fun tab() = k("⇥", KeyDef.CODE_TAB, 1.2f)
    private fun left() = k("◀", KeyDef.CODE_LEFT, 1f)
    private fun right() = k("▶", KeyDef.CODE_RIGHT, 1f)

    // Long-press vowels
    private val aLp = listOf("á","à","ả","ã","ạ","ă","â","ấ","ầ","ẩ","ẫ","ậ","ắ","ằ","ẳ","ẵ","ặ")
    private val eLp = listOf("é","è","ẻ","ẽ","ẹ","ê","ế","ề","ể","ễ","ệ")
    private val iLp = listOf("í","ì","ỉ","ĩ","ị")
    private val oLp = listOf("ó","ò","ỏ","õ","ọ","ô","ố","ồ","ổ","ỗ","ộ","ơ","ớ","ờ","ở","ỡ","ợ")
    private val uLp = listOf("ú","ù","ủ","ũ","ụ","ư","ứ","ừ","ử","ữ","ự")
    private val yLp = listOf("ý","ỳ","ỷ","ỹ","ỵ")
    private val dLp = listOf("đ")

    val NUMBER_ROW: List<KeyDef> = listOf(
        c("1"), c("2"), c("3"), c("4"), c("5"),
        c("6"), c("7"), c("8"), c("9"), c("0")
    )

    val LETTERS: List<List<KeyDef>> = listOf(
        listOf("q","w","e","r","t","y","u","i","o","p").map {
            when (it) { "e" -> c(it, lp = eLp); "i" -> c(it, lp = iLp)
                "o" -> c(it, lp = oLp); "u" -> c(it, lp = uLp)
                "y" -> c(it, lp = yLp); else -> c(it) }
        },
        listOf("a","s","d","f","g","h","j","k","l").map {
            when (it) { "a" -> c(it, lp = aLp); "d" -> c(it, lp = dLp)
                else -> c(it) }
        },
        listOf(shift()) + listOf("z","x","c","v","b","n","m").map { c(it) } + listOf(back()),
        listOf(toSym(), emoji(), lang(), c(",", 1.3f), space(), c(".", 1.3f), enter(), hide())
    )

    val SYMBOLS: List<List<KeyDef>> = listOf(
        listOf("1","2","3","4","5","6","7","8","9","0").map { c(it) },
        listOf("@","#","$","%","&","-","+","(",")","/").map { c(it) },
        listOf(c("*", 1.5f)) + listOf("\"","'",":",";","!","?").map { c(it) } + listOf(back()),
        listOf(toAbc(), emoji(), lang(), c(",", 1.3f), space(), c(".", 1.3f), enter(), hide())
    )

    // Layout với mũi tên + tab (dùng khi mở rộng)
    val SYMBOLS_EXTRA: List<List<KeyDef>> = listOf(
        listOf("1","2","3","4","5","6","7","8","9","0").map { c(it) },
        listOf("<",">","[","]","{","}","\\","|","~","`").map { c(it) },
        listOf(tab(), c("*", 1.5f)) + listOf("=","_","\"","'",":",";").map { c(it) } + listOf(back()),
        listOf(left(), right()) + listOf(toAbc(), emoji(), lang(), space(), enter(), hide())
    )
}
