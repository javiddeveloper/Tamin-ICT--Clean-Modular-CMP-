package com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.wizard

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.fake.FakeIllDaysCityProvinceRepository
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.fake.FakeIllDaysContractsRepository
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.fake.FakeIllDaysRepository
import com.tamin.taminhamrah.mapper.common.toCityPresentation
import com.tamin.taminhamrah.mapper.requestPaymentForIllDays.toPresentation
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysBranchWorkshopDN
import com.tamin.taminhamrah.useCases.common.GetCitiesPageUseCase
import com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase
import com.tamin.taminhamrah.useCases.requestPaymentForIllDays.GetCovidResultUseCase
import com.tamin.taminhamrah.useCases.requestPaymentForIllDays.GetIllDaysInsuredMainInfoUseCase
import com.tamin.taminhamrah.useCases.requestPaymentForIllDays.SendRequestForIllDayUseCase
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
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class IllDaysWizardViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var illDaysRepository: FakeIllDaysRepository
    private lateinit var cityRepository: FakeIllDaysCityProvinceRepository
    private lateinit var contractsRepository: FakeIllDaysContractsRepository
    private lateinit var viewModel: IllDaysWizardViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        illDaysRepository = FakeIllDaysRepository()
        cityRepository = FakeIllDaysCityProvinceRepository()
        contractsRepository = FakeIllDaysContractsRepository()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun buildViewModel(): IllDaysWizardViewModel = IllDaysWizardViewModel(
        getIllDaysInsuredMainInfoUseCase = GetIllDaysInsuredMainInfoUseCase(illDaysRepository),
        getCitiesPageUseCase = GetCitiesPageUseCase(cityRepository),
        getCovidResultUseCase = GetCovidResultUseCase(illDaysRepository),
        uploadImageUseCase = UploadImageUseCase(contractsRepository),
        sendRequestForIllDayUseCase = SendRequestForIllDayUseCase(illDaysRepository),
    )

    @Test
    fun loadInitial_singleBranch_autoSelectsBranch() = runTest(testDispatcher) {
        viewModel = buildViewModel()

        viewModel.uiState.test {
            val state = awaitUntil { !it.isLoading }
            assertNotNull(state.selectedBranch)
            assertEquals(1, state.branchOptions.size)
            assertTrue(state.canGoNextFromStep1.not()) // city not selected yet
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun loadInitial_multipleBranches_doesNotAutoSelect() = runTest(testDispatcher) {
        illDaysRepository.branchWorkshops = listOf(
            IllDaysBranchWorkshopDN("0100", "Branch A", "W1", "Workshop 1"),
            IllDaysBranchWorkshopDN("0200", "Branch B", "W2", "Workshop 2"),
        )
        viewModel = buildViewModel()

        viewModel.uiState.test {
            val state = awaitUntil { !it.isLoading }
            assertNull(state.selectedBranch)
            assertEquals(2, state.branchOptions.size)
            assertFalse(state.canGoNextFromStep1)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun endDateBeforeStart_blocksStepTwoAdvance() = runTest(testDispatcher) {
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        fillStepOne()

        viewModel.sendIntent(IllDaysWizardIntent.StartDatePicked(millis = 2_000L, label = "start"))
        viewModel.sendIntent(IllDaysWizardIntent.EndDatePicked(millis = 1_000L, label = "end"))
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.canGoNextFromStep2)

        viewModel.sendIntent(IllDaysWizardIntent.NextStep)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(IllDaysWizardStep.RestDays, viewModel.uiState.value.currentStep)
    }

    @Test
    fun restDaysOver365_showsToastOnNext() = runTest(testDispatcher) {
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        fillStepOne()
        advanceToRestDaysStep()

        val start = 1_700_000_000_000L
        val end = start + 365 * MILLIS_PER_DAY
        viewModel.sendIntent(IllDaysWizardIntent.StartDatePicked(millis = start, label = "start"))
        viewModel.sendIntent(IllDaysWizardIntent.EndDatePicked(millis = end, label = "end"))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(366, viewModel.uiState.value.dayCount)

        viewModel.events.test {
            viewModel.sendIntent(IllDaysWizardIntent.NextStep)
            assertIs<IllDaysWizardEvent.ShowToast>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(IllDaysWizardStep.RestDays, viewModel.uiState.value.currentStep)
    }

    @Test
    fun covidToggleOn_fetchesDatesFromServer() = runTest(testDispatcher) {
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(IllDaysWizardIntent.CovidChanged(enabled = true))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isCovid)
        assertNotNull(state.startDateMillis)
        assertNotNull(state.endDateMillis)
        assertNotNull(state.dayCount)
    }

    @Test
    fun covidToggleOff_clearsDates() = runTest(testDispatcher) {
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(IllDaysWizardIntent.CovidChanged(enabled = true))
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.sendIntent(IllDaysWizardIntent.CovidChanged(enabled = false))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isCovid)
        assertNull(state.startDateMillis)
        assertNull(state.endDateMillis)
        assertNull(state.dayCount)
    }

    @Test
    fun documentUpload_rejectsNonJpeg() = runTest(testDispatcher) {
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(
                IllDaysWizardIntent.DocumentImagePicked(
                    fileName = "scan.png",
                    bytes = byteArrayOf(1, 2, 3),
                )
            )
            assertIs<IllDaysWizardEvent.ShowToast>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertTrue(viewModel.uiState.value.documents.isEmpty())
    }

    @Test
    fun doctorCode_filtersNonDigits() = runTest(testDispatcher) {
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(IllDaysWizardIntent.DoctorCodeChanged("12ab34"))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("1234", viewModel.uiState.value.doctorCode)
    }

    private fun fillStepOne() {
        val city = cityRepository.cities.toCityPresentation().first()
        val branch = viewModel.uiState.value.branchOptions.firstOrNull()
            ?: illDaysRepository.branchWorkshops.first().toPresentation()
        viewModel.sendIntent(IllDaysWizardIntent.BranchPicked(branch))
        viewModel.sendIntent(IllDaysWizardIntent.CityPicked(city))
    }

    private fun advanceToRestDaysStep() {
        viewModel.sendIntent(IllDaysWizardIntent.NextStep)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(IllDaysWizardStep.RestDays, viewModel.uiState.value.currentStep)
    }

    private fun fillValidRestDates() {
        val start = 1_700_000_000_000L
        val end = start + MILLIS_PER_DAY
        viewModel.sendIntent(IllDaysWizardIntent.StartDatePicked(millis = start, label = "start"))
        viewModel.sendIntent(IllDaysWizardIntent.EndDatePicked(millis = end, label = "end"))
        testDispatcher.scheduler.advanceUntilIdle()
    }

    private suspend fun ReceiveTurbine<IllDaysWizardUiState>.awaitUntil(
        predicate: (IllDaysWizardUiState) -> Boolean,
    ): IllDaysWizardUiState {
        var state = awaitItem()
        while (!predicate(state)) {
            state = awaitItem()
        }
        return state
    }

    private companion object {
        const val MILLIS_PER_DAY = 86_400_000L
    }
}
