package com.tamin.taminhamrah.feature.pensionStatusInquiry.ui

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.contract.PensionStatusInquiryEvent
import com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.contract.PensionStatusInquiryIntent
import com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.contract.PensionStatusInquiryUiState
import com.tamin.taminhamrah.model.certificate.RecipientDN
import com.tamin.taminhamrah.model.certificate.RecipientPR
import com.tamin.taminhamrah.model.pension.InquirePensionCertificateDN
import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentRequestDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementRequestFormDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementSaveDocumentDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.useCases.pension.GetPensionInquiryUseCase
import com.tamin.taminhamrah.useCases.pension.SendRequestInquirePensionCertificateUseCase
import com.tamin.taminhamrah.useCases.user.GetRecipientsUseCase
import com.tamin.taminhamrah.repository.UserRepository
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
    private lateinit var userRepository: TestUserRepository
    private lateinit var viewModel: PensionStatusInquiryViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = TestPensionRepository()
        userRepository = TestUserRepository()
        viewModel = PensionStatusInquiryViewModel(
            getPensionInquiryUseCase = GetPensionInquiryUseCase(repository),
            sendRequestInquirePensionCertificateUseCase = SendRequestInquirePensionCertificateUseCase(repository),
            getRecipientsUseCase = GetRecipientsUseCase(userRepository),
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
            viewModel.sendIntent(PensionStatusInquiryIntent.OnSendCertificateClicked)
            viewModel.sendIntent(PensionStatusInquiryIntent.OnRecipientSelected(RecipientPR("01", "بانک ملت")))
            viewModel.sendIntent(PensionStatusInquiryIntent.OnIssueCertificateClicked)
            val success = awaitUntil { it.successMessage != null }
            assertEquals("درخواست شما با موفقیت ثبت شد", success.successMessage)
            assertFalse(success.isSendingCertificate)
            assertFalse(success.showCertificateSheet)
            assertEquals("بانک ملت", repository.lastCertificateFilters.single().value)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun issueCertificate_withoutRecipient_flagsErrorAndDoesNotSend() = runTest(testDispatcher) {
        viewModel.sendIntent(PensionStatusInquiryIntent.OnSendCertificateClicked)
        viewModel.sendIntent(PensionStatusInquiryIntent.OnIssueCertificateClicked)

        assertTrue(viewModel.uiState.value.showRecipientError)
        assertTrue(repository.lastCertificateFilters.isEmpty())
    }

    @Test
    fun buildCertificateTarget_matchesLegacyFormat() {
        assertEquals("بانک ملت", buildCertificateTarget("بانک ملت", " "))
        assertEquals("بانک ملت شعبه  ونک", buildCertificateTarget("بانک ملت", "ونک"))
        assertEquals("بانک ملت شعبه ونک", buildCertificateTarget("بانک ملت", "شعبه ونک"))
    }

    @Test
    fun selectRecipient_loadsRecipientsOnce() = runTest(testDispatcher) {
        userRepository.recipientsResult = listOf(RecipientDN("01", "بانک ملت"))

        viewModel.sendIntent(PensionStatusInquiryIntent.OnSelectRecipientClicked)
        viewModel.sendIntent(PensionStatusInquiryIntent.DismissRecipientsSheet)
        viewModel.sendIntent(PensionStatusInquiryIntent.OnSelectRecipientClicked)

        val state = viewModel.uiState.value
        assertTrue(state.showRecipientsSheet)
        assertFalse(state.isLoadingRecipients)
        assertEquals(listOf(RecipientPR("01", "بانک ملت")), state.recipients)
        assertEquals(1, userRepository.recipientsCalls)
    }

    @Test
    fun selectRecipient_failure_closesSheetAndShowsToast() = runTest(testDispatcher) {
        userRepository.recipientsError = TaminApiException(title = "recipients failed")

        viewModel.events.test {
            viewModel.sendIntent(PensionStatusInquiryIntent.OnSelectRecipientClicked)
            assertEquals(PensionStatusInquiryEvent.ShowToast("recipients failed"), awaitItem())
        }
        val state = viewModel.uiState.value
        assertFalse(state.showRecipientsSheet)
        assertFalse(state.isLoadingRecipients)
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
            viewModel.sendIntent(PensionStatusInquiryIntent.OnSendCertificateClicked)
            viewModel.sendIntent(PensionStatusInquiryIntent.OnRecipientSelected(RecipientPR("01", "بانک ملت")))
            viewModel.sendIntent(PensionStatusInquiryIntent.OnIssueCertificateClicked)
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
    var lastCertificateFilters: List<ApiFilterDN> = emptyList()
    var shouldThrowError: Boolean = false
    var error: Throwable = RuntimeException("error")

    override suspend fun getPensionInquiry(filters: List<ApiFilterDN>): Flow<List<PensionInquiryDN>> = flow {
        if (shouldThrowError) throw error
        emit(pensionInquiryResult)
    }

    override suspend fun sendRequestInquirePensionCertificate(
        filters: List<ApiFilterDN>,
    ): Flow<InquirePensionCertificateDN> = flow {
        lastCertificateFilters = filters
        if (shouldThrowError) throw error
        emit(inquirePensionCertificateResult!!)
    }

    override suspend fun getPensionerId() = unused()
    override suspend fun getEdictPensioner(query: ApiQueryParamDN) = unused()
    override suspend fun sendRequestDeferredInstallmentCertificate(
        request: DeferredInstallmentRequestDN,
    ) = unused()
    override suspend fun getPensionerPayRoll(filters: List<ApiFilterDN>) = unused()
    override suspend fun getDisabilityPersonalInfo() = unused()
    override suspend fun getUserAge(filters: List<ApiFilterDN>) = unused()
    override suspend fun pensionerPayRollPDF(filters: List<ApiFilterDN>) = unused()
    override suspend fun getEdictReportPDF(filters: List<ApiFilterDN>) = unused()
    override suspend fun getRetirementRequestInfo(filters: List<ApiFilterDN>) = unused()
    override suspend fun createRetirementRequest(
        authenticationsCode: Long,
        form: RetirementRequestFormDN,
    ) = unused()
    override suspend fun checkRetirementStatus() = unused()
    override suspend fun sendRetirementDocument(
        requestId: String,
        request: RetirementSaveDocumentDN,
    ) = unused()
    override suspend fun authenticationAndGetPersonalInfo(authenticationsCode: Long) = unused()
    override suspend fun getAuthenticationCode() = unused()
    override suspend fun sendEdictPensionerToMyInbox(filters: List<ApiFilterDN>) = unused()
    override suspend fun sendPayRollToInbox(filters: List<ApiFilterDN>) = unused()
    override suspend fun saveDisabilityUserInfo(body: com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveInfoDN) = unused()
    override suspend fun finalConfirmDisabilityRequest(requestId: Long, body: com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilityFinalConfirmDN) = unused()
    override suspend fun saveDocumentDisability(requestId: Long, body: com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveDocumentDN) = unused()
    override suspend fun getMedicalCommissionPdf(lastWorkshop: String) = unused()
    override suspend fun getRegisteredMedicalCommission(filters: List<ApiFilterDN>) = unused()

    private fun unused(): Nothing = error("not used")
}

private class TestUserRepository : UserRepository {
    var recipientsResult: List<RecipientDN> = emptyList()
    var recipientsError: Throwable? = null
    var recipientsCalls = 0

    override suspend fun getRecipients(filters: List<ApiFilterDN>): Flow<List<RecipientDN>> = flow {
        recipientsCalls++
        recipientsError?.let { throw it }
        emit(recipientsResult)
    }
    override suspend fun getWageCertificateReport(filters: List<ApiFilterDN>) = unused()
    override fun getIdentityInfo() = unused()
    override suspend fun getUserProfileImage() = unused()
    override suspend fun fetchTaminRelation() = unused()
    override suspend fun sendImageRequest(branchCode: String, serialId: String) = unused()
    override suspend fun changeMobile(mobileNumber: String) = unused()
    override suspend fun verifyChangeMobileCode(mobile: String, otp: String, otpHashCode: String) = unused()
    override suspend fun getSubDominantsInfo(filters: List<ApiFilterDN>) = unused()
    override suspend fun getBankAccountList(filters: List<ApiFilterDN>) = unused()
    override suspend fun getInsuredActiveBranch() = unused()
    override suspend fun getRelationTaminAll(filters: List<ApiFilterDN>) = unused()
    override fun getElectronicFile(filters: List<ApiFilterDN>) = unused()
    override suspend fun downloadDocument(url: String) = unused()
    override suspend fun getUserProfile() = unused()
    override suspend fun getCurrentUser() = unused()
    override fun checkUserIsNew(nationalId: String) = unused()
    override suspend fun registerBankAccount(
        accountNumber: String,
        bankCode: String,
        accountTypeCode: String,
        startDateMillis: Long,
    ) = unused()
    override suspend fun getStatusCertificateReport(filters: List<ApiFilterDN>) = unused()

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
