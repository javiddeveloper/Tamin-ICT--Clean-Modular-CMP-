package com.tamin.taminhamrah.model.personal.disabilityRequest.disabilityRequestPersonal

import com.tamin.taminhamrah.model.personal.disabilityRequest.GenderDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonalDTO(
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("fatherName") val fatherName: String? = null,
    @SerialName("idCardNumber") val idCardNumber: String? = null,
    @SerialName("cityOfIssue") val cityOfIssue: CityOfIssueDTO? = null,
    @SerialName("dateOfBirth") val dateOfBirth: Long? = null,
    @SerialName("gender") val gender: GenderDTO? = null,
)
