package com.tamin.taminhamrah.feature.developerOptions.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.BaseUrlKey
import com.tamin.taminhamrah.model.payment.PaymentMockMode

data class BaseUrlItemUi(
    val key: BaseUrlKey,
    val currentUrl: String,
    val isOverridden: Boolean,
    val requiresRestart: Boolean
)

@Immutable
data class DeveloperOptionsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val items: List<BaseUrlItemUi> = emptyList(),
    val editingKey: BaseUrlKey? = null,
    /** Which stand-in the payment gateway is answering with, if any. */
    val paymentMockMode: PaymentMockMode = PaymentMockMode.DISABLED
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String) : PartialState
        data class SetItems(val items: List<BaseUrlItemUi>) : PartialState
        data class SetEditingKey(val key: BaseUrlKey?) : PartialState
        data class SetPaymentMockMode(val mode: PaymentMockMode) : PartialState
    }
}

sealed interface DeveloperOptionsIntent {
    data object OnBackClicked : DeveloperOptionsIntent
    data class OnItemClicked(val key: BaseUrlKey) : DeveloperOptionsIntent
    data class OnOverridesUpdated(val overrides: Map<BaseUrlKey, String>) : DeveloperOptionsIntent
    data class OnUrlConfirmed(val key: BaseUrlKey, val url: String) : DeveloperOptionsIntent
    data class OnResetClicked(val key: BaseUrlKey) : DeveloperOptionsIntent
    data object OnDialogDismissed : DeveloperOptionsIntent
    data class OnPaymentMockModeSelected(val mode: PaymentMockMode) : DeveloperOptionsIntent
    data class OnPaymentMockModeUpdated(val mode: PaymentMockMode) : DeveloperOptionsIntent
}

sealed interface DeveloperOptionsEvent {
    data object NavigateBack : DeveloperOptionsEvent
}
