package com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.pension.PensionIdPR
import com.tamin.taminhamrah.model.pension.EdictPensionerPR

@Immutable
data class EdictUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val pensionerIds: List<PensionIdPR> = emptyList(),
    val selectedPensionerId: String? = null,
    val startDate: String = "",
    val edictPensioner: EdictPensionerPR? = null,
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
        data class PensionerIdsLoaded(val list: List<PensionIdPR>) : PartialState
        data class SelectedPensionerIdChanged(val id: String) : PartialState
        data class StartDateChanged(val date: String) : PartialState
        data class EdictLoaded(val edict: EdictPensionerPR?) : PartialState
    }
}

sealed interface EdictIntent {
    data object LoadPensionerIds : EdictIntent
    data class ChangeSelectedPensionerId(val id: String) : EdictIntent
    data class ChangeStartDate(val date: String) : EdictIntent
    data object LoadEdict : EdictIntent
}

sealed interface EdictEvent {
    data class ShowToast(val message: String) : EdictEvent
}
