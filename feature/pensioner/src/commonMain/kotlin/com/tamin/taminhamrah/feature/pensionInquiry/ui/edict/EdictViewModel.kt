package com.tamin.taminhamrah.feature.pensionInquiry.ui.edict

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.contract.*
import com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.contract.EdictUiState.PartialState
import com.tamin.taminhamrah.mapper.pension.toPresentation
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.useCases.pension.GetEdictPensionerUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerIdUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class EdictViewModel(
    private val getEdictPensionerUseCase: GetEdictPensionerUseCase,
    private val getPensionerIdUseCase: GetPensionerIdUseCase,
) : BaseViewModel<EdictUiState, PartialState, EdictEvent, EdictIntent>(
    initialState = EdictUiState()
) {

    init {
        sendIntent(EdictIntent.LoadPensionerIds)
    }

    override fun handleIntent(intent: EdictIntent): Flow<PartialState> = flow {
        when (intent) {
            is EdictIntent.LoadPensionerIds -> {
                emit(PartialState.Loading(true))
                try {
                    getPensionerIdUseCase().collect { list ->
                        val presentationList = list.toPresentation()
                        emit(PartialState.PensionerIdsLoaded(presentationList))
                        if (presentationList.isNotEmpty()) {
                            emit(PartialState.SelectedPensionerIdChanged(presentationList.first().pensionerId))
                        }
                    }
                } catch (e: Exception) {
                    emit(PartialState.Error(e.message))
                }
            }
            is EdictIntent.ChangeSelectedPensionerId -> {
                emit(PartialState.SelectedPensionerIdChanged(intent.id))
            }
            is EdictIntent.ChangeStartDate -> {
                emit(PartialState.StartDateChanged(intent.date))
            }
            is EdictIntent.LoadEdict -> {
                val state = uiState.value
                val pensionerId = state.selectedPensionerId
                if (pensionerId.isNullOrEmpty()) {
                    emit(PartialState.Error("شناسه مستمری‌بگیر یافت نشد"))
                    return@flow
                }
                emit(PartialState.Loading(true))
                try {
                    val query = ApiQueryParamDN(
                        filters = listOf(
                            ApiFilterDN(FilterProperty.PENSIONER_ID, pensionerId, FilterOperator.EQUAL),
                            ApiFilterDN(FilterProperty.START_DATE, state.startDate, FilterOperator.EQUAL)
                        )
                    )
                    getEdictPensionerUseCase(query).collect { edict ->
                        emit(PartialState.EdictLoaded(edict?.toPresentation()))
                    }
                } catch (e: Exception) {
                    emit(PartialState.Error(e.message))
                }
            }
        }
    }

    override fun reduceState(
        currentState: EdictUiState,
        partialState: PartialState
    ): EdictUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.PensionerIdsLoaded -> currentState.copy(
            isLoading = false,
            pensionerIds = partialState.list
        )
        is PartialState.SelectedPensionerIdChanged -> currentState.copy(
            selectedPensionerId = partialState.id
        )
        is PartialState.StartDateChanged -> currentState.copy(
            startDate = partialState.date
        )
        is PartialState.EdictLoaded -> currentState.copy(
            isLoading = false,
            edictPensioner = partialState.edict
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}
