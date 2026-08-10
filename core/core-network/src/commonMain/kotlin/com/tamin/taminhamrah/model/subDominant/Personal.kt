package com.tamin.taminhamrah.model.subDominant

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Personal(
    @SerialName("dateOfBirth") val dateOfBirthTimestamp: Long? = null,
    @SerialName("fatherName") val fatherName: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("gender") val gender: Gender? = null,
    @SerialName("idCardNumber") val idCardNumber: String? = null,
    @SerialName("idCardSerial1") val idCardSerial1: String? = null,
    @SerialName("idCardSerial2") val idCardSerial2: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("nationalId") val nationalId: String? = null,
)
