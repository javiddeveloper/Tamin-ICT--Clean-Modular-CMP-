package com.tamin.taminhamrah.model.payment

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * The payer identifier is the one thing this screen validates before spending a ticket. The
 * gateway answers a malformed identifier with a generic failure that reads, to the user, as though
 * the payment itself was refused — so a wrong answer here costs the user a payment attempt.
 */
class PayerTypeTest {

    @Test
    fun `the gateway's person-type numbers are the published ones`() {
        assertEquals(0, PayerType.CURRENT_USER.code)
        assertEquals(1, PayerType.OTHER_PERSON.code)
        assertEquals(2, PayerType.LEGAL_ENTITY.code)
        assertEquals(3, PayerType.FOREIGN_NATIONAL.code)
    }

    @Test
    fun `paying for oneself needs nothing typed`() {
        assertFalse(PayerType.CURRENT_USER.needsIdentifier)
        assertTrue(PayerType.CURRENT_USER.isIdentifierValid(""))
    }

    @Test
    fun `a national code is accepted only when its check digit agrees`() {
        assertTrue(isValidNationalCode("0499370899"))
        assertTrue(isValidNationalCode("0790419904"))
        // Same digits, last one changed: the checksum is the only thing separating them.
        assertFalse(isValidNationalCode("0499370898"))
    }

    @Test
    fun `ten identical digits are refused even though the checksum works out`() {
        assertFalse(isValidNationalCode("1111111111"))
    }

    @Test
    fun `a national code of the wrong length or with non-digits is refused`() {
        assertFalse(isValidNationalCode("049937089"))
        assertFalse(isValidNationalCode("04993708999"))
        assertFalse(isValidNationalCode("049937089a"))
        // Persian digits look like digits to Char.isDigit but no parser can read them.
        assertFalse(isValidNationalCode("۰۴۹۹۳۷۰۸۹۹"))
    }

    @Test
    fun `a legal entity is eleven digits`() {
        assertTrue(PayerType.LEGAL_ENTITY.isIdentifierValid("10101234567"))
        assertFalse(PayerType.LEGAL_ENTITY.isIdentifierValid("1010123456"))
        assertFalse(PayerType.LEGAL_ENTITY.isIdentifierValid("101012345678"))
    }

    @Test
    fun `a foreign national code is ten to sixteen digits`() {
        assertTrue(PayerType.FOREIGN_NATIONAL.isIdentifierValid("1234567890"))
        assertTrue(PayerType.FOREIGN_NATIONAL.isIdentifierValid("1234567890123456"))
        assertFalse(PayerType.FOREIGN_NATIONAL.isIdentifierValid("123456789"))
        assertFalse(PayerType.FOREIGN_NATIONAL.isIdentifierValid("12345678901234567"))
    }

    @Test
    fun `a foreign national code is not put through the national-code checksum`() {
        // The rule that governs کد ملی does not govern کد اتباع; applying it would reject valid
        // identifiers that the gateway accepts.
        assertTrue(PayerType.FOREIGN_NATIONAL.isIdentifierValid("0499370898"))
    }
}
