package com.tamin.taminhamrah.feature.workshops.ui.workshopStackholders

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.useCases.workshops.GetWorkshopStackHoldersUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WorkshopStackholdersViewModel(
    private val useCase: GetWorkshopStackHoldersUseCase
) : BaseViewModel<WorkshopStackholdersUiState, WorkshopStackholdersUiState.PartialState, WorkshopStackholdersEvent, WorkshopStackholdersIntent>(
    initialState = WorkshopStackholdersUiState()
) {
    override fun handleIntent(intent: WorkshopStackholdersIntent): Flow<WorkshopStackholdersUiState.PartialState> = when (intent) {
        is WorkshopStackholdersIntent.Load -> flow {
            emit(WorkshopStackholdersUiState.PartialState.Loading(true))
            try {
                val filters = listOf(
                    ApiFilterDN(FilterProperty.WORKSHOPID_ID, intent.workshopId, FilterOperator.EQ),
                    ApiFilterDN(FilterProperty.WORKSHOPID_BRANCH_CODE, intent.branchCode, FilterOperator.EQ)
                )
                val res = useCase(filters)
                emit(WorkshopStackholdersUiState.PartialState.Loaded(res?.list?.map { it.toPresentation() } ?: emptyList()))
            } catch (e: Exception) {
                emit(WorkshopStackholdersUiState.PartialState.Error(e.message))
            }
        }
    }

    override fun reduceState(currentState: WorkshopStackholdersUiState, partialState: WorkshopStackholdersUiState.PartialState): WorkshopStackholdersUiState = when (partialState) {
        is WorkshopStackholdersUiState.PartialState.Loading -> WorkshopStackholdersUiState(isLoading = partialState.isLoading)
        is WorkshopStackholdersUiState.PartialState.Loaded -> WorkshopStackholdersUiState(isLoading = false, list = partialState.list)
        is WorkshopStackholdersUiState.PartialState.Error -> WorkshopStackholdersUiState(isLoading = false, error = partialState.message)
    }
    override fun createErrorState(message: String): WorkshopStackholdersUiState.PartialState = WorkshopStackholdersUiState.PartialState.Error(message)
}
