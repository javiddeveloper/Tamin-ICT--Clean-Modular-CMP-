package com.tamin.taminhamrah.feature.weddingPresent.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentInfoPR

@Immutable
data class WeddingPresentUiState(
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val info: WeddingPresentInfoPR? = null,
    val isDetailsExpanded: Boolean = false,
    val marriageDateLabel: String = "",
    val marriageDateMillis: Long? = null,
    val partnerNationalCode: String = "",
    val isCommitmentChecked: Boolean = false,
    val showDatePicker: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val marriageDateError: String? = null,
    val partnerNationalCodeError: String? = null,
    val error: String? = null,
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Submitting(val isSubmitting: Boolean) : PartialState()
        data class InfoLoaded(val info: WeddingPresentInfoPR) : PartialState()
        data class DetailsExpanded(val expanded: Boolean) : PartialState()
        data class MarriageDateChanged(
            val label: String,
            val millis: Long,
        ) : PartialState()
        data class PartnerNationalCodeChanged(val value: String) : PartialState()
        data class CommitmentChecked(val checked: Boolean) : PartialState()
        data class ShowDatePicker(val show: Boolean) : PartialState()
        data class ShowSuccessDialog(val show: Boolean) : PartialState()
        data class FieldErrors(
            val marriageDateError: String? = null,
            val partnerNationalCodeError: String? = null,
        ) : PartialState()
        data class Error(val message: String?) : PartialState()
    }
}

sealed interface WeddingPresentIntent {
    data object Load : WeddingPresentIntent
    data object ToggleDetails : WeddingPresentIntent
    data object OpenDatePicker : WeddingPresentIntent
    data object DismissDatePicker : WeddingPresentIntent
    data class MarriageDatePicked(val label: String, val millis: Long) : WeddingPresentIntent
    data class PartnerNationalCodeChanged(val value: String) : WeddingPresentIntent
    data class CommitmentChecked(val checked: Boolean) : WeddingPresentIntent
    data object Submit : WeddingPresentIntent
    data object DismissSuccessDialog : WeddingPresentIntent
    data object OpenCalculate : WeddingPresentIntent
    data object DismissError : WeddingPresentIntent
}

sealed interface WeddingPresentEvent {
    data object NavigateBack : WeddingPresentEvent
    data object NavigateToCalculate : WeddingPresentEvent
    data class ShowToast(val message: String) : WeddingPresentEvent
}
