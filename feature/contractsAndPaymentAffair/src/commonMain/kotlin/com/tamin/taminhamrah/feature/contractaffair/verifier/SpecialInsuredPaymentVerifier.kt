package com.tamin.taminhamrah.feature.contractaffair.verifier

import com.tamin.taminhamrah.model.payment.PaymentVerifierKey
import com.tamin.taminhamrah.repository.payment.PaymentVerifier
import com.tamin.taminhamrah.useCases.contracts.CheckInsurancePaymentStatusUseCase
import kotlinx.coroutines.flow.first

/**
 * Post-payment confirmation for special insured (حرف و مشاغل آزاد / بیمه اختیاری) payments.
 *
 * Checks payment status against the service endpoint (`sep/online-payment-widthout-back`)
 * using [CheckInsurancePaymentStatusUseCase].
 */
class SpecialInsuredPaymentVerifier(
    private val checkInsurancePaymentStatusUseCase: CheckInsurancePaymentStatusUseCase,
) : PaymentVerifier {

    override val key: PaymentVerifierKey = PaymentVerifierKey.SPECIAL_INSURED

    override suspend fun verify(reference: String): String {
        val systemType = reference.ifBlank { "03" }
        val result = checkInsurancePaymentStatusUseCase(systemType).first()
        return result?.toString() ?: ""
    }
}
