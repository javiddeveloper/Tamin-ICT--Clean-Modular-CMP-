package com.tamin.taminhamrah.feature.workshops.ui.paymentSheets

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.paymentSheets.PaymentSheetsUiState.PartialState
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.useCases.workshops.GetAllPaymentSheetsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PaymentSheetsViewModel(
    private val getAllPaymentSheetsUseCase: GetAllPaymentSheetsUseCase
) : BaseViewModel<PaymentSheetsUiState, PartialState, PaymentSheetsEvent, PaymentSheetsIntent>(
    initialState = PaymentSheetsUiState()
) {

    override fun handleIntent(intent: PaymentSheetsIntent): Flow<PartialState> {
        return when (intent) {
            is PaymentSheetsIntent.LoadPaymentSheets -> handleLoadPaymentSheets(intent)
        }
    }

    private fun handleLoadPaymentSheets(intent: PaymentSheetsIntent.LoadPaymentSheets): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            val filters = mutableListOf<ApiFilterDN>()

            intent.workshopId?.takeIf { it.isNotEmpty() }?.let {
                filters.add(ApiFilterDN(FilterProperty.PAYMENT_WORKSHOP_ID, it, FilterOperator.EQ))
            }

            intent.branchCode?.takeIf { it.isNotEmpty() }?.let {
                filters.add(ApiFilterDN(FilterProperty.PAYMENT_BRANCH_CODE, it, FilterOperator.EQ))
            }

            intent.debitCause?.takeIf { it.isNotEmpty() }?.let {
                filters.add(ApiFilterDN(FilterProperty.DEBIT_REASON, it, FilterOperator.EQ))
            }

            intent.paymentType?.takeIf { it.isNotEmpty() }?.let {
                filters.add(ApiFilterDN(FilterProperty.PAYMENT_SHEET_STATUS, it, FilterOperator.EQ))
            }

            intent.payNumberFrom?.takeIf { it.isNotEmpty() }?.let {
                filters.add(ApiFilterDN(FilterProperty.PAY_ID_FROM, it, FilterOperator.EQ))
            }

            intent.payNumberTo?.takeIf { it.isNotEmpty() }?.let {
                filters.add(ApiFilterDN(FilterProperty.PAY_ID_TO, it, FilterOperator.EQ))
            }

            intent.dateFrom?.takeIf { it.isNotEmpty() }?.let {
                filters.add(ApiFilterDN(FilterProperty.DOC_DATE_FROM, it, FilterOperator.EQ))
            }

            intent.dateTo?.takeIf { it.isNotEmpty() }?.let {
                filters.add(ApiFilterDN(FilterProperty.DOC_DATE_TO, it, FilterOperator.EQ))
            }

            val queryParam = ApiQueryParamDN(
                page = 1,
                start = 0,
                limit = 100,
                filters = filters,
                sorts = emptyList()
            )

            val response = getAllPaymentSheetsUseCase(query = queryParam)
            val list = response?.list?.map { it.toPresentation() } ?: emptyList()
            emit(PartialState.PaymentSheetsLoaded(list))

        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    override fun reduceState(
        currentState: PaymentSheetsUiState,
        partialState: PartialState
    ): PaymentSheetsUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.PaymentSheetsLoaded -> currentState.copy(
            isLoading = false,
            paymentSheets = partialState.list
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}
