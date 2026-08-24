package com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentPR

@Immutable
data class SurvivorInfoUiState(
    val isLoading: Boolean = false,
    val survivor: SurvivorDependentPR? = null,
    val deceasedNationalId: String = "",
    val address: String = "",
    val phoneNumber: String = "",
    val mobileNumber: String = "",
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Initialized(
            val survivor: SurvivorDependentPR,
            val deceasedNationalId: String,
        ) : PartialState
        data class AddressChanged(val value: String) : PartialState
        data class PhoneNumberChanged(val value: String) : PartialState
        data class MobileNumberChanged(val value: String) : PartialState
        data class Error(val message: String) : PartialState
    }
}

sealed interface SurvivorInfoIntent {
    data class Init(
        val survivor: SurvivorDependentPR,
        val deceasedNationalId: String,
    ) : SurvivorInfoIntent

    data class AddressChanged(val value: String) : SurvivorInfoIntent
    data class PhoneNumberChanged(val value: String) : SurvivorInfoIntent
    data class MobileNumberChanged(val value: String) : SurvivorInfoIntent
    data object Save : SurvivorInfoIntent
    data object OnBack : SurvivorInfoIntent
}

sealed interface SurvivorInfoEvent {
    data class ShowError(val message: String) : SurvivorInfoEvent
    data class ShowSuccess(val message: String) : SurvivorInfoEvent
    data object NavigateBack : SurvivorInfoEvent
}
