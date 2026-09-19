package com.tamin.taminhamrah.feature.profile.ui.saveEvents.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.profile.model.SavedEventPR

@Immutable
data class SaveEventsUiState(
    val isLoading: Boolean = false,
    val events: List<SavedEventPR> = emptyList(),
    val error: String? = null
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Success(val events: List<SavedEventPR>) : PartialState()
        data class Error(val message: String?) : PartialState()
    }
}

sealed class SaveEventsIntent {
    data object LoadData : SaveEventsIntent()
    data object NavigateBack : SaveEventsIntent()
    data class ToggleSave(val eventId: String) : SaveEventsIntent()
}

sealed interface SaveEventsEvent {
    data object NavigateBack : SaveEventsEvent
}
