package com.example.kbstudio

object KeyLayouts {

    private fun c(s: String, w: Float = 1f, lp: List<String> = emptyList()) =
        KeyDef(s, s, KeyDef.CODE_CHAR, w, lp.map { KeyDef(it, it) })

    private fun k(label: String, code: Int, w: Float = 1f) =
        KeyDef(label, "", code, w)

    private fun shift() = k("⇧", KeyDef.CODE_SHIFT, 1.5f)
    private fun back() = k("⌫", KeyDef.CODE_BACKSPACE, 1.5f)
    private fun enter() = k("⏎", KeyDef.CODE_ENTER, 1.5f)

    // space long-press -> mic
    private fun space() = KeyDef(" ", " ", KeyDef.CODE_SPACE, 4f,
        longPressDirect = KeyDef("🎤", "", KeyDef.CODE_VOICE))

    // ?123 long-press -> doi ban phim
    private fun toSym() = KeyDef("?123", "", KeyDef.CODE_SYMBOLS, 1.5f,
        longPressDirect = KeyDef("🌐", "", KeyDef.CODE_LANG))
    private fun toAbc() = KeyDef("ABC", "", KeyDef.CODE_ABC, 1.5f,
        longPressDirect = KeyDef("🌐", "", KeyDef.CODE_LANG))

    private fun emoji() = k("😊", KeyDef.CODE_EMOJI, 1f)

    // Toolbar tren cung - kieu Laban
    val TOOLBAR: List<KeyDef> = listOf(
        k("🎤", KeyDef.CODE_VOICE, 1f),
        k("😊", KeyDef.CODE_EMOJI, 1f),
        k("⚙️", KeyDef.CODE_SETTINGS, 1f),
        k("🔍", KeyDef.CODE_SEARCH, 1f),
        k("⌄", KeyDef.CODE_HIDE, 1f)
    )

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

    // Tat ca hang chinh deu = 10 units
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
        // Hang cuoi kieu Laban: ?123 , SPACE . ? 🔍
        listOf(toSym(), c(",", 1f), space(), c(".", 1f), c("?", 1f), k("🔍", KeyDef.CODE_SEARCH, 1.5f))
    )

    val SYMBOLS: List<List<KeyDef>> = listOf(
        listOf("1","2","3","4","5","6","7","8","9","0").map { c(it) },
        listOf("@","#","$","%","&","-","+","(",")","/").map { c(it) },
        listOf(c("*", 1.5f)) + listOf("\"","'",":",";","!","?").map { c(it) } + listOf(back()),
        listOf(toAbc(), c(",", 1f), space(), c(".", 1f), c("?", 1f), k("🔍", KeyDef.CODE_SEARCH, 1.5f))
    )
}
