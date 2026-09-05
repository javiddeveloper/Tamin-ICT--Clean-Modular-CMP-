package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionEvent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionIntent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionStep
import com.tamin.taminhamrah.model.addDependent.BranchDN
import com.tamin.taminhamrah.model.addDependent.DependentInfoDN
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipDN
import com.tamin.taminhamrah.model.addDependent.GeneralResultDN
import com.tamin.taminhamrah.model.addDependent.RegistryDataDN
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDN
import com.tamin.taminhamrah.model.addDependent.UploadImageDN
import com.tamin.taminhamrah.model.pension.EdictPensionerDN
import com.tamin.taminhamrah.model.pension.EdictPensionerInboxDN
import com.tamin.taminhamrah.model.pension.InquirePensionCertificateDN
import com.tamin.taminhamrah.model.pension.PayRollDN
import com.tamin.taminhamrah.model.pension.PayRollInboxDN
import com.tamin.taminhamrah.model.pension.PensionIdDN
import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import com.tamin.taminhamrah.model.pension.authenticationTicket.AuthenticationTicketDN
import com.tamin.taminhamrah.model.pension.checkRetirementStatus.RetirementStatusDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilityFinalConfirmDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilityRequestRefDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveDocumentDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveInfoDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.medicalCommission.RegisteredMedicalCommissionDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentCertificateDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentRequestDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementPersonalDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementSaveDocumentDN
import com.tamin.taminhamrah.model.pension.retirementInfo.RetirementRequestDN
import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.DisabilityDependentDN
import com.tamin.taminhamrah.model.personal.DisabilityPersonalInfoDN
import com.tamin.taminhamrah.model.personal.GirlSurvivorConditionDN
import com.tamin.taminhamrah.model.personal.InsuredDocDN
import com.tamin.taminhamrah.model.personal.NewInsuredSummaryDN
import com.tamin.taminhamrah.model.personal.PersonalInfoDN
import com.tamin.taminhamrah.model.personal.SubmitFinalSurvivorPensionDN
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoDN
import com.tamin.taminhamrah.model.personal.girlSurvivor.ConfirmGirlSurvivorDN
import com.tamin.taminhamrah.model.personal.girlSurvivor.GirlSurvivorReportParamsDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.SaveSurvivorInfoDN
import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentDN
import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.addDependent.AddDependentRepository
import com.tamin.taminhamrah.repository.pension.PensionRepository
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import com.tamin.taminhamrah.useCases.addDependent.RefreshDependentsUseCase
import com.tamin.taminhamrah.useCases.pension.GetDisabilityPersonalInfoUseCase
import com.tamin.taminhamrah.useCases.personal.GetDisabilityDependentInfoUseCase
import kotlinx.coroutines.CompletableDeferred
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DisabilityPensionViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var pensionRepository: FakeDisabilityPensionRepository
    private lateinit var personalRepository: FakeDisabilityPersonalRepository
    private lateinit var addDependentRepository: FakeDisabilityAddDependentRepository
    private lateinit var viewModel: DisabilityPensionViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        pensionRepository = FakeDisabilityPensionRepository()
        personalRepository = FakeDisabilityPersonalRepository()
        addDependentRepository = FakeDisabilityAddDependentRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = DisabilityPensionViewModel(
        getDisabilityPersonalInfoUseCase = GetDisabilityPersonalInfoUseCase(pensionRepository),
        getDisabilityDependentInfoUseCase = GetDisabilityDependentInfoUseCase(personalRepository),
        refreshDependentsUseCase = RefreshDependentsUseCase(addDependentRepository),
    )

    @Test
    fun whenTermsAcceptedAndNextClicked_movesToDependentsStepAndLoadsList() = runTest(testDispatcher) {
        personalRepository.dependentInfoResult = listOf(
            DisabilityDependentDN(
                firstName = "منصوره",
                lastName = "آزادی",
                nationalId = "0073160997",
                dateOfBirth = null,
                fatherName = null,
                genderCode = "02",
                genderDesc = "زن",
                tendencyCode = "100",
                tendencyDescription = null,
            )
        )
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(DisabilityPensionIntent.TermsAcceptedChanged(true))

        viewModel.uiState.test {
            viewModel.sendIntent(DisabilityPensionIntent.NextStepClicked)
            var state = awaitItem()
            while (state.currentStep != DisabilityPensionStep.Dependents || state.dependents.isEmpty()) {
                state = awaitItem()
            }

            assertEquals(DisabilityPensionStep.Dependents, state.currentStep)
            assertEquals(1, state.dependents.size)
            assertEquals("100", state.dependents.first().tendencyCode)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenTermsNotAcceptedAndNextClicked_showsValidationErrorAndStaysOnTerms() = runTest(testDispatcher) {
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            viewModel.sendIntent(DisabilityPensionIntent.NextStepClicked)
            var state = awaitItem()
            while (!state.showTermsValidationError) state = awaitItem()

            assertTrue(state.showTermsValidationError)
            assertEquals(DisabilityPensionStep.Terms, state.currentStep)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenDependentsNotConfirmedAndNextClicked_showsConfirmationError() = runTest(testDispatcher) {
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.sendIntent(DisabilityPensionIntent.TermsAcceptedChanged(true))
        viewModel.sendIntent(DisabilityPensionIntent.NextStepClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            viewModel.sendIntent(DisabilityPensionIntent.NextStepClicked)
            var state = awaitItem()
            while (!state.showDependentsConfirmationError) state = awaitItem()

            assertTrue(state.showDependentsConfirmationError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenAddDependentClicked_emitsNavigateToAddDependentEvent() = runTest(testDispatcher) {
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(DisabilityPensionIntent.AddDependentClicked)
            val event = awaitItem()
            assertIs<DisabilityPensionEvent.NavigateToAddDependent>(event)
        }
    }

    @Test
    fun whenDependentCardToggled_addsAndRemovesFromExpandedSet() = runTest(testDispatcher) {
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(DisabilityPensionIntent.DependentCardToggled("0073160997"))
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue("0073160997" in viewModel.uiState.value.expandedDependentIds)

        viewModel.sendIntent(DisabilityPensionIntent.DependentCardToggled("0073160997"))
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse("0073160997" in viewModel.uiState.value.expandedDependentIds)
    }

    @Test
    fun whenConfirmRefreshDependents_callsUseCaseAndReloadsList() = runTest(testDispatcher) {
        addDependentRepository.refreshDependentsResult = GeneralResultDN(isSuccess = true, message = "بروزرسانی شد")
        personalRepository.dependentInfoResult = listOf(
            DisabilityDependentDN(
                firstName = "روناک",
                lastName = "موسوی",
                nationalId = "0052213341",
                dateOfBirth = null,
                fatherName = null,
                genderCode = "02",
                genderDesc = "زن",
                tendencyCode = "102",
                tendencyDescription = null,
            )
        )
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            viewModel.sendIntent(DisabilityPensionIntent.ConfirmRefreshDependents)
            var state = awaitItem()
            while (state.dependents.isEmpty() || state.isRefreshingDependents) state = awaitItem()

            assertEquals(1, state.dependents.size)
            assertFalse(state.showRefreshConfirmDialog)
            assertFalse(state.isRefreshingDependents)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenConfirmRefreshDependentsCalledTwiceConcurrently_secondCallIsIgnored() = runTest(testDispatcher) {
        val gate = CompletableDeferred<Unit>()
        addDependentRepository.refreshDependentsGate = gate
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(DisabilityPensionIntent.ConfirmRefreshDependents)
        assertTrue(viewModel.uiState.value.isRefreshingDependents)

        viewModel.sendIntent(DisabilityPensionIntent.ConfirmRefreshDependents)
        gate.complete(Unit)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, addDependentRepository.refreshDependentsCallCount)
    }
}

private class FakeDisabilityPensionRepository : PensionRepository {
    var disabilityPersonalInfoResult: DisabilityPersonalInfoDN = DisabilityPersonalInfoDN(
        branch = null,
        branchName = null,
        confirmed = null,
        insuranceId = null,
        mobileNumber = null,
        personal = null,
        provinceName = null,
        work = null,
        yearsAge = null,
        monthsAge = null,
        daysAge = null,
        strAge = null,
    )

    override suspend fun getDisabilityPersonalInfo(): Flow<DisabilityPersonalInfoDN> = flow {
        emit(disabilityPersonalInfoResult)
    }

    override suspend fun getPensionInquiry(filters: List<ApiFilterDN>): Flow<List<PensionInquiryDN>> =
        error("not used in DisabilityPensionViewModel")
    override suspend fun getPensionerId(): Flow<List<PensionIdDN>> =
        error("not used in DisabilityPensionViewModel")
    override suspend fun getEdictPensioner(query: ApiQueryParamDN): Flow<EdictPensionerDN?> =
        error("not used in DisabilityPensionViewModel")
    override suspend fun sendRequestDeferredInstallmentCertificate(request: DeferredInstallmentRequestDN): Flow<DeferredInstallmentCertificateDN> =
        error("not used in DisabilityPensionViewModel")
    override suspend fun getPensionerPayRoll(filters: List<ApiFilterDN>): Flow<List<PayRollDN>> =
        error("not used in DisabilityPensionViewModel")
    override suspend fun getUserAge(filters: List<ApiFilterDN>): Flow<AgeDN> =
        error("not used in DisabilityPensionViewModel")
    override suspend fun pensionerPayRollPDF(filters: List<ApiFilterDN>): Flow<PdfDownloadDN> =
        error("not used in DisabilityPensionViewModel")
    override suspend fun getEdictReportPDF(filters: List<ApiFilterDN>): Flow<PdfDownloadDN> =
        error("not used in DisabilityPensionViewModel")
    override suspend fun getRetirementRequestInfo(filters: List<ApiFilterDN>): Flow<List<RetirementRequestDN>> =
        error("not used in DisabilityPensionViewModel")
    override suspend fun checkRetirementStatus(): Flow<RetirementStatusDN> =
        error("not used in DisabilityPensionViewModel")
    override suspend fun sendRetirementDocument(requestId: String, request: RetirementSaveDocumentDN): Flow<String?> =
        error("not used in DisabilityPensionViewModel")
    override suspend fun authenticationAndGetPersonalInfo(authenticationsCode: Long): Flow<RetirementPersonalDN> =
        error("not used in DisabilityPensionViewModel")
    override suspend fun getAuthenticationCode(): Flow<AuthenticationTicketDN> =
        error("not used in DisabilityPensionViewModel")
    override suspend fun sendEdictPensionerToMyInbox(filters: List<ApiFilterDN>): Flow<EdictPensionerInboxDN> =
        error("not used in DisabilityPensionViewModel")
    override suspend fun sendPayRollToInbox(filters: List<ApiFilterDN>): Flow<PayRollInboxDN> =
        error("not used in DisabilityPensionViewModel")
    override suspend fun sendRequestInquirePensionCertificate(filters: List<ApiFilterDN>): Flow<InquirePensionCertificateDN> =
        error("not used in DisabilityPensionViewModel")
    override suspend fun saveDisabilityUserInfo(body: DisabilitySaveInfoDN): Flow<DisabilityRequestRefDN?> =
        error("not used in DisabilityPensionViewModel")
    override suspend fun finalConfirmDisabilityRequest(requestId: Long, body: DisabilityFinalConfirmDN): Flow<String?> =
        error("not used in DisabilityPensionViewModel")
    override suspend fun saveDocumentDisability(requestId: Long, body: DisabilitySaveDocumentDN): Flow<String?> =
        error("not used in DisabilityPensionViewModel")
    override suspend fun getMedicalCommissionPdf(lastWorkshop: String): Flow<PdfDownloadDN> =
        error("not used in DisabilityPensionViewModel")
    override suspend fun getRegisteredMedicalCommission(filters: List<ApiFilterDN>): Flow<List<RegisteredMedicalCommissionDN>> =
        error("not used in DisabilityPensionViewModel")
}

private class FakeDisabilityPersonalRepository : PersonalRepository {
    var dependentInfoResult: List<DisabilityDependentDN> = emptyList()

    override fun getDisabilityDependentInfo(filters: List<ApiFilterDN>): Flow<List<DisabilityDependentDN>> = flow {
        emit(dependentInfoResult)
    }

    override fun getPersonalInfo(refreshRemote: Boolean): Flow<PersonalInfoDN?> =
        error("not used in DisabilityPensionViewModel")
    override fun getDeceasedInfo(nationalId: String): Flow<DeceasedInfoDN> =
        error("not used in DisabilityPensionViewModel")
    override fun getAge(birthDate: Long): Flow<AgeDN> =
        error("not used in DisabilityPensionViewModel")
    override fun getSurvivorList(deceasedNationalId: String): Flow<List<SurvivorDependentDN>> =
        error("not used in DisabilityPensionViewModel")
    override fun checkGirlSurvivorConditions(nationalCode: String, pensionerId: String): Flow<GirlSurvivorConditionDN> =
        error("not used in DisabilityPensionViewModel")
    override fun getConfirmSurvivorsList(filters: List<ApiFilterDN>): Flow<List<ConfirmSurvivorDN>> =
        error("not used in DisabilityPensionViewModel")
    override fun submitFinalSurvivorPension(requestId: Int, body: SubmitFinalSurvivorPensionDN): Flow<String?> =
        error("not used in DisabilityPensionViewModel")
    override fun saveSurvivorInfo(body: SaveSurvivorInfoDN): Flow<String?> =
        error("not used in DisabilityPensionViewModel")
    override fun getFinalSurvivorPensionPDF(): Flow<PdfDownloadDN> =
        error("not used in DisabilityPensionViewModel")
    override fun getGirlSurvivorReport(params: GirlSurvivorReportParamsDN): Flow<PdfDownloadDN> =
        error("not used in DisabilityPensionViewModel")
    override fun confirmGirlSurvivor(body: ConfirmGirlSurvivorDN): Flow<String?> =
        error("not used in DisabilityPensionViewModel")
    override fun putInsuredRegistrationDocList(personalId: String, docs: List<InsuredDocDN>): Flow<String?> =
        error("not used in DisabilityPensionViewModel")
    override fun getRequestSummary(requestId: String): Flow<NewInsuredSummaryDN?> =
        error("not used in DisabilityPensionViewModel")
}

private class FakeDisabilityAddDependentRepository : AddDependentRepository {
    var refreshDependentsResult: GeneralResultDN = GeneralResultDN()
    var refreshDependentsCallCount: Int = 0
    var refreshDependentsGate: CompletableDeferred<Unit>? = null

    override fun refreshDependents(): Flow<GeneralResultDN> = flow {
        refreshDependentsCallCount++
        refreshDependentsGate?.await()
        emit(refreshDependentsResult)
    }

    override fun getDependentInfo(): Flow<List<DependentInfoDN>> =
        error("not used in DisabilityPensionViewModel")
    override fun getActiveBranches(): Flow<List<BranchDN>> =
        error("not used in DisabilityPensionViewModel")
    override fun getFamilyRelationships(filter: List<ApiFilterDN>): Flow<List<FamilyRelationshipDN>> =
        error("not used in DisabilityPensionViewModel")
    override fun getFamilyRelationshipsFromProxy(filter: List<ApiFilterDN>): Flow<List<FamilyRelationshipDN>> =
        error("not used in DisabilityPensionViewModel")
    override fun inquiryRegistry(dependentNationalId: String, birthDateTimeStamp: String, dependencyCode: String): Flow<RegistryDataDN> =
        error("not used in DisabilityPensionViewModel")
    override fun inquiryEducationCode(nationalId: String, educationCode: String): Flow<String> =
        error("not used in DisabilityPensionViewModel")
    override fun uploadImage(imageBytes: ByteArray, fileName: String, mimeType: String): Flow<UploadImageDN> =
        error("not used in DisabilityPensionViewModel")
    override fun addNewDependent(request: RequestAddDependentDN): Flow<GeneralResultDN> =
        error("not used in DisabilityPensionViewModel")
}
