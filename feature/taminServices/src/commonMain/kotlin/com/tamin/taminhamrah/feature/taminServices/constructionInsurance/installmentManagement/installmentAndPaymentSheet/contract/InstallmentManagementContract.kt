package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentAndPaymentSheet.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentConstructionListPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * مدیریت اقساط و برگ پرداخت — عملیات option "۱" of [InstallmentLetterActionsSheet]. Lists the
 * individual installments under one debit letter via `GetInstallmentConstructionListPageUseCase`.
 * Read-only, matching legacy's `InstallmentManagementFragment` — issuing a payment sheet only
 * happens from the payment-sheet screen (`PaymentSheetViewModel`), and only for the whole debit;
 * there is no per-installment issuance endpoint.
 */
@Immutable
data class InstallmentManagementUiState(
    val fileNumber: Long? = null,
    val workshopId: String? = null,
    val branchId: String = "",
    val debitNumber: String = "",
    /** The selected تقسیط‌نامه row's own step label (e.g. «مرحلهٔ اول تقسیط بدهی ساختمانی») — carried
     * from [com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterPR.debitStepDescription]
     * as a nav param, shown as this screen's summary-card title. */
    val debitStepDescription: String? = null,
    /** True only while the first page is in flight — drives the full-screen skeleton. */
    val isLoading: Boolean = false,
    val items: ImmutableList<InstallmentConstructionListPR> = persistentListOf(),
    val error: String? = null,
    /** True while a further page is in flight — drives the list-footer spinner, not the skeleton. */
    val isLoadingNextPage: Boolean = false,
    /** No more pages left to ask for, or none exist yet — hides the load-more trigger. */
    val endReached: Boolean = false,
    /** A page request past the first one failed — shown in the list footer with a retry action. */
    val paginationError: String? = null,
) {
    sealed interface PartialState {
        data class HeaderSeeded(
            val fileNumber: Long?,
            val workshopId: String?,
            val branchId: String,
            val debitNumber: String,
            val debitStepDescription: String?,
        ) : PartialState
        data class PagingChanged(
            val items: ImmutableList<InstallmentConstructionListPR>,
            val isLoadingFirstPage: Boolean,
            val isLoadingNextPage: Boolean,
            val endReached: Boolean,
            val error: String?,
        ) : PartialState
        data class Error(val message: String?) : PartialState
    }
}

sealed interface InstallmentManagementIntent {
    /** Sent once from the Route with the values carried by [InstallmentManagementUiState]. */
    data class Load(
        val fileNumber: Long?,
        val workshopId: String?,
        val branchId: String,
        val debitNumber: String,
        val debitStepDescription: String? = null,
    ) : InstallmentManagementIntent
    data object LoadNextPage : InstallmentManagementIntent
    data object RetryNextPage : InstallmentManagementIntent
    data object OnBackClicked : InstallmentManagementIntent
}

sealed interface InstallmentManagementEvent {
    data object NavigateBack : InstallmentManagementEvent
    data class ShowError(val message: String) : InstallmentManagementEvent
}
