package com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract

class PensionSurvivorUiState {
    sealed interface PartialState {
        data class Error(val message: String?) : PartialState
    }
}

sealed interface PensionSurvivorIntent {
    data object Init : PensionSurvivorIntent
}

sealed interface PensionSurvivorEvent {
    data class ShowToast(val message: String) : PensionSurvivorEvent
    data object NavigateBack : PensionSurvivorEvent
}
