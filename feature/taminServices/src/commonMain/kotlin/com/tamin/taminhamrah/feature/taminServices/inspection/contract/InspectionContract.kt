package com.tamin.taminhamrah.feature.taminServices.inspection.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.model.BranchPR
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.model.InspectionPerformedPR
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.model.JobPR
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.util.ValidationUtils

enum class InspectionRequestStep {
    IDENTITY_CONTACT,
    WORKSHOP_INFO,
    REQUEST_DESCRIPTION,
}

/** Tags a fatal load failure inside the request wizard to the step it blocks. */
enum class InspectionRequestErrorSource {
    USER_INFO,
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
    val inspections: List<InspectionPerformedPR> = emptyList(),
    val branches: List<BranchPR> = emptyList(),
    val jobs: List<JobPR> = emptyList(),
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
) {
    val isRequestStep1Valid: Boolean
        get() = ValidationUtils.isPhoneNumberValid(identityContact.mobile) &&
            ValidationUtils.isLandlineValid(identityContact.landline) &&
            ValidationUtils.isEmailValid(identityContact.email)

    val isRequestStep2Valid: Boolean
        get() = workshopInfo.workshopName.isNotBlank() &&
            workshopInfo.workshopCode.length == 10 &&
            ValidationUtils.isLandlineValid(workshopInfo.workshopPhone) &&
            workshopInfo.employerName.isNotBlank() &&
            workshopInfo.branchCode.isNotBlank() &&
            workshopInfo.jobCode.isNotBlank() &&
            workshopInfo.startDateTimestamp != null &&
            workshopInfo.endDateTimestamp != null &&
            ValidationUtils.isDateRangeValid(workshopInfo.startDateTimestamp, workshopInfo.endDateTimestamp) &&
            workshopInfo.workshopAddress.isNotBlank()

    val isRequestStep3Valid: Boolean get() = requestDescription.isNotBlank()

    val requestStepNumber: Int get() = requestStep.ordinal + 1
    val requestTotalSteps: Int get() = InspectionRequestStep.entries.size

    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class InspectionsLoaded(val list: List<InspectionPerformedPR>) : PartialState
        data class BranchesLoaded(val list: List<BranchPR>) : PartialState
        data class JobsLoaded(val list: List<JobPR>) : PartialState
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
    }
}

sealed interface InspectionIntent {
    data class LoadInspections(
        val filters: List<ApiFilterDN> = emptyList()
    ) : InspectionIntent

    data class LoadBranches(
        val filters: List<ApiFilterDN> = listOf(
            ApiFilterDN(FilterProperty.TYPE, "1", FilterOperator.EQUAL),
            ApiFilterDN(FilterProperty.STATUS, "1", FilterOperator.EQUAL)
        )
    ) : InspectionIntent

    data class LoadJobs(
        val filters: List<ApiFilterDN> = listOf(
            ApiFilterDN(FilterProperty.JOB_DESCRIPTION, "*", FilterOperator.LIKE)
        )
    ) : InspectionIntent

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
    data object RetryUserInfo : InspectionIntent
    data class UpdateIdentityContact(val identityContact: IdentityContactStepState) : InspectionIntent
    data class UpdateWorkshopInfo(val workshopInfo: WorkshopInfoStepState) : InspectionIntent
    data class UpdateRequestDescription(val description: String) : InspectionIntent
    data object GoToNextRequestStep : InspectionIntent
    data object GoToPreviousRequestStep : InspectionIntent
}

sealed interface InspectionEvent {
    data class ShowToast(val message: String) : InspectionEvent
    data object NavigateBack : InspectionEvent
}
