package com.tamin.taminhamrah.feature.treatment.ui.medicalConfirmations

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.treatment.ui.contract.ConfirmationsEvent
import com.tamin.taminhamrah.feature.treatment.ui.contract.ConfirmationsIntent
import com.tamin.taminhamrah.feature.treatment.ui.contract.ConfirmationsUiState
import com.tamin.taminhamrah.feature.treatment.ui.contract.ConfirmationsUiState.PartialState
import com.tamin.taminhamrah.mapper.treatment.toPresentation
import com.tamin.taminhamrah.useCases.treatment.GetMedicalAuthoritiesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MedicalConfirmationsViewModel(
    private val getMedicalAuthoritiesUseCase: GetMedicalAuthoritiesUseCase
) : BaseViewModel<ConfirmationsUiState, PartialState, ConfirmationsEvent, ConfirmationsIntent>(
    initialState = ConfirmationsUiState()
) {

    override fun handleIntent(intent: ConfirmationsIntent): Flow<PartialState> {
        return when (intent) {
            is ConfirmationsIntent.LoadList -> loadList()
        }
    }

    private fun loadList(): Flow<PartialState> = flow {
        emit(PartialState.Reset)
        emit(PartialState.Loading(true))
        try {
            getMedicalAuthoritiesUseCase().collect { list ->
                emit(PartialState.MedicalAuthoritiesLoaded(list.map { it.toPresentation() }))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    override fun reduceState(
        currentState: ConfirmationsUiState,
        partialState: PartialState
    ): ConfirmationsUiState = when (partialState) {
        is PartialState.Reset -> ConfirmationsUiState()
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.MedicalAuthoritiesLoaded -> currentState.copy(isLoading = false, medicalAuthorities = partialState.list)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
