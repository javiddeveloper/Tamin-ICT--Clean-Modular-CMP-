package com.tamin.taminhamrah.feature.orotezprotez.ui

import com.tamin.taminhamrah.feature.orotezprotez.fake.FakeContractsRepository
import com.tamin.taminhamrah.feature.orotezprotez.fake.FakeOrotezProtezRepository
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezDocumentChecklist
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezDocumentState
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezIntent
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezUiState
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
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Covers picking a document image and the upload result it lands in. Needs a real [PlatformFile]
 * backed by a JVM [File], so it can't live in commonTest.
 *
 * The rejection paths (non-JPEG, oversized, duplicate) and the submit validation messages all go
 * through `getString(Res.string...)`, which can't be resolved without an Android context, so they
 * are left to instrumented/manual testing rather than asserted here.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class OrotezProtezDocumentUploadTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var contractsRepository: FakeContractsRepository
    private lateinit var viewModel: OrotezProtezViewModel

    private val prescriptionId = OrotezProtezDocumentChecklist[0].id

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val orotezProtezRepository = FakeOrotezProtezRepository().apply {
            mainInfoResult = OrotezProtezTestData.mainInfo
            insuredPersonsResult = OrotezProtezTestData.insuredPersons
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
        viewModel.sendIntent(OrotezProtezIntent.OnDocumentImagePicked(prescriptionId, jpegFile()))

        val state = awaitState { it.documents[prescriptionId] is OrotezProtezDocumentState.Uploaded }

        val uploaded = state.documents[prescriptionId] as OrotezProtezDocumentState.Uploaded
        assertEquals("uploaded-guid", uploaded.guid)
        assertEquals(setOf(prescriptionId), state.uploadedDocumentIds)
        assertNotNull(contractsRepository.lastUploadImageRequest)
    }

    @Test
    fun documentPicked_whenUploadFails_endsInFailedStateWithAMessage() = runTest(testDispatcher) {
        contractsRepository.shouldThrowOnUpload = true

        viewModel.sendIntent(OrotezProtezIntent.OnDocumentImagePicked(prescriptionId, jpegFile()))

        val state = awaitState { it.documents[prescriptionId] is OrotezProtezDocumentState.Failed }

        val failed = state.documents[prescriptionId] as OrotezProtezDocumentState.Failed
        assertTrue(failed.message.isNotBlank())
        assertTrue(state.uploadedDocumentIds.isEmpty())
    }

    @Test
    fun documentRemoved_clearsThePreviouslyUploadedDocument() = runTest(testDispatcher) {
        viewModel.sendIntent(OrotezProtezIntent.OnDocumentImagePicked(prescriptionId, jpegFile()))
        awaitState { it.documents[prescriptionId] is OrotezProtezDocumentState.Uploaded }

        viewModel.sendIntent(OrotezProtezIntent.OnDocumentRemoveClicked(prescriptionId))

        val state = awaitState { it.documents[prescriptionId] is OrotezProtezDocumentState.Empty }
        assertTrue(state.uploadedDocumentIds.isEmpty())
    }

    /**
     * Reading and deleting a [PlatformFile] hops off the test dispatcher, so state changes land
     * asynchronously — wait on real time instead of the virtual test clock.
     */
    private suspend fun awaitState(predicate: (OrotezProtezUiState) -> Boolean): OrotezProtezUiState =
        withContext(Dispatchers.Default) {
            withTimeout(WAIT_TIMEOUT_MILLIS) { viewModel.uiState.first(predicate) }
        }

    private fun jpegFile(bytes: ByteArray = byteArrayOf(1, 2, 3)): PlatformFile {
        val file = File.createTempFile("orotez-protez-test", ".jpg")
        file.writeBytes(bytes)
        file.deleteOnExit()
        return PlatformFile(file)
    }

    private companion object {
        const val WAIT_TIMEOUT_MILLIS = 5_000L
    }
}
