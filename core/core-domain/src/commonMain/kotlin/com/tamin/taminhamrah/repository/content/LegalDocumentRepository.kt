package com.tamin.taminhamrah.repository.content

import com.tamin.taminhamrah.model.content.LegalDocumentDN

/**
 * Fetches a server-managed text document by id — تعهدنامه wording today, other notices/terms later.
 *
 * Backed by a bundled asset for now; a real `GET .../legal-document/{id}` endpoint will replace the
 * implementation without touching callers. Missing ids throw.
 */
interface LegalDocumentRepository {
    suspend fun getLegalDocument(id: String): LegalDocumentDN
}
