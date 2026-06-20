package com.tamin.taminhamrah.feature.cartable.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.cartable.ui.model.CartableMenuItem

@Immutable
data class CartableUiState(
    val isLoading: Boolean = false,
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
    }
}

sealed class CartableIntent {
    data class OnItemClick(val item: CartableMenuItem) : CartableIntent()
}

sealed class CartableEvent {
    data object NavigateBack : CartableEvent()
    data object NavigateToMyRequests : CartableEvent()
    data object NavigateToPersonalInbox : CartableEvent()
    data class ShowToast(val message: String) : CartableEvent()
}
