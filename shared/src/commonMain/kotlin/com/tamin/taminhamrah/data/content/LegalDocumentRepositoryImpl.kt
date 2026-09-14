package com.tamin.taminhamrah.data.content

import com.tamin.taminhamrah.model.content.LegalDocumentDN
import com.tamin.taminhamrah.repository.content.LegalDocumentRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import taminx.core.core_ui.Res

/**
 * Serves legal texts from a JSON asset keyed by id — a stand-in for the future
 * `GET .../legal-document/{id}` endpoint. Swapping in a remote data source here leaves every
 * [LegalDocumentRepository] caller untouched.
 *
 * The parsed file is cached in memory for the process lifetime; it is a handful of static strings
 * that never change between app launches.
 */
internal class LegalDocumentRepositoryImpl : LegalDocumentRepository {

    private val json = Json { ignoreUnknownKeys = true }
    private var documents: Map<String, LegalDocumentDN>? = null

    override suspend fun getLegalDocument(id: String): LegalDocumentDN {
        val all = documents ?: loadAll().also { documents = it }
        return all[id] ?: throw NoSuchElementException("Legal document '$id' is missing from $ASSET_PATH")
    }

    private suspend fun loadAll(): Map<String, LegalDocumentDN> {
        val raw = Res.readBytes(ASSET_PATH).decodeToString()
        return json.decodeFromString<Map<String, LegalDocumentEntry>>(raw)
            .mapValues { (id, entry) -> entry.toDomain(id) }
    }

    @Serializable
    private data class LegalDocumentEntry(
        val title: String = "",
        val intro: String = "",
        val clauses: List<String> = emptyList(),
        val acknowledgement: String = "",
    ) {
        fun toDomain(id: String) = LegalDocumentDN(
            id = id,
            title = title,
            intro = intro,
            clauses = clauses,
            acknowledgement = acknowledgement,
        )
    }

    private companion object {
        const val ASSET_PATH = "files/legal_documents.json"
    }
}
