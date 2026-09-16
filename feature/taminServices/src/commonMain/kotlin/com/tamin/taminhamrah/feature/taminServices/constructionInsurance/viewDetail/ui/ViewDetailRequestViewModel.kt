package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.viewDetail.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.viewDetail.contract.ViewDetailRequestEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.viewDetail.contract.ViewDetailRequestIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.viewDetail.contract.ViewDetailRequestUiState
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.viewDetail.contract.ViewDetailRequestUiState.PartialState
import com.tamin.taminhamrah.mapper.toPR
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileSearchParamsDN
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.constructionInsurance.GetConstructionFilesUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ViewDetailRequestViewModel(
    private val getConstructionFilesUseCase: GetConstructionFilesUseCase,
) : BaseViewModel<ViewDetailRequestUiState, PartialState, ViewDetailRequestEvent, ViewDetailRequestIntent>(
    initialState = ViewDetailRequestUiState()
) {

    private var fileNumber: Long? = null
    private var requestNumber: Long? = null
    private var hasLoaded = false

    override fun handleIntent(intent: ViewDetailRequestIntent): Flow<PartialState> =
        when (intent) {
            is ViewDetailRequestIntent.Load -> {
                if (hasLoaded) {
                    emptyFlow()
                } else {
                    hasLoaded = true
                    fileNumber = intent.fileNumber
                    requestNumber = intent.requestNumber
                    loadDetail(seed = intent)
                }
            }

            ViewDetailRequestIntent.Retry -> loadDetail(seed = null)

            ViewDetailRequestIntent.OnBackClicked -> {
                sendEvent(ViewDetailRequestEvent.NavigateBack)
                emptyFlow()
            }
        }

    private fun loadDetail(seed: ViewDetailRequestIntent.Load?): Flow<PartialState> = flow {
        seed?.let { emit(PartialState.HeaderSeeded(it.fileNumber, it.requestNumber)) }
        emit(PartialState.Loading(true))
        val search = ConstructionFileSearchParamsDN(
            fileNo = fileNumber?.takeIf { it != 0L }?.toString(),
            reqNo = requestNumber?.takeIf { it != 0L }?.toString(),
            workshopId = null,
            branchCode = null,
        )
        emitAll(
            getConstructionFilesUseCase(search)
                .map { list -> PartialState.Loaded(list.map { it.toPR() }.toImmutableList()) as PartialState }
                .catch { e ->
                    val message = e.toSingleLineMessage()
                    sendEvent(ViewDetailRequestEvent.ShowError(message))
                    emit(PartialState.Error(message))
                }
        )
        emit(PartialState.Loading(false))
    }

    override fun reduceState(
        currentState: ViewDetailRequestUiState,
        partialState: PartialState,
    ): ViewDetailRequestUiState = when (partialState) {
        is PartialState.HeaderSeeded -> currentState.copy(
            fileNumber = partialState.fileNumber,
            requestNumber = partialState.requestNumber,
        )

        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)

        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)

        is PartialState.Loaded -> currentState.copy(
            isLoading = false,
            items = partialState.items,
            error = null,
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
