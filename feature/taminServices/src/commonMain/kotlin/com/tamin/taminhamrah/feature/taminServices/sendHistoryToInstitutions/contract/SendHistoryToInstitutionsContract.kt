package com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.model.history.UserInfoPR

enum class SendHistoryStep { SelectType, Review }

@Immutable
data class SendHistoryToInstitutionsUiState(
    val currentStep: SendHistoryStep = SendHistoryStep.SelectType,
    val isLoading: Boolean = false,
    val selectedTypes: Set<HistoryCertificateType> = emptySet(),
    val userInfo: UserInfoPR? = null
) {
    val hasAnyTypeSelected: Boolean get() = selectedTypes.isNotEmpty()

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class UserInfoLoaded(val userInfo: UserInfoPR) : PartialState
        data class SetTypes(val selectedTypes: Set<HistoryCertificateType>) : PartialState
        data object GoToNextStep : PartialState
        data object GoToPreviousStep : PartialState
    }
}

sealed interface SendHistoryToInstitutionsIntent {
    data object LoadUserInfo : SendHistoryToInstitutionsIntent
    data class ConfirmTypeSelection(val selectedTypes: Set<HistoryCertificateType>) : SendHistoryToInstitutionsIntent
    data object GoToNextStep : SendHistoryToInstitutionsIntent
    data object GoToPreviousStep : SendHistoryToInstitutionsIntent
    data object SendToInstitution : SendHistoryToInstitutionsIntent
}

sealed interface SendHistoryToInstitutionsEvent {
    data class ShowToast(val message: String) : SendHistoryToInstitutionsEvent
    data class DisplayAccessDeniedModal(val message: String) : SendHistoryToInstitutionsEvent
    data object DisplaySuccessModal : SendHistoryToInstitutionsEvent
    data object NavigateBack : SendHistoryToInstitutionsEvent
}
