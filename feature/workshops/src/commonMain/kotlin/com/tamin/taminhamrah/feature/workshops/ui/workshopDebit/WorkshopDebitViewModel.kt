package com.tamin.taminhamrah.feature.workshops.ui.workshopDebit

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.workshopDebit.WorkshopDebitUiState.PartialState
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

import com.tamin.taminhamrah.useCases.workshops.GetWorkshopDebitUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WorkshopDebitViewModel(
    private val getWorkshopDebitUseCase: GetWorkshopDebitUseCase
) : BaseViewModel<WorkshopDebitUiState, PartialState, WorkshopDebitEvent, WorkshopDebitIntent>(
    initialState = WorkshopDebitUiState()
) {

    override fun handleIntent(intent: WorkshopDebitIntent): Flow<PartialState> {
        return when (intent) {
            is WorkshopDebitIntent.LoadWorkshopDebit -> handleLoadWorkshopDebit(intent)
        }
    }

    private fun handleLoadWorkshopDebit(intent: WorkshopDebitIntent.LoadWorkshopDebit): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            if (intent.workshopId.isNullOrEmpty() || intent.branchCode.isNullOrEmpty()) {
                 emit(PartialState.Error("کد کارگاه و کد شعبه الزامی است"))
                 return@flow
            }

            val response = getWorkshopDebitUseCase(
                workshopId = intent.workshopId,
                branchCode = intent.branchCode
            )
            val list = response?.list?.map { it.toPresentation() }?.toPersistentList() ?: persistentListOf()
            emit(PartialState.WorkshopDebitsLoaded(list))

        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    override fun reduceState(
        currentState: WorkshopDebitUiState,
        partialState: PartialState
    ): WorkshopDebitUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.WorkshopDebitsLoaded -> currentState.copy(
            isLoading = false,
            workshopDebits = partialState.list
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}
