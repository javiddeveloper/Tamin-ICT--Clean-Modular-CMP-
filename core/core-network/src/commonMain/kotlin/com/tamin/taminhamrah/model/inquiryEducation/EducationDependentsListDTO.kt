package com.tamin.taminhamrah.model.inquiryEducation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EducationDependentsListDTO(
    @SerialName("list") val list: List<EducationDependentItemDTO>? = null,
    @SerialName("total") val total: Int? = null,
)

@Serializable
data class EducationDependentItemDTO(
    @SerialName("relationWithTamin") val relationWithTamin: EducationDependentRelationDTO? = null,
)

@Serializable
data class EducationDependentRelationDTO(
    @SerialName("personal") val personal: EducationDependentPersonalDTO? = null,
    @SerialName("relationWithTamin") val relationWithTamin: EducationDependentFamilyRelationDTO? = null,
)

@Serializable
data class EducationDependentPersonalDTO(
    @SerialName("dateOfBirth") val dateOfBirth: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("idCardNumber") val idCardNumber: String? = null,
    @SerialName("idCardSerial1") val idCardSerial1: String? = null,
    @SerialName("idCardSerial2") val idCardSerial2: String? = null,
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("gender") val gender: EducationDependentGenderDTO? = null,
    @SerialName("subDominant") val subDominant: EducationDependentSubDominantDTO? = null,
)

@Serializable
data class EducationDependentGenderDTO(
    @SerialName("genderCode") val genderCode: String? = null,
)

@Serializable
data class EducationDependentSubDominantDTO(
    @SerialName("dateOfExpire") val dateOfExpire: String? = null,
)

@Serializable
data class EducationDependentFamilyRelationDTO(
    @SerialName("baseTendency") val baseTendency: EducationDependentRelationDetailDTO? = null,
)

@Serializable
data class EducationDependentRelationDetailDTO(
    @SerialName("tendencyCode") val tendencyCode: String? = null,
    @SerialName("tendencyDescription") val tendencyDescription: String? = null,
)
