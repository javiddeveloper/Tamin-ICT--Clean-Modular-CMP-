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
import com.tamin.taminhamrah.model.personal.DocumentFileDTO
import com.tamin.taminhamrah.model.personal.InsuredDocDN
import com.tamin.taminhamrah.model.personal.InsuredDocDTO
import com.tamin.taminhamrah.model.personal.NewInsuredSummaryDN
import com.tamin.taminhamrah.model.personal.NewInsuredSummaryDTO
import com.tamin.taminhamrah.model.personal.SubmitFinalSurvivorPensionDN
import com.tamin.taminhamrah.model.personal.disabilityRequest.DisabilityDependentDTO
import com.tamin.taminhamrah.model.personal.disabilityRequest.disabilityRequestPersonal.DisabilityPersonalInfoDTO
import com.tamin.taminhamrah.model.personal.disabilityRequest.disabilityRequestPersonal.WorkDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDN
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDTO
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentDN
import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentDTO
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
import com.tamin.taminhamrah.model.personal.girlSurvivor.ConfirmGirlSurvivorDN
import com.tamin.taminhamrah.model.personal.girlSurvivor.ConfirmGirlSurvivorRequestDTO
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
    val firstContact = contacts?.firstOrNull()
    return PersonalDN(
        firstName = firstName,
        lastName = lastName,
        fatherName = fatherName,
        nationalId = nationalId,
        ssn = ssn,
        genderDesc = gender?.genderDesc,
        genderCode = gender?.genderCode,
        dateOfBirth = dateOfBirth,
        idCardNumber = idCardNumber,
        dateOfDead = dateOfDead,
        contactAddress = firstContact?.address,
        contactZipCode = firstContact?.zipCode,
        contactPhoneNumber = firstContact?.phoneNumber,
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
        genderCode = personal?.gender?.genderCode,
        genderDesc = personal?.gender?.genderDesc,
        tendencyCode = tendency?.tendencyCode,
        tendencyDescription = tendency?.tendencyDescription
    )
}

fun SurvivorDependentDTO.toDomain(): SurvivorDependentDN {
    val relationWithTamin = relationWithTamin
    val personal = relationWithTamin?.personal
    val tendency = relationWithTamin?.relationWithTamin?.baseTendency
    return SurvivorDependentDN(
        firstName = personal?.firstName,
        lastName = personal?.lastName,
        nationalId = personal?.nationalId,
        fatherName = personal?.fatherName,
        idCardNumber = personal?.idCardNumber,
        cityOfIssue = personal?.cityOfIssue,
        genderCode = personal?.gender?.genderCode,
        genderDesc = personal?.gender?.genderDesc,
        dateOfBirth = personal?.dateOfBirth,
        insuranceId = relationWithTamin?.insuranceId,
        tendencyCode = tendency?.tendencyCode
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

fun ConfirmGirlSurvivorDN.toDTO(): ConfirmGirlSurvivorRequestDTO {
    return ConfirmGirlSurvivorRequestDTO(
        address = address,
        age = age,
        birthDate = birthDate,
        childInsuranceId = childInsuranceId,
        childNationalId = childNationalId,
        deathDate = deathDate,
        deathType = deathType,
        dependencyType = dependencyType?.toDTO(),
        firstName = firstName,
        gender = gender,
        idNumber = idNumber,
        insuranceNumber = insuranceNumber,
        lastName = lastName,
        mobileNumber = mobileNumber,
        nationalCode = nationalCode,
        pensionId = pensionId,
        phoneNumber = phoneNumber,
        status = status,
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

fun NewInsuredSummaryDTO.toDomain(): NewInsuredSummaryDN {
    return NewInsuredSummaryDN(
        refCode = refCode ?: "",
        nationalId = nationalId ?: "",
        firstName = firstName ?: "",
        lastName = lastName ?: "",
        relationDescription = relationDescription ?: "",
        jobDescription = jobDescription ?: ""
    )
}

fun InsuredDocDN.toDTO(): InsuredDocDTO {
    return InsuredDocDTO(
        documentType = documentType,
        id = id,
        documentFile = DocumentFileDTO(
            createdBy = documentFile.createdBy,
            id = documentFile.id,
            image = documentFile.image
        )
    )
}
