package com.tamin.taminhamrah.feature.pensionInquiry.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.contract.PensionInquiryUiState
import com.tamin.taminhamrah.feature.pensionInquiry.ui.contract.PensionInquiryUiState.PartialState
import com.tamin.taminhamrah.feature.pensionInquiry.ui.contract.PensionInquiryIntent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.contract.PensionInquiryEvent
import com.tamin.taminhamrah.mapper.pension.toPresentation
import com.tamin.taminhamrah.useCases.common.GetRecipientListUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionInquiryUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerIdUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge

class PensionInquiryViewModel(
    private val getPensionInquiryUseCase: GetPensionInquiryUseCase,
    private val getPensionerIdUseCase: GetPensionerIdUseCase,
    private val getRecipientListUseCase: GetRecipientListUseCase
) : BaseViewModel<PensionInquiryUiState, PartialState, PensionInquiryEvent, PensionInquiryIntent>(
    initialState = PensionInquiryUiState()
) {

    override fun handleIntent(intent: PensionInquiryIntent): Flow<PartialState> {
        return when (intent) {
            is PensionInquiryIntent.LoadPensionInquiry -> handleLoadPensionInquiry()
            is PensionInquiryIntent.LoadPensionerIds -> handleLoadPensionerIds()
            is PensionInquiryIntent.LoadRecipients -> handleLoadRecipients()
        }
    }

    private fun handleLoadRecipients(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getRecipientListUseCase().collect { list ->
                emit(PartialState.RecipientsLoaded(list.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun handleLoadPensionInquiry(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getPensionInquiryUseCase(

            ).collect { list ->
                emit(PartialState.PensionListLoaded(list.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun handleLoadPensionerIds(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getPensionerIdUseCase.invoke().collect { list ->
                emit(PartialState.PensionerIdsLoaded(list.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    override fun reduceState(
        currentState: PensionInquiryUiState,
        partialState: PartialState
    ): PensionInquiryUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.PensionListLoaded -> currentState.copy(
            isLoading = false,
            pensionList = partialState.list
        )

        is PartialState.PensionerIdsLoaded -> currentState.copy(
            isLoading = false,
            pensionerIds = partialState.list
        )

        is PartialState.RecipientsLoaded -> currentState.copy(
            isLoading = false,
            recipients = partialState.list
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}
