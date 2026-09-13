package com.tamin.taminhamrah.feature.payment.ui.result

import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.payment.ui.result.contract.PaymentResultEvent
import com.tamin.taminhamrah.feature.payment.ui.result.contract.PaymentResultIntent
import com.tamin.taminhamrah.feature.payment.ui.result.contract.PaymentResultUiState
import com.tamin.taminhamrah.feature.payment.ui.result.contract.PaymentResultUiState.PartialState
import com.tamin.taminhamrah.repository.payment.PaymentReturnNotifier
import com.tamin.taminhamrah.useCases.payment.VerifyPaymentUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * What happened to the payment, once the user is back from the gateway.
 *
 * Written to work from a cold start: the ticket and the verifier key come in from the navigation
 * route, so a process killed while the browser was in front still reports the right answer when
 * the user returns.
 */
class PaymentResultViewModel(
    private val verifyPayment: VerifyPaymentUseCase,
    private val paymentReturnNotifier: PaymentReturnNotifier,
) : BaseViewModel<PaymentResultUiState, PartialState, PaymentResultEvent, PaymentResultIntent>(
    initialState = PaymentResultUiState()
) {

    private var lastCheck: PaymentResultIntent.Check? = null

    init {
        // The deep link back from the gateway is the fastest signal that the answer has changed;
        // the screen also re-checks on resume, so a missed notification costs a moment rather than
        // a wrong answer.
        viewModelScope.launch {
            paymentReturnNotifier.returns.collect {
                lastCheck?.let { check -> sendIntent(check) }
            }
        }
    }

    override fun handleIntent(intent: PaymentResultIntent): Flow<PartialState> = when (intent) {
        is PaymentResultIntent.Check -> flow {
            if (uiState.value.isLoading && lastCheck != null) return@flow
            lastCheck = intent
            emit(PartialState.Loading(true))
            val outcome = verifyPayment(
                ticket = intent.ticket,
                verifierKey = intent.verifierKey,
                verifierReference = intent.verifierReference,
            )
            emit(PartialState.SetOutcome(outcome))
            emit(PartialState.Loading(false))
        }

        PaymentResultIntent.Recheck -> flow {
            val check = lastCheck ?: return@flow
            emit(PartialState.Loading(true))
            emit(
                PartialState.SetOutcome(
                    verifyPayment(check.ticket, check.verifierKey, check.verifierReference)
                )
            )
            emit(PartialState.Loading(false))
        }

        PaymentResultIntent.DoneClicked -> {
            sendEvent(PaymentResultEvent.Finished)
            emptyFlow()
        }

        PaymentResultIntent.ErrorDismissed -> flow {
            emit(PartialState.Error(""))
        }
    }

    override fun reduceState(
        currentState: PaymentResultUiState,
        partialState: PartialState,
    ): PaymentResultUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message.takeIf { it.isNotEmpty() },
        )

        is PartialState.SetOutcome -> currentState.copy(outcome = partialState.outcome, error = null)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
