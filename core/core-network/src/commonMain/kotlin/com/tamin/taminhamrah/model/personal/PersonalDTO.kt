package com.tamin.taminhamrah.model.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonalDTO(
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("fatherName") val fatherName: String? = null,
    @SerialName("country") val country: CityDTO? = null,
    @SerialName("cityOfIssue") val cityOfIssue: CityDTO? = null,
    @SerialName("cityOfBirth") val cityOfBirth: CityDTO? = null,
    @SerialName("idCardSerial1") val idCardSerial1: String? = null,
    @SerialName("gender") val gender: GenderDTO? = null,
    @SerialName("creationTime") val creationTime: Long? = null,
    @SerialName("idCardSerial2") val idCardSerial2: String? = null,
    @SerialName("nation") val nation: NationDTO? = null,
    @SerialName("lastModificationTime") val lastModificationTime: Long? = null,
    @SerialName("confirmed") val confirmed: Boolean? = null,
    @SerialName("ssn") val ssn: String? = null,
    @SerialName("id") val id: Long? = null,
    @SerialName("dateOfDead") val dateOfDead: Long? = null,
    @SerialName("lastModifiedBy") val lastModifiedBy: String? = null,
    @SerialName("dateOfBirth") val dateOfBirth: Long? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("createdBy") val createdBy: String? = null,
    @SerialName("idCardNumber") val idCardNumber: String? = null,
    // Live `survivor-request/personal` returns relationWithTamins as a list of nested
    // PersonalInfo-shaped objects (Jackson identity refs like `"personal": 2`), not List<Int>.
    // Legacy used Any?; we omit it so ignoreUnknownKeys skips the graph. Add a DTO later if needed.
    @SerialName("contacts") val contacts: List<ContactDTO>? = null,
)
