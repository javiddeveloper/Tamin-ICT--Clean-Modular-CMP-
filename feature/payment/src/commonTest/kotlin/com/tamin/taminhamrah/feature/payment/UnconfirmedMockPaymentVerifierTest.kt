package com.tamin.taminhamrah.feature.payment

import com.tamin.taminhamrah.feature.payment.verifier.UnconfirmedMockPaymentVerifier
import com.tamin.taminhamrah.model.payment.PaymentMockScenario
import com.tamin.taminhamrah.model.payment.PaymentVerifierKey
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class UnconfirmedMockPaymentVerifierTest {

    @Test
    fun `it answers only the sandbox key, so it cannot shadow a real verifier`() {
        assertEquals(PaymentVerifierKey.MOCK_UNCONFIRMED, UnconfirmedMockPaymentVerifier().key)
    }

    @Test
    fun `it always refuses, with a reason the result screen can show`() = runTest {
        val failure = assertFailsWith<IllegalStateException> {
            UnconfirmedMockPaymentVerifier().verify("anything")
        }

        assertTrue(failure.message.orEmpty().isNotBlank())
    }

    @Test
    fun `only the unconfirmed scenario routes to it`() {
        // Every other scenario carries the verifier key of the service that will really own it,
        // so wiring a real verifier later needs no change here.
        val usingMockKey = PaymentMockScenario.entries
            .filter { it.verifierKey == PaymentVerifierKey.MOCK_UNCONFIRMED }

        assertEquals(listOf(PaymentMockScenario.PAID_BUT_UNCONFIRMED), usingMockKey)
    }
}
