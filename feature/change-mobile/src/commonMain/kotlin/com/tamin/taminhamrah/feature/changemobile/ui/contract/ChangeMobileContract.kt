package com.tamin.taminhamrah.feature.changemobile.ui.contract

import androidx.compose.runtime.Stable
import com.tamin.taminhamrah.feature.changemobile.ui.ChangeMobileStep

@Stable
data class ChangeMobileUiState(
    val currentStep: ChangeMobileStep = ChangeMobileStep.EnterMobile,
    val currentMobile: String = "",
    val newMobile: String = "",
    val otpCode: String = "",
    val otpHashCode: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isMobileError: Boolean = false
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
        data class SetCurrentMobile(val mobile: String) : PartialState
        data class NewMobileChanged(val value: String) : PartialState
        data class OtpChanged(val value: String) : PartialState
        data class OtpHashCodeChanged(val value: String) : PartialState
        data class MobileError(val isError: Boolean) : PartialState
        data class ChangeStep(val step: ChangeMobileStep) : PartialState
    }
}

sealed interface ChangeMobileIntent {
    data object LoadCurrentMobile : ChangeMobileIntent
    data class NewMobileChanged(val value: String) : ChangeMobileIntent
    data class OtpChanged(val value: String) : ChangeMobileIntent
    data object GetOtpCode : ChangeMobileIntent
    data object VerifyOtp : ChangeMobileIntent
    data object BackToPreviousStep : ChangeMobileIntent
}

sealed interface ChangeMobileEvent {
    data object NavigateBack : ChangeMobileEvent
    data class ShowError(val message: String) : ChangeMobileEvent
}
