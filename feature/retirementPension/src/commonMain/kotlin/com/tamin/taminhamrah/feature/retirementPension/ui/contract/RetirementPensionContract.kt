package com.tamin.taminhamrah.feature.retirementPension.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.pension.retirement.RetirementBranchInfoPR
import com.tamin.taminhamrah.model.pension.retirement.RetirementHistoryPR
import com.tamin.taminhamrah.model.pension.retirement.RetirementIdentityPR
import com.tamin.taminhamrah.model.pension.retirement.RetirementInsuredPR
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf

/** The three destinations of this service, all behind one navigation entry. */
enum class RetirementScreen { Intro, Form, Track }

/** The eight steps of the request wizard, in order. */
enum class RetirementStep {
    Rules,
    Authentication,
    Identity,
    Workshop,
    History,
    IdentityDocuments,
    QuitLetter,
    Final,
    ;

    /** 1-based position, which is what the header counts. */
    val number: Int get() = ordinal + 1

    val previous: RetirementStep? get() = entries.getOrNull(ordinal - 1)
    val next: RetirementStep? get() = entries.getOrNull(ordinal + 1)

    companion object {
        val TOTAL: Int = entries.size
    }
}

/**
 * Document type codes the service files an upload under.
 *
 * The values are the server's own, taken from the legacy app's `Constants`
 * (`RETIREMENT_FIRST_PAGE_IDENTITY_IMAGE_TYPE`, `RETIREMENT_DESC_PAGE_IDENTITY`,
 * `RESIGNATION_LETTER_IMAGE_TYPE`). They are not sequential and must not be renumbered.
 */
enum class RetirementDocumentType(val code: String) {
    IdFirstPage("20"),
    IdDescriptionPage("21"),
    QuitLetter("18"),
    ;

    /** Whether this document belongs to step 6 rather than step 7. */
    val isIdentityDocument: Boolean get() = this != QuitLetter
}

/**
 * One validation failure, resolved to copy by the UI.
 *
 * An enum rather than the message itself: `getString` inside a ViewModel is unreliable under the
 * unit-test runtime — the exception is swallowed by `BaseViewModel`'s per-intent `catch` and the
 * specific partial state never arrives. Keeping the reason as data also makes the rules testable
 * without a resource loader.
 */
enum class RetirementFormError {
    Consent,
    Authentication,
    PhoneRequired,
    PhonePrefix,
    PhoneLength,
    AddressRequired,
    AddressShort,
    AddressInvalid,
    IdentityConfirm,
    WorkshopName,
    WorkshopCode,
    WorkshopAddress,
    WorkshopConfirm,
    IdentityDocuments,
    QuitLetter,
    FinalConfirm,
    ;

    /**
     * Whether the message belongs on an upload card rather than the shared line under the form.
     * Exactly one place owns each message; rendering both would say it twice.
     */
    val isOnDocumentCard: Boolean get() = this == IdentityDocuments || this == QuitLetter
}

/** Modal the screen is showing, if any. */
enum class RetirementDialog { Rules, Objection, AgeGate, Done, Leave }

/**
 * Where one document has got to.
 *
 * Deliberately does **not** hold the picked bytes: an upload is finished the moment the server
 * hands back a guid, and keeping a couple of megabytes of image in the UI state means every
 * subsequent `copy()` of that state carries them, and every equality check compares them.
 */
@Immutable
data class RetirementDocumentPR(
    val guid: String? = null,
    val isUploading: Boolean = false,
    val hasFailed: Boolean = false,
) {
    val isUploaded: Boolean get() = guid != null
}

