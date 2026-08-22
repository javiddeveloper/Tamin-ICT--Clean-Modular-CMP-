package com.tamin.taminhamrah.feature.historyobjection.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.historyObjection.NotExistRequestPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class HistoryObjectionUiState(
    val isLoading: Boolean = false,
    val hasActiveRequest: Boolean = false,
    val showActiveRequestDialog: Boolean = false,
    val notExistRequests: ImmutableList<NotExistRequestPR> = persistentListOf(),
    val description: String = "",
    val error: String? = null,
    val isDeleting: Boolean = false,
    val deleteConfirmationRequestNumber: String? = null,
    val deleteConfirmationRowIndex: String? = null,
    ) {

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class StatusChecked(val hasActiveRequest: Boolean) : PartialState
        data class RequestsLoaded(val requests: ImmutableList<NotExistRequestPR>) : PartialState
        data object ActiveRequestDialogDismissed : PartialState
        data class DescriptionChanged(val description: String) : PartialState
        data class Error(val message: String) : PartialState
        data object ErrorDismissed : PartialState
        data class DeleteConfirmationShown(val requestNumber: String, val rowIndex: String?) : PartialState
        data object DeleteConfirmationHidden : PartialState
        data class Deleting(val isDeleting: Boolean) : PartialState
    }
}

sealed interface HistoryObjectionIntent {
    data object Load : HistoryObjectionIntent
    data object OnAddNewObjectionClicked : HistoryObjectionIntent
    data object OnActiveRequestDialogDismissed : HistoryObjectionIntent
    data class OnEditNotExistRequestClicked(val requestNumber: String, val rowIndex: String?) : HistoryObjectionIntent
    data class OnDeleteNotExistRequestClicked(val requestNumber: String, val rowIndex: String?) : HistoryObjectionIntent
    data object OnDeleteConfirmationDismissed : HistoryObjectionIntent
    data class OnDeleteConfirmed(val requestNumber: String, val rowIndex: String?) : HistoryObjectionIntent
    data class OnDescriptionChanged(val description: String) : HistoryObjectionIntent
    data object OnSubmitClicked : HistoryObjectionIntent
    data object OnErrorDismissed : HistoryObjectionIntent
}

sealed interface HistoryObjectionEvent {
    data object NavigateToAddNewObjection : HistoryObjectionEvent
    data class NavigateToEditNotExistRequest(val requestNumber: String, val rowIndex: String?) : HistoryObjectionEvent
    data object SubmitRequested : HistoryObjectionEvent
}
