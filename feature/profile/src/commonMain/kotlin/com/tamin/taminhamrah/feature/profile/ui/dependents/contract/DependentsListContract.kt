package com.tamin.taminhamrah.feature.profile.ui.dependents.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.addDependent.DependentInfoPR

@Immutable
data class DependentsListState(
    val isLoading: Boolean = false,
    val dependentsList: List<DependentInfoPR> = emptyList(),
    val error: String? = null
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class DependentsLoaded(val dependents: List<DependentInfoPR>) : PartialState()
        data class Error(val message: String) : PartialState()
    }
}

sealed interface DependentsListIntent {
    data object InitData : DependentsListIntent
    data object OnAddDependentClicked : DependentsListIntent
    data object OnRefreshClicked : DependentsListIntent
}

sealed interface DependentsListEvent {
    data object NavigateToAddDependentWizard : DependentsListEvent
    data class ShowToast(val message: String) : DependentsListEvent
    data class ShowErrorDialog(val title: String, val message: String) : DependentsListEvent
}
