package com.tamin.taminhamrah.feature.workshops.ui.demandDocuments

import com.tamin.taminhamrah.feature.workshops.fake.FakeWorkShopsRepository
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.WorkshopDemandDocDN
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.useCases.workshops.GetDebitTurnoverPdfUseCase
import com.tamin.taminhamrah.useCases.workshops.GetDemandDocumentsUseCase
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
 * اسناد مطالبه of one debt, each openable as a PDF.
 *
 * The list and the PDF are two independent things that share one screen, and the interesting
 * behaviour is that they stay independent: a document that will not download is a failure of the
 * viewer, not of the list behind it.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DemandDocumentsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeWorkShopsRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeWorkShopsRepository()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = DemandDocumentsViewModel(
        GetDemandDocumentsUseCase(repository),
        GetDebitTurnoverPdfUseCase(repository),
    )

    private fun open(viewModel: DemandDocumentsViewModel) =
        viewModel.sendIntent(DemandDocumentsIntent.Open(DEBIT_NUMBER, BRANCH_CODE))

    @Test
    fun `opening loads the documents of that debt`() = runTest(testDispatcher) {
        repository.demandDocuments = documentsPage(count = 3)

        val viewModel = viewModel()
        open(viewModel)

        assertEquals(3, viewModel.uiState.value.list.items.size)
        assertEquals(DEBIT_NUMBER, viewModel.uiState.value.debitNumber)
    }

    @Test
    fun `a row the service gave no document number for cannot be opened`() = runTest(testDispatcher) {
        repository.demandDocuments = PagedListDN(
            items = listOf(WorkshopDemandDocDN(docNumber = "", docDate = "14050422")),
            total = 1,
        )

        val viewModel = viewModel()
        open(viewModel)

        assertFalse(viewModel.uiState.value.list.items[0].isViewable)
    }

    @Test
    fun `a downloaded document reaches the viewer`() = runTest(testDispatcher) {
        repository.demandDocuments = documentsPage(count = 1)

        val viewModel = viewModel()
        open(viewModel)
        viewModel.sendIntent(DemandDocumentsIntent.ShowCalculationPdf)

        assertNotNull(viewModel.uiState.value.viewerPdf)
        assertFalse(viewModel.uiState.value.downloadFailed)
    }

    /**
     * The regression this class exists for.
     *
     * A failed download used to be reported as the list's error, which replaced the documents with
     * an error view — the user lost the list they were reading because one PDF was unavailable.
     */
    @Test
    fun `a download that fails leaves the list standing`() = runTest(testDispatcher) {
        repository.demandDocuments = documentsPage(count = 2)

        val viewModel = viewModel()
        open(viewModel)
        repository.error = TaminApiException(title = "سند در دسترس نیست.")
        viewModel.sendIntent(DemandDocumentsIntent.ShowCalculationPdf)

        val state = viewModel.uiState.value
        assertTrue(state.downloadFailed)
        assertEquals(2, state.list.items.size)
        assertNull(state.list.error)
    }

    @Test
    fun `dismissing the viewer clears the document and the failure with it`() =
        runTest(testDispatcher) {
            repository.demandDocuments = documentsPage(count = 1)

            val viewModel = viewModel()
            open(viewModel)
            repository.error = TaminApiException(title = "سند در دسترس نیست.")
            viewModel.sendIntent(DemandDocumentsIntent.ShowCalculationPdf)
            viewModel.sendIntent(DemandDocumentsIntent.DismissViewer)

            val state = viewModel.uiState.value
            assertNull(state.viewerPdf)
            assertFalse(state.downloadFailed)
        }

    @Test
    fun `a failure to load the list is still the list's failure`() = runTest(testDispatcher) {
        repository.error = TaminApiException(title = LIST_FAILURE)

        val viewModel = viewModel()
        open(viewModel)

        assertNotNull(viewModel.uiState.value.list.error)
    }

    private fun documentsPage(count: Int) = PagedListDN(
        items = List(count) {
            WorkshopDemandDocDN(
                docNumber = "140500239$it",
                docDate = "14050418",
                docTypeDescription = "محاسبه جریمه ماه",
                debitStepDescription = "اجرائیه",
                debitStateDescription = "ابلاغ حکم",
            )
        },
        total = count,
    )

    private companion object {
        const val DEBIT_NUMBER = "6310030089235"
        const val BRANCH_CODE = "14"
        const val LIST_FAILURE = "اسناد مطالبه در دسترس نیست."
    }
}
