package com.tamin.taminhamrah.model.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContactDTO(
    @SerialName("zipCode") val zipCode: String? = null,
    @SerialName("address") val address: String? = null,
    @SerialName("creationTime") val creationTime: Long? = null,
    @SerialName("lastModificationTime") val lastModificationTime: Long? = null,
    @SerialName("lastModifiedBy") val lastModifiedBy: String? = null,
    @SerialName("personal") val personal: Int? = null,
    @SerialName("confirmed") val confirmed: Boolean? = null,
    @SerialName("phoneNumber") val phoneNumber: String? = null,
    @SerialName("createdBy") val createdBy: String? = null,
    @SerialName("id") val id: Long? = null,
)
