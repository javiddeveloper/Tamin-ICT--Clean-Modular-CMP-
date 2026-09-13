package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.AddressError
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionEvent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionIntent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionStep
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.LandlinePhoneError
import com.tamin.taminhamrah.model.addDependent.BranchDN
import com.tamin.taminhamrah.model.addDependent.DependentInfoDN
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipDN
import com.tamin.taminhamrah.model.addDependent.GeneralResultDN
import com.tamin.taminhamrah.model.addDependent.RegistryDataDN
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDN
import com.tamin.taminhamrah.model.addDependent.UploadImageDN
import com.tamin.taminhamrah.model.contracts.BranchDN as ContractsBranchDN
import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.contracts.FreeJobDN
import com.tamin.taminhamrah.model.contracts.FreelanceCalculateSalaryParams
import com.tamin.taminhamrah.model.contracts.FreelanceContractByGuardianParams
import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDN
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractParams
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeDN
import com.tamin.taminhamrah.model.contracts.FreelancePremiumRangeParams
import com.tamin.taminhamrah.model.contracts.InsurancePaymentDN
import com.tamin.taminhamrah.model.contracts.InsurancePaymentParamsDN
import com.tamin.taminhamrah.model.contracts.OptionalContractByGuardianParams
import com.tamin.taminhamrah.model.contracts.PremiumRateDN
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDN
import com.tamin.taminhamrah.model.contracts.SaveContactRequestDN
import com.tamin.taminhamrah.model.contracts.UploadImageRequestDN
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
import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.model.history.HistoryJobInfoDN
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.history.UserInfoDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentCertificateDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentRequestDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementPersonalDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementSaveDocumentDN
import com.tamin.taminhamrah.model.pension.retirementInfo.RetirementRequestDN
import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.DisabilityDependentDN
import com.tamin.taminhamrah.model.personal.DisabilityPersonalDN
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
import com.tamin.taminhamrah.repository.HistoryRepository
import com.tamin.taminhamrah.repository.addDependent.AddDependentRepository
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import com.tamin.taminhamrah.repository.pension.PensionRepository
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import com.tamin.taminhamrah.useCases.addDependent.RefreshDependentsUseCase
import com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase
import com.tamin.taminhamrah.useCases.history.GetTalfighInfosUseCase
import com.tamin.taminhamrah.useCases.pension.FinalConfirmDisabilityRequestUseCase
import com.tamin.taminhamrah.useCases.pension.GetDisabilityPersonalInfoUseCase
import com.tamin.taminhamrah.useCases.pension.GetMedicalCommissionPdfUseCase
import com.tamin.taminhamrah.useCases.pension.GetRegisteredMedicalCommissionUseCase
import com.tamin.taminhamrah.useCases.pension.GetUserAgeUseCase
import com.tamin.taminhamrah.useCases.pension.SaveDisabilityUserInfoUseCase
import com.tamin.taminhamrah.useCases.pension.SaveDocumentDisabilityUseCase
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
    private lateinit var historyRepository: FakeDisabilityHistoryRepository
    private lateinit var contractsRepository: FakeDisabilityContractsRepository
    private lateinit var viewModel: DisabilityPensionViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        pensionRepository = FakeDisabilityPensionRepository()
        personalRepository = FakeDisabilityPersonalRepository()
        addDependentRepository = FakeDisabilityAddDependentRepository()
        historyRepository = FakeDisabilityHistoryRepository()
        contractsRepository = FakeDisabilityContractsRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = DisabilityPensionViewModel(
        getDisabilityPersonalInfoUseCase = GetDisabilityPersonalInfoUseCase(pensionRepository),
        getDisabilityDependentInfoUseCase = GetDisabilityDependentInfoUseCase(personalRepository),
        refreshDependentsUseCase = RefreshDependentsUseCase(addDependentRepository),
        getUserAgeUseCase = GetUserAgeUseCase(pensionRepository),
        getTalfighInfosUseCase = GetTalfighInfosUseCase(historyRepository),
        getRegisteredMedicalCommissionUseCase = GetRegisteredMedicalCommissionUseCase(pensionRepository),
        getMedicalCommissionPdfUseCase = GetMedicalCommissionPdfUseCase(pensionRepository),
        uploadImageUseCase = UploadImageUseCase(contractsRepository),
        saveDisabilityUserInfoUseCase = SaveDisabilityUserInfoUseCase(pensionRepository),
        saveDocumentDisabilityUseCase = SaveDocumentDisabilityUseCase(pensionRepository),
        finalConfirmDisabilityRequestUseCase = FinalConfirmDisabilityRequestUseCase(pensionRepository),
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

    @Test
    fun whenInitLoads_identityInfoAndAgeAreResolvedFromPersonalInfoAndUserAgeUseCase() = runTest(testDispatcher) {
        pensionRepository.disabilityPersonalInfoResult = DisabilityPersonalInfoDN(
            branch = null,
            branchName = null,
            confirmed = null,
            insuranceId = "0019273648",
            mobileNumber = "09143018372",
            personal = DisabilityPersonalDN(
                firstName = "رضا",
                lastName = "دریکوند",
                nationalId = "4060434061",
                fatherName = "علی‌محمد",
                idCardNumber = "158",
                cityOfIssue = "مشهد",
                dateOfBirth = 400000000000L,
                genderDesc = "مرد",
            ),
            provinceName = null,
            work = null,
            yearsAge = null,
            monthsAge = null,
            daysAge = null,
            strAge = null,
        )
        pensionRepository.userAgeResult = AgeDN(age = "42,3,10", birthDate = null)
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("0019273648", state.identityInfo?.insuranceId)
        assertEquals("رضا", state.identityInfo?.personal?.firstName)
        assertEquals("42", state.identityAgeYears)
    }

    @Test
    fun whenIdentityContactNextClickedWithBlankFields_showsValidationErrorsAndDoesNotConfirm() = runTest(testDispatcher) {
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        advanceToIdentityContactStep()

        viewModel.uiState.test {
            viewModel.sendIntent(DisabilityPensionIntent.NextStepClicked)
            var state = awaitItem()
            while (state.landlinePhoneError == null) state = awaitItem()

            assertEquals(LandlinePhoneError.Blank, state.landlinePhoneError)
            assertEquals(AddressError.Blank, state.addressError)
            assertFalse(state.showIdentityConfirmationError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenIdentityContactFieldsValidButNotConfirmed_showsConfirmationError() = runTest(testDispatcher) {
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        advanceToIdentityContactStep()
        viewModel.sendIntent(DisabilityPensionIntent.LandlinePhoneChanged("05832245678"))
        viewModel.sendIntent(DisabilityPensionIntent.AddressChanged("مشهد، بلوار وکیل‌آباد، نبش وکیل‌آباد ۵۲"))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            viewModel.sendIntent(DisabilityPensionIntent.NextStepClicked)
            var state = awaitItem()
            while (!state.showIdentityConfirmationError) state = awaitItem()

            assertEquals(null, state.landlinePhoneError)
            assertEquals(null, state.addressError)
            assertTrue(state.showIdentityConfirmationError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenToggleIdentityDetails_flipsExpandedState() = runTest(testDispatcher) {
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(DisabilityPensionIntent.ToggleIdentityDetails)
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isIdentityDetailsExpanded)

        viewModel.sendIntent(DisabilityPensionIntent.ToggleIdentityDetails)
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isIdentityDetailsExpanded)
    }

    @Test
    fun whenSummaryNotConfirmedAndSubmitClicked_showsConfirmationErrorAndStaysOnSummary() = runTest(testDispatcher) {
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        advanceToSummaryStep()

        viewModel.uiState.test {
            viewModel.sendIntent(DisabilityPensionIntent.NextStepClicked)
            var state = awaitItem()
            while (!state.showFinalConfirmationError) state = awaitItem()

            assertTrue(state.showFinalConfirmationError)
            assertEquals(DisabilityPensionStep.Summary, state.currentStep)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenSummaryConfirmedAndSubmitClicked_runsThreeCallChainAndShowsTrackingCode() = runTest(testDispatcher) {
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        advanceToSummaryStep()
        viewModel.sendIntent(DisabilityPensionIntent.FinalConfirmedChanged(true))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            viewModel.sendIntent(DisabilityPensionIntent.NextStepClicked)
            var state = awaitItem()
            while (state.submitTrackingCode == null) state = awaitItem()

            assertEquals("3829147205", state.submitTrackingCode)
            assertFalse(state.isSubmitting)
            cancelAndIgnoreRemainingEvents()
        }

        assertEquals(555L, pensionRepository.lastSaveDocumentDisabilityRequestId)
        assertEquals(555L, pensionRepository.lastFinalConfirmRequestId)
        assertEquals("0", pensionRepository.lastFinalConfirmBody?.status)

        viewModel.events.test {
            viewModel.sendIntent(DisabilityPensionIntent.SubmitSuccessAcknowledged)
            val event = awaitItem()
            assertIs<DisabilityPensionEvent.NavigateBack>(event)
        }
    }

    @Test
    fun whenSaveDisabilityUserInfoFails_showsToastAndStopsSubmitting() = runTest(testDispatcher) {
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        advanceToSummaryStep()
        viewModel.sendIntent(DisabilityPensionIntent.FinalConfirmedChanged(true))
        testDispatcher.scheduler.advanceUntilIdle()
        pensionRepository.saveDisabilityUserInfoError = RuntimeException("save failed")

        viewModel.events.test {
            viewModel.sendIntent(DisabilityPensionIntent.NextStepClicked)
            val event = awaitItem()
            assertIs<DisabilityPensionEvent.ShowToast>(event)
        }
        assertFalse(viewModel.uiState.value.isSubmitting)
        assertEquals(null, viewModel.uiState.value.submitTrackingCode)
    }

    @Test
    fun whenSaveDocumentDisabilityFails_showsToastAndStopsSubmitting() = runTest(testDispatcher) {
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        advanceToSummaryStep()
        viewModel.sendIntent(DisabilityPensionIntent.FinalConfirmedChanged(true))
        testDispatcher.scheduler.advanceUntilIdle()
        pensionRepository.saveDocumentDisabilityError = RuntimeException("upload failed")

        viewModel.events.test {
            viewModel.sendIntent(DisabilityPensionIntent.NextStepClicked)
            val event = awaitItem()
            assertIs<DisabilityPensionEvent.ShowToast>(event)
        }
        assertFalse(viewModel.uiState.value.isSubmitting)
        assertEquals(null, viewModel.uiState.value.submitTrackingCode)
    }

    @Test
    fun whenFinalConfirmDisabilityRequestFails_showsToastAndStopsSubmitting() = runTest(testDispatcher) {
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        advanceToSummaryStep()
        viewModel.sendIntent(DisabilityPensionIntent.FinalConfirmedChanged(true))
        testDispatcher.scheduler.advanceUntilIdle()
        pensionRepository.finalConfirmError = RuntimeException("confirm failed")

        viewModel.events.test {
            viewModel.sendIntent(DisabilityPensionIntent.NextStepClicked)
            val event = awaitItem()
            assertIs<DisabilityPensionEvent.ShowToast>(event)
        }
        assertFalse(viewModel.uiState.value.isSubmitting)
        assertEquals(null, viewModel.uiState.value.submitTrackingCode)
    }

    private suspend fun advanceToIdentityContactStep() {
        viewModel.sendIntent(DisabilityPensionIntent.TermsAcceptedChanged(true))
        viewModel.sendIntent(DisabilityPensionIntent.NextStepClicked)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.sendIntent(DisabilityPensionIntent.DependentsListConfirmedChanged(true))
        viewModel.sendIntent(DisabilityPensionIntent.NextStepClicked)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    private suspend fun advanceToCommissionRecordStep() {
        advanceToIdentityContactStep()
        viewModel.sendIntent(DisabilityPensionIntent.LandlinePhoneChanged("05832245678"))
        viewModel.sendIntent(DisabilityPensionIntent.AddressChanged("مشهد، بلوار وکیل‌آباد، نبش وکیل‌آباد ۵۲"))
        viewModel.sendIntent(DisabilityPensionIntent.IdentityConfirmedChanged(true))
        viewModel.sendIntent(DisabilityPensionIntent.NextStepClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(DisabilityPensionIntent.WorkshopNameChanged("کارگاه تست"))
        viewModel.sendIntent(DisabilityPensionIntent.WorkshopAddressChanged("مشهد، شهرک صنعتی توس"))
        viewModel.sendIntent(DisabilityPensionIntent.WorkshopConfirmedChanged(true))
        viewModel.sendIntent(DisabilityPensionIntent.NextStepClicked)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    private suspend fun advanceToSummaryStep() {
        advanceToCommissionRecordStep()
        viewModel.sendIntent(DisabilityPensionIntent.CommissionObjectionChanged(false))
        viewModel.sendIntent(DisabilityPensionIntent.NextStepClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(DisabilityPensionIntent.NextStepClicked)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.sendIntent(DisabilityPensionIntent.ConfirmDocumentsSubmission)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @Test
    fun whenCommissionRecordNextClickedWithoutAnswer_showsValidationErrorAndStaysOnStep() = runTest(testDispatcher) {
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        advanceToCommissionRecordStep()

        viewModel.uiState.test {
            viewModel.sendIntent(DisabilityPensionIntent.NextStepClicked)
            var state = awaitItem()
            while (!state.showCommissionValidationError) state = awaitItem()

            assertTrue(state.showCommissionValidationError)
            assertEquals(DisabilityPensionStep.CommissionRecord, state.currentStep)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenCommissionRecordNextClickedWithObjection_staysOnStep() = runTest(testDispatcher) {
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        advanceToCommissionRecordStep()

        viewModel.sendIntent(DisabilityPensionIntent.CommissionObjectionChanged(true))

        viewModel.uiState.test {
            viewModel.sendIntent(DisabilityPensionIntent.NextStepClicked)
            val state = expectMostRecentItem()
            
            assertEquals(DisabilityPensionStep.CommissionRecord, state.currentStep)
            assertFalse(state.showCommissionValidationError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenCommissionRecordAnsweredNo_advancesToDocumentsStep() = runTest(testDispatcher) {
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        advanceToCommissionRecordStep()
        viewModel.sendIntent(DisabilityPensionIntent.CommissionObjectionChanged(false))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            viewModel.sendIntent(DisabilityPensionIntent.NextStepClicked)
            var state = awaitItem()
            while (state.currentStep != DisabilityPensionStep.Documents) state = awaitItem()

            assertEquals(DisabilityPensionStep.Documents, state.currentStep)
            assertFalse(state.showCommissionValidationError)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenEditSummarySectionClicked_jumpsToStepAndMarksEditingFromSummary() = runTest(testDispatcher) {
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        advanceToSummaryStep()

        viewModel.sendIntent(DisabilityPensionIntent.EditSummarySectionClicked(DisabilityPensionStep.Workshop))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(DisabilityPensionStep.Workshop, state.currentStep)
        assertTrue(state.isEditingFromSummary)
    }

    @Test
    fun whenEditingWorkshopFromSummaryAndNextClicked_returnsDirectlyToSummaryInsteadOfCommissionRecord() = runTest(testDispatcher) {
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        advanceToSummaryStep()
        viewModel.sendIntent(DisabilityPensionIntent.EditSummarySectionClicked(DisabilityPensionStep.Workshop))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            viewModel.sendIntent(DisabilityPensionIntent.NextStepClicked)
            var state = awaitItem()
            while (state.currentStep == DisabilityPensionStep.Workshop) state = awaitItem()

            assertEquals(DisabilityPensionStep.Summary, state.currentStep)
            assertFalse(state.isEditingFromSummary)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenEditingIdentityContactFromSummaryAndPreviousClicked_returnsDirectlyToSummaryInsteadOfDependents() = runTest(testDispatcher) {
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        advanceToSummaryStep()
        viewModel.sendIntent(DisabilityPensionIntent.EditSummarySectionClicked(DisabilityPensionStep.IdentityContact))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            viewModel.sendIntent(DisabilityPensionIntent.PreviousStepClicked)
            var state = awaitItem()
            while (state.currentStep == DisabilityPensionStep.IdentityContact) state = awaitItem()

            assertEquals(DisabilityPensionStep.Summary, state.currentStep)
            assertFalse(state.isEditingFromSummary)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenEditingCommissionRecordFromSummaryAndBlockedByObjection_previousClickedStillReturnsToSummary() = runTest(testDispatcher) {
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        advanceToSummaryStep()
        viewModel.sendIntent(DisabilityPensionIntent.EditSummarySectionClicked(DisabilityPensionStep.CommissionRecord))
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.sendIntent(DisabilityPensionIntent.CommissionObjectionChanged(true))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            viewModel.sendIntent(DisabilityPensionIntent.PreviousStepClicked)
            var state = awaitItem()
            while (state.currentStep == DisabilityPensionStep.CommissionRecord) state = awaitItem()

            assertEquals(DisabilityPensionStep.Summary, state.currentStep)
            assertFalse(state.isEditingFromSummary)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

internal class FakeDisabilityPensionRepository : PensionRepository {
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

    var userAgeResult: AgeDN = AgeDN(age = "42,3,10", birthDate = null)
    var lastUserAgeFilters: List<ApiFilterDN>? = null

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
    override suspend fun getUserAge(filters: List<ApiFilterDN>): Flow<AgeDN> = flow {
        lastUserAgeFilters = filters
        emit(userAgeResult)
    }
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
    var saveDisabilityUserInfoResult: DisabilityRequestRefDN? = DisabilityRequestRefDN(id = 555L, refCode = "9999999999")
    var saveDisabilityUserInfoError: Throwable? = null
    var lastSaveDisabilityUserInfoBody: DisabilitySaveInfoDN? = null
    var lastSaveDocumentDisabilityRequestId: Long? = null
    var lastSaveDocumentDisabilityBody: DisabilitySaveDocumentDN? = null
    var saveDocumentDisabilityError: Throwable? = null
    var lastFinalConfirmRequestId: Long? = null
    var lastFinalConfirmBody: DisabilityFinalConfirmDN? = null
    var finalConfirmResult: DisabilityRequestRefDN? = DisabilityRequestRefDN(id = 555L, refCode = "3829147205")
    var finalConfirmError: Throwable? = null

    override suspend fun saveDisabilityUserInfo(body: DisabilitySaveInfoDN): Flow<DisabilityRequestRefDN?> = flow {
        lastSaveDisabilityUserInfoBody = body
        saveDisabilityUserInfoError?.let { throw it }
        emit(saveDisabilityUserInfoResult)
    }
    override suspend fun finalConfirmDisabilityRequest(requestId: Long, body: DisabilityFinalConfirmDN): Flow<DisabilityRequestRefDN?> = flow {
        lastFinalConfirmRequestId = requestId
        lastFinalConfirmBody = body
        finalConfirmError?.let { throw it }
        emit(finalConfirmResult)
    }
    override suspend fun saveDocumentDisability(requestId: Long, body: DisabilitySaveDocumentDN): Flow<String?> = flow {
        lastSaveDocumentDisabilityRequestId = requestId
        lastSaveDocumentDisabilityBody = body
        saveDocumentDisabilityError?.let { throw it }
        emit(null)
    }
    override suspend fun getMedicalCommissionPdf(lastWorkshop: String): Flow<PdfDownloadDN> =
        error("not used in DisabilityPensionViewModel")
    override suspend fun getRegisteredMedicalCommission(filters: List<ApiFilterDN>): Flow<List<RegisteredMedicalCommissionDN>> =
        error("not used in DisabilityPensionViewModel")
}

internal class FakeDisabilityPersonalRepository : PersonalRepository {
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

internal class FakeDisabilityAddDependentRepository : AddDependentRepository {
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
    override suspend fun createRetirementRequest(authenticationsCode: Long, form: com.tamin.taminhamrah.model.pension.retirement.RetirementRequestFormDN): Flow<com.tamin.taminhamrah.model.pension.retirement.RetirementRequestCreatedDN> =
        error("not used in DisabilityPensionViewModel")
}

internal class FakeDisabilityHistoryRepository : HistoryRepository {
    override suspend fun getUserRole(): com.tamin.taminhamrah.model.history.UserRoleDN = error("")
    override suspend fun sendHistoryNotice(): String? = error("")
    override fun downloadHistoryReport(type: com.tamin.taminhamrah.model.history.HistoryCertificateType): Flow<com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN> = error("")
    var talfighInfosResult: TalfighInfoDN = TalfighInfoDN(list = emptyList(), total = 0)

    override suspend fun getTalfighInfos(filters: List<ApiFilterDN>): TalfighInfoDN = talfighInfosResult

    override suspend fun getDastmozdInfos(filters: List<ApiFilterDN>): DastmozdInfoDN =
        error("not used in DisabilityPensionViewModel")
    override suspend fun getUserInfos(): UserInfoDN =
        error("not used in DisabilityPensionViewModel")
    override suspend fun sendToInstitution(selectedTypes: Set<HistoryCertificateType>): Unit =
        error("not used in DisabilityPensionViewModel")
    override suspend fun getHistoryJobInfos(filters: List<ApiFilterDN>): Flow<HistoryJobInfoDN> =
        error("not used in DisabilityPensionViewModel")
}

/**
 * Only [uploadImage] is exercised by [com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase]
 * — the rest of [ContractsRepository] is unrelated to disability pension and stubbed to satisfy the interface.
 */
internal class FakeDisabilityContractsRepository : ContractsRepository {
    var uploadImageResult: String = "uploaded-guid"
    var shouldThrowOnUpload = false
    var uploadError: Throwable = RuntimeException("upload failed")

    override fun uploadImage(request: UploadImageRequestDN): Flow<String> = flow {
        if (shouldThrowOnUpload) throw uploadError
        emit(uploadImageResult)
    }

    override fun getContracts(page: Int): Flow<com.tamin.taminhamrah.model.util.PagedListDN<ContractDN>> =
        error("not used in DisabilityPensionViewModel")
    override fun getContractsByPremiumType(premiumTypeCode: String, page: Int): Flow<com.tamin.taminhamrah.model.util.PagedListDN<ContractDN>> =
        error("not used in DisabilityPensionViewModel")
    override fun getStudentInsuranceContracts(page: Int): Flow<com.tamin.taminhamrah.model.util.PagedListDN<ContractDN>> =
        error("not used in DisabilityPensionViewModel")
    override fun getRegistrationInfo(): Flow<RegistrationInfoDN> =
        error("not used in DisabilityPensionViewModel")
    override fun getBranches(cityCode: String, page: Int): Flow<com.tamin.taminhamrah.model.util.PagedListDN<com.tamin.taminhamrah.model.contracts.BranchDN>> =
        error("not used in DisabilityPensionViewModel")
    override fun getSpcPremiumRates(): Flow<List<PremiumRateDN>> =
        error("not used in DisabilityPensionViewModel")
    override fun getFreeJobWages(page: Int, searchQuery: String?): Flow<com.tamin.taminhamrah.model.util.PagedListDN<FreeJobDN>> =
        error("not used in DisabilityPensionViewModel")
    override fun getFreelancePremiumRange(params: FreelancePremiumRangeParams): Flow<FreelancePremiumRangeDN> =
        error("not used in DisabilityPensionViewModel")
    override fun getOptionalPremiumRange(): Flow<FreelancePremiumRangeDN> =
        error("not used in DisabilityPensionViewModel")
    override fun checkRedCrossStatus(): Flow<String> =
        error("not used in DisabilityPensionViewModel")
    override fun checkMedicalStudent(): Flow<String> =
        error("not used in DisabilityPensionViewModel")
    override fun calculateFreelanceSalary(params: FreelanceCalculateSalaryParams): Flow<Long> =
        error("not used in DisabilityPensionViewModel")
    override fun calculateOptionalSalary(premiumRateCode: String): Flow<Long> =
        error("not used in DisabilityPensionViewModel")
    override fun makeFreelanceContract(params: FreelanceMakeContractParams): Flow<FreelanceContractResultDN> =
        error("not used in DisabilityPensionViewModel")
    override fun makeContract(params: FreelanceMakeContractParams): Flow<FreelanceContractResultDN> =
        error("not used in DisabilityPensionViewModel")
    override fun updateFreelanceContract(params: FreelanceMakeContractParams): Flow<Unit> = error("")
    override fun updateOptionalContract(premium: Long): Flow<Unit> = error("")
    override fun updateFreelanceContractByGuardian(params: FreelanceContractByGuardianParams): Flow<Unit> = error("")
    override fun updateOptionalContractByGuardian(params: OptionalContractByGuardianParams): Flow<Unit> = error("")
    override fun makeFreelanceContractByGuardian(
        params: FreelanceContractByGuardianParams,
    ): Flow<FreelanceContractResultDN> = error("not used in DisabilityPensionViewModel")
    override fun makeOptionalContractByGuardian(
        params: OptionalContractByGuardianParams,
    ): Flow<FreelanceContractResultDN> = error("not used in DisabilityPensionViewModel")
    override fun getInsurancePayment(params: InsurancePaymentParamsDN): Flow<InsurancePaymentDN> =
        error("not used in DisabilityPensionViewModel")
    override fun checkInsurancePaymentStatus(systemType: String): Flow<Any?> =
        error("not used in DisabilityPensionViewModel")
    override fun saveContact(request: SaveContactRequestDN): Flow<Any?> =
        error("not used in DisabilityPensionViewModel")
}
