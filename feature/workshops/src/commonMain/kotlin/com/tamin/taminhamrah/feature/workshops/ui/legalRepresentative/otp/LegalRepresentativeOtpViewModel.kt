package com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.otp

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.workshops.RequestLegalRepresentativeTicketUseCase
import com.tamin.taminhamrah.useCases.workshops.VerifyLegalRepresentativeTicketUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LegalRepresentativeOtpViewModel(
    private val requestLegalRepresentativeTicketUseCase: RequestLegalRepresentativeTicketUseCase,
    private val verifyLegalRepresentativeTicketUseCase: VerifyLegalRepresentativeTicketUseCase,
) : BaseViewModel<
    LegalRepresentativeOtpUiState,
    LegalRepresentativeOtpUiState.PartialState,
    LegalRepresentativeOtpEvent,
    LegalRepresentativeOtpIntent
    >(initialState = LegalRepresentativeOtpUiState()) {

    override fun handleIntent(
        intent: LegalRepresentativeOtpIntent
    ): Flow<LegalRepresentativeOtpUiState.PartialState> = when (intent) {
        is LegalRepresentativeOtpIntent.RequestTicket -> flow {
            if (uiState.value.isRequestingTicket) return@flow
            emit(LegalRepresentativeOtpUiState.PartialState.RequestingTicket)
            try {
                requestLegalRepresentativeTicketUseCase()
                emit(LegalRepresentativeOtpUiState.PartialState.TicketRequested)
            } catch (e: Exception) {
                emit(LegalRepresentativeOtpUiState.PartialState.RequestFailed(e.toSingleLineMessage()))
            }
        }

        is LegalRepresentativeOtpIntent.OtpChanged -> flow {
            emit(LegalRepresentativeOtpUiState.PartialState.OtpChanged(intent.value))
        }

        is LegalRepresentativeOtpIntent.VerifyTicket -> flow {
            if (uiState.value.isVerifying) return@flow
            val code = uiState.value.otpCode
            emit(LegalRepresentativeOtpUiState.PartialState.Verifying)
            try {
                verifyLegalRepresentativeTicketUseCase(code)
                sendEvent(LegalRepresentativeOtpEvent.VerifiedSuccessfully(code))
            } catch (e: Exception) {
                emit(LegalRepresentativeOtpUiState.PartialState.VerifyFailed(e.toSingleLineMessage()))
            }
        }
    }

    override fun reduceState(
        currentState: LegalRepresentativeOtpUiState,
        partialState: LegalRepresentativeOtpUiState.PartialState
    ): LegalRepresentativeOtpUiState = when (partialState) {
        is LegalRepresentativeOtpUiState.PartialState.RequestingTicket ->
            currentState.copy(isRequestingTicket = true, error = null)

        is LegalRepresentativeOtpUiState.PartialState.TicketRequested ->
            currentState.copy(isRequestingTicket = false, isTicketRequested = true)

        is LegalRepresentativeOtpUiState.PartialState.RequestFailed ->
            currentState.copy(isRequestingTicket = false, error = partialState.message)

        is LegalRepresentativeOtpUiState.PartialState.OtpChanged ->
            currentState.copy(otpCode = partialState.value, error = null)

        is LegalRepresentativeOtpUiState.PartialState.Verifying ->
            currentState.copy(isVerifying = true, error = null)

        is LegalRepresentativeOtpUiState.PartialState.VerifyFailed ->
            currentState.copy(isVerifying = false, error = partialState.message)
    }

    override fun createErrorState(message: String): LegalRepresentativeOtpUiState.PartialState =
        LegalRepresentativeOtpUiState.PartialState.RequestFailed(message)
}
