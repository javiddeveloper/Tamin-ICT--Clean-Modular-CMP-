package com.tamin.taminhamrah.feature.security.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.security.ui.contract.SecurityEvent
import com.tamin.taminhamrah.feature.security.ui.contract.SecurityIntent
import com.tamin.taminhamrah.feature.security.ui.contract.SecurityUiState
import com.tamin.taminhamrah.feature.security.ui.contract.SecurityUiState.PartialState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SecurityViewModel(
    // Inject dependencies here
) : BaseViewModel<SecurityUiState, PartialState, SecurityEvent, SecurityIntent>(
    initialState = SecurityUiState()
) {

    override fun handleIntent(intent: SecurityIntent): Flow<PartialState> {
        return when (intent) {
            is SecurityIntent.OnBackClicked -> {
                sendEvent(SecurityEvent.NavigateBack)
                kotlinx.coroutines.flow.emptyFlow()
            }
        }
    }

    override fun reduceState(
        currentState: SecurityUiState,
        partialState: PartialState
    ): SecurityUiState = when (partialState) {
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
