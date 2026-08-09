package com.tamin.taminhamrah.feature.workshops.ui.contract

import com.tamin.taminhamrah.model.workshop.EmployerAgreementPR

data class WorkshopsUiState(
    val isLoading: Boolean = false,
    val agreements: List<EmployerAgreementPR> = emptyList(),
    val error: String? = null
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
        data class WorkshopsLoaded(val list: List<EmployerAgreementPR>) : PartialState
    }
}

sealed interface WorkshopsIntent {
    data class LoadWorkshops(
        val workshopId: String? = null,
        val branchCode: String? = null,
        val workshopStatus: String? = null
    ) : WorkshopsIntent
    
    data object TestDownloadPdf : WorkshopsIntent
}

sealed interface WorkshopsEvent {
    data class ShowToast(val message: String) : WorkshopsEvent
}
