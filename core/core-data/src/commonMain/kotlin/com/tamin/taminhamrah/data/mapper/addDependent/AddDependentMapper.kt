package com.tamin.taminhamrah.data.mapper.addDependent

import com.tamin.taminhamrah.model.addDependent.BailTypeDTO
import com.tamin.taminhamrah.model.addDependent.BranchDTO
import com.tamin.taminhamrah.model.addDependent.BranchDN
import com.tamin.taminhamrah.model.addDependent.DependencyDTO
import com.tamin.taminhamrah.model.addDependent.DependentTypeDTO
import com.tamin.taminhamrah.model.addDependent.DocumentFileDTO
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipDTO
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipProxyDTO
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipDN
import com.tamin.taminhamrah.model.addDependent.GeneralResponseDTO
import com.tamin.taminhamrah.model.addDependent.GeneralResultDN
import com.tamin.taminhamrah.model.addDependent.ParentIdDTO
import com.tamin.taminhamrah.model.addDependent.RegistryDataDTO
import com.tamin.taminhamrah.model.addDependent.RegistryDataDN
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDTO
import com.tamin.taminhamrah.model.addDependent.RequestAddDependentDN
import com.tamin.taminhamrah.model.addDependent.RequestFileDTO
import com.tamin.taminhamrah.model.addDependent.RequestFileDN
import com.tamin.taminhamrah.model.addDependent.UploadImageResponseDTO
import com.tamin.taminhamrah.model.addDependent.UploadImageDN
import com.tamin.taminhamrah.model.addDependent.DependentInfoDTO
import com.tamin.taminhamrah.model.addDependent.DependentInfoDN

fun DependentInfoDTO.toDomain(): DependentInfoDN = DependentInfoDN(
    id = id.orEmpty(),
    nationalId = nationalId.orEmpty(),
    fullName = fullName.orEmpty(),
    relationshipDesc = relationshipDesc.orEmpty(),
    isInsuranceActive = isInsuranceActive ?: true,
    birthDatePersian = birthDatePersian.orEmpty()
)

fun BranchDTO.toDomain(): BranchDN = BranchDN(
    branchCode = branchCode,
    branchName = branchName,
    workshopCode = workshopCode,
    workshopName = workshopName
)

fun RegistryDataDTO.toDomain(): RegistryDataDN = RegistryDataDN(
    age = age,
    birthDate = birthDate,
    fatherName = fatherName,
    firstName = firstName,
    lastName = lastName,
    nationalId = nationalId,
    gender = gender,
    registryConfirmState = registryConfirmState
)

fun FamilyRelationshipDTO.toDomain(): FamilyRelationshipDN = FamilyRelationshipDN(
    id = id,
    relationCode = relationCode,
    relationDesc = relationDesc,
    bailCode = bailCode
)

fun FamilyRelationshipProxyDTO.toDomain(): FamilyRelationshipDN = FamilyRelationshipDN(
    id = id,
    relationCode = relationCode,
    relationDesc = relationDesc,
    bailCode = bailCode
)

fun UploadImageResponseDTO.toDomain(): UploadImageDN = UploadImageDN(
    guid = guid
)

fun GeneralResponseDTO.toDomain(): GeneralResultDN = GeneralResultDN(
    isSuccess = isSuccess,
    message = message,
    code = code
)

fun RequestAddDependentDN.toDto(): RequestAddDependentDTO = RequestAddDependentDTO(
    bailType = bailTypeCode?.let { BailTypeDTO(code = it) },
    branchCode = branchCode,
    cityOfBirthId = cityOfBirthId,
    cityOfIssueId = cityOfIssueId,
    countryId = countryId,
    dateOfBirth = dateOfBirth,
    dependency = dependencyId?.let { DependencyDTO(id = it) },
    dependentType = dependentTypeCode?.let { DependentTypeDTO(code = it) },
    firstName = firstName,
    id = id,
    lastName = lastName,
    nation = nation,
    nationalId = nationalId,
    parentId = parentId?.let { ParentIdDTO(id = it) } ?: ParentIdDTO(),
    requestFileList = requestFileList?.map { it.toDto() }
)

fun RequestFileDN.toDto(): RequestFileDTO = RequestFileDTO(
    documentFile = documentFileId?.let { DocumentFileDTO(id = it) },
    documentType = documentType,
    id = id,
    personal = personal
)
