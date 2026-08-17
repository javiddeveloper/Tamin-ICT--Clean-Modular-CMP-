package com.tamin.taminhamrah.feature.workshops.ui.workshopDebit

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.model.workshop.WorkShopDebtPR
import org.jetbrains.compose.resources.StringResource

/** State of جزئیات محاسبه گردش حساب بدهی — the debts of one workshop, each payable online. */
@Immutable
data class WorkshopDebitUiState(
    val workshopId: String = "",
    val branchCode: String = "",
    val list: PagedListState<WorkShopDebtPR> = PagedListState(),
    /** The debt whose payment is being arranged; the row shows its own progress. */
    val payingDebitNumber: String? = null,
) {
    sealed interface PartialState {
        data class Opened(val workshopId: String, val branchCode: String) : PartialState
        data object Loading : PartialState
        data object LoadingMore : PartialState
        data class Error(val message: String?) : PartialState
        data class Loaded(val list: PagedListState<WorkShopDebtPR>) : PartialState
        data class Paying(val debitNumber: String?) : PartialState
    }
}

sealed interface WorkshopDebitIntent {
    data class Open(val workshopId: String, val branchCode: String) : WorkshopDebitIntent
    data object LoadMore : WorkshopDebitIntent
    data object Retry : WorkshopDebitIntent
    data class PayDebit(val debt: WorkShopDebtPR) : WorkshopDebitIntent
}

sealed interface WorkshopDebitEvent {
    /**
     * The payment page is opened outside the app and there is no return path, so the user is sent
     * off only once the service has agreed and handed over a ticket.
     */
    data class OpenPaymentPage(val url: String) : WorkshopDebitEvent

    /** The service refused, in its own words. */
    data class ShowServerMessage(val message: String) : WorkshopDebitEvent

    /**
     * The service refused without saying why. The old client showed nothing at all in this case;
     * a refusal always says something now.
     */
    data class ShowMessage(val message: StringResource) : WorkshopDebitEvent
}
