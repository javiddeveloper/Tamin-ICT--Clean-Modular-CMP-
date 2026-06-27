package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.repository.common.CommonRepository
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import io.ktor.client.statement.HttpStatement

class GetRegistrationDeclarationFormUseCase(
    private val repository: CommonRepository
) {
    suspend operator fun invoke(): HttpStatement {
        return repository.getRegistrationDeclarationForm()
    }
}
