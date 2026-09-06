package com.tamin.taminhamrah.feature.workshops.ui.workshopDebtInquiry

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.workshopDebtInquiry.WorkshopDebtInquiryUiState.PartialState
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.workshops.GetWorkshopDebtInquiryUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/** استعلام بدهی کارگاه. */
class WorkshopDebtInquiryViewModel(
    private val getWorkshopDebtInquiry: GetWorkshopDebtInquiryUseCase,
) : BaseViewModel<
    WorkshopDebtInquiryUiState,
    PartialState,
    WorkshopDebtInquiryEvent,
    WorkshopDebtInquiryIntent,
    >(initialState = WorkshopDebtInquiryUiState()) {

    override fun handleIntent(intent: WorkshopDebtInquiryIntent): Flow<PartialState> = when (intent) {
        is WorkshopDebtInquiryIntent.Open -> open(intent)
        WorkshopDebtInquiryIntent.Retry -> load()
    }

    private fun open(intent: WorkshopDebtInquiryIntent.Open): Flow<PartialState> = flow {
        val state = uiState.value
        if (state.workshopId == intent.workshopId && state.branchCode == intent.branchCode) return@flow
        emit(PartialState.Opened(intent.workshopId, intent.branchCode))
        emitAll(load(intent.workshopId to intent.branchCode))
    }

    private fun load(
        identity: Pair<String, String> = uiState.value.workshopId to uiState.value.branchCode,
    ): Flow<PartialState> = flow {
        val (workshopId, branchCode) = identity
        if (workshopId.isBlank() || branchCode.isBlank()) {
            emit(PartialState.Error(null))
            return@flow
        }
        emit(PartialState.Loading(true))
        emit(PartialState.Loaded(getWorkshopDebtInquiry(workshopId, branchCode).toPresentation()))
    }.catch { emit(PartialState.Error(it.toSingleLineMessage())) }

    override fun reduceState(
        currentState: WorkshopDebtInquiryUiState,
        partialState: PartialState,
    ): WorkshopDebtInquiryUiState = when (partialState) {
        is PartialState.Opened -> currentState.copy(
            workshopId = partialState.workshopId,
            branchCode = partialState.branchCode,
        )

        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.Loaded -> currentState.copy(
            isLoading = false,
            error = null,
            inquiry = partialState.inquiry,
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
