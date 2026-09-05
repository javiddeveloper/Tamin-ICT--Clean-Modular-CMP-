package com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.model.AgreementDocumentPR
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.model.EmployerAgreementRowPR
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.model.IdentityCardPR
import com.tamin.taminhamrah.model.workshop.WorkshopContractRowPR
import com.tamin.taminhamrah.model.workshop.WorkshopWithoutContractPR
import com.tamin.taminhamrah.util.ValidationUtils

/**
 * MVI contract for خدمات غیرحضوری کارفرمایان (Employer → Online Services).
 *
 * The section is a **multi-step flow** driven by a single ViewModel — exactly the shape the
 * inspection wizard uses. Each screen owns its own slice of state ([AgreementsListUiState],
 * [ContractRowsUiState], [AgreementRequestUiState]); [EmployerOnlineServicesUiState.currentScreen]
 * selects which one is on screen. "ثبت درخواست تعهدنامه" opens [EmployerOnlineServicesScreen.REQUEST_WIZARD],
 * whose step 2 (پذیرش تعهدنامه) is a placeholder until its design lands.
 */

/** Which screen of the flow is currently shown. */
enum class EmployerOnlineServicesScreen {
    /** Landing: کارفرما identity + the user's registered تعهدنامه list. */
    AGREEMENTS_LIST,

    /** Drill-down: the پیمانکار / contract rows of one workshop. */
    CONTRACT_ROWS,

    /** "ثبت درخواست تعهدنامه" — the OTP-gated agreement-request wizard. */
    REQUEST_WIZARD,
}

/**
 * The two visible steps of the "ثبت درخواست تعهدنامه" wizard. Step 1 has two sub-states (enter the
 * contact details, then enter the code) driven by [AgreementRequestUiState.ticketRequested] — not a
 * third step, matching the design's two-dot indicator.
 */
enum class AgreementRequestStep {
    /** اعتبارسنجی — request the security ticket, then verify the code. */
    VALIDATION,

    /** پذیرش تعهدنامه — accept the rules and submit. Design pending; a placeholder for now. */
    ACCEPT_AGREEMENT,
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

    /** "ارسال پیامک اعتبارسنجی" failed — retried by pressing the button again. */
    REQUEST_TICKET,

    /** "تأیید و مشاهدهٔ تعهدنامه" failed — the entered code was rejected. */
    VERIFY_CODE,

    /** Step 2's content (workshops-without-contract list + تعهدنامه text) failed to load. */
    STEP2_CONTENT,

    /** "ثبت تعهدنامه" failed. */
    SUBMIT,
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
    /** Where "ردیف‌های پیمان" was opened from — [EmployerOnlineServicesIntent.CloseContractRows] returns here. */
    val originScreen: EmployerOnlineServicesScreen = EmployerOnlineServicesScreen.AGREEMENTS_LIST,
)

/**
 * State of the "ثبت درخواست تعهدنامه" wizard.
 *
 * [mobile] and [email] are prefilled from the profile and both editable — the OTP is sent to
 * whatever [mobile] holds when "ارسال پیامک اعتبارسنجی" is pressed, and both are submitted with the
 * agreement. [ticketRequested] flips step 1 from the contact form to the code entry; [ticketNonce]
 * bumps on every (re)send so the code screen's countdown restarts.
 */
@Immutable
data class AgreementRequestUiState(
    val step: AgreementRequestStep = AgreementRequestStep.VALIDATION,
    /** Prefilled from the profile, editable — the number the OTP is sent to and the agreement registers. */
    val mobile: String = "",
    val email: String = "",
    val ticketRequested: Boolean = false,
    val ticketNonce: Int = 0,
    val code: String = "",
    val isSubmitting: Boolean = false,
    /** Employer identity read back by the verify call — feeds step 2 (پذیرش تعهدنامه). */
    val employerName: String = "",
    val employerNationalCode: String = "",
    /** The employer's *currently registered* contact, shown on step 2 against the newly requested one. */
    val currentMobile: String = "",
    val currentEmail: String = "",
    /** Step 2 lists the employer's workshops that have no registered agreement yet. */
    val workshopsWithoutContract: List<WorkshopWithoutContractPR> = emptyList(),
    /** The تعهدنامه wording, already personalised — see [AgreementDocumentPR]. */
    val document: AgreementDocumentPR = AgreementDocumentPR(),
    val isStep2Loading: Boolean = false,
    /** The step 2 consent checkbox — gates "ثبت تعهدنامه". */
    val accepted: Boolean = false,
    /** Set once the agreement is registered — the Route shows the success dialog. */
    val isSubmitted: Boolean = false,
) {
    val isEmailValid: Boolean get() = ValidationUtils.isEmailValid(email)

    /** Non-blank and a well-formed Iranian mobile (`09` + 9 digits) — gates "ارسال پیامک اعتبارسنجی". */
    val isMobileValid: Boolean get() = ValidationUtils.isMobileNumberValid(mobile)
    val isCodeComplete: Boolean get() = code.length == CODE_LENGTH

    companion object {
        const val CODE_LENGTH = 6
    }
}

