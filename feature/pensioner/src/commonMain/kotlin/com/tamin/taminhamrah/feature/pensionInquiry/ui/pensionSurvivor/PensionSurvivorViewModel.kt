package com.tamin.taminhamrah.feature.pensionInquiry.ui.pensionSurvivor

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.pensionSurvivor.contract.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PensionSurvivorViewModel : BaseViewModel<PensionSurvivorUiState, PensionSurvivorUiState.PartialState, PensionSurvivorEvent, PensionSurvivorIntent>(
    initialState = PensionSurvivorUiState()
) {
    override fun handleIntent(intent: PensionSurvivorIntent): Flow<PensionSurvivorUiState.PartialState> = flow {
        when (intent) {
            PensionSurvivorIntent.Init -> {
                // No-op for now
            }
        }
    }

    override fun reduceState(
        currentState: PensionSurvivorUiState,
        partialState: PensionSurvivorUiState.PartialState
    ): PensionSurvivorUiState = when (partialState) {
        is PensionSurvivorUiState.PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PensionSurvivorUiState.PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
    }

    override fun createErrorState(message: String): PensionSurvivorUiState.PartialState =
        PensionSurvivorUiState.PartialState.Error(message)
}
