package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.personal.PersonalDTO
import com.tamin.taminhamrah.model.personal.PersonalInfoDTO
import com.tamin.taminhamrah.model.personal.PersonalDN
import com.tamin.taminhamrah.model.personal.PersonalInfoDN

fun PersonalInfoDTO.toDomain(): PersonalInfoDN {
    return PersonalInfoDN(
        insuranceId = insuranceId,
        branch = branch,
        mobileNumber = mobileNumber,
        provinceName = provinceName,
        personal = personal?.toDomain()
    )
}

fun PersonalDTO.toDomain(): PersonalDN {
    return PersonalDN(
        firstName = firstName,
        lastName = lastName,
        fatherName = fatherName,
        nationalId = nationalId,
        ssn = ssn,
        genderDesc = gender?.genderDesc,
        dateOfBirth = dateOfBirth
    )
}
