package com.tamin.taminhamrah.model.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RelationWithTaminDTO(
    @SerialName("request") val request: RequestDTO? = null,
    @SerialName("creationTime") val creationTime: Long? = null,
    @SerialName("endDate") val endDate: Long? = null,
    @SerialName("lastModificationTime") val lastModificationTime: Long? = null,
    @SerialName("confirmed") val confirmed: Boolean? = null,
    @SerialName("organizationId") val organizationId: String? = null,
    @SerialName("insuranceId") val insuranceId: String? = null,
    @SerialName("id2") val id2: Int? = null,
    @SerialName("id") val id: Long? = null,
    @SerialName("lastModifiedBy") val lastModifiedBy: String? = null,
    @SerialName("personal") val personal: Int? = null,
    @SerialName("deleted") val deleted: Boolean? = null,
    @SerialName("createdBy") val createdBy: String? = null,
    @SerialName("relationWithTamin") val relationWithTamin: RelationWithTaminDTO? = null
)
