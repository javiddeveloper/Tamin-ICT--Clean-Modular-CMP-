package com.tamin.taminhamrah.feature.pensionInquiry.ui.issuanceCertificate

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.pensionInquiry.ui.issuanceCertificate.contract.IssuanceCertificateEvent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.issuanceCertificate.contract.IssuanceCertificateIntent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.issuanceCertificate.contract.IssuanceCertificateStep
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN
import com.tamin.taminhamrah.model.bankAccount.BankAccountDN
import com.tamin.taminhamrah.model.certificate.RecipientDN
import com.tamin.taminhamrah.model.certificate.RecipientPR
import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDN
import com.tamin.taminhamrah.model.identity.IdentityInfoDN
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
import com.tamin.taminhamrah.model.pension.retirement.RetirementPersonalDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementSaveDocumentDN
import com.tamin.taminhamrah.model.pension.retirementInfo.RetirementRequestDN
import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.DisabilityPersonalInfoDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.model.subdominant.insuredActiveBranch.InsuredActiveBranchDN
import com.tamin.taminhamrah.model.user.EditMobileResponseDN
import com.tamin.taminhamrah.model.user.TaminRelationDN
import com.tamin.taminhamrah.model.user.UserProfileDN
import com.tamin.taminhamrah.repository.UserRepository
import com.tamin.taminhamrah.repository.pension.PensionRepository
import com.tamin.taminhamrah.useCases.pension.GetPensionerIdUseCase
import com.tamin.taminhamrah.useCases.user.GetIdentityInfoUseCase
import com.tamin.taminhamrah.useCases.user.GetRecipientsUseCase
import com.tamin.taminhamrah.useCases.user.GetWageCertificateReportUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class IssuanceCertificateViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var pensionRepository: FakeIssuanceCertificatePensionRepository
    private lateinit var userRepository: FakeIssuanceCertificateUserRepository
    private lateinit var viewModel: IssuanceCertificateViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        pensionRepository = FakeIssuanceCertificatePensionRepository()
        userRepository = FakeIssuanceCertificateUserRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = IssuanceCertificateViewModel(
        getPensionerIdUseCase = GetPensionerIdUseCase(pensionRepository),
        getRecipientsUseCase = GetRecipientsUseCase(userRepository),
        getWageCertificateReportUseCase = GetWageCertificateReportUseCase(userRepository),
        getIdentityInfoUseCase = GetIdentityInfoUseCase(userRepository),
    )

    @Test
    fun whenInit_loadsAndSelectsFirstPensionerId() = runTest(testDispatcher) {
        pensionRepository.pensionIdResult = listOf(PensionIdDN("111"), PensionIdDN("222"))
        viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertEquals(2, state.pensionerIds.size)
            assertEquals("111", state.selectedPensionerId)
            assertEquals("سیدرحمت اله میرفضلی", state.fullName)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenIdentityInfoFetchFails_pensionerIdsStillLoad() = runTest(testDispatcher) {
        pensionRepository.pensionIdResult = listOf(PensionIdDN("111"), PensionIdDN("222"))
        userRepository.identityShouldThrow = true
        viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertEquals("", state.fullName)
            assertEquals(2, state.pensionerIds.size)
            assertEquals("111", state.selectedPensionerId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenRecipientsSheetShown_loadsRecipientsOnce() = runTest(testDispatcher) {
        pensionRepository.pensionIdResult = listOf(PensionIdDN("111"))
        userRepository.recipientsResult = listOf(
            RecipientDN(recipientCode = "001", recipientName = "بانک رفاه")
        )
        viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(IssuanceCertificateIntent.ShowRecipientsSheet)

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertTrue(state.showRecipientsSheet)
            assertEquals(1, state.recipients.size)
            assertEquals("001", state.recipients.first().code)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenSubmitWithoutRequiredFields_showsValidationErrors() = runTest(testDispatcher) {
        pensionRepository.pensionIdResult = emptyList()
        viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(IssuanceCertificateIntent.SubmitRequest)

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertEquals("شماره مستمری را انتخاب کنید", state.pensionerIdError)
            assertEquals("گیرنده را انتخاب کنید", state.recipientError)
            assertEquals("نام شعبه را وارد کنید", state.branchNameError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenSubmitSucceeds_showsSuccessDialog() = runTest(testDispatcher) {
        pensionRepository.pensionIdResult = listOf(PensionIdDN("111"))
        userRepository.recipientsResult = listOf(
            RecipientDN(recipientCode = "001", recipientName = "بانک رفاه")
        )
        userRepository.wageCertificateReportResult = "OK"
        viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(IssuanceCertificateIntent.ShowRecipientsSheet)
        viewModel.sendIntent(
            IssuanceCertificateIntent.SelectRecipient(RecipientPR(code = "001", name = "بانک رفاه"))
        )
        viewModel.sendIntent(IssuanceCertificateIntent.ChangeBranchName("مرکزی"))
        viewModel.sendIntent(IssuanceCertificateIntent.SubmitRequest)

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertTrue(state.showSuccessDialog)
            assertNull(state.pensionerIdError)
            assertNull(state.recipientError)
            assertNull(state.branchNameError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenBranchNameContainsInvalidCharacters_showsValidationError() = runTest(testDispatcher) {
        pensionRepository.pensionIdResult = listOf(PensionIdDN("111"))
        userRepository.recipientsResult = listOf(
            RecipientDN(recipientCode = "001", recipientName = "بانک رفاه")
        )
        viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(IssuanceCertificateIntent.ShowRecipientsSheet)
        viewModel.sendIntent(
            IssuanceCertificateIntent.SelectRecipient(RecipientPR(code = "001", name = "بانک رفاه"))
        )
        viewModel.sendIntent(IssuanceCertificateIntent.ChangeBranchName("Central#1"))
        viewModel.sendIntent(IssuanceCertificateIntent.SubmitRequest)

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertEquals(IssuanceCertificateStep.Info, state.currentStep)
            assertEquals("نام شعبه شامل کاراکتر غیر مجاز است", state.branchNameError)
            assertEquals(false, state.showSuccessDialog)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenBranchNameIsTooShortAndNonNumeric_showsValidationError() = runTest(testDispatcher) {
        pensionRepository.pensionIdResult = listOf(PensionIdDN("111"))
        userRepository.recipientsResult = listOf(
            RecipientDN(recipientCode = "001", recipientName = "بانک رفاه")
        )
        viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(IssuanceCertificateIntent.ShowRecipientsSheet)
        viewModel.sendIntent(
            IssuanceCertificateIntent.SelectRecipient(RecipientPR(code = "001", name = "بانک رفاه"))
        )
        viewModel.sendIntent(IssuanceCertificateIntent.ChangeBranchName("م"))
        viewModel.sendIntent(IssuanceCertificateIntent.SubmitRequest)

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertEquals("نام شعبه نمی‌تواند کمتر از دو کاراکتر باشد", state.branchNameError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenSubmitSucceeds_sendsBranchNameWithShoabehPrefix() = runTest(testDispatcher) {
        pensionRepository.pensionIdResult = listOf(PensionIdDN("111"))
        userRepository.recipientsResult = listOf(
            RecipientDN(recipientCode = "001", recipientName = "بانک رفاه")
        )
        userRepository.wageCertificateReportResult = "OK"
        viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(IssuanceCertificateIntent.ShowRecipientsSheet)
        viewModel.sendIntent(
            IssuanceCertificateIntent.SelectRecipient(RecipientPR(code = "001", name = "بانک رفاه"))
        )
        viewModel.sendIntent(IssuanceCertificateIntent.ChangeBranchName("مرکزی"))
        viewModel.sendIntent(IssuanceCertificateIntent.SubmitRequest)
        advanceUntilIdle()

        val branchNameFilter = userRepository.lastWageCertificateFilters
            .first { it.property == FilterProperty.BRANCH_NAME }
        assertEquals(" شعبه مرکزی", branchNameFilter.value)
    }

    @Test
    fun whenBranchNameAlreadyContainsShoabeh_doesNotDuplicatePrefix() = runTest(testDispatcher) {
        pensionRepository.pensionIdResult = listOf(PensionIdDN("111"))
        userRepository.recipientsResult = listOf(
            RecipientDN(recipientCode = "001", recipientName = "بانک رفاه")
        )
        userRepository.wageCertificateReportResult = "OK"
        viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(IssuanceCertificateIntent.ShowRecipientsSheet)
        viewModel.sendIntent(
            IssuanceCertificateIntent.SelectRecipient(RecipientPR(code = "001", name = "بانک رفاه"))
        )
        viewModel.sendIntent(IssuanceCertificateIntent.ChangeBranchName("شعبه مرکزی"))
        viewModel.sendIntent(IssuanceCertificateIntent.SubmitRequest)
        advanceUntilIdle()

        val branchNameFilter = userRepository.lastWageCertificateFilters
            .first { it.property == FilterProperty.BRANCH_NAME }
        assertEquals("شعبه مرکزی", branchNameFilter.value)
    }

    @Test
    fun whenSubmitFails_showsToastAndKeepsDialogClosed() = runTest(testDispatcher) {
        pensionRepository.pensionIdResult = listOf(PensionIdDN("111"))
        userRepository.recipientsResult = listOf(
            RecipientDN(recipientCode = "001", recipientName = "بانک رفاه")
        )
        userRepository.wageCertificateShouldThrow = true
        userRepository.wageCertificateError = RuntimeException("failed")
        viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(IssuanceCertificateIntent.ShowRecipientsSheet)
        viewModel.sendIntent(
            IssuanceCertificateIntent.SelectRecipient(RecipientPR(code = "001", name = "بانک رفاه"))
        )
        viewModel.sendIntent(IssuanceCertificateIntent.ChangeBranchName("مرکزی"))

        viewModel.events.test {
            viewModel.sendIntent(IssuanceCertificateIntent.SubmitRequest)
            val event = awaitItem()
            assertTrue(event is IssuanceCertificateEvent.ShowToast)
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(false, viewModel.uiState.value.showSuccessDialog)
    }

    @Test
    fun whenGoToNextStepWithMissingFields_staysOnInfoStepWithErrors() = runTest(testDispatcher) {
        pensionRepository.pensionIdResult = emptyList()
        viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(IssuanceCertificateIntent.GoToNextStep)

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertEquals(IssuanceCertificateStep.Info, state.currentStep)
            assertEquals("شماره مستمری را انتخاب کنید", state.pensionerIdError)
            assertEquals("گیرنده را انتخاب کنید", state.recipientError)
            assertEquals("نام شعبه را وارد کنید", state.branchNameError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenGoToNextStepWithValidFields_advancesToConfirmStep() = runTest(testDispatcher) {
        pensionRepository.pensionIdResult = listOf(PensionIdDN("111"))
        userRepository.recipientsResult = listOf(
            RecipientDN(recipientCode = "001", recipientName = "بانک رفاه")
        )
        viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(IssuanceCertificateIntent.ShowRecipientsSheet)
        viewModel.sendIntent(
            IssuanceCertificateIntent.SelectRecipient(RecipientPR(code = "001", name = "بانک رفاه"))
        )
        viewModel.sendIntent(IssuanceCertificateIntent.ChangeBranchName("مرکزی"))
        viewModel.sendIntent(IssuanceCertificateIntent.GoToNextStep)

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertEquals(IssuanceCertificateStep.Confirm, state.currentStep)
            assertEquals(false, state.showSuccessDialog)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenGoToPreviousStep_returnsToInfoStep() = runTest(testDispatcher) {
        pensionRepository.pensionIdResult = listOf(PensionIdDN("111"))
        userRepository.recipientsResult = listOf(
            RecipientDN(recipientCode = "001", recipientName = "بانک رفاه")
        )
        viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.sendIntent(IssuanceCertificateIntent.ShowRecipientsSheet)
        viewModel.sendIntent(
            IssuanceCertificateIntent.SelectRecipient(RecipientPR(code = "001", name = "بانک رفاه"))
        )
        viewModel.sendIntent(IssuanceCertificateIntent.ChangeBranchName("مرکزی"))
        viewModel.sendIntent(IssuanceCertificateIntent.GoToNextStep)
        advanceUntilIdle()
        viewModel.sendIntent(IssuanceCertificateIntent.GoToPreviousStep)

        viewModel.uiState.test {
            val state = expectMostRecentItem()
            assertEquals(IssuanceCertificateStep.Info, state.currentStep)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

private class FakeIssuanceCertificatePensionRepository : PensionRepository {

    var pensionIdResult: List<PensionIdDN> = emptyList()

    override suspend fun getPensionerId(): Flow<List<PensionIdDN>> = flow {
        emit(pensionIdResult)
    }

    override suspend fun getPensionInquiry(filters: List<ApiFilterDN>): Flow<List<PensionInquiryDN>> =
        error("not used in IssuanceCertificateViewModel")
    override suspend fun getEdictPensioner(query: ApiQueryParamDN): Flow<EdictPensionerDN?> =
        error("not used in IssuanceCertificateViewModel")
    override suspend fun sendRequestDeferredInstallmentCertificate(request: DeferredInstallmentRequestDN): Flow<DeferredInstallmentCertificateDN> =
        error("not used in IssuanceCertificateViewModel")
    override suspend fun getPensionerPayRoll(filters: List<ApiFilterDN>): Flow<List<PayRollDN>> =
        error("not used in IssuanceCertificateViewModel")
    override suspend fun getDisabilityPersonalInfo(): Flow<DisabilityPersonalInfoDN> =
        error("not used in IssuanceCertificateViewModel")
    override suspend fun getUserAge(filters: List<ApiFilterDN>): Flow<AgeDN> =
        error("not used in IssuanceCertificateViewModel")
    override suspend fun pensionerPayRollPDF(filters: List<ApiFilterDN>): Flow<PdfDownloadDN> =
        error("not used in IssuanceCertificateViewModel")
    override suspend fun getEdictReportPDF(filters: List<ApiFilterDN>): Flow<PdfDownloadDN> =
        error("not used in IssuanceCertificateViewModel")
    override suspend fun getRetirementRequestInfo(filters: List<ApiFilterDN>): Flow<List<RetirementRequestDN>> =
        error("not used in IssuanceCertificateViewModel")
    override suspend fun checkRetirementStatus(): Flow<RetirementStatusDN> =
        error("not used in IssuanceCertificateViewModel")
    override suspend fun sendRetirementDocument(requestId: String, request: RetirementSaveDocumentDN): Flow<String?> =
        error("not used in IssuanceCertificateViewModel")
    override suspend fun authenticationAndGetPersonalInfo(authenticationsCode: Long): Flow<RetirementPersonalDN> =
        error("not used in IssuanceCertificateViewModel")
    override suspend fun getAuthenticationCode(): Flow<AuthenticationTicketDN> =
        error("not used in IssuanceCertificateViewModel")
    override suspend fun sendEdictPensionerToMyInbox(filters: List<ApiFilterDN>): Flow<EdictPensionerInboxDN> =
        error("not used in IssuanceCertificateViewModel")
    override suspend fun sendPayRollToInbox(filters: List<ApiFilterDN>): Flow<PayRollInboxDN> =
        error("not used in IssuanceCertificateViewModel")
    override suspend fun sendRequestInquirePensionCertificate(filters: List<ApiFilterDN>): Flow<InquirePensionCertificateDN> =
        error("not used in IssuanceCertificateViewModel")
}

private class FakeIssuanceCertificateUserRepository : UserRepository {

    var recipientsResult: List<RecipientDN> = emptyList()
    var wageCertificateReportResult: String = ""
    var wageCertificateShouldThrow: Boolean = false
    var wageCertificateError: Throwable = RuntimeException("fake error")
    var lastWageCertificateFilters: List<ApiFilterDN> = emptyList()
    var identityShouldThrow: Boolean = false
    var identityResult: IdentityInfoDN = IdentityInfoDN(
        cityOfBirthId = null,
        cityOfIssueId = null,
        countryId = null,
        dateOfBirth = null,
        fatherName = null,
        firstName = "سیدرحمت اله",
        gender = null,
        id = null,
        idCardNumber = null,
        idCardSerial1 = null,
        idCardSerial2 = null,
        lastName = "میرفضلی",
        nationalId = null,
        ssn = null,
    )

    override suspend fun getRecipients(filters: List<ApiFilterDN>): Flow<List<RecipientDN>> = flow {
        emit(recipientsResult)
    }

    override suspend fun getWageCertificateReport(filters: List<ApiFilterDN>): Flow<String> = flow {
        lastWageCertificateFilters = filters
        if (wageCertificateShouldThrow) throw wageCertificateError
        emit(wageCertificateReportResult)
    }

    override fun getIdentityInfo(): Flow<IdentityInfoDN> = flow {
        if (identityShouldThrow) throw RuntimeException("fake identity error")
        emit(identityResult)
    }
    override suspend fun getUserProfileImage(): Flow<String> =
        error("not used in IssuanceCertificateViewModel")
    override suspend fun fetchTaminRelation(): Flow<TaminRelationDN> =
        error("not used in IssuanceCertificateViewModel")
    override suspend fun sendImageRequest(branchCode: String, serialId: String): Flow<String> =
        error("not used in IssuanceCertificateViewModel")
    override suspend fun changeMobile(mobileNumber: String): Flow<EditMobileResponseDN> =
        error("not used in IssuanceCertificateViewModel")
    override suspend fun verifyChangeMobileCode(mobile: String, otp: String, otpHashCode: String): Flow<String> =
        error("not used in IssuanceCertificateViewModel")
    override suspend fun getSubDominantsInfo(filters: List<ApiFilterDN>): Flow<SubdominantDN> =
        error("not used in IssuanceCertificateViewModel")
    override suspend fun getBankAccountList(filters: List<ApiFilterDN>): Flow<List<BankAccountDN>> =
        error("not used in IssuanceCertificateViewModel")
    override suspend fun getInsuredActiveBranch(): Flow<List<InsuredActiveBranchDN>> =
        error("not used in IssuanceCertificateViewModel")
    override suspend fun getRelationTaminAll(filters: List<ApiFilterDN>): Flow<List<ActiveRelationDN>> =
        error("not used in IssuanceCertificateViewModel")
    override fun getElectronicFile(filters: List<ApiFilterDN>): Flow<List<ElectronicFileDN>> =
        error("not used in IssuanceCertificateViewModel")
    override suspend fun downloadDocument(url: String): PdfDownloadDN =
        error("not used in IssuanceCertificateViewModel")
    override suspend fun getUserProfile(): Flow<UserProfileDN> =
        error("not used in IssuanceCertificateViewModel")
    override fun checkUserIsNew(nationalId: String): Flow<Boolean> =
        error("not used in IssuanceCertificateViewModel")
    override suspend fun registerBankAccount(
        accountNumber: String,
        bankCode: String,
        accountTypeCode: String,
        startDateMillis: Long,
    ): Flow<String?> =
        error("not used in IssuanceCertificateViewModel")
    override suspend fun getStatusCertificateReport(filters: List<ApiFilterDN>): Flow<String> =
        error("not used in IssuanceCertificateViewModel")
}
