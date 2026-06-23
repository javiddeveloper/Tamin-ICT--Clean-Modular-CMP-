package com.tamin.taminhamrah.mapper.personal

import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoDN
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoPR
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedPersonalDN
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedPersonalPR

fun DeceasedInfoDN.toPresentation(): DeceasedInfoPR {
    return DeceasedInfoPR(
        branchCode = branchCode,
        branchName = branchName,
        deadDate = deadDate,
        insuranceId = insuranceId,
        pensionerId = pensionerId,
        personal = personal?.toPresentation(),
        yearsAge = yearsAge,
        monthsAge = monthsAge,
        daysAge = daysAge,
        related = related
    )
}

fun DeceasedPersonalDN.toPresentation(): DeceasedPersonalPR {
    return DeceasedPersonalPR(
        cityOfIssueDesc = cityOfIssueDesc,
        dateOfBirth = dateOfBirth,
        fatherName = fatherName,
        firstName = firstName,
        lastName = lastName,
        nationalId = nationalId,
        gender = gender,
        idCardNumber = idCardNumber,
    )
}
