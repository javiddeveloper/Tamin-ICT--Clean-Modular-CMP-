package com.tamin.taminhamrah.model.personal

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class DisabilityPersonalInfoPR(
    val branch: String,
    val branchName: String,
    val confirmed: Boolean,
    val insuranceId: String,
    val mobileNumber: String,
    val personal: DisabilityPersonalPR?,
    val provinceName: String,
    val work: DisabilityWorkPR?,
    val yearsAge: String,
    val monthsAge: String,
    val daysAge: String,
    val strAge: String,
)

@Immutable
@Serializable
data class DisabilityPersonalPR(
    val firstName: String,
    val lastName: String,
    val nationalId: String,
    val fatherName: String,
    val idCardNumber: String,
    val cityOfIssue: String,
    val dateOfBirth: String,
    val genderDesc: String,
)

@Immutable
@Serializable
data class DisabilityWorkPR(
    val jobDescription: String,
    val workshopId: String,
)
