package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * مدیریت پرداخت اقساط — عملیات menu option "۳" (shown instead of «صدور و مدیریت برگه پرداخت»
 * when the row's debitStatusCode is "51", i.e. installment). Lists the debit letters via
 * `GetInstallmentLetterListPageUseCase`; picking one row's own عملیات menu leads to
 * `InstallmentManagementRoute`/`InstallmentDebitListRoute` — the deeper flow the old app resolves
 * in `InstallmentLetterFragment.showDialog`.
 */
@Immutable
data class InstallmentLetterUiState(
    val fileNumber: Long? = null,
    val workshopId: String = "",
    val branchId: String = "",
    /** True only while the first page is in flight — drives the full-screen skeleton. */
    val isLoading: Boolean = false,
    val items: ImmutableList<InstallmentLetterPR> = persistentListOf(),
    val error: String? = null,
    /** True while a further page is in flight — drives the list-footer spinner, not the skeleton. */
    val isLoadingNextPage: Boolean = false,
    /** No more pages left to ask for, or none exist yet — hides the load-more trigger. */
    val endReached: Boolean = false,
    /** A page request past the first one failed — shown in the list footer with a retry action. */
    val paginationError: String? = null,
) {
    sealed interface PartialState {
        data class HeaderSeeded(val fileNumber: Long?, val workshopId: String, val branchId: String) : PartialState
        data class PagingChanged(
            val items: ImmutableList<InstallmentLetterPR>,
            val isLoadingFirstPage: Boolean,
            val isLoadingNextPage: Boolean,
            val endReached: Boolean,
            val error: String?,
        ) : PartialState
        data class Error(val message: String?) : PartialState
    }
}

sealed interface InstallmentLetterIntent {
    /** Sent once from the Route with the values carried by [InstallmentLetterUiState]. */
    data class Load(val fileNumber: Long?, val workshopId: String, val branchId: String) : InstallmentLetterIntent
    data object LoadNextPage : InstallmentLetterIntent
    data object RetryNextPage : InstallmentLetterIntent
    data object OnBackClicked : InstallmentLetterIntent
}

sealed interface InstallmentLetterEvent {
    data object NavigateBack : InstallmentLetterEvent
    data class ShowError(val message: String) : InstallmentLetterEvent
}
