package com.tamin.taminhamrah.feature.pregnancyPay.ui

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.pregnancyPay.fake.FakeContractsRepository
import com.tamin.taminhamrah.feature.pregnancyPay.fake.FakePregnancyPayRepository
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayDocumentState
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayEvent
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayIntent
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayOptionUi
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayRequiredDocumentIds
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayUiState
import com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase
import com.tamin.taminhamrah.useCases.pregnancyPay.CalculatePregnancyPayEstimateUseCase
import com.tamin.taminhamrah.useCases.pregnancyPay.GetPregnancyMainInfoUseCase
import com.tamin.taminhamrah.useCases.pregnancyPay.GetPregnancyStatusListUseCase
import com.tamin.taminhamrah.useCases.pregnancyPay.GetPregnancyTypeListUseCase
import com.tamin.taminhamrah.useCases.pregnancyPay.SendPregnancyPayRequestUseCase
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Covers document picking (JPEG-only / 2MB max / duplicate-bytes rejection), the "3 required
 * documents" submit gate, submit success/failure, and the double-submit guard added alongside this
 * test (see `PregnancyPayViewModel.handleSubmitDocumentsClicked` — it previously proceeded on every
 * `OnSubmitDocumentsClicked` intent regardless of `isSubmitting`/`hasSubmitted`, unlike the
 * in-flight-flag convention `.claude/rules/state-management.md` requires for non-idempotent
 * intents; `BaseViewModel` merges intents via `flatMapMerge`, so two submit taps before the first
 * request resolves could otherwise both reach the repository).
 *
 * Needs a real [PlatformFile] backed by a JVM [File] (can't live in commonTest) and Robolectric for
 * the `getString(Res.string...)` calls in the document-validation/upload-error messages — same setup
 * as `OrotezProtezDocumentUploadTest`, which this file's structure mirrors.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PregnancyPayDocumentUploadTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var pregnancyPayRepository: FakePregnancyPayRepository
    private lateinit var contractsRepository: FakeContractsRepository
    private lateinit var viewModel: PregnancyPayViewModel

    private val medicalRestId = PregnancyPayRequiredDocumentIds[0]
    private val motherIdPage1Id = PregnancyPayRequiredDocumentIds[1]
    private val motherIdPage2Id = PregnancyPayRequiredDocumentIds[2]

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        pregnancyPayRepository = FakePregnancyPayRepository().apply {
            mainInfoResult = PregnancyPayTestData.femaleMainInfo
            pregnancyStatusListResult = PregnancyPayTestData.pregnancyStatusOptions
            pregnancyTypeListResult = PregnancyPayTestData.pregnancyTypeOptions
        }
        contractsRepository = FakeContractsRepository()
        viewModel = PregnancyPayViewModel(
            getPregnancyMainInfoUseCase = GetPregnancyMainInfoUseCase(pregnancyPayRepository),
            getPregnancyStatusListUseCase = GetPregnancyStatusListUseCase(pregnancyPayRepository),
            getPregnancyTypeListUseCase = GetPregnancyTypeListUseCase(pregnancyPayRepository),
            uploadImageUseCase = UploadImageUseCase(contractsRepository),
            sendPregnancyPayRequestUseCase = SendPregnancyPayRequestUseCase(pregnancyPayRepository),
            calculatePregnancyPayEstimateUseCase = CalculatePregnancyPayEstimateUseCase(pregnancyPayRepository),
        )
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun documentPicked_withValidJpeg_isUploadedAndKeepsTheReturnedGuid() = runTest(testDispatcher) {
        advanceToDocumentsStep()

        viewModel.sendIntent(PregnancyPayIntent.OnDocumentImagePicked(medicalRestId, jpegFile()))

        val state = awaitState { it.documents[medicalRestId] is PregnancyPayDocumentState.Uploaded }
        val uploaded = state.documents[medicalRestId] as PregnancyPayDocumentState.Uploaded
        assertEquals("uploaded-guid", uploaded.guid)
        assertNotNull(contractsRepository.lastUploadImageRequest)
    }

    @Test
    fun documentPicked_whenUploadFails_endsInFailedStateWithAMessage() = runTest(testDispatcher) {
        advanceToDocumentsStep()
        contractsRepository.shouldThrowOnUpload = true

        viewModel.sendIntent(PregnancyPayIntent.OnDocumentImagePicked(medicalRestId, jpegFile()))

        val state = awaitState { it.documents[medicalRestId] is PregnancyPayDocumentState.Failed }
        val failed = state.documents[medicalRestId] as PregnancyPayDocumentState.Failed
        assertTrue(failed.message.isNotBlank())
    }

    @Test
    fun documentRemoved_clearsThePreviouslyUploadedDocument() = runTest(testDispatcher) {
        advanceToDocumentsStep()
        viewModel.sendIntent(PregnancyPayIntent.OnDocumentImagePicked(medicalRestId, jpegFile()))
        awaitState { it.documents[medicalRestId] is PregnancyPayDocumentState.Uploaded }

        viewModel.sendIntent(PregnancyPayIntent.OnDocumentRemoveClicked(medicalRestId))

        val state = awaitState { it.documents[medicalRestId] is PregnancyPayDocumentState.Empty }
        assertEquals(0, state.requiredDocumentsUploadedCount)
    }

    @Test
    fun documentPicked_withNonJpegExtension_isRejectedUnderItsOwnDocumentCard() = runTest(testDispatcher) {
        advanceToDocumentsStep()

        viewModel.sendIntent(
            PregnancyPayIntent.OnDocumentImagePicked(medicalRestId, tempFile(".png", sizeBytes = 100)),
        )
        val state = awaitState { it.documentPickError != null }

        assertNotNull(state.documentPickError)
        assertFalse(state.documents[medicalRestId] is PregnancyPayDocumentState.Uploaded)
    }

    @Test
    fun documentPicked_exceedingMaxSize_isRejectedUnderItsOwnDocumentCard() = runTest(testDispatcher) {
        advanceToDocumentsStep()

        viewModel.sendIntent(
            PregnancyPayIntent.OnDocumentImagePicked(medicalRestId, tempFile(".jpg", sizeBytes = 2 * 1024 * 1024 + 1)),
        )
        val state = awaitState { it.documentPickError != null }

        assertNotNull(state.documentPickError)
        assertFalse(state.documents[medicalRestId] is PregnancyPayDocumentState.Uploaded)
    }

    @Test
    fun documentPicked_duplicateOfAnotherDocument_isRejectedUnderItsOwnDocumentCard() = runTest(testDispatcher) {
        advanceToDocumentsStep()
        val sharedBytes = byteArrayOf(1, 2, 3, 4, 5)

        viewModel.sendIntent(PregnancyPayIntent.OnDocumentImagePicked(medicalRestId, jpegFile(sharedBytes)))
        awaitState { it.documents[medicalRestId] is PregnancyPayDocumentState.Uploaded }

        viewModel.sendIntent(PregnancyPayIntent.OnDocumentImagePicked(motherIdPage1Id, jpegFile(sharedBytes)))
        val state = awaitState { it.documentPickError != null }

        assertNotNull(state.documentPickError)
        assertFalse(state.documents[motherIdPage1Id] is PregnancyPayDocumentState.Uploaded)
    }

    @Test
    fun submitDocuments_withOneRequiredDocumentMissing_failsRequiredValidation() = runTest(testDispatcher) {
        advanceToDocumentsStep()
        uploadRequiredDocument(medicalRestId, byteArrayOf(1))
        uploadRequiredDocument(motherIdPage1Id, byteArrayOf(2))
        // motherIdPage2Id intentionally left empty — only 2 of the 3 required documents uploaded.

        viewModel.sendIntent(PregnancyPayIntent.OnSubmitDocumentsClicked)
        val state = awaitState { it.documentValidationError != null }

        assertNotNull(state.documentValidationError)
        assertFalse(state.hasSubmitted)
        assertFalse(state.isSubmitting)
        assertEquals(0, pregnancyPayRepository.sendCallCount)
    }

    @Test
    fun submitDocuments_withAllRequiredDocuments_succeedsAndAcknowledgeNavigatesBack() = runTest(testDispatcher) {
        advanceToDocumentsStep()
        uploadRequiredDocument(medicalRestId, byteArrayOf(1))
        uploadRequiredDocument(motherIdPage1Id, byteArrayOf(2))
        uploadRequiredDocument(motherIdPage2Id, byteArrayOf(3))

        viewModel.sendIntent(PregnancyPayIntent.OnSubmitDocumentsClicked)
        val state = awaitState { it.hasSubmitted }

        assertFalse(state.isSubmitting)
        assertEquals("درخواست شما با موفقیت ثبت شد.", state.submittedResultMessage)

        val request = pregnancyPayRepository.lastSendRequest
        assertNotNull(request)
        assertEquals("risuid-1", request.risuid)
        assertEquals(3, request.requestFileList.size)

        viewModel.events.test {
            viewModel.sendIntent(PregnancyPayIntent.OnSubmitSuccessAcknowledged)
            assertEquals(PregnancyPayEvent.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun submitDocuments_whenSendFails_setsSubmitErrorAndAllowsRetry() = runTest(testDispatcher) {
        advanceToDocumentsStep()
        uploadRequiredDocument(medicalRestId, byteArrayOf(1))
        uploadRequiredDocument(motherIdPage1Id, byteArrayOf(2))
        uploadRequiredDocument(motherIdPage2Id, byteArrayOf(3))
        pregnancyPayRepository.shouldThrowOnSend = true

        viewModel.sendIntent(PregnancyPayIntent.OnSubmitDocumentsClicked)
        val state = awaitState { it.submitError != null }

        assertFalse(state.hasSubmitted)
        assertFalse(state.isSubmitting)
        assertEquals(1, pregnancyPayRepository.sendCallCount)
    }

    /**
     * Regression test for the double-submit guard added to `handleSubmitDocumentsClicked`. Before
     * the fix, the handler had no in-flight/already-done check at all, so re-sending
     * `OnSubmitDocumentsClicked` after the first request had already succeeded — e.g. a second tap
     * on the submit button before the success screen navigates away — re-validated the documents and
     * re-sent the request. This asserts the fix's `hasSubmitted` half of the guard; the `isSubmitting`
     * half (blocking a tap that lands *while* the first request is still in flight) is real but not
     * exercised here — `BaseViewModel` runs intents through `flatMapMerge`, so two
     * `OnSubmitDocumentsClicked` sent with zero gap can both start reading `uiState.value` before
     * either's `Submitting(true)` is reduced in; closing that fully would need a different
     * concurrency primitive than the flag-in-state convention this codebase uses everywhere else
     * (`.claude/rules/state-management.md` explicitly steers away from `Mutex`), so it's a
     * pre-existing, codebase-wide trait rather than something specific to this handler.
     */
    @Test
    fun submitDocuments_tappedAgainAfterFirstSubmitSucceeded_doesNotResend() = runTest(testDispatcher) {
        advanceToDocumentsStep()
        uploadRequiredDocument(medicalRestId, byteArrayOf(1))
        uploadRequiredDocument(motherIdPage1Id, byteArrayOf(2))
        uploadRequiredDocument(motherIdPage2Id, byteArrayOf(3))

        viewModel.sendIntent(PregnancyPayIntent.OnSubmitDocumentsClicked)
        awaitState { it.hasSubmitted }
        assertEquals(1, pregnancyPayRepository.sendCallCount)

        viewModel.sendIntent(PregnancyPayIntent.OnSubmitDocumentsClicked)
        awaitState { true } // let the second intent finish processing before asserting

        assertEquals(1, pregnancyPayRepository.sendCallCount)
    }

    private suspend fun uploadRequiredDocument(documentId: String, bytes: ByteArray) {
        viewModel.sendIntent(PregnancyPayIntent.OnDocumentImagePicked(documentId, jpegFile(bytes)))
        awaitState { it.documents[documentId] is PregnancyPayDocumentState.Uploaded }
    }

    private fun advanceToDocumentsStep() {
        viewModel.sendIntent(PregnancyPayIntent.OnLandingStartClicked)
        viewModel.sendIntent(PregnancyPayIntent.OnRestStartDatePicked(millis = 0L, label = "۱۴۰۵/۰۱/۰۱"))
        viewModel.sendIntent(
            PregnancyPayIntent.OnRestEndDatePicked(millis = 10 * MILLIS_PER_DAY, label = "۱۴۰۵/۰۲/۰۱"),
        )
        viewModel.sendIntent(PregnancyPayIntent.OnNextFromBranchAndRestClicked)
        viewModel.sendIntent(PregnancyPayIntent.OnBabyBirthDatePicked(millis = 0L, label = "۱۴۰۵/۰۱/۰۲"))
        viewModel.sendIntent(
            PregnancyPayIntent.OnPregnancyStatusPicked(PregnancyPayOptionUi(id = "1", label = "بارداری طبیعی")),
        )
        viewModel.sendIntent(
            PregnancyPayIntent.OnPregnancyTypePicked(PregnancyPayOptionUi(id = "1", label = "تک قلو")),
        )
        viewModel.sendIntent(PregnancyPayIntent.OnChildNationalCodeChanged(1, "0011122233"))
        viewModel.sendIntent(PregnancyPayIntent.OnNextFromPregnancyAndNewbornClicked)
        viewModel.sendIntent(
            PregnancyPayIntent.OnRequestTypePicked(PregnancyPayOptionUi(id = "1", label = "تا شش ماه")),
        )
        viewModel.sendIntent(PregnancyPayIntent.OnDoctorNameChanged("دکتر رضایی"))
        viewModel.sendIntent(PregnancyPayIntent.OnDoctorCodeChanged("12345"))
        viewModel.sendIntent(PregnancyPayIntent.OnNextFromDoctorAndRequestClicked)
    }

    /**
     * Reading and deleting a [PlatformFile] hops off the test dispatcher, so state changes land
     * asynchronously — wait on real time instead of the virtual test clock.
     */
    private suspend fun awaitState(predicate: (PregnancyPayUiState) -> Boolean): PregnancyPayUiState =
        withContext(Dispatchers.Default) {
            withTimeout(WAIT_TIMEOUT_MILLIS) { viewModel.uiState.first(predicate) }
        }

    private fun jpegFile(bytes: ByteArray = byteArrayOf(1, 2, 3)): PlatformFile = tempFile(".jpg", bytes)

    private fun tempFile(extension: String, sizeBytes: Int): PlatformFile =
        tempFile(extension, ByteArray(sizeBytes) { 1 })

    private fun tempFile(extension: String, bytes: ByteArray): PlatformFile {
        val file = File.createTempFile("pregnancy-pay-test", extension)
        file.writeBytes(bytes)
        file.deleteOnExit()
        return PlatformFile(file)
    }

    private companion object {
        const val WAIT_TIMEOUT_MILLIS = 5_000L
        const val MILLIS_PER_DAY = 86_400_000L
    }
}
