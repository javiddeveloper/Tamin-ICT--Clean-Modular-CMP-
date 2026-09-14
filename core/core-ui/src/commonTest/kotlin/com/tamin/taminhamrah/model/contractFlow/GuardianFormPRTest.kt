package com.tamin.taminhamrah.model.contractFlow

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GuardianFormPRTest {

    @Test
    fun `isValid requires ten-digit letter number`() {
        val form = validForm(letterNumber = "1234567890")
        assertTrue(form.isValid)
        assertTrue(form.isLetterNumberValid)
    }

    @Test
    fun `isValid rejects letter number shorter than ten digits`() {
        val form = validForm(letterNumber = "123")
        assertFalse(form.isLetterNumberValid)
        assertFalse(form.isValid)
    }

    @Test
    fun `isValid rejects blank letter number`() {
        val form = validForm(letterNumber = "")
        assertFalse(form.isLetterNumberValid)
        assertFalse(form.isValid)
    }

    @Test
    fun `isValid rejects incomplete national id`() {
        assertFalse(validForm(nationalId = "123").isValid)
    }

    @Test
    fun `isValid rejects missing full name`() {
        assertFalse(validForm(fullName = "").isValid)
    }

    @Test
    fun `isValid rejects missing letter date`() {
        assertFalse(validForm(letterDateFormatted = "").isValid)
    }

    @Test
    fun `isValid rejects missing document`() {
        assertFalse(
            validForm().copy(documentGuid = null, documentPreviewBytes = null).isValid,
        )
    }

    private fun validForm(
        nationalId: String = "0012345678",
        letterNumber: String = "1234567890",
        fullName: String = "علی رضایی",
        letterDateFormatted: String = "1403/01/01",
    ) = GuardianFormPR(
        nationalId = nationalId,
        letterNumber = letterNumber,
        fullName = fullName,
        letterDateFormatted = letterDateFormatted,
        letterDateEpoch = 1_700_000_000_000L,
        documentGuid = "guid-1",
    )
}
