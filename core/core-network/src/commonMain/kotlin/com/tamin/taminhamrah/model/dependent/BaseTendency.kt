package com.tamin.taminhamrah.model.dependent

import kotlinx.serialization.SerialName

data class BaseTendency(
    @SerialName("createdBy") val createdBy: Any? = null,
    @SerialName("creationTime") val creationTime: Any? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("lastModificationTime") val lastModificationTime: Any? = null,
    @SerialName("lastModifiedBy") val lastModifiedBy: Any? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("statusDate") val statusDate: Any? = null,
    @SerialName("tendencyCode") val tendencyCode: String? = null,
    @SerialName("tendencyDescription") val tendencyDescription: String? = null,
)
