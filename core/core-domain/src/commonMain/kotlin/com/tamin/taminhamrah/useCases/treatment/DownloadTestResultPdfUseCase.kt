package com.tamin.taminhamrah.useCases.treatment

import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import kotlinx.coroutines.flow.Flow

class DownloadTestResultPdfUseCase(private val repository: TreatmentRepository) {
    suspend operator fun invoke(
        patientID: String?, noteHeadEprescID: String?, currentUserNationalCode: String?
    ): Flow<PdfDownloadDN> =
        repository.downloadTestResultPdf(patientID, noteHeadEprescID, currentUserNationalCode)
}
