package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.contract.InstallmentLetterEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.contract.InstallmentLetterIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.ui.InstallmentLetterViewModel
import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileSearchParamsDN
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterDN
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFileDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.constructionInsurance.ConstructionInsuranceRepository
import com.tamin.taminhamrah.useCases.constructionInsurance.GetInstallmentLetterListPageUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
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
class InstallmentLetterViewModelTest {

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

    private fun buildViewModel() = InstallmentLetterViewModel(
        getInstallmentLetterListPageUseCase = GetInstallmentLetterListPageUseCase(fakeRepository),
    )

    private fun sampleLetter(debitNumber: String = "77640000001") = InstallmentLetterDN(
        workshopId = "14020901", debitNumber = debitNumber, debitStepDescription = "قسط اول",
        debitStatusDescription = "پرداخت شده", debitStartDate = "14021001", debitEndDate = "14031001",
        remainingAmount = 400_000L, debitNumberOld = null,
    )

    @Test
    fun load_populatesItemsAndSeedsHeader() = runTest {
        fakeRepository.allLetters = listOf(sampleLetter())
        val viewModel = buildViewModel()

        viewModel.sendIntent(InstallmentLetterIntent.Load(workshopId = "14020901", branchId = "6400"))

        val state = viewModel.uiState.value
        assertEquals(1, state.items.size)
        assertEquals("77640000001", state.items.first().debitNumber)
        assertEquals("14020901", state.workshopId)
        assertEquals("6400", state.branchId)
        assertFalse(state.isLoading)
        assertEquals("14020901", fakeRepository.lastWorkshopId)
        assertEquals("6400", fakeRepository.lastBranchId)
    }

    @Test
    fun load_calledTwice_onlyLoadsOnce() = runTest {
        val viewModel = buildViewModel()

        viewModel.sendIntent(InstallmentLetterIntent.Load(workshopId = "1", branchId = "1"))
        viewModel.sendIntent(InstallmentLetterIntent.Load(workshopId = "2", branchId = "2"))

        assertEquals("1", viewModel.uiState.value.workshopId)
    }

    @Test
    fun load_error_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnPage = true
        val viewModel = buildViewModel()

        viewModel.sendIntent(InstallmentLetterIntent.Load(workshopId = "1", branchId = "1"))

        assertNotNull(viewModel.uiState.value.paginationError)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun loadNextPage_appendsTheSecondPageAndDetectsEndOfList() = runTest {
        fakeRepository.allLetters = (1..15).map { sampleLetter(debitNumber = "7764000000$it") }
        val viewModel = buildViewModel()

        viewModel.sendIntent(InstallmentLetterIntent.Load(workshopId = "1", branchId = "1"))
        assertEquals(10, viewModel.uiState.value.items.size)
        assertFalse(viewModel.uiState.value.endReached)

        viewModel.sendIntent(InstallmentLetterIntent.LoadNextPage)

        assertEquals(15, viewModel.uiState.value.items.size)
        assertTrue(viewModel.uiState.value.endReached)
    }

    @Test
    fun retryNextPage_recoversAfterAFailedPage() = runTest {
        fakeRepository.allLetters = (1..15).map { sampleLetter(debitNumber = "7764000000$it") }
        val viewModel = buildViewModel()
        viewModel.sendIntent(InstallmentLetterIntent.Load(workshopId = "1", branchId = "1"))

        fakeRepository.shouldThrowOnPage = true
        viewModel.sendIntent(InstallmentLetterIntent.LoadNextPage)
        assertEquals(10, viewModel.uiState.value.items.size)
        assertNotNull(viewModel.uiState.value.paginationError)

        fakeRepository.shouldThrowOnPage = false
        viewModel.sendIntent(InstallmentLetterIntent.RetryNextPage)

        assertEquals(15, viewModel.uiState.value.items.size)
        assertEquals(null, viewModel.uiState.value.paginationError)
    }

    @Test
    fun onBackClicked_sendsNavigateBackEvent() = runTest {
        val viewModel = buildViewModel()

        viewModel.events.test {
            viewModel.sendIntent(InstallmentLetterIntent.OnBackClicked)
            assertIs<InstallmentLetterEvent.NavigateBack>(awaitItem())
        }
    }
}

private class FakeConstructionInsuranceRepository : ConstructionInsuranceRepository {
    var constructionFilesResult: List<ConstructionFileDN> = emptyList()
    var beneficiariesResult: List<BeneficiaryConstructionDN> = emptyList()
    var paymentSheetsResult: List<PaymentSheetConstructionFileDN> = emptyList()
    var certificatePdfResult: PdfDownloadDN = PdfDownloadDN(pdf = null)
    var issuanceMessageResult: String = "OK"
    var allLetters: List<InstallmentLetterDN> = emptyList()

    var shouldThrowOnPage = false
    var lastWorkshopId: String? = null
    var lastBranchId: String? = null

    override fun getConstructionFiles(search: ConstructionFileSearchParamsDN?): Flow<List<ConstructionFileDN>> = flow {
        emit(constructionFilesResult)
    }

    override fun getConstructionFilesPage(query: ApiQueryParamDN): Flow<PageDN<ConstructionFileDN>> = flow {
        emit(PageDN(items = constructionFilesResult, total = constructionFilesResult.size))
    }

    override fun getBeneficiariesWorkshopPage(query: ApiQueryParamDN): Flow<PageDN<BeneficiaryConstructionDN>> = flow {
        emit(PageDN(items = beneficiariesResult, total = beneficiariesResult.size))
    }

    override fun getPaymentSheetConstructionInfo(debitNumber: String): Flow<List<PaymentSheetConstructionFileDN>> = flow {
        emit(paymentSheetsResult)
    }

    override fun getCertificatePaymentSheetPdf(debitNumber: String, branchCode: String): Flow<PdfDownloadDN> = flow {
        emit(certificatePdfResult)
    }

    override fun issuancePaymentSheet(debitNumber: String): Flow<String> = flow {
        emit(issuanceMessageResult)
    }

    override fun getInstallmentLetterListPage(
        workshopId: String,
        branchId: String,
        query: ApiQueryParamDN,
    ): Flow<PageDN<InstallmentLetterDN>> = flow {
        lastWorkshopId = workshopId
        lastBranchId = branchId
        if (shouldThrowOnPage) error("network")
        val start = query.start
        val end = (start + query.limit).coerceAtMost(allLetters.size)
        val slice = if (start >= allLetters.size) emptyList() else allLetters.subList(start, end)
        emit(PageDN(items = slice, total = allLetters.size))
    }
}
