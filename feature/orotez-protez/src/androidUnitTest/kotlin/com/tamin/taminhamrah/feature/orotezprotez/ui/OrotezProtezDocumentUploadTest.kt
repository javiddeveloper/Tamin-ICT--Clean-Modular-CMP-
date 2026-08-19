package com.tamin.taminhamrah.feature.orotezprotez.ui

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.orotezprotez.fake.FakeContractsRepository
import com.tamin.taminhamrah.feature.orotezprotez.fake.FakeOrotezProtezRepository
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezDocumentChecklist
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezDocumentState
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezEvent
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezIntent
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezOptionUi
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezUiState
import com.tamin.taminhamrah.model.orotezProtez.BranchWorkshopDN
import com.tamin.taminhamrah.model.orotezProtez.InsuredPersonDN
import com.tamin.taminhamrah.model.orotezProtez.RequestInsuredMainInfoDN
import com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase
import com.tamin.taminhamrah.useCases.orotezProtez.GetInsuredPersonsUseCase
import com.tamin.taminhamrah.useCases.orotezProtez.GetRequestInsuredMainInfoUseCase
import com.tamin.taminhamrah.useCases.orotezProtez.SaveShortTermOrthosisUseCase
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
 * Covers picking a document image, the upload result it lands in, and the document validation /
 * submit-gate rules from review round 1 of MR !181 (JPEG-only, 2MB max, duplicate rejection,
 * "both required docs, min 2 total"). Needs a real [PlatformFile] backed by a JVM [File], so it
 * can't live in commonTest.
 *
 * Several of these paths call `getString(Res.string...)`, which needs a real Android context to
 * resolve Compose Multiplatform resources — plain JVM unit tests don't have one (this repo has no
 * Robolectric elsewhere), and that call hangs indefinitely rather than throwing. Confirmed with a
 * 15s-timeout probe against this exact call before adding Robolectric here. [RobolectricTestRunner]
 * provides the context this feature's validation/rejection messages need.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class OrotezProtezDocumentUploadTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var orotezProtezRepository: FakeOrotezProtezRepository
    private lateinit var contractsRepository: FakeContractsRepository
    private lateinit var viewModel: OrotezProtezViewModel

    private val prescriptionId = OrotezProtezDocumentChecklist[0].id
    private val invoiceId = OrotezProtezDocumentChecklist[1].id
    private val earMoldId = OrotezProtezDocumentChecklist[2].id

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        orotezProtezRepository = FakeOrotezProtezRepository().apply {
            mainInfoResult = RequestInsuredMainInfoDN(
                risuid = "risuid-1",
                nationalCode = "0012345678",
                firstName = "علی",
                lastName = "رضایی",
                mobileNumber = "09120000000",
                genderCode = "M",
                branchCode = "10",
                branchName = "شعبه مرکزی",
                bankAccount = null,
                bankName = null,
                insuranceTypeDesc = null,
                insuranceStatusDesc = null,
                branchWorkshops = listOf(
                    BranchWorkshopDN(
                        branchCode = "10",
                        branchName = "شعبه مرکزی",
                        workshopCode = "20",
                        workshopName = "کارگاه اصلی",
                    ),
                ),
            )
            insuredPersonsResult = listOf(
                InsuredPersonDN(
                    insuredId = "insured-1",
                    firstName = "علی",
                    lastName = "رضایی",
                    nationalCode = "0012345678",
                    birthCertificateNumber = "1",
                    cityName = "تهران",
                    birthDate = null,
                    relationship = "بیمه شده اصلی",
                    relationshipCode = "01",
                    bookletValidUntil = null,
                ),
            )
        }
        contractsRepository = FakeContractsRepository()
        viewModel = OrotezProtezViewModel(
            getRequestInsuredMainInfoUseCase = GetRequestInsuredMainInfoUseCase(orotezProtezRepository),
            getInsuredPersonsUseCase = GetInsuredPersonsUseCase(orotezProtezRepository),
            uploadImageUseCase = UploadImageUseCase(contractsRepository),
            saveShortTermOrthosisUseCase = SaveShortTermOrthosisUseCase(orotezProtezRepository),
        )
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun documentPicked_withValidJpeg_isUploadedAndKeepsTheReturnedGuid() = runTest(testDispatcher) {
        awaitState { it.branchOptions.isNotEmpty() }

        viewModel.sendIntent(OrotezProtezIntent.OnDocumentImagePicked(prescriptionId, jpegFile()))

        val state = awaitState { it.documents[prescriptionId] is OrotezProtezDocumentState.Uploaded }

        val uploaded = state.documents[prescriptionId] as OrotezProtezDocumentState.Uploaded
        assertEquals("uploaded-guid", uploaded.guid)
        assertEquals(setOf(prescriptionId), state.uploadedDocumentIds)
        assertNotNull(contractsRepository.lastUploadImageRequest)
    }

    @Test
    fun documentPicked_whenUploadFails_endsInFailedStateWithAMessage() = runTest(testDispatcher) {
        awaitState { it.branchOptions.isNotEmpty() }
        contractsRepository.shouldThrowOnUpload = true

        viewModel.sendIntent(OrotezProtezIntent.OnDocumentImagePicked(prescriptionId, jpegFile()))

        val state = awaitState { it.documents[prescriptionId] is OrotezProtezDocumentState.Failed }

        val failed = state.documents[prescriptionId] as OrotezProtezDocumentState.Failed
        assertTrue(failed.message.isNotBlank())
        assertTrue(state.uploadedDocumentIds.isEmpty())
    }

    @Test
    fun documentRemoved_clearsThePreviouslyUploadedDocument() = runTest(testDispatcher) {
        awaitState { it.branchOptions.isNotEmpty() }
        viewModel.sendIntent(OrotezProtezIntent.OnDocumentImagePicked(prescriptionId, jpegFile()))
        awaitState { it.documents[prescriptionId] is OrotezProtezDocumentState.Uploaded }

        viewModel.sendIntent(OrotezProtezIntent.OnDocumentRemoveClicked(prescriptionId))

        val state = awaitState { it.documents[prescriptionId] is OrotezProtezDocumentState.Empty }
        assertTrue(state.uploadedDocumentIds.isEmpty())
    }

    @Test
    fun documentPicked_withNonJpegExtension_isRejectedAndStaysEmpty() = runTest(testDispatcher) {
        awaitState { it.branchOptions.isNotEmpty() }

        viewModel.sendIntent(
            OrotezProtezIntent.OnDocumentImagePicked(prescriptionId, tempFile(".png", sizeBytes = 100)),
        )
        val state = awaitState { it.documentPickError != null }

        assertNotNull(state.documentPickError)
        assertTrue(state.documents[prescriptionId] !is OrotezProtezDocumentState.Uploaded)
    }

    @Test
    fun documentPicked_exceedingMaxSize_isRejectedAndStaysEmpty() = runTest(testDispatcher) {
        awaitState { it.branchOptions.isNotEmpty() }

        viewModel.sendIntent(
            OrotezProtezIntent.OnDocumentImagePicked(prescriptionId, tempFile(".jpg", sizeBytes = 2 * 1024 * 1024 + 1)),
        )
        val state = awaitState { it.documentPickError != null }

        assertNotNull(state.documentPickError)
        assertTrue(state.documents[prescriptionId] !is OrotezProtezDocumentState.Uploaded)
    }

    @Test
    fun documentPicked_duplicateOfAnotherDocument_isRejected() = runTest(testDispatcher) {
        awaitState { it.branchOptions.isNotEmpty() }
        val sharedBytes = byteArrayOf(1, 2, 3, 4, 5)

        viewModel.sendIntent(OrotezProtezIntent.OnDocumentImagePicked(prescriptionId, jpegFile(sharedBytes)))
        awaitState { it.documents[prescriptionId] is OrotezProtezDocumentState.Uploaded }

        viewModel.sendIntent(OrotezProtezIntent.OnDocumentImagePicked(invoiceId, jpegFile(sharedBytes)))
        val state = awaitState { it.documentPickError != null }

        assertNotNull(state.documentPickError)
        assertTrue(state.documents[invoiceId] !is OrotezProtezDocumentState.Uploaded)
    }

    @Test
    fun submitDocuments_withOneRequiredDocumentMissing_failsRequiredValidation() = runTest(testDispatcher) {
        awaitState { it.branchOptions.isNotEmpty() }
        advanceToDocumentsStep()
        awaitState { it.currentStep == OrotezProtezStep.Documents }

        viewModel.sendIntent(OrotezProtezIntent.OnDocumentImagePicked(prescriptionId, jpegFile(byteArrayOf(1))))
        awaitState { it.documents[prescriptionId] is OrotezProtezDocumentState.Uploaded }

        viewModel.sendIntent(OrotezProtezIntent.OnDocumentImagePicked(earMoldId, jpegFile(byteArrayOf(2))))
        awaitState { it.documents[earMoldId] is OrotezProtezDocumentState.Uploaded }

        viewModel.sendIntent(OrotezProtezIntent.OnSubmitDocumentsClicked)
        val state = awaitState { it.documentValidationError != null }

        assertNotNull(state.documentValidationError)
        assertFalse(state.hasSubmitted)
        assertFalse(state.isSubmitting)
    }

    @Test
    fun submitDocuments_withBothRequiredDocuments_succeedsAndAcknowledgeNavigatesBack() = runTest(testDispatcher) {
        awaitState { it.branchOptions.isNotEmpty() }
        advanceToDocumentsStep()
        awaitState { it.currentStep == OrotezProtezStep.Documents }

        viewModel.sendIntent(OrotezProtezIntent.OnDocumentImagePicked(prescriptionId, jpegFile(byteArrayOf(1))))
        awaitState { it.documents[prescriptionId] is OrotezProtezDocumentState.Uploaded }

        viewModel.sendIntent(OrotezProtezIntent.OnDocumentImagePicked(invoiceId, jpegFile(byteArrayOf(2))))
        awaitState { it.documents[invoiceId] is OrotezProtezDocumentState.Uploaded }

        viewModel.sendIntent(OrotezProtezIntent.OnSubmitDocumentsClicked)
        val state = awaitState { it.hasSubmitted }

        assertFalse(state.isSubmitting)
        assertEquals("درخواست شما با موفقیت ثبت شد.", state.submittedResultMessage)

        val request = orotezProtezRepository.lastSaveRequest
        assertNotNull(request)
        assertEquals("risuid-1", request.risuid)
        assertEquals("insured-1", request.userInsuredId)
        assertEquals(2, request.requestFileList.size)

        viewModel.events.test {
            viewModel.sendIntent(OrotezProtezIntent.OnSubmitSuccessAcknowledged)
            assertEquals(OrotezProtezEvent.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun advanceToDocumentsStep() {
        val insuredPerson = OrotezProtezOptionUi(id = "insured-1", label = "علی رضایی")
        viewModel.sendIntent(OrotezProtezIntent.OnInsuredPersonPicked(insuredPerson))
        viewModel.sendIntent(OrotezProtezIntent.OnPrescriptionDatePicked(millis = 1_000L, label = "1403/01/01"))
        viewModel.sendIntent(OrotezProtezIntent.OnNextStepClicked)
        viewModel.sendIntent(OrotezProtezIntent.OnConfirmInsuredInfoClicked)
    }

    /**
     * Reading and deleting a [PlatformFile] hops off the test dispatcher, so state changes land
     * asynchronously — wait on real time instead of the virtual test clock.
     */
    private suspend fun awaitState(predicate: (OrotezProtezUiState) -> Boolean): OrotezProtezUiState =
        withContext(Dispatchers.Default) {
            withTimeout(WAIT_TIMEOUT_MILLIS) { viewModel.uiState.first(predicate) }
        }

    private fun jpegFile(bytes: ByteArray = byteArrayOf(1, 2, 3)): PlatformFile = tempFile(".jpg", bytes)

    private fun tempFile(extension: String, sizeBytes: Int): PlatformFile =
        tempFile(extension, ByteArray(sizeBytes) { 1 })

    private fun tempFile(extension: String, bytes: ByteArray): PlatformFile {
        val file = File.createTempFile("orotez-protez-test", extension)
        file.writeBytes(bytes)
        file.deleteOnExit()
        return PlatformFile(file)
    }

    private companion object {
        const val WAIT_TIMEOUT_MILLIS = 5_000L
    }
}
