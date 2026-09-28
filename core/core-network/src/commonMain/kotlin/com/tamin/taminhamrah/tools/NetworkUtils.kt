package com.tamin.taminhamrah.tools

/**
 * Arabic-script heuristic used to decide whether a backend string is safe
 * to show to the user. Same Unicode ranges as old_android's
 * `ValidationUtil.isProbablyArabic()` — Persian, Arabic, and related presentation forms.
 */
private val ARABIC_SCRIPT_REGEX = Regex("[\u0600-\u06FF\uFB50-\uFDFF\uFE70-\uFEFF]")

fun String.looksLikeArabicScript(): Boolean = ARABIC_SCRIPT_REGEX.containsMatchIn(this)
