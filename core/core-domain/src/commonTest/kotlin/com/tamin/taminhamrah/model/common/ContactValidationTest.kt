package com.tamin.taminhamrah.model.common

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ContactValidationTest {

    @Test
    fun elevenDigitsBeginningZeroNineIsAValidMobile() {
        assertTrue(isValidIranianMobile("09153214478"))
    }

    @Test
    fun aMobileIsRejectedWhenItIsTooShortTooLongOrDoesNotStartWithZeroNine() {
        assertFalse(isValidIranianMobile("0915321447"))
        assertFalse(isValidIranianMobile("091532144780"))
        assertFalse(isValidIranianMobile("08153214478"))
        assertFalse(isValidIranianMobile(""))
    }

    /**
     * The caller folds Persian digits before asking. Guarded here because `Char.isDigit()` is true
     * of `۰`-`۹`, so a length check upstream can let them reach this without folding.
     */
    @Test
    fun persianDigitsAreNotAValidMobile() {
        assertFalse(isValidIranianMobile("۰۹۱۵۳۲۱۴۴۷۸"))
    }

    @Test
    fun anAddressWithAUserHostAndDotIsAValidEmail() {
        assertTrue(isValidEmail("h.tavakoli@gmail.com"))
        assertTrue(isValidEmail("  info@damabokhar.ir  "))
    }

    @Test
    fun anAddressMissingItsAtHostOrDotIsRejected() {
        assertFalse(isValidEmail("h.tavakoli"))
        assertFalse(isValidEmail("h.tavakoli@gmail"))
        assertFalse(isValidEmail("@gmail.com"))
        assertFalse(isValidEmail("a b@gmail.com"))
        assertFalse(isValidEmail(""))
    }
}
