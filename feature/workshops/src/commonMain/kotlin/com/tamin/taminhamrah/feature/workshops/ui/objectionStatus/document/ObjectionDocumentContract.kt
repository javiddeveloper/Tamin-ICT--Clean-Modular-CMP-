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
    val pdf: PdfDownloadPR? = null,
    val downloadFailed: Boolean = false,
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
        data class PdfChanged(val pdf: PdfDownloadPR?) : PartialState
        data object DownloadFailed : PartialState
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

sealed interface ObjectionDocumentEvent
