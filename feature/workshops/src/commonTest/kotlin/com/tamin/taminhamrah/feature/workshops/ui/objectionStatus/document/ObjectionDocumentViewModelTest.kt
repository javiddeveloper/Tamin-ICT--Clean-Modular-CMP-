package com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.document

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.workshops.fake.FakeWorkShopsRepository
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionStatus
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionType
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.useCases.workshops.GetArticleSixteenReportPdfUseCase
import com.tamin.taminhamrah.useCases.workshops.GetDebitObjectionPdfUseCase
import kotlinx.coroutines.CompletableDeferred
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * سند اعتراض — the most important business rule on this screen: which endpoint a document comes
 * from depends entirely on [ObjectionDocumentUiState.objectionType]. Swap the routing and the user
 * silently downloads the wrong file with no error shown, so it is pinned here per type, alongside
 * the failure path and the double-tap guard already in the code.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ObjectionDocumentViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeWorkShopsRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeWorkShopsRepository()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = ObjectionDocumentViewModel(
        getDebitObjectionPdf = GetDebitObjectionPdfUseCase(repository),
        getArticleSixteenReportPdf = GetArticleSixteenReportPdfUseCase(repository),
    )

    private fun open(seqNo: Long, type: WorkShopObjectionType) = ObjectionDocumentIntent.Open(
        seqNo = seqNo,
        debitNumber = "88214",
        workshopId = "0968210170",
        objectionDate = "14030512",
        objectionType = type,
        objectionStatus = WorkShopObjectionStatus.SUBMITTED,
    )

    @Test
    fun `article sixteen requests the committee report endpoint, not the debit objection one`() =
        runTest(testDispatcher) {
            val viewModel = viewModel()
            viewModel.sendIntent(open(seqNo = 55L, type = WorkShopObjectionType.ARTICLE_SIXTEEN))

            viewModel.sendIntent(ObjectionDocumentIntent.DownloadFile)

            assertEquals(55L, repository.lastArticleSixteenReportPdfSeqNo)
            assertNull(repository.lastDebitObjectionPdfSeqNo)
        }

    @Test
    fun `estimate and primary vote request the debit objection endpoint`() = runTest(testDispatcher) {
        val viewModel = viewModel()
        viewModel.sendIntent(open(seqNo = 77L, type = WorkShopObjectionType.ESTIMATE))

        viewModel.sendIntent(ObjectionDocumentIntent.DownloadFile)

        assertEquals(77L, repository.lastDebitObjectionPdfSeqNo)
        assertNull(repository.lastArticleSixteenReportPdfSeqNo)
    }

    @Test
    fun `a successful download sends the file and clears the downloading flag`() = runTest(testDispatcher) {
        val viewModel = viewModel()
        viewModel.sendIntent(open(seqNo = 1L, type = WorkShopObjectionType.ESTIMATE))

        viewModel.events.test {
            viewModel.sendIntent(ObjectionDocumentIntent.DownloadFile)

            assertTrue(awaitItem() is ObjectionDocumentEvent.DownloadSucceeded)
            cancelAndIgnoreRemainingEvents()
        }
        assertFalse(viewModel.uiState.value.isDownloading)
    }

    @Test
    fun `a failed download surfaces DownloadFailed and clears the downloading flag`() = runTest(testDispatcher) {
        repository.error = TaminApiException(title = "دریافت فایل ناموفق بود")
        val viewModel = viewModel()
        viewModel.sendIntent(open(seqNo = 1L, type = WorkShopObjectionType.ESTIMATE))

        viewModel.events.test {
            viewModel.sendIntent(ObjectionDocumentIntent.DownloadFile)

            assertEquals(ObjectionDocumentEvent.DownloadFailed, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertFalse(viewModel.uiState.value.isDownloading)
    }

    @Test
    fun `tapping download twice before the first resolves only fires one request`() = runTest(testDispatcher) {
        repository.debitObjectionPdfGate = CompletableDeferred()
        val viewModel = viewModel()
        viewModel.sendIntent(open(seqNo = 1L, type = WorkShopObjectionType.ESTIMATE))

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(ObjectionDocumentIntent.DownloadFile)
            awaitUntil { it.isDownloading }

            // The second tap arrives while the first request is still in flight.
            viewModel.sendIntent(ObjectionDocumentIntent.DownloadFile)
            assertEquals(1, repository.debitObjectionPdfCallCount)

            repository.debitObjectionPdfGate?.complete(Unit)
            awaitUntil { !it.isDownloading }
            assertEquals(1, repository.debitObjectionPdfCallCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `an unknown objection type falls back to the debit objection endpoint`() = runTest(testDispatcher) {
        val viewModel = viewModel()
        viewModel.sendIntent(open(seqNo = 9L, type = WorkShopObjectionType.UNKNOWN))

        viewModel.sendIntent(ObjectionDocumentIntent.DownloadFile)

        assertEquals(9L, repository.lastDebitObjectionPdfSeqNo)
        assertNull(repository.lastArticleSixteenReportPdfSeqNo)
    }

    private suspend fun ReceiveTurbine<ObjectionDocumentUiState>.awaitUntil(
        predicate: (ObjectionDocumentUiState) -> Boolean,
    ): ObjectionDocumentUiState {
        while (true) {
            val item = awaitItem()
            if (predicate(item)) return item
        }
    }
}
