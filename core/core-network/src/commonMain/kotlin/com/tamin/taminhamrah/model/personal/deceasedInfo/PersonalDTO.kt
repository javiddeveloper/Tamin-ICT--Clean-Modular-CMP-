package com.tamin.taminhamrah.model.personal.deceasedInfo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonalDTO (
    @SerialName("cityOfIssueDesc") val cityOfIssueDesc: String? = null,
    @SerialName("dateOfBirth") val dateOfBirth: Long? = null,
    @SerialName("fatherName") val fatherName: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("gender") val gender: String? = null,
    @SerialName("idCardNumber") val idCardNumber: String? = null,
)
