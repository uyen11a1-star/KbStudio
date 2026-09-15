package com.example.kbstudio

/**
 * Telex co ban:
 *  - aa -> â, aw -> ă, ee -> ê, oo -> ô, ow -> ơ, uw -> ư, dd -> đ
 *  - Them dau thanh: s=sac, f=huyen, r=hoi, x=nga, j=nang
 */
object TelexEngine {

    private val vowelMarks = mapOf(
        'a' to mapOf(
            '\u0000' to "a",
            's' to "á", 'f' to "à", 'r' to "ả", 'x' to "ã", 'j' to "ạ"
        ),
        'ă' to mapOf(
            '\u0000' to "ă",
            's' to "ắ", 'f' to "ằ", 'r' to "ẳ", 'x' to "ẵ", 'j' to "ặ"
        ),
        'â' to mapOf(
            '\u0000' to "â",
            's' to "ấ", 'f' to "ầ", 'r' to "ẩ", 'x' to "ẫ", 'j' to "ậ"
        ),
        'e' to mapOf(
            '\u0000' to "e",
            's' to "é", 'f' to "è", 'r' to "ẻ", 'x' to "ẽ", 'j' to "ẹ"
        ),
        'ê' to mapOf(
            '\u0000' to "ê",
            's' to "ế", 'f' to "ề", 'r' to "ể", 'x' to "ễ", 'j' to "ệ"
        ),
        'i' to mapOf(
            '\u0000' to "i",
            's' to "í", 'f' to "ì", 'r' to "ỉ", 'x' to "ĩ", 'j' to "ị"
        ),
        'o' to mapOf(
            '\u0000' to "o",
            's' to "ó", 'f' to "ò", 'r' to "ỏ", 'x' to "õ", 'j' to "ọ"
        ),
        'ô' to mapOf(
            '\u0000' to "ô",
            's' to "ố", 'f' to "ồ", 'r' to "ổ", 'x' to "ỗ", 'j' to "ộ"
        ),
        'ơ' to mapOf(
            '\u0000' to "ơ",
            's' to "ớ", 'f' to "ờ", 'r' to "ở", 'x' to "ỡ", 'j' to "ợ"
        ),
        'u' to mapOf(
            '\u0000' to "u",
            's' to "ú", 'f' to "ù", 'r' to "ủ", 'x' to "ũ", 'j' to "ụ"
        ),
        'ư' to mapOf(
            '\u0000' to "ư",
            's' to "ứ", 'f' to "ừ", 'r' to "ử", 'x' to "ữ", 'j' to "ự"
        ),
        'y' to mapOf(
            '\u0000' to "y",
            's' to "ý", 'f' to "ỳ", 'r' to "ỷ", 'x' to "ỹ", 'j' to "ỵ"
        )
    )

    private val vowelBase = "aăâeêioôơuưy"
    private val toneKeys = "sfrxj"

    /**
     * Tra ve Pair<soKyTuCanXoa, chuoiThayThe> hoac null neu khong khop.
     */
    fun tryApply(prev: String, newKey: Char): Pair<Int, String>? {
        val k = newKey.lowercaseChar()

        // Double letter -> a a -> â, e e -> ê...
        if (prev.isNotEmpty()) {
            val last = prev.last()
            val combo = when {
                last == 'a' && k == 'a' -> "â"
                last == 'a' && k == 'w' -> "ă"
                last == 'e' && k == 'e' -> "ê"
                last == 'o' && k == 'o' -> "ô"
                last == 'o' && k == 'w' -> "ơ"
                last == 'u' && k == 'w' -> "ư"
                last == 'd' && k == 'd' -> "đ"
                last == 'ă' && k == 'w' -> "ă"
                last == 'â' && k == 'a' -> "â"
                last == 'ê' && k == 'e' -> "ê"
                last == 'ô' && k == 'o' -> "ô"
                last == 'ơ' && k == 'w' -> "ơ"
                last == 'ư' && k == 'w' -> "ư"
                else -> null
            }
            if (combo != null) return Pair(1, combo)
        }

        // Tone mark
        if (k in toneKeys) {
            // Tim nguyen am cuoi cung trong prev
            var idx = prev.length - 1
            while (idx >= 0 && prev[idx].lowercaseChar() !in vowelBase) idx--
            if (idx < 0) return null
            val vowel = prev[idx].lowercaseChar()
            val marked = vowelMarks[vowel]?.get(k) ?: return null
            val result = prev.substring(0, idx) + marked + prev.substring(idx + 1)
            return Pair(0, result)
        }

        return null
    }
}
