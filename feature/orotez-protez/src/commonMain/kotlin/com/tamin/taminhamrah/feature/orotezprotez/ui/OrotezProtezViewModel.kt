package com.tamin.taminhamrah.feature.orotezprotez.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezEvent
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezIntent
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezUiState
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezUiState.PartialState
import com.tamin.taminhamrah.model.userRequest.UserRequestPR
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class OrotezProtezViewModel : BaseViewModel<OrotezProtezUiState, PartialState, OrotezProtezEvent, OrotezProtezIntent>(
    initialState = OrotezProtezUiState()
) {

    init {
        sendIntent(OrotezProtezIntent.LoadRequests)
    }

    override fun handleIntent(intent: OrotezProtezIntent): Flow<PartialState> = when (intent) {
        is OrotezProtezIntent.LoadRequests -> flow {

        }
    }

    override fun reduceState(
        currentState: OrotezProtezUiState,
        partialState: PartialState
    ): OrotezProtezUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)

}
