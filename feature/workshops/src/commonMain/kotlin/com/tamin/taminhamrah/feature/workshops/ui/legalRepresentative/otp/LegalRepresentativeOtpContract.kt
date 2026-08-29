package com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.otp

import androidx.compose.runtime.Immutable

@Immutable
data class LegalRepresentativeOtpUiState(
    val isRequestingTicket: Boolean = false,
    val isVerifying: Boolean = false,
    val isTicketRequested: Boolean = false,
    val otpCode: String = "",
    val error: String? = null,
) {
    sealed interface PartialState {
        data object RequestingTicket : PartialState
        data object TicketRequested : PartialState
        data class RequestFailed(val message: String?) : PartialState
        data class OtpChanged(val value: String) : PartialState
        data object Verifying : PartialState
        data class VerifyFailed(val message: String?) : PartialState
    }
}

sealed interface LegalRepresentativeOtpIntent {
    data object RequestTicket : LegalRepresentativeOtpIntent
    data class OtpChanged(val value: String) : LegalRepresentativeOtpIntent
    data object VerifyTicket : LegalRepresentativeOtpIntent
}

sealed interface LegalRepresentativeOtpEvent {
    data class VerifiedSuccessfully(val ticket: String) : LegalRepresentativeOtpEvent
}
