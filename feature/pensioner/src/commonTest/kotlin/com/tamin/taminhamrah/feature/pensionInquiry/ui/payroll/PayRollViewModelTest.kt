package com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.contract.PayRollEvent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.contract.PayRollIntent
import com.tamin.taminhamrah.model.pension.EdictPensionerDN
import com.tamin.taminhamrah.model.pension.EdictPensionerInboxDN
import com.tamin.taminhamrah.model.pension.InquirePensionCertificateDN
import com.tamin.taminhamrah.model.pension.PayRollDN
import com.tamin.taminhamrah.model.pension.PayRollInboxDN
import com.tamin.taminhamrah.model.pension.PaymentTypeDN
import com.tamin.taminhamrah.model.pension.PensionIdDN
import com.tamin.taminhamrah.model.pension.PensionInquiryDN
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
import com.tamin.taminhamrah.repository.pension.PensionRepository
import com.tamin.taminhamrah.useCases.pension.GetPensionerIdUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerPayRollPDFUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerPayRollUseCase
import com.tamin.taminhamrah.useCases.pension.SendPayRollToInboxUseCase
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PayRollViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: FakePayRollPensionRepository
    private lateinit var viewModel: PayRollViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakePayRollPensionRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = PayRollViewModel(
        getPensionerPayRollUseCase = GetPensionerPayRollUseCase(repository),
        getPensionerPayRollPDFUseCase = GetPensionerPayRollPDFUseCase(repository),
        sendPayRollToInboxUseCase = SendPayRollToInboxUseCase(repository),
        getPensionerIdUseCase = GetPensionerIdUseCase(repository),
    )

    @Test
    fun whenPensionerIdsLoaded_autoLoadsPayRollForFirstPensioner() = runTest(testDispatcher) {
        repository.pensionIdResult = listOf(PensionIdDN(pensionerId = "123"))
        repository.payRollResult = listOf(
            PayRollDN(id = 1, tprDesc = "حقوق مرداد", sumAmount = 1_000_000L, hisYear = "1402", hisMon = "05")
        )
        viewModel = buildViewModel()

        viewModel.uiState.test {
            var state = awaitItem()
            while (!state.hasLoadedOnce || state.isLoading) state = awaitItem()

            assertEquals("123", state.selectedPensionerId)
            assertEquals(1, state.payRollList.size)
            assertEquals("حقوق مرداد", state.payRollList.first().tprDesc)
            cancelAndIgnoreRemainingEvents()
        }
    }

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
            viewModel.sendIntent(PayRollIntent.DismissNoPensionerDialog)
            val event = awaitItem()
            assertIs<PayRollEvent.NavigateBack>(event)
        }

        assertEquals(false, viewModel.uiState.value.showNoPensionerDialog)
    }

    @Test
    fun whenRequestSendToInbox_showsSendSuccess() = runTest(testDispatcher) {
        repository.pensionIdResult = listOf(PensionIdDN(pensionerId = "123"))
        repository.payRollResult = listOf(PayRollDN(id = 1))
        repository.sendPayRollToInboxResult = PayRollInboxDN(message = "ok")
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            viewModel.sendIntent(PayRollIntent.RequestSendToInbox)
            var state = awaitItem()
            while (!state.showSendSuccess) state = awaitItem()

            assertTrue(state.showSendSuccess)
            assertEquals(false, state.isSendingToInbox)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenLoadPayRollPDF_setsPayRollPDF() = runTest(testDispatcher) {
        repository.pensionIdResult = listOf(PensionIdDN(pensionerId = "123"))
        repository.payRollResult = listOf(PayRollDN(id = 1))
        repository.payRollPDFResult = PdfDownloadDN(pdf = null)
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            viewModel.sendIntent(PayRollIntent.LoadPayRollPDF)
            var state = awaitItem()
            while (state.payRollPDF == null) state = awaitItem()

            assertNotNull(state.payRollPDF)
            assertEquals(false, state.viewerDownloadFailed)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenGetPensionerIdFails_emitsErrorStateAndShowsToast() = runTest(testDispatcher) {
        repository.pensionIdShouldThrow = true
        repository.pensionIdError = RuntimeException("خطای شبکه")
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
            assertIs<PayRollEvent.ShowToast>(event)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenGetPensionerPayRollFails_emitsErrorStateAndShowsToast() = runTest(testDispatcher) {
        repository.pensionIdResult = listOf(PensionIdDN(pensionerId = "123"))
        repository.payRollShouldThrow = true
        repository.payRollError = RuntimeException("خطای فیش حقوقی")
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            var state = awaitItem()
            while (state.error == null) state = awaitItem()

            assertNotNull(state.error)
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.events.test {
            val event = awaitItem()
            assertIs<PayRollEvent.ShowToast>(event)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenApplySearch_updatesDateAndPaymentTypeAndReloadsPayRoll() = runTest(testDispatcher) {
        repository.pensionIdResult = listOf(PensionIdDN(pensionerId = "123"))
        repository.payRollResult = listOf(PayRollDN(id = 1, tprDesc = "حقوق فروردین"))
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendIntent(PayRollIntent.ChangeSearchYear("1401"))
        viewModel.sendIntent(PayRollIntent.ChangeSearchMonth("03"))
        viewModel.sendIntent(PayRollIntent.ChangeSearchPaymentType(PaymentTypeDN.BONUS.code))

        viewModel.uiState.test {
            viewModel.sendIntent(PayRollIntent.ApplySearch)
            var state = awaitItem()
            while (state.isLoading || state.startDate != "140103") state = awaitItem()

            assertEquals("140103", state.startDate)
            assertEquals(PaymentTypeDN.BONUS.code, state.paymentType)
            assertTrue(state.isDateFilteredBySearch)
            assertEquals(false, state.showSearchSheet)
            assertEquals(1, state.payRollList.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenClearDateFilter_resetsToDefaultDateAndPaymentType() = runTest(testDispatcher) {
        repository.pensionIdResult = listOf(PensionIdDN(pensionerId = "123"))
        repository.payRollResult = listOf(PayRollDN(id = 1))
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        val defaultDate = viewModel.uiState.value.startDate

        viewModel.sendIntent(PayRollIntent.ChangeSearchYear("1401"))
        viewModel.sendIntent(PayRollIntent.ChangeSearchMonth("03"))
        viewModel.sendIntent(PayRollIntent.ChangeSearchPaymentType(PaymentTypeDN.BONUS.code))
        viewModel.sendIntent(PayRollIntent.ApplySearch)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("140103", viewModel.uiState.value.startDate)

        viewModel.uiState.test {
            viewModel.sendIntent(PayRollIntent.ClearDateFilter)
            var state = awaitItem()
            while (state.isLoading || state.startDate != defaultDate) state = awaitItem()

            assertEquals(defaultDate, state.startDate)
            assertEquals(PaymentTypeDN.MONTHLY.code, state.paymentType)
            assertEquals(false, state.isDateFilteredBySearch)
            assertEquals(false, state.showSearchSheet)
            assertEquals(PaymentTypeDN.MONTHLY.code, state.searchPaymentType)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenLoadPayRollPDFFails_emitsViewerDownloadFailedAndShowsToast() = runTest(testDispatcher) {
        repository.pensionIdResult = listOf(PensionIdDN(pensionerId = "123"))
        repository.payRollResult = listOf(PayRollDN(id = 1))
        repository.payRollPDFShouldThrow = true
        repository.payRollPDFError = RuntimeException("خطای دانلود فیش")
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            viewModel.sendIntent(PayRollIntent.LoadPayRollPDF)
            var state = awaitItem()
            while (!state.viewerDownloadFailed) state = awaitItem()

            assertTrue(state.viewerDownloadFailed)
            assertNotNull(state.error)
            assertEquals(false, state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.events.test {
            val event = awaitItem()
            assertIs<PayRollEvent.ShowToast>(event)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenSendPayRollToInboxFails_emitsErrorStateAndShowsToast() = runTest(testDispatcher) {
        repository.pensionIdResult = listOf(PensionIdDN(pensionerId = "123"))
        repository.payRollResult = listOf(PayRollDN(id = 1))
        repository.sendToInboxShouldThrow = true
        repository.sendToInboxError = RuntimeException("خطای ارسال به صندوق پیام")
        viewModel = buildViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            viewModel.sendIntent(PayRollIntent.RequestSendToInbox)
            var state = awaitItem()
            while (state.error == null) state = awaitItem()

            assertNotNull(state.error)
            assertEquals(false, state.isSendingToInbox)
            assertEquals(false, state.showSendSuccess)
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.events.test {
            val event = awaitItem()
            assertIs<PayRollEvent.ShowToast>(event)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

private class FakePayRollPensionRepository : PensionRepository {

    var pensionIdResult: List<PensionIdDN> = emptyList()
    var payRollResult: List<PayRollDN> = emptyList()
    var payRollPDFResult: PdfDownloadDN? = null
    var sendPayRollToInboxResult: PayRollInboxDN = PayRollInboxDN(null)

    var pensionIdShouldThrow: Boolean = false
    var pensionIdError: Throwable = RuntimeException("fake pensionId error")

    var payRollShouldThrow: Boolean = false
    var payRollError: Throwable = RuntimeException("fake payRoll error")

    var payRollPDFShouldThrow: Boolean = false
    var payRollPDFError: Throwable = RuntimeException("fake payRollPDF error")

    var sendToInboxShouldThrow: Boolean = false
    var sendToInboxError: Throwable = RuntimeException("fake sendToInbox error")

    override suspend fun getPensionerId(): Flow<List<PensionIdDN>> = flow {
        if (pensionIdShouldThrow) throw pensionIdError
        emit(pensionIdResult)
    }

    override suspend fun getPensionerPayRoll(filters: List<ApiFilterDN>): Flow<List<PayRollDN>> = flow {
        if (payRollShouldThrow) throw payRollError
        emit(payRollResult)
    }

    override suspend fun pensionerPayRollPDF(filters: List<ApiFilterDN>): Flow<PdfDownloadDN> = flow {
        if (payRollPDFShouldThrow) throw payRollPDFError
        emit(payRollPDFResult ?: PdfDownloadDN())
    }

    override suspend fun sendPayRollToInbox(filters: List<ApiFilterDN>): Flow<PayRollInboxDN> = flow {
        if (sendToInboxShouldThrow) throw sendToInboxError
        emit(sendPayRollToInboxResult)
    }

    override suspend fun getPensionInquiry(filters: List<ApiFilterDN>): Flow<List<PensionInquiryDN>> =
        error("not used in PayRollViewModel")
    override suspend fun getEdictPensioner(query: ApiQueryParamDN): Flow<EdictPensionerDN?> =
        error("not used in PayRollViewModel")
    override suspend fun sendRequestDeferredInstallmentCertificate(request: DeferredInstallmentRequestDN): Flow<DeferredInstallmentCertificateDN> =
        error("not used in PayRollViewModel")
    override suspend fun getDisabilityPersonalInfo(): Flow<DisabilityPersonalInfoDN> =
        error("not used in PayRollViewModel")
    override suspend fun getUserAge(filters: List<ApiFilterDN>): Flow<AgeDN> =
        error("not used in PayRollViewModel")
    override suspend fun getEdictReportPDF(filters: List<ApiFilterDN>): Flow<PdfDownloadDN> =
        error("not used in PayRollViewModel")
    override suspend fun getRetirementRequestInfo(filters: List<ApiFilterDN>): Flow<List<RetirementRequestDN>> =
        error("not used in PayRollViewModel")
    override suspend fun checkRetirementStatus(): Flow<RetirementStatusDN> =
        error("not used in PayRollViewModel")
    override suspend fun sendRetirementDocument(requestId: String, request: RetirementSaveDocumentDN): Flow<String?> =
        error("not used in PayRollViewModel")
    override suspend fun authenticationAndGetPersonalInfo(authenticationsCode: Long): Flow<RetirementPersonalDN> =
        error("not used in PayRollViewModel")
    override suspend fun getAuthenticationCode(): Flow<AuthenticationTicketDN> =
        error("not used in PayRollViewModel")
    override suspend fun sendEdictPensionerToMyInbox(filters: List<ApiFilterDN>): Flow<EdictPensionerInboxDN> =
        error("not used in PayRollViewModel")
    override suspend fun sendRequestInquirePensionCertificate(filters: List<ApiFilterDN>): Flow<InquirePensionCertificateDN> =
        error("not used in PayRollViewModel")
}
