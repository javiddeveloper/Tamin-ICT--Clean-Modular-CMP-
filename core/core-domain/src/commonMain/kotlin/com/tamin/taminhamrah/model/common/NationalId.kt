package com.tamin.taminhamrah.model.common

/**
 * Validates an Iranian 10-digit National Code (کد ملی) according to the official checksum algorithm.
 */
fun isValidIranianNationalId(nationalId: String): Boolean {
    val clean = nationalId.filter { it.isDigit() }
    if (clean.length != 10) return false
    if (clean.all { it == clean[0] }) return false

    val check = clean[9].digitToInt()
    val sum = (0..8).sumOf { i -> clean[i].digitToInt() * (10 - i) }
    val remainder = sum % 11

    return if (remainder < 2) {
        check == remainder
    } else {
        check == 11 - remainder
    }
}
