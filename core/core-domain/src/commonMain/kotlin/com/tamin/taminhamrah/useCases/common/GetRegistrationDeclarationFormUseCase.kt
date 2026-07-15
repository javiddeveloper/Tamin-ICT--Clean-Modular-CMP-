package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.repository.common.CommonRepository
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import io.ktor.client.statement.HttpStatement
import kotlinx.coroutines.flow.Flow

class GetRegistrationDeclarationFormUseCase(
    private val repository: CommonRepository
) {
    operator fun invoke(): Flow<ByteArray> {
        return repository.getRegistrationDeclarationForm()
    }
}
