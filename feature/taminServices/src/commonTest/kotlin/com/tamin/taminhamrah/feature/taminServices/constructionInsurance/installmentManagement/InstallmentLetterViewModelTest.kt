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
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.repository.constructionInsurance.ConstructionInsuranceRepository
import com.tamin.taminhamrah.useCases.constructionInsurance.GetInstallmentLetterListUseCase
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
        getInstallmentLetterListUseCase = GetInstallmentLetterListUseCase(fakeRepository),
    )

    @Test
    fun load_populatesItemsAndSeedsHeader() = runTest {
        fakeRepository.installmentLettersResult = listOf(
            InstallmentLetterDN(
                workshopId = "14020901", debitNumber = "77640000001", debitStepDescription = "قسط اول",
                debitStatusDescription = "پرداخت شده", debitStartDate = "14021001", debitEndDate = "14031001",
                remainingAmount = 400_000L, debitNumberOld = null,
            )
        )
        val viewModel = buildViewModel()

        viewModel.sendIntent(InstallmentLetterIntent.Load(workshopId = "14020901", branchId = "6400"))

        val state = viewModel.uiState.value
        assertEquals(1, state.items.size)
        assertEquals("77640000001", state.items.first().debitNumber)
        assertEquals("14020901", state.workshopId)
        assertEquals("6400", state.branchId)
        assertFalse(state.isLoading)
        assertEquals("14020901", fakeRepository.lastInstallmentWorkshopId)
        assertEquals("6400", fakeRepository.lastInstallmentBranchId)
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
        fakeRepository.shouldThrowError = true
        val viewModel = buildViewModel()

        viewModel.sendIntent(InstallmentLetterIntent.Load(workshopId = "1", branchId = "1"))

        assertNotNull(viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun retry_reloadsInstallmentLetters() = runTest {
        val viewModel = buildViewModel()
        viewModel.sendIntent(InstallmentLetterIntent.Load(workshopId = "1", branchId = "1"))

        fakeRepository.installmentLettersResult = listOf(
            InstallmentLetterDN(
                workshopId = "1", debitNumber = "77100000001", debitStepDescription = null,
                debitStatusDescription = null, debitStartDate = null, debitEndDate = null,
                remainingAmount = 0L, debitNumberOld = null,
            )
        )
        viewModel.sendIntent(InstallmentLetterIntent.Retry)

        assertEquals(1, viewModel.uiState.value.items.size)
        assertEquals("77100000001", viewModel.uiState.value.items.first().debitNumber)
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
    var installmentLettersResult: List<InstallmentLetterDN> = emptyList()

    var shouldThrowError = false
    var lastInstallmentWorkshopId: String? = null
    var lastInstallmentBranchId: String? = null

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
        emit(paymentSheetsResult)
    }

    override fun getCertificatePaymentSheetPdf(debitNumber: String, branchCode: String): Flow<PdfDownloadDN> = flow {
        emit(certificatePdfResult)
    }

    override fun issuancePaymentSheet(debitNumber: String): Flow<String> = flow {
        emit(issuanceMessageResult)
    }

    override fun getInstallmentLetterList(workshopId: String, branchId: String): Flow<List<InstallmentLetterDN>> = flow {
        lastInstallmentWorkshopId = workshopId
        lastInstallmentBranchId = branchId
        if (shouldThrowError) throw RuntimeException("Error")
        emit(installmentLettersResult)
    }
}
