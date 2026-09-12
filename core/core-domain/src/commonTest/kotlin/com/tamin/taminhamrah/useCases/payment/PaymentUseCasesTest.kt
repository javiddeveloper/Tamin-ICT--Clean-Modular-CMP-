package com.tamin.taminhamrah.useCases.payment

import com.tamin.taminhamrah.model.payment.PayerType
import com.tamin.taminhamrah.model.payment.PaymentLinkDN
import com.tamin.taminhamrah.model.payment.PaymentPreviewDN
import com.tamin.taminhamrah.model.payment.PaymentStatus
import com.tamin.taminhamrah.model.payment.PaymentVerificationDN
import com.tamin.taminhamrah.model.payment.PaymentVerifierKey
import com.tamin.taminhamrah.repository.payment.PaymentGatewayRepository
import com.tamin.taminhamrah.repository.payment.PaymentVerifier
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

private class FakePaymentGatewayRepository(
    var preview: PaymentPreviewDN = PaymentPreviewDN(),
    var link: PaymentLinkDN = PaymentLinkDN(),
) : PaymentGatewayRepository {
    var cancelledTicket: String? = null

    override suspend fun getPreview(ticket: String): PaymentPreviewDN = preview

    override suspend fun createPaymentLink(
        ticket: String,
        payerType: PayerType,
        payerIdentifier: String,
    ): PaymentLinkDN = link

    override suspend fun cancelPayment(ticket: String) {
        cancelledTicket = ticket
    }
}

private class RecordingVerifier(
    override val key: PaymentVerifierKey,
    private val result: Result<String>,
) : PaymentVerifier {
    var seenReference: String? = null

    override suspend fun verify(reference: String): String {
        seenReference = reference
        return result.getOrThrow()
    }
}

class PaymentUseCasesTest {

    private val repository = FakePaymentGatewayRepository()

    @Test
    fun `a settled payment with no verifier required is fully successful`() = runTest {
        repository.preview = PaymentPreviewDN(status = PaymentStatus.SUCCESSFUL)

        val outcome = VerifyPaymentUseCase(repository, verifiers = emptyList())(
            ticket = "t-1",
            verifierKey = PaymentVerifierKey.NONE,
            verifierReference = "",
        )

        assertTrue(outcome.isFullySuccessful)
        assertIs<PaymentVerificationDN.NotRequired>(outcome.verification)
    }

    @Test
    fun `a payment still being settled by the gateway counts as paid`() = runTest {
        // VERIFYING means the money has already left the account. Reporting it as a failure would
        // send a user who has paid back to pay a second time.
        repository.preview = PaymentPreviewDN(status = PaymentStatus.VERIFYING)

        val outcome = VerifyPaymentUseCase(repository, verifiers = emptyList())(
            ticket = "t-1",
            verifierKey = PaymentVerifierKey.NONE,
            verifierReference = "",
        )

        assertTrue(outcome.isFullySuccessful)
    }

    @Test
    fun `the verifier named by the key is the one that runs, with its own reference`() = runTest {
        repository.preview = PaymentPreviewDN(status = PaymentStatus.SUCCESSFUL)
        val wanted = RecordingVerifier(
            PaymentVerifierKey.WORKSHOP_DEBIT_INSTALLMENT,
            Result.success("تسویه شد"),
        )
        val other = RecordingVerifier(PaymentVerifierKey.SPECIAL_INSURED, Result.success(""))

        val outcome = VerifyPaymentUseCase(repository, verifiers = listOf(other, wanted))(
            ticket = "t-1",
            verifierKey = PaymentVerifierKey.WORKSHOP_DEBIT_INSTALLMENT,
            verifierReference = "serial-9",
        )

        assertEquals("serial-9", wanted.seenReference)
        assertEquals(null, other.seenReference)
        assertEquals(
            PaymentVerificationDN.Confirmed("تسویه شد"),
            outcome.verification,
        )
    }

