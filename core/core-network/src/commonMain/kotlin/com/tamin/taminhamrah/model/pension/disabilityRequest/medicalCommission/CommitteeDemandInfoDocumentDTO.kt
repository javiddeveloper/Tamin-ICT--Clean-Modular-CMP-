package com.tamin.taminhamrah.model.pension.disabilityRequest.medicalCommission

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommitteeDemandInfoDocumentDTO(
    @SerialName("documentId") val documentId: Long? = null,
    @SerialName("ownerId") val ownerId: Long? = null,
    @SerialName("documentTypeId") val documentTypeId: String? = null,
    @SerialName("documentFileId") val documentFileId: String? = null,
)
