package com.tamin.taminhamrah.feature.workshops.ui.workshopMembers

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.useCases.workshops.GetWorkshopMembersUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class WorkshopMembersViewModel(
    private val useCase: GetWorkshopMembersUseCase
) : BaseViewModel<WorkshopMembersUiState, WorkshopMembersUiState.PartialState, WorkshopMembersEvent, WorkshopMembersIntent>(
    initialState = WorkshopMembersUiState()
) {
    override fun handleIntent(intent: WorkshopMembersIntent): Flow<WorkshopMembersUiState.PartialState> = when (intent) {
        is WorkshopMembersIntent.Load -> flow {
            emit(WorkshopMembersUiState.PartialState.Loading(true))
            try {
                val filters = listOf(
                    ApiFilterDN(FilterProperty.WORKSHOP_ID, intent.workshopId, FilterOperator.EQ),
                    ApiFilterDN(FilterProperty.WORKSHOP_BRANCH_CODE, intent.branchCode, FilterOperator.EQ)
                )
                val res = useCase(filters).first()
                emit(WorkshopMembersUiState.PartialState.Loaded(res?.list?.map { it.toPresentation() }?.toPersistentList() ?: persistentListOf()))
            } catch (e: Exception) {
                emit(WorkshopMembersUiState.PartialState.Error(e.message))
            }
        }
    }

    override fun reduceState(currentState: WorkshopMembersUiState, partialState: WorkshopMembersUiState.PartialState): WorkshopMembersUiState = when (partialState) {
        is WorkshopMembersUiState.PartialState.Loading -> WorkshopMembersUiState(isLoading = partialState.isLoading)
        is WorkshopMembersUiState.PartialState.Loaded -> WorkshopMembersUiState(isLoading = false, list = partialState.list)
        is WorkshopMembersUiState.PartialState.Error -> WorkshopMembersUiState(isLoading = false, error = partialState.message)
    }
    override fun createErrorState(message: String): WorkshopMembersUiState.PartialState = WorkshopMembersUiState.PartialState.Error(message)
}
