package com.tamin.taminhamrah.model.pension.retirement

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.model.personal.PersonalPR
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class RetirementPersonalPR(
    val branch: String,
    val branchName: String,
    val insuranceId: String,
    val mobileNumber: String,
    val organizationId: String,
    val personal: PersonalPR?,
    val provinceName: String,
    val work: WorkPR?,
    val strAge: String,
    val verificationResult: String
)

@Immutable
@Serializable
data class WorkPR(
    val job: JobPR?,
    val workshopId: String
)

@Immutable
@Serializable
data class JobPR(
    val jobCode: String,
    val jobDescription: String,
    val status: String,
    val statusDate: String
)
