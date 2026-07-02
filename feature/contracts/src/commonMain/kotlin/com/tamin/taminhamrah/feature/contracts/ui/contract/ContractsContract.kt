package com.tamin.taminhamrah.feature.contracts.ui.contract

import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.contracts.ContractPR

data class ContractsUiState(
    val isLoading: Boolean = false,
    val contracts: List<ContractPR> = emptyList(),
    val error: String? = null,
    val newContractOptions: List<MainServiceDN> = emptyList()
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
        data class ContractsLoaded(val contracts: List<ContractPR>) : PartialState
        data class OptionsLoaded(val options: List<MainServiceDN>) : PartialState
    }
}

sealed interface ContractsIntent {
    data object LoadContracts : ContractsIntent
    data class OnServiceClick(val service: MainServiceDN) : ContractsIntent
}

sealed interface ContractsEvent {
    data class ShowToast(val message: String) : ContractsEvent
    data class NavigateToService(val flag: FeatureFlag) : ContractsEvent
    data class NavigateToWeb(val url: String) : ContractsEvent
}
