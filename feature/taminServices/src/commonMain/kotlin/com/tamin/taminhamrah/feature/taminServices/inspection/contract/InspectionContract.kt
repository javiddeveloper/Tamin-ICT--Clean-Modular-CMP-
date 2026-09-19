package com.tamin.taminhamrah.feature.taminServices.inspection.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.model.BranchPR
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.model.InspectionPerformedPR
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.model.JobPR
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import com.tamin.taminhamrah.util.ValidationUtils
import com.tamin.taminhamrah.util.ValidationUtils.isPhoneNumberValid
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private const val MIN_REQUEST_DESCRIPTION_LENGTH = 10

private fun isPhoneNumberAcceptable(phone: String): Boolean {
    return phone.isEmpty() || isPhoneNumberValid(phone)
}

enum class InspectionRequestStep {
    IDENTITY_CONTACT,
    WORKSHOP_INFO,
    REQUEST_DESCRIPTION,
}

/**
 * Tags a fatal load failure inside the request wizard to the specific call that produced it, so
 * retry only re-issues that one call — not every call feeding the step. [BRANCHES] and [JOBS] both
 * block Step 2 (Workshop Info) since it needs both, but are tracked separately since they load
 * concurrently and independently.
 */
enum class InspectionRequestErrorSource {
    USER_INFO,
    BRANCHES,
    JOBS,
}

data class IdentityContactStepState(
    val fullName: String = "",
    val nationalCode: String = "",
    val mobile: String = "",
    val landline: String = "",
    val email: String = "",
)

data class WorkshopInfoStepState(
    val workshopName: String = "",
    val workshopCode: String = "",
    val workshopPhone: String = "",
    val employerName: String = "",
    val branchCode: String = "",
    val branchName: String = "",
    val jobCode: String = "",
    val jobTitle: String = "",
    val startDate: String = "",
    val startDateTimestamp: Long? = null,
    val endDate: String = "",
    val endDateTimestamp: Long? = null,
    val workshopAddress: String = "",
)

@Immutable
data class InspectionUiState(
    val isLoading: Boolean = false,
    // --- Inspection list (inspection-header/get-all-insurance), offset-paginated (no search — the
    //     endpoint takes no filters, matching the legacy app) ---
    val inspections: ImmutableList<InspectionPerformedPR> = persistentListOf(),
    val isLoadingInspections: Boolean = false,
    val isLoadingNextInspections: Boolean = false,
    val inspectionsEndReached: Boolean = false,
    val inspectionsPagingError: String? = null,
    // --- Branch picker (proxy/models/branch), offset-paginated + server search ---
    val branches: ImmutableList<BranchPR> = persistentListOf(),
    val isLoadingBranches: Boolean = false,
    val isLoadingNextBranches: Boolean = false,
    val branchesEndReached: Boolean = false,
    val branchesPagingError: String? = null,
    val branchQuery: String = "",
    // --- Job picker (baseinfo/job), offset-paginated + server search ---
    val jobs: ImmutableList<JobPR> = persistentListOf(),
    val isLoadingJobs: Boolean = false,
    val isLoadingNextJobs: Boolean = false,
    val jobsEndReached: Boolean = false,
    val jobsPagingError: String? = null,
    val jobQuery: String = "",
    val isSubmitted: Boolean = false,
    val viewerPdf: PdfDownloadPR? = null,
    val viewerDownloadFailed: Boolean = false,
    // Request wizard ("درخواست بازرسی" / "ثبت اعتراض") — driven by this same ViewModel/contract
    // rather than a separate screen ViewModel, so list and wizard always share one source of truth.
    val showRequestFlow: Boolean = false,
    val requestInspectionNo: String? = null,
    val requestInsuranceNo: String? = null,
    val isObjectionRequest: Boolean = false,
    val requestStep: InspectionRequestStep = InspectionRequestStep.IDENTITY_CONTACT,
    val identityContact: IdentityContactStepState = IdentityContactStepState(),
    val workshopInfo: WorkshopInfoStepState = WorkshopInfoStepState(),
    val requestDescription: String = "",
    val submittedTrackingId: Long? = null,
    val requestErrors: Map<InspectionRequestErrorSource, String> = emptyMap(),
    val showExitConfirmation: Boolean = false,
) {
    val isRequestStep1Valid: Boolean
        get() = isPhoneNumberAcceptable(identityContact.mobile) &&
            ValidationUtils.isLandlineValid(identityContact.landline) &&
            ValidationUtils.isEmailValid(identityContact.email)


    val isRequestStep2Valid: Boolean
        get() = workshopInfo.workshopName.isNotBlank() &&
            ValidationUtils.isLandlineValid(workshopInfo.workshopPhone) &&
            workshopInfo.employerName.isNotBlank() &&
            workshopInfo.branchCode.isNotBlank() &&
            workshopInfo.jobCode.isNotBlank() &&
            workshopInfo.startDateTimestamp != null &&
            workshopInfo.endDateTimestamp != null &&
            ValidationUtils.isDateRangeValid(workshopInfo.startDateTimestamp, workshopInfo.endDateTimestamp) &&
            workshopInfo.workshopAddress.isNotBlank()

    val isRequestStep3Valid: Boolean get() = requestDescription.length > MIN_REQUEST_DESCRIPTION_LENGTH

    val requestStepNumber: Int get() = requestStep.ordinal + 1
    val requestTotalSteps: Int get() = InspectionRequestStep.entries.size

    /** Step 2 (Workshop Info) is still loading while the wizard prefetch or either picker's first page is in flight. */
    val isRequestStep2Loading: Boolean get() = isLoading || isLoadingBranches || isLoadingJobs

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState

        data class InspectionsPagingChanged(
            val items: ImmutableList<InspectionPerformedPR>,
            val isLoadingFirstPage: Boolean,
            val isLoadingNextPage: Boolean,
            val endReached: Boolean,
            val error: String?,
        ) : PartialState

        data class BranchesPagingChanged(
            val items: ImmutableList<BranchPR>,
            val isLoadingFirstPage: Boolean,
            val isLoadingNextPage: Boolean,
            val endReached: Boolean,
            val error: String?,
        ) : PartialState

        data class JobsPagingChanged(
            val items: ImmutableList<JobPR>,
            val isLoadingFirstPage: Boolean,
            val isLoadingNextPage: Boolean,
            val endReached: Boolean,
            val error: String?,
        ) : PartialState

        data class BranchQueryChanged(val query: String) : PartialState
        data class JobQueryChanged(val query: String) : PartialState

        data class SubmitSuccess(val id: Long?) : PartialState
        data class ViewerPdfChanged(val pdf: PdfDownloadPR?) : PartialState
        data object ViewerDownloadFailed : PartialState

        data class RequestFlowOpened(val item: InspectionPerformedPR?) : PartialState
        data object RequestFlowClosed : PartialState
        data object ClearRequestErrors : PartialState
        data class RequestError(
            val message: String,
            val source: InspectionRequestErrorSource = InspectionRequestErrorSource.USER_INFO,
        ) : PartialState
        data class IdentityContactUpdated(val identityContact: IdentityContactStepState) : PartialState
        data class WorkshopInfoUpdated(val workshopInfo: WorkshopInfoStepState) : PartialState
        data class RequestDescriptionUpdated(val description: String) : PartialState
        data object GoToNextRequestStep : PartialState
        data object GoToPreviousRequestStep : PartialState
        data class ExitConfirmationChanged(val show: Boolean) : PartialState
        data class RequestErrorCleared(val source: InspectionRequestErrorSource) : PartialState
    }
}

