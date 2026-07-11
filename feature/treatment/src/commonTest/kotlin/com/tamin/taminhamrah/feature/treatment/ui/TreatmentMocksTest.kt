package com.tamin.taminhamrah.feature.treatment.ui

import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMocks
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TreatmentMocksTest {

    @Test
    fun testMocksAreInitializedCorrectly() {
        assertNotNull(TreatmentMocks.mainUiState)
        assertNotNull(TreatmentMocks.prescriptionsUiState)

        assertEquals("رضا احمدی", TreatmentMocks.patientMain.fullName)
        assertEquals("TRK123456", TreatmentMocks.prescription.trackingCode)
    }
}
