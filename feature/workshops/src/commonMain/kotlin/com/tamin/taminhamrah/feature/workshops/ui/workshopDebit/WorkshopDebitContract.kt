package com.tamin.taminhamrah.feature.workshops.ui.workshopDebit

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.model.payment.PaymentRequestDN
import com.tamin.taminhamrah.model.workshop.WorkShopDebtPR
import org.jetbrains.compose.resources.StringResource

/** State of جزئیات محاسبه گردش حساب بدهی — the debts of one workshop, each payable online. */
@Immutable
data class WorkshopDebitUiState(
    val workshopId: String = "",
    val branchCode: String = "",
    /**
     * `01` حقیقی / `02` حقوقی, and the حقوقی workshop's national id — carried from the list row
     * because `pay-normal-debit` sends both and this screen never loads the workshop itself.
     */
    val characterCode: String = "",
    val legalNationalId: String = "",
    val list: PagedListState<WorkShopDebtPR> = PagedListState(),
    /** The debt whose payment is being arranged; the row shows its own progress. */
    val payingDebitNumber: String? = null,
) {
    sealed interface PartialState {
        data class Opened(
            val workshopId: String,
            val branchCode: String,
            val characterCode: String,
            val legalNationalId: String,
        ) : PartialState
        data object Loading : PartialState
        data object LoadingMore : PartialState
        data class Error(val message: String?) : PartialState
        data class Loaded(val list: PagedListState<WorkShopDebtPR>) : PartialState
        data class Paying(val debitNumber: String?) : PartialState
    }
}

sealed interface WorkshopDebitIntent {
    data class Open(
        val workshopId: String,
        val branchCode: String,
        val characterCode: String = "",
        val legalNationalId: String = "",
    ) : WorkshopDebitIntent
    data object LoadMore : WorkshopDebitIntent
    data object Retry : WorkshopDebitIntent
    data class PayDebit(val debt: WorkShopDebtPR) : WorkshopDebitIntent
}

sealed interface WorkshopDebitEvent {
    /**
     * Hands the debt over to the shared payment flow (`:feature:payment`), which owns the address
     * of the gateway -- the user is sent there only once the service has agreed and handed over a
     * ticket, and the ticket has been bound to them.
     */
    data class StartPayment(val request: PaymentRequestDN) : WorkshopDebitEvent

    /** The service refused, in its own words. */
    data class ShowServerMessage(val message: String) : WorkshopDebitEvent

    /**
     * The service refused without saying why. The old client showed nothing at all in this case;
     * a refusal always says something now.
     */
    data class ShowMessage(val message: StringResource) : WorkshopDebitEvent
}
