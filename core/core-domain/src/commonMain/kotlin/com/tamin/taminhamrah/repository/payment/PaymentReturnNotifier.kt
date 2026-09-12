package com.tamin.taminhamrah.repository.payment

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Carries "the user just came back from the payment gateway" from the platform layer, which sees
 * the deep link, to whichever screen is waiting on that payment.
 *
 * The result screen also re-checks on resume, so a missed notification costs a moment rather than
 * a wrong answer — this only makes the common case immediate.
 */
interface PaymentReturnNotifier {
    /** Emits the ticket the deep link came back with, or blank when the link carried none. */
    val returns: Flow<String>

    /** Called from the platform layer when a `mytamin://payment_callback` link arrives. */
    suspend fun notifyReturn(ticket: String)
}

class PaymentReturnNotifierImpl : PaymentReturnNotifier {

    // extraBufferCapacity keeps notifyReturn from suspending when nothing is listening yet, which
    // happens whenever the deep link arrives before the result screen has been composed.
    private val _returns = MutableSharedFlow<String>(replay = 1, extraBufferCapacity = 1)

    override val returns: Flow<String> = _returns.asSharedFlow()

    override suspend fun notifyReturn(ticket: String) {
        _returns.emit(ticket)
    }
}
