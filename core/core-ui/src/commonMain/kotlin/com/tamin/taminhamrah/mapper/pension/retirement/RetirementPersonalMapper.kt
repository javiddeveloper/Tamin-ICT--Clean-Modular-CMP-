package com.tamin.taminhamrah.mapper.pension.retirement

import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.model.pension.retirement.RetirementPersonalDN
import com.tamin.taminhamrah.model.pension.retirement.RetirementPersonalPR
import com.tamin.taminhamrah.model.pension.retirement.WorkDN
import com.tamin.taminhamrah.model.pension.retirement.WorkPR
import com.tamin.taminhamrah.model.pension.retirement.JobDN
import com.tamin.taminhamrah.model.pension.retirement.JobPR

fun RetirementPersonalDN.toPresentation(): RetirementPersonalPR {
    return RetirementPersonalPR(
        branch = branch ?: "",
        branchName = branchName ?: "",
        insuranceId = insuranceId ?: "",
        mobileNumber = mobileNumber ?: "",
        organizationId = organizationId ?: "",
        personal = personal?.toPresentation(),
        provinceName = provinceName ?: "",
        work = work?.toPresentation(),
        strAge = strAge ?: "",
        verificationResult = verificationResult ?: ""
    )
}

fun WorkDN.toPresentation(): WorkPR {
    return WorkPR(
        job = job?.toPresentation(),
        workshopId = workshopId ?: ""
    )
}

fun JobDN.toPresentation(): JobPR {
    return JobPR(
        jobCode = jobCode ?: "",
        jobDescription = jobDescription ?: "",
        status = status ?: "",
        statusDate = statusDate ?: ""
    )
}