sealed interface InspectionIntent {
    /** Starts observing all three paginators and loads the first page of the inspection list. */
    data object LoadInspections : InspectionIntent
    data object LoadNextInspections : InspectionIntent
    data object RetryNextInspections : InspectionIntent

    data object LoadNextBranches : InspectionIntent
    data object RetryNextBranches : InspectionIntent
    data class SearchBranches(val query: String) : InspectionIntent

    data object LoadNextJobs : InspectionIntent
    data object RetryNextJobs : InspectionIntent
    data class SearchJobs(val query: String) : InspectionIntent

    data class SubmitRequest(
        val request: SubmitInspectionRequestDN
    ) : InspectionIntent

    data class DownloadReportPdf(val inspectionNo: String) : InspectionIntent
    data object DismissPdfViewer : InspectionIntent

    /**
     * Opens the request wizard. [item] non-null means "submit objection" for that inspection —
     * its workshop/branch data prefills Step 2 so the user doesn't retype what's already on the card.
     * A blank [item] (from the list screen's own "درخواست بازرسی" button) opens a fresh request.
     */
    data class OpenRequestFlow(
        val item: InspectionPerformedPR? = null,
    ) : InspectionIntent
    data object CloseRequestFlow : InspectionIntent

    /** Retries only the single API call behind [source]'s error instead of reloading the whole wizard. */
    data class RetrySource(val source: InspectionRequestErrorSource) : InspectionIntent
    data class UpdateIdentityContact(val identityContact: IdentityContactStepState) : InspectionIntent
    data class UpdateWorkshopInfo(val workshopInfo: WorkshopInfoStepState) : InspectionIntent
    data class UpdateRequestDescription(val description: String) : InspectionIntent
    data object GoToNextRequestStep : InspectionIntent
    data object GoToPreviousRequestStep : InspectionIntent

    /** Toggles the "leave form?" confirmation shown when closing the wizard from a step with unsaved input. */
    data class SetExitConfirmationVisible(val visible: Boolean) : InspectionIntent
}

sealed interface InspectionEvent {
    data class ShowToast(val message: String) : InspectionEvent
    data object NavigateBack : InspectionEvent
}
