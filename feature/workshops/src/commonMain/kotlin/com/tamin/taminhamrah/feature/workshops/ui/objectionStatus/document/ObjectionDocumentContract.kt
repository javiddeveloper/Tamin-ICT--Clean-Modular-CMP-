package com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.document

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionStatus
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionType

@Immutable
data class ObjectionDocumentUiState(
    val seqNo: Long = 0,
    val debitNumber: String = "",
    val workshopId: String = "",
    val objectionDate: String = "",
    val objectionType: WorkShopObjectionType = WorkShopObjectionType.UNKNOWN,
    val objectionStatus: WorkShopObjectionStatus = WorkShopObjectionStatus.UNKNOWN,
    val isDownloading: Boolean = false,
) {
    sealed interface PartialState {
        data class Opened(
            val seqNo: Long,
            val debitNumber: String,
            val workshopId: String,
            val objectionDate: String,
            val objectionType: WorkShopObjectionType,
            val objectionStatus: WorkShopObjectionStatus,
        ) : PartialState

        data object Downloading : PartialState

        /** Reached on both success and failure — [ObjectionDocumentEvent] carries the outcome. */
        data object DownloadFinished : PartialState
    }
}

sealed interface ObjectionDocumentIntent {
    data class Open(
        val seqNo: Long,
        val debitNumber: String,
        val workshopId: String,
        val objectionDate: String,
        val objectionType: WorkShopObjectionType,
        val objectionStatus: WorkShopObjectionStatus,
    ) : ObjectionDocumentIntent

    data object DownloadFile : ObjectionDocumentIntent
}

/**
 * One-shot outcome of a download, dispatched via `sendEvent` rather than stored in [ObjectionDocumentUiState].
 *
 * [DownloadSucceeded.pdf] wraps a single-use byte channel ([com.tamin.taminhamrah.ui.components.drainBytesOrNull]
 * drains it once) — keeping it in the persisted UiState let a screen recomposition after the fact
 * (e.g. a fresh Composition on the same ViewModel) replay the drain against an already-emptied
 * channel and show a false "no document" dialog for a download that had actually succeeded. An
 * event is consumed exactly once, so that can no longer happen.
 */
sealed interface ObjectionDocumentEvent {
    data class DownloadSucceeded(val pdf: PdfDownloadPR) : ObjectionDocumentEvent
    data object DownloadFailed : ObjectionDocumentEvent
}
