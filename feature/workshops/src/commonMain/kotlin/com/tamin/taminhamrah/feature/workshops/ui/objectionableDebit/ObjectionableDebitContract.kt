package com.tamin.taminhamrah.feature.workshops.ui.objectionableDebit

import com.tamin.taminhamrah.model.workshop.WorkShopDebtPR

class ObjectionableDebitUiState(
    val isLoading: Boolean = false,
    val list: List<WorkShopDebtPR> = emptyList(),
    val error: String? = null
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Loaded(val list: List<WorkShopDebtPR>) : PartialState()
        data class Error(val message: String?) : PartialState()
    }
}

sealed interface ObjectionableDebitEvent
sealed interface ObjectionableDebitIntent {
    data class Load(val workshopId: String, val branchCode: String) : ObjectionableDebitIntent
}

