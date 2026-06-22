package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoDN
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoDTO
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedPersonalDN
import com.tamin.taminhamrah.model.personal.deceasedInfo.PersonalDTO

fun DeceasedInfoDTO.toDomain(): DeceasedInfoDN {
    return DeceasedInfoDN(
        branchCode = branchCode,
        branchName = branchName,
        deadDate = deadDate,
        insuranceId = insuranceId,
        pensionerId = pensionerId,
        personal = personal?.toDomain(),
        yearsAge = yearsAge,
        monthsAge = monthsAge,
        daysAge = daysAge,
        related = related
    )
}

fun PersonalDTO.toDomain(): DeceasedPersonalDN {
    return DeceasedPersonalDN(
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
