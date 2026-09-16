package com.tamin.taminhamrah.feature.payment

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.payment.ui.checkout.PaymentCheckoutViewModel
import com.tamin.taminhamrah.feature.payment.ui.checkout.contract.PaymentCheckoutEvent
import com.tamin.taminhamrah.feature.payment.ui.checkout.contract.PaymentCheckoutIntent
import com.tamin.taminhamrah.model.payment.PayerType
import com.tamin.taminhamrah.model.payment.PaymentLinkDN
import com.tamin.taminhamrah.model.payment.PaymentPreviewDN
import com.tamin.taminhamrah.model.payment.PaymentStatus
import com.tamin.taminhamrah.repository.payment.PaymentGatewayRepository
import com.tamin.taminhamrah.useCases.agent.GetCurrentUserNationalCodeUseCase
import com.tamin.taminhamrah.useCases.payment.CancelPaymentUseCase
import com.tamin.taminhamrah.useCases.payment.CreatePaymentLinkUseCase
import com.tamin.taminhamrah.useCases.payment.GetPaymentPreviewUseCase
import kotlinx.coroutines.Dispatchers
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

private class FakeGatewayRepository : PaymentGatewayRepository {
    var preview = PaymentPreviewDN(
        ticket = "t-1",
        amount = 1_000_000L,
        status = PaymentStatus.NOT_PAID,
        millisToExpire = 300_000L,
    )
    var link = PaymentLinkDN(succeeded = true, paymentUrl = "https://gateway/pay")

    var lastPayerType: PayerType? = null
    var lastPayerIdentifier: String? = null
    var cancelledTicket: String? = null
    var paymentLinkCalls = 0

    override suspend fun getPreview(ticket: String) = preview

    override suspend fun createPaymentLink(
        ticket: String,
        payerType: PayerType,
        payerIdentifier: String,
    ): PaymentLinkDN {
        paymentLinkCalls++
        lastPayerType = payerType
        lastPayerIdentifier = payerIdentifier
        return link
    }

    override suspend fun cancelPayment(ticket: String) {
        cancelledTicket = ticket
    }
}

/**
 * These cover the decisions the shared checkout screen makes on the user's behalf: who the payment
 * is filed under, whether it may be sent at all, and — the one that costs real money if it is
 * wrong — whether an abandoned ticket gets released.
 */
class PaymentCheckoutViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val repository = FakeGatewayRepository()

    private fun viewModel(nationalCode: String? = OWN_NATIONAL_CODE) = PaymentCheckoutViewModel(
        getPaymentPreview = GetPaymentPreviewUseCase(repository),
        createPaymentLink = CreatePaymentLinkUseCase(repository),
        cancelPayment = CancelPaymentUseCase(repository),
        getCurrentUserNationalCode = GetCurrentUserNationalCodeUseCase { nationalCode },
    )

    @BeforeTest
    fun setUp() = Dispatchers.setMain(testDispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `a payment with no ticket is reported as such instead of being attempted`() = runTest {
        val vm = viewModel()

        vm.sendIntent(PaymentCheckoutIntent.Load(""))

        assertTrue(vm.uiState.value.isTicketMissing)
        assertEquals(0, repository.paymentLinkCalls)
    }

    @Test
    fun `paying for oneself sends the signed-in user's own national code`() = runTest {
        val vm = viewModel(nationalCode = OWN_NATIONAL_CODE)
        vm.sendIntent(PaymentCheckoutIntent.Load(TICKET))

        vm.events.test {
            vm.sendIntent(PaymentCheckoutIntent.PayClicked)

            assertIs<PaymentCheckoutEvent.OpenGateway>(awaitItem())
            assertEquals(PayerType.CURRENT_USER, repository.lastPayerType)
            assertEquals(OWN_NATIONAL_CODE, repository.lastPayerIdentifier)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `paying for oneself is blocked when the app could not load the user's own national code`() =
        runTest {
            val vm = viewModel(nationalCode = null)
            vm.sendIntent(PaymentCheckoutIntent.Load(TICKET))

            assertFalse(vm.uiState.value.canSubmit)

            vm.sendIntent(PaymentCheckoutIntent.PayClicked)
            assertEquals(0, repository.paymentLinkCalls)
        }

    @Test
    fun `an identifier that fails its own rule keeps the payment from being sent`() = runTest {
        val vm = viewModel()
        vm.sendIntent(PaymentCheckoutIntent.Load(TICKET))

        vm.sendIntent(PaymentCheckoutIntent.PayerTypeSelected(PayerType.OTHER_PERSON))
        // Same digits as a valid code with the last one changed: only the checksum separates them.
        vm.sendIntent(PaymentCheckoutIntent.PayerIdentifierChanged("0499370898"))

        assertTrue(vm.uiState.value.identifierError)
        assertFalse(vm.uiState.value.canSubmit)

        vm.sendIntent(PaymentCheckoutIntent.PayClicked)
        assertEquals(0, repository.paymentLinkCalls)
    }

    @Test
    fun `typing a national code on a Persian-numeral keyboard keeps every digit typed so far`() =
        runTest {
            val vm = viewModel()
            vm.sendIntent(PaymentCheckoutIntent.Load(TICKET))
            vm.sendIntent(PaymentCheckoutIntent.PayerTypeSelected(PayerType.OTHER_PERSON))

            // A Persian-numeral keyboard types Persian glyphs into the field one at a time; each
            // keystroke must fold onto what was already typed, not wipe it.
            vm.sendIntent(PaymentCheckoutIntent.PayerIdentifierChanged("۰"))
            vm.sendIntent(PaymentCheckoutIntent.PayerIdentifierChanged("۰۴"))
            vm.sendIntent(PaymentCheckoutIntent.PayerIdentifierChanged("۰۴۹"))

            assertEquals("049", vm.uiState.value.payerIdentifier)
        }

    @Test
    fun `an empty identifier is incomplete rather than flagged as wrong`() = runTest {
        val vm = viewModel()
        vm.sendIntent(PaymentCheckoutIntent.Load(TICKET))

        vm.sendIntent(PaymentCheckoutIntent.PayerTypeSelected(PayerType.OTHER_PERSON))

        assertFalse(vm.uiState.value.identifierError)
        assertFalse(vm.uiState.value.canSubmit)
    }

    @Test
    fun `switching payer type clears what was typed for the previous one`() = runTest {
        // A national code typed for one person is never the right value for another; leaving it
        // behind is how a payment could end up filed under the wrong code.
        val vm = viewModel()
        vm.sendIntent(PaymentCheckoutIntent.Load(TICKET))

        vm.sendIntent(PaymentCheckoutIntent.PayerTypeSelected(PayerType.OTHER_PERSON))
        vm.sendIntent(PaymentCheckoutIntent.PayerIdentifierChanged(OWN_NATIONAL_CODE))
        assertEquals(OWN_NATIONAL_CODE, vm.uiState.value.payerIdentifier)

        vm.sendIntent(PaymentCheckoutIntent.PayerTypeSelected(PayerType.LEGAL_ENTITY))

        assertEquals("", vm.uiState.value.payerIdentifier)
        assertFalse(vm.uiState.value.identifierError)
    }

    @Test
    fun `an expired ticket cannot be paid`() = runTest {
        repository.preview = repository.preview.copy(millisToExpire = 0L)
        val vm = viewModel()

        vm.sendIntent(PaymentCheckoutIntent.Load(TICKET))

        assertTrue(vm.uiState.value.isExpired)
        assertFalse(vm.uiState.value.canSubmit)
    }

    @Test
    fun `running out of time releases the ticket so the debt can be paid again`() = runTest {
        val vm = viewModel()
        vm.sendIntent(PaymentCheckoutIntent.Load(TICKET))

        vm.sendIntent(PaymentCheckoutIntent.TimerFinished)

        assertTrue(vm.uiState.value.isExpired)
        assertEquals(TICKET, repository.cancelledTicket)
    }

    @Test
    fun `backing out of a live ticket asks first, then releases it`() = runTest {
        val vm = viewModel()
        vm.sendIntent(PaymentCheckoutIntent.Load(TICKET))

        vm.sendIntent(PaymentCheckoutIntent.BackRequested)
        assertTrue(vm.uiState.value.showCancelConfirmation)
        assertNull(repository.cancelledTicket)

        vm.sendIntent(PaymentCheckoutIntent.CancelConfirmed)
        assertFalse(vm.uiState.value.showCancelConfirmation)
        assertEquals(TICKET, repository.cancelledTicket)
    }

    @Test
    fun `backing out of an expired ticket leaves immediately without a second question`() =
        runTest {
            repository.preview = repository.preview.copy(millisToExpire = 0L)
            val vm = viewModel()
            vm.sendIntent(PaymentCheckoutIntent.Load(TICKET))

            vm.events.test {
                vm.sendIntent(PaymentCheckoutIntent.BackRequested)

                assertIs<PaymentCheckoutEvent.Cancelled>(awaitItem())
                assertFalse(vm.uiState.value.showCancelConfirmation)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `a refused payment link is surfaced with the gateway's own reason`() = runTest {
        repository.link = PaymentLinkDN(succeeded = false, message = REFUSAL)
        val vm = viewModel()
        vm.sendIntent(PaymentCheckoutIntent.Load(TICKET))

        vm.sendIntent(PaymentCheckoutIntent.PayClicked)

        assertEquals(REFUSAL, vm.uiState.value.error)
    }

    @Test
    fun `a second tap cannot start a second payment on the same ticket`() = runTest {
        val vm = viewModel()
        vm.sendIntent(PaymentCheckoutIntent.Load(TICKET))

        vm.sendIntent(PaymentCheckoutIntent.PayClicked)
        vm.sendIntent(PaymentCheckoutIntent.PayClicked)

        assertEquals(1, repository.paymentLinkCalls)
    }

    private companion object {
        const val TICKET = "t-1"
        const val OWN_NATIONAL_CODE = "0499370899"
        const val REFUSAL = "پرداخت‌کننده مجاز نیست"
    }
}
