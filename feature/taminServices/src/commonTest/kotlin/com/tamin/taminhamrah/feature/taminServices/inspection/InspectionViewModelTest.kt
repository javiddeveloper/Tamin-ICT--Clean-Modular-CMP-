package com.tamin.taminhamrah.feature.taminServices.inspection

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.InspectionViewModel
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.contract.InspectionEvent
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.contract.InspectionIntent
import com.tamin.taminhamrah.model.inspection.BranchDN
import com.tamin.taminhamrah.model.inspection.BranchListDN
import com.tamin.taminhamrah.model.inspection.InspectionPerformedDN
import com.tamin.taminhamrah.model.inspection.InspectionPerformedListDN
import com.tamin.taminhamrah.model.inspection.JobDN
import com.tamin.taminhamrah.model.inspection.JobListDN
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestDN
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestResultDN
import com.tamin.taminhamrah.feature.taminServices.inspection.FakeInspectionRepository
import com.tamin.taminhamrah.useCases.inspection.GetBranchListUseCase
import com.tamin.taminhamrah.useCases.inspection.GetInspectionListUseCase
import com.tamin.taminhamrah.useCases.inspection.GetJobListUseCase
import com.tamin.taminhamrah.useCases.inspection.SubmitInspectionUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class InspectionViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: FakeInspectionRepository
    private lateinit var viewModel: InspectionViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeInspectionRepository()
        viewModel = buildViewModel()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = InspectionViewModel(
        getInspectionListUseCase = GetInspectionListUseCase(repository),
        getBranchListUseCase = GetBranchListUseCase(repository),
        getJobListUseCase = GetJobListUseCase(repository),
        submitInspectionUseCase = SubmitInspectionUseCase(repository)
    )

    @Test
    fun LoadInspections_success_updatesUiStateWithList() = runTest(testDispatcher) {
        val expected = InspectionPerformedListDN(
            total = 1,
            list = listOf(
                InspectionPerformedDN(
                    activityDesc = "تست", branchCode = "0010", branchdesc = "شعبه",
                    inspectionDate = 0L, inspectionNo = "", insuranceNo = "",
                    objectable = "", relationType = "", workshopName = "",
                    workshopNo = "", nationalCode = ""
                )
            )
        )
        repository.allInsuranceResult = expected

        viewModel.uiState.test {
            awaitItem() // initial
            viewModel.sendIntent(InspectionIntent.LoadInspections())

            var state = awaitItem()
            while (state.isLoading) {
                state = awaitItem()
            }

            assertEquals(1, state.inspections.size)
            assertEquals("تست", state.inspections.first().activityDesc)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun LoadInspections_error_sendsShowToastEvent() = runTest(testDispatcher) {
        repository.shouldThrowError = true

        viewModel.events.test {
            viewModel.sendIntent(InspectionIntent.LoadInspections())
            assertIs<InspectionEvent.ShowToast>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun LoadBranches_success_updatesUiStateWithList() = runTest(testDispatcher) {
        val expected = BranchListDN(
            total = 1,
            list = listOf(
                BranchDN(
                    operation = "", code = "0010", name = "یک تهران",
                    minCode = "", maxCode = "", type = "", branchAddress = "",
                    cityCode = "", status = ""
                )
            )
        )
        repository.branchesResult = expected

        viewModel.uiState.test {
            awaitItem() // initial
            viewModel.sendIntent(InspectionIntent.LoadBranches())

            var state = awaitItem()
            while (state.isLoading) {
                state = awaitItem()
            }

            assertEquals(1, state.branches.size)
            assertEquals("یک تهران", state.branches.first().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun LoadJobs_success_updatesUiStateWithList() = runTest(testDispatcher) {
        val expected = JobListDN(
            total = 1,
            list = listOf(
                JobDN(
                    operation = "", jobCode = "2035", jobDescription = "قرص ساز",
                    status = "", statusDate = ""
                )
            )
        )
        repository.jobsResult = expected

        viewModel.uiState.test {
            awaitItem() // initial
            viewModel.sendIntent(InspectionIntent.LoadJobs())

            var state = awaitItem()
            while (state.isLoading) {
                state = awaitItem()
            }

            assertEquals(1, state.jobs.size)
            assertEquals("قرص ساز", state.jobs.first().jobDescription)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun SubmitRequest_success_updatesUiStateIsSubmitted() = runTest(testDispatcher) {
        repository.submitResult = SubmitInspectionRequestResultDN(id = 123L)

        viewModel.uiState.test {
            awaitItem() // initial
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
}
