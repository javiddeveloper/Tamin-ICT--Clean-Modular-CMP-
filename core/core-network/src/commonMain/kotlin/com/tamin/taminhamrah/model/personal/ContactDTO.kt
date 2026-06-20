package com.tamin.taminhamrah.model.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContactDTO(
    @SerialName("zipCode") val zipCode: String?,
    @SerialName("address") val address: String?,
    @SerialName("creationTime") val creationTime: Long?,
    @SerialName("lastModificationTime") val lastModificationTime: Long?,
    @SerialName("lastModifiedBy") val lastModifiedBy: String?,
    @SerialName("personal") val personal: Int?,
    @SerialName("confirmed") val confirmed: Boolean?,
    @SerialName("phoneNumber") val phoneNumber: String?,
    @SerialName("createdBy") val createdBy: String?,
    @SerialName("id") val id: Long?,
)
