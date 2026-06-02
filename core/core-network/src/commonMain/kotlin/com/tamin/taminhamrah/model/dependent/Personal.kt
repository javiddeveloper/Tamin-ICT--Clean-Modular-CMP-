package com.tamin.taminhamrah.model.dependent

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Personal(
    @SerialName("dateOfBirth") val dateOfBirth: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("gender") val gender: Gender? = null,
    @SerialName("idCardNumber") val idCardNumber: String? = null,
    @SerialName("idCardSerial1") val idCardSerial1: String? = null,
    @SerialName("idCardSerial2") val idCardSerial2: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("subDominant") val subDominant: SubDominant? = null,
)
