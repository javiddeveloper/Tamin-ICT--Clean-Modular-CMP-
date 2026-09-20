package com.tamin.taminhamrah.model.treatment

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TreatmentCoverageMapperTest {

    private fun deserved(
        finalDesc: String? = null,
        message: String? = null,
    ) = DeservedTreatmentDN(
        birthDate = null, brhCode = null, brhName = null, dependenceType = null,
        fatherName = null, feranshiz = null, firstName = null, gender = null,
        healthBookletDate = null, id = null, idNumber = null, insuranceType = null,
        lastBookletDate = null, lastName = null, natCode = null, nationalId = null,
        parentRisuid = null, provinceCode = null, provinceName = null, regWorkshopId = null,
        regWorkshopName = null, risuid = null, message = message, finalDesc = finalDesc,
        illness = null, trackingCode = null,
    )

    @Test
    fun `toDarmanCoveredOrNull returns null when there is no record`() {
        assertNull(emptyList<DeservedTreatmentDN>().toDarmanCoveredOrNull())
    }

    @Test
    fun `toDarmanCoveredOrNull is true when the main record states no refusal`() {
        assertEquals(true, listOf(deserved()).toDarmanCoveredOrNull())
        assertEquals(true, listOf(deserved(finalDesc = "   ", message = "")).toDarmanCoveredOrNull())
    }

    @Test
    fun `toDarmanCoveredOrNull is false when finalDesc is worded`() {
        assertEquals(false, listOf(deserved(finalDesc = "عدم استحقاق درمان")).toDarmanCoveredOrNull())
    }

    @Test
    fun `toDarmanCoveredOrNull is false when message carries the not-entitled phrase`() {
        assertEquals(false, listOf(deserved(message = "بیمه‌شده عدم استحقاق دارد")).toDarmanCoveredOrNull())
    }

    @Test
    fun `toDarmanCoveredOrNull ignores a narrative message that is not a refusal`() {
        assertEquals(true, listOf(deserved(message = "از کفالت خارج شده است")).toDarmanCoveredOrNull())
    }
}
