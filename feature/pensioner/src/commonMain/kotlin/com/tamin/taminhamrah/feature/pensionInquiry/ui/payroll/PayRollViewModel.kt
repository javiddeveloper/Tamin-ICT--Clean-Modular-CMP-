package com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.contract.*
import com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.contract.PayRollUiState.PartialState
import com.tamin.taminhamrah.mapper.pension.toPresentation
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.useCases.pension.GetPensionerIdUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerPayRollUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerPayRollPDFUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PayRollViewModel(
    private val getPensionerPayRollUseCase: GetPensionerPayRollUseCase,
    private val getPensionerPayRollPDFUseCase: GetPensionerPayRollPDFUseCase,
    private val getPensionerIdUseCase: GetPensionerIdUseCase,
) : BaseViewModel<PayRollUiState, PartialState, PayRollEvent, PayRollIntent>(
    initialState = PayRollUiState()
) {

    init {
        sendIntent(PayRollIntent.LoadPensionerIds)
    }

    override fun handleIntent(intent: PayRollIntent): Flow<PartialState> = flow {
        when (intent) {
            is PayRollIntent.LoadPensionerIds -> {
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
            is PayRollIntent.ChangeSelectedPensionerId -> {
                emit(PartialState.SelectedPensionerIdChanged(intent.id))
            }
            is PayRollIntent.ChangeStartDate -> {
                emit(PartialState.StartDateChanged(intent.date))
            }
            is PayRollIntent.ChangePaymentType -> {
                emit(PartialState.PaymentTypeChanged(intent.type))
            }
            is PayRollIntent.LoadPayRoll -> {
                val state = uiState.value
                val pensionerId = state.selectedPensionerId
                if (pensionerId.isNullOrEmpty()) {
                    emit(PartialState.Error("شناسه مستمری‌بگیر یافت نشد"))
                    return@flow
                }
                emit(PartialState.Loading(true))
                try {
                    val filters = listOf(
                        ApiFilterDN(FilterProperty.PENSIONER_ID, pensionerId, FilterOperator.EQUAL),
                        ApiFilterDN(FilterProperty.START_DATE, state.startDate, FilterOperator.EQUAL),
                        ApiFilterDN(FilterProperty.PAYMENT_TYPE, state.paymentType, FilterOperator.EQUAL)
                    )
                    getPensionerPayRollUseCase(filters).collect { payRollList ->
                        emit(PartialState.PayRollLoaded(payRollList.map { it.toPresentation() }))
                    }
                } catch (e: Exception) {
                    emit(PartialState.Error(e.message))
                }
            }
            is PayRollIntent.LoadPayRollPDF -> {
                val state = uiState.value
                val pensionerId = state.selectedPensionerId
                if (pensionerId.isNullOrEmpty()) {
                    emit(PartialState.Error("شناسه مستمری‌بگیر یافت نشد"))
                    return@flow
                }
                emit(PartialState.Loading(true))
                try {
                    val filters = listOf(
                        ApiFilterDN(FilterProperty.PENSIONER_ID, pensionerId, FilterOperator.EQUAL),
                        ApiFilterDN(FilterProperty.START_DATE, state.startDate, FilterOperator.EQUAL),
                        ApiFilterDN(FilterProperty.PAYMENT_TYPE, state.paymentType, FilterOperator.EQUAL)
                    )
                    getPensionerPayRollPDFUseCase(filters).collect { pdf ->
                        emit(PartialState.PayRollPDFLoaded(pdf.toPresentation()))
                        emit(PartialState.TogglePdfDialog(true))
                    }
                } catch (e: Exception) {
                    emit(PartialState.Error(e.message))
                }
            }
            is PayRollIntent.TogglePdfDialog -> {
                emit(PartialState.TogglePdfDialog(intent.show))
            }
        }
    }

    override fun reduceState(
        currentState: PayRollUiState,
        partialState: PartialState
    ): PayRollUiState = when (partialState) {
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
        is PartialState.PaymentTypeChanged -> currentState.copy(
            paymentType = partialState.type
        )
        is PartialState.PayRollLoaded -> currentState.copy(
            isLoading = false,
            payRollList = partialState.payRoll
        )
        is PartialState.PayRollPDFLoaded -> currentState.copy(
            isLoading = false,
            payRollPDF = partialState.pdf
        )
        is PartialState.TogglePdfDialog -> currentState.copy(
            showPdfDialog = partialState.show
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}
