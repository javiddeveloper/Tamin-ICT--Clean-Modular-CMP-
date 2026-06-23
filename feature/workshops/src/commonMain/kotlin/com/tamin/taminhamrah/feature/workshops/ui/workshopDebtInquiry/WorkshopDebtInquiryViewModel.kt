package com.tamin.taminhamrah.feature.workshops.ui.workshopDebtInquiry

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.workshopDebtInquiry.WorkshopDebtInquiryUiState.PartialState
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.useCases.workshops.GetWorkshopDebtInquiryUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WorkshopDebtInquiryViewModel(
    private val getWorkshopDebtInquiryUseCase: GetWorkshopDebtInquiryUseCase
) : BaseViewModel<WorkshopDebtInquiryUiState, PartialState, WorkshopDebtInquiryEvent, WorkshopDebtInquiryIntent>(
    initialState = WorkshopDebtInquiryUiState()
) {

    override fun handleIntent(intent: WorkshopDebtInquiryIntent): Flow<PartialState> {
        return when (intent) {
            is WorkshopDebtInquiryIntent.LoadDebtInquiry -> handleLoadDebtInquiry(intent)
        }
    }

    private fun handleLoadDebtInquiry(intent: WorkshopDebtInquiryIntent.LoadDebtInquiry): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            if (intent.workshopId.isNullOrEmpty() || intent.branchCode.isNullOrEmpty()) {
                emit(PartialState.Error("کد کارگاه و کد شعبه الزامی است"))
                return@flow
            }

            val response = getWorkshopDebtInquiryUseCase(
                workshopId = intent.workshopId,
                branchCode = intent.branchCode
            )
            val result = response?.toPresentation()
            emit(PartialState.DebtInquiryLoaded(result))

        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    override fun reduceState(
        currentState: WorkshopDebtInquiryUiState,
        partialState: PartialState
    ): WorkshopDebtInquiryUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.DebtInquiryLoaded -> currentState.copy(isLoading = false, inquiryResult = partialState.result)
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}
