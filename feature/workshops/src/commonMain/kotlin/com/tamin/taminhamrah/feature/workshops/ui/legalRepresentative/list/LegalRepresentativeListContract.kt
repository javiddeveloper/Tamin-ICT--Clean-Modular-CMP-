package com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.list

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.workshop.LegalRepresentativePR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class LegalRepresentativeListUiState(
    val workshopId: String = "",
    val branchCode: String = "",
    val ticket: String = "",
    val isLoading: Boolean = false,
    val representatives: ImmutableList<LegalRepresentativePR> = persistentListOf(),
    val error: String? = null,
    val expandedStakeId: Long? = null,
    val menuOpenStakeId: Long? = null,
    val deleteTarget: LegalRepresentativePR? = null,
    val isDeleting: Boolean = false,
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Loaded(val representatives: ImmutableList<LegalRepresentativePR>) : PartialState
        data class Error(val message: String?) : PartialState
        data class ToggleExpand(val stakeId: Long?) : PartialState
        data class ToggleMenu(val stakeId: Long?) : PartialState
        data class SetDeleteTarget(val target: LegalRepresentativePR?) : PartialState
        data object Deleting : PartialState
        data object Deleted : PartialState
        data class DeleteFailed(val message: String?) : PartialState
    }
}

sealed interface LegalRepresentativeListIntent {
    data class Load(val workshopId: String, val branchCode: String, val ticket: String) :
        LegalRepresentativeListIntent

    data class ToggleExpand(val stakeId: Long) : LegalRepresentativeListIntent
    data class ToggleMenu(val stakeId: Long?) : LegalRepresentativeListIntent
    data class RequestDelete(val target: LegalRepresentativePR) : LegalRepresentativeListIntent
    data object CancelDelete : LegalRepresentativeListIntent
    data object ConfirmDelete : LegalRepresentativeListIntent
    data class EditClicked(val target: LegalRepresentativePR) : LegalRepresentativeListIntent
    data object AddClicked : LegalRepresentativeListIntent
}

sealed interface LegalRepresentativeListEvent {
    data class NavigateToEdit(val target: LegalRepresentativePR) : LegalRepresentativeListEvent
    data object NavigateToAdd : LegalRepresentativeListEvent
}
