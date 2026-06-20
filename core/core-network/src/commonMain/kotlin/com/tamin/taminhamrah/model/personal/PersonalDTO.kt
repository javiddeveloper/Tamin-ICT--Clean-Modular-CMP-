package com.tamin.taminhamrah.model.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonalDTO(
    @SerialName("lastName") val lastName: String?,
    @SerialName("fatherName") val fatherName: String?,
    @SerialName("cityOfIssue") val cityOfIssue: CityDTO?,
    @SerialName("idCardSerial1") val idCardSerial1: String?,
    @SerialName("gender") val gender: Gender2DTO?,
    @SerialName("creationTime") val creationTime: Long?,
    @SerialName("idCardSerial2") val idCardSerial2: String?,
    @SerialName("nation") val nation: NationDTO?,
    @SerialName("lastModificationTime") val lastModificationTime: Long?,
    @SerialName("confirmed") val confirmed: Boolean?,
    @SerialName("ssn") val ssn: String?,
    @SerialName("id") val id: Long?,
    @SerialName("dateOfDead") val dateOfDead: Long?,
    @SerialName("lastModifiedBy") val lastModifiedBy: String?,
    @SerialName("dateOfBirth") val dateOfBirth: Long?,
    @SerialName("firstName") val firstName: String?,
    @SerialName("nationalId") val nationalId: String?,
    @SerialName("createdBy") val createdBy: String?,
    @SerialName("idCardNumber") val idCardNumber: String?,
    @SerialName("contacts") val contacts: List<ContactDTO>?,
)
