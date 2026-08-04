package com.tamin.taminhamrah.mapper.addDependent

import com.tamin.taminhamrah.model.addDependent.BranchDN
import com.tamin.taminhamrah.model.addDependent.BranchPR
import com.tamin.taminhamrah.model.addDependent.DependentInfoDN
import com.tamin.taminhamrah.model.addDependent.DependentInfoPR
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipDN
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipPR
import com.tamin.taminhamrah.model.addDependent.GeneralResultDN
import com.tamin.taminhamrah.model.addDependent.GeneralResultPR
import com.tamin.taminhamrah.model.addDependent.InquiryEducationCodePR
import com.tamin.taminhamrah.model.addDependent.RegistryDataDN
import com.tamin.taminhamrah.model.addDependent.RegistryDataPR
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDN
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentPR
import com.tamin.taminhamrah.model.addDependent.RequestFileDN
import com.tamin.taminhamrah.model.addDependent.RequestFilePR
import com.tamin.taminhamrah.model.addDependent.UploadImageDN
import com.tamin.taminhamrah.model.addDependent.UploadImagePR

fun DependentInfoDN.toPresentation(): DependentInfoPR = DependentInfoPR(
    id = id,
    nationalId = nationalId,
    fullName = fullName,
    relationshipDesc = relationshipDesc,
    isInsuranceActive = isInsuranceActive,
    birthDatePersian = birthDatePersian
)

fun BranchDN.toPresentation(): BranchPR = BranchPR(
    branchCode = branchCode,
    branchName = branchName,
    workshopCode = workshopCode,
    workshopName = workshopName
)

fun RegistryDataDN.toPresentation(): RegistryDataPR = RegistryDataPR(
    age = age,
    birthDate = birthDate,
    fatherName = fatherName,
    firstName = firstName,
    lastName = lastName,
    fullName = "$firstName $lastName".trim(),
    nationalId = nationalId,
    gender = gender,
    registryConfirmState = registryConfirmState
)

fun FamilyRelationshipDN.toPresentation(): FamilyRelationshipPR = FamilyRelationshipPR(
    id = id,
    relationCode = relationCode,
    relationDesc = relationDesc,
    bailCode = bailCode
)

fun String.toEducationPresentation(): InquiryEducationCodePR = InquiryEducationCodePR(
    universityOrSchoolName = this
)

fun UploadImageDN.toPresentation(): UploadImagePR = UploadImagePR(
    guid = guid
)

fun GeneralResultDN.toPresentation(): GeneralResultPR = GeneralResultPR(
    isSuccess = isSuccess,
    message = message.orEmpty(),
    code = code ?: 0
)

fun RequestAddDependentPR.toDomain(): RequestAddDependentDN = RequestAddDependentDN(
    bailTypeCode = bailTypeCode,
    branchCode = branchCode,
    cityOfBirthId = cityOfBirthId,
    cityOfIssueId = cityOfIssueId,
    countryId = countryId,
    dateOfBirth = dateOfBirth,
    dependencyId = dependencyId,
    dependentTypeCode = dependentTypeCode,
    firstName = firstName,
    id = id,
    lastName = lastName,
    nation = nation,
    nationalId = nationalId,
    parentId = parentId,
    requestFileList = requestFileList?.map { it.toDomain() }
)

fun RequestFilePR.toDomain(): RequestFileDN = RequestFileDN(
    documentFileId = documentFileId,
    documentType = documentType,
    id = id,
    personal = personal
)
