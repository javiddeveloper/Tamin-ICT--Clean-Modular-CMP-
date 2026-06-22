package com.tamin.taminhamrah.mapper.personal

import com.tamin.taminhamrah.model.personal.PersonalDN
import com.tamin.taminhamrah.model.personal.PersonalPR
import com.tamin.taminhamrah.model.personal.PersonalInfoDN
import com.tamin.taminhamrah.model.personal.PersonalInfoPR
import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.AgePR
import com.tamin.taminhamrah.model.personal.DisabilityDependentDN
import com.tamin.taminhamrah.model.personal.DisabilityDependentPR
import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorDN
import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorPR
import com.tamin.taminhamrah.model.personal.survivorList.RequestModelDN
import com.tamin.taminhamrah.model.personal.survivorList.RequestModelPR
import com.tamin.taminhamrah.model.personal.SubmitFinalSurvivorPensionDN
import com.tamin.taminhamrah.model.personal.SubmitFinalSurvivorPensionPR
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.SaveSurvivorInfoDN
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.SaveSurvivorInfoPR
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.DependencyTypeDN
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.DependencyTypePR
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.PensionDocDN
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.PensionDocPR
import kotlin.jvm.JvmName

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

@JvmName("disabilityDependentToPresentation")
fun List<DisabilityDependentDN>.toPresentation(): List<DisabilityDependentPR> {
    return map { it.toPresentation() }
}

fun ConfirmSurvivorDN.toPresentation(): ConfirmSurvivorPR {
    return ConfirmSurvivorPR(
        request = request?.toPresentation()
    )
}

fun RequestModelDN.toPresentation(): RequestModelPR {
    return RequestModelPR(
        id = id
    )
}

@JvmName("confirmSurvivorToPresentation")
fun List<ConfirmSurvivorDN>.toPresentation(): List<ConfirmSurvivorPR> {
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

fun SaveSurvivorInfoPR.toDomain(): SaveSurvivorInfoDN {
    return SaveSurvivorInfoDN(
        address = address,
        age = age,
        birthDate = birthDate,
        branchCode = branchCode,
        survivorInsuranceId = survivorInsuranceId,
        survivorNationalId = survivorNationalId,
        deathType = deathType,
        dependencyType = dependencyType?.toDomain(),
        fatherName = fatherName,
        firstName = firstName,
        gender = gender,
        idCardNumber = idCardNumber,
        insuranceNumber = insuranceNumber,
        issuePlace = issuePlace,
        lastName = lastName,
        mobileNumber = mobileNumber,
        deceasedNationalId = deceasedNationalId,
        pensionId = pensionId,
        pensionRequestDocList = pensionRequestDocList?.map { it.toDomain() },
        phoneNumber = phoneNumber,
        status = status
    )
}

fun DependencyTypePR.toDomain(): DependencyTypeDN {
    return DependencyTypeDN(
        code = code
    )
}

fun PensionDocPR.toDomain(): PensionDocDN {
    return PensionDocDN(
        documentType = documentType,
        guid = guid
    )
}

fun SaveSurvivorInfoDN.toPresentation(): SaveSurvivorInfoPR {
    return SaveSurvivorInfoPR(
        address = address,
        age = age,
        birthDate = birthDate,
        branchCode = branchCode,
        survivorInsuranceId = survivorInsuranceId,
        survivorNationalId = survivorNationalId,
        deathType = deathType,
        dependencyType = dependencyType?.toPresentation(),
        fatherName = fatherName,
        firstName = firstName,
        gender = gender,
        idCardNumber = idCardNumber,
        insuranceNumber = insuranceNumber,
        issuePlace = issuePlace,
        lastName = lastName,
        mobileNumber = mobileNumber,
        deceasedNationalId = deceasedNationalId,
        pensionId = pensionId,
        pensionRequestDocList = pensionRequestDocList?.map { it.toPresentation() },
        phoneNumber = phoneNumber,
        status = status
    )
}

fun DependencyTypeDN.toPresentation(): DependencyTypePR {
    return DependencyTypePR(
        code = code
    )
}

fun PensionDocDN.toPresentation(): PensionDocPR {
    return PensionDocPR(
        documentType = documentType,
        guid = guid
    )
}
