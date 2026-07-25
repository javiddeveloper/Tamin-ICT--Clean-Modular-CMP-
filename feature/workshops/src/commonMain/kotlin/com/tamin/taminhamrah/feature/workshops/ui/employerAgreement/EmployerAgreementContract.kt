package com.tamin.taminhamrah.feature.workshops.ui.employerAgreement

import com.tamin.taminhamrah.model.workshop.EmployerAgreementPR

class EmployerAgreementUiState(
    val isLoading: Boolean = false,
    val list: List<EmployerAgreementPR> = emptyList(),
    val error: String? = null
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Loaded(val list: List<EmployerAgreementPR>) : PartialState()
        data class Error(val message: String?) : PartialState()
    }
}

sealed interface EmployerAgreementEvent
sealed interface EmployerAgreementIntent {
    data class Load(val workshopId: String, val branchCode: String) : EmployerAgreementIntent
}

