package com.tamin.taminhamrah.tools

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NetworkUtilsTest {

    @Test
    fun `looksLikeArabicScript is true for Persian copy`() {
        assertTrue("خطایی رخ داده است".looksLikeArabicScript())
        assertTrue("شما فاقد شماره حساب بانکی می باشید".looksLikeArabicScript())
    }

    @Test
    fun `looksLikeArabicScript is true for mixed text that contains Arabic-script characters`() {
        assertTrue("Error: خطای سرور".looksLikeArabicScript())
    }

    @Test
    fun `looksLikeArabicScript is false for English and technical tokens`() {
        assertFalse("Internal Server Error".looksLikeArabicScript())
        assertFalse("sso.to.sa.connection.exception".looksLikeArabicScript())
        assertFalse("invalid_grant".looksLikeArabicScript())
        assertFalse("".looksLikeArabicScript())
    }
}
