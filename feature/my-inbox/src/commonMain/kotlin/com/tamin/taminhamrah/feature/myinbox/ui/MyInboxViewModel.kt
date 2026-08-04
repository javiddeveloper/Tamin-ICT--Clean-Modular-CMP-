package com.tamin.taminhamrah.feature.myinbox.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.myinbox.ui.contract.MyInboxEvent
import com.tamin.taminhamrah.feature.myinbox.ui.contract.MyInboxIntent
import com.tamin.taminhamrah.feature.myinbox.ui.contract.MyInboxUiState
import com.tamin.taminhamrah.feature.myinbox.ui.contract.MyInboxUiState.PartialState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MyInboxViewModel(
    // Inject dependencies here
) : BaseViewModel<MyInboxUiState, PartialState, MyInboxEvent, MyInboxIntent>(
    initialState = MyInboxUiState()
) {

    override fun handleIntent(intent: MyInboxIntent): Flow<PartialState> {
        return when (intent) {
            is MyInboxIntent.OnBackClicked -> {
                sendEvent(MyInboxEvent.NavigateBack)
                kotlinx.coroutines.flow.emptyFlow()
            }
        }
    }

    override fun reduceState(
        currentState: MyInboxUiState,
        partialState: PartialState
    ): MyInboxUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading
        )
        is PartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}
