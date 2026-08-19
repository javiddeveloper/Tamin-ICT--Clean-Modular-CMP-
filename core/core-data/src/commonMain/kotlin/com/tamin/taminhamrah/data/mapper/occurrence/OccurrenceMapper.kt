package com.tamin.taminhamrah.data.mapper.occurrence

import com.tamin.taminhamrah.model.occurrence.InsuredRelationDTO
import com.tamin.taminhamrah.model.occurrence.InsuredRelationDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceDocTypeDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceDocTypeDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceDocumentDTO
import com.tamin.taminhamrah.model.occurrence.OccurrencePersonalInfoDTO
import com.tamin.taminhamrah.model.occurrence.OccurrencePersonalInfoDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceRequestDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceResponseDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceResultDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceSubmitRequestDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceUploadedDocDN
import com.tamin.taminhamrah.model.occurrence.WorkshopItemDTO
import com.tamin.taminhamrah.model.occurrence.WorkshopItemDN

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
    trackingCode = trackingCode.orEmpty(),
)

fun OccurrenceSubmitRequestDN.toDTO(): OccurrenceRequestDTO = OccurrenceRequestDTO(
    birthDate = birthDate,
    workshopId = workshopId,
    employerName = employerName,
    employerPhone = employerPhone,
    workshopAddress = workshopAddress,
    workshopPostalCode = workshopPostalCode,
    workshopPhone = workshopPhone,
    employmentDate = employmentDate,
    maritalStatus = maritalStatus,
    jobTitle = jobTitle,
    workLocation = workLocation,
    transportation = transportation,
    workStartTime = workStartTime,
    workEndTime = workEndTime,
    homeAddress = homeAddress,
    homePhone = homePhone,
    homePostalCode = homePostalCode,
    accidentDate = accidentDate,
    accidentTime = accidentTime,
    accidentOutcomeId = accidentOutcomeId,
    exactLocation = exactLocation,
    description = description,
    documents = documents.map { it.toDTO() },
)

fun OccurrenceUploadedDocDN.toDTO(): OccurrenceDocumentDTO = OccurrenceDocumentDTO(
    docTypeId = typeId,
    guid = guid,
)
