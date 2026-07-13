package com.tamin.taminhamrah.feature.pensionInquiry.ui.contract

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.mapper.pension.toPresentation
import com.tamin.taminhamrah.useCases.pension.GetPensionInquiryUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PensionInquiryViewModel(
    private val getPensionInquiryUseCase: GetPensionInquiryUseCase,
) : BaseViewModel<PensionInquiryUiState, PensionInquiryUiState.PartialState, PensionInquiryEvent, PensionInquiryIntent>(
    initialState = PensionInquiryUiState()
) {

    init {
        sendIntent(PensionInquiryIntent.LoadPensionInquiry)
    }

    override fun handleIntent(intent: PensionInquiryIntent): Flow<PensionInquiryUiState.PartialState> {
        return when (intent) {
            is PensionInquiryIntent.LoadPensionInquiry -> handleLoadPensionInquiry()
        }
    }

    private fun handleLoadPensionInquiry(): Flow<PensionInquiryUiState.PartialState> = flow {
        emit(PensionInquiryUiState.PartialState.Loading(true))
        try {
            getPensionInquiryUseCase().collect { list ->
                val presentationList = list.toPresentation()
                emit(PensionInquiryUiState.PartialState.PensionListLoaded(presentationList))
            }
        } catch (e: Exception) {
            emit(PensionInquiryUiState.PartialState.Error(e.message))
        }
    }

    override fun reduceState(
        currentState: PensionInquiryUiState,
        partialState: PensionInquiryUiState.PartialState
    ): PensionInquiryUiState = when (partialState) {
        is PensionInquiryUiState.PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PensionInquiryUiState.PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PensionInquiryUiState.PartialState.PensionListLoaded -> currentState.copy(
            isLoading = false,
            pensionList = partialState.list
        )
    }

    override fun createErrorState(message: String): PensionInquiryUiState.PartialState =
        PensionInquiryUiState.PartialState.Error(message)
}
