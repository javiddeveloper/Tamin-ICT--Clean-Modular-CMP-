package com.tamin.taminhamrah.feature.cartable.ui.contract

import androidx.compose.runtime.Immutable

@Immutable
data class PersonalInboxUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Error(val message: String?) : PartialState()
    }
}

sealed class PersonalInboxIntent {
    data object LoadInbox : PersonalInboxIntent()
}

sealed class PersonalInboxEvent {
    data class ShowToast(val message: String) : PersonalInboxEvent()
}
