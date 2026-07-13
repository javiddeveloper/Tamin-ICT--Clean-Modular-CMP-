package com.tamin.taminhamrah.model.pension.retirement

import com.tamin.taminhamrah.model.personal.CityDTO
import com.tamin.taminhamrah.model.personal.GenderDTO
import com.tamin.taminhamrah.model.personal.NationDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonalDTO(
    @SerialName("cityOfBirth") val cityOfBirth: CityDTO? = null,
    @SerialName("cityOfIssue") val cityOfIssue: CityDTO? = null,
    @SerialName("confirmed") val confirmed: Boolean? = null,
    @SerialName("dateOfBirth") val dateOfBirth: Long? = null,
    @SerialName("fatherName") val fatherName: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("gender") val gender: GenderDTO? = null,
    @SerialName("idCardNumber") val idCardNumber: String? = null,
    @SerialName("idCardSerial1") val idCardSerial1: String? = null,
    @SerialName("idCardSerial2") val idCardSerial2: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("nation") val nation: NationDTO? = null,
    @SerialName("nationalId") val nationalId: String? = null,
)
