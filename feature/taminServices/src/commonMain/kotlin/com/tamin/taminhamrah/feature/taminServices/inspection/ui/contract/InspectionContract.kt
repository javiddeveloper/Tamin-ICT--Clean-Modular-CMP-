package com.tamin.taminhamrah.feature.taminServices.inspection.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.model.BranchPR
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.model.InspectionPerformedPR
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.model.JobPR
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty

@Immutable
data class InspectionUiState(
    val isLoading: Boolean = false,
    val inspections: List<InspectionPerformedPR> = emptyList(),
    val branches: List<BranchPR> = emptyList(),
    val jobs: List<JobPR> = emptyList(),
    val isSubmitted: Boolean = false,
    val viewerPdf: PdfDownloadPR? = null,
    val viewerDownloadFailed: Boolean = false,
) {
    sealed interface PartialState {
        data class Loading(val isLoading: Boolean) : PartialState
        data class InspectionsLoaded(val list: List<InspectionPerformedPR>) : PartialState
        data class BranchesLoaded(val list: List<BranchPR>) : PartialState
        data class JobsLoaded(val list: List<JobPR>) : PartialState
        data object SubmitSuccess : PartialState
        data class ViewerPdfChanged(val pdf: PdfDownloadPR?) : PartialState
        data object ViewerDownloadFailed : PartialState
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
}

sealed interface InspectionEvent {
    data class ShowToast(val message: String) : InspectionEvent
    data object NavigateBack : InspectionEvent
}
