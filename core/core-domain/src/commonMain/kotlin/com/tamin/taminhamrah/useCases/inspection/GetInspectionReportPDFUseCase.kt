package com.tamin.taminhamrah.useCases.inspection

import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.repository.inspection.InspectionRepository

class GetInspectionReportPDFUseCase(
    private val repository: InspectionRepository
) {
    suspend operator fun invoke(inspectionNo: String): PdfDownloadDN {
        return repository.getInspectionReportPDF(inspectionNo)
    }
}
