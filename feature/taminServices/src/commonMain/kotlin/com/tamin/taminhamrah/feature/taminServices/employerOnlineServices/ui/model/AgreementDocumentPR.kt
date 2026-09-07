package com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.model

import androidx.compose.runtime.Immutable

/**
 * The تعهدنامه wording shown on step 2, ready to render: [intro] already has the signed-in user's
 * name and national code substituted in, [clauses] are the numbered paragraphs, [acknowledgement]
 * is the line next to the consent checkbox.
 *
 * Sourced from `GetLegalDocumentUseCase` — a bundled asset today, a `.../legal-document/{id}`
 * endpoint later — so the screen never carries the raw text itself.
 */
@Immutable
data class AgreementDocumentPR(
    val intro: String = "",
    val clauses: List<String> = emptyList(),
    val acknowledgement: String = "",
)
