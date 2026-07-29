package com.tamin.taminhamrah.data.remote.models.services

import com.google.gson.annotations.SerializedName

data class ConfirmSurvivorRequest(
    val address:String?,
    val age:String?,
    val birthDate:Long?,
    @SerializedName("childInsuranceId")
    val insuranceId:String?,
    @SerializedName("childNationalId")
    val nationalId:String?,
    val deathDate:Long?,
    val deathType:String?,
    val dependencyType:DependencyType?,
    val firstName:String?,
    val gender:String?,
    val idNumber:String?,
    val insuranceNumber:String?,
    val lastName:String?,
    val mobileNumber:String?,
    val nationalCode:String?,
    val pensionId:String?,
    val phoneNumber:String?,
    val status:String?
)

data class DependencyType(
    val code:String
)
