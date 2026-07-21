package com.tamin.taminhamrah.feature.healthProfile.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileUiState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileUiState.PartialState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileEvent
import com.tamin.taminhamrah.feature.healthProfile.ui.model.HealthProfileMockData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay

class HealthProfileViewModel : BaseViewModel<HealthProfileUiState, PartialState, HealthProfileEvent, HealthProfileIntent>(
    initialState = HealthProfileUiState()
) {

    override fun handleIntent(intent: HealthProfileIntent): Flow<PartialState> {
        return when (intent) {
            is HealthProfileIntent.LoadHealthProfile -> handleLoadHealthProfile()
        }
    }

    private fun handleLoadHealthProfile(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        // Add a slight delay to simulate network call loading effect
        delay(500)
        emit(PartialState.GeneralLoaded(HealthProfileMockData.generalInfo))
        emit(PartialState.LifestyleLoaded(HealthProfileMockData.lifestyleInfo))
        emit(PartialState.AllergiesLoaded(HealthProfileMockData.drugAllergies))
    }

    override fun reduceState(
        currentState: HealthProfileUiState,
        partialState: PartialState
    ): HealthProfileUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading,
            error = null
        )
        is PartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )
        is PartialState.GeneralLoaded -> currentState.copy(
            isLoading = false,
            generalInfo = partialState.info
        )
        is PartialState.LifestyleLoaded -> currentState.copy(
            isLoading = false,
            lifestyleInfo = partialState.info
        )
        is PartialState.AllergiesLoaded -> currentState.copy(
            isLoading = false,
            drugAllergies = partialState.list
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}
