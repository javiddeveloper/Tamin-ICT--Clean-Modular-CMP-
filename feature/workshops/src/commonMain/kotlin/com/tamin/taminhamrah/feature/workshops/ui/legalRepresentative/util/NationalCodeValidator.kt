package com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.util

/** Standard Iranian national-ID checksum (10 digits, weighted-sum check digit). */
internal fun isValidIranianNationalCode(code: String): Boolean {
    if (code.length != 10 || code.any { !it.isDigit() }) return false
    if (code.toSet().size == 1) return false

    val digits = code.map { it.digitToInt() }
    val checkDigit = digits[9]
    val sum = (0..8).sumOf { digits[it] * (10 - it) }
    val remainder = sum % 11
    return if (remainder < 2) checkDigit == remainder else checkDigit == 11 - remainder
}
