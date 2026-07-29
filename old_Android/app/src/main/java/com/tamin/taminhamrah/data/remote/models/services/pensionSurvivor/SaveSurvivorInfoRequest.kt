package com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor

import com.google.gson.annotations.SerializedName

data class SaveSurvivorInfoRequest(
    val address: String? = null,
    val age: String? = null,
    val birthDate: Long? = null,
    val branchCode: String? = null,
    @SerializedName("childInsuranceId")
    val survivorInsuranceId: String? = null,
    @SerializedName("childNationalId")
    val survivorNationalId: String? = null,
    val deathDate: Any? = null,
    val deathType: String? = null, //Type of death (1 == insured death, 2 == pensioner death)
    val dependencyType: DependencyType? = null,
    val fatherName: String? = null,
    val firstName: String? = null,
    val gender: String? = null,
    @SerializedName("idNumber")
    val idCardNumber: String? = null,
    val insuranceNumber: String? = null,
    val issuePlace: String? = null,
    val lastName: String? = null,
    val mobileNumber: String? = null,
    @SerializedName("nationalCode")
    val deceasedNationalId: String? = null,
    val pensionId: String? = null,
    val pensionRequestDocList: List<PensionRequestDoc?>? = null,
    val phoneNumber: String? = null,
    val status: String? = null, // (3=Initial request,2= non-approval, 1= final approval)
)

data class DependencyType(
    val code: String? = null,
)

data class PensionRequestDoc(
    val documentType: String? = null,
    val guid: String? = null,
    val id: Any? = null,
    val pensionRequest: Any? = null,
)
