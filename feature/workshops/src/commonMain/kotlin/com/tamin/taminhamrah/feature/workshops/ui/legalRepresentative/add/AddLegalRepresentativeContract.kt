package com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.add

import androidx.compose.runtime.Immutable

@Immutable
data class AddLegalRepresentativeUiState(
    val workshopId: String = "",
    val branchCode: String = "",
    val isEditMode: Boolean = false,
    val nationalCode: String = "",
    val nationalCodeError: String? = null,
    val hasElectronicNotification: Boolean = false,
    val hasInternetList: Boolean = false,
    val hasInsuredRegistration: Boolean = false,
    val isTicketRequested: Boolean = false,
    val isRequestingTicket: Boolean = false,
    val otpCode: String = "",
    val otpError: String? = null,
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
) {
    /** The 8-character bitmask the backend expects — shown to the user as a live preview. */
    val accessCodePreview: String
        get() = buildString {
            append(if (hasElectronicNotification) '1' else '0')
            append(if (hasInternetList) '1' else '0')
            append(if (hasInsuredRegistration) '1' else '0')
            append("00000")
        }

    val canSubmit: Boolean
        get() = nationalCode.length == 10 && otpCode.isNotBlank() && !isSubmitting

    sealed interface PartialState {
        data class Init(
            val workshopId: String,
            val branchCode: String,
            val isEditMode: Boolean,
            val nationalCode: String,
            val hasElectronicNotification: Boolean,
            val hasInternetList: Boolean,
            val hasInsuredRegistration: Boolean,
        ) : PartialState

        data class NationalCodeChanged(val value: String, val error: String?) : PartialState
        data class ElectronicNotificationChanged(val value: Boolean) : PartialState
        data class InternetListChanged(val value: Boolean) : PartialState
        data class InsuredRegistrationChanged(val value: Boolean) : PartialState
        data object RequestingTicket : PartialState
        data object TicketRequested : PartialState
        data class RequestTicketFailed(val message: String?) : PartialState
        data class OtpChanged(val value: String) : PartialState
        data object Submitting : PartialState
        data object Submitted : PartialState
        data class SubmitFailed(val message: String?) : PartialState
    }
}

sealed interface AddLegalRepresentativeIntent {
    data class Init(
        val workshopId: String,
        val branchCode: String,
        val isEditMode: Boolean,
        val nationalCode: String,
        val hasElectronicNotification: Boolean,
        val hasInternetList: Boolean,
        val hasInsuredRegistration: Boolean,
    ) : AddLegalRepresentativeIntent

    data class NationalCodeChanged(val value: String) : AddLegalRepresentativeIntent
    data class ElectronicNotificationChanged(val value: Boolean) : AddLegalRepresentativeIntent
    data class InternetListChanged(val value: Boolean) : AddLegalRepresentativeIntent
    data class InsuredRegistrationChanged(val value: Boolean) : AddLegalRepresentativeIntent
    data object RequestTicket : AddLegalRepresentativeIntent
    data class OtpChanged(val value: String) : AddLegalRepresentativeIntent
    data object Submit : AddLegalRepresentativeIntent
}

sealed interface AddLegalRepresentativeEvent
