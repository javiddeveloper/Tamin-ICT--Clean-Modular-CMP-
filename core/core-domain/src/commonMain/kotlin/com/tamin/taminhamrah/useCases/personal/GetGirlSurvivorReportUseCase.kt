package com.tamin.taminhamrah.useCases.personal

import com.tamin.taminhamrah.model.personal.girlSurvivor.GirlSurvivorReportParamsDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import kotlinx.coroutines.flow.Flow

class GetGirlSurvivorReportUseCase(
    private val personalRepository: PersonalRepository,
) {
    operator fun invoke(params: GirlSurvivorReportParamsDN): Flow<PdfDownloadDN> {
        return personalRepository.getGirlSurvivorReport(params)
    }
}
