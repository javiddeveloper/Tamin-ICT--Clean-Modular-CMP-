package com.tamin.taminhamrah.model.subDominant.disabilityRequest

import com.tamin.taminhamrah.model.subDominant.Gender
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class PersonaDTO(
    @SerialName("firstName") val firstName: String ?,
    @SerialName("lastName") val lastName: String ?,
    @SerialName("nationalId") val nationalId: String?,
    @SerialName("dateOfBirth") val dateOfBirth: Long?,
    @SerialName("fatherName") val fatherName: String?,
    @SerialName("gender") val gender: GenderDTO?,
    @SerialName("relation") val relation :String? = null
)
