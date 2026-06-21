package com.tamin.taminhamrah.model.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseTypeDTO(
    @SerialName("relationTypeCode") val relationTypeCode: String? = null,
    @SerialName("relationTypeDescription") val relationTypeDescription: String? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("serviceTypeDescription") val serviceTypeDescription: String? = null,
    @SerialName("serviceTypeCode") val serviceTypeCode: String? = null,
    @SerialName("audienceTypeDescription") val audienceTypeDescription: String? = null,
    @SerialName("audienceTypeCode") val audienceTypeCode: String? = null,
    @SerialName("statusDate") val statusDate: String? = null,
    @SerialName("creationTime") val creationTime: Long? = null,
    @SerialName("lastModificationTime") val lastModificationTime: Long? = null,
    @SerialName("createdBy") val createdBy: String? = null,
    @SerialName("lastModifiedBy") val lastModifiedBy: String? = null,
)
