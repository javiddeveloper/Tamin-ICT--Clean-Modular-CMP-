package com.tamin.taminhamrah.feature.workshops.ui.workshopDebit

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.workshops.fake.FakeWorkShopsRepository
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.DebitPaymentDN
import com.tamin.taminhamrah.model.workshop.DebitPaymentPreCheckDN
import com.tamin.taminhamrah.model.workshop.WORKSHOP_PAGE_SIZE
import com.tamin.taminhamrah.model.workshop.WorkShopDebtDN
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.useCases.workshops.GetWorkshopDebitsUseCase
import com.tamin.taminhamrah.useCases.workshops.PayWorkshopDebitUseCase
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * جزئیات محاسبه گردش حساب بدهی — the debts of one workshop, each payable online.
 *
 * What is pinned down here is the payment call. The row a user taps is a presentation model whose
 * numbers are printed in Persian digits, and everything on it beyond the two identifiers exists to
 * be read rather than sent. A field that reaches the service in its printed form is refused, and
 * the refusal reads like a server problem rather than a client one.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WorkshopDebitViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeWorkShopsRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeWorkShopsRepository()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = WorkshopDebitViewModel(
        GetWorkshopDebitsUseCase(repository),
        PayWorkshopDebitUseCase(repository),
    )

    private fun open(viewModel: WorkshopDebitViewModel) =
        viewModel.sendIntent(WorkshopDebitIntent.Open(WORKSHOP_ID, BRANCH_CODE))

    @Test
    fun `opening loads the first page of debts`() = runTest(testDispatcher) {
        repository.workshopDebits = debtsPage(count = 2, total = 2)

        val viewModel = viewModel()
        open(viewModel)

        assertEquals(2, viewModel.uiState.value.list.items.size)
        assertEquals(WORKSHOP_ID, viewModel.uiState.value.workshopId)
    }

    @Test
    fun `reopening the same workshop does not refetch`() = runTest(testDispatcher) {
        repository.workshopDebits = debtsPage(count = 1, total = 1)

        val viewModel = viewModel()
        open(viewModel)
        repository.workshopDebits = debtsPage(count = 5, total = 5)
        open(viewModel)

        assertEquals(1, viewModel.uiState.value.list.items.size)
    }

    /**
     * The regression this class exists for.
     *
     * `agreementRow` goes on the wire as `peymanSequence`. It is also printed on the card, and for
     * a while the row carried only the printed form — so the service was sent `۰۹۶۰۰۰۰۲` and
     * answered that the debt was not valid.
     */
    @Test
    fun `paying sends the identifiers as the service spells them, not as the card prints them`() =
        runTest(testDispatcher) {
            repository.workshopDebits = debtsPage(count = 1, total = 1)
            repository.paymentPreCheck = DebitPaymentPreCheckDN(allowed = true)
            repository.paymentResult = DebitPaymentDN(succeeded = true, paymentPageUrl = PAYMENT_URL)

            val viewModel = viewModel()
            open(viewModel)
            viewModel.sendIntent(WorkshopDebitIntent.PayDebit(viewModel.uiState.value.list.items[0]))

            val sent = assertNotNull(repository.lastPaymentRequest)
            assertEquals(RAW_DEBIT_NUMBER, sent.debitNumber)
            assertEquals(RAW_AGREEMENT_ROW, sent.agreementRow)
            assertEquals(WORKSHOP_ID, sent.workshopId)
            assertEquals(BRANCH_CODE, sent.branchCode)
        }

    /**
     * A debt whose agreement row the service reports as `null` must not be paid for with an empty
     * one: the list answers `"peymanSequence": null`, the old client omits the field entirely, and
     * sending `""` instead was answered with `ProxyRuntimeException`.
     */
    @Test
    fun `a debt with no agreement row sends none`() = runTest(testDispatcher) {
        repository.workshopDebits = PagedListDN(
            items = listOf(
                WorkShopDebtDN(debitNumber = RAW_DEBIT_NUMBER, agreementRow = ""),
            ),
            total = 1,
        )
        repository.paymentPreCheck = DebitPaymentPreCheckDN(allowed = true)
        repository.paymentResult = DebitPaymentDN(succeeded = true, paymentPageUrl = PAYMENT_URL)

        val viewModel = viewModel()
        open(viewModel)
        viewModel.sendIntent(WorkshopDebitIntent.PayDebit(viewModel.uiState.value.list.items[0]))

        assertEquals("", assertNotNull(repository.lastPaymentRequest).agreementRow)
    }

    @Test
    fun `an accepted payment sends the user to the payment page`() = runTest(testDispatcher) {
        repository.workshopDebits = debtsPage(count = 1, total = 1)
        repository.paymentPreCheck = DebitPaymentPreCheckDN(allowed = true)
        repository.paymentResult = DebitPaymentDN(succeeded = true, paymentPageUrl = PAYMENT_URL)

        val viewModel = viewModel()
        open(viewModel)

        viewModel.events.test {
            viewModel.sendIntent(
                WorkshopDebitIntent.PayDebit(viewModel.uiState.value.list.items[0])
            )
            val event = awaitItem()
            assertTrue(event is WorkshopDebitEvent.OpenPaymentPage)
            assertEquals(PAYMENT_URL, event.url)
        }
    }

    /**
     * The gateway is asked to bind the ticket to the signed-in user before anyone is sent to it,
     * so the ticket that reaches the gateway has to be the one the payment service just issued.
     */
    @Test
    fun `an accepted payment confirms its ticket with the gateway first`() =
        runTest(testDispatcher) {
            repository.workshopDebits = debtsPage(count = 1, total = 1)
            repository.paymentPreCheck = DebitPaymentPreCheckDN(allowed = true)
            repository.paymentResult = DebitPaymentDN(
                succeeded = true,
                paymentPageUrl = PAYMENT_URL,
                ticket = TICKET,
            )

            val viewModel = viewModel()
            open(viewModel)
            viewModel.sendIntent(WorkshopDebitIntent.PayDebit(viewModel.uiState.value.list.items[0]))

            assertEquals(TICKET, repository.confirmedTicket)
        }

    /**
     * A ticket the gateway will not honor must stop the flow rather than open a page that cannot
     * be paid. The old client read this outcome and then ignored it, which is how a dead ticket
     * became a button that did nothing.
     */
    @Test
    fun `a ticket the gateway refuses is reported and no page is opened`() =
        runTest(testDispatcher) {
            repository.workshopDebits = debtsPage(count = 1, total = 1)
            repository.paymentPreCheck = DebitPaymentPreCheckDN(allowed = true)
            repository.paymentResult = DebitPaymentDN(
                succeeded = true,
                paymentPageUrl = PAYMENT_URL,
                ticket = TICKET,
            )

            val viewModel = viewModel()
            open(viewModel)
            repository.error = TaminApiException(title = GATEWAY_REFUSAL)

            viewModel.events.test {
                viewModel.sendIntent(
                    WorkshopDebitIntent.PayDebit(viewModel.uiState.value.list.items[0])
                )
                val event = awaitItem()
                assertTrue(event is WorkshopDebitEvent.ShowServerMessage, "expected a spoken failure")
                // The reason is reported as the error formatter leaves it, and that formatter drops
                // a trailing full stop so the message can be composed with a subtitle.
                assertEquals(GATEWAY_REFUSAL.removeSuffix("."), event.message)
            }
            assertNull(viewModel.uiState.value.payingDebitNumber)
        }

    /** A refused debt never reaches the gateway: there is no ticket to bind. */
    @Test
    fun `a refused payment never confirms a ticket`() = runTest(testDispatcher) {
        repository.workshopDebits = debtsPage(count = 1, total = 1)
        repository.paymentPreCheck = DebitPaymentPreCheckDN(allowed = true)
        repository.paymentResult = DebitPaymentDN(succeeded = false, message = REFUSAL)

        val viewModel = viewModel()
        open(viewModel)
        viewModel.sendIntent(WorkshopDebitIntent.PayDebit(viewModel.uiState.value.list.items[0]))

        assertNull(repository.confirmedTicket)
    }

    @Test
    fun `a refusal is repeated in the words the service used`() = runTest(testDispatcher) {
        repository.workshopDebits = debtsPage(count = 1, total = 1)
        repository.paymentPreCheck = DebitPaymentPreCheckDN(allowed = true)
        repository.paymentResult = DebitPaymentDN(succeeded = false, message = REFUSAL)

        val viewModel = viewModel()
        open(viewModel)

        viewModel.events.test {
            viewModel.sendIntent(
                WorkshopDebitIntent.PayDebit(viewModel.uiState.value.list.items[0])
            )
            val event = awaitItem()
            assertTrue(event is WorkshopDebitEvent.ShowServerMessage)
            assertEquals(REFUSAL, event.message)
        }
    }

    /** A silent refusal is the one path that left the old client showing nothing at all. */
    @Test
    fun `a wordless refusal still says something`() = runTest(testDispatcher) {
        repository.workshopDebits = debtsPage(count = 1, total = 1)
        repository.paymentPreCheck = DebitPaymentPreCheckDN(allowed = true)
        repository.paymentResult = DebitPaymentDN(succeeded = false)

        val viewModel = viewModel()
        open(viewModel)

        viewModel.events.test {
            viewModel.sendIntent(
                WorkshopDebitIntent.PayDebit(viewModel.uiState.value.list.items[0])
            )
            assertTrue(awaitItem() is WorkshopDebitEvent.ShowMessage)
        }
    }

    /** The pre-check gates the payment, so a refused check must never reach `pay-normal-debit`. */
    @Test
    fun `a refused pre-check never asks for payment`() = runTest(testDispatcher) {
        repository.workshopDebits = debtsPage(count = 1, total = 1)
        repository.paymentPreCheck = DebitPaymentPreCheckDN(allowed = false)

        val viewModel = viewModel()
        open(viewModel)

        viewModel.events.test {
            viewModel.sendIntent(
                WorkshopDebitIntent.PayDebit(viewModel.uiState.value.list.items[0])
            )
            assertTrue(awaitItem() is WorkshopDebitEvent.ShowMessage)
        }
        assertNull(repository.lastPaymentRequest)
    }

    /**
     * The second regression this class carries.
     *
     * A payment that never reached the service used to be reported as the *list*'s error, and the
     * list only draws its error when it has no rows — so a 500 left the button doing visibly
     * nothing while the debts sat on screen.
     */
    @Test
    fun `a payment that never reached the service still says so`() = runTest(testDispatcher) {
        repository.workshopDebits = debtsPage(count = 2, total = 2)

        val viewModel = viewModel()
        open(viewModel)
        repository.error = TaminApiException(title = SERVER_FAILURE)

        viewModel.events.test {
            viewModel.sendIntent(
                WorkshopDebitIntent.PayDebit(viewModel.uiState.value.list.items[0])
            )
            val event = awaitItem()
            assertTrue(event is WorkshopDebitEvent.ShowServerMessage)
            assertEquals(SERVER_FAILURE, event.message)
        }

        val state = viewModel.uiState.value
        assertNull(state.list.error, "a failed payment must not put the list into an error state")
        assertEquals(2, state.list.items.size)
        assertNull(state.payingDebitNumber, "the row must stop showing progress")
    }

    /** A failure with nothing to say must not surface as an empty toast. */
    @Test
    fun `a failure with no wording falls back to the refusal message`() = runTest(testDispatcher) {
        repository.workshopDebits = debtsPage(count = 1, total = 1)

        val viewModel = viewModel()
        open(viewModel)
        repository.error = TaminApiException(title = "")

        viewModel.events.test {
            viewModel.sendIntent(
                WorkshopDebitIntent.PayDebit(viewModel.uiState.value.list.items[0])
            )
            assertTrue(awaitItem() is WorkshopDebitEvent.ShowMessage)
        }
    }

    @Test
    fun `the row stops showing progress once the answer is in`() = runTest(testDispatcher) {
        repository.workshopDebits = debtsPage(count = 1, total = 1)
        repository.paymentPreCheck = DebitPaymentPreCheckDN(allowed = true)
        repository.paymentResult = DebitPaymentDN(succeeded = true, paymentPageUrl = PAYMENT_URL)

        val viewModel = viewModel()
        open(viewModel)
        viewModel.sendIntent(WorkshopDebitIntent.PayDebit(viewModel.uiState.value.list.items[0]))

        assertNull(viewModel.uiState.value.payingDebitNumber)
    }

    @Test
    fun `a full first page leaves room to page further`() = runTest(testDispatcher) {
        repository.workshopDebits =
            debtsPage(count = WORKSHOP_PAGE_SIZE, total = WORKSHOP_PAGE_SIZE * 2)

        val viewModel = viewModel()
        open(viewModel)

        assertTrue(viewModel.uiState.value.list.hasMore)
    }

    /**
     * Mapping is what turns the wire values into the printed ones, so the fixture goes through it.
     *
     * Rows are given distinct debit numbers because the list collapses duplicates — a page of
     * identical rows would arrive as one, and count assertions would be measuring the deduplication
     * rather than the paging. The first row keeps the canonical number the payment tests assert on.
     */
    private fun debtsPage(count: Int, total: Int) = PagedListDN(
        items = List(count) {
            WorkShopDebtDN(
                debitNumber = if (it == 0) RAW_DEBIT_NUMBER else "$RAW_DEBIT_NUMBER$it",
                agreementRow = RAW_AGREEMENT_ROW,
                customerCode = "09600002",
                debitAmount = 14_203_311L,
                debitRemain = 14_203_311L,
                orderRecipeDate = "14050525",
                debitStartDate = "13960701",
                debitEndDate = "13970631",
            )
        },
        total = total,
    )

    private companion object {
        const val WORKSHOP_ID = "9028218513"
        const val BRANCH_CODE = "14"
        const val RAW_DEBIT_NUMBER = "6310030089235"
        const val RAW_AGREEMENT_ROW = "09600002"
        const val PAYMENT_URL = "https://example.invalid/pay/ticket"
        const val TICKET = "ticket-9028218513"
        const val GATEWAY_REFUSAL = "تیکت پرداخت معتبر نیست."
        const val REFUSAL = "بدهی ارسالی معتبر نمی باشد."
        const val SERVER_FAILURE = "خطای سرور"
    }
}
