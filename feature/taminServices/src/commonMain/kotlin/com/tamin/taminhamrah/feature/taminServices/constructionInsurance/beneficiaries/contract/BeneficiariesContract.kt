package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * ذینفعان کارگاه — عملیات menu option "۴". Reached with the file/request identifiers of the row
 * that was acted on; loaded via `GetBeneficiariesWorkshopUseCase`
 * (`bld-request-services/building-workshops-owners`).
 */
@Immutable
data class BeneficiariesUiState(
    val requestNumber: Long? = null,
    val fileNumber: Long? = null,
    val requestDate: String? = null,
    val isLoading: Boolean = false,
    val items: ImmutableList<BeneficiaryConstructionPR> = persistentListOf(),
    val error: String? = null,
) {
    sealed interface PartialState {
        data class HeaderSeeded(
            val requestNumber: Long?,
            val fileNumber: Long?,
            val requestDate: String?,
        ) : PartialState
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
        data class Loaded(val items: ImmutableList<BeneficiaryConstructionPR>) : PartialState
    }
}

sealed interface BeneficiariesIntent {
    /** Sent once from the Route with the values carried by [BeneficiariesUiState]. */
    data class Load(
        val requestNumber: Long?,
        val fileNumber: Long?,
        val requestDate: String?,
    ) : BeneficiariesIntent

    data object Retry : BeneficiariesIntent
    data object OnBackClicked : BeneficiariesIntent
}

sealed interface BeneficiariesEvent {
    data object NavigateBack : BeneficiariesEvent
    data class ShowError(val message: String) : BeneficiariesEvent
}
