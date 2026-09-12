package com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.sms

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.sms.ObjectionSmsUiState.PartialState
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.workshops.GetWorkShopObjectionSmsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/** پیامک‌های one filed objection. */
class ObjectionSmsViewModel(
    private val getObjectionSms: GetWorkShopObjectionSmsUseCase,
) : BaseViewModel<ObjectionSmsUiState, PartialState, ObjectionSmsEvent, ObjectionSmsIntent>(
    initialState = ObjectionSmsUiState()
) {

    override fun handleIntent(intent: ObjectionSmsIntent): Flow<PartialState> = when (intent) {
        is ObjectionSmsIntent.Open -> open(intent)
        ObjectionSmsIntent.LoadMore -> loadMore()
    }

    /** Re-opening the same seqNo does not refetch — returning from the document screen keeps the page. */
    private fun open(intent: ObjectionSmsIntent.Open): Flow<PartialState> = flow {
        if (uiState.value.seqNo == intent.seqNo && uiState.value.list.items.isNotEmpty()) return@flow
        emit(
            PartialState.Opened(
                seqNo = intent.seqNo,
                debitNumber = intent.debitNumber,
                objectionType = intent.objectionType,
                objectionStatus = intent.objectionStatus,
            )
        )
        emitAll(loadPage(page = 0, seqNo = intent.seqNo))
    }

    private fun loadPage(page: Int, seqNo: Long = uiState.value.seqNo): Flow<PartialState> = flow {
        emit(if (page == 0) PartialState.Loading else PartialState.LoadingMore)
        val result = getObjectionSms(seqNo, page)
        emit(
            PartialState.Loaded(
                uiState.value.list.loaded(result, isFirstPage = page == 0) { it.toPresentation() }
            )
        )
    }.catch { emit(PartialState.Error(it.toSingleLineMessage())) }

    private fun loadMore(): Flow<PartialState> {
        val list = uiState.value.list
        if (!list.canLoadMore) return flow { }
        return loadPage(page = list.nextPage)
    }

    override fun reduceState(
        currentState: ObjectionSmsUiState,
        partialState: PartialState,
    ): ObjectionSmsUiState = when (partialState) {
        is PartialState.Opened -> currentState.copy(
            seqNo = partialState.seqNo,
            debitNumber = partialState.debitNumber,
            objectionType = partialState.objectionType,
            objectionStatus = partialState.objectionStatus,
        )
        PartialState.Loading -> currentState.copy(list = currentState.list.loading())
        PartialState.LoadingMore -> currentState.copy(list = currentState.list.loadingMore())
        is PartialState.Error -> currentState.copy(list = currentState.list.failed(partialState.message))
        is PartialState.Loaded -> currentState.copy(list = partialState.list)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
