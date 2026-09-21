package com.tamin.taminhamrah.feature.taminServices.workshopInspection

import com.tamin.taminhamrah.feature.taminServices.inspection.contract.IdentityContactStepState
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.WorkshopInfoStepState
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.model.InspectionPerformedPR
import com.tamin.taminhamrah.feature.taminServices.workshopInspection.contract.WorkshopInspectionFilter
import com.tamin.taminhamrah.feature.taminServices.workshopInspection.contract.WorkshopInspectionUiState
import kotlinx.collections.immutable.toImmutableList
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WorkshopInspectionContractTest {

    @Test
    fun `step1 is valid when mobile is blank`() {
        // Matches legacy's SubmitInspectionRequestFragment#checkValidInputIdentityInfoStep, which
        // treats mobile as optional for this same employer/objection flow.
        val state = WorkshopInspectionUiState(identityContact = IdentityContactStepState())

        assertTrue(state.isRequestStep1Valid)
    }

    @Test
    fun `step1 is invalid when mobile is provided but malformed`() {
        val state = WorkshopInspectionUiState(
            identityContact = IdentityContactStepState(mobile = "123"),
        )

        assertFalse(state.isRequestStep1Valid)
    }

    @Test
    fun `step1 is invalid when mobile has 11 digits but not a real mobile pattern`() {
        // Legacy validates with the real ^09\d{9}$ pattern, not just length — a landline-shaped
        // 11-digit string must not pass as a mobile number.
        val state = WorkshopInspectionUiState(
            identityContact = IdentityContactStepState(mobile = "02112345678"),
        )

        assertFalse(state.isRequestStep1Valid)
    }

    @Test
    fun `step1 is valid with a valid mobile and blank landline, email`() {
        val state = WorkshopInspectionUiState(
            identityContact = IdentityContactStepState(mobile = "09123456789"),
        )

        assertTrue(state.isRequestStep1Valid)
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
        val state = WorkshopInspectionUiState(
            workshopInfo = validWorkshopInfo.copy(
                startDateTimestamp = sameDay,
                endDateTimestamp = sameDay,
            ),
        )

        assertTrue(state.isRequestStep2Valid)
    }

    @Test
    fun `step2 is invalid when end date is before start date`() {
        val state = WorkshopInspectionUiState(
            workshopInfo = validWorkshopInfo.copy(
                startDateTimestamp = 1_700_000_000_000L,
                endDateTimestamp = 1_699_999_999_000L,
            ),
        )

        assertFalse(state.isRequestStep2Valid)
    }

    @Test
    fun `step3 rejects a description at or below the minimum length`() {
        val state = WorkshopInspectionUiState(requestDescription = "short")

        assertFalse(state.isRequestStep3Valid)
    }

    @Test
    fun `step3 accepts a description above the minimum length`() {
        val state = WorkshopInspectionUiState(requestDescription = "a description long enough")

        assertTrue(state.isRequestStep3Valid)
    }

    private fun inspection(workshopNo: String, inspectionNo: String) = InspectionPerformedPR(
        activityDesc = "", branchCode = "", branchdesc = "", inspectionDate = 0L,
        inspectionNo = inspectionNo, insuranceNo = "", objectable = "1", relationType = "",
        workshopName = "", workshopNo = workshopNo, nationalCode = "",
    )

    @Test
    fun `filteredInspections returns all items when no filter is applied`() {
        val items = listOf(inspection("111", "a"), inspection("222", "b")).toImmutableList()
        val state = WorkshopInspectionUiState(inspections = items)

        assertEquals(items, state.filteredInspections)
    }

    @Test
    fun `filteredInspections matches by workshop code substring`() {
        val items = listOf(inspection("9028212822", "a"), inspection("9014778315", "b")).toImmutableList()
        val state = WorkshopInspectionUiState(
            inspections = items,
            appliedFilter = WorkshopInspectionFilter(workshopCode = "9028", inspectionId = ""),
        )

        assertEquals(1, state.filteredInspections.size)
        assertEquals("9028212822", state.filteredInspections.single().workshopNo)
    }

    @Test
    fun `filteredInspections matches by inspection id substring`() {
        val items = listOf(inspection("111", "6310020000706"), inspection("222", "6310020000598")).toImmutableList()
        val state = WorkshopInspectionUiState(
            inspections = items,
            appliedFilter = WorkshopInspectionFilter(workshopCode = "", inspectionId = "706"),
        )

        assertEquals(1, state.filteredInspections.size)
        assertEquals("6310020000706", state.filteredInspections.single().inspectionNo)
    }

    @Test
    fun `filteredInspections requires both fields to match when both are set`() {
        val items = listOf(inspection("111", "aaa"), inspection("111", "bbb")).toImmutableList()
        val state = WorkshopInspectionUiState(
            inspections = items,
            appliedFilter = WorkshopInspectionFilter(workshopCode = "111", inspectionId = "bbb"),
        )

        assertEquals(1, state.filteredInspections.size)
        assertEquals("bbb", state.filteredInspections.single().inspectionNo)
    }
}
