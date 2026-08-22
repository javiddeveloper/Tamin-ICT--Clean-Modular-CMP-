package com.tamin.taminhamrah.model.occurrence

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * occurence/office-personalInfo is one of the legacy occurrence endpoints that doesn't reliably
 * send every key (confirmed in production — a real response omitted nationalCode, insuranceNumber,
 * branchCode, nationality and insuranceType outright), so every field needs a default here or real
 * responses fail to deserialize with a [kotlinx.serialization.MissingFieldException].
 */
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
