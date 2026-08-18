package com.tamin.taminhamrah.feature.pensionStatusInquiry.ui

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.contract.PensionStatusInquiryEvent
import com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.contract.PensionStatusInquiryIntent
import com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.contract.PensionStatusInquiryUiState
import com.tamin.taminhamrah.model.pension.InquirePensionCertificateDN
import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import com.tamin.taminhamrah.model.pension.PensionInquiryPR
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.useCases.pension.GetPensionInquiryUseCase
import com.tamin.taminhamrah.useCases.pension.SendRequestInquirePensionCertificateUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

@OptIn(ExperimentalCoroutinesApi::class)
class PensionStatusInquiryViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: TestPensionRepository
    private lateinit var viewModel: PensionStatusInquiryViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = TestPensionRepository()
        viewModel = PensionStatusInquiryViewModel(
            getPensionInquiryUseCase = GetPensionInquiryUseCase(repository),
            sendRequestInquirePensionCertificateUseCase = SendRequestInquirePensionCertificateUseCase(repository),
        )
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun load_emitsPensionList() = runTest(testDispatcher) {
        repository.pensionInquiryResult = listOf(sampleInquiry())

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(PensionStatusInquiryIntent.Load)
            val loaded = awaitUntil { it.pensionList.isNotEmpty() }
            assertEquals("سیدرحمت اله میرفضلی", loaded.pensionList.first().fullName)
            assertFalse(loaded.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun load_emitsError_whenRepositoryFails() = runTest(testDispatcher) {
        repository.shouldThrowError = true
        repository.error = TaminApiException(title = "API error")

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(PensionStatusInquiryIntent.Load)
            val failed = awaitUntil { it.error != null }
            assertEquals("API error", failed.error)
            assertTrue(failed.pensionList.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun sendCertificate_showsSuccessMessage() = runTest(testDispatcher) {
        repository.inquirePensionCertificateResult = InquirePensionCertificateDN(
            message = "درخواست شما با موفقیت ثبت شد",
        )

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(
                PensionStatusInquiryIntent.OnSendCertificateClicked(sampleInquiry().toPresentationItem()),
            )
            val success = awaitUntil { it.successMessage != null }
            assertEquals("درخواست شما با موفقیت ثبت شد", success.successMessage)
            assertFalse(success.isSendingCertificate)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun back_emitsNavigateBack() = runTest(testDispatcher) {
        viewModel.events.test {
            viewModel.sendIntent(PensionStatusInquiryIntent.OnBackClicked)
            assertEquals(PensionStatusInquiryEvent.NavigateBack, awaitItem())
        }
    }

    @Test
    fun dismissSuccess_clearsMessage() = runTest(testDispatcher) {
        repository.inquirePensionCertificateResult = InquirePensionCertificateDN(message = "ok")

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(
                PensionStatusInquiryIntent.OnSendCertificateClicked(sampleInquiry().toPresentationItem()),
            )
            awaitUntil { it.successMessage != null }
            viewModel.sendIntent(PensionStatusInquiryIntent.DismissSuccess)
            val dismissed = awaitUntil { it.successMessage == null }
            assertNull(dismissed.successMessage)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private suspend fun ReceiveTurbine<PensionStatusInquiryUiState>.awaitUntil(
        predicate: (PensionStatusInquiryUiState) -> Boolean,
    ): PensionStatusInquiryUiState {
        while (true) {
            val item = awaitItem()
            if (predicate(item)) return item
        }
    }
}

private class TestPensionRepository : PensionRepository {
    var pensionInquiryResult: List<PensionInquiryDN> = emptyList()
    var inquirePensionCertificateResult: InquirePensionCertificateDN? = null
    var shouldThrowError: Boolean = false
    var error: Throwable = RuntimeException("error")

    override suspend fun getPensionInquiry(filters: List<ApiFilterDN>): Flow<List<PensionInquiryDN>> = flow {
        if (shouldThrowError) throw error
        emit(pensionInquiryResult)
    }

    override suspend fun sendRequestInquirePensionCertificate(
        filters: List<ApiFilterDN>,
    ): Flow<InquirePensionCertificateDN> = flow {
        if (shouldThrowError) throw error
        emit(inquirePensionCertificateResult!!)
    }

    override suspend fun getPensionerId() = unused()
    override suspend fun getEdictPensioner(query: com.tamin.taminhamrah.model.request.ApiQueryParamDN) = unused()
    override suspend fun sendRequestDeferredInstallmentCertificate(
        request: com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentRequestDN,
    ) = unused()
    override suspend fun getPensionerPayRoll(filters: List<ApiFilterDN>) = unused()
    override suspend fun getDisabilityPersonalInfo() = unused()
    override suspend fun getUserAge(filters: List<ApiFilterDN>) = unused()
    override suspend fun pensionerPayRollPDF(filters: List<ApiFilterDN>) = unused()
    override suspend fun getEdictReportPDF(filters: List<ApiFilterDN>) = unused()
    override suspend fun getRetirementRequestInfo(filters: List<ApiFilterDN>) = unused()
    override suspend fun checkRetirementStatus() = unused()
    override suspend fun sendRetirementDocument(
        requestId: String,
        request: com.tamin.taminhamrah.model.pension.retirement.RetirementSaveDocumentDN,
    ) = unused()
    override suspend fun authenticationAndGetPersonalInfo(authenticationsCode: Long) = unused()
    override suspend fun getAuthenticationCode() = unused()
    override suspend fun sendEdictPensionerToMyInbox(filters: List<ApiFilterDN>) = unused()

    private fun unused(): Nothing = error("not used")
}

private fun sampleInquiry() = PensionInquiryDN(
    branchCode = "5750",
    insuranceNumber = "0043007196",
    pensionerRisUid = "1003406938",
    pensionerType = "بازنشستگی",
    paymentDate = "14050530",
    pensionerBaseDate = "13881201",
    fullName = "سیدرحمت اله میرفضلی",
    statusDesc = "مستمری‌بگیر فعال سازمان",
    sexDesc = null,
    branchName = "یک کرج",
    pensionEndDate = null,
    nationalId = "0043007196",
    paymentAmount = null,
)

private fun PensionInquiryDN.toPresentationItem() = PensionInquiryPR(
    branchCode = branchCode.orEmpty(),
    insuranceNumber = insuranceNumber.orEmpty(),
    pensionerRisUid = pensionerRisUid.orEmpty(),
    pensionerType = pensionerType.orEmpty(),
    paymentDate = paymentDate.orEmpty(),
    pensionerBaseDate = pensionerBaseDate.orEmpty(),
    fullName = fullName.orEmpty(),
    statusDesc = statusDesc.orEmpty(),
    sexDesc = sexDesc.orEmpty(),
    branchName = branchName.orEmpty(),
    pensionEndDate = pensionEndDate.orEmpty(),
    nationalId = nationalId.orEmpty(),
    paymentAmount = paymentAmount?.toString().orEmpty(),
)
