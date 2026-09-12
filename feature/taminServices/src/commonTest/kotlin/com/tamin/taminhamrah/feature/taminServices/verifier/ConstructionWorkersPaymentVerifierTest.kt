package com.tamin.taminhamrah.feature.taminServices.verifier

import com.tamin.taminhamrah.feature.taminServices.workersPayment.FakeWorkersPaymentRepository
import com.tamin.taminhamrah.model.payment.PaymentVerifierKey
import com.tamin.taminhamrah.useCases.workersPayment.InspectWorkersPaymentTicketUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class ConstructionWorkersPaymentVerifierTest {

    private lateinit var repository: FakeWorkersPaymentRepository
    private lateinit var verifier: ConstructionWorkersPaymentVerifier

    @BeforeTest
    fun setUp() {
        repository = FakeWorkersPaymentRepository()
        val inspectUseCase = InspectWorkersPaymentTicketUseCase(repository)
        verifier = ConstructionWorkersPaymentVerifier(inspectUseCase)
    }

    @Test
    fun `verifier key is CONSTRUCTION_WORKERS`() {
        assertEquals(PaymentVerifierKey.CONSTRUCTION_WORKERS, verifier.key)
    }

    @Test
    fun `verify invokes inspect ticket use case with reference token`() = runTest {
        repository.inspectTicketResult = "SUCCESSFUL_CONFIRMATION"

        val result = verifier.verify("WRK-TOKEN-123")

        assertEquals(null to "WRK-TOKEN-123", repository.lastInspectTicketParams)
        assertEquals("SUCCESSFUL_CONFIRMATION", result)
    }

    @Test
    fun `verify propagates repository exception`() = runTest {
        repository.shouldThrowError = true

        assertFailsWith<RuntimeException> {
            verifier.verify("WRK-TOKEN-123")
        }
    }
}
