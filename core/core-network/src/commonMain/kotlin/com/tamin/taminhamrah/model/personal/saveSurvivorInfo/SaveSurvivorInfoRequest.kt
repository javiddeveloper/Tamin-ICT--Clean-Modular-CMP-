package com.tamin.taminhamrah.model.personal.saveSurvivorInfo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaveSurvivorInfoRequest(
    @SerialName("address")val address: String? = null,
    @SerialName("age")val age: String? = null,
    @SerialName("birthDate")val birthDate: Long? = null,
    @SerialName("branchCode")val branchCode: String? = null,
    @SerialName("survivorInsuranceId")val survivorInsuranceId: String? = null,
    @SerialName("survivorNationalId")val survivorNationalId: String? = null,
    @SerialName("deathType")val deathType: String? = null, //Type of death (1 == insured death, 2 == pensioner death)
    @SerialName("dependencyType")val dependencyType: DependencyTypeRequest? = null,
    @SerialName("fatherName")val fatherName: String? = null,
    @SerialName("firstName")val firstName: String? = null,
    @SerialName("gender")val gender: String? = null,
    @SerialName("idCardNumber")val idCardNumber: String? = null,
    @SerialName("insuranceNumber")val insuranceNumber: String? = null,
    @SerialName("issuePlace")val issuePlace: String? = null,
    @SerialName("lastName")val lastName: String? = null,
    @SerialName("mobileNumber")val mobileNumber: String? = null,
    @SerialName("deceasedNationalId")val deceasedNationalId: String? = null,
    @SerialName("pensionId")val pensionId: String? = null,
    @SerialName("pensionRequestDocList")val pensionRequestDocList: List<PensionDocRequest>? = null,
    @SerialName("phoneNumber")val phoneNumber: String? = null,
    @SerialName("status")val status: String? = null, // (3=Initial request,2= non-approval, 1= final approval)
)
