package com.tamin.taminhamrah.feature.myinbox.ui.contract

import com.tamin.taminhamrah.model.inbox.PersonalInboxItemPR

data class MyInboxUiState(
    val isLoading: Boolean = false,
    val items: List<PersonalInboxItemPR> = emptyList(),
    val error: String? = null
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String) : PartialState
    }
}

sealed interface MyInboxIntent {
    data object OnBackClicked : MyInboxIntent
}

sealed interface MyInboxEvent {
    data object NavigateBack : MyInboxEvent
}
