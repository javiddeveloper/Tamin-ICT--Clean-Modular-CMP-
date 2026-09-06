package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.model.contracts.ContractPremiumType
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class DownloadContractReportUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(premiumType: ContractPremiumType): Flow<PdfDownloadDN> =
        contractsRepository.downloadContractReport(premiumType)
}
