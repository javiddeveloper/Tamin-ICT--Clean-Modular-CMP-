package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentAndPaymentSheet

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentAndPaymentSheet.contract.InstallmentManagementEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentAndPaymentSheet.contract.InstallmentManagementIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentAndPaymentSheet.ui.InstallmentManagementViewModel
import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileSearchParamsDN
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentConstructionListDN
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentDebitListDN
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterDN
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFileDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.constructionInsurance.ConstructionInsuranceRepository
import com.tamin.taminhamrah.useCases.constructionInsurance.GetInstallmentConstructionListPageUseCase
import com.tamin.taminhamrah.useCases.constructionInsurance.IssuancePaymentSheetUseCase
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
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class InstallmentManagementViewModelTest {

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

    private fun buildViewModel() = InstallmentManagementViewModel(
        getInstallmentConstructionListPageUseCase = GetInstallmentConstructionListPageUseCase(fakeRepository),
        issuancePaymentSheetUseCase = IssuancePaymentSheetUseCase(fakeRepository),
    )

    private fun sampleInstallment(debitSubCode: String = "1") = InstallmentConstructionListDN(
        workshopId = "2361847", debitNumber = "77640000001", debitSubCode = debitSubCode,
        dtnAmount = 400_000L, lastPaymentSheetAmount = null, dtnExpireDate = "14031001",
        lastPaymentSheetDescription = "سررسید نشده", paymentDate = null,
    )

    @Test
    fun load_populatesItemsAndSeedsHeader() = runTest {
        fakeRepository.allInstallments = listOf(sampleInstallment())
        val viewModel = buildViewModel()

        viewModel.sendIntent(
            InstallmentManagementIntent.Load(fileNumber = 1L, workshopId = "2361847", branchId = "7", debitNumber = "77640000001")
        )

        val state = viewModel.uiState.value
        assertEquals(1, state.items.size)
        assertEquals("77640000001", state.items.first().debitNumber)
        assertEquals(1L, state.fileNumber)
        assertEquals("2361847", state.workshopId)
        assertEquals("7", state.branchId)
        assertEquals("77640000001", state.debitNumber)
        assertFalse(state.isLoading)
        assertEquals("77640000001", fakeRepository.lastDebitNumber)
        assertEquals("7", fakeRepository.lastBranchId)
    }

    @Test
    fun load_calledTwice_onlyLoadsOnce() = runTest {
        val viewModel = buildViewModel()

        viewModel.sendIntent(InstallmentManagementIntent.Load(fileNumber = null, workshopId = "1", branchId = "1", debitNumber = "1"))
        viewModel.sendIntent(InstallmentManagementIntent.Load(fileNumber = null, workshopId = "2", branchId = "2", debitNumber = "2"))

        assertEquals("1", viewModel.uiState.value.debitNumber)
    }

    @Test
    fun load_error_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnPage = true
        val viewModel = buildViewModel()

        viewModel.sendIntent(InstallmentManagementIntent.Load(fileNumber = null, workshopId = "1", branchId = "1", debitNumber = "1"))

        assertNotNull(viewModel.uiState.value.paginationError)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun loadNextPage_appendsTheSecondPageAndDetectsEndOfList() = runTest {
        fakeRepository.allInstallments = (1..15).map { sampleInstallment(debitSubCode = "$it") }
        val viewModel = buildViewModel()

        viewModel.sendIntent(InstallmentManagementIntent.Load(fileNumber = null, workshopId = "1", branchId = "1", debitNumber = "1"))
        assertEquals(10, viewModel.uiState.value.items.size)
        assertFalse(viewModel.uiState.value.endReached)

        viewModel.sendIntent(InstallmentManagementIntent.LoadNextPage)

        assertEquals(15, viewModel.uiState.value.items.size)
        assertTrue(viewModel.uiState.value.endReached)
    }

    @Test
    fun retryNextPage_recoversAfterAFailedPage() = runTest {
        fakeRepository.allInstallments = (1..15).map { sampleInstallment(debitSubCode = "$it") }
        val viewModel = buildViewModel()
        viewModel.sendIntent(InstallmentManagementIntent.Load(fileNumber = null, workshopId = "1", branchId = "1", debitNumber = "1"))

        fakeRepository.shouldThrowOnPage = true
        viewModel.sendIntent(InstallmentManagementIntent.LoadNextPage)
        assertEquals(10, viewModel.uiState.value.items.size)
        assertNotNull(viewModel.uiState.value.paginationError)

        fakeRepository.shouldThrowOnPage = false
        viewModel.sendIntent(InstallmentManagementIntent.RetryNextPage)

        assertEquals(15, viewModel.uiState.value.items.size)
        assertEquals(null, viewModel.uiState.value.paginationError)
    }

    @Test
    fun onBackClicked_sendsNavigateBackEvent() = runTest {
        val viewModel = buildViewModel()

        viewModel.events.test {
            viewModel.sendIntent(InstallmentManagementIntent.OnBackClicked)
            assertIs<InstallmentManagementEvent.NavigateBack>(awaitItem())
        }
    }

    @Test
    fun load_seedsDebitStepDescription() = runTest {
        val viewModel = buildViewModel()

        viewModel.sendIntent(
            InstallmentManagementIntent.Load(
                fileNumber = 1L,
                workshopId = "2361847",
                branchId = "7",
                debitNumber = "77640000001",
                debitStepDescription = "مرحلهٔ اول تقسیط بدهی ساختمانی",
            )
        )

        assertEquals("مرحلهٔ اول تقسیط بدهی ساختمانی", viewModel.uiState.value.debitStepDescription)
    }

    @Test
    fun issuePaymentSheet_success_setsIssuanceMessage() = runTest {
        fakeRepository.issuanceMessageResult = "برگه پرداخت با موفقیت صادر شد."
        val viewModel = buildViewModel()
        viewModel.sendIntent(InstallmentManagementIntent.Load(fileNumber = null, workshopId = "1", branchId = "1", debitNumber = "1"))

        viewModel.sendIntent(InstallmentManagementIntent.IssuePaymentSheet)

        val state = viewModel.uiState.value
        assertEquals("برگه پرداخت با موفقیت صادر شد.", state.issuanceMessage)
        assertFalse(state.issuanceFailed)
        assertFalse(state.isIssuing)
        assertEquals("1", fakeRepository.lastIssuanceDebitNumber)
    }

    @Test
    fun issuePaymentSheet_error_marksIssuanceFailedAndSendsShowErrorEvent() = runTest {
        fakeRepository.shouldThrowOnIssuance = true
        val viewModel = buildViewModel()
        viewModel.sendIntent(InstallmentManagementIntent.Load(fileNumber = null, workshopId = "1", branchId = "1", debitNumber = "1"))

        viewModel.events.test {
            viewModel.sendIntent(InstallmentManagementIntent.IssuePaymentSheet)
            assertIs<InstallmentManagementEvent.ShowError>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        assertTrue(viewModel.uiState.value.issuanceFailed)
        assertFalse(viewModel.uiState.value.isIssuing)
    }

    @Test
    fun issuePaymentSheet_whileAlreadyIssuing_doesNotCallRepositoryTwice() = runTest {
        fakeRepository.issuanceNeverCompletes = true
        val viewModel = buildViewModel()
        viewModel.sendIntent(InstallmentManagementIntent.Load(fileNumber = null, workshopId = "1", branchId = "1", debitNumber = "1"))

        viewModel.sendIntent(InstallmentManagementIntent.IssuePaymentSheet)
        assertTrue(viewModel.uiState.value.isIssuing)

        viewModel.sendIntent(InstallmentManagementIntent.IssuePaymentSheet)

        assertEquals(1, fakeRepository.issuanceCallCount)
    }

    @Test
    fun dismissIssuanceNotice_clearsIssuanceMessageWithoutNavigatingBack() = runTest {
        fakeRepository.issuanceMessageResult = "برگه پرداخت با موفقیت صادر شد."
        val viewModel = buildViewModel()
        viewModel.sendIntent(InstallmentManagementIntent.Load(fileNumber = null, workshopId = "1", branchId = "1", debitNumber = "1"))
        viewModel.sendIntent(InstallmentManagementIntent.IssuePaymentSheet)
        assertNotNull(viewModel.uiState.value.issuanceMessage)

        viewModel.events.test {
            viewModel.sendIntent(InstallmentManagementIntent.DismissIssuanceNotice)
            expectNoEvents()
        }
        assertNull(viewModel.uiState.value.issuanceMessage)
    }
}

private class FakeConstructionInsuranceRepository : ConstructionInsuranceRepository {
    var allInstallments: List<InstallmentConstructionListDN> = emptyList()

    var shouldThrowOnPage = false
    var lastDebitNumber: String? = null
    var lastBranchId: String? = null

    var issuanceMessageResult: String = "OK"
    var shouldThrowOnIssuance = false
    var issuanceNeverCompletes = false
    var issuanceCallCount = 0
    var lastIssuanceDebitNumber: String? = null

    override fun getConstructionFiles(search: ConstructionFileSearchParamsDN?): Flow<List<ConstructionFileDN>> = flow {
        emit(emptyList())
    }

    override fun getConstructionFilesPage(query: ApiQueryParamDN): Flow<PageDN<ConstructionFileDN>> = flow {
        emit(PageDN(items = emptyList(), total = 0))
    }

    override fun getBeneficiariesWorkshopPage(query: ApiQueryParamDN): Flow<PageDN<BeneficiaryConstructionDN>> = flow {
        emit(PageDN(items = emptyList(), total = 0))
    }

    override fun getPaymentSheetConstructionInfo(debitNumber: String): Flow<List<PaymentSheetConstructionFileDN>> = flow {
        emit(emptyList())
    }

    override fun getCertificatePaymentSheetPdf(debitNumber: String, branchCode: String): Flow<PdfDownloadDN> = flow {
        emit(PdfDownloadDN(pdf = null))
    }

    override fun issuancePaymentSheet(debitNumber: String): Flow<String> = flow {
        issuanceCallCount++
        lastIssuanceDebitNumber = debitNumber
        if (issuanceNeverCompletes) awaitCancellation()
        if (shouldThrowOnIssuance) throw RuntimeException("Error")
        emit(issuanceMessageResult)
    }

    override fun getInstallmentLetterListPage(
        workshopId: String,
        branchId: String,
        query: ApiQueryParamDN,
    ): Flow<PageDN<InstallmentLetterDN>> = flow {
        emit(PageDN(items = emptyList(), total = 0))
    }

    override fun getDetailDebitListPage(
        debitNumber: String,
        branchId: String,
        query: ApiQueryParamDN,
    ): Flow<PageDN<InstallmentDebitListDN>> = flow {
        emit(PageDN(items = emptyList(), total = 0))
    }

    override fun getInstallmentConstructionListPage(
        debitNumber: String,
        branchId: String,
        query: ApiQueryParamDN,
    ): Flow<PageDN<InstallmentConstructionListDN>> = flow {
        lastDebitNumber = debitNumber
        lastBranchId = branchId
        if (shouldThrowOnPage) error("network")
        val start = query.start
        val end = (start + query.limit).coerceAtMost(allInstallments.size)
        val slice = if (start >= allInstallments.size) emptyList() else allInstallments.subList(start, end)
        emit(PageDN(items = slice, total = allInstallments.size))
    }
}
