package com.tamin.taminhamrah.feature.workshops.ui.managementDebit

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import com.tamin.taminhamrah.useCases.workshops.GetWorkshopsDebtsListUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class ManagementDebitViewModel(
    private val useCase: GetWorkshopsDebtsListUseCase
) : BaseViewModel<ManagementDebitUiState, ManagementDebitUiState.PartialState, ManagementDebitEvent, ManagementDebitIntent>(
    initialState = ManagementDebitUiState()
) {
    override fun handleIntent(intent: ManagementDebitIntent): Flow<ManagementDebitUiState.PartialState> = when (intent) {
        is ManagementDebitIntent.Load -> flow {
            emit(ManagementDebitUiState.PartialState.Loading(true))
            try {
                val res = useCase(intent.workshopId, intent.branchCode, emptyList()).first()
                emit(ManagementDebitUiState.PartialState.Loaded(res?.list?.map { it.toPresentation() }?.toPersistentList() ?: persistentListOf()))
            } catch (e: Exception) {
                emit(ManagementDebitUiState.PartialState.Error(e.message))
            }
        }
    }

    override fun reduceState(currentState: ManagementDebitUiState, partialState: ManagementDebitUiState.PartialState): ManagementDebitUiState = when (partialState) {
        is ManagementDebitUiState.PartialState.Loading -> ManagementDebitUiState(isLoading = partialState.isLoading)
        is ManagementDebitUiState.PartialState.Loaded -> ManagementDebitUiState(isLoading = false, list = partialState.list)
        is ManagementDebitUiState.PartialState.Error -> ManagementDebitUiState(isLoading = false, error = partialState.message)
    }
    override fun createErrorState(message: String): ManagementDebitUiState.PartialState = ManagementDebitUiState.PartialState.Error(message)
}
