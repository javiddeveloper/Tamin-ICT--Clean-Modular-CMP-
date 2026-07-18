package com.tamin.taminhamrah.model.pension.retirement

import com.tamin.taminhamrah.model.personal.PersonalDN

data class RetirementPersonalDN(
    val branch: String?,
    val branchName: String?,
    val insuranceId: String?,
    val mobileNumber: String?,
    val organizationId: String?,
    val personal: PersonalDN?,
    val provinceName: String?,
    val work: WorkDN?,
    val strAge: String?,
    val verificationResult: String?
)

data class WorkDN(
    val job: JobDN?,
    val workshopId: String?
)

data class JobDN(
    val jobCode: String?,
    val jobDescription: String?,
    val status: String?,
    val statusDate: String?
)
