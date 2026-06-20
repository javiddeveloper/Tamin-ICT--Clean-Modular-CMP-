package com.tamin.taminhamrah.model.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestDTO(
    @SerialName("deadDate") val deadDate: Long?,
    @SerialName("requestType") val requestType: RequestTypeDTO?,
    @SerialName("creationTime") val creationTime: Long?,
    @SerialName("lastModificationTime") val lastModificationTime: Long?,
    @SerialName("personal") val personal: Int?,
    @SerialName("dateOfBirth") val dateOfBirth: Long?,
    @SerialName("ssn") val ssn: String?,
    @SerialName("deleted") val deleted: Boolean?,
    @SerialName("nationalId") val nationalId: String?,
    @SerialName("createdBy") val createdBy: String?,
    @SerialName("id") val id: Long?,
)
