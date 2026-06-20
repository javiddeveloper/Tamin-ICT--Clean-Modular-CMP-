package com.tamin.taminhamrah.feature.cartable.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.inbox.PersonalInboxItemPR
import com.tamin.taminhamrah.model.inbox.PersonalInboxSizePR

@Immutable
data class PersonalInboxUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val items: List<PersonalInboxItemPR> = emptyList(),
    val size: PersonalInboxSizePR? = null,
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Error(val message: String?) : PartialState()
        data class ItemsLoaded(val items: List<PersonalInboxItemPR>) : PartialState()
        data class SizeLoaded(val size: PersonalInboxSizePR) : PartialState()
    }
}

sealed class PersonalInboxIntent {
    data object LoadInbox : PersonalInboxIntent()
}

sealed class PersonalInboxEvent {
    data class ShowToast(val message: String) : PersonalInboxEvent()
}
