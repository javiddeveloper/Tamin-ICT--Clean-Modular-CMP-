package com.tamin.taminhamrah.model.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestDTO(
    @SerialName("deadDate") val deadDate: Long? = null,
    @SerialName("requestType") val requestType: RequestTypeDTO? = null,
    @SerialName("creationTime") val creationTime: Long? = null,
    @SerialName("lastModificationTime") val lastModificationTime: Long? = null,
    @SerialName("personal") val personal: Int? = null,
    @SerialName("dateOfBirth") val dateOfBirth: Long? = null,
    @SerialName("ssn") val ssn: String? = null,
    @SerialName("deleted") val deleted: Boolean? = null,
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("createdBy") val createdBy: String? = null,
    @SerialName("id") val id: Long? = null,
)
