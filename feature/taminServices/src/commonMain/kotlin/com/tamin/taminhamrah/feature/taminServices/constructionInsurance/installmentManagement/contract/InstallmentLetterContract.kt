package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * مدیریت پرداخت اقساط — عملیات menu option "۳" (shown instead of «صدور و مدیریت برگه پرداخت»
 * when the row's debitStatusCode is "51", i.e. installment). Entry screen only — lists the debit
 * letters via `GetInstallmentLetterListUseCase`; picking one and choosing an action from its own
 * عملیات menu (installment management vs. debit list) is a deeper flow the old app resolves in
 * `InstallmentLetterFragment.showDialog` and is out of scope here.
 */
@Immutable
data class InstallmentLetterUiState(
    val workshopId: String = "",
    val branchId: String = "",
    val isLoading: Boolean = false,
    val items: ImmutableList<InstallmentLetterPR> = persistentListOf(),
    val error: String? = null,
) {
    sealed interface PartialState {
        data class HeaderSeeded(val workshopId: String, val branchId: String) : PartialState
        data class Loading(val isLoading: Boolean) : PartialState
        data class Error(val message: String?) : PartialState
        data class Loaded(val items: ImmutableList<InstallmentLetterPR>) : PartialState
    }
}

sealed interface InstallmentLetterIntent {
    /** Sent once from the Route with the values carried by [InstallmentLetterUiState]. */
    data class Load(val workshopId: String, val branchId: String) : InstallmentLetterIntent
    data object Retry : InstallmentLetterIntent
    data object OnBackClicked : InstallmentLetterIntent
}

sealed interface InstallmentLetterEvent {
    data object NavigateBack : InstallmentLetterEvent
}
