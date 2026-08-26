package com.tamin.taminhamrah.feature.taminServices.inspection

import com.tamin.taminhamrah.feature.taminServices.inspection.contract.IdentityContactStepState
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionUiState
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.WorkshopInfoStepState
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InspectionContractTest {

    @Test
    fun `step1 is valid with blank mobile, landline and email`() {
        val state = InspectionUiState(identityContact = IdentityContactStepState())

        assertTrue(state.isRequestStep1Valid)
    }

    @Test
    fun `step1 is invalid when mobile is provided but malformed`() {
        val state = InspectionUiState(
            identityContact = IdentityContactStepState(mobile = "123"),
        )

        assertFalse(state.isRequestStep1Valid)
    }

    private val validWorkshopInfo = WorkshopInfoStepState(
        workshopName = "workshop",
        workshopCode = "1234567890",
        employerName = "employer",
        branchCode = "branch",
        jobCode = "job",
        workshopAddress = "address",
    )

    @Test
    fun `step2 allows the same day for start and end of employment`() {
        val sameDay = 1_700_000_000_000L
        val state = InspectionUiState(
            workshopInfo = validWorkshopInfo.copy(
                startDateTimestamp = sameDay,
                endDateTimestamp = sameDay,
            ),
        )

        assertTrue(state.isRequestStep2Valid)
    }

    @Test
    fun `step2 is invalid when end date is before start date`() {
        val state = InspectionUiState(
            workshopInfo = validWorkshopInfo.copy(
                startDateTimestamp = 1_700_000_000_000L,
                endDateTimestamp = 1_699_999_999_000L,
            ),
        )

        assertFalse(state.isRequestStep2Valid)
    }

    @Test
    fun `step3 rejects a description at or below the minimum length`() {
        val state = InspectionUiState(requestDescription = "short")

        assertFalse(state.isRequestStep3Valid)
    }

    @Test
    fun `step3 accepts a description above the minimum length`() {
        val state = InspectionUiState(requestDescription = "a description long enough")

        assertTrue(state.isRequestStep3Valid)
    }
}
