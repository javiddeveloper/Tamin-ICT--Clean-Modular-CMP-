package com.tamin.taminhamrah.feature.pensionInquiry.ui.edict

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.contract.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class EdictViewModel : BaseViewModel<EdictUiState, EdictUiState.PartialState, EdictEvent, EdictIntent>(
    initialState = EdictUiState()
) {
    override fun handleIntent(intent: EdictIntent): Flow<EdictUiState.PartialState> = flow {
        when (intent) {
            EdictIntent.Init -> {
                // No-op for now
            }
        }
    }

    override fun reduceState(
        currentState: EdictUiState,
        partialState: EdictUiState.PartialState
    ): EdictUiState = when (partialState) {
        is EdictUiState.PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is EdictUiState.PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
    }

    override fun createErrorState(message: String): EdictUiState.PartialState =
        EdictUiState.PartialState.Error(message)
}
