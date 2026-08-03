package com.tamin.taminhamrah.feature.profile.ui.versionHistory.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.versionHistory.VersionHistoryPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class VersionHistoryUiState(
    val isLoading: Boolean = false,
    val lastUpdatedDate: String = "۳۰ فروردین ۱۴۰۵",
    val items: ImmutableList<VersionHistoryPR> = persistentListOf(),
    val error: String? = null
) {
    sealed interface PartialState {
        data class SetLoading(val isLoading: Boolean) : PartialState
        data class SetItems(val items: ImmutableList<VersionHistoryPR>) : PartialState
        data class ToggleExpand(val version: String) : PartialState
    }
}

sealed interface VersionHistoryIntent {
    data object LoadVersionHistory : VersionHistoryIntent
    data class ToggleExpand(val version: String) : VersionHistoryIntent
    data object OnBackClicked : VersionHistoryIntent
}

sealed interface VersionHistoryEvent {
    data object NavigateBack : VersionHistoryEvent
}
