package com.tamin.taminhamrah.model.occurrence

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OccurrenceDocumentDTO(
    @SerialName("documentFile") val documentFile: OccurrenceDocumentFileDTO,
    @SerialName("ocurrenceDocumentType") val occurrenceDocumentType: OccurrenceDocumentTypeRefDTO,
)

@Serializable
data class OccurrenceDocumentFileDTO(
    @SerialName("id") val id: String,
)

@Serializable
data class OccurrenceDocumentTypeRefDTO(
    @SerialName("docTypeId") val docTypeId: String,
)
