package com.tamin.taminhamrah.data.mapper.occurrence

import com.tamin.taminhamrah.model.occurrence.InsuredRelationDTO
import com.tamin.taminhamrah.model.occurrence.InsuredRelationDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceDocTypeDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceDocTypeDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceDocumentDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceDocumentFileDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceDocumentTypeRefDTO
import com.tamin.taminhamrah.model.occurrence.OccurrencePersonalInfoDTO
import com.tamin.taminhamrah.model.occurrence.OccurrencePersonalInfoDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceRequestDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceResponseDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceResultDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceSubmitRequestDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceUploadedDocDN
import com.tamin.taminhamrah.model.occurrence.WorkshopItemDTO
import com.tamin.taminhamrah.model.occurrence.WorkshopItemDN
import com.tamin.taminhamrah.model.occurrence.WorkshopListItemDTO

fun WorkshopItemDTO.toDomain(): WorkshopItemDN = WorkshopItemDN(
    id = id.orEmpty(),
    workshopCode = workshopCode.orEmpty(),
    branchCode = branchCode.orEmpty(),
    name = name.orEmpty(),
    employerName = employerName.orEmpty(),
    employerPhone = employerPhone.orEmpty(),
    address = address.orEmpty(),
    postalCode = postalCode.orEmpty(),
    phone = phone.orEmpty(),
    nationality = nation?.nationDesc.orEmpty(),
    nationalityCode = nation?.nationCode.orEmpty(),
)

/** [getWorkshopSpec][com.tamin.taminhamrah.dataSource.occurrence.OccurrenceRemoteDataSource.getWorkshopSpec] fills in the rest once a workshop is picked. */
fun WorkshopListItemDTO.toDomain(): WorkshopItemDN = WorkshopItemDN(
    id = workshopCode.orEmpty(),
    workshopCode = workshopCode.orEmpty(),
    branchCode = branchCode.orEmpty(),
    name = name.orEmpty(),
    employerName = "",
    employerPhone = "",
    address = "",
    postalCode = "",
    phone = "",
    nationality = "",
    nationalityCode = "",
)

fun OccurrencePersonalInfoDTO.toDomain(): OccurrencePersonalInfoDN = OccurrencePersonalInfoDN(
    nationalCode = nationalCode.orEmpty(),
    firstName = firstName.orEmpty(),
    lastName = lastName.orEmpty(),
    fatherName = fatherName.orEmpty(),
    gender = gender.orEmpty(),
    birthDate = birthDate.orEmpty(),
    insuranceNumber = insuranceNumber.orEmpty(),
    branchCode = branchCode.orEmpty(),
    nationality = nationality.orEmpty(),
    insuranceType = insuranceType.orEmpty(),
)

fun InsuredRelationDTO.toDomain(): InsuredRelationDN = InsuredRelationDN(
    insuranceTypeCode = insuranceTypeCode.orEmpty(),
    insuranceType = insuranceType.orEmpty(),
    branchCode = branchCode.orEmpty(),
    branchName = branchName.orEmpty(),
)

fun OccurrenceDocTypeDTO.toDomain(): OccurrenceDocTypeDN = OccurrenceDocTypeDN(
    id = docTypeId?.toIntOrNull() ?: 0,
    title = docDesc.orEmpty(),
)

fun OccurrenceResponseDTO.toDomain(): OccurrenceResultDN = OccurrenceResultDN(
    trackingCode = reportRefrenceNumber.orEmpty(),
)

fun OccurrenceSubmitRequestDN.toDTO(): OccurrenceRequestDTO = OccurrenceRequestDTO(
    birthDate = birthDate.toString(),
    bossFullName = employerName,
    bossMobileNumber = employerPhone,
    branchCode = branchCode,
    branchName = branchName,
    employeeDate = employmentDate.toString(),
    gender = gender,
    insuranceID = insuranceNumber,
    isuTypeDesc = insuranceType,
    isuTypecode = insuranceTypeCode,
    jobDesc = jobTitle,
    marriageStatusCode = maritalStatus,
    nationCode = nationalityCode,
    occurrenceAddress = exactLocation,
    occurrenceDate = accidentDate.toString(),
    occurrenceDesc = description,
    occurrenceDocumentList = documents.map { it.toDTO() },
    occurrenceResult = accidentOutcomeId,
    occurrenceTime = accidentTime,
    pFirstName = firstName,
    pLastName = lastName,
    pNationalCode = nationalCode,
    reportAddress = homeAddress,
    reportJobLocation = workLocation,
    reportPostalCode = homePostalCode,
    reportTelephone = homePhone,
    reporterType = reporterType,
    rwworkfinish = workEndTime,
    rwworkstart = workStartTime,
    vehicle = transportation,
    workshopAddress = workshopAddress,
    workshopBranchCode = workshopBranchCode,
    workshopCode = workshopId,
    workshopName = workshopName,
    workshopPostalCode = workshopPostalCode,
    workshopTelephone = workshopPhone,
)

fun OccurrenceUploadedDocDN.toDTO(): OccurrenceDocumentDTO = OccurrenceDocumentDTO(
    documentFile = OccurrenceDocumentFileDTO(id = guid),
    occurrenceDocumentType = OccurrenceDocumentTypeRefDTO(docTypeId = typeId.toString()),
)
