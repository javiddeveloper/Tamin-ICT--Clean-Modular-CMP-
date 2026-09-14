package com.tamin.taminhamrah.model.personal

data class DisabilityPersonalInfoDN(
    val branch: String?,
    val branchName: String?,
    val confirmed: Boolean?,
    val insuranceId: String?,
    val mobileNumber: String?,
    val personal: DisabilityPersonalDN?,
    val provinceName: String?,
    val work: DisabilityWorkDN?,
    val yearsAge: String?,
    val monthsAge: String?,
    val daysAge: String?,
    val strAge: String?,
)

data class DisabilityPersonalDN(
    val firstName: String?,
    val lastName: String?,
    val nationalId: String?,
    val fatherName: String?,
    val idCardNumber: String?,
    val cityOfIssue: String?,
    val dateOfBirth: Long?,
    val genderDesc: String?,
    val genderCode: String? = null,
)

data class DisabilityWorkDN(
    val jobDescription: String?,
    val workshopId: String?,
)
