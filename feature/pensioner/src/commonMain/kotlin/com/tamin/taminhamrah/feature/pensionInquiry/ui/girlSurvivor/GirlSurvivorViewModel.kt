package com.tamin.taminhamrah.feature.pensionInquiry.ui.girlSurvivor

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.girlSurvivor.contract.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GirlSurvivorViewModel : BaseViewModel<GirlSurvivorUiState, GirlSurvivorUiState.PartialState, GirlSurvivorEvent, GirlSurvivorIntent>(
    initialState = GirlSurvivorUiState()
) {
    override fun handleIntent(intent: GirlSurvivorIntent): Flow<GirlSurvivorUiState.PartialState> = flow {
        when (intent) {
            GirlSurvivorIntent.Init -> {
                // No-op for now
            }
        }
    }

    override fun reduceState(
        currentState: GirlSurvivorUiState,
        partialState: GirlSurvivorUiState.PartialState
    ): GirlSurvivorUiState = when (partialState) {
        is GirlSurvivorUiState.PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is GirlSurvivorUiState.PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
    }

    override fun createErrorState(message: String): GirlSurvivorUiState.PartialState =
        GirlSurvivorUiState.PartialState.Error(message)
}
