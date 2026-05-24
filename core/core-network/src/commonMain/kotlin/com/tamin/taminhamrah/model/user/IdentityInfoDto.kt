package com.tamin.core.network.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IdentityInfoDto(
    @SerialName("cityOfBirthId") val cityOfBirthId: String?,
    @SerialName("cityOfIssueId") val cityOfIssueId: String?,
    @SerialName("countryId") val countryId: String?,
    @SerialName("dateOfBirth") val dateOfBirth: Long?,
    @SerialName("fatherName") val fatherName: String?,
    @SerialName("firstName") val firstName: String?,
    @SerialName("gender") val gender: String?,
    @SerialName("id") val id: Int,
    @SerialName("idCardNumber") val idCardNumber: String?,
    @SerialName("idCardSerial1") val idCardSerial1: String?,
    @SerialName("idCardSerial2") val idCardSerial2: String?,
    @SerialName("lastName") val lastName: String?,
    @SerialName("nationalId") val nationalId: String?,
    @SerialName("ssn") val ssn: String?
)


