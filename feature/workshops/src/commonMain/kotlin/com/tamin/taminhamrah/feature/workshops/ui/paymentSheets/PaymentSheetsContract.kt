package com.tamin.taminhamrah.feature.workshops.ui.paymentSheets

import com.tamin.taminhamrah.model.workshop.PaymentSheetPR

data class PaymentSheetsUiState(
    val isLoading: Boolean = false,
    val paymentSheets: List<PaymentSheetPR> = emptyList(),
    val error: String? = null
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class PaymentSheetsLoaded(val list: List<PaymentSheetPR>) : PartialState
        data class Error(val message: String?) : PartialState
    }
}

sealed interface PaymentSheetsEvent {
    data class ShowToast(val message: String) : PaymentSheetsEvent
}

sealed interface PaymentSheetsIntent {
    data class LoadPaymentSheets(
        val workshopId: String?,
        val branchCode: String?,
        val debitCause: String? = null,
        val paymentType: String? = null,
        val payNumberFrom: String? = null,
        val payNumberTo: String? = null,
        val dateFrom: String? = null,
        val dateTo: String? = null
    ) : PaymentSheetsIntent
}
