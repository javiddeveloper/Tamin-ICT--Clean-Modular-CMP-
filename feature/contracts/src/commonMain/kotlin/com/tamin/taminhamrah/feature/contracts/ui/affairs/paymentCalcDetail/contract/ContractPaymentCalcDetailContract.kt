package com.tamin.taminhamrah.feature.contracts.ui.affairs.paymentCalcDetail.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.contracts.PaymentCalcMonthPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * جزئیات برگ پرداخت — ریز محاسبهٔ حق بیمه for the period the محاسبهٔ حق بیمه screen produced.
 * [premiumTypeCode] / [startDate] / [endDate] arrive as route arguments (from the debit result);
 * [startLabel] / [endLabel] are their Persian-date form for the header subtitle.
 */
@Immutable
data class ContractPaymentCalcDetailUiState(
    val startLabel: String = "",
    val endLabel: String = "",
    val isLoading: Boolean = false,
    val rows: ImmutableList<PaymentCalcMonthPR> = persistentListOf(),
    /** ریز محاسبهٔ N ماه — the number of month cards shown, already in Persian digits. */
    val monthCountLabel: String = "",
    /** جمع کل — sum of the shown rows' مبلغ, formatted with «ریال». */
    val totalLabel: String = "",
    val error: String? = null,
) {
    sealed interface PartialState {
        data class HeaderSeeded(val startLabel: String, val endLabel: String) : PartialState
        data class Loading(val isLoading: Boolean) : PartialState
        data class Loaded(
            val rows: ImmutableList<PaymentCalcMonthPR>,
            val monthCountLabel: String,
            val totalLabel: String,
        ) : PartialState
        data class Error(val message: String?) : PartialState
    }
}

sealed interface ContractPaymentCalcDetailIntent {
    data class Load(
        val premiumTypeCode: String,
        val startDate: Long,
        val endDate: Long,
    ) : ContractPaymentCalcDetailIntent

    data object Retry : ContractPaymentCalcDetailIntent
    data object OnBackClicked : ContractPaymentCalcDetailIntent
}

sealed interface ContractPaymentCalcDetailEvent {
    data object NavigateBack : ContractPaymentCalcDetailEvent
}
