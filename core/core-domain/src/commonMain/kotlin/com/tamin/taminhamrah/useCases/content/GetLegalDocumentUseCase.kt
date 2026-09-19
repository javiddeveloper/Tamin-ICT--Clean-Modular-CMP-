package com.tamin.taminhamrah.useCases.content

import com.tamin.taminhamrah.model.content.LegalDocumentDN
import com.tamin.taminhamrah.repository.content.LegalDocumentRepository

/** Reads one server-managed text document (e.g. the تعهدنامه wording) by its id. */
class GetLegalDocumentUseCase(private val repository: LegalDocumentRepository) {
    suspend operator fun invoke(id: String): LegalDocumentDN = repository.getLegalDocument(id)
}
