package com.tamin.taminhamrah.model.dependent

import kotlinx.serialization.SerialName

data class BaseRelationType(
    @SerialName("createdBy") val createdBy: Any? = null,
    @SerialName("creationTime") val creationTime: Any? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("lastModificationTime") val lastModificationTime: Any? = null,
    @SerialName("lastModifiedBy") val lastModifiedBy: Any? = null,
    @SerialName("relationTypeCode") val relationTypeCode: String? = null,
    @SerialName("relationTypeDescription") val relationTypeDescription: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("statusDate") val statusDate: Any? = null,
)
