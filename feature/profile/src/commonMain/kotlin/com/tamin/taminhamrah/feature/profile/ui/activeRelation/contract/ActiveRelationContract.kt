package com.tamin.taminhamrah.feature.profile.ui.activeRelation.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class ActiveRelationUiState(
    val isLoading: Boolean = false,
    val items: ImmutableList<ActiveRelationPR> = persistentListOf(),
    val error: String? = null,
    val activeCount: Int = 0,
    val inactiveCount: Int = 0,
    val lastCheckTime: String = ""
) {
    sealed interface PartialState {
        data class SetLoading(val isLoading: Boolean) : PartialState
        data class SetData(
            val items: ImmutableList<ActiveRelationPR>,
            val activeCount: Int,
            val inactiveCount: Int,
            val lastCheckTime: String
        ) : PartialState
        data class SetError(val error: String?) : PartialState
    }
}

sealed interface ActiveRelationIntent {
    data object LoadActiveRelations : ActiveRelationIntent
    data object OnBackClicked : ActiveRelationIntent
}

sealed interface ActiveRelationEvent {
    data object NavigateBack : ActiveRelationEvent
    data class ShowToast(val message: String) : ActiveRelationEvent
}
