package com.tamin.taminhamrah.feature.treatment.ui

import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMocks
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TreatmentMocksTest {

    @Test
    fun testMocksAreInitializedCorrectly() {
        assertNotNull(TreatmentMocks.mainUiState)
        assertNotNull(TreatmentMocks.healthProfileUiState)

        assertEquals("رضا احمدی", TreatmentMocks.patientMain.fullName)
        assertEquals(1, TreatmentMocks.mainUiState.deservedList.size)
        assertEquals("رضا احمدی", TreatmentMocks.deservedTreatment.fullName)
        assertEquals("پنی‌سیلین", TreatmentMocks.drugAllergy.drugName)
    }
}
