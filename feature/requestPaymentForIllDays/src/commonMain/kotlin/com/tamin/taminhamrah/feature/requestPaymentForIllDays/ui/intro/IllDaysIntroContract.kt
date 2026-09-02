package com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.intro

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysInsuredMainInfoPR

@Immutable
data class IllDaysIntroUiState(
    val isLoading: Boolean = true,
    val insuredInfo: IllDaysInsuredMainInfoPR? = null,
    val errorMessage: String? = null,
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Loaded(val insuredInfo: IllDaysInsuredMainInfoPR) : PartialState
        data class Error(val message: String?) : PartialState
    }
}

sealed interface IllDaysIntroIntent {
    data object Load : IllDaysIntroIntent
    data object Retry : IllDaysIntroIntent
    data object OpenCalculate : IllDaysIntroIntent
    data object StartRequest : IllDaysIntroIntent
    data object Back : IllDaysIntroIntent
}

sealed interface IllDaysIntroEvent {
    data object NavigateBack : IllDaysIntroEvent
    data object NavigateToCalculate : IllDaysIntroEvent
    data object NavigateToWizard : IllDaysIntroEvent
    data class ShowToast(val message: String) : IllDaysIntroEvent
}
