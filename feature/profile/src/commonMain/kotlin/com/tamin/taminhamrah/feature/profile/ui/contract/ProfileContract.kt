package com.tamin.taminhamrah.feature.profile.ui.contract

import androidx.compose.runtime.Immutable

@Immutable
data class ProfileUiState(
    val isLoading: Boolean = false,
    val userId: String? = null,
    val errorMessage: String? = null
) {
    sealed class PartialState {
        data object Loading : PartialState()
        data class SetUserId(val userId: String?) : PartialState()
        data class Error(val message: String) : PartialState()
    }
}

sealed class ProfileIntent {
    data class LoadProfile(val userId: String? = null) : ProfileIntent()
    data object Logout : ProfileIntent()
    data class OnItemClick(val title: String) : ProfileIntent()
}

sealed class ProfileEvent {
    data object NavigateBack : ProfileEvent()
    data object NavigateToSettings : ProfileEvent()
    data class ShowToast(val message: String) : ProfileEvent()
}
