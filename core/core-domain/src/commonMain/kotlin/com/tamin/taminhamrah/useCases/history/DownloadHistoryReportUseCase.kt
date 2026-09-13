package com.tamin.taminhamrah.useCases.history

import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow

class DownloadHistoryReportUseCase(
    private val repository: HistoryRepository
) {
    operator fun invoke(type: HistoryCertificateType): Flow<PdfDownloadDN> {
        return repository.downloadHistoryReport(type)
    }
}
