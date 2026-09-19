package com.tamin.taminhamrah.feature.calculateWagePension.ui.contract

import com.tamin.taminhamrah.model.calculateWagePension.WagePensionCalculationPR
import com.tamin.taminhamrah.model.calculateWagePension.WagePensionChartItemPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

data class CalculateWagePensionUiState(
    val isLoading: Boolean = false,
    val isMultipleWorkshopLoading: Boolean = false,
    val error: String? = null,
    val calculation: WagePensionCalculationPR? = null,
    val displayedEligibleAmount: Long = 0L,
    val isMultipleWorkshopsEnabled: Boolean = false,
    val selectedChartYearIndex: Int? = null,
    val showInfoDialog: Boolean = false,
    val showMultipleWorkshopsInfoDialog: Boolean = false,
) {
    val chartItems: ImmutableList<WagePensionChartItemPR>
        get() = calculation?.chartItems?.toImmutableList() ?: persistentListOf()

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class MultipleWorkshopLoading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
        data class CalculationLoaded(val calculation: WagePensionCalculationPR) : PartialState
        data class DisplayedAmountChanged(val amount: Long) : PartialState
        data class MultipleWorkshopsEnabled(val enabled: Boolean) : PartialState
        data class ChartYearSelected(val index: Int?) : PartialState
        data class InfoDialogVisibility(val visible: Boolean) : PartialState
        data class MultipleWorkshopsInfoDialogVisibility(val visible: Boolean) : PartialState
    }
}

sealed interface CalculateWagePensionIntent {
    data object Load : CalculateWagePensionIntent
    data object Retry : CalculateWagePensionIntent
    data class MultipleWorkshopsToggled(val enabled: Boolean) : CalculateWagePensionIntent
    data class ChartYearSelected(val index: Int?) : CalculateWagePensionIntent
    data object ShowInfo : CalculateWagePensionIntent
    data object DismissInfo : CalculateWagePensionIntent
    data object DismissMultipleWorkshopsInfo : CalculateWagePensionIntent
}

sealed interface CalculateWagePensionEvent {
    data class ShowToast(val message: String) : CalculateWagePensionEvent
}
