package com.tamin.taminhamrah.model.personal.deceasedInfo

import androidx.compose.runtime.Immutable

@Immutable
data class DeceasedInfoPR(
    val branchCode: String?,
    val branchName: String?,
    val deadDate: String?,
    val insuranceId: String?,
    val pensionerId: String?,
    val personal: DeceasedPersonalPR?,
    val yearsAge: String?,
    val monthsAge: String?,
    val daysAge: String?,
    val related: String?,
)

@Immutable
data class DeceasedPersonalPR(
    val cityOfIssueDesc: String?,
    val dateOfBirth: Long?,
    val fatherName: String?,
    val firstName: String?,
    val lastName: String?,
    val nationalId: String?,
    val gender: String?,
    val idCardNumber: String?,
)
