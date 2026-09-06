package com.tamin.taminhamrah.feature.taminServices.inspection

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionEvent
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionIntent
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionRequestErrorSource
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.InspectionViewModel
import com.tamin.taminhamrah.model.inspection.BranchDN
import com.tamin.taminhamrah.model.inspection.InspectionPerformedDN
import com.tamin.taminhamrah.model.inspection.JobDN
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestDN
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestResultDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.model.user.UserProfileDN
import com.tamin.taminhamrah.useCases.inspection.GetBranchPageUseCase
import com.tamin.taminhamrah.useCases.inspection.GetInspectionReportPDFUseCase
import com.tamin.taminhamrah.useCases.inspection.GetInsurancePageUseCase
import com.tamin.taminhamrah.useCases.inspection.GetJobPageUseCase
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class InspectionViewModelTest {

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

    // Built per-test so repository fixtures can be arranged before init's first-page load fires.
    private fun buildViewModel() = InspectionViewModel(
        getInsurancePageUseCase = GetInsurancePageUseCase(repository),
        getBranchPageUseCase = GetBranchPageUseCase(repository),
        getJobPageUseCase = GetJobPageUseCase(repository),
        submitInspectionUseCase = SubmitInspectionUseCase(repository),
        getInspectionReportPDFUseCase = GetInspectionReportPDFUseCase(repository),
        getUserProfileUseCase = GetUserProfileUseCase(userRepository),
    )

    private fun sampleInspection(inspectionNo: String = "01") = InspectionPerformedDN(
        activityDesc = "تست", branchCode = "0010", branchdesc = "شعبه",
        inspectionDate = 0L, inspectionNo = inspectionNo, insuranceNo = "",
        objectable = "", relationType = "", workshopName = "",
        workshopNo = "", nationalCode = ""
    )

    @Test
    fun init_loadsFirstInspectionPage() = runTest(testDispatcher) {
        repository.insurancePageResult = listOf(sampleInspection())

        val viewModel = buildViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.inspections.size)
        assertEquals("تست", state.inspections.first().activityDesc)
        assertTrue(state.inspectionsEndReached) // short page -> end reached
    }

    @Test
    fun firstPageError_withNoItems_sendsShowToast() = runTest(testDispatcher) {
        repository.shouldThrowError = true

        val viewModel = buildViewModel()

        viewModel.events.test {
            assertIs<InspectionEvent.ShowToast>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun openRequestFlow_loadsBranchAndJobFirstPages() = runTest(testDispatcher) {
        repository.branchPageResult = listOf(
            BranchDN(
                operation = "", code = "0010", name = "یک تهران",
                minCode = "", maxCode = "", type = "", branchAddress = "",
                cityCode = "", status = ""
            )
        )
        repository.jobPageResult = listOf(
            JobDN(operation = "", jobCode = "2035", jobDescription = "قرص ساز", status = "", statusDate = "")
        )
        userRepository.userProfileResult = null
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(InspectionIntent.OpenRequestFlow())
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.showRequestFlow)
        assertEquals("یک تهران", state.branches.single().name)
        assertEquals("قرص ساز", state.jobs.single().jobDescription)
        // branch base query always carries type + status filters
        assertEquals(
            listOf(FilterProperty.TYPE, FilterProperty.STATUS),
            repository.lastBranchQuery?.filters?.map { it.property },
        )
    }

    @Test
    fun searchBranches_addsNameLikeFilter() = runTest(testDispatcher) {
        val viewModel = buildViewModel()
        advanceUntilIdle()
        viewModel.sendIntent(InspectionIntent.OpenRequestFlow())
        advanceUntilIdle()

        viewModel.sendIntent(InspectionIntent.SearchBranches("پانزده"))
        advanceUntilIdle()

        val filters = repository.lastBranchQuery?.filters.orEmpty()
        assertEquals(
            listOf(FilterProperty.TYPE, FilterProperty.STATUS, FilterProperty.NAME),
            filters.map { it.property },
        )
        assertEquals("*پانزده*", filters.last().value)
        assertEquals("پانزده", viewModel.uiState.value.branchQuery)
    }

    @Test
    fun searchJobs_blankQuery_sendsMatchAllLikeFilter() = runTest(testDispatcher) {
        val viewModel = buildViewModel()
        advanceUntilIdle()
        viewModel.sendIntent(InspectionIntent.OpenRequestFlow())
        advanceUntilIdle()

        viewModel.sendIntent(InspectionIntent.SearchJobs("   "))
        advanceUntilIdle()

        val filters = repository.lastJobQuery?.filters.orEmpty()
        assertEquals(listOf(FilterProperty.JOB_DESCRIPTION), filters.map { it.property })
        // blank query -> match-all wildcard, never an absent filter
        // (ported from SubmitInspectionRequestViewModel.getJob: the job endpoint returns
        //  nothing at all without a jobDescription filter)
        assertEquals("*", filters.single().value)
        assertEquals("   ", viewModel.uiState.value.jobQuery)
    }

    @Test
    fun searchJobs_nonBlankQuery_wrapsQueryInLikeWildcards() = runTest(testDispatcher) {
        val viewModel = buildViewModel()
        advanceUntilIdle()
        viewModel.sendIntent(InspectionIntent.OpenRequestFlow())
        advanceUntilIdle()

        viewModel.sendIntent(InspectionIntent.SearchJobs("کارگر"))
        advanceUntilIdle()

        val filters = repository.lastJobQuery?.filters.orEmpty()
        assertEquals(listOf(FilterProperty.JOB_DESCRIPTION), filters.map { it.property })
        assertEquals("*کارگر*", filters.single().value)
        assertEquals("کارگر", viewModel.uiState.value.jobQuery)
    }

    @Test
    fun retrySource_branches_clearsOnlyBranchErrorAndLeavesJobError() = runTest(testDispatcher) {
        repository.shouldThrowError = true
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(InspectionIntent.OpenRequestFlow())
        advanceUntilIdle()

        // both pickers failed their first page with nothing to show -> both tagged
        var errors = viewModel.uiState.value.requestErrors
        assertTrue(InspectionRequestErrorSource.BRANCHES in errors)
        assertTrue(InspectionRequestErrorSource.JOBS in errors)

        repository.shouldThrowError = false
        repository.branchPageResult = listOf(
            BranchDN(
                operation = "", code = "0010", name = "یک تهران",
                minCode = "", maxCode = "", type = "", branchAddress = "",
                cityCode = "", status = ""
            )
        )

        viewModel.sendIntent(InspectionIntent.RetrySource(InspectionRequestErrorSource.BRANCHES))
        advanceUntilIdle()

        errors = viewModel.uiState.value.requestErrors
        assertTrue(InspectionRequestErrorSource.BRANCHES !in errors) // retried source recovered
        assertTrue(InspectionRequestErrorSource.JOBS in errors)      // untouched picker still errored
        assertEquals("یک تهران", viewModel.uiState.value.branches.single().name)
    }

    @Test
    fun retrySource_jobs_clearsOnlyJobErrorAndLeavesBranchError() = runTest(testDispatcher) {
        repository.shouldThrowError = true
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(InspectionIntent.OpenRequestFlow())
        advanceUntilIdle()

        var errors = viewModel.uiState.value.requestErrors
        assertTrue(InspectionRequestErrorSource.BRANCHES in errors)
        assertTrue(InspectionRequestErrorSource.JOBS in errors)

        repository.shouldThrowError = false
        repository.jobPageResult = listOf(
            JobDN(operation = "", jobCode = "2035", jobDescription = "قرص ساز", status = "", statusDate = "")
        )

        viewModel.sendIntent(InspectionIntent.RetrySource(InspectionRequestErrorSource.JOBS))
        advanceUntilIdle()

        errors = viewModel.uiState.value.requestErrors
        assertTrue(InspectionRequestErrorSource.JOBS !in errors)
        assertTrue(InspectionRequestErrorSource.BRANCHES in errors)
        assertEquals("قرص ساز", viewModel.uiState.value.jobs.single().jobDescription)
    }

    @Test
    fun retrySource_userInfo_clearsOnlyUserInfoErrorAndLeavesPickerErrors() = runTest(testDispatcher) {
        repository.shouldThrowError = true
        userRepository.shouldThrowError = true
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(InspectionIntent.OpenRequestFlow())
        advanceUntilIdle()

        var errors = viewModel.uiState.value.requestErrors
        assertTrue(InspectionRequestErrorSource.USER_INFO in errors)
        assertTrue(InspectionRequestErrorSource.BRANCHES in errors)
        assertTrue(InspectionRequestErrorSource.JOBS in errors)

        userRepository.shouldThrowError = false
        userRepository.userProfileResult = UserProfileDN(
            entityId = null, login = null, firstName = "علی", lastName = "رضایی",
            email = null, nationalCode = "0012345678", mobile = "09120000000"
        )

        viewModel.sendIntent(InspectionIntent.RetrySource(InspectionRequestErrorSource.USER_INFO))
        advanceUntilIdle()

        errors = viewModel.uiState.value.requestErrors
        assertTrue(InspectionRequestErrorSource.USER_INFO !in errors)
        assertTrue(InspectionRequestErrorSource.BRANCHES in errors)
        assertTrue(InspectionRequestErrorSource.JOBS in errors)
        assertEquals("علی رضایی", viewModel.uiState.value.identityContact.fullName)
    }

    @Test
    fun submitRequest_success_updatesUiStateIsSubmitted() = runTest(testDispatcher) {
        repository.submitResult = SubmitInspectionRequestResultDN(id = 123L)
        val viewModel = buildViewModel()

        viewModel.uiState.test {
            awaitItem() // current
            viewModel.sendIntent(
                InspectionIntent.SubmitRequest(
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
            viewModel.sendIntent(InspectionIntent.DownloadReportPdf("0130980012641"))

            var state = awaitItem()
            while (state.isLoading) {
                state = awaitItem()
            }

            assertNotNull(state.viewerPdf)
            assertEquals(false, state.viewerDownloadFailed)
            assertEquals("0130980012641", repository.lastReportPdfInspectionNo)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun downloadReportPdf_error_marksViewerDownloadFailedAndSendsShowToastEvent() = runTest(testDispatcher) {
        val viewModel = buildViewModel()
        repository.shouldThrowError = true

        viewModel.events.test {
            viewModel.sendIntent(InspectionIntent.DownloadReportPdf("0130980012641"))
            assertIs<InspectionEvent.ShowToast>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun dismissPdfViewer_clearsViewerPdf() = runTest(testDispatcher) {
        repository.reportPdfResult = PdfDownloadDN(pdf = null)
        val viewModel = buildViewModel()

        viewModel.uiState.test {
            awaitItem() // current
            viewModel.sendIntent(InspectionIntent.DownloadReportPdf("0130980012641"))

            var state = awaitItem()
            while (state.isLoading) {
                state = awaitItem()
            }
            assertNotNull(state.viewerPdf)

            viewModel.sendIntent(InspectionIntent.DismissPdfViewer)
            state = awaitItem()

            assertEquals(null, state.viewerPdf)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
