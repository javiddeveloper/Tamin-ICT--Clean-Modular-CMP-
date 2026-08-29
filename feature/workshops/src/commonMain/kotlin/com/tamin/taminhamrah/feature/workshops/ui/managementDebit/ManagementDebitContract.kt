package com.tamin.taminhamrah.feature.workshops.ui.managementDebit

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListModelPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

// `data` on purpose: without structural equality two states holding the same values never
// compare equal, so the screen recomposes on every emission whatever the annotation promises.
@Immutable
data class ManagementDebitUiState(
    val isLoading: Boolean = false,
    val list: ImmutableList<WorkshopsDebtListModelPR> = persistentListOf(),
    val error: String? = null
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Loaded(val list: ImmutableList<WorkshopsDebtListModelPR>) : PartialState()
        data class Error(val message: String?) : PartialState()
    }
}

sealed interface ManagementDebitEvent
sealed interface ManagementDebitIntent {
    data class Load(val workshopId: String, val branchCode: String) : ManagementDebitIntent
}

