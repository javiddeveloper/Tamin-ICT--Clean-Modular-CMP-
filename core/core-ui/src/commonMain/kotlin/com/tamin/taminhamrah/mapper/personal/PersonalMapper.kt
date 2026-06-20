package com.tamin.taminhamrah.mapper.personal

import com.tamin.taminhamrah.model.personal.PersonalDN
import com.tamin.taminhamrah.model.personal.PersonalPR
import com.tamin.taminhamrah.model.personal.PersonalInfoDN
import com.tamin.taminhamrah.model.personal.PersonalInfoPR
import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.AgePR

fun PersonalInfoDN.toPresentation(): PersonalInfoPR {
    return PersonalInfoPR(
        insuranceId = insuranceId ?: "",
        branch = branch ?: "",
        mobileNumber = mobileNumber ?: "",
        provinceName = provinceName ?: "",
        personal = personal?.toPresentation()
    )
}

fun PersonalDN.toPresentation(): PersonalPR {
    return PersonalPR(
        firstName = firstName ?: "",
        lastName = lastName ?: "",
        fatherName = fatherName ?: "",
        nationalId = nationalId ?: "",
        ssn = ssn ?: "",
        genderDesc = genderDesc ?: "",
        dateOfBirth = dateOfBirth?.toString() ?: ""
    )
}

fun AgeDN.toPresentation(): AgePR {
    return AgePR(
        age = age ?: "",
        birthDate = birthDate ?: ""
    )
}
