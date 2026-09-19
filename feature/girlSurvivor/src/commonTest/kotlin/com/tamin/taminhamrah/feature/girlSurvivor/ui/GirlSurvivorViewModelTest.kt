package com.tamin.taminhamrah.feature.girlSurvivor.ui

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.girlSurvivor.fake.FakePersonalRepository
import com.tamin.taminhamrah.feature.girlSurvivor.ui.contract.GirlSurvivorEvent
import com.tamin.taminhamrah.feature.girlSurvivor.ui.contract.GirlSurvivorIntent
import com.tamin.taminhamrah.feature.girlSurvivor.ui.contract.GirlSurvivorStep
import com.tamin.taminhamrah.useCases.personal.CheckGirlSurvivorConditionsUseCase
import com.tamin.taminhamrah.useCases.personal.ConfirmGirlSurvivorUseCase
import com.tamin.taminhamrah.useCases.personal.GetGirlSurvivorReportUseCase
import com.tamin.taminhamrah.useCases.personal.GetPersonalInfoUseCase
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Compose [org.jetbrains.compose.resources.getString] is unavailable in JVM unit tests,
 * so Init with a non-null [PersonalInfoDN] fails inside [GirlSurvivorViewModel] profile-row
 * building. These tests seed a null personal-info result and cover navigation / field logic
 * that does not depend on resolved string resources.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GirlSurvivorViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var personalRepository: FakePersonalRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        personalRepository = FakePersonalRepository().apply {
            personalInfoResult = null
        }
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): GirlSurvivorViewModel = GirlSurvivorViewModel(
        getPersonalInfoUseCase = GetPersonalInfoUseCase(personalRepository),
        checkGirlSurvivorConditionsUseCase = CheckGirlSurvivorConditionsUseCase(personalRepository),
        getGirlSurvivorReportUseCase = GetGirlSurvivorReportUseCase(personalRepository),
        confirmGirlSurvivorUseCase = ConfirmGirlSurvivorUseCase(personalRepository),
    )

    @Test
    fun init_startsOnDetailsStep() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(GirlSurvivorStep.Details, state.currentStep)
        assertEquals("", state.fullName)
        assertTrue(state.profileRows.isEmpty())
    }

    @Test
    fun zipCodeChanged_filtersDigitsAndCapsAtTen() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(GirlSurvivorIntent.ZipCodeChanged("123abc4567890123"))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("1234567890", viewModel.uiState.value.zipCode)
    }

    @Test
    fun phoneNumberChanged_filtersDigitsAndCapsAtEleven() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(GirlSurvivorIntent.PhoneNumberChanged("0912abc34567890123"))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("09123456789", viewModel.uiState.value.phoneNumber)
    }

    @Test
    fun usePensionIdModeChanged_clearsDeceasedIdentifiersAndErrors() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(GirlSurvivorIntent.DeceasedNationalCodeChanged("0012345678"))
        viewModel.sendIntent(GirlSurvivorIntent.UsePensionIdModeChanged(true))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.usePensionIdMode)
        assertEquals("", state.deceasedNationalCode)
        assertEquals("", state.deceasedPensionId)
        assertNull(state.fieldErrors.deceasedNationalCode)
        assertNull(state.fieldErrors.deceasedPensionId)
    }

    @Test
    fun goToPreviousStep_fromCommitment_returnsToDetails() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        // Advance to Commitment the same way the PDF viewer path does.
        viewModel.sendIntent(GirlSurvivorIntent.RetryPdfDownload)
        testDispatcher.scheduler.advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.viewerPdf)

        viewModel.sendIntent(GirlSurvivorIntent.DismissPdfViewer)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(GirlSurvivorStep.Commitment, viewModel.uiState.value.currentStep)

        viewModel.sendIntent(GirlSurvivorIntent.GoToPreviousStep)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(GirlSurvivorStep.Details, viewModel.uiState.value.currentStep)
    }

    @Test
    fun dismissSuccessDialog_hidesDialogAndEmitsNavigateBack() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(GirlSurvivorIntent.DismissSuccessDialog)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(GirlSurvivorEvent.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertFalse(viewModel.uiState.value.showSuccessDialog)
    }

    @Test
    fun pdfConfirmedChanged_updatesFlag() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(GirlSurvivorIntent.PdfConfirmedChanged(true))
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isPdfConfirmed)
    }

    @Test
    fun retryPdfDownload_whenReportFails_setsViewerDownloadFailed() = runTest(testDispatcher) {
        personalRepository.shouldThrowOnReport = true
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(GirlSurvivorIntent.RetryPdfDownload)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.viewerDownloadFailed)
        assertNull(state.viewerPdf)
        assertFalse(state.isLoading)
    }
}
