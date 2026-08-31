package com.tamin.taminhamrah.model.common

/** How many digits an Iranian national id has. */
const val NATIONAL_ID_LENGTH = 10

/**
 * Whether [value] is a well-formed Iranian national id.
 *
 * Checked here rather than left to the service so a mistyped code is refused with a message the
 * user can act on, instead of a request that fails for a reason they cannot see. The last digit
 * is a checksum over the other nine; ten identical digits satisfy the arithmetic but are never
 * issued, so they are refused too.
 *
 * [value] must already be ASCII — convert Persian digits with `String.digitsOnly()` first.
 */
fun isValidIranianNationalId(value: String): Boolean {
    if (value.length != NATIONAL_ID_LENGTH || value.any { it !in '0'..'9' }) return false
    if (value.all { it == value[0] }) return false

    val checkDigit = value.last().digitToInt()
    val weightedSum = (0 until NATIONAL_ID_LENGTH - 1)
        .sumOf { index -> value[index].digitToInt() * (NATIONAL_ID_LENGTH - index) }
    val remainder = weightedSum % CHECKSUM_MODULUS

    return if (remainder < SMALL_REMAINDER) {
        checkDigit == remainder
    } else {
        checkDigit == CHECKSUM_MODULUS - remainder
    }
}

private const val CHECKSUM_MODULUS = 11

/** Below this the remainder *is* the check digit; at or above it the complement is. */
private const val SMALL_REMAINDER = 2