@Immutable
data class RetirementPensionUiState(
    val screen: RetirementScreen = RetirementScreen.Intro,
    val step: RetirementStep = RetirementStep.Rules,
    /** Furthest step reached, so the header can offer a tap back to it. */
    val maxReachedStep: RetirementStep = RetirementStep.Rules,

    val isLoading: Boolean = false,
    val isIntroLoading: Boolean = true,
    val isHistoryLoading: Boolean = false,
    val isOtpSending: Boolean = false,

    val insured: RetirementInsuredPR? = null,
    val isAgeEligible: Boolean = true,
    /** Server age, as `"years,months,days"`; carried through to the create-request body verbatim. */
    val rawAge: String = "",
    val requestId: String? = null,
    val statusCode: String? = null,

    val consentAccepted: Boolean = false,

    val otpMobile: String = "",
    val otpSent: Boolean = false,
    val otpValue: String = "",
    val otpVerified: Boolean = false,
    val otpInvalid: Boolean = false,
    /** The verified code, re-presented when the request is created. */
    val ticketCode: Long? = null,

    val identity: RetirementIdentityPR? = null,
    val phoneNumber: String = "",
    val address: String = "",
    val identityConfirmed: Boolean = false,

    val branch: RetirementBranchInfoPR? = null,
    val workshopName: String = "",
    val workshopCode: String = "",
    val workshopAddress: String = "",
    val employerName: String = "",
    val activityType: String = "",
    val workshopConfirmed: Boolean = false,

    val history: RetirementHistoryPR? = null,

    val documents: PersistentMap<RetirementDocumentType, RetirementDocumentPR> = persistentMapOf(),
    val activeDocument: RetirementDocumentType? = null,

    val finalConfirmed: Boolean = false,

    /** Validation only surfaces once the step's action has been pressed. */
    val validationAttempted: Boolean = false,
    val error: RetirementFormError? = null,

    val dialog: RetirementDialog? = null,
) {
    /** A request already exists, so the intro can offer to track it. */
    val hasExistingRequest: Boolean get() = !requestId.isNullOrBlank()

    /**
     * The gate passed on an age the service actually reported. [isAgeEligible] also stays open
     * when no age came back at all, which must not read as "age conditions met".
     */
    val isAgeConfirmed: Boolean get() = isAgeEligible && !insured?.ageYears.isNullOrBlank()

    val isDocumentUploading: Boolean
        get() = documents.values.any(RetirementDocumentPR::isUploading)

    /** Whether [error] should be drawn in the shared line under the form. */
    val visibleFormError: RetirementFormError?
        get() = error?.takeIf { validationAttempted && !it.isOnDocumentCard }

    /** True once the user has tried to move on without supplying [type] on the step that needs it. */
    fun isDocumentMissing(type: RetirementDocumentType): Boolean {
        if (!validationAttempted) return false
        val document = documents[type]
        if (document?.isUploaded == true || document?.isUploading == true) return false
        return when (step) {
            RetirementStep.IdentityDocuments -> type.isIdentityDocument
            RetirementStep.QuitLetter -> type == RetirementDocumentType.QuitLetter
            else -> false
        }
    }

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class IntroLoading(val isIntroLoading: Boolean) : PartialState
        data class HistoryLoading(val isHistoryLoading: Boolean) : PartialState
        data class OtpSending(val isSending: Boolean) : PartialState

        data class IntroLoaded(
            val insured: RetirementInsuredPR,
            val branch: RetirementBranchInfoPR,
            val rawAge: String,
            val isAgeEligible: Boolean,
            val requestId: String?,
            val statusCode: String?,
        ) : PartialState

        data class ScreenChanged(val screen: RetirementScreen) : PartialState
        data class StepChanged(val step: RetirementStep) : PartialState
        data class ValidationAttempted(val attempted: Boolean) : PartialState

        data class ConsentChanged(val accepted: Boolean) : PartialState

        data class OtpTicketReceived(val mobileNumber: String) : PartialState
        data class OtpChanged(val value: String) : PartialState
        data object OtpRejected : PartialState
        data class OtpVerified(
            val ticketCode: Long,
            val identity: RetirementIdentityPR,
            val branch: RetirementBranchInfoPR,
            val insuranceNumber: String,
            val workshopCode: String,
            val activityType: String,
            val phoneNumber: String,
            val address: String,
        ) : PartialState

        data class PrefillLoaded(
            val phoneNumber: String,
            val address: String,
            val workshopName: String,
            val workshopCode: String,
            val workshopAddress: String,
            val employerName: String,
            val activityType: String,
        ) : PartialState

        data class PhoneChanged(val value: String) : PartialState
        data class AddressChanged(val value: String) : PartialState
        data class IdentityConfirmedChanged(val confirmed: Boolean) : PartialState

        data class WorkshopNameChanged(val value: String) : PartialState
        data class WorkshopCodeChanged(val value: String) : PartialState
        data class WorkshopAddressChanged(val value: String) : PartialState
        data class EmployerNameChanged(val value: String) : PartialState
        data class ActivityTypeChanged(val value: String) : PartialState
        data class WorkshopConfirmedChanged(val confirmed: Boolean) : PartialState

        data class HistoryLoaded(val history: RetirementHistoryPR) : PartialState

        data class RequestCreated(val requestId: String) : PartialState
        data class StatusChanged(val statusCode: String?) : PartialState

        data class DocumentSourceRequested(val type: RetirementDocumentType) : PartialState
        data object DocumentSourceDismissed : PartialState
        data class DocumentUploadStarted(val type: RetirementDocumentType) : PartialState
        data class DocumentUploaded(val type: RetirementDocumentType, val guid: String) : PartialState
        data class DocumentUploadFailed(val type: RetirementDocumentType) : PartialState

        data class FinalConfirmedChanged(val confirmed: Boolean) : PartialState

        data class DialogChanged(val dialog: RetirementDialog?) : PartialState
        data object Error : PartialState
    }
}

