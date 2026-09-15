package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.viewDetail

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.viewDetail.contract.ViewDetailRequestEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.viewDetail.contract.ViewDetailRequestIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.viewDetail.ui.ViewDetailRequestViewModel
import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileSearchParamsDN
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterDN
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFileDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.repository.constructionInsurance.ConstructionInsuranceRepository
import com.tamin.taminhamrah.useCases.constructionInsurance.GetConstructionFilesUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class ViewDetailRequestViewModelTest {

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

    private fun buildViewModel() = ViewDetailRequestViewModel(
        getConstructionFilesUseCase = GetConstructionFilesUseCase(fakeRepository),
    )

    @Test
    fun load_populatesItemsAndFileGetter() = runTest {
        fakeRepository.constructionFilesResult = listOf(
            ConstructionFileDN(
                fileNumber = 4_479_890_000L, requestNumber = 123_456_700L, requestDate = null,
                workshopInfo = null, postalCode = null, address = null, mainPlaque = null,
                subPlaque = null, block = null, propertyConstruction = null, apartment = null,
                trade = null, partPlaque = null, sumOfComplications = null, debitNumber = null,
                totalPayment = null, meterage = null, debitStatusCode = "51", protrusion = null,
                applicationFees = null, residentialServiceInfrastructureFees = null,
                excessDensitySurchargeFees = null, increasePropertyValue = null,
                issuanceFencingWallConstructionFees = null, coveredClause3Fees = null,
                article100 = null, paymentDeadLine = null,
            )
        )
        val viewModel = buildViewModel()

        viewModel.sendIntent(ViewDetailRequestIntent.Load(fileNumber = 4_479_890_000L, requestNumber = 123_456_700L))

        val state = viewModel.uiState.value
        assertEquals(1, state.items.size)
        assertNotNull(state.file)
        assertEquals(4_479_890_000L, state.file?.fileNumber)
        assertFalse(state.isLoading)
    }

    @Test
    fun load_buildsSearchParamsFromNonZeroFileAndRequestNumbers() = runTest {
        val viewModel = buildViewModel()

        viewModel.sendIntent(ViewDetailRequestIntent.Load(fileNumber = 4_479_890_000L, requestNumber = 123_456_700L))

        assertEquals("4479890000", fakeRepository.lastSearch?.fileNo)
        assertEquals("123456700", fakeRepository.lastSearch?.reqNo)
    }

    @Test
    fun load_zeroOrNullIdentifiers_omitThemFromSearch() = runTest {
        val viewModel = buildViewModel()

        viewModel.sendIntent(ViewDetailRequestIntent.Load(fileNumber = 0L, requestNumber = null))

        assertEquals(null, fakeRepository.lastSearch?.fileNo)
        assertEquals(null, fakeRepository.lastSearch?.reqNo)
    }

    @Test
    fun load_calledTwice_onlyLoadsOnce() = runTest {
        val viewModel = buildViewModel()

        viewModel.sendIntent(ViewDetailRequestIntent.Load(fileNumber = 1L, requestNumber = 1L))
        viewModel.sendIntent(ViewDetailRequestIntent.Load(fileNumber = 2L, requestNumber = 2L))

        assertEquals(1L, viewModel.uiState.value.fileNumber)
    }

    @Test
    fun load_error_setsErrorState() = runTest {
        fakeRepository.shouldThrowError = true
        val viewModel = buildViewModel()

        viewModel.sendIntent(ViewDetailRequestIntent.Load(fileNumber = 1L, requestNumber = 1L))

        assertNotNull(viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun onBackClicked_sendsNavigateBackEvent() = runTest {
        val viewModel = buildViewModel()

        viewModel.events.test {
            viewModel.sendIntent(ViewDetailRequestIntent.OnBackClicked)
            assertIs<ViewDetailRequestEvent.NavigateBack>(awaitItem())
        }
    }
}

private class FakeConstructionInsuranceRepository : ConstructionInsuranceRepository {
    var constructionFilesResult: List<ConstructionFileDN> = emptyList()
    var beneficiariesResult: List<BeneficiaryConstructionDN> = emptyList()
    var paymentSheetsResult: List<PaymentSheetConstructionFileDN> = emptyList()
    var certificatePdfResult: PdfDownloadDN = PdfDownloadDN(pdf = null)
    var issuanceMessageResult: String = "OK"
    var installmentLettersResult: List<InstallmentLetterDN> = emptyList()

    var shouldThrowError = false
    var lastSearch: ConstructionFileSearchParamsDN? = null

    override fun getConstructionFiles(search: ConstructionFileSearchParamsDN?): Flow<List<ConstructionFileDN>> = flow {
        lastSearch = search
        if (shouldThrowError) throw RuntimeException("Error")
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
        emit(paymentSheetsResult)
    }

    override fun getCertificatePaymentSheetPdf(debitNumber: String, branchCode: String): Flow<PdfDownloadDN> = flow {
        emit(certificatePdfResult)
    }

    override fun issuancePaymentSheet(debitNumber: String): Flow<String> = flow {
        emit(issuanceMessageResult)
    }

    override fun getInstallmentLetterList(workshopId: String, branchId: String): Flow<List<InstallmentLetterDN>> = flow {
        emit(installmentLettersResult)
    }
}
