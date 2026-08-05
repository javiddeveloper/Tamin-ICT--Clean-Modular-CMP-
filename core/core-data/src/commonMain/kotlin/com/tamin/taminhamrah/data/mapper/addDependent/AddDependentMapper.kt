package com.tamin.taminhamrah.data.mapper.addDependent

import com.tamin.taminhamrah.model.addDependent.BailTypeDto
import com.tamin.taminhamrah.model.addDependent.BranchDto
import com.tamin.taminhamrah.model.addDependent.BranchDN
import com.tamin.taminhamrah.model.addDependent.DependencyDto
import com.tamin.taminhamrah.model.addDependent.DependentTypeDto
import com.tamin.taminhamrah.model.addDependent.DocumentFileDto
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipDto
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipProxyDto
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipDN
import com.tamin.taminhamrah.model.addDependent.GeneralResponseDto
import com.tamin.taminhamrah.model.addDependent.GeneralResultDN
import com.tamin.taminhamrah.model.addDependent.ParentIdDto
import com.tamin.taminhamrah.model.addDependent.RegistryDataDto
import com.tamin.taminhamrah.model.addDependent.RegistryDataDN
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDto
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDN
import com.tamin.taminhamrah.model.addDependent.RequestFileDto
import com.tamin.taminhamrah.model.addDependent.RequestFileDN
import com.tamin.taminhamrah.model.addDependent.UploadImageResponseDto
import com.tamin.taminhamrah.model.addDependent.UploadImageDN
import com.tamin.taminhamrah.model.addDependent.DependentInfoDto
import com.tamin.taminhamrah.model.addDependent.DependentInfoDN

internal fun DependentInfoDto.toDomain(): DependentInfoDN = DependentInfoDN(
    id = id.orEmpty(),
    nationalId = nationalId.orEmpty(),
    fullName = fullName.orEmpty(),
    relationshipDesc = relationshipDesc.orEmpty(),
    isInsuranceActive = isInsuranceActive ?: true,
    birthDatePersian = birthDatePersian.orEmpty()
)

internal fun BranchDto.toDomain(): BranchDN = BranchDN(
    branchCode = branchCode,
    branchName = branchName,
    workshopCode = workshopCode,
    workshopName = workshopName
)

internal fun RegistryDataDto.toDomain(): RegistryDataDN = RegistryDataDN(
    age = age,
    birthDate = birthDate,
    fatherName = fatherName,
    firstName = firstName,
    lastName = lastName,
    nationalId = nationalId,
    gender = gender,
    registryConfirmState = registryConfirmState
)

internal fun FamilyRelationshipDto.toDomain(): FamilyRelationshipDN = FamilyRelationshipDN(
    id = id,
    relationCode = relationCode,
    relationDesc = relationDesc,
    bailCode = bailCode
)

internal fun FamilyRelationshipProxyDto.toDomain(): FamilyRelationshipDN = FamilyRelationshipDN(
    id = id,
    relationCode = relationCode,
    relationDesc = relationDesc,
    bailCode = bailCode
)

internal fun UploadImageResponseDto.toDomain(): UploadImageDN = UploadImageDN(
    guid = guid
)

internal fun GeneralResponseDto.toDomain(): GeneralResultDN = GeneralResultDN(
    isSuccess = isSuccess,
    message = message,
    code = code
)

internal fun RequestAddDependentDN.toDto(): RequestAddDependentDto = RequestAddDependentDto(
    bailType = bailTypeCode?.let { BailTypeDto(code = it) },
    branchCode = branchCode,
    cityOfBirthId = cityOfBirthId,
    cityOfIssueId = cityOfIssueId,
    countryId = countryId,
    dateOfBirth = dateOfBirth,
    dependency = dependencyId?.let { DependencyDto(id = it) },
    dependentType = dependentTypeCode?.let { DependentTypeDto(code = it) },
    firstName = firstName,
    id = id,
    lastName = lastName,
    nation = nation,
    nationalId = nationalId,
    parentId = parentId?.let { ParentIdDto(id = it) } ?: ParentIdDto(),
    requestFileList = requestFileList?.map { it.toDto() }
)

internal fun RequestFileDN.toDto(): RequestFileDto = RequestFileDto(
    documentFile = documentFileId?.let { DocumentFileDto(id = it) },
    documentType = documentType,
    id = id,
    personal = personal
)
