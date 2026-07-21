package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.pension.retirement.RetirementPersonalDTO
import com.tamin.taminhamrah.model.pension.retirement.RetirementPersonalDN
import com.tamin.taminhamrah.model.pension.retirement.WorkDN
import com.tamin.taminhamrah.model.pension.retirement.JobDN
import com.tamin.taminhamrah.model.pension.retirement.PersonalDTO as RetirementPersonalDTO_Nested
import com.tamin.taminhamrah.model.personal.disabilityRequest.disabilityRequestPersonal.WorkDTO
import com.tamin.taminhamrah.model.personal.disabilityRequest.disabilityRequestPersonal.JobDTO
import com.tamin.taminhamrah.model.personal.PersonalDN

fun RetirementPersonalDTO.toDomain(): RetirementPersonalDN {
    return RetirementPersonalDN(
        branch = branch,
        branchName = branchName,
        insuranceId = insuranceId,
        mobileNumber = mobileNumber,
        organizationId = organizationId,
        personal = personal?.toDomain(),
        provinceName = provinceName,
        work = work?.toRetirementWorkDomain(),
        strAge = strAge,
        verificationResult = verificationResult
    )
}

fun RetirementPersonalDTO_Nested.toDomain(): PersonalDN {
    return PersonalDN(
        firstName = firstName,
        lastName = lastName,
        fatherName = fatherName,
        nationalId = nationalId,
        ssn = idCardNumber,
        genderDesc = gender?.genderDesc,
        dateOfBirth = dateOfBirth
    )
}

fun WorkDTO.toRetirementWorkDomain(): WorkDN {
    return WorkDN(
        job = job?.toRetirementJobDomain(),
        workshopId = workshopId
    )
}

fun JobDTO.toRetirementJobDomain(): JobDN {
    return JobDN(
        jobCode = jobCode,
        jobDescription = jobDescription,
        status = status,
        statusDate = statusDate
    )
}
