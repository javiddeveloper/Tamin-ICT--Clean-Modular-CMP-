package com.tamin.taminhamrah.feature.workshops.ui.objectionableDebit

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.useCases.workshops.GetWorkshopObjectionableDebitListUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ObjectionableDebitViewModel(
    private val useCase: GetWorkshopObjectionableDebitListUseCase
) : BaseViewModel<ObjectionableDebitUiState, ObjectionableDebitUiState.PartialState, ObjectionableDebitEvent, ObjectionableDebitIntent>(
    initialState = ObjectionableDebitUiState()
) {
    override fun handleIntent(intent: ObjectionableDebitIntent): Flow<ObjectionableDebitUiState.PartialState> = when (intent) {
        is ObjectionableDebitIntent.Load -> flow {
            emit(ObjectionableDebitUiState.PartialState.Loading(true))
            try {
                val res = useCase(intent.workshopId, intent.branchCode, emptyList())
                emit(ObjectionableDebitUiState.PartialState.Loaded(res?.list?.map { it.toPresentation() } ?: emptyList()))
            } catch (e: Exception) {
                emit(ObjectionableDebitUiState.PartialState.Error(e.message))
            }
        }
    }

    override fun reduceState(currentState: ObjectionableDebitUiState, partialState: ObjectionableDebitUiState.PartialState): ObjectionableDebitUiState = when (partialState) {
        is ObjectionableDebitUiState.PartialState.Loading -> ObjectionableDebitUiState(isLoading = partialState.isLoading)
        is ObjectionableDebitUiState.PartialState.Loaded -> ObjectionableDebitUiState(isLoading = false, list = partialState.list)
        is ObjectionableDebitUiState.PartialState.Error -> ObjectionableDebitUiState(isLoading = false, error = partialState.message)
    }
    override fun createErrorState(message: String): ObjectionableDebitUiState.PartialState = ObjectionableDebitUiState.PartialState.Error(message)
}
