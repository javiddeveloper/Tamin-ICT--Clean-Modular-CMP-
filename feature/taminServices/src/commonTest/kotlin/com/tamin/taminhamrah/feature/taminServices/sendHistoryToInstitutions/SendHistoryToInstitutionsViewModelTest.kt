package com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.contract.SendHistoryStep
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.contract.SendHistoryToInstitutionsEvent
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.contract.SendHistoryToInstitutionsIntent
import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.model.history.HistoryJobInfoDN
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.history.UserInfoDN
import com.tamin.taminhamrah.model.pension.EdictPensionerDN
import com.tamin.taminhamrah.model.pension.PayRollDN
import com.tamin.taminhamrah.model.pension.PayRollInboxDN
import com.tamin.taminhamrah.model.pension.PensionIdDN
import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentRequestDN
import com.tamin.taminhamrah.model.personal.DisabilityPersonalInfoDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.HistoryRepository
import com.tamin.taminhamrah.repository.pension.PensionRepository
import com.tamin.taminhamrah.useCases.history.GetUserInfosUseCase
import com.tamin.taminhamrah.useCases.history.SendToInstitutionUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerIdUseCase
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
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SendHistoryToInstitutionsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var historyRepository: FakeSendHistoryRepository
    private lateinit var pensionRepository: FakeSendHistoryPensionRepository
    private lateinit var viewModel: SendHistoryToInstitutionsViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        historyRepository = FakeSendHistoryRepository()
        pensionRepository = FakeSendHistoryPensionRepository()
        viewModel = buildViewModel()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = SendHistoryToInstitutionsViewModel(
        getUserInfosUseCase = GetUserInfosUseCase(historyRepository),
        sendToInstitutionUseCase = SendToInstitutionUseCase(historyRepository),
        getPensionerIdUseCase = GetPensionerIdUseCase(pensionRepository),
    )

    // ── LoadUserInfo ──────────────────────────────────────────────────────────

    @Test
    fun `LoadUserInfo loads user info for active insured user`() = runTest(testDispatcher) {
        historyRepository.userInfoResult = defaultUserInfo()
        pensionRepository.pensionIdResult = emptyList()

        viewModel.uiState.test {
            awaitItem() // initial
            viewModel.sendIntent(SendHistoryToInstitutionsIntent.LoadUserInfo)

            var state = awaitItem()
            while (state.isLoading || state.userInfo == null) {
                state = awaitItem()
            }
            assertNotNull(state.userInfo)
            assertEquals("عادل", state.userInfo.firstName)
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `LoadUserInfo blocks pensioner and shows access denied modal`() = runTest(testDispatcher) {
        pensionRepository.pensionIdResult = listOf(PensionIdDN(pensionerId = "12345"))

        viewModel.events.test {
            viewModel.sendIntent(SendHistoryToInstitutionsIntent.LoadUserInfo)

            val modal = assertIs<SendHistoryToInstitutionsEvent.DisplayAccessDeniedModal>(awaitItem())
            assertTrue(modal.message.contains("بازنشسته") || modal.message.contains("مستمری"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `LoadUserInfo blocks anonymous user with no insurance number and shows access denied modal`() = runTest(testDispatcher) {
        pensionRepository.pensionIdResult = emptyList()
        historyRepository.userInfoResult = defaultUserInfo(insuranceNumber = null)

        viewModel.events.test {
            viewModel.sendIntent(SendHistoryToInstitutionsIntent.LoadUserInfo)

            assertIs<SendHistoryToInstitutionsEvent.DisplayAccessDeniedModal>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `LoadUserInfo fires NavigateBack when pensioner check throws`() = runTest(testDispatcher) {
        pensionRepository.shouldThrowError = true

        viewModel.events.test {
            viewModel.sendIntent(SendHistoryToInstitutionsIntent.LoadUserInfo)

            assertIs<SendHistoryToInstitutionsEvent.ShowToast>(awaitItem())
            assertIs<SendHistoryToInstitutionsEvent.NavigateBack>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ── Step navigation ───────────────────────────────────────────────────────

    @Test
    fun `GoToNextStep moves to Review step`() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitItem() // initial — SelectType
            viewModel.sendIntent(SendHistoryToInstitutionsIntent.GoToNextStep)
            val state = awaitItem()
            assertEquals(SendHistoryStep.Review, state.currentStep)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GoToPreviousStep moves back to SelectType step`() = runTest(testDispatcher) {
        viewModel.sendIntent(SendHistoryToInstitutionsIntent.GoToNextStep)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            var state = awaitItem()
            while (state.currentStep != SendHistoryStep.Review) {
                state = awaitItem()
            }
            viewModel.sendIntent(SendHistoryToInstitutionsIntent.GoToPreviousStep)
            val backState = awaitItem()
            assertEquals(SendHistoryStep.SelectType, backState.currentStep)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ── Type selection ────────────────────────────────────────────────────────

    @Test
    fun `ConfirmTypeSelection updates selectedTypes set`() = runTest(testDispatcher) {
        val types = setOf(HistoryCertificateType.ALL, HistoryCertificateType.COMBINED)

        viewModel.uiState.test {
            awaitItem() // initial
            viewModel.sendIntent(SendHistoryToInstitutionsIntent.ConfirmTypeSelection(types))
            val state = awaitItem()
            assertEquals(types, state.selectedTypes)
            assertTrue(state.hasAnyTypeSelected)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `hasAnyTypeSelected is false when empty set selected`() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitItem() // initial — already empty, so pre-select something first
            viewModel.sendIntent(SendHistoryToInstitutionsIntent.ConfirmTypeSelection(setOf(HistoryCertificateType.ALL)))
            awaitItem() // state with ALL selected
            viewModel.sendIntent(SendHistoryToInstitutionsIntent.ConfirmTypeSelection(emptySet()))
            val state = awaitItem()
            assertFalse(state.hasAnyTypeSelected)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ── SendToInstitution ─────────────────────────────────────────────────────

    @Test
    fun `SendToInstitution success fires DisplaySuccessModal event`() = runTest(testDispatcher) {
        viewModel.sendIntent(
            SendHistoryToInstitutionsIntent.ConfirmTypeSelection(setOf(HistoryCertificateType.ALL))
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(SendHistoryToInstitutionsIntent.SendToInstitution)
            assertIs<SendHistoryToInstitutionsEvent.DisplaySuccessModal>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SendToInstitution passes selected types to repository`() = runTest(testDispatcher) {
        val types = setOf(HistoryCertificateType.WAGES, HistoryCertificateType.COMBINED)
        viewModel.sendIntent(SendHistoryToInstitutionsIntent.ConfirmTypeSelection(types))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(SendHistoryToInstitutionsIntent.SendToInstitution)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(types, historyRepository.lastSentTypes)
    }

    @Test
    fun `SendToInstitution error fires ShowToast event`() = runTest(testDispatcher) {
        historyRepository.shouldThrowError = true

        viewModel.events.test {
            viewModel.sendIntent(SendHistoryToInstitutionsIntent.SendToInstitution)
            assertIs<SendHistoryToInstitutionsEvent.ShowToast>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ── Test doubles ──────────────────────────────────────────────────────────

    private class FakeSendHistoryRepository : HistoryRepository {
        var shouldThrowError = false
        var userInfoResult: UserInfoDN = defaultUserInfo()
        var lastSentTypes: Set<HistoryCertificateType>? = null

        override suspend fun getTalfighInfos(filters: List<ApiFilterDN>): TalfighInfoDN =
            TalfighInfoDN(list = emptyList(), total = 0)

        override suspend fun getDastmozdInfos(filters: List<ApiFilterDN>): DastmozdInfoDN =
            DastmozdInfoDN(list = emptyList(), total = 0)

        override suspend fun getUserInfos(): UserInfoDN {
            if (shouldThrowError) throw RuntimeException("network error")
            return userInfoResult
        }

        override suspend fun sendToInstitution(selectedTypes: Set<HistoryCertificateType>) {
            if (shouldThrowError) throw RuntimeException("send failed")
            lastSentTypes = selectedTypes
        }

        override suspend fun getHistoryJobInfos(filters: List<ApiFilterDN>): Flow<HistoryJobInfoDN> = flow {
            emit(HistoryJobInfoDN(list = emptyList(), total = 0))
        }
    }

    private class FakeSendHistoryPensionRepository : PensionRepository {
        var shouldThrowError = false
        var pensionIdResult: List<PensionIdDN> = emptyList()

        override suspend fun getPensionerId(): Flow<List<PensionIdDN>> = flow {
            if (shouldThrowError) throw RuntimeException("pension error")
            emit(pensionIdResult)
        }

        // Unused stubs
        override suspend fun getPensionInquiry(filters: List<ApiFilterDN>) = flow<List<PensionInquiryDN>> { TODO() }
        override suspend fun getEdictPensioner(query: ApiQueryParamDN) = flow<EdictPensionerDN?> { TODO() }
        override suspend fun sendRequestDeferredInstallmentCertificate(request: DeferredInstallmentRequestDN) = flow<com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentCertificateDN> { TODO() }
        override suspend fun getPensionerPayRoll(filters: List<ApiFilterDN>) = flow<List<PayRollDN>> { TODO() }
        override suspend fun getDisabilityPersonalInfo() = flow<DisabilityPersonalInfoDN> { TODO() }
        override suspend fun getUserAge(filters: List<ApiFilterDN>) = flow<com.tamin.taminhamrah.model.personal.AgeDN> { TODO() }
        override suspend fun pensionerPayRollPDF(filters: List<ApiFilterDN>) = flow<PdfDownloadDN> { TODO() }
        override suspend fun getEdictReportPDF(filters: List<ApiFilterDN>): Flow<PdfDownloadDN> = flow { TODO() }
        override suspend fun getRetirementRequestInfo(filters: List<ApiFilterDN>) = flow<List<com.tamin.taminhamrah.model.pension.retirementInfo.RetirementRequestDN>> { TODO() }
        override suspend fun checkRetirementStatus() = flow<com.tamin.taminhamrah.model.pension.checkRetirementStatus.RetirementStatusDN> { TODO() }
        override suspend fun sendRetirementDocument(requestId: String, request: com.tamin.taminhamrah.model.pension.retirement.RetirementSaveDocumentDN) = flow<String?> { TODO() }
        override suspend fun authenticationAndGetPersonalInfo(authenticationsCode: Long) = flow<com.tamin.taminhamrah.model.pension.retirement.RetirementPersonalDN> { TODO() }
        override suspend fun getAuthenticationCode() = flow<com.tamin.taminhamrah.model.pension.authenticationTicket.AuthenticationTicketDN> { TODO() }
        override suspend fun sendEdictPensionerToMyInbox(filters: List<ApiFilterDN>) = flow<com.tamin.taminhamrah.model.pension.EdictPensionerInboxDN> { TODO() }
        override suspend fun sendPayRollToInbox(filters: List<ApiFilterDN>): Flow<PayRollInboxDN> { TODO() }

        override suspend fun sendRequestInquirePensionCertificate(filters: List<ApiFilterDN>) = flow<com.tamin.taminhamrah.model.pension.InquirePensionCertificateDN> { TODO() }
        override suspend fun saveDisabilityUserInfo(body: com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveInfoDN) = flow<com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilityRequestRefDN?> { TODO() }
        override suspend fun finalConfirmDisabilityRequest(requestId: Long, body: com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilityFinalConfirmDN) = flow<String?> { TODO() }
        override suspend fun saveDocumentDisability(requestId: Long, body: com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveDocumentDN) = flow<String?> { TODO() }
        override suspend fun getMedicalCommissionPdf(lastWorkshop: String) = flow<PdfDownloadDN> { TODO() }
        override suspend fun getRegisteredMedicalCommission(filters: List<ApiFilterDN>) = flow<List<com.tamin.taminhamrah.model.pension.disabilityRequest.medicalCommission.RegisteredMedicalCommissionDN>> { TODO() }
    }
}

private fun defaultUserInfo(
    firstName: String? = "عادل",
    lastName: String? = "حسین‌پناهی",
    insuranceNumber: String? = "0082984639",
) = UserInfoDN(
    serial1 = null, militaryServiceCode = null, fatherName = null,
    lastName = lastName, serial2 = null, creationTime = null,
    lastModificationTime = null, cityCode = null, socialSecurityNumber = null,
    lastModifiedBy = null, issueplaceName = null, birthDate = null,
    firstName = firstName, insuranceNumber = insuranceNumber,
    genderCode = null, nationalID = null, marriageCode = null,
    createdBy = null, identityNumber = null, countryCode = null,
    id = null, birthDateTimestamp = null, issueplace = null, nationCode = null
)
