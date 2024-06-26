package com.sandorln.game.util


/**
 * `null`문자(`'\0'`)를 나타냅니다.
 */
private val nullChar = '\u0000'

/**
 * 코드 값 [code]가 한글인지 확인합니다.
 *
 * @see isHangul
 *
 * @param code 한글인지 확인하려는 코드 값
 * @return 만일 한글이면 `true`, 그렇지 않으면 `false`를 반환
 */
private fun isHangul(code: Char): Boolean = code.code in 0xAC00..0xD7A3

/**
 * 문자 [char]를 초성, 중성, 종성으로 분리된 문자열을 반환합니다.
 *
 * @param char 초성, 중성, 종성으로 분리하고 하는 문자
 * @param atomic `true`이면 중성과 종성을 더 잘게 분리합니다. 예)`ㄼ`은 `ㄹㅂ`으로, `ㅚ`는 `ㅗㅣ`로
 * @return 초성, 중성, 종성으로 분리된 문자열을 반환합니다.
 */
private fun splitAsString(char: Char, atomic: Boolean = false): String {
    val (i, m, f) = split(char) ?: return char.toString()
    val initial = i
    val medial = if (atomic) splitTable[m] ?: m else m
    val final = if (atomic) splitTable[f] ?: f else f
    return "$initial$medial$final".filter { it != nullChar }
}

/**
 * 문자 [char]를 초성, 중성, 종성으로 분리합니다.
 *
 * @see splitAsString
 *
 * @param char 초성, 중성, 종성으로 분리하고 하는 문자
 * @return 초성, 중성, 종성으로 분리된 결과
 */
private fun split(char: Char): Triple<Char, Char, Char>? {
    if (!isHangul(char)) {
        return null
    }

    return (char.code - 0xAC00).let {
        var v = it
        val f = v % 28
        v /= 28
        val m = v % 21
        v /= 21
        val i = v % 19
        Triple(initialList[i], medialList[m], finalList[f])
    }
}

fun Char.getInitialHangul() = runCatching { splitAsString(this).first().toString() }.getOrNull() ?: ""

private val initialList = charArrayOf(
    'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ',
    'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
)

private val medialList = charArrayOf(
    'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ',
    'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'
)

private val finalList = charArrayOf(
    nullChar, 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ',
    'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ',
    'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
)

private val splitTable = mapOf(
    'ㅘ' to "ㅗㅏ",
    'ㅙ' to "ㅗㅐ",
    'ㅚ' to "ㅗㅣ",
    'ㅝ' to "ㅜㅓ",
    'ㅞ' to "ㅜㅔ",
    'ㅟ' to "ㅜㅣ",
    'ㅢ' to "ㅡㅣ",
    'ㄵ' to "ㄴㅈ",
    'ㄺ' to "ㄹㄱ",
    'ㄻ' to "ㄹㅁ",
    'ㄼ' to "ㄹㅂ",
    'ㄽ' to "ㄹㅅ",
    'ㄾ' to "ㄹㅌ",
    'ㄿ' to "ㄹㅍ",
    'ㅀ' to "ㄹㅎ",
    'ㅄ' to "ㅂㅅ"
)
