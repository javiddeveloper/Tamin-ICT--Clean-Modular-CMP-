package com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.wizard

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysBranchWorkshopPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

enum class IllDaysWizardStep {
    BranchCity,
    RestDays,
}

enum class IllDaysWizardPicker {
    None,
    Branch,
    City,
    StartDate,
    EndDate,
}

@Immutable
data class IllDaysWizardUiState(
    val currentStep: IllDaysWizardStep = IllDaysWizardStep.BranchCity,
    val isLoading: Boolean = true,
    val isCovidLoading: Boolean = false,
    val branchOptions: ImmutableList<IllDaysBranchWorkshopPR> = persistentListOf(),
    val cityOptions: ImmutableList<CityPR> = persistentListOf(),
    val selectedBranch: IllDaysBranchWorkshopPR? = null,
    val selectedCity: CityPR? = null,
    val isCovid: Boolean = false,
    val startDateLabel: String = "",
    val startDateMillis: Long? = null,
    val endDateLabel: String = "",
    val endDateMillis: Long? = null,
    val dayCount: Int? = null,
    val picker: IllDaysWizardPicker = IllDaysWizardPicker.None,
    val errorMessage: String? = null,
) {
    val canGoNextFromStep1: Boolean
        get() = selectedBranch != null && selectedCity != null

    val canGoNextFromStep2: Boolean
        get() = startDateMillis != null && endDateMillis != null &&
            (endDateMillis ?: 0L) >= (startDateMillis ?: 0L)

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class CovidLoading(val isLoading: Boolean) : PartialState
        data class BranchesLoaded(
            val branches: ImmutableList<IllDaysBranchWorkshopPR>,
            val selected: IllDaysBranchWorkshopPR?,
        ) : PartialState
        data class CitiesLoaded(val cities: ImmutableList<CityPR>) : PartialState
        data class BranchSelected(val branch: IllDaysBranchWorkshopPR) : PartialState
        data class CitySelected(val city: CityPR) : PartialState
        data class StepChanged(val step: IllDaysWizardStep) : PartialState
        data class PickerChanged(val picker: IllDaysWizardPicker) : PartialState
        data class CovidToggled(val enabled: Boolean) : PartialState
        data class RestDatesSet(
            val startMillis: Long?,
            val startLabel: String,
            val endMillis: Long?,
            val endLabel: String,
            val dayCount: Int?,
        ) : PartialState
        data class Error(val message: String?) : PartialState
    }
}

sealed interface IllDaysWizardIntent {
    data object Load : IllDaysWizardIntent
    data object Retry : IllDaysWizardIntent
    data object OpenBranchPicker : IllDaysWizardIntent
    data object OpenCityPicker : IllDaysWizardIntent
    data object DismissPicker : IllDaysWizardIntent
    data class BranchPicked(val branch: IllDaysBranchWorkshopPR) : IllDaysWizardIntent
    data class CityPicked(val city: CityPR) : IllDaysWizardIntent
    data object NextStep : IllDaysWizardIntent
    data object PreviousStep : IllDaysWizardIntent
    data class CovidChanged(val enabled: Boolean) : IllDaysWizardIntent
    data object OpenStartDatePicker : IllDaysWizardIntent
    data object OpenEndDatePicker : IllDaysWizardIntent
    data class StartDatePicked(val millis: Long, val label: String) : IllDaysWizardIntent
    data class EndDatePicked(val millis: Long, val label: String) : IllDaysWizardIntent
    data object OpenCalculate : IllDaysWizardIntent
    data object Back : IllDaysWizardIntent
}

sealed interface IllDaysWizardEvent {
    data object NavigateBack : IllDaysWizardEvent
    data object NavigateToCalculate : IllDaysWizardEvent
    data class ShowToast(val message: String) : IllDaysWizardEvent
}
