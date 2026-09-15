package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.contract.BeneficiariesEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.contract.BeneficiariesIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.ui.BeneficiariesViewModel
import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileSearchParamsDN
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterDN
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFileDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.repository.constructionInsurance.ConstructionInsuranceRepository
import com.tamin.taminhamrah.useCases.constructionInsurance.GetBeneficiariesWorkshopUseCase
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
        getBeneficiariesWorkshopUseCase = GetBeneficiariesWorkshopUseCase(fakeRepository),
    )

    @Test
    fun load_populatesItemsAndSeedsHeader() = runTest {
        fakeRepository.beneficiariesResult = listOf(
            BeneficiaryConstructionDN(
                nationalCode = "0930123450", ownerType = "01", requestNumber = 123L,
                fileNumber = 456L, requestDate = "14020901", name = "علی",
                lastName = "توکلی", mobile = "09123456700",
            )
        )
        val viewModel = buildViewModel()

        viewModel.sendIntent(BeneficiariesIntent.Load(requestNumber = 123L, fileNumber = 456L, requestDate = "14020901"))

        val state = viewModel.uiState.value
        assertEquals(1, state.items.size)
        assertEquals("علی", state.items.first().name)
        assertEquals(123L, state.requestNumber)
        assertEquals(456L, state.fileNumber)
        assertFalse(state.isLoading)
        assertEquals(123L, fakeRepository.lastBeneficiariesRequestNumber)
    }

    @Test
    fun load_calledTwice_onlyLoadsOnce() = runTest {
        val viewModel = buildViewModel()

        viewModel.sendIntent(BeneficiariesIntent.Load(requestNumber = 1L, fileNumber = 1L, requestDate = null))
        viewModel.sendIntent(BeneficiariesIntent.Load(requestNumber = 2L, fileNumber = 2L, requestDate = null))

        assertEquals(1L, viewModel.uiState.value.requestNumber)
    }

    @Test
    fun load_error_setsErrorState() = runTest {
        fakeRepository.shouldThrowError = true
        val viewModel = buildViewModel()

        viewModel.sendIntent(BeneficiariesIntent.Load(requestNumber = 1L, fileNumber = 1L, requestDate = null))

        assertNotNull(viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun retry_reloadsBeneficiaries() = runTest {
        val viewModel = buildViewModel()
        viewModel.sendIntent(BeneficiariesIntent.Load(requestNumber = 1L, fileNumber = 1L, requestDate = null))

        fakeRepository.beneficiariesResult = listOf(
            BeneficiaryConstructionDN(
                nationalCode = null, ownerType = "02", requestNumber = 1L, fileNumber = 1L,
                requestDate = null, name = "زهرا", lastName = "احمدی", mobile = null,
            )
        )
        viewModel.sendIntent(BeneficiariesIntent.Retry)

        assertEquals(1, viewModel.uiState.value.items.size)
        assertEquals("زهرا", viewModel.uiState.value.items.first().name)
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
    var beneficiariesResult: List<BeneficiaryConstructionDN> = emptyList()
    var paymentSheetsResult: List<PaymentSheetConstructionFileDN> = emptyList()
    var certificatePdfResult: PdfDownloadDN = PdfDownloadDN(pdf = null)
    var issuanceMessageResult: String = "OK"
    var installmentLettersResult: List<InstallmentLetterDN> = emptyList()

    var shouldThrowError = false
    var lastBeneficiariesRequestNumber: Long? = null

    override fun getConstructionFiles(search: ConstructionFileSearchParamsDN?): Flow<List<ConstructionFileDN>> = flow {
        emit(constructionFilesResult)
    }

    override fun getBeneficiariesWorkshop(
        requestNumber: Long?,
        fileNumber: Long?,
        requestDate: String?,
    ): Flow<List<BeneficiaryConstructionDN>> = flow {
        lastBeneficiariesRequestNumber = requestNumber
        if (shouldThrowError) throw RuntimeException("Error")
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
