package com.tamin.taminhamrah.useCases.pension

import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import kotlinx.coroutines.flow.Flow

class GetMedicalCommissionPdfUseCase(
    private val pensionRepository: PensionRepository
) {
    suspend operator fun invoke(lastWorkshop: String): Flow<PdfDownloadDN> {
        return pensionRepository.getMedicalCommissionPdf(lastWorkshop)
    }
}
