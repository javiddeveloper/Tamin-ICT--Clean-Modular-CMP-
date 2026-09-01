package com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.model.EmployerAgreementRowPR
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.model.IdentityCardPR

/**
 * MVI contract for خدمات غیرحضوری کارفرمایان (Employer → Online Services).
 *
 * The section is a **multi-step flow** driven by a single ViewModel — exactly the shape the
 * inspection wizard uses. Each screen owns its own slice of state
 * ([AgreementsListUiState] here); [EmployerOnlineServicesUiState.currentScreen] selects which one
 * is on screen. This first screen — the landing agreements list — is the only one wired today;
 * [EmployerOnlineServicesScreen] and the nested-state pattern are the seam the "ثبت درخواست
 * تعهدنامه" wizard steps slot into next (see `TODO(step 2+)` in the ViewModel/screen).
 */

/** Which screen of the flow is currently shown. */
enum class EmployerOnlineServicesScreen {
    /** Landing: کارفرما identity + the user's registered تعهدنامه list. */
    AGREEMENTS_LIST,
}

/**
 * Tags a fatal load failure on the landing screen to the call that produced it, so a retry
 * re-issues only that one call. Identity and the agreements list load concurrently and
 * independently, so they are tracked apart.
 */
enum class EmployerOnlineServicesErrorSource {
    IDENTITY,
    AGREEMENTS,
}

/** State of the landing agreements-list screen. */
@Immutable
data class AgreementsListUiState(
    val identity: IdentityCardPR = IdentityCardPR(),
    val agreements: List<EmployerAgreementRowPR> = emptyList(),
    /** The server's grand total — what the count tile shows, independent of how many rows loaded. */
    val agreementCount: Int = 0,
)

@Immutable
data class EmployerOnlineServicesUiState(
    val currentScreen: EmployerOnlineServicesScreen = EmployerOnlineServicesScreen.AGREEMENTS_LIST,
    val isLoading: Boolean = false,
    val agreementsList: AgreementsListUiState = AgreementsListUiState(),
    /** One fatal, retryable message per still-failed call; empty once everything loaded. */
    val errors: Map<EmployerOnlineServicesErrorSource, String> = emptyMap(),
) {
    val hasAnyError: Boolean get() = errors.isNotEmpty()

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class IdentityLoaded(val identity: IdentityCardPR) : PartialState
        data class AgreementsLoaded(
            val agreements: List<EmployerAgreementRowPR>,
            val total: Int,
        ) : PartialState

        data class Error(
            val message: String,
            val source: EmployerOnlineServicesErrorSource,
        ) : PartialState

        data class ErrorCleared(val source: EmployerOnlineServicesErrorSource) : PartialState
    }
}

sealed interface EmployerOnlineServicesIntent {
    /** Initial load — identity + first page of agreements, fired once on entry. */
    data object Load : EmployerOnlineServicesIntent

    /** Retries only the single call behind [source]'s error. */
    data class RetrySource(val source: EmployerOnlineServicesErrorSource) : EmployerOnlineServicesIntent

    /** "+ ثبت درخواست تعهدنامه" — opens the agreement-request wizard. TODO(step 2+): not built yet. */
    data object OpenAgreementRequest : EmployerOnlineServicesIntent

    /** A card's "ردیف‌های پیمان" chip — drills into that workshop's contract rows. TODO(step 2+). */
    data class OpenContractRows(val row: EmployerAgreementRowPR) : EmployerOnlineServicesIntent
}

sealed interface EmployerOnlineServicesEvent {
    data class ShowToast(val message: String) : EmployerOnlineServicesEvent
    /** A destination that has no screen yet — surfaced as a "به‌زودی" toast by the Route. */
    data object ShowComingSoon : EmployerOnlineServicesEvent
    data object NavigateBack : EmployerOnlineServicesEvent
}
