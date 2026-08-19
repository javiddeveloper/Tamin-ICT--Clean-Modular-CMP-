package com.tamin.taminhamrah.feature.historyobjection.ui.contract

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

@Immutable
data class HistoryObjectionUiState(
    val isLoading: Boolean = false,
    ) {

    sealed interface PartialState {
        data class Error(val message: String) : PartialState

    }
}

sealed interface HistoryObjectionIntent {
    data object OnAddNewObjectionClicked : HistoryObjectionIntent
}

sealed interface HistoryObjectionEvent {
    data object NavigateToAddNewObjection : HistoryObjectionEvent
}
