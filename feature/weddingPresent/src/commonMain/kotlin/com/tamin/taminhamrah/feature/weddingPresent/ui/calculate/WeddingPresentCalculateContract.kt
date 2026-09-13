package com.tamin.taminhamrah.feature.weddingPresent.ui.calculate

import androidx.compose.runtime.Immutable

@Immutable
data class WeddingPresentCalcResultUi(
    val marriageDateLabel: String,
    val averageSalaryLabel: String,
    val payableAmountLabel: String,
)

@Immutable
data class WeddingPresentCalculateUiState(
    val marriageDateLabel: String = "",
    val marriageDateMillis: Long? = null,
    val showDatePicker: Boolean = false,
    val marriageDateError: String? = null,
    val isCalculating: Boolean = false,
    val result: WeddingPresentCalcResultUi? = null,
) {
    sealed interface PartialState {
        data class MarriageDateSelected(
            val millis: Long,
            val label: String,
        ) : PartialState

        data class ShowDatePicker(val show: Boolean) : PartialState
        data class DateError(val message: String?) : PartialState
        data class Calculating(val isCalculating: Boolean) : PartialState
        data class ResultLoaded(val result: WeddingPresentCalcResultUi) : PartialState
        data object ClearResult : PartialState
        data class Error(val message: String?) : PartialState
    }
}

sealed interface WeddingPresentCalculateIntent {
    data object OpenDatePicker : WeddingPresentCalculateIntent
    data object DismissDatePicker : WeddingPresentCalculateIntent
    data class MarriageDatePicked(val millis: Long, val label: String) : WeddingPresentCalculateIntent
    data object Calculate : WeddingPresentCalculateIntent
    data object Back : WeddingPresentCalculateIntent
}

sealed interface WeddingPresentCalculateEvent {
    data object NavigateBack : WeddingPresentCalculateEvent
    data class ShowToast(val message: String) : WeddingPresentCalculateEvent
}
