package com.tamin.taminhamrah.feature.treatment.ui.medicalConfirmations

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.treatment.fake.FakeTreatmentRepository
import com.tamin.taminhamrah.feature.treatment.ui.contract.ConfirmationsEvent
import com.tamin.taminhamrah.feature.treatment.ui.contract.ConfirmationsIntent
import com.tamin.taminhamrah.model.treatment.MedicalConfirmationDN
import com.tamin.taminhamrah.useCases.treatment.GetMedicalConfirmationPDFUseCase
import com.tamin.taminhamrah.useCases.treatment.GetMedicalConfirmationsUseCase
import com.tamin.taminhamrah.useCases.treatment.SendToInboxMedicalConfirmationUseCase
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MedicalConfirmationsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: FakeTreatmentRepository
    private lateinit var viewModel: MedicalConfirmationsViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTreatmentRepository()
        viewModel = MedicalConfirmationsViewModel(
            getMedicalConfirmationsUseCase = GetMedicalConfirmationsUseCase(repository),
            getMedicalConfirmationPDFUseCase = GetMedicalConfirmationPDFUseCase(repository),
            sendToInboxMedicalConfirmationUseCase = SendToInboxMedicalConfirmationUseCase(repository),
        )
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private val confirmation = MedicalConfirmationDN(
        repId = "1",
        supportType = "استراحت پزشکی",
        treatmentCenter = "مرکز ۱",
        outpatientRestStartDate = "1402/01/01",
        outpatientRestEndDate = "1402/01/14",
        numberOfOutpatientDays = "14",
        inpatientRestStartDate = null,
        inpatientRestEndDate = null,
        numberOfInpatientDays = null,
        unapprovedFromDate = null,
        unapprovedToDate = null,
        branchName = "بیست تهران",
        branchStatus = "تائید شعبه",
        description = "موافقت شد",
        statusDesc = "تائید شده"
    )

    @Test
    fun testLoadList_populatesConfirmations() = runTest(testDispatcher) {
        repository.medicalConfirmationsResult = listOf(confirmation)

        viewModel.uiState.test {
            awaitItem() // initial
            viewModel.sendIntent(ConfirmationsIntent.LoadList)

            var state = awaitItem()
            while (state.confirmationList.isEmpty()) state = awaitItem()

            assertEquals(1, state.confirmationList.size)
            assertEquals(false, state.isLoading)
        }
    }

    @Test
    fun testLoadList_whenRepositoryFails_emitsError() = runTest(testDispatcher) {
        repository.shouldThrowError = true

        viewModel.uiState.test {
            awaitItem() // initial
            viewModel.sendIntent(ConfirmationsIntent.LoadList)

            var state = awaitItem()
            while (state.error == null) state = awaitItem()

            assertNotNull(state.error)
            assertEquals(false, state.isLoading)
        }
    }

    @Test
    fun testDownloadPdf_setsViewerPdf() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitItem() // initial
            viewModel.sendIntent(ConfirmationsIntent.DownloadPdf("1"))

            var state = awaitItem()
            while (state.viewerPdf == null) state = awaitItem()

            assertNotNull(state.viewerPdf)

            viewModel.sendIntent(ConfirmationsIntent.DismissPdfViewer)
            while (state.viewerPdf != null) state = awaitItem()
            assertNull(state.viewerPdf)
        }
    }

    /**
     * A failed download says so twice: the banner explains, and [viewerDownloadFailed] lets the
     * viewer close itself instead of sitting on an empty page.
     */
    @Test
    fun testDownloadPdf_whenRepositoryFails_reportsTheFailureToTheViewer() = runTest(testDispatcher) {
        repository.shouldThrowError = true

        viewModel.sendIntent(ConfirmationsIntent.DownloadPdf("1"))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.error)
        assertTrue(state.viewerDownloadFailed)
        assertNull(state.viewerPdf)
    }

    @Test
    fun testSendToInbox_whenRepositoryFails_emitsErrorInsteadOfAConfirmation() = runTest(testDispatcher) {
        repository.shouldThrowError = true

        viewModel.sendIntent(ConfirmationsIntent.SendToInbox("1"))
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.error)
    }

    @Test
    fun testSendToInbox_setsResult() = runTest(testDispatcher) {
        repository.sendToInboxResult = "SUCCESS"

        viewModel.events.test {
            viewModel.sendIntent(ConfirmationsIntent.SendToInbox("1"))

            // The design acknowledges a saved certificate with a modal, not a toast.
            assertEquals(ConfirmationsEvent.SavedToInbox, awaitItem())
        }
    }
}
