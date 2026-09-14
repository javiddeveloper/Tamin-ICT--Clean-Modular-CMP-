package com.tamin.taminhamrah.model.pension.disabilityRequest.medicalCommission

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommitteeRequestInfoDocumentDTO(
    @SerialName("documentId") val documentId: String? = null,
    @SerialName("committeeRequestInfo") val committeeRequestInfo: String? = null,
    @SerialName("documentTypeId") val documentTypeId: String? = null,
    @SerialName("documentFileId") val documentFileId: String? = null,
)
