package com.tamin.taminhamrah.model.personal.survivorDependent

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SurvivorDependentDTO(
    @SerialName("relationWithTamin")
    val relationWithTamin: SurvivorDependentInfoDTO? = null,
)

@Serializable
data class SurvivorDependentInfoDTO(
    @SerialName("personal")
    val personal: SurvivorDependentPersonalDTO? = null,
    @SerialName("insuranceId")
    val insuranceId: String? = null,
    @SerialName("relationWithTamin")
    val relationWithTamin: SurvivorDependentTendencyInfoDTO? = null,
)

@Serializable
data class SurvivorDependentPersonalDTO(
    @SerialName("firstName")
    val firstName: String? = null,
    @SerialName("lastName")
    val lastName: String? = null,
    @SerialName("nationalId")
    val nationalId: String? = null,
    @SerialName("fatherName")
    val fatherName: String? = null,
    @SerialName("idCardNumber")
    val idCardNumber: String? = null,
    @SerialName("cityOfIssue")
    val cityOfIssue: String? = null,
    @SerialName("gender")
    val gender: SurvivorDependentGenderDTO? = null,
    @SerialName("dateOfBirth")
    val dateOfBirth: Long? = null,
)

@Serializable
data class SurvivorDependentGenderDTO(
    @SerialName("genderCode")
    val genderCode: String? = null,
    @SerialName("genderDesc")
    val genderDesc: String? = null,
)

@Serializable
data class SurvivorDependentTendencyInfoDTO(
    @SerialName("baseTendency")
    val baseTendency: SurvivorDependentBaseTendencyDTO? = null,
)

@Serializable
data class SurvivorDependentBaseTendencyDTO(
    @SerialName("tendencyCode")
    val tendencyCode: String? = null,
)
