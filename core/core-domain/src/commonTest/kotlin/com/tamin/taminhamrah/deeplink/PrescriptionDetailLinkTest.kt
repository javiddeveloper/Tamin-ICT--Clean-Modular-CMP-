package com.tamin.taminhamrah.deeplink

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class PrescriptionDetailLinkTest {

    /** A link exactly as the assistant sent it. */
    private val assistantLink =
        "@prescription_detail?ARG_NOTE_HEAD_ELECTRONIC_PRESCRIPTION=12891082757&ARG_REQUEST_TYPE=3&PRES_TYPE=3" +
            "&ARG_NATIONAL_CODE=2400139474&ARG_CHILD_NATIONAL_CODE=0&ARG_FLAG_SATA=0&TOOLBAR_TITLE=%D9%86%D8%B3%D8%AE%D9%87"

    private fun argsOf(link: String) = assertIs<ParsedDeepLink.Feature>(DeepLinkParser.parse(link, DeepLinkSource.AGENT)).args

    @Test
    fun `the assistant's link names the insured's own prescription`() {
        assertEquals(
            PrescriptionDetailLink(noteHeadId = "12891082757", type = "3", patientNationalCode = "2400139474", flagSata = "0"),
            PrescriptionDetailLink.fromArgs(argsOf(assistantLink)),
        )
    }

    @Test
    fun `a child's prescription is opened for the child`() {
        val link = PrescriptionDetailLink.fromArgs(
            mapOf(
                "ARG_NOTE_HEAD_ELECTRONIC_PRESCRIPTION" to "1",
                "ARG_NATIONAL_CODE" to "2400139474",
                "ARG_CHILD_NATIONAL_CODE" to "0012345678",
            )
        )
        assertEquals("0012345678", link?.patientNationalCode)
    }

    @Test
    fun `the prescription type falls back to PRES_TYPE`() {
        assertEquals("2", PrescriptionDetailLink.fromArgs(mapOf("ARG_NOTE_HEAD_ELECTRONIC_PRESCRIPTION" to "1", "PRES_TYPE" to "2"))?.type)
    }

    @Test
    fun `without a prescription id there is nothing to open`() {
        assertNull(PrescriptionDetailLink.fromArgs(emptyMap()))
        assertNull(PrescriptionDetailLink.fromArgs(mapOf("ARG_NOTE_HEAD_ELECTRONIC_PRESCRIPTION" to " ")))
        assertNull(PrescriptionDetailLink.fromArgs(mapOf("ARG_NOTE_HEAD_ELECTRONIC_PRESCRIPTION" to "0")))
    }
}
