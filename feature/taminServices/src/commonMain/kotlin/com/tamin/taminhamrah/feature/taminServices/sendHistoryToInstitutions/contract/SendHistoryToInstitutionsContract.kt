package com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.history.UserInfoPR

enum class SendHistoryStep { SelectType, Review }

@Immutable
data class SendHistoryToInstitutionsUiState(
    val currentStep: SendHistoryStep = SendHistoryStep.SelectType,
    val isLoading: Boolean = false,
    val isType1Selected: Boolean = false,
    val isType2Selected: Boolean = false,
    val isType3Selected: Boolean = false,
    val userInfo: UserInfoPR? = null,
    val isSendSuccess: Boolean = false,
    val error: String? = null
) {
    val hasAnyTypeSelected: Boolean get() = isType1Selected || isType2Selected || isType3Selected

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
        data class UserInfoLoaded(val userInfo: UserInfoPR) : PartialState
        data object SendSuccess : PartialState
        data object ClearSendSuccess : PartialState
        data class SetTypes(val type1: Boolean, val type2: Boolean, val type3: Boolean) : PartialState
        data object GoToNextStep : PartialState
        data object GoToPreviousStep : PartialState
    }
}

sealed interface SendHistoryToInstitutionsIntent {
    data object LoadUserInfo : SendHistoryToInstitutionsIntent
    data class ConfirmTypeSelection(val type1: Boolean, val type2: Boolean, val type3: Boolean) : SendHistoryToInstitutionsIntent
    data object GoToNextStep : SendHistoryToInstitutionsIntent
    data object GoToPreviousStep : SendHistoryToInstitutionsIntent
    data object SendToInstitution : SendHistoryToInstitutionsIntent
    data object DismissSendSuccess : SendHistoryToInstitutionsIntent
}

sealed interface SendHistoryToInstitutionsEvent {
    data class ShowToast(val message: String) : SendHistoryToInstitutionsEvent
    data object NavigateBack : SendHistoryToInstitutionsEvent
}
