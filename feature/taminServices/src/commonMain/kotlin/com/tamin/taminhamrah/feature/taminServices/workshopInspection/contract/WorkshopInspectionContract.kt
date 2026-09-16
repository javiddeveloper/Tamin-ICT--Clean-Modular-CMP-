package com.tamin.taminhamrah.feature.taminServices.workshopInspection.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.IdentityContactStepState
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionRequestErrorSource
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionRequestStep
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.WorkshopInfoStepState
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.model.InspectionPerformedPR
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.model.JobPR
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import com.tamin.taminhamrah.util.ValidationUtils
import com.tamin.taminhamrah.util.ValidationUtils.isPhoneNumberValid
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

private const val MIN_REQUEST_DESCRIPTION_LENGTH = 10

/**
 * The list's client-side search filter — see [WorkshopInspectionUiState.filteredInspections].
 * There is no confirmed backend support for filtering `inspection-header/get-all-manager` by these
 * fields (the legacy production app's equivalent call takes no filter params at all, and the design
 * mock this screen is built from only filters its own local mock array). Filtering the already-loaded
 * page in memory here is the honest behavior until a real server filter contract is confirmed.
 */
@Immutable
data class WorkshopInspectionFilter(
    val workshopCode: String,
    val inspectionId: String,
)

@Immutable
data class WorkshopInspectionUiState(
    val isLoading: Boolean = false,
    // --- Inspection list (inspection-header/get-all-manager), offset-paginated ---
    val inspections: ImmutableList<InspectionPerformedPR> = persistentListOf(),
    val isLoadingInspections: Boolean = false,
    val isLoadingNextInspections: Boolean = false,
    val inspectionsEndReached: Boolean = false,
    val inspectionsPagingError: String? = null,
    // --- Search sheet: live-typed field values + the currently applied filter ---
    val isSearchSheetOpen: Boolean = false,
    val workshopCodeQuery: String = "",
    val inspectionIdQuery: String = "",
    val appliedFilter: WorkshopInspectionFilter? = null,
    // Branch is always prefilled/locked from the selected list item (this screen only ever opens
    // the wizard as an objection against an existing inspection) — no branch picker, unlike the
    // insured-side screen this is ported from.
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
    // "ثبت اعتراض" wizard — driven by this same ViewModel/contract so the list and wizard always
    // share one source of truth. Unlike the insured-side screen, this list has no "request a new
    // inspection" entry point — the wizard only ever opens as an objection against a list item.
    val showRequestFlow: Boolean = false,
    val requestInspectionNo: String? = null,
    val requestInsuranceNo: String? = null,
    val requestStep: InspectionRequestStep = InspectionRequestStep.IDENTITY_CONTACT,
    val identityContact: IdentityContactStepState = IdentityContactStepState(),
    val workshopInfo: WorkshopInfoStepState = WorkshopInfoStepState(),
    val requestDescription: String = "",
    val submittedTrackingId: Long? = null,
    val requestErrors: Map<InspectionRequestErrorSource, String> = emptyMap(),
    val showExitConfirmation: Boolean = false,
) {
    val filteredInspections: ImmutableList<InspectionPerformedPR>
        get() {
            val filter = appliedFilter ?: return inspections
            return inspections.filter { item ->
                (filter.workshopCode.isBlank() || item.workshopNo.contains(filter.workshopCode)) &&
                    (filter.inspectionId.isBlank() || item.inspectionNo.contains(filter.inspectionId))
            }.toImmutableList()
        }

    // Unlike the insured-side flow this is ported from, the employer/workshop objection wizard
    // requires a mobile number rather than treating it as optional.
    val isRequestStep1Valid: Boolean
        get() = isPhoneNumberValid(identityContact.mobile) &&
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

    /** Step 2 (Workshop Info) is still loading while the job picker's first page is in flight. */
    val isRequestStep2Loading: Boolean get() = isLoading || isLoadingJobs

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState

        data class InspectionsPagingChanged(
            val items: ImmutableList<InspectionPerformedPR>,
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

        data class JobQueryChanged(val query: String) : PartialState

        data class SubmitSuccess(val id: Long?) : PartialState
        data class ViewerPdfChanged(val pdf: PdfDownloadPR?) : PartialState
        data object ViewerDownloadFailed : PartialState

        data class RequestFlowOpened(val item: InspectionPerformedPR) : PartialState
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

        data object SearchSheetOpened : PartialState
        data object SearchSheetClosed : PartialState
        data class WorkshopCodeQueryChanged(val query: String) : PartialState
        data class InspectionIdQueryChanged(val query: String) : PartialState
        data class FilterApplied(val filter: WorkshopInspectionFilter?) : PartialState
    }
}

sealed interface WorkshopInspectionIntent {
    data object LoadInspections : WorkshopInspectionIntent
    data object LoadNextInspections : WorkshopInspectionIntent
    data object RetryNextInspections : WorkshopInspectionIntent

    data object LoadNextJobs : WorkshopInspectionIntent
    data object RetryNextJobs : WorkshopInspectionIntent
    data class SearchJobs(val query: String) : WorkshopInspectionIntent

    data class SubmitRequest(val request: SubmitInspectionRequestDN) : WorkshopInspectionIntent

    data class DownloadReportPdf(val inspectionNo: String) : WorkshopInspectionIntent
    data object DismissPdfViewer : WorkshopInspectionIntent

    /** Opens the "ثبت اعتراض" wizard for [item] — its workshop/branch data prefills Step 2. */
    data class OpenRequestFlow(val item: InspectionPerformedPR) : WorkshopInspectionIntent
    data object CloseRequestFlow : WorkshopInspectionIntent

    data class RetrySource(val source: InspectionRequestErrorSource) : WorkshopInspectionIntent
    data class UpdateIdentityContact(val identityContact: IdentityContactStepState) : WorkshopInspectionIntent
    data class UpdateWorkshopInfo(val workshopInfo: WorkshopInfoStepState) : WorkshopInspectionIntent
    data class UpdateRequestDescription(val description: String) : WorkshopInspectionIntent
    data object GoToNextRequestStep : WorkshopInspectionIntent
    data object GoToPreviousRequestStep : WorkshopInspectionIntent
    data class SetExitConfirmationVisible(val visible: Boolean) : WorkshopInspectionIntent

    data object OpenSearchSheet : WorkshopInspectionIntent
    data object CloseSearchSheet : WorkshopInspectionIntent
    data class UpdateWorkshopCodeQuery(val query: String) : WorkshopInspectionIntent
    data class UpdateInspectionIdQuery(val query: String) : WorkshopInspectionIntent
    data object ApplySearch : WorkshopInspectionIntent
    data object ClearSearch : WorkshopInspectionIntent
}

sealed interface WorkshopInspectionEvent {
    data class ShowToast(val message: String) : WorkshopInspectionEvent
    data object NavigateBack : WorkshopInspectionEvent
}
