package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.data.local.entity.PersonalInfoEntity
import com.tamin.taminhamrah.model.personal.PersonalDTO
import com.tamin.taminhamrah.model.personal.PersonalInfoDTO
import com.tamin.taminhamrah.model.personal.PersonalDN
import com.tamin.taminhamrah.model.personal.PersonalInfoDN
import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.age.AgeDTO
import com.tamin.taminhamrah.model.personal.DisabilityDependentDN
import com.tamin.taminhamrah.model.personal.SubmitFinalSurvivorPensionDN
import com.tamin.taminhamrah.model.personal.disabilityRequest.DisabilityDependentDTO
import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorDN
import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorDTO
import com.tamin.taminhamrah.model.personal.survivorList.RequestModelDN
import com.tamin.taminhamrah.model.personal.survivorList.RequestModelDTO
import com.tamin.taminhamrah.model.personal.submitFinalSurvivorPension.SubmitFinalSurvivorPensionRequest

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

fun AgeDTO.toDomain(): AgeDN {
    return AgeDN(
        age = age,
        birthDate = birthDate
    )
}

fun DisabilityDependentDTO.toDomain(): DisabilityDependentDN {
    val personal = relationWithTamin?.personal
    val tendency = relationWithTamin?.tendencyInfo?.baseTendency
    return DisabilityDependentDN(
        firstName = personal?.firstName,
        lastName = personal?.lastName,
        nationalId = personal?.nationalId,
        dateOfBirth = personal?.dateOfBirth,
        fatherName = personal?.fatherName,
        genderDesc = personal?.gender?.genderDesc,
        relation = personal?.relation,
        tendencyDescription = tendency?.tendencyDescription
    )
}

fun ConfirmSurvivorDTO.toDomain(): ConfirmSurvivorDN {
    return ConfirmSurvivorDN(
        request = request?.toDomain()
    )
}

fun RequestModelDTO.toDomain(): RequestModelDN {
    return RequestModelDN(
        id = id
    )
}

fun SubmitFinalSurvivorPensionDN.toDTO(): SubmitFinalSurvivorPensionRequest {
    return SubmitFinalSurvivorPensionRequest(
        id = id
    )
}

fun PersonalInfoDN.toEntity(): PersonalInfoEntity {
    return PersonalInfoEntity(
        insuranceId = insuranceId ?: "",
        branch = branch,
        mobileNumber = mobileNumber,
        provinceName = provinceName,
        firstName = personal?.firstName,
        lastName = personal?.lastName,
        fatherName = personal?.fatherName,
        nationalId = personal?.nationalId,
        ssn = personal?.ssn,
        genderDesc = personal?.genderDesc,
        dateOfBirth = personal?.dateOfBirth
    )
}

fun PersonalInfoEntity.toDomain(): PersonalInfoDN {
    return PersonalInfoDN(
        insuranceId = insuranceId,
        branch = branch,
        mobileNumber = mobileNumber,
        provinceName = provinceName,
        personal = PersonalDN(
            firstName = firstName,
            lastName = lastName,
            fatherName = fatherName,
            nationalId = nationalId,
            ssn = ssn,
            genderDesc = genderDesc,
            dateOfBirth = dateOfBirth
        )
    )
}
