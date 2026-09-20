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
import org.jetbrains.compose.resources.StringResource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Compose [org.jetbrains.compose.resources.getString] is unavailable in JVM unit tests.
 * Tests inject a stub [resolveString] so profile-load, validation, and submit can run.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GirlSurvivorViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var personalRepository: FakePersonalRepository
    private val stubResolveString: suspend (StringResource) -> String = { "stub" }

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

    private fun createViewModel(
        withProfile: Boolean = false,
    ): GirlSurvivorViewModel {
        if (withProfile) {
            personalRepository.personalInfoResult = FakePersonalRepository.samplePersonalInfo()
        }
        return GirlSurvivorViewModel(
            getPersonalInfoUseCase = GetPersonalInfoUseCase(personalRepository),
            checkGirlSurvivorConditionsUseCase = CheckGirlSurvivorConditionsUseCase(personalRepository),
            getGirlSurvivorReportUseCase = GetGirlSurvivorReportUseCase(personalRepository),
            confirmGirlSurvivorUseCase = ConfirmGirlSurvivorUseCase(personalRepository),
            resolveString = stubResolveString,
        )
    }

    private fun GirlSurvivorViewModel.fillValidDetails(
        usePensionId: Boolean = false,
    ) {
        sendIntent(GirlSurvivorIntent.AddressChanged("تهران خیابان آزادی"))
        sendIntent(GirlSurvivorIntent.ZipCodeChanged("1234567890"))
        sendIntent(GirlSurvivorIntent.PhoneNumberChanged("09123456789"))
        if (usePensionId) {
            sendIntent(GirlSurvivorIntent.UsePensionIdModeChanged(true))
            sendIntent(GirlSurvivorIntent.DeceasedPensionIdChanged("1234567890"))
        } else {
            sendIntent(GirlSurvivorIntent.DeceasedNationalCodeChanged("0012345678"))
        }
    }

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
    fun init_withPersonalInfo_loadsProfileAndConfirmPayload() = runTest(testDispatcher) {
        val viewModel = createViewModel(withProfile = true)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("زهرا احمدی", state.fullName)
        assertEquals("علی", state.fatherName)
        assertEquals("0012345678", state.nationalId)
        assertEquals("09123456789", state.phoneNumber)
        assertTrue(state.profileRows.isNotEmpty())
        assertNotNull(state.confirmPayload)
        assertFalse(state.isProfileLoading)
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
    fun downloadAndViewForm_blankAddress_setsAddressError() = runTest(testDispatcher) {
        val viewModel = createViewModel(withProfile = true)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(GirlSurvivorIntent.DownloadAndViewForm)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.fieldErrors.address)
        assertNull(viewModel.uiState.value.viewerPdf)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun downloadAndViewForm_invalidZipCode_setsZipCodeError() = runTest(testDispatcher) {
        val viewModel = createViewModel(withProfile = true)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(GirlSurvivorIntent.AddressChanged("تهران"))
        viewModel.sendIntent(GirlSurvivorIntent.ZipCodeChanged("12345"))
        viewModel.sendIntent(GirlSurvivorIntent.DownloadAndViewForm)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.fieldErrors.zipCode)
        assertNull(viewModel.uiState.value.viewerPdf)
    }

    @Test
    fun downloadAndViewForm_invalidPhone_setsPhoneError() = runTest(testDispatcher) {
        val viewModel = createViewModel(withProfile = true)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(GirlSurvivorIntent.AddressChanged("تهران"))
        viewModel.sendIntent(GirlSurvivorIntent.ZipCodeChanged("1234567890"))
        viewModel.sendIntent(GirlSurvivorIntent.PhoneNumberChanged("9123456789"))
        viewModel.sendIntent(GirlSurvivorIntent.DownloadAndViewForm)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.fieldErrors.phoneNumber)
        assertNull(viewModel.uiState.value.viewerPdf)
    }

    @Test
    fun downloadAndViewForm_blankNationalCode_setsNationalCodeError() = runTest(testDispatcher) {
        val viewModel = createViewModel(withProfile = true)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(GirlSurvivorIntent.AddressChanged("تهران"))
        viewModel.sendIntent(GirlSurvivorIntent.ZipCodeChanged("1234567890"))
        viewModel.sendIntent(GirlSurvivorIntent.PhoneNumberChanged("09123456789"))
        viewModel.sendIntent(GirlSurvivorIntent.DownloadAndViewForm)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.fieldErrors.deceasedNationalCode)
        assertNull(viewModel.uiState.value.viewerPdf)
    }

    @Test
    fun downloadAndViewForm_invalidPensionId_setsPensionIdError() = runTest(testDispatcher) {
        val viewModel = createViewModel(withProfile = true)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(GirlSurvivorIntent.AddressChanged("تهران"))
        viewModel.sendIntent(GirlSurvivorIntent.ZipCodeChanged("1234567890"))
        viewModel.sendIntent(GirlSurvivorIntent.PhoneNumberChanged("09123456789"))
        viewModel.sendIntent(GirlSurvivorIntent.UsePensionIdModeChanged(true))
        viewModel.sendIntent(GirlSurvivorIntent.DeceasedPensionIdChanged("123"))
        viewModel.sendIntent(GirlSurvivorIntent.DownloadAndViewForm)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.fieldErrors.deceasedPensionId)
        assertNull(viewModel.uiState.value.viewerPdf)
    }

    @Test
    fun downloadAndViewForm_validInput_opensPdfViewer() = runTest(testDispatcher) {
        val viewModel = createViewModel(withProfile = true)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.fillValidDetails()

        viewModel.events.test {
            viewModel.sendIntent(GirlSurvivorIntent.DownloadAndViewForm)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(GirlSurvivorEvent.OpenPdfViewer, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertNotNull(viewModel.uiState.value.viewerPdf)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun goToPreviousStep_fromCommitment_returnsToDetails() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

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

    @Test
    fun submitRequest_whenNotConfirmed_doesNothing() = runTest(testDispatcher) {
        val viewModel = createViewModel(withProfile = true)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.fillValidDetails()

        viewModel.sendIntent(GirlSurvivorIntent.SubmitRequest)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.showSuccessDialog)
        assertFalse(viewModel.uiState.value.isSubmitting)
        assertNull(personalRepository.lastConfirmBody)
    }

    @Test
    fun submitRequest_whenConfirmed_showsSuccessDialog() = runTest(testDispatcher) {
        val viewModel = createViewModel(withProfile = true)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.fillValidDetails()
        viewModel.sendIntent(GirlSurvivorIntent.PdfConfirmedChanged(true))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(GirlSurvivorIntent.SubmitRequest)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.showSuccessDialog)
        assertFalse(viewModel.uiState.value.isSubmitting)
        assertNotNull(personalRepository.lastConfirmBody)
        assertEquals("تهران خیابان آزادی", personalRepository.lastConfirmBody?.address)
        assertEquals("09123456789", personalRepository.lastConfirmBody?.phoneNumber)
        assertEquals("0012345678", personalRepository.lastConfirmBody?.nationalCode)
    }

    @Test
    fun submitRequest_whenConfirmFails_clearsSubmitting() = runTest(testDispatcher) {
        personalRepository.shouldThrowOnConfirm = true
        val viewModel = createViewModel(withProfile = true)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.fillValidDetails()
        viewModel.sendIntent(GirlSurvivorIntent.PdfConfirmedChanged(true))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(GirlSurvivorIntent.SubmitRequest)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.showSuccessDialog)
        assertFalse(viewModel.uiState.value.isSubmitting)
    }
}
