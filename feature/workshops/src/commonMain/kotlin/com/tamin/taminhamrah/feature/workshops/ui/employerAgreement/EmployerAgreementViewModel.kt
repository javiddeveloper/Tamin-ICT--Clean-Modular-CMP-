package com.tamin.taminhamrah.feature.workshops.ui.employerAgreement

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.useCases.workshops.GetAllEmployerAgreementByNationalIdUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class EmployerAgreementViewModel(
    private val useCase: GetAllEmployerAgreementByNationalIdUseCase
) : BaseViewModel<EmployerAgreementUiState, EmployerAgreementUiState.PartialState, EmployerAgreementEvent, EmployerAgreementIntent>(
    initialState = EmployerAgreementUiState()
) {
    override fun handleIntent(intent: EmployerAgreementIntent): Flow<EmployerAgreementUiState.PartialState> = when (intent) {
        is EmployerAgreementIntent.Load -> flow {
            emit(EmployerAgreementUiState.PartialState.Loading(true))
            try {
                val filters = listOf(
                    ApiFilterDN(FilterProperty.WORKSHOP_ID, intent.workshopId, FilterOperator.EQ),
                    ApiFilterDN(FilterProperty.WORKSHOP_BRANCH_CODE, intent.branchCode, FilterOperator.EQ)
                )
                val res = useCase(filters)
                emit(EmployerAgreementUiState.PartialState.Loaded(res?.list?.map { it.toPresentation() }?.toPersistentList() ?: persistentListOf()))
            } catch (e: Exception) {
                emit(EmployerAgreementUiState.PartialState.Error(e.message))
            }
        }
    }

    override fun reduceState(currentState: EmployerAgreementUiState, partialState: EmployerAgreementUiState.PartialState): EmployerAgreementUiState = when (partialState) {
        is EmployerAgreementUiState.PartialState.Loading -> EmployerAgreementUiState(isLoading = partialState.isLoading)
        is EmployerAgreementUiState.PartialState.Loaded -> EmployerAgreementUiState(isLoading = false, list = partialState.list)
        is EmployerAgreementUiState.PartialState.Error -> EmployerAgreementUiState(isLoading = false, error = partialState.message)
    }
    override fun createErrorState(message: String): EmployerAgreementUiState.PartialState = EmployerAgreementUiState.PartialState.Error(message)
}
