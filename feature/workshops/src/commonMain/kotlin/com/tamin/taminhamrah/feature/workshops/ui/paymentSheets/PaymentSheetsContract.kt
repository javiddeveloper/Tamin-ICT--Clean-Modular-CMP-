package com.tamin.taminhamrah.feature.workshops.ui.paymentSheets

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.workshop.PaymentSheetPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class PaymentSheetsUiState(
    val isLoading: Boolean = false,
    val paymentSheets: ImmutableList<PaymentSheetPR> = persistentListOf(),
    val error: String? = null
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class PaymentSheetsLoaded(val list: ImmutableList<PaymentSheetPR>) : PartialState
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
