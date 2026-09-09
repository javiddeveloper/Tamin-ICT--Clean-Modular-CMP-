package com.tamin.taminhamrah.model.payment

import kotlinx.serialization.Serializable

/**
 * Who the money is being paid on behalf of, as the payment gateway numbers it.
 *
 * The gateway takes this as `personType` in the payment-link body, and the number is part of its
 * published contract — hence [code] rather than an ordinal, so reordering the entries here can
 * never silently change what is sent.
 */
@Serializable
enum class PayerType(val code: Int) {
    /** The signed-in user; their own national code is sent without asking for it. */
    CURRENT_USER(0),

    /** Another natural person — a ten-digit national code (کد ملی). */
    OTHER_PERSON(1),

    /** A legal entity — an eleven-digit national ID (شناسه ملی). */
    LEGAL_ENTITY(2),

    /** A foreign national — a ten-to-sixteen character identifier (کد اتباع). */
    FOREIGN_NATIONAL(3);

    /** Only [CURRENT_USER] pays without the user typing an identifier first. */
    val needsIdentifier: Boolean get() = this != CURRENT_USER
}

/**
 * Whether [identifier] is a usable identifier for this payer type.
 *
 * Fold Persian digits to ASCII before calling: `Char.isDigit()` is true of `۰`–`۹` as well, so a
 * length-and-digits check alone would accept numerals the gateway cannot read.
 */
fun PayerType.isIdentifierValid(identifier: String): Boolean = when (this) {
    PayerType.CURRENT_USER -> true
    PayerType.OTHER_PERSON -> isValidNationalCode(identifier)
    PayerType.LEGAL_ENTITY -> identifier.length == 11 && identifier.all { it in '0'..'9' }
    PayerType.FOREIGN_NATIONAL -> identifier.length in 10..16 && identifier.all { it in '0'..'9' }
}

/**
 * The Iranian national-code check digit, the same rule the old client applied before letting a
 * payment start. Ten digits, not all identical, with the eleventh-position checksum matching.
 */
fun isValidNationalCode(code: String): Boolean {
    if (code.length != 10 || code.any { it !in '0'..'9' }) return false
    if (code.all { it == code[0] }) return false
    val digits = code.map { it - '0' }
    val sum = (0..8).sumOf { digits[it] * (10 - it) }
    val remainder = sum % 11
    val checkDigit = digits[9]
    return if (remainder < 2) checkDigit == remainder else checkDigit == 11 - remainder
}
