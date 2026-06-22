package com.tamin.taminhamrah.model.personal.deceasedInfo

data class DeceasedInfoDN(
    val branchCode: String?,
    val branchName: String?,
    val deadDate: String?,
    val insuranceId: String?,
    val pensionerId: String?,
    val personal: DeceasedPersonalDN?,
    val yearsAge: String?,
    val monthsAge: String?,
    val daysAge: String?,
    val related : String?,
)

data class DeceasedPersonalDN(
    val cityOfIssueDesc: String?,
    val dateOfBirth: Long?,
    val fatherName: String?,
    val firstName: String?,
    val lastName: String?,
    val nationalId: String?,
    val gender: String?,
    val idCardNumber: String?,
)
