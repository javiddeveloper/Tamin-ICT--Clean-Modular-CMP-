package com.tamin.taminhamrah.mapper.personal

import com.tamin.taminhamrah.model.personal.PersonalDN
import com.tamin.taminhamrah.model.personal.PersonalPR
import com.tamin.taminhamrah.model.personal.PersonalInfoDN
import com.tamin.taminhamrah.model.personal.PersonalInfoPR
import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.AgePR
import com.tamin.taminhamrah.model.personal.DisabilityDependentDN
import com.tamin.taminhamrah.model.personal.DisabilityDependentPR
import com.tamin.taminhamrah.model.personal.SubmitFinalSurvivorPensionDN
import com.tamin.taminhamrah.model.personal.SubmitFinalSurvivorPensionPR

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

fun DisabilityDependentDN.toPresentation(): DisabilityDependentPR {
    return DisabilityDependentPR(
        firstName = firstName ?: "",
        lastName = lastName ?: "",
        nationalId = nationalId ?: "",
        dateOfBirth = dateOfBirth?.toString() ?: "",
        fatherName = fatherName ?: "",
        genderDesc = genderDesc ?: "",
        relation = relation ?: "",
        tendencyDescription = tendencyDescription ?: ""
    )
}

fun List<DisabilityDependentDN>.toPresentation(): List<DisabilityDependentPR> {
    return map { it.toPresentation() }
}

fun SubmitFinalSurvivorPensionPR.toDomain(): SubmitFinalSurvivorPensionDN {
    return SubmitFinalSurvivorPensionDN(
        id = id
    )
}

fun SubmitFinalSurvivorPensionDN.toPresentation(): SubmitFinalSurvivorPensionPR {
    return SubmitFinalSurvivorPensionPR(
        id = id
    )
}
