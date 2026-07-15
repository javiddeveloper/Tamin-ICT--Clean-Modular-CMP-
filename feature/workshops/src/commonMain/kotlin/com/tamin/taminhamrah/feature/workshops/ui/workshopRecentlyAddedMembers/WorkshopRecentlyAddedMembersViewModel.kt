package com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.useCases.workshops.GetWorkshopRecentlyAddedMembersUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class WorkshopRecentlyAddedMembersViewModel(
    private val useCase: GetWorkshopRecentlyAddedMembersUseCase
) : BaseViewModel<WorkshopRecentlyAddedMembersUiState, WorkshopRecentlyAddedMembersUiState.PartialState, WorkshopRecentlyAddedMembersEvent, WorkshopRecentlyAddedMembersIntent>(
    initialState = WorkshopRecentlyAddedMembersUiState()
) {
    override fun handleIntent(intent: WorkshopRecentlyAddedMembersIntent): Flow<WorkshopRecentlyAddedMembersUiState.PartialState> = when (intent) {
        is WorkshopRecentlyAddedMembersIntent.Load -> flow {
            emit(WorkshopRecentlyAddedMembersUiState.PartialState.Loading(true))
            try {
                val filters = listOf(
                    ApiFilterDN(FilterProperty.WORKSHOP_ID, intent.workshopId, FilterOperator.EQ),
                    ApiFilterDN(FilterProperty.WORKSHOP_BRANCH_CODE, intent.branchCode, FilterOperator.EQ)
                )
                val res = useCase(filters).first()
                emit(WorkshopRecentlyAddedMembersUiState.PartialState.Loaded(res?.list?.map { it.toPresentation() } ?: emptyList()))
            } catch (e: Exception) {
                emit(WorkshopRecentlyAddedMembersUiState.PartialState.Error(e.message))
            }
        }
    }

    override fun reduceState(currentState: WorkshopRecentlyAddedMembersUiState, partialState: WorkshopRecentlyAddedMembersUiState.PartialState): WorkshopRecentlyAddedMembersUiState = when (partialState) {
        is WorkshopRecentlyAddedMembersUiState.PartialState.Loading -> WorkshopRecentlyAddedMembersUiState(isLoading = partialState.isLoading)
        is WorkshopRecentlyAddedMembersUiState.PartialState.Loaded -> WorkshopRecentlyAddedMembersUiState(isLoading = false, list = partialState.list)
        is WorkshopRecentlyAddedMembersUiState.PartialState.Error -> WorkshopRecentlyAddedMembersUiState(isLoading = false, error = partialState.message)
    }

    override fun createErrorState(message: String): WorkshopRecentlyAddedMembersUiState.PartialState = WorkshopRecentlyAddedMembersUiState.PartialState.Error(message)
}
