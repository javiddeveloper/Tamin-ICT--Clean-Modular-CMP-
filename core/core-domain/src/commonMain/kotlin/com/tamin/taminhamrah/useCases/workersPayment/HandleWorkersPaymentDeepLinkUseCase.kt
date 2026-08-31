package com.tamin.taminhamrah.useCases.workersPayment

import com.tamin.taminhamrah.util.NetworkConstants

interface HandleWorkersPaymentDeepLinkUseCase {
    /** @return true if [uriString] was the workers-payment gateway callback and was consumed. */
    operator fun invoke(uriString: String): Boolean
}

class HandleWorkersPaymentDeepLinkUseCaseImpl(
    private val notifier: WorkersPaymentCallbackNotifier,
) : HandleWorkersPaymentDeepLinkUseCase {
    override fun invoke(uriString: String): Boolean {
        println("WorkersPaymentCallback: deep link received -> uriString=$uriString") // TEMP
        if (!uriString.startsWith(NetworkConstants.WORKERS_PAYMENT_CALLBACK)) return false
        println("WorkersPaymentCallback: matched workers_payment_callback, notifying observers") // TEMP
        notifier.notifyCallback()
        return true
    }
}
