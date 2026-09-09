package com.tamin.taminhamrah.useCases.payment

import com.tamin.taminhamrah.model.payment.PayerType
import com.tamin.taminhamrah.model.payment.PaymentLinkDN
import com.tamin.taminhamrah.model.payment.PaymentPreviewDN
import com.tamin.taminhamrah.model.payment.PaymentStatus
import com.tamin.taminhamrah.model.payment.PaymentVerifierKey
import com.tamin.taminhamrah.repository.payment.PaymentGatewayRepository
import com.tamin.taminhamrah.repository.payment.PaymentVerifier
import kotlinx.coroutines.test.runTest
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * `VerifyPaymentUseCase` is wired with Koin's `getAll()` so that a feature can contribute a
 * verifier without core-domain — or any shared list — having to know that the feature exists.
 *
 * That is a runtime binding, not a compile-time one, so it is worth a test of its own: both the
 * empty case (no feature has registered one yet) and the several-features case have to work, and
 * either failing would surface as a crash on the way back from a real payment.
 */
class PaymentVerifierWiringTest {

    private val gatewayModule = module {
        single<PaymentGatewayRepository> { StubGatewayRepository() }
        factory { VerifyPaymentUseCase(repository = get(), verifiers = getAll()) }
    }

    @AfterTest
    fun tearDown() = stopKoin()

    @Test
    fun `resolves with no verifiers registered at all`() = runTest {
        val koin = startKoin { modules(gatewayModule) }.koin

        val outcome = koin.get<VerifyPaymentUseCase>()(
            ticket = "t-1",
            verifierKey = PaymentVerifierKey.NONE,
            verifierReference = "",
        )

        assertTrue(outcome.isFullySuccessful)
    }

    @Test
    fun `collects verifiers registered separately, the way feature modules register them`() =
        runTest {
            val koin = startKoin {
                modules(
                    gatewayModule,
                    module {
                        single<PaymentVerifier>(named("workshopDebit")) {
                            StubVerifier(PaymentVerifierKey.WORKSHOP_DEBIT_INSTALLMENT)
                        }
                    },
                    module {
                        single<PaymentVerifier>(named("specialInsured")) {
                            StubVerifier(PaymentVerifierKey.SPECIAL_INSURED)
                        }
                    },
                )
            }.koin

            val outcome = koin.get<VerifyPaymentUseCase>()(
                ticket = "t-1",
                verifierKey = PaymentVerifierKey.SPECIAL_INSURED,
                verifierReference = "03",
            )

            assertEquals(
                "verified:SPECIAL_INSURED:03",
                (outcome.verification as? com.tamin.taminhamrah.model.payment.PaymentVerificationDN.Confirmed)?.message,
            )
        }
}

private class StubGatewayRepository : PaymentGatewayRepository {
    override suspend fun getPreview(ticket: String) =
        PaymentPreviewDN(ticket = ticket, status = PaymentStatus.SUCCESSFUL)

    override suspend fun createPaymentLink(
        ticket: String,
        payerType: PayerType,
        payerIdentifier: String,
    ) = PaymentLinkDN()

    override suspend fun cancelPayment(ticket: String) = Unit
}

private class StubVerifier(override val key: PaymentVerifierKey) : PaymentVerifier {
    override suspend fun verify(reference: String) = "verified:${key.name}:$reference"
}
