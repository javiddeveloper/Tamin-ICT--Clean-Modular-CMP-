package com.tamin.taminhamrah.useCases.treatment

import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import kotlinx.coroutines.flow.Flow

class GetTreatmentCostsPDFUseCase(private val repository: TreatmentRepository) {
    suspend operator fun invoke(repId: String): Flow<PdfDownloadDN> =
        repository.getTreatmentCostsPDF(repId)
}
