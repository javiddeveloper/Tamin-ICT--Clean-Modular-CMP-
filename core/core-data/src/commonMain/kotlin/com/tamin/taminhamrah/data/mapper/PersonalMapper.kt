package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.data.local.entity.PersonalInfoEntity
import com.tamin.taminhamrah.model.personal.PersonalDTO
import com.tamin.taminhamrah.model.personal.PersonalInfoDTO
import com.tamin.taminhamrah.model.personal.PersonalDN
import com.tamin.taminhamrah.model.personal.PersonalInfoDN
import com.tamin.taminhamrah.model.personal.AgeDN
import com.tamin.taminhamrah.model.personal.DisabilityPersonalInfoDN
import com.tamin.taminhamrah.model.personal.DisabilityPersonalDN
import com.tamin.taminhamrah.model.personal.DisabilityWorkDN
import com.tamin.taminhamrah.model.personal.GirlSurvivorConditionDN
import com.tamin.taminhamrah.model.personal.age.AgeDTO
import com.tamin.taminhamrah.model.personal.DisabilityDependentDN
import com.tamin.taminhamrah.model.personal.SubmitFinalSurvivorPensionDN
import com.tamin.taminhamrah.model.personal.disabilityRequest.DisabilityDependentDTO
import com.tamin.taminhamrah.model.personal.disabilityRequest.disabilityRequestPersonal.DisabilityPersonalInfoDTO
import com.tamin.taminhamrah.model.personal.disabilityRequest.disabilityRequestPersonal.WorkDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDN
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorDN
import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorDTO
import com.tamin.taminhamrah.model.personal.survivorList.RequestModelDN
import com.tamin.taminhamrah.model.personal.survivorList.RequestModelDTO
import com.tamin.taminhamrah.model.personal.submitFinalSurvivorPension.SubmitFinalSurvivorPensionRequest
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.SaveSurvivorInfoDN
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.DependencyTypeDN
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.PensionDocDN
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.SaveSurvivorInfoRequest
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.DependencyTypeRequest
import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.PensionDocRequest
import com.tamin.taminhamrah.model.personal.disabilityRequest.disabilityRequestPersonal.PersonalDTO as DisabilityPersonalDTO
import kotlin.jvm.JvmName

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

@JvmName("toGirlSurvivorConditionDomain")
fun String?.toDomain(): GirlSurvivorConditionDN {
    return GirlSurvivorConditionDN(
        condition = this
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

fun DisabilityPersonalInfoDTO.toDomain(): DisabilityPersonalInfoDN {
    return DisabilityPersonalInfoDN(
        branch = branch,
        branchName = branchName,
        confirmed = confirmed,
        insuranceId = insuranceId,
        mobileNumber = mobileNumber,
        personal = personal?.toDomain(),
        provinceName = provinceName,
        work = work?.toDomain(),
        yearsAge = yearsAge,
        monthsAge = monthsAge,
        daysAge = daysAge,
        strAge = strAge
    )
}

fun DisabilityPersonalDTO.toDomain(): DisabilityPersonalDN {
    return DisabilityPersonalDN(
        firstName = firstName,
        lastName = lastName,
        nationalId = nationalId,
        fatherName = fatherName,
        idCardNumber = idCardNumber,
        cityOfIssue = cityOfIssue?.description,
        dateOfBirth = dateOfBirth,
        genderDesc = gender?.genderDesc
    )
}

fun WorkDTO.toDomain(): DisabilityWorkDN {
    return DisabilityWorkDN(
        jobDescription = job?.jobDescription,
        workshopId = workshopId
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

fun PdfDownloadDTO.toDomain(): PdfDownloadDN {
    return PdfDownloadDN(
        pdf = pdf?.toDomain()
    )
}

fun InputStreamDTO.toDomain(): InputStreamDN {
    return InputStreamDN(
        pdf = pdf
    )
}

fun SubmitFinalSurvivorPensionDN.toDTO(): SubmitFinalSurvivorPensionRequest {
    return SubmitFinalSurvivorPensionRequest(
        id = id
    )
}

fun SaveSurvivorInfoDN.toDTO(): SaveSurvivorInfoRequest {
    return SaveSurvivorInfoRequest(
        address = address,
        age = age,
        birthDate = birthDate,
        branchCode = branchCode,
        survivorInsuranceId = survivorInsuranceId,
        survivorNationalId = survivorNationalId,
        deathType = deathType,
        dependencyType = dependencyType?.toDTO(),
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
        pensionRequestDocList = pensionRequestDocList?.map { it.toDTO() },
        phoneNumber = phoneNumber,
        status = status
    )
}

fun DependencyTypeDN.toDTO(): DependencyTypeRequest {
    return DependencyTypeRequest(
        code = code
    )
}

fun PensionDocDN.toDTO(): PensionDocRequest {
    return PensionDocRequest(
        documentType = documentType,
        guid = guid
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
