package com.tamin.taminhamrah.model.pension.retirementInfo

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class RetirementRequestPR(
    val activityType: String,
    val address: String,
    val age: String,
    val birthDate: Long,
    val branchCode: String,
    val fatherName: String,
    val firstName: String,
    val gender: String,
    val insuranceNumber: String,
    val issuePlace: String,
    val idNumber: String,
    val lastName: String,
    val mobileNumber: String,
    val nationalCode: String,
    val phoneNumber: String,
    val workshopAddress: String,
    val workshopCode: String,
    val workshopName: String,
    val managerName: String
)
