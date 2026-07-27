package com.tamin.taminhamrah.useCases.treatment

import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import kotlinx.coroutines.flow.Flow

class DownloadLabResultPdfUseCase(private val repository: TreatmentRepository) {
    suspend operator fun invoke(
        patientID: String?, noteHeadEprescID: String?, currentUserNationalCode: String?
    ): Flow<PdfDownloadDN> =
        repository.downloadLabResultPdf(patientID, noteHeadEprescID, currentUserNationalCode)
}
