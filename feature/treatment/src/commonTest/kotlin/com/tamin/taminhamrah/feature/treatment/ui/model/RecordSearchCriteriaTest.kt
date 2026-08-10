package com.tamin.taminhamrah.feature.treatment.ui.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Which type ids the list request asks for — the one thing that decides what the person sees.
 */
class RecordSearchCriteriaTest {

    @Test
    fun `falls back to the tab when no service type is picked`() {
        val criteria = RecordSearchCriteria(tab = RecordTab.MEDICINE)

        assertEquals(RecordTab.MEDICINE.requestTypeIds, criteria.requestTypeIds())
    }

    @Test
    fun `all fans out across every tabbed category`() {
        val criteria = RecordSearchCriteria(tab = RecordTab.ALL)

        assertEquals(RecordTab.ALL.requestTypeIds, criteria.requestTypeIds())
        // داروخانه is deliberately not among them — it is only reachable through prescType.
        assertTrue(RecordTab.pharmacyTypeId !in criteria.requestTypeIds())
    }

    @Test
    fun `an explicit service type replaces the tab`() {
        val criteria = RecordSearchCriteria(
            tab = RecordTab.ALL,
            prescType = RecordTab.pharmacyTypeId,
        )

        assertEquals(listOf(RecordTab.pharmacyTypeId), criteria.requestTypeIds())
        assertTrue(criteria.isActive)
    }
}
