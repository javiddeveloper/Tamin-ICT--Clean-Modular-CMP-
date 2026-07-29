package com.tamin.taminhamrah.feature.treatment.ui.contract

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.treatment.TreatmentCostPR
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR

/** Immutable by construction: the reducer only ever `copy`s, and no collection here is mutated. */
@Immutable
data class CostsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val treatmentCostList: List<TreatmentCostPR> = emptyList(),

    /**
     * The fetched certificate waiting to be shown, or null when none has been asked for.
     *
     * Cleared as each download starts: the payload is a single-use stream, so a stale one would
     * render as an empty file. Which viewer is open, and under what name it is saved, is the
     * screen's business — it may not need a download at all if the device already has the file.
     */
    val viewerPdf: PdfDownloadPR? = null,
    /** The last requested certificate could not be fetched, so the viewer can stop waiting. */
    val viewerDownloadFailed: Boolean = false,
) {
    sealed class PartialState {
        data class Loading(val isLoading: Boolean) : PartialState()
        data class Error(val message: String?) : PartialState()
        data object Reset : PartialState()
        data class TreatmentCostsLoaded(val list: List<TreatmentCostPR>) : PartialState()
        data class ViewerPdfChanged(val pdf: PdfDownloadPR?) : PartialState()
        data object ViewerDownloadFailed : PartialState()
    }
}

sealed class CostsIntent {
    data object LoadList : CostsIntent()
    data class DownloadPdf(val repId: String) : CostsIntent()
    data class SendToInbox(val repId: String) : CostsIntent()

    /** The viewer was closed; drops the fetched PDF so a re-open never shows a drained stream. */
    data object DismissPdfViewer : CostsIntent()
}

sealed class CostsEvent {
    data class ShowToast(val message: String) : CostsEvent()
}
