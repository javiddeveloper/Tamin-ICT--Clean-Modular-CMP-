package com.tamin.taminhamrah.feature.workshops.ui.paymentSheets

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.model.workshop.DebitReasonPR
import com.tamin.taminhamrah.model.workshop.PaymentSheetPR
import com.tamin.taminhamrah.model.workshop.PaymentSheetStatus
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * State of برگ پرداخت‌ها.
 *
 * [draft] is what the search sheet is editing; [applied] is what the visible page was fetched
 * with. Keeping them apart is what lets the sheet be dismissed without silently changing the list,
 * and lets the list say which filters are in force.
 */
@Immutable
data class PaymentSheetsUiState(
    val workshopId: String = "",
    val branchCode: String = "",
    val list: PagedListState<PaymentSheetPR> = PagedListState(),
    val draft: PaymentSheetFilters = PaymentSheetFilters(),
    val applied: PaymentSheetFilters = PaymentSheetFilters(),
    val isSearchOpen: Boolean = false,
    /** The علت ایجاد بدهی options, fetched once the picker is first opened. */
    val debitReasons: ImmutableList<DebitReasonPR> = persistentListOf(),
) {
    /** Both halves of the workshop identity are needed before anything can be requested. */
    val hasIdentity: Boolean get() = workshopId.isNotBlank() && branchCode.isNotBlank()

    sealed interface PartialState {
        data class Opened(val workshopId: String, val branchCode: String) : PartialState
        data object Loading : PartialState
        data object LoadingMore : PartialState
        data class Error(val message: String?) : PartialState
        data class Loaded(val list: PagedListState<PaymentSheetPR>) : PartialState
        data class DraftChanged(val draft: PaymentSheetFilters) : PartialState
        data class Applied(val filters: PaymentSheetFilters) : PartialState
        data class SearchOpenChanged(val isOpen: Boolean) : PartialState
        data class DebitReasonsLoaded(val reasons: ImmutableList<DebitReasonPR>) : PartialState
    }
}

/**
 * The eight optional filters of the search sheet.
 *
 * A date the user never picked stays null and is left out of the request; the old screen sent the
 * literal `"0"`, which the service read as a real bound. The reason and type carry their *codes* —
 * the display text the picker shows is not what the query takes.
 */
@Immutable
data class PaymentSheetFilters(
    val type: PaymentSheetStatus? = null,
    val debitReason: DebitReasonPR? = null,
    val payIdFrom: String = "",
    val payIdTo: String = "",
    val docDateFrom: Long? = null,
    val docDateTo: Long? = null,
) {
    val isNotEmpty: Boolean
        get() = type != null || debitReason != null || payIdFrom.isNotBlank() ||
            payIdTo.isNotBlank() || docDateFrom != null || docDateTo != null
}

sealed interface PaymentSheetsIntent {
    /** Carries the identity the route was opened with, then loads the first page. */
    data class Open(val workshopId: String, val branchCode: String) : PaymentSheetsIntent

    data object Load : PaymentSheetsIntent
    data object LoadMore : PaymentSheetsIntent
    data class SearchOpenChanged(val isOpen: Boolean) : PaymentSheetsIntent
    data class DraftChanged(val draft: PaymentSheetFilters) : PaymentSheetsIntent
    data object ApplyFilters : PaymentSheetsIntent
    data object ClearFilters : PaymentSheetsIntent
    data object LoadDebitReasons : PaymentSheetsIntent
}

sealed interface PaymentSheetsEvent
