package com.tamin.taminhamrah.feature.orotezprotez.ui

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.orotezprotez.fake.FakeContractsRepository
import com.tamin.taminhamrah.feature.orotezprotez.fake.FakeOrotezProtezRepository
import com.tamin.taminhamrah.feature.orotezprotez.test.FakeComposeResourceEnvironment
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
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Covers the document validation and submit-gate rules described in review round 1 of MR !181
 * (JPEG-only, 2MB max, duplicate rejection, "both required docs, min 2 total"), which need a real
 * [PlatformFile] backed by a JVM [File] and so can't live in commonTest.
 */
@OptIn(ExperimentalCoroutinesApi::class)
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
        FakeComposeResourceEnvironment.install()
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
            saveResult = "درخواست شما با موفقیت ثبت شد."
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
    fun documentPicked_withNonJpegExtension_isRejectedAndStaysEmpty() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitUntil { it.branchOptions.isNotEmpty() }

            viewModel.sendIntent(
                OrotezProtezIntent.OnDocumentImagePicked(prescriptionId, tempFile(".png", sizeBytes = 100)),
            )
            val state = awaitUntil { it.documentPickError != null }

            assertNotNull(state.documentPickError)
            assertTrue(state.documents[prescriptionId] !is OrotezProtezDocumentState.Uploaded)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun documentPicked_exceedingMaxSize_isRejectedAndStaysEmpty() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitUntil { it.branchOptions.isNotEmpty() }

            viewModel.sendIntent(
                OrotezProtezIntent.OnDocumentImagePicked(
                    prescriptionId,
                    tempFile(".jpg", sizeBytes = 2 * 1024 * 1024 + 1),
                ),
            )
            val state = awaitUntil { it.documentPickError != null }

            assertNotNull(state.documentPickError)
            assertTrue(state.documents[prescriptionId] !is OrotezProtezDocumentState.Uploaded)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun documentPicked_duplicateOfAnotherDocument_isRejected() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitUntil { it.branchOptions.isNotEmpty() }
            val sharedBytes = byteArrayOf(1, 2, 3, 4, 5)

            viewModel.sendIntent(
                OrotezProtezIntent.OnDocumentImagePicked(prescriptionId, tempFile(".jpg", bytes = sharedBytes)),
            )
            awaitUntil { it.documents[prescriptionId] is OrotezProtezDocumentState.Uploaded }

            viewModel.sendIntent(
                OrotezProtezIntent.OnDocumentImagePicked(invoiceId, tempFile(".jpg", bytes = sharedBytes)),
            )
            val state = awaitUntil { it.documentPickError != null }

            assertNotNull(state.documentPickError)
            assertTrue(state.documents[invoiceId] !is OrotezProtezDocumentState.Uploaded)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun submitDocuments_withOneRequiredDocumentMissing_failsRequiredValidation() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitUntil { it.branchOptions.isNotEmpty() }
            advanceToDocumentsStep()
            awaitUntil { it.currentStep == OrotezProtezStep.Documents }

            viewModel.sendIntent(
                OrotezProtezIntent.OnDocumentImagePicked(prescriptionId, tempFile(".jpg", bytes = byteArrayOf(1))),
            )
            awaitUntil { it.documents[prescriptionId] is OrotezProtezDocumentState.Uploaded }

            viewModel.sendIntent(
                OrotezProtezIntent.OnDocumentImagePicked(earMoldId, tempFile(".jpg", bytes = byteArrayOf(2))),
            )
            awaitUntil { it.documents[earMoldId] is OrotezProtezDocumentState.Uploaded }

            viewModel.sendIntent(OrotezProtezIntent.OnSubmitDocumentsClicked)
            val state = awaitUntil { it.documentValidationError != null }

            assertNotNull(state.documentValidationError)
            assertFalse(state.hasSubmitted)
            assertFalse(state.isSubmitting)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun submitDocuments_withBothRequiredDocuments_succeedsAndAcknowledgeNavigatesBack() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitUntil { it.branchOptions.isNotEmpty() }
            advanceToDocumentsStep()
            awaitUntil { it.currentStep == OrotezProtezStep.Documents }

            viewModel.sendIntent(
                OrotezProtezIntent.OnDocumentImagePicked(prescriptionId, tempFile(".jpg", bytes = byteArrayOf(1))),
            )
            awaitUntil { it.documents[prescriptionId] is OrotezProtezDocumentState.Uploaded }

            viewModel.sendIntent(
                OrotezProtezIntent.OnDocumentImagePicked(invoiceId, tempFile(".jpg", bytes = byteArrayOf(2))),
            )
            awaitUntil { it.documents[invoiceId] is OrotezProtezDocumentState.Uploaded }

            viewModel.sendIntent(OrotezProtezIntent.OnSubmitDocumentsClicked)
            val state = awaitUntil { it.hasSubmitted }

            assertFalse(state.isSubmitting)
            assertEquals("درخواست شما با موفقیت ثبت شد.", state.submittedResultMessage)
            cancelAndIgnoreRemainingEvents()
        }

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

    private fun tempFile(extension: String, sizeBytes: Int): PlatformFile =
        tempFile(extension, bytes = ByteArray(sizeBytes) { 1 })

    private fun tempFile(extension: String, bytes: ByteArray): PlatformFile {
        val file = File.createTempFile("orotez-protez-test", extension)
        file.writeBytes(bytes)
        file.deleteOnExit()
        return PlatformFile(file)
    }

    private suspend fun ReceiveTurbine<OrotezProtezUiState>.awaitUntil(
        predicate: (OrotezProtezUiState) -> Boolean,
    ): OrotezProtezUiState {
        var state = awaitItem()
        while (!predicate(state)) {
            state = awaitItem()
        }
        return state
    }
}