@Immutable
data class EmployerOnlineServicesUiState(
    val currentScreen: EmployerOnlineServicesScreen = EmployerOnlineServicesScreen.AGREEMENTS_LIST,
    val isLoading: Boolean = false,
    val agreementsList: AgreementsListUiState = AgreementsListUiState(),
    val contractRows: ContractRowsUiState = ContractRowsUiState(),
    val agreementRequest: AgreementRequestUiState = AgreementRequestUiState(),
    /** Profile contact, side-loaded with the identity — seeds the request wizard's editable fields. */
    val profileMobile: String = "",
    val profileEmail: String = "",
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
            val originScreen: EmployerOnlineServicesScreen,
        ) : PartialState

        data class ContractRowsLoaded(val rows: List<WorkshopContractRowPR>) : PartialState

        /** Profile mobile + email, side-loaded with the identity — seeds the request wizard. */
        data class ContactPrefillLoaded(val mobile: String, val email: String) : PartialState

        /** Enter the request wizard fresh — resets it to the contact form seeded from the profile. */
        data object RequestWizardOpened : PartialState

        /** Leave the wizard, back to the landing list, discarding its state. */
        data object RequestWizardClosed : PartialState

        data class RequestMobileUpdated(val mobile: String) : PartialState
        data class RequestEmailUpdated(val email: String) : PartialState
        data class RequestCodeUpdated(val code: String) : PartialState

        /** The security ticket was (re)sent — show the code entry and restart its countdown. */
        data class RequestTicketIssued(val nonce: Int) : PartialState

        /** "ویرایش اطلاعات" — back to the contact form from the code entry or step 2. */
        data object RequestContactEditing : PartialState

        data class RequestSubmitting(val submitting: Boolean) : PartialState

        /** The code was accepted — advance to پذیرش تعهدنامه with the employer identity. */
        data class AgreementCodeVerified(
            val employerName: String,
            val employerNationalCode: String,
            val currentMobile: String,
            val currentEmail: String,
        ) : PartialState

        data class Step2ContentLoading(val loading: Boolean) : PartialState
        data class WorkshopsWithoutContractLoaded(val workshops: List<WorkshopWithoutContractPR>) : PartialState
        data class AgreementDocumentLoaded(val document: AgreementDocumentPR) : PartialState
        data class AgreementAcceptedChanged(val accepted: Boolean) : PartialState

        /** "ثبت تعهدنامه" succeeded — the Route shows the success dialog. */
        data object AgreementSubmitted : PartialState

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

    /** "+ ثبت درخواست تعهدنامه" — opens the agreement-request wizard at its first step. */
    data object OpenAgreementRequest : EmployerOnlineServicesIntent

    /** Back out of the request wizard to the landing list. */
    data object CloseAgreementRequest : EmployerOnlineServicesIntent

    data class UpdateRequestMobile(val mobile: String) : EmployerOnlineServicesIntent
    data class UpdateRequestEmail(val email: String) : EmployerOnlineServicesIntent
    data class UpdateRequestCode(val code: String) : EmployerOnlineServicesIntent

    /** "ارسال پیامک اعتبارسنجی" / "ارسال مجدد" — sends the OTP to the registered mobile. */
    data object RequestAgreementTicket : EmployerOnlineServicesIntent

    /** "ویرایش اطلاعات" — return to the contact form to correct the email. */
    data object EditAgreementContact : EmployerOnlineServicesIntent

    /** "تأیید و مشاهدهٔ تعهدنامه" — verifies the entered code and advances to پذیرش تعهدنامه. */
    data object VerifyAgreementCode : EmployerOnlineServicesIntent

    /** Step 2 consent checkbox. */
    data class SetAgreementAccepted(val accepted: Boolean) : EmployerOnlineServicesIntent

    /** "ثبت تعهدنامه" — registers the agreement. */
    data object SubmitAgreement : EmployerOnlineServicesIntent

    /** "متوجه شدم" on the success dialog — leaves the wizard and refreshes the landing list. */
    data object DismissAgreementSuccess : EmployerOnlineServicesIntent

    /**
     * A "ردیف‌های پیمان" tap — opens [EmployerOnlineServicesScreen.CONTRACT_ROWS] for that workshop.
     * Fired from an agreement card on the landing list *or* a workshop row in step 2 of the request
     * wizard; [EmployerOnlineServicesScreen.CONTRACT_ROWS] returns to whichever one opened it.
     */
    data class OpenContractRows(
        val workshopName: String,
        val workshopCodeLabel: String,
        val workshopId: String,
        val branchCode: String,
    ) : EmployerOnlineServicesIntent

    /** Back out of the contract-rows drill-down to the landing list. */
    data object CloseContractRows : EmployerOnlineServicesIntent
}

sealed interface EmployerOnlineServicesEvent {
    data class ShowToast(val message: String) : EmployerOnlineServicesEvent
    data object NavigateBack : EmployerOnlineServicesEvent
}
