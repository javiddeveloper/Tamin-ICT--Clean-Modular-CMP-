package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.contract.ConstructionInsuranceEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.contract.ConstructionInsuranceIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.contract.ConstructionInsurancePartialState
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.contract.ConstructionInsuranceState
import com.tamin.taminhamrah.mapper.toPR
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileSearchParamsDN
import com.tamin.taminhamrah.useCases.constructionInsurance.GetConstructionFilesUseCase
import com.tamin.taminhamrah.useCases.user.GetIdentityInfoUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.Res.string
import taminx.core.core_ui.operation_request_selected
import taminx.core.core_ui.workshop_error_receive_data

class ConstructionInsuranceViewModel(
    private val getConstructionFilesUseCase: GetConstructionFilesUseCase,
    private val getIdentityInfoUseCase: GetIdentityInfoUseCase,
) : BaseViewModel<ConstructionInsuranceState, ConstructionInsurancePartialState, ConstructionInsuranceEvent, ConstructionInsuranceIntent>(
    initialState = ConstructionInsuranceState(isLoading = true)
) {

    init {
        sendIntent(ConstructionInsuranceIntent.LoadData)
    }

    override fun handleIntent(intent: ConstructionInsuranceIntent): Flow<ConstructionInsurancePartialState> =
        flow {
            val state = uiState.value
            when (intent) {
                is ConstructionInsuranceIntent.LoadData -> {
                    emit(ConstructionInsurancePartialState.Loading(true))
                    emitAll(merge(loadIdentity(), fetchData(search = null)))
                }

                is ConstructionInsuranceIntent.Refresh -> {
                    emit(ConstructionInsurancePartialState.Loading(true))
                    emitAll(fetchData(search = buildSearchParams(state)))
                }

                is ConstructionInsuranceIntent.ToggleSearchExpanded -> {
                    emit(ConstructionInsurancePartialState.SearchExpandedToggled(intent.expanded))
                }

                is ConstructionInsuranceIntent.OnFileNoQueryChanged -> {
                    emit(
                        ConstructionInsurancePartialState.SearchQueriesChanged(
                            fileNo = intent.query,
                            reqNo = state.reqNoQuery,
                            workshopId = state.workshopIdQuery,
                            branchCode = state.branchCodeQuery
                        )
                    )
                }

                is ConstructionInsuranceIntent.OnReqNoQueryChanged -> {
                    emit(
                        ConstructionInsurancePartialState.SearchQueriesChanged(
                            fileNo = state.fileNoQuery,
                            reqNo = intent.query,
                            workshopId = state.workshopIdQuery,
                            branchCode = state.branchCodeQuery
                        )
                    )
                }

                is ConstructionInsuranceIntent.OnWorkshopIdQueryChanged -> {
                    emit(
                        ConstructionInsurancePartialState.SearchQueriesChanged(
                            fileNo = state.fileNoQuery,
                            reqNo = state.reqNoQuery,
                            workshopId = intent.query,
                            branchCode = state.branchCodeQuery
                        )
                    )
                }

                is ConstructionInsuranceIntent.OnBranchCodeQueryChanged -> {
                    emit(
                        ConstructionInsurancePartialState.SearchQueriesChanged(
                            fileNo = state.fileNoQuery,
                            reqNo = state.reqNoQuery,
                            workshopId = state.workshopIdQuery,
                            branchCode = intent.query
                        )
                    )
                }

                is ConstructionInsuranceIntent.ExecuteSearch -> {
                    emit(ConstructionInsurancePartialState.Loading(true))
                    emitAll(fetchData(search = buildSearchParams(state)))
                }

                is ConstructionInsuranceIntent.ResetSearch -> {
                    emit(
                        ConstructionInsurancePartialState.SearchQueriesChanged(
                            fileNo = "",
                            reqNo = "",
                            workshopId = "",
                            branchCode = ""
                        )
                    )
                    emit(ConstructionInsurancePartialState.Loading(true))
                    emitAll(fetchData(search = null))
                }

                is ConstructionInsuranceIntent.OnDetailClick -> {
                    sendEvent(ConstructionInsuranceEvent.NavigateToDetails(intent.item))
                }

                is ConstructionInsuranceIntent.OnActionClick -> {
                    sendEvent(ConstructionInsuranceEvent.ShowToast(getString(Res.string.operation_request_selected)))
                }
            }
        }

    private fun loadIdentity(): Flow<ConstructionInsurancePartialState> = flow {
        getIdentityInfoUseCase().collect { identity ->
            val name = listOfNotNull(identity.firstName, identity.lastName)
                .filter { it.isNotBlank() }
                .joinToString(" ")
                .trim()
            val nationalCode = identity.nationalId.orEmpty()
            emit(
                ConstructionInsurancePartialState.IdentityLoaded(
                    userName = name,
                    nationalCode = nationalCode
                )
            )
        }
    }.catch { /* degraded to blank if fails */ }

    private fun fetchData(
        search: ConstructionFileSearchParamsDN?
    ): Flow<ConstructionInsurancePartialState> = flow {
        emitAll(
            getConstructionFilesUseCase(search)
                .map { list ->
                    val prItems = list.map { it.toPR() }.toImmutableList()
                    ConstructionInsurancePartialState.DataLoaded(
                        items = prItems
                    ) as ConstructionInsurancePartialState
                }
                .catch { e ->
                    emit(
                        ConstructionInsurancePartialState.Error(
                            e.message ?: getString(string.workshop_error_receive_data)
                        )
                    )
                }
        )
    }

    private fun buildSearchParams(state: ConstructionInsuranceState): ConstructionFileSearchParamsDN? {
        if (state.fileNoQuery.isBlank() &&
            state.reqNoQuery.isBlank() &&
            state.workshopIdQuery.isBlank() &&
            state.branchCodeQuery.isBlank()
        ) {
            return null
        }
        return ConstructionFileSearchParamsDN(
            fileNo = state.fileNoQuery.takeIf { it.isNotBlank() },
            reqNo = state.reqNoQuery.takeIf { it.isNotBlank() },
            workshopId = state.workshopIdQuery.takeIf { it.isNotBlank() },
            branchCode = state.branchCodeQuery.takeIf { it.isNotBlank() }
        )
    }

    override fun reduceState(
        currentState: ConstructionInsuranceState,
        partialState: ConstructionInsurancePartialState
    ): ConstructionInsuranceState = when (partialState) {
        is ConstructionInsurancePartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading,
            error = null
        )

        is ConstructionInsurancePartialState.IdentityLoaded -> currentState.copy(
            userName = partialState.userName,
            nationalCode = partialState.nationalCode
        )

        is ConstructionInsurancePartialState.DataLoaded -> currentState.copy(
            isLoading = false,
            items = partialState.items,
            error = null
        )

        is ConstructionInsurancePartialState.SearchQueriesChanged -> currentState.copy(
            fileNoQuery = partialState.fileNo,
            reqNoQuery = partialState.reqNo,
            workshopIdQuery = partialState.workshopId,
            branchCodeQuery = partialState.branchCode
        )

        is ConstructionInsurancePartialState.SearchExpandedToggled -> currentState.copy(
            isSearchExpanded = partialState.expanded
        )

        is ConstructionInsurancePartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )
    }

    override fun createErrorState(message: String): ConstructionInsurancePartialState =
        ConstructionInsurancePartialState.Error(message)
}
