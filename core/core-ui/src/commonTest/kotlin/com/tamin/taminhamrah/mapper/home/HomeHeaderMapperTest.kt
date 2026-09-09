package com.tamin.taminhamrah.mapper.home

import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentDN
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HomeHeaderMapperTest {

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

    private fun relation(relationDescription: String?) = ActiveRelationDN(
        id = null, firstName = null, lastName = null, nationalId = null, insuranceId = null,
        birthDate = null, relationWithTaminId = null, startDate = null, endDate = null,
        workshopId = null, workshopName = null, organizationId = null, organizationName = null,
        relationDescription = relationDescription,
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

    @Test
    fun `hasActiveRelation reflects whether any relation is active`() {
        assertTrue(listOf(relation("اصلی"), relation(null)).hasActiveRelation())
        assertFalse(listOf(relation(null), relation(null)).hasActiveRelation())
        assertFalse(emptyList<ActiveRelationDN>().hasActiveRelation())
    }
}
