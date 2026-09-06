package com.tamin.taminhamrah.feature.deferredInstallment.ui

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.deferredInstallment.ui.contract.DeferredInstallmentFieldError
import com.tamin.taminhamrah.feature.deferredInstallment.ui.contract.DeferredInstallmentIntent
import com.tamin.taminhamrah.feature.deferredInstallment.ui.contract.DeferredInstallmentOptionUi
import com.tamin.taminhamrah.feature.deferredInstallment.ui.contract.GUARANTEE_FOR_OTHERS
import com.tamin.taminhamrah.feature.deferredInstallment.ui.contract.GUARANTEE_FOR_SELF
import com.tamin.taminhamrah.model.common.BeneficiaryDN
import com.tamin.taminhamrah.model.common.InsuranceTypeDN
import com.tamin.taminhamrah.model.common.UserType
import com.tamin.taminhamrah.model.common.UserTypeInfoDN
import com.tamin.taminhamrah.model.common.JobTitleListDN
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.pension.EdictPensionerDN
import com.tamin.taminhamrah.model.pension.EdictPensionerInboxDN
import com.tamin.taminhamrah.model.pension.InquirePensionCertificateDN
import com.tamin.taminhamrah.model.pension.PensionIdDN
import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import com.tamin.taminhamrah.model.pension.PayRollDN
import com.tamin.taminhamrah.model.pension.PayRollInboxDN
import com.tamin.taminhamrah.model.pension.authenticationTicket.AuthenticationTicketDN
import com.tamin.taminhamrah.model.pension.checkRetirementStatus.RetirementStatusDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentCertificateDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentRequestDN
import com.tamin.taminhamrah.model.pension.installment.RequestCertificateDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementPersonalDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementSaveDocumentDN
import com.tamin.taminhamrah.model.pension.retirementInfo.RetirementRequestDN
import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.DisabilityPersonalInfoDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.common.CommonRepository
import com.tamin.taminhamrah.repository.pension.PensionRepository
import com.tamin.taminhamrah.useCases.common.GetBeneficiaryUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerIdUseCase
import com.tamin.taminhamrah.useCases.pension.SendRequestDeferredInstallmentCertificateUseCase
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DeferredInstallmentViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var pensionRepository: FakeDeferredInstallmentPensionRepository
    private lateinit var commonRepository: FakeDeferredInstallmentCommonRepository
    private lateinit var viewModel: DeferredInstallmentViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        pensionRepository = FakeDeferredInstallmentPensionRepository()
        commonRepository = FakeDeferredInstallmentCommonRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(): DeferredInstallmentViewModel = DeferredInstallmentViewModel(
        getPensionerIdUseCase = GetPensionerIdUseCase(pensionRepository),
        getBeneficiaryUseCase = GetBeneficiaryUseCase(commonRepository),
        sendRequestUseCase = SendRequestDeferredInstallmentCertificateUseCase(pensionRepository),
    )

    @Test
    fun loadInitialData_whenApiFails_keepsErrorVisible() = runTest(testDispatcher) {
        pensionRepository.shouldThrowOnGetPensionerId = true
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertTrue(state.error!!.isNotBlank())
    }

    @Test
    fun loadInitialData_populatesFirstPensionerId() = runTest(testDispatcher) {
        pensionRepository.pensionIds = listOf(PensionIdDN("1003406938"), PensionIdDN("2000000000"))
        viewModel = buildViewModel()

        viewModel.uiState.test {
            var state = awaitItem()
            while (state.isLoading || state.pensionerId.isBlank()) {
                state = awaitItem()
            }

            assertEquals("1003406938", state.pensionerId)
            assertEquals(listOf("1003406938", "2000000000"), state.pensionerIds)
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun stepOne_withoutGuaranteeType_setsGuaranteeFieldError() = runTest(testDispatcher) {
        pensionRepository.pensionIds = listOf(PensionIdDN("1003406938"))
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(DeferredInstallmentIntent.OnNextStepClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(DeferredInstallmentFieldError.GUARANTEE, viewModel.uiState.value.fieldError)
        assertEquals(DeferredInstallmentStep.CertificateRequest, viewModel.uiState.value.currentStep)
    }

    @Test
    fun stepOne_guaranteeForOthers_requiresSubFields() = runTest(testDispatcher) {
        pensionRepository.pensionIds = listOf(PensionIdDN("1003406938"))
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(DeferredInstallmentIntent.OnGuaranteePicked(GUARANTEE_FOR_OTHERS))
        viewModel.sendIntent(DeferredInstallmentIntent.OnNextStepClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(DeferredInstallmentFieldError.FIRST_NAME, viewModel.uiState.value.fieldError)
    }

    @Test
    fun stepOne_validSelfGuarantee_advancesToLoanDetails() = runTest(testDispatcher) {
        pensionRepository.pensionIds = listOf(PensionIdDN("1003406938"))
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(DeferredInstallmentIntent.OnGuaranteePicked(GUARANTEE_FOR_SELF))
        viewModel.sendIntent(DeferredInstallmentIntent.OnNextStepClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(DeferredInstallmentStep.LoanDetails, viewModel.uiState.value.currentStep)
        assertNull(viewModel.uiState.value.fieldError)
    }

    @Test
    fun stepTwo_withoutBank_setsBankFieldError() = runTest(testDispatcher) {
        advanceToStepTwo()
        viewModel.sendIntent(DeferredInstallmentIntent.OnSubmitClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(DeferredInstallmentFieldError.BANK, viewModel.uiState.value.fieldError)
        assertFalse(viewModel.uiState.value.showConfirmDialog)
    }

    @Test
    fun stepTwo_amountBelowMinimum_setsAmountMinFieldError() = runTest(testDispatcher) {
        advanceToStepTwo()
        fillValidStepTwo(amount = "100000", count = "12")
        viewModel.sendIntent(DeferredInstallmentIntent.OnSubmitClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(DeferredInstallmentFieldError.AMOUNT_MIN, viewModel.uiState.value.fieldError)
    }

    @Test
    fun stepTwo_installmentCountOutOfRange_setsCountRangeFieldError() = runTest(testDispatcher) {
        advanceToStepTwo()
        fillValidStepTwo(amount = "500000", count = "10")
        viewModel.sendIntent(DeferredInstallmentIntent.OnSubmitClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(DeferredInstallmentFieldError.COUNT_RANGE, viewModel.uiState.value.fieldError)
    }

    @Test
    fun stepTwo_guaranteeAmountBelowMinimum_setsGuaranteeAmountFieldError() = runTest(testDispatcher) {
        advanceToStepTwo()
        fillValidStepTwo(amount = "500000", count = "12")
        viewModel.sendIntent(DeferredInstallmentIntent.OnGuaranteeAmountChanged("5000000"))
        viewModel.sendIntent(DeferredInstallmentIntent.OnSubmitClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(DeferredInstallmentFieldError.GUARANTEE_AMOUNT, viewModel.uiState.value.fieldError)
    }

    @Test
    fun submit_selfGuarantee_buildsCorrectRequest() = runTest(testDispatcher) {
        advanceToStepTwo()
        fillValidStepTwo(amount = "500000", count = "12")
        viewModel.sendIntent(DeferredInstallmentIntent.OnSubmitClicked)
        viewModel.sendIntent(DeferredInstallmentIntent.OnConfirmSubmit)
        testDispatcher.scheduler.advanceUntilIdle()

        val request = pensionRepository.lastSubmittedRequest
        assertNotNull(request)
        assertEquals("1003406938", request.pensionerId)
        assertEquals(GUARANTEE_FOR_SELF, request.garanteeType)
        assertEquals("017", request.bank?.bankCode)
        assertEquals("شعبه مرکزی", request.bankBranch)
        assertEquals("500000", request.installmentAmount)
        assertEquals("12", request.installmentCount)
        assertEquals(6_000_000L, request.loanAmount)
        assertEquals(7_200_000L, request.guaranteeAmount)
        assertNull(request.firstName)
        assertNull(request.lastName)
        assertNull(request.nationalId)
        assertNull(request.birthDate)
        assertTrue(viewModel.uiState.value.hasSubmitted)
    }

    @Test
    fun submit_guaranteeForOthers_buildsCorrectRequest() = runTest(testDispatcher) {
        pensionRepository.pensionIds = listOf(PensionIdDN("1003406938"))
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(DeferredInstallmentIntent.OnGuaranteePicked(GUARANTEE_FOR_OTHERS))
        viewModel.sendIntent(DeferredInstallmentIntent.OnFirstNameChanged("علی"))
        viewModel.sendIntent(DeferredInstallmentIntent.OnLastNameChanged("محمدی"))
        viewModel.sendIntent(DeferredInstallmentIntent.OnNationalIdChanged("1234567890"))
        viewModel.sendIntent(
            DeferredInstallmentIntent.OnBirthDatePicked(
                year = 1360,
                month = 1,
                day = 1,
            )
        )
        viewModel.sendIntent(DeferredInstallmentIntent.OnNextStepClicked)
        fillValidStepTwo(amount = "500000", count = "12")
        viewModel.sendIntent(DeferredInstallmentIntent.OnSubmitClicked)
        viewModel.sendIntent(DeferredInstallmentIntent.OnConfirmSubmit)
        testDispatcher.scheduler.advanceUntilIdle()

        val request = pensionRepository.lastSubmittedRequest
        assertNotNull(request)
        assertEquals(GUARANTEE_FOR_OTHERS, request.garanteeType)
        assertEquals("علی", request.firstName)
        assertEquals("محمدی", request.lastName)
        assertEquals("1234567890", request.nationalId)
        assertNotNull(request.birthDate)
    }

    private fun advanceToStepTwo() {
        pensionRepository.pensionIds = listOf(PensionIdDN("1003406938"))
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.sendIntent(DeferredInstallmentIntent.OnGuaranteePicked(GUARANTEE_FOR_SELF))
        viewModel.sendIntent(DeferredInstallmentIntent.OnNextStepClicked)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    private fun fillValidStepTwo(amount: String, count: String) {
        viewModel.sendIntent(
            DeferredInstallmentIntent.OnBankPicked(
                DeferredInstallmentOptionUi(id = "017", label = "بانک ملی"),
            )
        )
        viewModel.sendIntent(DeferredInstallmentIntent.OnBranchChanged("شعبه مرکزی"))
        viewModel.sendIntent(DeferredInstallmentIntent.OnInstallmentAmountChanged(amount))
        viewModel.sendIntent(DeferredInstallmentIntent.OnInstallmentCountChanged(count))
    }
}

private class FakeDeferredInstallmentPensionRepository : PensionRepository {
    var pensionIds: List<PensionIdDN> = emptyList()
    var lastSubmittedRequest: DeferredInstallmentRequestDN? = null
    var shouldThrowOnGetPensionerId: Boolean = false

    override suspend fun getPensionerId(): Flow<List<PensionIdDN>> = flow {
        if (shouldThrowOnGetPensionerId) {
            throw RuntimeException("SSL handshake failed")
        }
        emit(pensionIds)
    }

    override suspend fun sendRequestDeferredInstallmentCertificate(
        request: DeferredInstallmentRequestDN,
    ): Flow<DeferredInstallmentCertificateDN> = flow {
        lastSubmittedRequest = request
        emit(DeferredInstallmentCertificateDN(request = RequestCertificateDN(refCode = "REF-123")))
    }

    override suspend fun getEdictPensioner(query: ApiQueryParamDN): Flow<EdictPensionerDN?> =
        error("not used")
    override suspend fun sendEdictPensionerToMyInbox(filters: List<ApiFilterDN>): Flow<EdictPensionerInboxDN> =
        error("not used")
    override suspend fun getEdictReportPDF(filters: List<ApiFilterDN>): Flow<PdfDownloadDN> =
        error("not used")
    override suspend fun getPensionInquiry(filters: List<ApiFilterDN>): Flow<List<PensionInquiryDN>> =
        error("not used")
    override suspend fun getPensionerPayRoll(filters: List<ApiFilterDN>): Flow<List<PayRollDN>> =
        error("not used")
    override suspend fun getDisabilityPersonalInfo(): Flow<DisabilityPersonalInfoDN> =
        error("not used")
    override suspend fun getUserAge(filters: List<ApiFilterDN>): Flow<AgeDN> =
        error("not used")
    override suspend fun pensionerPayRollPDF(filters: List<ApiFilterDN>): Flow<PdfDownloadDN> =
        error("not used")
    override suspend fun getRetirementRequestInfo(filters: List<ApiFilterDN>): Flow<List<RetirementRequestDN>> =
        error("not used")
    override suspend fun checkRetirementStatus(): Flow<RetirementStatusDN> =
        error("not used")
    override suspend fun sendRetirementDocument(requestId: String, request: RetirementSaveDocumentDN): Flow<String?> =
        error("not used")
    override suspend fun authenticationAndGetPersonalInfo(authenticationsCode: Long): Flow<RetirementPersonalDN> =
        error("not used")
    override suspend fun getAuthenticationCode(): Flow<AuthenticationTicketDN> =
        error("not used")
    override suspend fun sendRequestInquirePensionCertificate(filters: List<ApiFilterDN>): Flow<InquirePensionCertificateDN> =
        error("not used")
    override suspend fun sendPayRollToInbox(filters: List<ApiFilterDN>): Flow<PayRollInboxDN> =
        error("not used")
    override suspend fun saveDisabilityUserInfo(body: com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveInfoDN): Flow<com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilityRequestRefDN?> =
        error("not used")
    override suspend fun finalConfirmDisabilityRequest(requestId: Long, body: com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilityFinalConfirmDN): Flow<com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilityRequestRefDN?> =
        error("not used")
    override suspend fun saveDocumentDisability(requestId: Long, body: com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveDocumentDN): Flow<String?> =
        error("not used")
    override suspend fun getMedicalCommissionPdf(lastWorkshop: String): Flow<PdfDownloadDN> =
        error("not used")
    override suspend fun getRegisteredMedicalCommission(filters: List<ApiFilterDN>): Flow<List<com.tamin.taminhamrah.model.pension.disabilityRequest.medicalCommission.RegisteredMedicalCommissionDN>> =
        error("not used")
}

private class FakeDeferredInstallmentCommonRepository : CommonRepository {
    override fun getBeneficiary(filters: List<ApiFilterDN>): Flow<List<BeneficiaryDN>> = flow {
        emit(emptyList())
    }

    override fun getMainMenu(versionCode: String, forceUpdate: Boolean): Flow<List<MainServiceDN>> =
        error("not used")
    override fun getRegistrationDeclarationForm(): Flow<ByteArray> = error("not used")
    override fun getJobTitle(query: ApiQueryParamDN): Flow<JobTitleListDN?> = error("not used")
    override fun getRoles(): Flow<List<com.tamin.taminhamrah.model.common.RoleDN>> = error("not used")
    override fun getInsuranceTypes(searchText: String?): Flow<List<InsuranceTypeDN>> {
        error("not used")
    }
    override fun checkUserType(): Flow<UserTypeInfoDN> = flow {
        emit(UserTypeInfoDN(userType = UserType.INSURED))
    }
}
