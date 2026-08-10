package com.tamin.taminhamrah.useCases.file

import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.repository.UserRepository

class DownloadDocumentUseCase(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(url: String): PdfDownloadDN = userRepository.downloadDocument(url)
}
