package com.tamin.taminhamrah.model.contracts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaveContactRequestDTO(
    @SerialName("address") val address: String,
    @SerialName("mobile") val mobile: String,
    @SerialName("personal") val personal: SaveContactPersonalDTO,
    @SerialName("phoneNumber") val phoneNumber: String,
    @SerialName("zipCode") val zipCode: String,
)

@Serializable
data class SaveContactPersonalDTO(
    @SerialName("ssn") val ssn: String,
)
