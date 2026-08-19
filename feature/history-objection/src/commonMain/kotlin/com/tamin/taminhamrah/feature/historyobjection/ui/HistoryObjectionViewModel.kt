package com.tamin.taminhamrah.feature.historyobjection.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionEvent
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionIntent
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionUiState
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionUiState.PartialState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class HistoryObjectionViewModel(
) : BaseViewModel<HistoryObjectionUiState, PartialState, HistoryObjectionEvent, HistoryObjectionIntent>(
    initialState = HistoryObjectionUiState()
) {

    init {

    }

    override fun handleIntent(intent: HistoryObjectionIntent): Flow<PartialState> = when (intent) {
        HistoryObjectionIntent.OnAddNewObjectionClicked -> {
            sendEvent(HistoryObjectionEvent.NavigateToAddNewObjection)
            emptyFlow()
        }
    }


    override fun reduceState(
        currentState: HistoryObjectionUiState,
        partialState: PartialState,
    ): HistoryObjectionUiState = when (partialState) {

        else -> {
            currentState
        }
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)

    private companion object {

    }
}



