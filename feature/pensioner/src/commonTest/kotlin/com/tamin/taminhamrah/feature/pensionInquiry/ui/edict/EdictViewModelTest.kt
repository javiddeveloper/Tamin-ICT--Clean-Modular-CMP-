package com.tamin.taminhamrah.feature.pensionInquiry.ui.edict

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.contract.EdictEvent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.contract.EdictIntent
import com.tamin.taminhamrah.model.pension.EdictInfoDN
import com.tamin.taminhamrah.model.pension.EdictPensionerDN
import com.tamin.taminhamrah.model.pension.EdictPensionerInboxDN
import com.tamin.taminhamrah.model.pension.InquirePensionCertificateDN
import com.tamin.taminhamrah.model.pension.PensionIdDN
import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import com.tamin.taminhamrah.model.pension.PayRollDN
import com.tamin.taminhamrah.model.pension.PayRollInboxDN
import com.tamin.taminhamrah.model.pension.SurvivorInfoDN
import com.tamin.taminhamrah.model.pension.authenticationTicket.AuthenticationTicketDN
import com.tamin.taminhamrah.model.pension.checkRetirementStatus.RetirementStatusDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentCertificateDN
import com.tamin.taminhamrah.model.pension.installment.DeferredInstallmentRequestDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementPersonalDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementSaveDocumentDN
import com.tamin.taminhamrah.model.pension.retirementInfo.RetirementRequestDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilityFinalConfirmDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilityRequestRefDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveDocumentDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.DisabilitySaveInfoDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.medicalCommission.RegisteredMedicalCommissionDN
import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.DisabilityPersonalInfoDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import com.tamin.taminhamrah.useCases.pension.GetEdictPensionerUseCase
import com.tamin.taminhamrah.useCases.pension.GetEdictReportPDFUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerIdUseCase
import com.tamin.taminhamrah.useCases.pension.SendEdictPensionerToMyInboxUseCase
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
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class EdictViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: FakeEdictPensionRepository
    private lateinit var viewModel: EdictViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeEdictPensionRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = EdictViewModel(
        getEdictPensionerUseCase = GetEdictPensionerUseCase(repository),
        getPensionerIdUseCase = GetPensionerIdUseCase(repository),
        sendEdictPensionerToMyInboxUseCase = SendEdictPensionerToMyInboxUseCase(repository),
        getEdictReportPDFUseCase = GetEdictReportPDFUseCase(repository),
    )

    @Test
    fun whenEmptyPensionerList_showsNoPensionerDialog() = runTest(testDispatcher) {
        repository.pensionIdResult = emptyList()
        viewModel = buildViewModel()

        viewModel.uiState.test {
            var state = awaitItem()
            while (!state.showNoPensionerDialog) state = awaitItem()

            assertTrue(state.showNoPensionerDialog)
            assertEquals(false, state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenDismissNoPensionerDialog_hidesDialogAndEmitsNavigateBack() = runTest(testDispatcher) {
        repository.pensionIdResult = emptyList()
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.sendIntent(EdictIntent.DismissNoPensionerDialog)
            val event = awaitItem()
            assertIs<EdictEvent.NavigateBack>(event)
        }

        assertEquals(false, viewModel.uiState.value.showNoPensionerDialog)
    }

    @Test
    fun whenPensionerListLoaded_autoLoadsEdictForFirstPensioner() = runTest(testDispatcher) {
        repository.pensionIdResult = listOf(PensionIdDN(pensionerId = "123"))
        repository.edictPensionerResult = EdictPensionerDN(
            edictInfo = EdictInfoDN(
                pensionerId = "123",
                nationalCode = "1234567890",
                firstName = "علی",
                lastName = "محمدی",
                pensionBeforeIncrease = "5000000",
                pensionAfterIncrease = "5500000",
                payableMonthly = "5500000",
                fatherName = null, birthDate = null, idNumber = null, gender = null,
                insuranceType = null, pensionStartDate = null, originalHistoryYear = null,
                originalHistoryMonth = null, originalHistoryDay = null, additionalYear = null,
                additionalMonth = null, additionalDay = null, basisImplementation = null,
                edictDescription = null, id = null, firstStageTotalPensionAndProportional = null,
                totalPensionBeforeIncrease = null, firstStageTotalProportional = null,
                totalAmount = null, lettersPayableMonthly = null,
            )
        )
        viewModel = buildViewModel()

        viewModel.uiState.test {
            var state = awaitItem()
            while (state.edictPensioner == null || state.isLoading) state = awaitItem()

            assertNotNull(state.edictPensioner)
            assertNotNull(state.edictPensioner?.edictInfo)
            assertEquals("5500000", state.edictPensioner?.edictInfo?.payableMonthly)
            assertEquals("123", state.selectedPensionerId)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenSurvivorOnlyEdict_loadsEdictWithNullEdictInfoAndSurvivorPresent() = runTest(testDispatcher) {
        repository.pensionIdResult = listOf(PensionIdDN(pensionerId = "456"))
        repository.edictPensionerResult = EdictPensionerDN(
            edictInfo = null,
            survivorInfo = listOf(
                SurvivorInfoDN(
                    pensionAfterIncrease = "3000000",
                    previousPension = "2700000",
                    quota = "100",
                    nationalCode = "0987654321",
                    pensionerId = "456",
                    firstName = "فاطمه",
                    lastName = "احمدی",
                    originalHistoryDay = null, originalHistoryMonth = null,
                    originalHistoryYear = null, leniencyYear = null, leniencyMonth = null,
                    leniencyDay = null, edictDescription = null, id = null,
                    insuranceType = null, firstStageTotalPensionAndProportional = null,
                    firstStageTotalProportional = null,
                    differenceProportionalityBasedHistory = null, totalAmount = null,
                )
            )
        )
        viewModel = buildViewModel()

        viewModel.uiState.test {
            var state = awaitItem()
            while (state.edictPensioner == null || state.isLoading) state = awaitItem()

            val edict = state.edictPensioner
            assertNotNull(edict)
            assertNull(edict.edictInfo)
            assertTrue(edict.survivorInfo.isNotEmpty())
            assertEquals("3000000", edict.survivorInfo.first().pensionAfterIncrease)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenGetPensionerIdFails_emitsErrorStateAndShowsToast() = runTest(testDispatcher) {
        repository.shouldThrowError = true
        repository.error = RuntimeException("خطای شبکه")
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            var state = awaitItem()
            while (state.error == null) state = awaitItem()

            assertNotNull(state.error)
            assertEquals(false, state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.events.test {
            val event = awaitItem()
            assertIs<EdictEvent.ShowToast>(event)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenGetEdictFails_emitsErrorStateAndShowsToast() = runTest(testDispatcher) {
        repository.pensionIdResult = listOf(PensionIdDN(pensionerId = "123"))
        repository.edictShouldThrow = true
        repository.edictError = RuntimeException("خطای حکم")
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            var state = awaitItem()
            while (state.error == null) state = awaitItem()

            assertNotNull(state.error)
            assertEquals(false, state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.events.test {
            val event = awaitItem()
            assertIs<EdictEvent.ShowToast>(event)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

private class FakeEdictPensionRepository : PensionRepository {

    var pensionIdResult: List<PensionIdDN> = emptyList()
    var edictPensionerResult: EdictPensionerDN? = null
    var shouldThrowError: Boolean = false
    var error: Throwable = RuntimeException("fake error")
    var edictShouldThrow: Boolean = false
    var edictError: Throwable = RuntimeException("fake edict error")

    override suspend fun getPensionerId(): Flow<List<PensionIdDN>> = flow {
        if (shouldThrowError) throw error
        emit(pensionIdResult)
    }

    override suspend fun getEdictPensioner(query: ApiQueryParamDN): Flow<EdictPensionerDN?> = flow {
        if (edictShouldThrow) throw edictError
        emit(edictPensionerResult)
    }

    override suspend fun sendEdictPensionerToMyInbox(filters: List<ApiFilterDN>): Flow<EdictPensionerInboxDN> = flow {
        if (shouldThrowError) throw error
        emit(EdictPensionerInboxDN(message = null))
    }

    override suspend fun getEdictReportPDF(filters: List<ApiFilterDN>): Flow<PdfDownloadDN> = flow {
        if (shouldThrowError) throw error
        emit(PdfDownloadDN())
    }

    override suspend fun getPensionInquiry(filters: List<ApiFilterDN>): Flow<List<PensionInquiryDN>> =
        error("not used in EdictViewModel")
    override suspend fun sendRequestDeferredInstallmentCertificate(request: DeferredInstallmentRequestDN): Flow<DeferredInstallmentCertificateDN> =
        error("not used in EdictViewModel")
    override suspend fun getPensionerPayRoll(filters: List<ApiFilterDN>): Flow<List<PayRollDN>> =
        error("not used in EdictViewModel")
    override suspend fun getDisabilityPersonalInfo(): Flow<DisabilityPersonalInfoDN> =
        error("not used in EdictViewModel")
    override suspend fun getUserAge(filters: List<ApiFilterDN>): Flow<AgeDN> =
        error("not used in EdictViewModel")
    override suspend fun pensionerPayRollPDF(filters: List<ApiFilterDN>): Flow<PdfDownloadDN> =
        error("not used in EdictViewModel")
    override suspend fun getRetirementRequestInfo(filters: List<ApiFilterDN>): Flow<List<RetirementRequestDN>> =
        error("not used in EdictViewModel")
    override suspend fun checkRetirementStatus(): Flow<RetirementStatusDN> =
        error("not used in EdictViewModel")
    override suspend fun sendRetirementDocument(requestId: String, request: RetirementSaveDocumentDN): Flow<String?> =
        error("not used in EdictViewModel")
    override suspend fun authenticationAndGetPersonalInfo(authenticationsCode: Long): Flow<RetirementPersonalDN> =
        error("not used in EdictViewModel")
    override suspend fun sendPayRollToInbox(filters: List<ApiFilterDN>): Flow<PayRollInboxDN> =
        error("not used in EdictViewModel")
    override suspend fun getAuthenticationCode(): Flow<AuthenticationTicketDN> =
        error("not used in EdictViewModel")
    override suspend fun sendRequestInquirePensionCertificate(filters: List<ApiFilterDN>): Flow<InquirePensionCertificateDN> =
        error("not used in EdictViewModel")
    override suspend fun saveDisabilityUserInfo(body: DisabilitySaveInfoDN): Flow<DisabilityRequestRefDN?> =
        error("not used in EdictViewModel")
    override suspend fun finalConfirmDisabilityRequest(requestId: Long, body: DisabilityFinalConfirmDN): Flow<DisabilityRequestRefDN?> =
        error("not used in EdictViewModel")
    override suspend fun saveDocumentDisability(requestId: Long, body: DisabilitySaveDocumentDN): Flow<String?> =
        error("not used in EdictViewModel")
    override suspend fun getMedicalCommissionPdf(lastWorkshop: String): Flow<PdfDownloadDN> =
        error("not used in EdictViewModel")
    override suspend fun getRegisteredMedicalCommission(filters: List<ApiFilterDN>): Flow<List<RegisteredMedicalCommissionDN>> =
        error("not used in EdictViewModel")
}
