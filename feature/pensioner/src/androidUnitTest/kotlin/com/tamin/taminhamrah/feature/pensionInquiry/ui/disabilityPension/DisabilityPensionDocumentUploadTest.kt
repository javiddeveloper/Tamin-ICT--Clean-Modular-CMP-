package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityDocumentState
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionEvent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionIntent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionUiState
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase
import com.tamin.taminhamrah.useCases.addDependent.RefreshDependentsUseCase
import com.tamin.taminhamrah.useCases.history.GetTalfighInfosUseCase
import com.tamin.taminhamrah.useCases.pension.FinalConfirmDisabilityRequestUseCase
import com.tamin.taminhamrah.useCases.pension.GetDisabilityPersonalInfoUseCase
import com.tamin.taminhamrah.useCases.pension.GetMedicalCommissionPdfUseCase
import com.tamin.taminhamrah.useCases.pension.GetRegisteredMedicalCommissionUseCase
import com.tamin.taminhamrah.useCases.pension.GetUserAgeUseCase
import com.tamin.taminhamrah.useCases.pension.SaveDisabilityUserInfoUseCase
import com.tamin.taminhamrah.useCases.pension.SaveDocumentDisabilityUseCase
import com.tamin.taminhamrah.useCases.personal.GetDisabilityDependentInfoUseCase
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
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
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DisabilityPensionDocumentUploadTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var contractsRepository: FakeDisabilityContractsRepository
    private lateinit var viewModel: DisabilityPensionViewModel
    private val docId = "primary_commission_opinion"

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val pensionRepository = FakeDisabilityPensionRepository()
        val personalRepository = FakeDisabilityPersonalRepository()
        val addDependentRepository = FakeDisabilityAddDependentRepository()
        val historyRepository = FakeDisabilityHistoryRepository()
        contractsRepository = FakeDisabilityContractsRepository()

        viewModel = DisabilityPensionViewModel(
            getDisabilityPersonalInfoUseCase = GetDisabilityPersonalInfoUseCase(pensionRepository),
            getDisabilityDependentInfoUseCase = GetDisabilityDependentInfoUseCase(personalRepository),
            refreshDependentsUseCase = RefreshDependentsUseCase(addDependentRepository),
            getUserAgeUseCase = GetUserAgeUseCase(pensionRepository),
            getTalfighInfosUseCase = GetTalfighInfosUseCase(historyRepository),
            getRegisteredMedicalCommissionUseCase = GetRegisteredMedicalCommissionUseCase(pensionRepository),
            getMedicalCommissionPdfUseCase = GetMedicalCommissionPdfUseCase(pensionRepository),
            uploadImageUseCase = UploadImageUseCase(contractsRepository),
            saveDisabilityUserInfoUseCase = SaveDisabilityUserInfoUseCase(pensionRepository),
            saveDocumentDisabilityUseCase = SaveDocumentDisabilityUseCase(pensionRepository),
            finalConfirmDisabilityRequestUseCase = FinalConfirmDisabilityRequestUseCase(pensionRepository),
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun documentPicked_withJpegExtension_isAcceptedAndUploaded() = runTest(testDispatcher) {
        advanceToDocumentsStep()
        viewModel.sendIntent(DisabilityPensionIntent.DocumentImagePicked(docId, tempFile(".jpg", sizeBytes = 100)))

        val state = awaitState { it.documents[docId] is DisabilityDocumentState.Uploaded }
        assertTrue(state.documents[docId] is DisabilityDocumentState.Uploaded)
        assertEquals(null, state.documentPickError)
    }

    @Test
    fun documentPicked_withNonJpegExtension_isRejected() = runTest(testDispatcher) {
        advanceToDocumentsStep()
        viewModel.sendIntent(DisabilityPensionIntent.DocumentImagePicked(docId, tempFile(".png", sizeBytes = 100)))

        val state = awaitState { it.documentPickError != null || it.documentPickErrorRes != null }
        assertTrue(state.documentPickError != null || state.documentPickErrorRes != null)
        assertFalse(state.documents[docId] is DisabilityDocumentState.Uploaded)
    }

    @Test
    fun documentPicked_exceedingMaxSize_isRejected() = runTest(testDispatcher) {
        advanceToDocumentsStep()
        viewModel.sendIntent(DisabilityPensionIntent.DocumentImagePicked(docId, tempFile(".jpg", sizeBytes = 3 * 1024 * 1024)))

        val state = awaitState { it.documentPickError != null || it.documentPickErrorRes != null }
        assertTrue(state.documentPickError != null || state.documentPickErrorRes != null)
        assertFalse(state.documents[docId] is DisabilityDocumentState.Uploaded)
    }

    @Test
    fun documentPicked_duplicateContent_isRejected() = runTest(testDispatcher) {
        advanceToDocumentsStep()
        val file1 = tempFile(".jpg", sizeBytes = 100)
        val file2 = tempFile(".jpg", sizeBytes = 100)

        viewModel.sendIntent(DisabilityPensionIntent.DocumentImagePicked(docId, file1))
        awaitState { it.documents[docId] is DisabilityDocumentState.Uploaded }

        viewModel.sendIntent(DisabilityPensionIntent.DocumentImagePicked("appeal_commission_opinion", file2))

        val state = awaitState { it.documentPickError != null || it.documentPickErrorRes != null }
        assertTrue(state.documentPickError != null || state.documentPickErrorRes != null)
    }

    private suspend fun advanceToDocumentsStep() {
        // Just cheat the step
        viewModel.sendIntent(DisabilityPensionIntent.EditSummarySectionClicked(
            com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionStep.Documents
        ))
        testDispatcher.scheduler.advanceUntilIdle()
    }

    private suspend fun awaitState(predicate: (DisabilityPensionUiState) -> Boolean): DisabilityPensionUiState =
        withContext(Dispatchers.Default) {
            withTimeout(5000L) { viewModel.uiState.first(predicate) }
        }

    private fun tempFile(extension: String, sizeBytes: Int): PlatformFile =
        tempFile(extension, ByteArray(sizeBytes) { 1 })

    private fun tempFile(extension: String, bytes: ByteArray): PlatformFile {
        val file = File.createTempFile("disability-test", extension)
        file.writeBytes(bytes)
        file.deleteOnExit()
        return PlatformFile(file)
    }
}
