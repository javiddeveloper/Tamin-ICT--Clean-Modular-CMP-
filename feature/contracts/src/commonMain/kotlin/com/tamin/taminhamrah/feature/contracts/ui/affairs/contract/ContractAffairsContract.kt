package com.tamin.taminhamrah.feature.contracts.ui.affairs.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.contracts.ContractPR
import com.tamin.taminhamrah.model.contracts.ContractPaymentHistoryItemPR
import com.tamin.taminhamrah.model.contracts.ContractStatePR
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * جستجوی قرارداد — the نوع بیمه option. [premiumTypeCode] is the `premiumTypeCode` filter value
 * `list-contracts-mobile` expects; `null` means "همهٔ قراردادها" (no type filter).
 */
enum class ContractSearchFilter(val premiumTypeCode: String?) {
    ALL(null),
    FREELANCE("01"),
    OPTIONAL("02"),
    FRACTION("38"),
}

/** امور قرارداد — the per-contract actions offered for an active contract. */
enum class ContractOperation {
    /** پرداخت حق بیمه */
    PAY_PREMIUM,

    /** مشاهده پرداخت‌ها */
    VIEW_PAYMENTS,

    /** ویرایش قرارداد */
    EDIT_CONTRACT,

    /** مشاهده قرارداد (PDF) */
    VIEW_CONTRACT,

    /** غیرفعال کردن قرارداد */
    DEACTIVATE,
}

@Immutable
data class ContractAffairsUiState(
    val isLoading: Boolean = false,
    val contracts: ImmutableList<ContractPR> = persistentListOf(),
    val error: String? = null,

    // انعقاد قرارداد جدید
    val newContractOptions: List<MainServiceDN> = emptyList(),

    // pagination
    val isLoadingNextPage: Boolean = false,
    val endReached: Boolean = false,
    val paginationError: String? = null,

    // جستجوی قرارداد
    val searchContractNumber: String = "",
    val searchFilter: ContractSearchFilter = ContractSearchFilter.ALL,
    val isSearchActive: Boolean = false,

    // امور قرارداد sheet
    val operationsContract: ContractPR? = null,
    val operations: ImmutableList<ContractOperation> = persistentListOf(),

    // غیرفعال کردن قرارداد
    val showCancelSheet: Boolean = false,
    val cancelContract: ContractPR? = null,
    val cancelReasons: ImmutableList<ContractStatePR> = persistentListOf(),
    val isCancelReasonsLoading: Boolean = false,
    val selectedCancelReason: ContractStatePR? = null,
    val cancelDescription: String = "",
    val isCancelling: Boolean = false,

    // مشاهده پرداخت‌ها
    val showPaymentHistory: Boolean = false,
    val paymentHistory: ImmutableList<ContractPaymentHistoryItemPR> = persistentListOf(),
    val isPaymentHistoryLoading: Boolean = false,

    // مشاهده قرارداد (PDF)
    val showPdfViewer: Boolean = false,
    val pdfDownload: PdfDownloadPR? = null,
    val isPdfLoading: Boolean = false,
    val pdfDownloadFailed: Boolean = false,
) {
    sealed interface PartialState {
        data class Error(val message: String?) : PartialState
        data class OptionsLoaded(val options: List<MainServiceDN>) : PartialState

        data class PagingChanged(
            val items: ImmutableList<ContractPR>,
            val isLoadingFirstPage: Boolean,
            val isLoadingNextPage: Boolean,
            val endReached: Boolean,
            val error: String?,
        ) : PartialState

        data class SearchChanged(
            val contractNumber: String,
            val filter: ContractSearchFilter,
        ) : PartialState

        data class SearchApplied(val active: Boolean) : PartialState

        data class OperationsSheetShown(
            val contract: ContractPR,
            val operations: ImmutableList<ContractOperation>,
        ) : PartialState

        data object OperationsSheetHidden : PartialState

        data class CancelSheetShown(val contract: ContractPR) : PartialState
        data object CancelSheetHidden : PartialState
        data class CancelReasonsLoading(val loading: Boolean) : PartialState
        data class CancelReasonsLoaded(val reasons: ImmutableList<ContractStatePR>) : PartialState
        data class CancelReasonSelected(val reason: ContractStatePR) : PartialState
        data class CancelDescriptionChanged(val description: String) : PartialState
        data class Cancelling(val inProgress: Boolean) : PartialState

        data class PaymentHistoryVisibility(val visible: Boolean) : PartialState
        data class PaymentHistoryLoading(val loading: Boolean) : PartialState
        data class PaymentHistoryLoaded(
            val items: ImmutableList<ContractPaymentHistoryItemPR>,
        ) : PartialState

        data class PdfViewerVisibility(val visible: Boolean) : PartialState
        data class PdfLoading(val loading: Boolean) : PartialState
        data class PdfLoaded(val pdf: PdfDownloadPR?) : PartialState
        data class PdfFailed(val failed: Boolean) : PartialState
    }
}

sealed interface ContractAffairsIntent {
    data object LoadContracts : ContractAffairsIntent
    data object LoadNextPage : ContractAffairsIntent
    data object RetryNextPage : ContractAffairsIntent
    data object RefreshContracts : ContractAffairsIntent

    /** جستجو — the criteria the sheet holds locally, applied only when the button is tapped. */
    data class ApplySearch(
        val contractNumber: String,
        val filter: ContractSearchFilter,
    ) : ContractAffairsIntent

    data object ClearSearch : ContractAffairsIntent

    data class OnNewContractServiceClick(val service: MainServiceDN) : ContractAffairsIntent

    data class ShowContractOperations(val contract: ContractPR) : ContractAffairsIntent
    data object DismissContractOperations : ContractAffairsIntent
    data class OnOperationClick(
        val contract: ContractPR,
        val operation: ContractOperation,
    ) : ContractAffairsIntent

    // غیرفعال کردن قرارداد
    data object DismissCancelSheet : ContractAffairsIntent
    data class OnCancelReasonSelected(val reason: ContractStatePR) : ContractAffairsIntent
    data class OnCancelDescriptionChanged(val value: String) : ContractAffairsIntent
    data object ConfirmCancelContract : ContractAffairsIntent

    // مشاهده پرداخت‌ها
    data object DismissPaymentHistory : ContractAffairsIntent

    // مشاهده قرارداد
    data object DismissPdfViewer : ContractAffairsIntent
}

sealed interface ContractAffairsEvent {
    data class ShowToast(val message: String) : ContractAffairsEvent
    data class ShowError(val message: String) : ContractAffairsEvent
    data class NavigateToService(val flag: FeatureFlag) : ContractAffairsEvent
    data class NavigateToWeb(val url: String) : ContractAffairsEvent

    /** پرداخت حق بیمه — hand off to the SEP online-payment flow for this contract. */
    data class NavigateToPremiumPayment(val contract: ContractPR) : ContractAffairsEvent

    /** ویرایش قرارداد — hand off to the edit-contract flow for this contract. */
    data class NavigateToEditContract(val contract: ContractPR) : ContractAffairsEvent

    data object ContractCancelled : ContractAffairsEvent
}
