package com.tamin.taminhamrah.useCases.treatment

import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import kotlinx.coroutines.flow.Flow

class GetPrescriptionPdfFileUseCase(private val repository: TreatmentRepository) {
    suspend operator fun invoke(prescriptionID: String): Flow<PdfDownloadDN> =
        repository.getPrescriptionPdfFile(prescriptionID)
}
