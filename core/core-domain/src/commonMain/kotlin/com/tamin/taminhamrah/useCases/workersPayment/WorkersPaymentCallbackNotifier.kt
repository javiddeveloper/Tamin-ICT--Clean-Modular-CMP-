package com.tamin.taminhamrah.useCases.workersPayment

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * One-shot signal that the payment gateway has bounced the user back into the app via the
 * `mytamin://workers_payment_callback` deep link. Emitted from the platform deep-link entry points
 * (Android `MainActivity`, iOS `onOpenURL`) and observed by `WorkersPaymentViewModel`, which then
 * verifies the pending ticket. Must be a singleton so producer and consumer share the same instance.
 */
interface WorkersPaymentCallbackNotifier {
    val callbacks: SharedFlow<Unit>
    fun notifyCallback()
}

class WorkersPaymentCallbackNotifierImpl : WorkersPaymentCallbackNotifier {
    // Buffer one emission so a callback that arrives a beat before the ViewModel starts collecting
    // is not lost; the lifecycle-RESUMED check in WorkersPaymentRoute is the additional backstop.
    private val _callbacks = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    override val callbacks: SharedFlow<Unit> = _callbacks.asSharedFlow()

    override fun notifyCallback() {
        _callbacks.tryEmit(Unit)
    }
}
