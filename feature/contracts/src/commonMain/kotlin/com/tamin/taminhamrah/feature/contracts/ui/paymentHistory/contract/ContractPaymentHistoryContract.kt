package com.tamin.taminhamrah.feature.contracts.ui.paymentHistory.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.contracts.ContractPaymentHistoryItemPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * سوابق پرداخت — the per-contract payment-history screen reached from «مشاهدهٔ پرداخت‌ها» in the
 * امور قرارداد sheet. [contractNumber] / [insuranceType] arrive as route arguments and only seed
 * the header; [items] and [successfulTotalLabel] come from `GetContractPaymentHistoryUseCase`.
 */
@Immutable
data class ContractPaymentHistoryUiState(
    val contractNumber: String = "",
    val insuranceType: String = "",
    val isLoading: Boolean = false,
    val items: ImmutableList<ContractPaymentHistoryItemPR> = persistentListOf(),
    /** جمع پرداخت‌های موفق — sum of the paid rows, already formatted with «ریال». */
    val successfulTotalLabel: String = "",
    val error: String? = null,
) {
    val paymentCount: Int get() = items.size

    sealed interface PartialState {
        data class HeaderSeeded(val contractNumber: String, val insuranceType: String) : PartialState
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
        data class Loaded(
            val items: ImmutableList<ContractPaymentHistoryItemPR>,
            val successfulTotalLabel: String,
        ) : PartialState
    }
}

sealed interface ContractPaymentHistoryIntent {
    /** Sent once from the Route with the values carried by [ContractPaymentHistoryUiState]. */
    data class Load(val contractNumber: String, val insuranceType: String) :
        ContractPaymentHistoryIntent

    data object Retry : ContractPaymentHistoryIntent
    data object OnBackClicked : ContractPaymentHistoryIntent
}

sealed interface ContractPaymentHistoryEvent {
    data object NavigateBack : ContractPaymentHistoryEvent
}
