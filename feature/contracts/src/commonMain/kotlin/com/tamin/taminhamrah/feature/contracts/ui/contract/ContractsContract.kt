package com.tamin.taminhamrah.feature.contracts.ui.contract

import com.tamin.taminhamrah.model.contracts.ContractPR

data class ContractsUiState(
    val isLoading: Boolean = false,
    val contracts: List<ContractPR> = emptyList(),
    val error: String? = null,
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
        data class ContractsLoaded(val contracts: List<ContractPR>) : PartialState
    }
}

sealed interface ContractsIntent {
    data object LoadContracts : ContractsIntent
}

sealed interface ContractsEvent {
    data class ShowToast(val message: String) : ContractsEvent
}
