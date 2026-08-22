package com.tamin.taminhamrah.model.occurrence

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OccurrencePersonalInfoDTO(
    @SerialName("nationalCode") val nationalCode: String?,
    @SerialName("firstName") val firstName: String?,
    @SerialName("lastName") val lastName: String?,
    @SerialName("fatherName") val fatherName: String?,
    @SerialName("gender") val gender: String?,
    @SerialName("birthDate") val birthDate: String?,
    @SerialName("insuranceNumber") val insuranceNumber: String?,
    @SerialName("branchCode") val branchCode: String?,
    @SerialName("nationality") val nationality: String?,
    @SerialName("insuranceType") val insuranceType: String?,
)
