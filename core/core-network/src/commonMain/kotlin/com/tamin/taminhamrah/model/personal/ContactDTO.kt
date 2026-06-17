package com.tamin.taminhamrah.model.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContactDTO(
    @SerialName("zipCode") val zipCode: String? = null,
    @SerialName("address") val address: String? = null,
    @SerialName("creationTime") val creationTime: Long? = null,
    @SerialName("city") val city: String? = null, // Changed from Any? to String? or could be CityDTO?
    @SerialName("lastModificationTime") val lastModificationTime: Long? = null,
    @SerialName("lastModifiedBy") val lastModifiedBy: String? = null,
    @SerialName("mobile") val mobile: String? = null, // Changed from Any? to String?
    @SerialName("personal") val personal: Int? = null,
    @SerialName("confirmed") val confirmed: Boolean? = null,
    @SerialName("phoneNumber") val phoneNumber: String? = null,
    @SerialName("deleted") val deleted: Boolean? = null, // Changed from Any? to Boolean?
    @SerialName("createdBy") val createdBy: String? = null,
    @SerialName("id") val id: Long? = null,
)
