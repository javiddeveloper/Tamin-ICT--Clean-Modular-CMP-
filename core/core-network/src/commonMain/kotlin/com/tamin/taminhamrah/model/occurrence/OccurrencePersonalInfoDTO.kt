package com.tamin.taminhamrah.model.occurrence

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OccurrencePersonalInfoDTO(
    @SerialName("nationalCode") val nationalCode: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("fatherName") val fatherName: String? = null,
    @SerialName("gender") val gender: String? = null,
    @SerialName("birthDate") val birthDate: String? = null,
    @SerialName("insuranceNumber") val insuranceNumber: String? = null,
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("nationality") val nationality: String? = null,
    @SerialName("insuranceType") val insuranceType: String? = null,
)
