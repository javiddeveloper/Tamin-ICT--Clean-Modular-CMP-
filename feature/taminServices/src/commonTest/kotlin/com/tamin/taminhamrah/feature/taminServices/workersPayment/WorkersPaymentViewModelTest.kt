package com.tamin.taminhamrah.feature.taminServices.workersPayment

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.taminServices.workersPayment.contract.WorkersPaymentEvent
import com.tamin.taminhamrah.feature.taminServices.workersPayment.contract.WorkersPaymentIntent
import com.tamin.taminhamrah.feature.taminServices.workersPayment.model.WorkersPaymentInfoPR
import com.tamin.taminhamrah.model.workersPayment.WorkersPaymentInfoDN
import com.tamin.taminhamrah.model.workersPayment.WorkersPaymentInfoListDN
import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitResultDN
import com.tamin.taminhamrah.model.payment.PaymentVerifierKey
import com.tamin.taminhamrah.util.NetworkConstants
import com.tamin.taminhamrah.useCases.workersPayment.GetWorkersPaymentInfoUseCase
import com.tamin.taminhamrah.useCases.workersPayment.PayWorkersDebitUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class WorkersPaymentViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: FakeWorkersPaymentRepository
    private lateinit var viewModel: WorkersPaymentViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeWorkersPaymentRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = WorkersPaymentViewModel(
        getWorkersPaymentInfoUseCase = GetWorkersPaymentInfoUseCase(repository),
        payWorkersDebitUseCase = PayWorkersDebitUseCase(repository),
        savedStateHandle = SavedStateHandle(),
    )

    // ── init list load ──────────────────────────────────────────────────────

    @Test
    fun `init loads the payment info into state`() = runTest(testDispatcher) {
        repository.paymentInfoResult = WorkersPaymentInfoListDN(
            totalAmount = 8940000L,
            totalPenalty = 1341000L,
            totalPremium = 7599000L,
            total = 1,
            list = listOf(sampleDn()),
        )

        viewModel = buildViewModel()

        val state = viewModel.uiState.value
        assertEquals(1, state.items.size)
        assertEquals(8940000L, state.totalAmount)
        assertEquals(1341000L, state.totalPenalty)
        assertEquals(7599000L, state.totalPremium)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertEquals("05", state.items.first().month)
        assertEquals(WorkersPaymentInfoPR.Status.PAYABLE, state.items.first().status)
    }

    @Test
    fun `init load failure surfaces an error message`() = runTest(testDispatcher) {
        repository.shouldThrowError = true

        viewModel = buildViewModel()

        val state = viewModel.uiState.value
        assertNotNull(state.errorMessage)
        assertFalse(state.isLoading)
        assertTrue(state.items.isEmpty())
    }

    @Test
    fun `Retry after a failure re-fetches and emits a toast on repeated failure`() = runTest(testDispatcher) {
        repository.paymentInfoResult = WorkersPaymentInfoListDN(0L, 0L, 0L, 1, listOf(sampleDn()))
        viewModel = buildViewModel()
        assertEquals(1, viewModel.uiState.value.items.size)

        repository.shouldThrowError = true
        viewModel.events.test {
            viewModel.sendIntent(WorkersPaymentIntent.Retry)
            val event = assertIs<WorkersPaymentEvent.ShowToast>(awaitItem())
            assertTrue(event.message.isNotBlank())
            cancelAndIgnoreRemainingEvents()
        }
        assertNotNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `OnResumed when already loaded does not call repository again on theme change or resume`() = runTest(testDispatcher) {
        repository.paymentInfoResult = WorkersPaymentInfoListDN(0L, 0L, 0L, 1, listOf(sampleDn()))
        viewModel = buildViewModel()
        val initialCallCount = repository.getPaymentInfoCallCount

        viewModel.sendIntent(WorkersPaymentIntent.OnResumed)

        assertEquals(initialCallCount, repository.getPaymentInfoCallCount)
    }

    @Test
    fun `OnResumed when payment is in flight forces repository re-fetch and closes detail screen`() = runTest(testDispatcher) {
        repository.paymentInfoResult = WorkersPaymentInfoListDN(0L, 0L, 0L, 1, listOf(sampleDn()))
        viewModel = buildViewModel()
        val item = viewModel.uiState.value.items.first()
        viewModel.sendIntent(WorkersPaymentIntent.OpenPaymentScreen(item))
        assertEquals(item, viewModel.uiState.value.selectedPaymentItem)

        repository.payDebitResult = WorkersPayDebitResultDN(
            paymentUrl = "https://tfh.tamin.ir/view/#/payment/T-1",
            ticket = "T-1",
            paymentInfo = "enc-info",
        )
        viewModel.sendIntent(WorkersPaymentIntent.PayItem(item))
        val initialCallCount = repository.getPaymentInfoCallCount

        viewModel.sendIntent(WorkersPaymentIntent.OnResumed)

        assertEquals(initialCallCount + 1, repository.getPaymentInfoCallCount)
        assertNull(viewModel.uiState.value.selectedPaymentItem)
    }

    // ── screen 2 open / close ──────────────────────────────────────────────

    @Test
    fun `OpenPaymentScreen selects the item and ClosePaymentScreen clears it`() = runTest(testDispatcher) {
        repository.paymentInfoResult = WorkersPaymentInfoListDN(0L, 0L, 0L, 1, listOf(sampleDn()))
        viewModel = buildViewModel()
        val item = viewModel.uiState.value.items.first()

        viewModel.sendIntent(WorkersPaymentIntent.OpenPaymentScreen(item))
        assertEquals(item, viewModel.uiState.value.selectedPaymentItem)

        viewModel.sendIntent(WorkersPaymentIntent.ClosePaymentScreen)
        assertNull(viewModel.uiState.value.selectedPaymentItem)
    }

    // ── payDebit → shared payment ──────────────────────────────────────────

    @Test
    fun `PayItem with a gateway ticket emits NavigateToPayment with CONSTRUCTION_WORKERS verifier key`() = runTest(testDispatcher) {
        repository.paymentInfoResult = WorkersPaymentInfoListDN(0L, 0L, 0L, 1, listOf(sampleDn()))
        viewModel = buildViewModel()
        val item = viewModel.uiState.value.items.first()
        repository.payDebitResult = WorkersPayDebitResultDN(
            paymentUrl = "https://tfh.tamin.ir/view/#/payment/T-1",
            ticket = "T-1",
            paymentInfo = "enc-info",
        )

        viewModel.events.test {
            viewModel.sendIntent(WorkersPaymentIntent.PayItem(item))
            val event = assertIs<WorkersPaymentEvent.NavigateToPayment>(awaitItem())
            assertEquals("T-1", event.request.ticket)
            assertEquals(PaymentVerifierKey.CONSTRUCTION_WORKERS, event.request.verifierKey)
            assertEquals("T-1|enc-info", event.request.verifierReference)
            cancelAndIgnoreRemainingEvents()
        }

        val state = viewModel.uiState.value
        assertFalse(state.isProcessingPayment)

        val params = assertNotNull(repository.lastPayDebitParams)
        assertEquals(item.totalPayable, params.amount)
        assertEquals(listOf(item.fromDatePersian), params.dates)
        assertEquals(listOf(item.fromDateToDate), params.fromToDate)
        assertEquals(NetworkConstants.PAYMENT_RETURN_URI, params.redirectUrl)
    }

    @Test
    fun `PayItem with a blank ticket emits a toast`() = runTest(testDispatcher) {
        repository.paymentInfoResult = WorkersPaymentInfoListDN(0L, 0L, 0L, 1, listOf(sampleDn()))
        viewModel = buildViewModel()
        val item = viewModel.uiState.value.items.first()
        repository.payDebitResult = WorkersPayDebitResultDN(paymentUrl = null, ticket = null, paymentInfo = null)

        viewModel.events.test {
            viewModel.sendIntent(WorkersPaymentIntent.PayItem(item))
            assertIs<WorkersPaymentEvent.ShowToast>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        val state = viewModel.uiState.value
        assertFalse(state.isProcessingPayment)
    }

    @Test
    fun `PayItem failure emits a toast and resets the processing flag`() = runTest(testDispatcher) {
        repository.paymentInfoResult = WorkersPaymentInfoListDN(0L, 0L, 0L, 1, listOf(sampleDn()))
        viewModel = buildViewModel()
        val item = viewModel.uiState.value.items.first()
        repository.shouldThrowError = true

        viewModel.events.test {
            viewModel.sendIntent(WorkersPaymentIntent.PayItem(item))
            val event = assertIs<WorkersPaymentEvent.ShowToast>(awaitItem())
            assertTrue(event.message.isNotBlank())
            cancelAndIgnoreRemainingEvents()
        }
        assertFalse(viewModel.uiState.value.isProcessingPayment)
    }

    // ── helpers ────────────────────────────────────────────────────────────

    private fun sampleDn(month: String = "05") = WorkersPaymentInfoDN(
        pay = false,
        payable = true,
        fines = true,
        year = "1404",
        month = month,
        days = "31",
        professional = "7112",
        professionalTitle = "بنّای سفت‌کار",
        rate = "1.9",
        amount = 7599000L,
        amountFines = 1341000L,
        fromDatePersian = "1404${month}01",
        toDatePersian = "1404${month}31",
        fromDateToDate = "1404${month}011404${month}31",
        monthTitle = "حق بیمه",
        type = "Premium",
        salary = 2312000L,
        payDay = 5541850L,
        paymentStatus = "",
        paymentDate = null,
        payableDes = "هست",
        fishStatus = "دارد",
        maharatStatus = "دارد",
        bazresiStatus = "دارد",
        kargarStatus = "فعال می‌باشد.",
    )
}

