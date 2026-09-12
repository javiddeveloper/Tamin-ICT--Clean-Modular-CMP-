package com.tamin.taminhamrah.dataSource.paymentSource

import com.tamin.taminhamrah.model.payment.PaymentInfoDTO
import com.tamin.taminhamrah.model.payment.PaymentLinkDTO
import com.tamin.taminhamrah.model.payment.PaymentLinkRequestDTO
import com.tamin.taminhamrah.model.payment.PaymentMockScenario
import com.tamin.taminhamrah.util.NetworkConstants
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Stands in for the payment gateway so the payment screens can be built and demonstrated when the
 * real one will not take the traffic — the test bank is down, or the ticket service refuses
 * non-production callers.
 *
 * It answers the whole flow, not one call: a ticket previews as unpaid, exchanging it for a link
 * marks it paid, and the next preview reports the outcome [succeeds] asks for. That progression is
 * the point — a fake that always answered "successful" would never exercise the result screen's
 * failure branch, which is the branch that gets shipped broken.
 *
 * The amount and reason come from the [PaymentMockScenario] encoded in the ticket, so each of the
 * app's payments looks like itself rather than like one generic figure. A ticket that names no
 * scenario still works and falls back to a generic one.
 *
 * The link it hands back is the app's own return deep link, so "go and pay" re-enters the app the
 * same way a real return from the browser does, with no browser in the loop.
 *
 * Selected from Developer Options in debug builds only — see
 * [com.tamin.taminhamrah.model.payment.PaymentMockMode] and [PaymentGatewayRemoteDataSourceSelector].
 */
class FakePaymentGatewayRemoteDataSource(
    /** Whether a ticket taken through the flow ends up paid or refused. */
    private val succeeds: Boolean,
) : PaymentGatewayRemoteDataSource {

    private val mutex = Mutex()
    private val visitedTickets = mutableSetOf<String>()
    private val cancelledTickets = mutableSetOf<String>()

    override suspend fun getPaymentInfo(ticket: String): PaymentInfoDTO {
        delay(FAKE_LATENCY_MILLIS)
        val visited: Boolean
        val cancelled: Boolean
        mutex.withLock {
            visited = ticket in visitedTickets
            cancelled = ticket in cancelledTickets
        }
        return when {
            cancelled -> settledNothing(ticket, status = EXPIRED, reason = FAKE_CANCELLED_DESC)
            !visited -> base(ticket).copy(
                milliSecondsToExpire = FAKE_EXPIRY_MILLIS,
                paymentStatus = NOT_PAID,
            )

            succeeds -> base(ticket).copy(
                affectiveAmount = amountOf(ticket),
                paymentStatus = SUCCESSFUL,
                refNum = FAKE_REFERENCE_NUMBER,
                traceNo = FAKE_TRACE_NUMBER,
            )

            else -> settledNothing(ticket, status = FAILED, reason = FAKE_FAILURE_DESC)
        }
    }

    override suspend fun createPaymentLink(
        ticket: String,
        request: PaymentLinkRequestDTO,
    ): PaymentLinkDTO {
        delay(FAKE_LATENCY_MILLIS)
        mutex.withLock { visitedTickets += ticket }
        return PaymentLinkDTO(
            paymentUrl = "${NetworkConstants.PAYMENT_RETURN_URI}?ticket=$ticket",
            success = true,
        )
    }

    override suspend fun cancelPayment(ticket: String) {
        delay(FAKE_LATENCY_MILLIS)
        mutex.withLock { cancelledTickets += ticket }
    }

    /** The fields every answer about [ticket] shares, before it has been paid. */
    private fun base(ticket: String) = PaymentInfoDTO(
        ticket = ticket,
        paymentId = paymentIdOf(ticket),
        paymentAmount = amountOf(ticket),
        affectiveAmount = 0L,
        paymentDesc = descriptionOf(ticket),
        milliSecondsToExpire = 0L,
    )

    private fun settledNothing(ticket: String, status: String, reason: String) =
        base(ticket).copy(paymentStatus = status, transactionResultDesc = reason)

    private fun scenarioOf(ticket: String) = PaymentMockScenario.fromTicket(ticket)

    private fun amountOf(ticket: String) = scenarioOf(ticket)?.mockAmount ?: FALLBACK_AMOUNT

    private fun descriptionOf(ticket: String) =
        scenarioOf(ticket)?.mockDescription ?: FALLBACK_DESC

    private fun paymentIdOf(ticket: String) =
        scenarioOf(ticket)?.let { "MOCK-${it.name}" } ?: FALLBACK_PAYMENT_ID

    private companion object {
        const val FAKE_LATENCY_MILLIS = 600L
        const val FAKE_EXPIRY_MILLIS = 15 * 60 * 1000L
        const val FAKE_REFERENCE_NUMBER = "900000000001"
        const val FAKE_TRACE_NUMBER = "123456"

        const val NOT_PAID = "NOT_PAYED"
        const val SUCCESSFUL = "SUCCESSFUL"
        const val FAILED = "FAILED"
        const val EXPIRED = "EXPIRED"

        // Simulated gateway payload for a ticket that names no scenario, plus the two outcome
        // messages. Debug-only server text, never rendered in a release build, so not strings.xml.
        const val FALLBACK_AMOUNT = 1_250_000L
        const val FALLBACK_PAYMENT_ID = "MOCK-000000001"
        const val FALLBACK_DESC = "پرداخت آزمایشی (Mock)"
        const val FAKE_FAILURE_DESC = "تراکنش آزمایشی ناموفق (Mock)"
        const val FAKE_CANCELLED_DESC = "تراکنش آزمایشی لغو شد (Mock)"
    }
}
