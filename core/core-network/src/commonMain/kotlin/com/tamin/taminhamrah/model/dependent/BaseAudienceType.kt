package com.tamin.taminhamrah.model.dependent

import kotlinx.serialization.SerialName

data class BaseAudienceType(
    @SerialName("audienceTypeCode") val audienceTypeCode: String? = null,
    @SerialName("audienceTypeDescription") val audienceTypeDescription: String? = null,
    @SerialName("createdBy") val createdBy: Any? = null,
    @SerialName("creationTime") val creationTime: Any? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("lastModificationTime") val lastModificationTime: Any? = null,
    @SerialName("lastModifiedBy") val lastModifiedBy: Any? = null,
    @SerialName("status") val status: Any? = null,
    @SerialName("statusDate") val statusDate: Any? = null,
)
