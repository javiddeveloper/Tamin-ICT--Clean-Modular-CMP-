package com.tamin.taminhamrah.feature.myinbox.ui.contract

import com.tamin.taminhamrah.model.inbox.PersonalInboxItemPR
import com.tamin.taminhamrah.model.inbox.PersonalInboxSizePR

data class MyInboxUiState(
    val isLoading: Boolean = false,
    val items: List<PersonalInboxItemPR> = emptyList(),
    val size: PersonalInboxSizePR? = null,
    val error: String? = null
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String) : PartialState
        data class ItemsLoaded(val items: List<PersonalInboxItemPR>) : PartialState
        data class SizeLoaded(val size: PersonalInboxSizePR) : PartialState
    }
}

sealed interface MyInboxIntent {
    data object LoadInbox : MyInboxIntent
    data object OnBackClicked : MyInboxIntent
    data class OnCopyClicked(val id: Long) : MyInboxIntent
    data class OnItemActionClicked(val id: Long, val actionValue: String) : MyInboxIntent
}

sealed interface MyInboxEvent {
    data object NavigateBack : MyInboxEvent
    data class CopyToClipboard(val id: Long) : MyInboxEvent
}