sealed interface RetirementPensionIntent {
    data object Init : RetirementPensionIntent
    data object StartRequest : RetirementPensionIntent
    data object OpenTrack : RetirementPensionIntent
    data object Back : RetirementPensionIntent
    /** Close (X) on the form — always asks before discarding. */
    data object CloseClicked : RetirementPensionIntent
    data object LeaveConfirmed : RetirementPensionIntent
    data class GoToStep(val step: RetirementStep) : RetirementPensionIntent
    data object NextStep : RetirementPensionIntent

    data class ConsentChanged(val accepted: Boolean) : RetirementPensionIntent
    data object ViewRules : RetirementPensionIntent
    data object RequestOtp : RetirementPensionIntent
    data class OtpChanged(val value: String) : RetirementPensionIntent

    data class PhoneChanged(val value: String) : RetirementPensionIntent
    data class AddressChanged(val value: String) : RetirementPensionIntent
    data class IdentityConfirmedChanged(val confirmed: Boolean) : RetirementPensionIntent

    data class WorkshopNameChanged(val value: String) : RetirementPensionIntent
    data class WorkshopCodeChanged(val value: String) : RetirementPensionIntent
    data class WorkshopAddressChanged(val value: String) : RetirementPensionIntent
    data class EmployerNameChanged(val value: String) : RetirementPensionIntent
    data class ActivityTypeChanged(val value: String) : RetirementPensionIntent
    data class WorkshopConfirmedChanged(val confirmed: Boolean) : RetirementPensionIntent

    data class DocumentClicked(val type: RetirementDocumentType) : RetirementPensionIntent
    /**
     * The picked file itself, not its bytes: reading them is IO, and it belongs on the ViewModel's
     * scope rather than in the picker callback.
     */
    data class DocumentPicked(
        val type: RetirementDocumentType,
        val file: PlatformFile,
    ) : RetirementPensionIntent
    data object DismissDocumentSource : RetirementPensionIntent

    /** The camera was refused; the sheet closes and the refusal is reported. */
    data object CameraDenied : RetirementPensionIntent

    data class FinalConfirmedChanged(val confirmed: Boolean) : RetirementPensionIntent

    data class ShowDialog(val dialog: RetirementDialog) : RetirementPensionIntent
    data object DismissDialog : RetirementPensionIntent
}

sealed interface RetirementPensionEvent {
    /** A message the service itself worded — passed through as-is. */
    data class ShowError(val message: String) : RetirementPensionEvent

    /** The camera was refused, so there is nothing to photograph with. */
    data object CameraPermissionDenied : RetirementPensionEvent

    /**
     * The create call came back without an id, so there is nothing to attach documents to.
     *
     * Carries no text: the wording is a string resource, and `getString` inside a ViewModel is
     * swallowed by `BaseViewModel`'s per-intent `catch`.
     */
    data object RequestCreationFailed : RetirementPensionEvent

    data object NavigateBack : RetirementPensionEvent
    data object OpenRulesDocument : RetirementPensionEvent

    /** Step 2 verified: the wizard moves on after a beat so the success card is actually seen. */
    data object AuthenticationSucceeded : RetirementPensionEvent
}
