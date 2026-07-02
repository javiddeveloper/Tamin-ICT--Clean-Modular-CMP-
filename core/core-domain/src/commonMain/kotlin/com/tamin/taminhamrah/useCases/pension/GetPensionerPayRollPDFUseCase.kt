package com.tamin.taminhamrah.useCases.pension

import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.pension.PensionRepository
import kotlinx.coroutines.flow.Flow

class GetPensionerPayRollPDFUseCase(
    private val repository: PensionRepository
) {
    suspend operator fun invoke(filters: List<ApiFilterDN>): Flow<PdfDownloadDN> {
        return repository.pensionerPayRollPDF(filters)
    }
}