    @Test
    fun `money taken but the service refusing to confirm is reported as paid-unconfirmed`() =
        runTest {
            repository.preview = PaymentPreviewDN(status = PaymentStatus.SUCCESSFUL)
            val failing = RecordingVerifier(
                PaymentVerifierKey.SPECIAL_INSURED,
                Result.failure(IllegalStateException("بدهی همچنان باز است")),
            )

            val outcome = VerifyPaymentUseCase(repository, verifiers = listOf(failing))(
                ticket = "t-1",
                verifierKey = PaymentVerifierKey.SPECIAL_INSURED,
                verifierReference = "03",
            )

            assertFalse(outcome.isFullySuccessful)
            assertTrue(outcome.isPaidButUnconfirmed)
            assertEquals(
                PaymentVerificationDN.Failed("بدهی همچنان باز است"),
                outcome.verification,
            )
        }

    @Test
    fun `a key with no verifier registered still produces an answer`() = runTest {
        // A feature whose verifier has not been written yet must still get a working payment
        // screen rather than an exception on the way back from the gateway.
        repository.preview = PaymentPreviewDN(status = PaymentStatus.SUCCESSFUL)

        val outcome = VerifyPaymentUseCase(repository, verifiers = emptyList())(
            ticket = "t-1",
            verifierKey = PaymentVerifierKey.CONSTRUCTION_WORKERS,
            verifierReference = "info",
        )

        assertTrue(outcome.isFullySuccessful)
        assertIs<PaymentVerificationDN.NotRequired>(outcome.verification)
    }

    @Test
    fun `a failed gateway payment is not rescued by a passing verifier`() = runTest {
        repository.preview = PaymentPreviewDN(status = PaymentStatus.FAILED)
        val passing = RecordingVerifier(PaymentVerifierKey.SPECIAL_INSURED, Result.success(""))

        val outcome = VerifyPaymentUseCase(repository, verifiers = listOf(passing))(
            ticket = "t-1",
            verifierKey = PaymentVerifierKey.SPECIAL_INSURED,
            verifierReference = "03",
        )

        assertFalse(outcome.isFullySuccessful)
        assertFalse(outcome.isPaidButUnconfirmed)
    }

    @Test
    fun `cancelling releases the ticket that was passed in`() = runTest {
        CancelPaymentUseCase(repository)("t-42")

        assertEquals("t-42", repository.cancelledTicket)
    }
}

class PaymentStatusTest {

    @Test
    fun `the gateway's own status strings map to the right entries`() {
        assertEquals(PaymentStatus.NOT_PAID, PaymentStatus.fromCode("NOT_PAYED"))
        assertEquals(PaymentStatus.VERIFYING, PaymentStatus.fromCode("VERIFYING"))
        assertEquals(PaymentStatus.SUCCESSFUL, PaymentStatus.fromCode("SUCCESSFUL"))
        assertEquals(PaymentStatus.EXPIRED, PaymentStatus.fromCode("EXPIRED"))
        assertEquals(PaymentStatus.FAILED, PaymentStatus.fromCode("FAILED"))
    }

    @Test
    fun `an unknown or missing status is not mistaken for anything else`() {
        assertEquals(PaymentStatus.UNKNOWN, PaymentStatus.fromCode(null))
        assertEquals(PaymentStatus.UNKNOWN, PaymentStatus.fromCode(""))
        assertEquals(PaymentStatus.UNKNOWN, PaymentStatus.fromCode("SOMETHING_NEW"))
        assertFalse(PaymentStatus.UNKNOWN.isSettled)
        assertFalse(PaymentStatus.UNKNOWN.isPayable)
    }

    @Test
    fun `only an unpaid ticket with time left may be paid`() {
        val payable = PaymentPreviewDN(
            status = PaymentStatus.NOT_PAID,
            millisToExpire = 60_000L,
        )
        assertTrue(payable.isPayable)
        assertFalse(payable.copy(millisToExpire = 0L).isPayable)
        assertFalse(payable.copy(status = PaymentStatus.SUCCESSFUL).isPayable)
    }
}
