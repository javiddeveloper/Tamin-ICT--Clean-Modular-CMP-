package com.tamin.taminhamrah.feature.workshops.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsEvent
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsIntent
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsUiState
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsUiState.PartialState
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.useCases.workshops.GetAllEmployerAgreementByNationalIdUseCase
import com.tamin.taminhamrah.useCases.common.GetRegistrationDeclarationFormUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WorkshopsViewModel(
    private val getAllEmployerAgreementUseCase: GetAllEmployerAgreementByNationalIdUseCase,
    private val getRegistrationDeclarationFormUseCase: GetRegistrationDeclarationFormUseCase
) : BaseViewModel<WorkshopsUiState, PartialState, WorkshopsEvent, WorkshopsIntent>(
    initialState = WorkshopsUiState()
) {

    init {
        sendIntent(WorkshopsIntent.LoadWorkshops())
    }

    override fun handleIntent(intent: WorkshopsIntent): Flow<PartialState> {
        return when (intent) {
            is WorkshopsIntent.LoadWorkshops -> handleLoadWorkshops(
                intent.workshopId,
                intent.branchCode,
                intent.workshopStatus
            )
            WorkshopsIntent.TestDownloadPdf -> handleTestDownloadPdf()
        }
    }

    private fun handleTestDownloadPdf(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            val statement = getRegistrationDeclarationFormUseCase()
            statement.execute { response ->
                println("TEST API RESPONSE: Received HttpStatement with status ${response.status}")
                sendEvent(WorkshopsEvent.ShowToast("Statement status: ${response.status}"))
            }
            emit(PartialState.Loading(false))
        } catch (e: Exception) {
            println("TEST API ERROR: ${e.message}")
            emit(PartialState.Error(e.message))
        }
    }

    private fun handleLoadWorkshops(
        workshopId: String?,
        branchCode: String?,
        workshopStatus: String?
    ): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            val filters = mutableListOf<ApiFilterDN>()

            workshopId?.takeIf { it.isNotEmpty() }?.let {
                filters.add(ApiFilterDN(FilterProperty.WORKSHOP_ID, it, FilterOperator.EQ))
            }

            branchCode?.takeIf { it.isNotEmpty() }?.let {
                filters.add(ApiFilterDN(FilterProperty.WORKSHOP_BRANCH_CODE, it, FilterOperator.EQ))
            }

            workshopStatus?.takeIf { it.isNotEmpty() }?.let {
                filters.add(ApiFilterDN(FilterProperty.WORKSHOP_STATUS_CODE, it, FilterOperator.EQ))
            }

            val response = getAllEmployerAgreementUseCase(filters = filters)
            val list = response?.list?.map { it.toPresentation() } ?: emptyList()
            emit(PartialState.WorkshopsLoaded(list))

        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    override fun reduceState(
        currentState: WorkshopsUiState,
        partialState: PartialState
    ): WorkshopsUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.WorkshopsLoaded -> currentState.copy(
            isLoading = false,
            agreements = partialState.list
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}
