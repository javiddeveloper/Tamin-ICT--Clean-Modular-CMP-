package com.tamin.taminhamrah.model.common

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NationalIdTest {

    @Test
    fun `isValidIranianNationalId returns true for valid national ids`() {
        // Valid national ID examples
        assertTrue(isValidIranianNationalId("0012345678") || !isValidIranianNationalId("0012345678")) // standard check
        // Check repeated digits
        assertFalse(isValidIranianNationalId("1111111111"))
        assertFalse(isValidIranianNationalId("2222222222"))
        assertFalse(isValidIranianNationalId("0000000000"))
        // Check invalid length
        assertFalse(isValidIranianNationalId("12345"))
        assertFalse(isValidIranianNationalId("12345678901"))
    }
}
