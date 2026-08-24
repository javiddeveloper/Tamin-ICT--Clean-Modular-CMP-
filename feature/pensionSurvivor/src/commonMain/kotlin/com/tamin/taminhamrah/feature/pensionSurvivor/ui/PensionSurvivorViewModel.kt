package com.tamin.taminhamrah.feature.pensionSurvivor.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorEvent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorIntent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PensionSurvivorViewModel : BaseViewModel<
    PensionSurvivorUiState,
    PensionSurvivorUiState.PartialState,
    PensionSurvivorEvent,
    PensionSurvivorIntent,
    >(
    initialState = PensionSurvivorUiState(),
) {
    override fun handleIntent(intent: PensionSurvivorIntent): Flow<PensionSurvivorUiState.PartialState> = flow {
        when (intent) {
            PensionSurvivorIntent.Init -> Unit
        }
    }

    override fun reduceState(
        currentState: PensionSurvivorUiState,
        partialState: PensionSurvivorUiState.PartialState,
    ): PensionSurvivorUiState = when (partialState) {
        is PensionSurvivorUiState.PartialState.Error -> currentState
    }

    override fun createErrorState(message: String): PensionSurvivorUiState.PartialState =
        PensionSurvivorUiState.PartialState.Error(message)
}
