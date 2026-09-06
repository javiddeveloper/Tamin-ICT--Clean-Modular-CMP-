package com.tamin.taminhamrah.model.content

/**
 * A server-managed text document fetched by id — the تعهدنامه wording today, any other notice or
 * terms text the backend owns tomorrow.
 *
 * [intro] and each [clauses] entry are plain paragraphs; [intro] may carry `{name}` / `{nationalCode}`
 * placeholders the caller substitutes with the signed-in user's identity. Blank fields mean the
 * document simply omitted that part.
 */
data class LegalDocumentDN(
    val id: String = "",
    val title: String = "",
    val intro: String = "",
    val clauses: List<String> = emptyList(),
    val acknowledgement: String = "",
)
