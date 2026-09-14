package com.tamin.taminhamrah.model.pension.disabilityRequest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DisabilitySaveInfoRequest(
    @SerialName("activityType") val activityType: String? = null,
    @SerialName("address") val address: String? = null,
    @SerialName("age") val age: String? = null,
    @SerialName("birthDate") val birthDate: Long? = null,
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("fatherName") val fatherName: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("gender") val gender: String? = null,
    @SerialName("idNumber") val idNumber: String? = null,
    @SerialName("insuranceNumber") val insuranceNumber: String? = null,
    @SerialName("issuePlace") val issuePlace: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("managerName") val managerName: String? = null,
    @SerialName("mobileNumber") val mobileNumber: String? = null,
    @SerialName("nationalCode") val nationalCode: String? = null,
    @SerialName("pensionRequestDocList") val pensionRequestDocList: List<DisabilityDocumentDTO>? = null,
    @SerialName("phoneNumber") val phoneNumber: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("workshopAddress") val workshopAddress: String? = null,
    @SerialName("workshopCode") val workshopCode: String? = null,
    @SerialName("workshopName") val workshopName: String? = null,
)
