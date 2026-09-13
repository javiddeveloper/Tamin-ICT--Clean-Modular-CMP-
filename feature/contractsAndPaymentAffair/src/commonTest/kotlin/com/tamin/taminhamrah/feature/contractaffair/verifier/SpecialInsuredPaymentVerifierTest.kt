package com.tamin.taminhamrah.feature.contractaffair.verifier

import com.tamin.taminhamrah.model.payment.PaymentVerifierKey
import com.tamin.taminhamrah.feature.contractaffair.fake.FakeContractsRepository
import com.tamin.taminhamrah.useCases.contracts.CheckInsurancePaymentStatusUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class SpecialInsuredPaymentVerifierTest {

    private lateinit var contractsRepository: FakeContractsRepository
    private lateinit var verifier: SpecialInsuredPaymentVerifier

    @BeforeTest
    fun setUp() {
        contractsRepository = FakeContractsRepository()
        val checkStatusUseCase = CheckInsurancePaymentStatusUseCase(contractsRepository)
        verifier = SpecialInsuredPaymentVerifier(checkStatusUseCase)
    }

    @Test
    fun `verifier key is SPECIAL_INSURED`() {
        assertEquals(PaymentVerifierKey.SPECIAL_INSURED, verifier.key)
    }

    @Test
    fun `verify invokes status use case with reference systemType`() = runTest {
        contractsRepository.paymentStatusResult = "SUCCESS"

        val result = verifier.verify("03")

        assertEquals("03", contractsRepository.lastPaymentStatusSystemType)
        assertEquals("SUCCESS", result)
    }

    @Test
    fun `verify uses default 03 systemType when reference is blank`() = runTest {
        contractsRepository.paymentStatusResult = "OK"

        val result = verifier.verify("")

        assertEquals("03", contractsRepository.lastPaymentStatusSystemType)
        assertEquals("OK", result)
    }

    @Test
    fun `verify propagates repository exception`() = runTest {
        contractsRepository.shouldThrowError = true

        assertFailsWith<RuntimeException> {
            verifier.verify("03")
        }
    }
}
