package com.tamin.taminhamrah.feature.payment.di

import com.tamin.taminhamrah.feature.payment.ui.checkout.PaymentCheckoutViewModel
import com.tamin.taminhamrah.feature.payment.ui.result.PaymentResultViewModel
import com.tamin.taminhamrah.feature.payment.verifier.UnconfirmedMockPaymentVerifier
import com.tamin.taminhamrah.repository.payment.PaymentVerifier
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val paymentModule = module {
    viewModelOf(::PaymentCheckoutViewModel)
    viewModelOf(::PaymentResultViewModel)

    // Answers only the sandbox's own key, so it can never shadow a real feature's verifier.
    single<PaymentVerifier>(named("unconfirmedMockPaymentVerifier")) {
        UnconfirmedMockPaymentVerifier()
    }
}
