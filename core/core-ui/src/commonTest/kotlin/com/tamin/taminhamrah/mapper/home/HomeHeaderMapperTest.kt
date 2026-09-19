package com.tamin.taminhamrah.mapper.home

import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HomeHeaderMapperTest {

    private fun relation(relationDescription: String?) = ActiveRelationDN(
        id = null, firstName = null, lastName = null, nationalId = null, insuranceId = null,
        birthDate = null, relationWithTaminId = null, startDate = null, endDate = null,
        workshopId = null, workshopName = null, organizationId = null, organizationName = null,
        relationDescription = relationDescription,
    )

    @Test
    fun `hasActiveRelation reflects whether any relation is active`() {
        assertTrue(listOf(relation("اصلی"), relation(null)).hasActiveRelation())
        assertFalse(listOf(relation(null), relation(null)).hasActiveRelation())
        assertFalse(emptyList<ActiveRelationDN>().hasActiveRelation())
    }
}
