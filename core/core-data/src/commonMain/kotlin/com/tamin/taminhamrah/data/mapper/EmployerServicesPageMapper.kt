package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.data.local.entity.WorkshopContractRowPageEntity
import com.tamin.taminhamrah.data.local.entity.WorkshopSummaryColumns
import com.tamin.taminhamrah.data.local.entity.WorkshopWithoutContractPageEntity
import com.tamin.taminhamrah.model.workshop.WorkshopContractRowDN
import com.tamin.taminhamrah.model.workshop.WorkshopSummaryDN
import com.tamin.taminhamrah.model.workshop.WorkshopWithoutContractDN


internal fun WorkshopWithoutContractDN.toPageEntity(listKey: String, position: Int) = WorkshopWithoutContractPageEntity(
    listKey = listKey,
    position = position,
    workshopId = workshopId,
    branchCode = branchCode,
    name = name,
    nationalId = nationalId,
    postalCode = postalCode,
    tel = tel,
    address = address,
    branchOfficeName = branchOfficeName,
    branchOfficeCode = branchOfficeCode,
)

internal fun WorkshopWithoutContractPageEntity.toDomain() = WorkshopWithoutContractDN(
    workshopId = workshopId,
    branchCode = branchCode,
    name = name,
    nationalId = nationalId,
    postalCode = postalCode,
    tel = tel,
    address = address,
    branchOfficeName = branchOfficeName,
    branchOfficeCode = branchOfficeCode,
)

internal fun WorkshopContractRowDN.toPageEntity(listKey: String, position: Int) = WorkshopContractRowPageEntity(
    listKey = listKey,
    position = position,
    contractRow = contractRow,
    startDate = startDate,
    endDate = endDate,
    firstName = firstName,
    lastName = lastName,
    mobile = mobile,
    email = email,
    nationalCode = nationalCode,
    tel = tel,
    postalCode = postalCode,
    workshop = workshop.toColumns(),
)

internal fun WorkshopContractRowPageEntity.toDomain() = WorkshopContractRowDN(
    contractRow = contractRow,
    startDate = startDate,
    endDate = endDate,
    firstName = firstName,
    lastName = lastName,
    mobile = mobile,
    email = email,
    nationalCode = nationalCode,
    tel = tel,
    postalCode = postalCode,
    workshop = workshop.toDomain(),
)

private fun WorkshopSummaryDN.toColumns() = WorkshopSummaryColumns(
    workshopId = workshopId,
    branchCode = branchCode,
    name = name,
    employerName = employerName,
    activityName = activityName,
    address = address,
    registerDate = registerDate,
    approveDate = approveDate,
    contractRow = contractRow,
    branchOfficeCode = branchOfficeCode,
    branchTitle = branchTitle,
    branchOfficeName = branchOfficeName,
    characterCode = characterCode,
    legalNationalId = legalNationalId,
    characterDescription = characterDescription,
    workshopTypeDescription = workshopTypeDescription,
    statusCode = statusCode,
    statusDescription = statusDescription,
)

private fun WorkshopSummaryColumns.toDomain() = WorkshopSummaryDN(
    workshopId = workshopId,
    branchCode = branchCode,
    name = name,
    employerName = employerName,
    activityName = activityName,
    address = address,
    registerDate = registerDate,
    approveDate = approveDate,
    contractRow = contractRow,
    branchOfficeCode = branchOfficeCode,
    branchTitle = branchTitle,
    branchOfficeName = branchOfficeName,
    characterCode = characterCode,
    legalNationalId = legalNationalId,
    characterDescription = characterDescription,
    workshopTypeDescription = workshopTypeDescription,
    statusCode = statusCode,
    statusDescription = statusDescription,
)
