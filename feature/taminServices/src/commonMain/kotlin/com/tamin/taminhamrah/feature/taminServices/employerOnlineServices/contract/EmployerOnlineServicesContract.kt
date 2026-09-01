package com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.model.EmployerAgreementRowPR
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.model.IdentityCardPR
import com.tamin.taminhamrah.model.workshop.WorkshopContractRowPR

/**
 * MVI contract for خدمات غیرحضوری کارفرمایان (Employer → Online Services).
 *
 * The section is a **multi-step flow** driven by a single ViewModel — exactly the shape the
 * inspection wizard uses. Each screen owns its own slice of state ([AgreementsListUiState],
 * [ContractRowsUiState]); [EmployerOnlineServicesUiState.currentScreen] selects which one is on
 * screen. The "ثبت درخواست تعهدنامه" wizard steps slot into this same seam next (see
 * `TODO(step 2+)` in the ViewModel/screen).
 */

/** Which screen of the flow is currently shown. */
enum class EmployerOnlineServicesScreen {
    /** Landing: کارفرما identity + the user's registered تعهدنامه list. */
    AGREEMENTS_LIST,

    /** Drill-down: the پیمانکار / contract rows of one workshop. */
    CONTRACT_ROWS,
}

/**
 * Tags a fatal load failure to the call that produced it, so a retry re-issues only that one call.
 * The landing screen's identity and agreements list load concurrently and independently, and the
 * contract-rows drill-down is its own call, so all three are tracked apart.
 */
enum class EmployerOnlineServicesErrorSource {
    IDENTITY,
    AGREEMENTS,
    CONTRACT_ROWS,
}

/** State of the landing agreements-list screen. */
@Immutable
data class AgreementsListUiState(
    val identity: IdentityCardPR = IdentityCardPR(),
    val agreements: List<EmployerAgreementRowPR> = emptyList(),
    /** The server's grand total — what the count tile shows, independent of how many rows loaded. */
    val agreementCount: Int = 0,
)

/**
 * State of the "ردیف‌های پیمان کارگاه" drill-down. The workshop identity is carried over from the
 * card that opened it (so the header renders instantly, before the rows arrive).
 */
@Immutable
data class ContractRowsUiState(
    val workshopName: String = "",
    val workshopCodeLabel: String = "",
    /** Raw identity — the query parameters the rows are fetched with, and what a retry re-uses. */
    val workshopId: String = "",
    val branchCode: String = "",
    val rows: List<WorkshopContractRowPR> = emptyList(),
)

@Immutable
data class EmployerOnlineServicesUiState(
    val currentScreen: EmployerOnlineServicesScreen = EmployerOnlineServicesScreen.AGREEMENTS_LIST,
    val isLoading: Boolean = false,
    val agreementsList: AgreementsListUiState = AgreementsListUiState(),
    val contractRows: ContractRowsUiState = ContractRowsUiState(),
    /** One fatal, retryable message per still-failed call; empty once everything loaded. */
    val errors: Map<EmployerOnlineServicesErrorSource, String> = emptyMap(),
) {
    val hasAnyError: Boolean get() = errors.isNotEmpty()

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class ScreenChanged(val screen: EmployerOnlineServicesScreen) : PartialState
        data class IdentityLoaded(val identity: IdentityCardPR) : PartialState
        data class AgreementsLoaded(
            val agreements: List<EmployerAgreementRowPR>,
            val total: Int,
        ) : PartialState

        /** The workshop a "ردیف‌های پیمان" tap targets — sets the header before its rows load. */
        data class ContractRowsTarget(
            val workshopName: String,
            val workshopCodeLabel: String,
            val workshopId: String,
            val branchCode: String,
        ) : PartialState

        data class ContractRowsLoaded(val rows: List<WorkshopContractRowPR>) : PartialState

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

    /** A card's "ردیف‌های پیمان" chip — opens [EmployerOnlineServicesScreen.CONTRACT_ROWS] for that workshop. */
    data class OpenContractRows(val row: EmployerAgreementRowPR) : EmployerOnlineServicesIntent

    /** Back out of the contract-rows drill-down to the landing list. */
    data object CloseContractRows : EmployerOnlineServicesIntent
}

sealed interface EmployerOnlineServicesEvent {
    data class ShowToast(val message: String) : EmployerOnlineServicesEvent
    /** A destination that has no screen yet — surfaced as a "به‌زودی" toast by the Route. */
    data object ShowComingSoon : EmployerOnlineServicesEvent
    data object NavigateBack : EmployerOnlineServicesEvent
}
