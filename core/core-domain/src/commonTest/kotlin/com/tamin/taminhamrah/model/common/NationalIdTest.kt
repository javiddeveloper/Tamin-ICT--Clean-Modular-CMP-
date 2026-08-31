package com.tamin.taminhamrah.model.common

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * The check digit is the only thing standing between a mistyped کد ملی and a request the service
 * rejects for a reason the user never sees, so each branch of it is pinned here.
 */
class NationalIdTest {

    @Test
    fun `accepts a code whose check digit matches`() {
        // Remainder >= 2, so the check digit is the complement.
        assertTrue(isValidIranianNationalId("0499370899"))
        assertTrue(isValidIranianNationalId("0790419904"))
    }

    @Test
    fun `accepts a code whose remainder is below two`() {
        // The branch where the remainder *is* the check digit rather than its complement.
        assertTrue(isValidIranianNationalId("1234567891"))
    }

    @Test
    fun `rejects a code whose check digit is wrong`() {
        // 0024567894 differs from a valid code only in its last digit — the case that reached
        // review as "the validator is broken"; it is the code that is wrong.
        assertFalse(isValidIranianNationalId("0024567894"))
        assertFalse(isValidIranianNationalId("0499370898"))
    }

    @Test
    fun `rejects ten identical digits`() {
        // These satisfy the arithmetic but are never issued.
        assertFalse(isValidIranianNationalId("1111111111"))
        assertFalse(isValidIranianNationalId("0000000000"))
    }

    @Test
    fun `rejects anything that is not ten ascii digits`() {
        assertFalse(isValidIranianNationalId(""))
        assertFalse(isValidIranianNationalId("123456789"))
        assertFalse(isValidIranianNationalId("04993708999"))
        assertFalse(isValidIranianNationalId("04993 0899"))
        // Persian digits must be normalised with digitsOnly() before they get here.
        assertFalse(isValidIranianNationalId("۰۴۹۹۳۷۰۸۹۹"))
    }
}
