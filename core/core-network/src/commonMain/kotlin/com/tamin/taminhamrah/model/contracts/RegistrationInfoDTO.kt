package com.tamin.taminhamrah.model.contracts

import com.tamin.taminhamrah.model.personal.GenderDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegistrationInfoDTO(
    @SerialName("personalInfo") val personalInfo: RegistrationPersonalInfoDTO?,
    @SerialName("insuranceIdValidity") val insuranceIdValidity: Boolean?,
    @SerialName("mobileNumber") val mobileNumber: String?,
    @SerialName("insuranceId") val insuranceId: String?,
    @SerialName("lastContact") val lastContact: RegistrationContactDTO?,
)

@Serializable
data class RegistrationPersonalInfoDTO(
    @SerialName("firstName") val firstName: String?,
    @SerialName("lastName") val lastName: String?,
    @SerialName("nationalId") val nationalId: String?,
    @SerialName("dateOfBirth") val dateOfBirth: Long?,
    @SerialName("gender") val gender: GenderDTO?,
    @SerialName("ssn") val ssn: String?,
)

@Serializable
data class RegistrationContactDTO(
    @SerialName("address") val address: String?,
    @SerialName("zipCode") val zipCode: String?,
    @SerialName("mobile") val mobile: String?,
    @SerialName("phoneNumber") val phoneNumber: String?,
)
