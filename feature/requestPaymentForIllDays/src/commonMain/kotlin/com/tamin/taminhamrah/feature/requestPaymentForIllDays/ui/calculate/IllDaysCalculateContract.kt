package com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.calculate

import androidx.compose.runtime.Immutable

enum class IllDaysMaritalStatus(val apiValue: String) {
    Single("1"),
    Married("2"),
}

@Immutable
data class IllDaysCalculateUiState(
    val startDateLabel: String = "",
    val startDateMillis: Long? = null,
    val endDateLabel: String = "",
    val endDateMillis: Long? = null,
    val maritalStatus: IllDaysMaritalStatus? = null,
    val activeDatePicker: IllDaysDatePicker = IllDaysDatePicker.None,
    val isCalculating: Boolean = false,
    val resultLines: List<String> = emptyList(),
    val showResultDialog: Boolean = false,
) {
    val canCalculate: Boolean
        get() = startDateMillis != null && endDateMillis != null && maritalStatus != null

    sealed interface PartialState {
        data class StartDateSelected(val millis: Long, val label: String) : PartialState
        data class EndDateSelected(val millis: Long, val label: String) : PartialState
        data class MaritalSelected(val status: IllDaysMaritalStatus) : PartialState
        data class DatePickerChanged(val picker: IllDaysDatePicker) : PartialState
        data class Calculating(val isCalculating: Boolean) : PartialState
        data class ResultLoaded(val lines: List<String>) : PartialState
        data object DismissResult : PartialState
        data class Error(val message: String?) : PartialState
    }
}

enum class IllDaysDatePicker {
    None,
    Start,
    End,
}

sealed interface IllDaysCalculateIntent {
    data object OpenStartDatePicker : IllDaysCalculateIntent
    data object OpenEndDatePicker : IllDaysCalculateIntent
    data object DismissDatePicker : IllDaysCalculateIntent
    data class StartDatePicked(val millis: Long, val label: String) : IllDaysCalculateIntent
    data class EndDatePicked(val millis: Long, val label: String) : IllDaysCalculateIntent
    data class SelectMarital(val status: IllDaysMaritalStatus) : IllDaysCalculateIntent
    data object Calculate : IllDaysCalculateIntent
    data object DismissResult : IllDaysCalculateIntent
    data object Back : IllDaysCalculateIntent
}

sealed interface IllDaysCalculateEvent {
    data object NavigateBack : IllDaysCalculateEvent
    data class ShowToast(val message: String) : IllDaysCalculateEvent
}
