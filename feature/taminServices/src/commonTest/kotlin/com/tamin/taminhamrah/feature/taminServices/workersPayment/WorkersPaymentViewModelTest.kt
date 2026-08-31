package com.tamin.taminhamrah.feature.taminServices.workersPayment

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.taminServices.workersPayment.contract.WorkersPaymentEvent
import com.tamin.taminhamrah.feature.taminServices.workersPayment.contract.WorkersPaymentIntent
import com.tamin.taminhamrah.feature.taminServices.workersPayment.model.WorkersPaymentInfoPR
import com.tamin.taminhamrah.model.workersPayment.WorkersPaymentInfoDN
import com.tamin.taminhamrah.model.workersPayment.WorkersPaymentInfoListDN
import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitResultDN
import com.tamin.taminhamrah.util.NetworkConstants
import com.tamin.taminhamrah.useCases.workersPayment.GetWorkersPaymentInfoUseCase
import com.tamin.taminhamrah.useCases.workersPayment.InspectWorkersPaymentTicketUseCase
import com.tamin.taminhamrah.useCases.workersPayment.PayWorkersDebitUseCase
import com.tamin.taminhamrah.useCases.workersPayment.WorkersPaymentCallbackNotifierImpl
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

/**
 * Covers the whole single-ViewModel flow the two workers-payment screens share: the init list load,
 * screen-2 open/close, the `payDebit` → gateway-url handoff, and the deep-link return path
 * (`VerifyPendingPayment` → receipt → `DismissReceipt` → list reload).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WorkersPaymentViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: FakeWorkersPaymentRepository
    private lateinit var callbackNotifier: WorkersPaymentCallbackNotifierImpl
    private lateinit var viewModel: WorkersPaymentViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeWorkersPaymentRepository()
        callbackNotifier = WorkersPaymentCallbackNotifierImpl()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = WorkersPaymentViewModel(
        getWorkersPaymentInfoUseCase = GetWorkersPaymentInfoUseCase(repository),
        payWorkersDebitUseCase = PayWorkersDebitUseCase(repository),
        inspectWorkersPaymentTicketUseCase = InspectWorkersPaymentTicketUseCase(repository),
        callbackNotifier = callbackNotifier,
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

    // ── payDebit → gateway url ─────────────────────────────────────────────

    @Test
    fun `PayItem with a gateway url stores the pending ticket and emits OpenPaymentUrl`() = runTest(testDispatcher) {
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
            val event = assertIs<WorkersPaymentEvent.OpenPaymentUrl>(awaitItem())
            assertEquals("https://tfh.tamin.ir/view/#/payment/T-1", event.url)
            cancelAndIgnoreRemainingEvents()
        }

        val state = viewModel.uiState.value
        assertEquals("T-1", state.pendingTicket)
        assertEquals("enc-info", state.pendingPaymentInfo)
        assertTrue(state.hasPendingPayment)
        assertFalse(state.isProcessingPayment)

        val params = assertNotNull(repository.lastPayDebitParams)
        assertEquals(item.totalPayable, params.amount)
        assertEquals(listOf(item.fromDatePersian), params.dates)
        assertEquals(listOf(item.fromDateToDate), params.fromToDate)
        assertEquals(NetworkConstants.WORKERS_PAYMENT_CALLBACK, params.redirectUrl)
    }

    @Test
    fun `PayItem with a blank url emits a toast and leaves no pending ticket`() = runTest(testDispatcher) {
        repository.paymentInfoResult = WorkersPaymentInfoListDN(0L, 0L, 0L, 1, listOf(sampleDn()))
        viewModel = buildViewModel()
        val item = viewModel.uiState.value.items.first()
        repository.payDebitResult = WorkersPayDebitResultDN(paymentUrl = null, ticket = "T-1", paymentInfo = null)

        viewModel.events.test {
            viewModel.sendIntent(WorkersPaymentIntent.PayItem(item))
            assertIs<WorkersPaymentEvent.ShowToast>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        val state = viewModel.uiState.value
        assertNull(state.pendingTicket)
        assertFalse(state.hasPendingPayment)
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

    // ── deep-link return: verify / dismiss ────────────────────────────────

    @Test
    fun `VerifyPendingPayment success produces a receipt and clears the pending ticket`() = runTest(testDispatcher) {
        val viewModel = driveToPendingPayment()
        repository.inspectTicketResult = "پرداخت با موفقیت انجام شد."

        viewModel.sendIntent(WorkersPaymentIntent.VerifyPendingPayment)

        val state = viewModel.uiState.value
        val receipt = assertNotNull(state.paymentReceipt)
        assertEquals("T-1", receipt.trackingCode)
        assertEquals("پرداخت با موفقیت انجام شد.", receipt.message)
        assertEquals(state.selectedPaymentItem, receipt.item)
        assertNull(state.pendingTicket)
        assertNull(state.pendingPaymentInfo)
        assertFalse(state.isVerifying)
        assertEquals("T-1" to "enc-info", repository.lastInspectTicketParams)
    }

    @Test
    fun `gateway callback verifies the pending payment and produces a receipt`() = runTest(testDispatcher) {
        val viewModel = driveToPendingPayment()
        repository.inspectTicketResult = "پرداخت با موفقیت انجام شد."

        callbackNotifier.notifyCallback()

        val state = viewModel.uiState.value
        val receipt = assertNotNull(state.paymentReceipt)
        assertEquals("T-1", receipt.trackingCode)
        assertNull(state.pendingTicket)
        assertEquals("T-1" to "enc-info", repository.lastInspectTicketParams)
    }

    @Test
    fun `gateway callback with no pending payment is ignored`() = runTest(testDispatcher) {
        repository.paymentInfoResult = WorkersPaymentInfoListDN(0L, 0L, 0L, 1, listOf(sampleDn()))
        viewModel = buildViewModel()

        callbackNotifier.notifyCallback()

        assertNull(viewModel.uiState.value.paymentReceipt)
        assertNull(repository.lastInspectTicketParams)
    }

    @Test
    fun `VerifyPendingPayment with no pending ticket is a no-op`() = runTest(testDispatcher) {
        repository.paymentInfoResult = WorkersPaymentInfoListDN(0L, 0L, 0L, 1, listOf(sampleDn()))
        viewModel = buildViewModel()

        viewModel.sendIntent(WorkersPaymentIntent.VerifyPendingPayment)

        assertNull(viewModel.uiState.value.paymentReceipt)
        assertNull(repository.lastInspectTicketParams)
    }

    @Test
    fun `VerifyPendingPayment failure is silent - no toast, resets the verifying flag`() = runTest(testDispatcher) {
        val viewModel = driveToPendingPayment()
        repository.shouldThrowError = true

        viewModel.events.test {
            // buffered by the successful payDebit inside driveToPendingPayment()
            assertIs<WorkersPaymentEvent.OpenPaymentUrl>(awaitItem())
            viewModel.sendIntent(WorkersPaymentIntent.VerifyPendingPayment)
            // matches legacy: a failed inpectTicket on the callback surfaces nothing
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }

        val state = viewModel.uiState.value
        assertNull(state.paymentReceipt)
        assertFalse(state.isVerifying)
        // one-shot: pending ticket dropped so RESUMED / the callback can't retry-loop
        assertNull(state.pendingTicket)
        assertFalse(state.hasPendingPayment)
        // failure steps back off the confirmation screen to the debt list
        assertNull(state.selectedPaymentItem)
    }

    @Test
    fun `DismissReceipt clears the receipt and selected item and reloads the list`() = runTest(testDispatcher) {
        val viewModel = driveToPendingPayment()
        viewModel.sendIntent(WorkersPaymentIntent.VerifyPendingPayment)
        assertNotNull(viewModel.uiState.value.paymentReceipt)

        // the reload after dismissing should pick up this fresh fixture (2 rows now)
        repository.paymentInfoResult = WorkersPaymentInfoListDN(
            totalAmount = 10L, totalPenalty = 0L, totalPremium = 10L, total = 2,
            list = listOf(sampleDn(month = "05"), sampleDn(month = "06")),
        )

        viewModel.sendIntent(WorkersPaymentIntent.DismissReceipt)

        val state = viewModel.uiState.value
        assertNull(state.paymentReceipt)
        assertNull(state.selectedPaymentItem)
        assertEquals(2, state.items.size)
    }

    // ── helpers ────────────────────────────────────────────────────────────

    /**
     * Loads one payable row, opens screen 2 for it, and runs a successful `payDebit`. That
     * `payDebit` leaves one `OpenPaymentUrl` buffered on [WorkersPaymentViewModel.events]; tests
     * that go on to collect events must consume it first (see the failure test below).
     */
    private fun driveToPendingPayment(): WorkersPaymentViewModel {
        repository.paymentInfoResult = WorkersPaymentInfoListDN(0L, 0L, 0L, 1, listOf(sampleDn()))
        val vm = buildViewModel()
        val item = vm.uiState.value.items.first()
        repository.payDebitResult = WorkersPayDebitResultDN(
            paymentUrl = "https://tfh.tamin.ir/view/#/payment/T-1",
            ticket = "T-1",
            paymentInfo = "enc-info",
        )
        vm.sendIntent(WorkersPaymentIntent.OpenPaymentScreen(item))
        vm.sendIntent(WorkersPaymentIntent.PayItem(item))
        return vm
    }

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
