package com.tamin.taminhamrah.util

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ValidationUtilsTest {

    @Test
    fun isLandlineNumberValid_acceptsLegacyPattern() {
        assertTrue(ValidationUtils.isLandlineNumberValid("02188974532"))
        assertTrue(ValidationUtils.isLandlineNumberValid("05832245678"))
    }

    @Test
    fun isLandlineNumberValid_rejectsMobilePrefix() {
        assertFalse(ValidationUtils.isLandlineNumberValid("09123456789"))
    }

    @Test
    fun isLandlineNumberValid_rejectsInvalidAreaCode() {
        assertFalse(ValidationUtils.isLandlineNumberValid("00000000000"))
        assertFalse(ValidationUtils.isLandlineNumberValid("09912345678"))
    }

    @Test
    fun isLandlineValid_allowsEmptyOrValidLandline() {
        assertTrue(ValidationUtils.isLandlineValid(""))
        assertTrue(ValidationUtils.isLandlineValid("02188974532"))
        assertFalse(ValidationUtils.isLandlineValid("09123456789"))
    }
}
