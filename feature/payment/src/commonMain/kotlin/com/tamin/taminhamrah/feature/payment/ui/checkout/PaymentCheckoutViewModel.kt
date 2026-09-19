package com.tamin.taminhamrah.feature.payment.ui.checkout

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.payment.ui.checkout.contract.PaymentCheckoutEvent
import com.tamin.taminhamrah.feature.payment.ui.checkout.contract.PaymentCheckoutIntent
import com.tamin.taminhamrah.feature.payment.ui.checkout.contract.PaymentCheckoutUiState
import com.tamin.taminhamrah.feature.payment.ui.checkout.contract.PaymentCheckoutUiState.PartialState
import com.tamin.taminhamrah.model.payment.PayerType
import com.tamin.taminhamrah.model.payment.isIdentifierValid
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.useCases.agent.GetCurrentUserNationalCodeUseCase
import com.tamin.taminhamrah.useCases.payment.CancelPaymentUseCase
import com.tamin.taminhamrah.useCases.payment.CreatePaymentLinkUseCase
import com.tamin.taminhamrah.useCases.payment.GetPaymentPreviewUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow

/**
 * The screen where a ticket becomes a payment: preview, payer, and the hand-off to the gateway.
 *
 * The ticket is held here rather than re-read from the route on every intent, because every path
 * out of this screen — paying, backing out, running out of time — has to be able to release it.
 */
class PaymentCheckoutViewModel(
    private val getPaymentPreview: GetPaymentPreviewUseCase,
    private val createPaymentLink: CreatePaymentLinkUseCase,
    private val cancelPayment: CancelPaymentUseCase,
    private val getCurrentUserNationalCode: GetCurrentUserNationalCodeUseCase,
) : BaseViewModel<PaymentCheckoutUiState, PartialState, PaymentCheckoutEvent, PaymentCheckoutIntent>(
    initialState = PaymentCheckoutUiState()
) {

    private var ticket: String = ""

    override fun handleIntent(intent: PaymentCheckoutIntent): Flow<PartialState> = when (intent) {
        is PaymentCheckoutIntent.Load -> flow {
            if (intent.ticket.isBlank()) {
                emit(PartialState.TicketMissing)
                return@flow
            }
            ticket = intent.ticket
            emit(PartialState.Loading(true))
            val preview = getPaymentPreview(intent.ticket)
            emit(PartialState.SetPreview(preview))
            getCurrentUserNationalCode()?.let { emit(PartialState.SetCurrentUserNationalCode(it)) }
            emit(PartialState.Loading(false))
        }

        is PaymentCheckoutIntent.PayerTypeSelected -> flow {
            // Switching payer type clears the identifier: a national code typed for one person is
            // never the right value for another, and leaving it behind is how the old screen
            // occasionally sent a payment under the wrong national code.
            emit(PartialState.SetPayerType(intent.payerType))
        }

        is PaymentCheckoutIntent.PayerIdentifierChanged -> flow {
            val digits = intent.identifier.digitsOnly()
            val payerType = uiState.value.payerType
            emit(
                PartialState.SetPayerIdentifier(
                    identifier = digits,
                    // An empty field is incomplete, not wrong; flagging it while the user is still
                    // typing puts an error under a field they have barely touched.
                    hasError = digits.isNotEmpty() && !payerType.isIdentifierValid(digits),
                )
            )
        }

        PaymentCheckoutIntent.PayClicked -> flow {
            val state = uiState.value
            // flatMapMerge runs intents concurrently, so two quick taps would otherwise start two
            // payments on the same ticket.
            if (!state.canSubmit) return@flow
            emit(PartialState.Submitting(true))
            val identifier = when (state.payerType) {
                PayerType.CURRENT_USER -> state.currentUserNationalCode
                else -> state.payerIdentifier
            }
            val link = createPaymentLink(ticket, state.payerType, identifier)
            if (link.isOpenable) {
                sendEvent(PaymentCheckoutEvent.OpenGateway(link.paymentUrl))
                emit(PartialState.GatewayOpened)
            } else {
                emit(PartialState.Error(link.message))
            }
            emit(PartialState.Submitting(false))
        }

        PaymentCheckoutIntent.TimerFinished -> flow {
            emit(PartialState.Expired)
            releaseTicket()
        }

        PaymentCheckoutIntent.BackRequested -> flow {
            // An expired or already-handed-off ticket has nothing left to cancel, so leaving is
            // immediate rather than asking a question with only one sensible answer.
            val state = uiState.value
            if (state.isExpired || state.hasOpenedGateway || !state.preview.isPayable) {
                sendEvent(PaymentCheckoutEvent.Cancelled)
            } else {
                emit(PartialState.ShowCancelConfirmation(true))
            }
        }

        PaymentCheckoutIntent.CancelConfirmed -> flow {
            emit(PartialState.ShowCancelConfirmation(false))
            releaseTicket()
            sendEvent(PaymentCheckoutEvent.Cancelled)
        }

        PaymentCheckoutIntent.CancelDismissed -> flow {
            emit(PartialState.ShowCancelConfirmation(false))
        }

        PaymentCheckoutIntent.ErrorDismissed -> {
            sendEvent(PaymentCheckoutEvent.Cancelled)
            emptyFlow()
        }
    }

    /**
     * Releases the ticket so the same debt can be paid again immediately.
     *
     * A failure here is deliberately not surfaced: the user is already on their way out, and the
     * gateway releases an unpaid ticket on its own once it expires.
     */
    private suspend fun releaseTicket() {
        if (ticket.isBlank()) return
        runCatching { cancelPayment(ticket) }
    }

    override fun reduceState(
        currentState: PaymentCheckoutUiState,
        partialState: PartialState,
    ): PaymentCheckoutUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.Submitting -> currentState.copy(isSubmitting = partialState.isSubmitting)
        is PartialState.Error -> currentState.copy(
            isLoading = false,
            isSubmitting = false,
            error = partialState.message,
        )

        PartialState.TicketMissing -> currentState.copy(
            isLoading = false,
            isSubmitting = false,
            isTicketMissing = true,
        )

        is PartialState.SetPreview -> currentState.copy(
            preview = partialState.preview,
            isExpired = !partialState.preview.isPayable,
        )

        is PartialState.SetCurrentUserNationalCode ->
            currentState.copy(currentUserNationalCode = partialState.nationalCode)

        is PartialState.SetPayerType -> currentState.copy(
            payerType = partialState.payerType,
            payerIdentifier = "",
            identifierError = false,
        )

        is PartialState.SetPayerIdentifier -> currentState.copy(
            payerIdentifier = partialState.identifier,
            identifierError = partialState.hasError,
        )

        PartialState.Expired -> currentState.copy(isExpired = true)
        PartialState.GatewayOpened -> currentState.copy(hasOpenedGateway = true)
        is PartialState.ShowCancelConfirmation ->
            currentState.copy(showCancelConfirmation = partialState.show)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
