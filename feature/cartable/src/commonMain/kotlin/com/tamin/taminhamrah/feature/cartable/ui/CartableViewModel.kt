package com.tamin.taminhamrah.feature.cartable.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.cartable.ui.contract.CartableEvent
import com.tamin.taminhamrah.feature.cartable.ui.contract.CartableIntent
import com.tamin.taminhamrah.feature.cartable.ui.contract.CartableUiState
import com.tamin.taminhamrah.feature.cartable.ui.contract.CartableUiState.PartialState
import com.tamin.taminhamrah.feature.cartable.ui.model.CartableMenuItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class CartableViewModel : BaseViewModel<CartableUiState, PartialState, CartableEvent, CartableIntent>(
    initialState = CartableUiState()
) {

    override fun handleIntent(intent: CartableIntent): Flow<PartialState> {
        return when (intent) {
            is CartableIntent.OnItemClick -> handleItemClick(intent.item)
        }
    }

    private fun handleItemClick(item: CartableMenuItem): Flow<PartialState> {
        when (item) {
            CartableMenuItem.MY_REQUESTS -> sendEvent(CartableEvent.NavigateToMyRequests)
            CartableMenuItem.PERSONAL_INBOX -> sendEvent(CartableEvent.NavigateToPersonalInbox)
        }
        return emptyFlow()
    }

    override fun reduceState(
        currentState: CartableUiState,
        partialState: PartialState
    ): CartableUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Loading(false)
}
