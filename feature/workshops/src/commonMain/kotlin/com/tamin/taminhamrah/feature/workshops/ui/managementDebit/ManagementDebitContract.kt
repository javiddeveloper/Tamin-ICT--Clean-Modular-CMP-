package com.tamin.taminhamrah.feature.workshops.ui.managementDebit

import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelPR

class ManagementDebitUiState(
    val isLoading: Boolean = false,
    val list: List<WorkshopsDebtListModelPR> = emptyList(),
    val error: String? = null
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Loaded(val list: List<WorkshopsDebtListModelPR>) : PartialState()
        data class Error(val message: String?) : PartialState()
    }
}

sealed interface ManagementDebitEvent
sealed interface ManagementDebitIntent {
    data class Load(val workshopId: String, val branchCode: String) : ManagementDebitIntent
}

