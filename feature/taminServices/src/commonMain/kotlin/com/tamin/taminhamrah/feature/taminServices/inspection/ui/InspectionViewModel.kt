package com.tamin.taminhamrah.feature.taminServices.inspection.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.contract.InspectionEvent
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.contract.InspectionIntent
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.contract.InspectionUiState
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.contract.InspectionUiState.PartialState
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.mapper.toPR
import com.tamin.taminhamrah.useCases.inspection.GetInspectionListUseCase
import com.tamin.taminhamrah.useCases.inspection.GetBranchListUseCase
import com.tamin.taminhamrah.useCases.inspection.GetJobListUseCase
import com.tamin.taminhamrah.useCases.inspection.SubmitInspectionUseCase
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InspectionViewModel(
    private val getInspectionListUseCase: GetInspectionListUseCase,
    private val getBranchListUseCase: GetBranchListUseCase,
    private val getJobListUseCase: GetJobListUseCase,
    private val submitInspectionUseCase: SubmitInspectionUseCase
) : BaseViewModel<InspectionUiState, PartialState, InspectionEvent, InspectionIntent>(
    initialState = InspectionUiState()
) {

    override fun handleIntent(intent: InspectionIntent): Flow<PartialState> {
        return when (intent) {
            is InspectionIntent.LoadInspections -> handleLoadInspections(intent)
            is InspectionIntent.LoadBranches -> handleLoadBranches(intent)
            is InspectionIntent.LoadJobs -> handleLoadJobs(intent)
            is InspectionIntent.SubmitRequest -> handleSubmitRequest(intent)
        }
    }

    private fun handleLoadInspections(intent: InspectionIntent.LoadInspections): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            val result = getInspectionListUseCase(filters = intent.filters)
            emit(PartialState.InspectionsLoaded(result.list.map { it.toPR() }))
        } catch (e: Exception) {
            emit(PartialState.Loading(false))
            sendEvent(InspectionEvent.ShowToast(e.toSingleLineMessage()))
        }
    }

    private fun handleLoadBranches(intent: InspectionIntent.LoadBranches): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            val result = getBranchListUseCase(filters = intent.filters)
            emit(PartialState.BranchesLoaded(result.list.map { it.toPR() }))
        } catch (e: Exception) {
            emit(PartialState.Loading(false))
            sendEvent(InspectionEvent.ShowToast(e.toSingleLineMessage()))
        }
    }

    private fun handleLoadJobs(intent: InspectionIntent.LoadJobs): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            val result = getJobListUseCase(filters = intent.filters)
            emit(PartialState.JobsLoaded(result.list.map { it.toPR() }))
        } catch (e: Exception) {
            emit(PartialState.Loading(false))
            sendEvent(InspectionEvent.ShowToast(e.toSingleLineMessage()))
        }
    }

    private fun handleSubmitRequest(intent: InspectionIntent.SubmitRequest): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            val result = submitInspectionUseCase(intent.request)
            emit(PartialState.SubmitSuccess)
        } catch (e: Exception) {
            emit(PartialState.Loading(false))
            sendEvent(InspectionEvent.ShowToast(e.toSingleLineMessage()))
        }
    }

    override fun reduceState(
        currentState: InspectionUiState,
        partialState: PartialState
    ): InspectionUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.InspectionsLoaded -> currentState.copy(isLoading = false, inspections = partialState.list)
        is PartialState.BranchesLoaded -> currentState.copy(isLoading = false, branches = partialState.list)
        is PartialState.JobsLoaded -> currentState.copy(isLoading = false, jobs = partialState.list)
        is PartialState.SubmitSuccess -> currentState.copy(isLoading = false, isSubmitted = true)
    }

    override fun createErrorState(message: String): PartialState {
        sendEvent(InspectionEvent.ShowToast(message))
        return PartialState.Loading(false)
    }
}
