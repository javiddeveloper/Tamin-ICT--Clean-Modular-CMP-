package com.tamin.taminhamrah.feature.workshops.ui.employerAgreement

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.workshop.EmployerAgreementPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

// `data` on purpose: without structural equality two states holding the same values never
// compare equal, so the screen recomposes on every emission whatever the annotation promises.
@Immutable
data class EmployerAgreementUiState(
    val isLoading: Boolean = false,
    val list: ImmutableList<EmployerAgreementPR> = persistentListOf(),
    val error: String? = null
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Loaded(val list: ImmutableList<EmployerAgreementPR>) : PartialState()
        data class Error(val message: String?) : PartialState()
    }
}

sealed interface EmployerAgreementEvent
sealed interface EmployerAgreementIntent {
    data class Load(val workshopId: String, val branchCode: String) : EmployerAgreementIntent
}

