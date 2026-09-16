package com.tamin.taminhamrah.feature.taminServices.workshopInspection

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.taminServices.inspection.FakeInspectionRepository
import com.tamin.taminhamrah.feature.taminServices.inspection.FakeUserRepository
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionRequestErrorSource
import com.tamin.taminhamrah.feature.taminServices.workshopInspection.contract.WorkshopInspectionEvent
import com.tamin.taminhamrah.feature.taminServices.workshopInspection.contract.WorkshopInspectionIntent
import com.tamin.taminhamrah.feature.taminServices.workshopInspection.ui.WorkshopInspectionViewModel
import com.tamin.taminhamrah.model.inspection.InspectionPerformedDN
import com.tamin.taminhamrah.model.inspection.JobDN
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestDN
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestResultDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.model.user.UserProfileDN
import com.tamin.taminhamrah.useCases.inspection.GetInspectionReportPDFUseCase
import com.tamin.taminhamrah.useCases.inspection.GetJobPageUseCase
import com.tamin.taminhamrah.useCases.inspection.GetWorkshopInspectionsPageUseCase
import com.tamin.taminhamrah.useCases.inspection.SubmitInspectionUseCase
import com.tamin.taminhamrah.useCases.user.GetUserProfileUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class WorkshopInspectionViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: FakeInspectionRepository
    private lateinit var userRepository: FakeUserRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeInspectionRepository()
        userRepository = FakeUserRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = WorkshopInspectionViewModel(
        getWorkshopInspectionsPageUseCase = GetWorkshopInspectionsPageUseCase(repository),
        getJobPageUseCase = GetJobPageUseCase(repository),
        submitInspectionUseCase = SubmitInspectionUseCase(repository),
        getInspectionReportPDFUseCase = GetInspectionReportPDFUseCase(repository),
        getUserProfileUseCase = GetUserProfileUseCase(userRepository),
    )

    private fun sampleInspection(
        workshopNo: String = "9028212822",
        inspectionNo: String = "6310020000706",
    ) = InspectionPerformedDN(
        activityDesc = "تست", branchCode = "0210", branchdesc = "یک بجنورد",
        inspectionDate = 0L, inspectionNo = inspectionNo, insuranceNo = "",
        objectable = "1", relationType = "کارفرما", workshopName = "دبستان کارن",
        workshopNo = workshopNo, nationalCode = "0681895705"
    )

    @Test
    fun init_loadsFirstWorkshopInspectionPage() = runTest(testDispatcher) {
        repository.workshopInspectionsPageResult = listOf(sampleInspection())

        val viewModel = buildViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.inspections.size)
        assertEquals("دبستان کارن", state.inspections.first().workshopName)
        assertTrue(state.inspectionsEndReached)
    }

    @Test
    fun firstPageError_withNoItems_sendsShowToast() = runTest(testDispatcher) {
        repository.shouldThrowError = true

        val viewModel = buildViewModel()

        viewModel.events.test {
            assertIs<WorkshopInspectionEvent.ShowToast>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun openRequestFlow_prefillsWorkshopInfoFromItemAndLoadsJobFirstPage() = runTest(testDispatcher) {
        repository.jobPageResult = listOf(
            JobDN(operation = "", jobCode = "2035", jobDescription = "کارگر ساده تولید", status = "", statusDate = "")
        )
        userRepository.userProfileResult = null
        val viewModel = buildViewModel()
        advanceUntilIdle()

        val item = sampleInspection()
        viewModel.sendIntent(WorkshopInspectionIntent.OpenRequestFlow(item.toPRForTest()))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.showRequestFlow)
        assertEquals("دبستان کارن", state.workshopInfo.workshopName)
        assertEquals("9028212822", state.workshopInfo.workshopCode)
        assertEquals("0210", state.workshopInfo.branchCode)
        assertEquals("کارگر ساده تولید", state.jobs.single().jobDescription)
    }

    @Test
    fun searchJobs_nonBlankQuery_wrapsQueryInLikeWildcards() = runTest(testDispatcher) {
        val viewModel = buildViewModel()
        advanceUntilIdle()
        viewModel.sendIntent(WorkshopInspectionIntent.OpenRequestFlow(sampleInspection().toPRForTest()))
        advanceUntilIdle()

        viewModel.sendIntent(WorkshopInspectionIntent.SearchJobs("جوشکار"))
        advanceUntilIdle()

        val filters = repository.lastJobQuery?.filters.orEmpty()
        assertEquals(listOf(FilterProperty.JOB_DESCRIPTION), filters.map { it.property })
        assertEquals("*جوشکار*", filters.single().value)
        assertEquals("جوشکار", viewModel.uiState.value.jobQuery)
    }

    @Test
    fun retrySource_jobs_clearsJobErrorButLeavesUserInfoError() = runTest(testDispatcher) {
        repository.shouldThrowError = true
        userRepository.shouldThrowError = true
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(WorkshopInspectionIntent.OpenRequestFlow(sampleInspection().toPRForTest()))
        advanceUntilIdle()

        var errors = viewModel.uiState.value.requestErrors
        assertTrue(InspectionRequestErrorSource.USER_INFO in errors)
        assertTrue(InspectionRequestErrorSource.JOBS in errors)

        repository.shouldThrowError = false
        repository.jobPageResult = listOf(
            JobDN(operation = "", jobCode = "2035", jobDescription = "جوشکار", status = "", statusDate = "")
        )

        viewModel.sendIntent(WorkshopInspectionIntent.RetrySource(InspectionRequestErrorSource.JOBS))
        advanceUntilIdle()

        errors = viewModel.uiState.value.requestErrors
        assertTrue(InspectionRequestErrorSource.JOBS !in errors)
        assertTrue(InspectionRequestErrorSource.USER_INFO in errors)
        assertEquals("جوشکار", viewModel.uiState.value.jobs.single().jobDescription)
    }

    @Test
    fun submitRequest_success_updatesUiStateIsSubmitted() = runTest(testDispatcher) {
        repository.submitResult = SubmitInspectionRequestResultDN(id = 123L)
        val viewModel = buildViewModel()

        viewModel.uiState.test {
            awaitItem() // current
            viewModel.sendIntent(
                WorkshopInspectionIntent.SubmitRequest(
                    SubmitInspectionRequestDN(
                        brchCode = "", endDate = 0L, inspectionNumberOld = "",
                        insuranceId = "", insuranceJob = "", requestDescription = "",
                        startDate = 0L, workshopAddress = "", workshopManager = "",
                        workshopName = "", workshopNumber = "", workshopTel = ""
                    )
                )
            )

            var state = awaitItem()
            while (state.isLoading) {
                state = awaitItem()
            }

            assertTrue(state.isSubmitted)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun downloadReportPdf_success_updatesUiStateWithViewerPdf() = runTest(testDispatcher) {
        repository.reportPdfResult = PdfDownloadDN(pdf = null)
        val viewModel = buildViewModel()

        viewModel.uiState.test {
            awaitItem() // current
            viewModel.sendIntent(WorkshopInspectionIntent.DownloadReportPdf("6310020000706"))

            var state = awaitItem()
            while (state.isLoading) {
                state = awaitItem()
            }

            assertNotNull(state.viewerPdf)
            assertEquals(false, state.viewerDownloadFailed)
            assertEquals("6310020000706", repository.lastReportPdfInspectionNo)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun applySearch_setsAppliedFilterAndClosesSheet() = runTest(testDispatcher) {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(WorkshopInspectionIntent.UpdateWorkshopCodeQuery("9028"))
        viewModel.sendIntent(WorkshopInspectionIntent.ApplySearch)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("9028", state.appliedFilter?.workshopCode)
        assertEquals("", state.appliedFilter?.inspectionId)
        assertEquals(false, state.isSearchSheetOpen)
    }

    @Test
    fun applySearch_withBothFieldsBlank_clearsFilter() = runTest(testDispatcher) {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(WorkshopInspectionIntent.ApplySearch)
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.appliedFilter)
    }

    @Test
    fun clearSearch_resetsQueriesAndFilter() = runTest(testDispatcher) {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(WorkshopInspectionIntent.UpdateWorkshopCodeQuery("9028"))
        viewModel.sendIntent(WorkshopInspectionIntent.UpdateInspectionIdQuery("706"))
        viewModel.sendIntent(WorkshopInspectionIntent.ApplySearch)
        advanceUntilIdle()

        viewModel.sendIntent(WorkshopInspectionIntent.ClearSearch)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.appliedFilter)
        assertEquals("", state.workshopCodeQuery)
        assertEquals("", state.inspectionIdQuery)
    }
}

private fun InspectionPerformedDN.toPRForTest() =
    com.tamin.taminhamrah.feature.taminServices.inspection.ui.model.InspectionPerformedPR(
        activityDesc = activityDesc,
        branchCode = branchCode,
        branchdesc = branchdesc,
        inspectionDate = inspectionDate,
        inspectionNo = inspectionNo,
        insuranceNo = insuranceNo,
        objectable = objectable,
        relationType = relationType,
        workshopName = workshopName,
        workshopNo = workshopNo,
        nationalCode = nationalCode,
    )
