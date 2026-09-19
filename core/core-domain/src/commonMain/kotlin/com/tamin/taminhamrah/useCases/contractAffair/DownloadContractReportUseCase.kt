package com.tamin.taminhamrah.useCases.contractAffair

import com.tamin.taminhamrah.model.contractAffair.ContractPremiumType
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.repository.contractAffair.ContractAffairRepository
import kotlinx.coroutines.flow.Flow

class DownloadContractReportUseCase(
    private val contractAffairRepository: ContractAffairRepository,
) {
    operator fun invoke(premiumType: ContractPremiumType): Flow<PdfDownloadDN> =
        contractAffairRepository.downloadContractReport(premiumType)
}
