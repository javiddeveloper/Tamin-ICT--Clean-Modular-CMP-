package com.tamin.taminhamrah.model.personal.girlSurvivor

import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.DependencyTypeRequest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConfirmGirlSurvivorRequestDTO(
    @SerialName("address") val address: String? = null,
    @SerialName("age") val age: String? = null,
    @SerialName("birthDate") val birthDate: Long? = null,
    @SerialName("childInsuranceId") val childInsuranceId: String? = null,
    @SerialName("childNationalId") val childNationalId: String? = null,
    @SerialName("deathDate") val deathDate: Long? = null,
    @SerialName("deathType") val deathType: String? = null,
    @SerialName("dependencyType") val dependencyType: DependencyTypeRequest? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("gender") val gender: String? = null,
    @SerialName("idNumber") val idNumber: String? = null,
    @SerialName("insuranceNumber") val insuranceNumber: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("mobileNumber") val mobileNumber: String? = null,
    @SerialName("nationalCode") val nationalCode: String? = null,
    @SerialName("pensionId") val pensionId: String? = null,
    @SerialName("phoneNumber") val phoneNumber: String? = null,
    @SerialName("status") val status: String? = null,
)
