package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * ذینفعان کارگاه — عملیات menu option "۴". Reached with the file/request identifiers of the row
 * that was acted on; loaded via `GetBeneficiariesWorkshopPageUseCase`
 * (`bld-request-services/building-workshops-owners`).
 */
@Immutable
data class BeneficiariesUiState(
    val requestNumber: Long? = null,
    val fileNumber: Long? = null,
    val requestDate: String? = null,
    /** Carried only for the header hero card — not sent as a query filter. */
    val workshopId: String? = null,
    /** Carried only for the header hero card — not sent as a query filter. */
    val branchCode: String? = null,
    /** True only while the first page is in flight — drives the full-screen skeleton. */
    val isLoading: Boolean = false,
    val items: ImmutableList<BeneficiaryConstructionPR> = persistentListOf(),
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
            val requestNumber: Long?,
            val fileNumber: Long?,
            val requestDate: String?,
            val workshopId: String?,
            val branchCode: String?,
        ) : PartialState
        data class PagingChanged(
            val items: ImmutableList<BeneficiaryConstructionPR>,
            val isLoadingFirstPage: Boolean,
            val isLoadingNextPage: Boolean,
            val endReached: Boolean,
            val error: String?,
        ) : PartialState
        data class Error(val message: String?) : PartialState
    }
}

sealed interface BeneficiariesIntent {
    /** Sent once from the Route with the values carried by [BeneficiariesUiState]. */
    data class Load(
        val requestNumber: Long?,
        val fileNumber: Long?,
        val requestDate: String?,
        val workshopId: String?,
        val branchCode: String?,
    ) : BeneficiariesIntent

    data object LoadNextPage : BeneficiariesIntent
    data object RetryNextPage : BeneficiariesIntent
    data object OnBackClicked : BeneficiariesIntent
}

sealed interface BeneficiariesEvent {
    data object NavigateBack : BeneficiariesEvent
    data class ShowError(val message: String) : BeneficiariesEvent
}
