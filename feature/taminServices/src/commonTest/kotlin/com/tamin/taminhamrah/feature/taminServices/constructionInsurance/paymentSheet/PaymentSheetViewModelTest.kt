package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.paymentSheet

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.paymentSheet.contract.PaymentSheetEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.paymentSheet.contract.PaymentSheetIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.paymentSheet.ui.PaymentSheetViewModel
import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileSearchParamsDN
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterDN
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFileDN
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.repository.constructionInsurance.ConstructionInsuranceRepository
import com.tamin.taminhamrah.useCases.constructionInsurance.GetCertificatePaymentSheetPdfUseCase
import com.tamin.taminhamrah.useCases.constructionInsurance.GetPaymentSheetConstructionInfoUseCase
import com.tamin.taminhamrah.useCases.constructionInsurance.IssuancePaymentSheetUseCase
import io.ktor.utils.io.ByteReadChannel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentSheetViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeRepository: FakeConstructionInsuranceRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeConstructionInsuranceRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = PaymentSheetViewModel(
        getPaymentSheetConstructionInfoUseCase = GetPaymentSheetConstructionInfoUseCase(fakeRepository),
        getCertificatePaymentSheetPdfUseCase = GetCertificatePaymentSheetPdfUseCase(fakeRepository),
        issuancePaymentSheetUseCase = IssuancePaymentSheetUseCase(fakeRepository),
    )

    private fun samplePaymentSheet(orderNumber: String = "1") = PaymentSheetConstructionFileDN(
        orderNumber = orderNumber, paymentCode = "3600123456", paymentSheetAmount = 500_000L,
        status = "پرداخت شده", paymentDate = "14021110", buildingRequest = null,
    )

    @Test
    fun load_populatesItemsAndSeedsHeader() = runTest {
        fakeRepository.paymentSheetsResult = listOf(samplePaymentSheet())
        val viewModel = buildViewModel()

        viewModel.sendIntent(PaymentSheetIntent.Load(debitNumber = "123456789010", branchCode = "6400"))

        val state = viewModel.uiState.value
        assertEquals(1, state.items.size)
        assertEquals("123456789010", state.debitNumber)
        assertEquals("6400", state.branchCode)
        assertFalse(state.isLoading)
        assertEquals("123456789010", fakeRepository.lastPaymentSheetDebitNumber)
    }

    @Test
    fun load_calledTwice_onlyLoadsOnce() = runTest {
        val viewModel = buildViewModel()

        viewModel.sendIntent(PaymentSheetIntent.Load(debitNumber = "1", branchCode = "6400"))
        viewModel.sendIntent(PaymentSheetIntent.Load(debitNumber = "2", branchCode = "6122"))

        assertEquals("1", viewModel.uiState.value.debitNumber)
    }

    @Test
    fun load_error_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnPaymentSheets = true
        val viewModel = buildViewModel()

        viewModel.sendIntent(PaymentSheetIntent.Load(debitNumber = "123", branchCode = "6400"))

        assertTrue(viewModel.uiState.value.error != null)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun retry_reloadsPaymentSheets() = runTest {
        val viewModel = buildViewModel()
        viewModel.sendIntent(PaymentSheetIntent.Load(debitNumber = "123", branchCode = "6400"))

        fakeRepository.paymentSheetsResult = listOf(samplePaymentSheet(orderNumber = "2"))
        viewModel.sendIntent(PaymentSheetIntent.Retry)

        assertEquals(1, viewModel.uiState.value.items.size)
        assertEquals("2", viewModel.uiState.value.items.first().orderNumber)
    }

    @Test
    fun downloadCertificate_success_showsPdfViewerWithDownloadedPdf() = runTest {
        fakeRepository.pdfResult = PdfDownloadDN(pdf = InputStreamDN(pdf = ByteReadChannel(byteArrayOf(1, 2, 3))))
        val viewModel = buildViewModel()
        viewModel.sendIntent(PaymentSheetIntent.Load(debitNumber = "123456789010", branchCode = "6400"))

        viewModel.sendIntent(PaymentSheetIntent.DownloadCertificate)

        val state = viewModel.uiState.value
        assertTrue(state.showPdfViewer)
        assertFalse(state.isPdfLoading)
        assertFalse(state.pdfDownloadFailed)
        assertEquals("123456789010", fakeRepository.lastPdfDebitNumber)
        assertEquals("6400", fakeRepository.lastPdfBranchCode)
    }

    @Test
    fun downloadCertificate_nullPdfContent_marksPdfDownloadFailedWithoutThrowing() = runTest {
        // The real endpoint can 200 with no actual PDF bytes attached — PaymentSheetViewModel
        // treats a null pdf.pdf.pdf channel as a failure even though no exception was thrown.
        fakeRepository.pdfResult = PdfDownloadDN(pdf = null)
        val viewModel = buildViewModel()
        viewModel.sendIntent(PaymentSheetIntent.Load(debitNumber = "123", branchCode = "6400"))

        viewModel.sendIntent(PaymentSheetIntent.DownloadCertificate)

        val state = viewModel.uiState.value
        assertTrue(state.showPdfViewer)
        assertTrue(state.pdfDownloadFailed)
        assertFalse(state.isPdfLoading)
    }

    @Test
    fun downloadCertificate_error_marksPdfDownloadFailedAndSendsShowErrorEvent() = runTest {
        fakeRepository.shouldThrowOnPdf = true
        val viewModel = buildViewModel()
        viewModel.sendIntent(PaymentSheetIntent.Load(debitNumber = "123", branchCode = "6400"))

        viewModel.events.test {
            viewModel.sendIntent(PaymentSheetIntent.DownloadCertificate)
            assertIs<PaymentSheetEvent.ShowError>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        val state = viewModel.uiState.value
        assertTrue(state.showPdfViewer) // viewer opens immediately, then shows the failure inside it
        assertTrue(state.pdfDownloadFailed)
        assertFalse(state.isPdfLoading)
    }

    @Test
    fun dismissPdfViewer_hidesViewerAndClearsPdfState() = runTest {
        fakeRepository.pdfResult = PdfDownloadDN(pdf = InputStreamDN(pdf = ByteReadChannel(byteArrayOf(1, 2, 3))))
        val viewModel = buildViewModel()
        viewModel.sendIntent(PaymentSheetIntent.Load(debitNumber = "123", branchCode = "6400"))
        viewModel.sendIntent(PaymentSheetIntent.DownloadCertificate)
        assertTrue(viewModel.uiState.value.showPdfViewer)
        assertNotNull(viewModel.uiState.value.pdfDownload)

        viewModel.sendIntent(PaymentSheetIntent.DismissPdfViewer)

        val state = viewModel.uiState.value
        assertFalse(state.showPdfViewer)
        assertNull(state.pdfDownload)
        assertFalse(state.pdfDownloadFailed)
    }

    @Test
    fun issuePaymentSheet_success_setsIssuanceMessage() = runTest {
        fakeRepository.issuanceMessageResult = "برگه پرداخت با موفقیت صادر شد."
        val viewModel = buildViewModel()
        viewModel.sendIntent(PaymentSheetIntent.Load(debitNumber = "123456789010", branchCode = "6400"))

        viewModel.sendIntent(PaymentSheetIntent.IssuePaymentSheet)

        val state = viewModel.uiState.value
        assertEquals("برگه پرداخت با موفقیت صادر شد.", state.issuanceMessage)
        assertFalse(state.issuanceFailed)
        assertFalse(state.isIssuing)
        assertEquals("123456789010", fakeRepository.lastIssuanceDebitNumber)
    }

    @Test
    fun issuePaymentSheet_error_marksIssuanceFailedAndSendsShowErrorEvent() = runTest {
        fakeRepository.shouldThrowOnIssuance = true
        val viewModel = buildViewModel()
        viewModel.sendIntent(PaymentSheetIntent.Load(debitNumber = "123", branchCode = "6400"))

        viewModel.events.test {
            viewModel.sendIntent(PaymentSheetIntent.IssuePaymentSheet)
            assertIs<PaymentSheetEvent.ShowError>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        assertTrue(viewModel.uiState.value.issuanceFailed)
        assertFalse(viewModel.uiState.value.isIssuing)
    }

    @Test
    fun onBackClicked_sendsNavigateBackEvent() = runTest {
        val viewModel = buildViewModel()

        viewModel.events.test {
            viewModel.sendIntent(PaymentSheetIntent.OnBackClicked)
            assertIs<PaymentSheetEvent.NavigateBack>(awaitItem())
        }
    }
}

private class FakeConstructionInsuranceRepository : ConstructionInsuranceRepository {
    var constructionFilesResult: List<ConstructionFileDN> = emptyList()
    var beneficiariesResult: List<BeneficiaryConstructionDN> = emptyList()
    var paymentSheetsResult: List<PaymentSheetConstructionFileDN> = emptyList()
    var pdfResult: PdfDownloadDN = PdfDownloadDN(pdf = null)
    var issuanceMessageResult: String = "OK"
    var installmentLettersResult: List<InstallmentLetterDN> = emptyList()

    var shouldThrowOnPaymentSheets = false
    var shouldThrowOnPdf = false
    var shouldThrowOnIssuance = false

    var lastPaymentSheetDebitNumber: String? = null
    var lastPdfDebitNumber: String? = null
    var lastPdfBranchCode: String? = null
    var lastIssuanceDebitNumber: String? = null

    override fun getConstructionFiles(search: ConstructionFileSearchParamsDN?): Flow<List<ConstructionFileDN>> = flow {
        emit(constructionFilesResult)
    }

    override fun getBeneficiariesWorkshop(
        requestNumber: Long?,
        fileNumber: Long?,
        requestDate: String?,
    ): Flow<List<BeneficiaryConstructionDN>> = flow {
        emit(beneficiariesResult)
    }

    override fun getPaymentSheetConstructionInfo(debitNumber: String): Flow<List<PaymentSheetConstructionFileDN>> = flow {
        lastPaymentSheetDebitNumber = debitNumber
        if (shouldThrowOnPaymentSheets) throw RuntimeException("Error")
        emit(paymentSheetsResult)
    }

    override fun getCertificatePaymentSheetPdf(debitNumber: String, branchCode: String): Flow<PdfDownloadDN> = flow {
        lastPdfDebitNumber = debitNumber
        lastPdfBranchCode = branchCode
        if (shouldThrowOnPdf) throw RuntimeException("Error")
        emit(pdfResult)
    }

    override fun issuancePaymentSheet(debitNumber: String): Flow<String> = flow {
        lastIssuanceDebitNumber = debitNumber
        if (shouldThrowOnIssuance) throw RuntimeException("Error")
        emit(issuanceMessageResult)
    }

    override fun getInstallmentLetterList(workshopId: String, branchId: String): Flow<List<InstallmentLetterDN>> = flow {
        emit(installmentLettersResult)
    }
}
