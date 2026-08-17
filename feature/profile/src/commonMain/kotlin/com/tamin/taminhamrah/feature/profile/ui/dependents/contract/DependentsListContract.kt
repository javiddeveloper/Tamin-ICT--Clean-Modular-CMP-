package com.tamin.taminhamrah.feature.profile.ui.dependents.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.subdominant.SubdominantItemPR

@Immutable
data class DependentsListState(
    val isLoading: Boolean = false,
    val dependentsList: List<SubdominantItemPR> = emptyList(),
    val expandedIds: Set<Long> = emptySet(),
    val error: String? = null
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class DependentsLoaded(val dependents: List<SubdominantItemPR>) : PartialState()
        data class ExpandedToggled(val id: Long) : PartialState()
        data class Error(val message: String) : PartialState()
    }
}

sealed interface DependentsListIntent {
    data object InitData : DependentsListIntent
    data object OnAddDependentClicked : DependentsListIntent
    data object OnRefreshClicked : DependentsListIntent
    data class OnDependentCardToggled(val id: Long) : DependentsListIntent
}

sealed interface DependentsListEvent {
    data object NavigateToAddDependentWizard : DependentsListEvent
    data class ShowToast(val message: String) : DependentsListEvent
    data class ShowErrorDialog(val title: String, val message: String) : DependentsListEvent
}
