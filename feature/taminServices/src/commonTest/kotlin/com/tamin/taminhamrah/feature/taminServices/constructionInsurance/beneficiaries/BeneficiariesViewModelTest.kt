package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.contract.BeneficiariesEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.contract.BeneficiariesIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.ui.BeneficiariesViewModel
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
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.repository.constructionInsurance.ConstructionInsuranceRepository
import com.tamin.taminhamrah.useCases.constructionInsurance.GetBeneficiariesWorkshopPageUseCase
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
class BeneficiariesViewModelTest {

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

    private fun buildViewModel() = BeneficiariesViewModel(
        getBeneficiariesWorkshopPageUseCase = GetBeneficiariesWorkshopPageUseCase(fakeRepository),
    )

    private fun sampleBeneficiary(nationalCode: String = "0930123450") = BeneficiaryConstructionDN(
        nationalCode = nationalCode, ownerType = "01", requestNumber = 123L,
        fileNumber = 456L, requestDate = "14020901", name = "علی",
        lastName = "توکلی", mobile = "09123456700",
    )

    @Test
    fun load_populatesItemsAndSeedsHeader() = runTest {
        fakeRepository.allBeneficiaries = listOf(sampleBeneficiary())
        val viewModel = buildViewModel()

        viewModel.sendIntent(
            BeneficiariesIntent.Load(
                requestNumber = 123L,
                fileNumber = 456L,
                requestDate = "14020901",
                workshopId = "9028222442",
                branchCode = "6400",
            )
        )

        val state = viewModel.uiState.value
        assertEquals(1, state.items.size)
        assertEquals("علی", state.items.first().name)
        assertEquals(123L, state.requestNumber)
        assertEquals(456L, state.fileNumber)
        assertEquals("9028222442", state.workshopId)
        assertEquals("6400", state.branchCode)
        assertFalse(state.isLoading)
    }

    @Test
    fun load_forwardsNonZeroIdentifiersAsEqFilters() = runTest {
        val viewModel = buildViewModel()

        viewModel.sendIntent(
            BeneficiariesIntent.Load(
                requestNumber = 123L,
                fileNumber = 0L,
                requestDate = null,
                workshopId = null,
                branchCode = null,
            )
        )

        val filterProperties = fakeRepository.lastPageQuery?.filters.orEmpty().map { it.property }
        assertEquals(listOf(FilterProperty.REQ_NO), filterProperties)
    }

    @Test
    fun load_calledTwice_onlyLoadsOnce() = runTest {
        val viewModel = buildViewModel()

        viewModel.sendIntent(
            BeneficiariesIntent.Load(
                requestNumber = 1L,
                fileNumber = 1L,
                requestDate = null,
                workshopId = null,
                branchCode = null,
            )
        )
        viewModel.sendIntent(
            BeneficiariesIntent.Load(
                requestNumber = 2L,
                fileNumber = 2L,
                requestDate = null,
                workshopId = null,
                branchCode = null,
            )
        )

        assertEquals(1L, viewModel.uiState.value.requestNumber)
    }

    @Test
    fun load_error_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnPage = true
        val viewModel = buildViewModel()

        viewModel.sendIntent(
            BeneficiariesIntent.Load(
                requestNumber = 1L,
                fileNumber = 1L,
                requestDate = null,
                workshopId = null,
                branchCode = null,
            )
        )

        assertNotNull(viewModel.uiState.value.paginationError)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun loadNextPage_appendsTheSecondPageAndDetectsEndOfList() = runTest {
        fakeRepository.allBeneficiaries = (1..15).map { sampleBeneficiary(nationalCode = "093012345$it") }
        val viewModel = buildViewModel()

        viewModel.sendIntent(
            BeneficiariesIntent.Load(
                requestNumber = null,
                fileNumber = null,
                requestDate = null,
                workshopId = null,
                branchCode = null,
            )
        )
        assertEquals(10, viewModel.uiState.value.items.size)
        assertFalse(viewModel.uiState.value.endReached)

        viewModel.sendIntent(BeneficiariesIntent.LoadNextPage)

        assertEquals(15, viewModel.uiState.value.items.size)
        assertTrue(viewModel.uiState.value.endReached)
    }

    @Test
    fun retryNextPage_recoversAfterAFailedPage() = runTest {
        fakeRepository.allBeneficiaries = (1..15).map { sampleBeneficiary(nationalCode = "093012345$it") }
        val viewModel = buildViewModel()
        viewModel.sendIntent(
            BeneficiariesIntent.Load(
                requestNumber = null,
                fileNumber = null,
                requestDate = null,
                workshopId = null,
                branchCode = null,
            )
        )

        fakeRepository.shouldThrowOnPage = true
        viewModel.sendIntent(BeneficiariesIntent.LoadNextPage)
        assertEquals(10, viewModel.uiState.value.items.size)
        assertNotNull(viewModel.uiState.value.paginationError)

        fakeRepository.shouldThrowOnPage = false
        viewModel.sendIntent(BeneficiariesIntent.RetryNextPage)

        assertEquals(15, viewModel.uiState.value.items.size)
        assertEquals(null, viewModel.uiState.value.paginationError)
    }

    @Test
    fun onBackClicked_sendsNavigateBackEvent() = runTest {
        val viewModel = buildViewModel()

        viewModel.events.test {
            viewModel.sendIntent(BeneficiariesIntent.OnBackClicked)
            assertIs<BeneficiariesEvent.NavigateBack>(awaitItem())
        }
    }
}

private class FakeConstructionInsuranceRepository : ConstructionInsuranceRepository {
    var constructionFilesResult: List<ConstructionFileDN> = emptyList()
    var allBeneficiaries: List<BeneficiaryConstructionDN> = emptyList()
    var paymentSheetsResult: List<PaymentSheetConstructionFileDN> = emptyList()
    var certificatePdfResult: PdfDownloadDN = PdfDownloadDN(pdf = null)
    var issuanceMessageResult: String = "OK"
    var installmentLettersResult: List<InstallmentLetterDN> = emptyList()

    var shouldThrowOnPage = false
    var lastPageQuery: ApiQueryParamDN? = null

    override fun getConstructionFiles(search: ConstructionFileSearchParamsDN?): Flow<List<ConstructionFileDN>> = flow {
        emit(constructionFilesResult)
    }

    override fun getConstructionFilesPage(query: ApiQueryParamDN): Flow<PageDN<ConstructionFileDN>> = flow {
        emit(PageDN(items = constructionFilesResult, total = constructionFilesResult.size))
    }

    override fun getBeneficiariesWorkshopPage(query: ApiQueryParamDN): Flow<PageDN<BeneficiaryConstructionDN>> = flow {
        lastPageQuery = query
        if (shouldThrowOnPage) error("network")
        val start = query.start
        val end = (start + query.limit).coerceAtMost(allBeneficiaries.size)
        val slice = if (start >= allBeneficiaries.size) emptyList() else allBeneficiaries.subList(start, end)
        emit(PageDN(items = slice, total = allBeneficiaries.size))
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
        emit(PageDN(items = installmentLettersResult, total = installmentLettersResult.size))
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
        emit(PageDN(items = emptyList(), total = 0))
    }
}
