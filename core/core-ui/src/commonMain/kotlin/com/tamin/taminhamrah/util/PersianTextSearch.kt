package com.tamin.taminhamrah.util

/**
 * Folding Persian text down to something two strings can actually be compared on.
 *
 * The same name is not stored the same way twice. Records come from several source systems and the
 * person typing uses whatever keyboard they have, so «علي اكبر» (Arabic ي and ك), «علی‌اکبر» (Persian
 * letters, joined by a zero-width non-joiner) and «علی  اکبر» (two spaces) are all the same doctor
 * and none of them `contains` another. Comparing raw text therefore fails on data that is correct.
 */

/**
 * The same string with every difference that does not change how a Persian reader reads it removed.
 *
 * Specifically: Arabic ي/ك/ة folded to Persian ی/ک/ه, all alef forms to bare ا, tatweel and harakat
 * dropped, the zero-width joiners treated as word breaks, Arabic-Indic and Persian digits folded to
 * ASCII, and runs of any whitespace collapsed to a single space with none left at either end. The
 * result is lower-cased so a Latin name mixed into the field compares too.
 */
fun String.foldForSearch(): String {
    val folded = StringBuilder(length)
    var pendingSpace = false
    for (char in this) {
        val mapped = char.foldPersianChar()
        when {
            mapped == null -> Unit // dropped: joiner, tatweel or a diacritic
            mapped.isWhitespace() -> if (folded.isNotEmpty()) pendingSpace = true
            else -> {
                if (pendingSpace) {
                    folded.append(' ')
                    pendingSpace = false
                }
                folded.append(mapped)
            }
        }
    }
    return folded.toString().lowercase()
}

/**
 * Whether every word of [query] is found in this text once both sides are folded.
 *
 * Two things are deliberately forgiving here, because both are ways a correct search comes back
 * empty:
 *
 *  - **Order.** The words need not appear in the order typed, so «احمدی علی» finds «دکتر علی احمدی».
 *    Some source systems record surname first and others given-name first.
 *  - **Word breaks.** A word is also looked for with the text's spaces removed, so «علی اکبر»,
 *    «علی‌اکبر» (نیم‌فاصله) and «علیاکبر» all find each other. Whether a compound name is written
 *    open, half-spaced or closed is not something the person searching can be expected to guess.
 */
fun String.containsFoldedWords(query: List<String>): Boolean {
    if (query.isEmpty()) return true
    val spaced = foldForSearch()
    if (query.all { word -> spaced.contains(word) }) return true
    // Only worth building when a word failed: it exists solely to bridge the word-break variants.
    val closed = spaced.replace(" ", "")
    return query.all { word -> closed.contains(word.replace(" ", "")) }
}

/** Splits a typed query into the folded words [containsFoldedWords] looks for. */
fun String.toFoldedWords(): List<String> =
    foldForSearch().split(' ').filter { it.isNotEmpty() }

/**
 * One character folded, or null when it carries no meaning for a comparison.
 *
 * Kept as a `when` over code points rather than a map lookup: it runs once per character of every
 * record on screen, and a branch table is cheaper than hashing each char.
 */
private fun Char.foldPersianChar(): Char? = when (this) {
    // Arabic forms of letters Persian writes differently.
    'ي', 'ى', 'ئ' -> 'ی'
    'ك' -> 'ک'
    'ة' -> 'ه'
    'ؤ' -> 'و'
    // Every alef with a mark reads as a plain alef when searching.
    'أ', 'إ', 'آ', 'ٱ', 'ٲ', 'ٳ' -> 'ا'
    // A نیم‌فاصله separates words to a reader, so it folds to a space rather than vanishing --
    // dropping it would leave «علی‌اکبر» as one token that a query typed with a real space misses.
    ZWNJ, ZWJ -> ' '
    // Tatweel only stretches a join; it is decoration and never a distinction.
    TATWEEL -> null
    // Arabic-Indic and Persian digits, so a number inside a name compares to its ASCII form.
    in ARABIC_INDIC_ZERO..ARABIC_INDIC_NINE -> '0' + (this - ARABIC_INDIC_ZERO)
    in PERSIAN_ZERO..PERSIAN_NINE -> '0' + (this - PERSIAN_ZERO)
    else -> if (isDiacritic()) null else this
}

/** Harakat and the other combining marks that may or may not have been typed. */
private fun Char.isDiacritic(): Boolean = this in DIACRITICS_START..DIACRITICS_END

private const val ZWNJ = '‌'
private const val ZWJ = '‍'
private const val TATWEEL = 'ـ'
private const val ARABIC_INDIC_ZERO = '٠'
private const val ARABIC_INDIC_NINE = '٩'
private const val PERSIAN_ZERO = '۰'
private const val PERSIAN_NINE = '۹'
private const val DIACRITICS_START = 'ً'
private const val DIACRITICS_END = 'ْ'
