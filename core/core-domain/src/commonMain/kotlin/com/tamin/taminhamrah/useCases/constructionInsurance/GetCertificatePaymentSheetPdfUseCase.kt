package com.tamin.taminhamrah.useCases.constructionInsurance

import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.repository.constructionInsurance.ConstructionInsuranceRepository
import kotlinx.coroutines.flow.Flow

class GetCertificatePaymentSheetPdfUseCase(
    private val repository: ConstructionInsuranceRepository
) {
    operator fun invoke(debitNumber: String, branchCode: String): Flow<PdfDownloadDN> =
        repository.getCertificatePaymentSheetPdf(debitNumber, branchCode)
}
