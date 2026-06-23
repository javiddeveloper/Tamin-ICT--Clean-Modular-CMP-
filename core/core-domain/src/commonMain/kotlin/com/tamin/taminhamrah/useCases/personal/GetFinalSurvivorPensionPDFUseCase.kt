package com.tamin.taminhamrah.useCases.personal

import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import kotlinx.coroutines.flow.Flow

class GetFinalSurvivorPensionPDFUseCase(
    private val personalRepository: PersonalRepository
) {
    operator fun invoke(): Flow<PdfDownloadDN> {
        return personalRepository.getFinalSurvivorPensionPDF()
    }
}
