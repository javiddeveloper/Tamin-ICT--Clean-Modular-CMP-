package com.tamin.taminhamrah.model.common

/**
 * An Iranian mobile number: eleven digits beginning `09`.
 *
 * Fold Persian digits to ASCII before calling — `Char.isDigit()` is true of `۰`–`۹` too, so a
 * length check alone lets numerals through that no `toLong` can read.
 */
private val IRANIAN_MOBILE = Regex("""^09\d{9}$""")

/**
 * Deliberately permissive: something, an `@`, something, a dot, something.
 *
 * The services accept addresses that a stricter pattern would reject, and the authority on whether
 * an address exists is the message that gets sent to it — the client's job here is to catch the
 * obvious typo, not to adjudicate RFC 5322.
 */
private val EMAIL = Regex("""^\S+@\S+\.\S+$""")

fun isValidIranianMobile(mobile: String): Boolean = IRANIAN_MOBILE.matches(mobile)

fun isValidEmail(email: String): Boolean = EMAIL.matches(email.trim())
