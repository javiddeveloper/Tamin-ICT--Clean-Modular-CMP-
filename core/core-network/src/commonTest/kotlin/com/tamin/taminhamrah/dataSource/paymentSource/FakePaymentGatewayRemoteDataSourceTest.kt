package com.tamin.taminhamrah.dataSource.paymentSource

import com.tamin.taminhamrah.model.payment.PaymentLinkRequestDTO
import com.tamin.taminhamrah.model.payment.PaymentMockScenario
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * The mock gateway is only useful if it answers the *whole* flow. A stand-in that always said
 * "successful" would never exercise the result screen's failure branch, which is the branch that
 * gets shipped broken.
 */
class FakePaymentGatewayRemoteDataSourceTest {

    private val request = PaymentLinkRequestDTO(enteredNationalCode = "0499370899", personType = "0")

    private fun ticketFor(scenario: PaymentMockScenario, nonce: String = "1") =
        scenario.ticket(nonce)

    @Test
    fun `a fresh ticket previews as unpaid with an amount and time left`() = runTest {
        val info = FakePaymentGatewayRemoteDataSource(succeeds = true).getPaymentInfo("t-1")

        assertEquals("NOT_PAYED", info.paymentStatus)
        assertTrue((info.paymentAmount ?: 0L) > 0L)
        assertTrue((info.milliSecondsToExpire ?: 0L) > 0L)
    }

    @Test
    fun `the success mock reports a paid ticket once it has been through the gateway`() = runTest {
        val fake = FakePaymentGatewayRemoteDataSource(succeeds = true)

        fake.createPaymentLink("t-1", request)
        val info = fake.getPaymentInfo("t-1")

        assertEquals("SUCCESSFUL", info.paymentStatus)
        assertEquals(info.paymentAmount, info.affectiveAmount)
        assertTrue(info.refNum.orEmpty().isNotBlank())
    }

    @Test
    fun `the failure mock reports a failed ticket with nothing taken`() = runTest {
        val fake = FakePaymentGatewayRemoteDataSource(succeeds = false)

        fake.createPaymentLink("t-1", request)
        val info = fake.getPaymentInfo("t-1")

        assertEquals("FAILED", info.paymentStatus)
        assertEquals(0L, info.affectiveAmount)
    }

    @Test
    fun `the link the mock hands back re-enters the app instead of opening a browser`() = runTest {
        val link = FakePaymentGatewayRemoteDataSource(succeeds = true)
            .createPaymentLink("t-1", request)

        assertEquals(true, link.success)
        assertTrue(link.paymentUrl.orEmpty().startsWith("mytamin://payment_callback"))
        assertTrue(link.paymentUrl.orEmpty().endsWith("ticket=t-1"))
    }

    @Test
    fun `a cancelled ticket reads as expired rather than payable`() = runTest {
        val fake = FakePaymentGatewayRemoteDataSource(succeeds = true)

        fake.cancelPayment("t-1")

        assertEquals("EXPIRED", fake.getPaymentInfo("t-1").paymentStatus)
    }

    @Test
    fun `tickets are tracked separately`() = runTest {
        val fake = FakePaymentGatewayRemoteDataSource(succeeds = true)

        fake.createPaymentLink("t-1", request)

        assertEquals("SUCCESSFUL", fake.getPaymentInfo("t-1").paymentStatus)
        assertEquals("NOT_PAYED", fake.getPaymentInfo("t-2").paymentStatus)
    }

    // ------------------------------------------------------------------ scenarios

    @Test
    fun `each scenario previews with its own amount and reason`() = runTest {
        val fake = FakePaymentGatewayRemoteDataSource(succeeds = true)
        val workers = fake.getPaymentInfo(ticketFor(PaymentMockScenario.CONSTRUCTION_WORKERS))
        val debt = fake.getPaymentInfo(ticketFor(PaymentMockScenario.WORKSHOP_DEBT))

        assertEquals(PaymentMockScenario.CONSTRUCTION_WORKERS.mockAmount, workers.paymentAmount)
        assertEquals(
            PaymentMockScenario.CONSTRUCTION_WORKERS.mockDescription,
            workers.paymentDesc,
        )
        assertNotEquals(workers.paymentAmount, debt.paymentAmount)
        assertNotEquals(workers.paymentDesc, debt.paymentDesc)
    }

    @Test
    fun `a ticket that names no scenario still previews`() = runTest {
        // Real tickets are opaque strings from the service; the mock must not choke on one.
        val info = FakePaymentGatewayRemoteDataSource(succeeds = true)
            .getPaymentInfo("7a5220b4-5cc4-48ea-b943-a657ebb72fe7")

        assertEquals("NOT_PAYED", info.paymentStatus)
        assertTrue((info.paymentAmount ?: 0L) > 0L)
    }

    @Test
    fun `a new run of the same scenario is payable again`() = runTest {
        // The bug this guards: reusing one ticket made the second run preview as already-paid,
        // so the screen opened expired with no time on the clock.
        val fake = FakePaymentGatewayRemoteDataSource(succeeds = true)
        val first = ticketFor(PaymentMockScenario.WORKSHOP_DEBT, nonce = "1")
        val second = ticketFor(PaymentMockScenario.WORKSHOP_DEBT, nonce = "2")

        fake.createPaymentLink(first, request)

        assertEquals("SUCCESSFUL", fake.getPaymentInfo(first).paymentStatus)
        assertEquals("NOT_PAYED", fake.getPaymentInfo(second).paymentStatus)
        assertTrue((fake.getPaymentInfo(second).milliSecondsToExpire ?: 0L) > 0L)
    }
}

class PaymentMockScenarioTest {

    @Test
    fun `a scenario survives the round trip through its ticket`() {
        // The scenario travels in the ticket rather than in memory so the mock still answers
        // correctly after a process death — the same property the real flow depends on.
        PaymentMockScenario.entries.forEach { scenario ->
            assertEquals(scenario, PaymentMockScenario.fromTicket(scenario.ticket("42")))
        }
    }

    @Test
    fun `a real service ticket is not read as a scenario`() {
        assertEquals(null, PaymentMockScenario.fromTicket("7a5220b4-5cc4-48ea-b943-a657ebb72fe7"))
        assertEquals(null, PaymentMockScenario.fromTicket("mock:NOT_A_SCENARIO:1"))
        assertEquals(null, PaymentMockScenario.fromTicket(""))
    }

    @Test
    fun `a request carries the scenario's verifier key and reference`() {
        val request = PaymentMockScenario.CONSTRUCTION_WORKERS.toPaymentRequest(nonce = "9")

        assertEquals(PaymentMockScenario.CONSTRUCTION_WORKERS.verifierKey, request.verifierKey)
        assertEquals(
            PaymentMockScenario.CONSTRUCTION_WORKERS.verifierReference,
            request.verifierReference,
        )
    }
}
