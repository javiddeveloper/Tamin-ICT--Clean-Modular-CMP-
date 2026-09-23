package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.installmentDebitList.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentDebitListPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * بدهی‌های تقسیط‌شده — عملیات option "۲" of [InstallmentLetterActionsSheet]. Lists the flat
 * per-installment debit detail rows for one debit letter via `GetDetailDebitListPageUseCase`.
 */
@Immutable
data class InstallmentDebitListUiState(
    val fileNumber: Long? = null,
    val workshopId: String? = null,
    val branchId: String = "",
    val debitNumber: String = "",
    /** True only while the first page is in flight — drives the full-screen skeleton. */
    val isLoading: Boolean = false,
    val items: ImmutableList<InstallmentDebitListPR> = persistentListOf(),
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
        ) : PartialState
        data class PagingChanged(
            val items: ImmutableList<InstallmentDebitListPR>,
            val isLoadingFirstPage: Boolean,
            val isLoadingNextPage: Boolean,
            val endReached: Boolean,
            val error: String?,
        ) : PartialState
        data class Error(val message: String?) : PartialState
    }
}

sealed interface InstallmentDebitListIntent {
    /** Sent once from the Route with the values carried by [InstallmentDebitListUiState]. */
    data class Load(
        val fileNumber: Long?,
        val workshopId: String?,
        val branchId: String,
        val debitNumber: String,
    ) : InstallmentDebitListIntent
    data object LoadNextPage : InstallmentDebitListIntent
    data object RetryNextPage : InstallmentDebitListIntent
    data object OnBackClicked : InstallmentDebitListIntent
}

sealed interface InstallmentDebitListEvent {
    data object NavigateBack : InstallmentDebitListEvent
    data class ShowError(val message: String) : InstallmentDebitListEvent
}
