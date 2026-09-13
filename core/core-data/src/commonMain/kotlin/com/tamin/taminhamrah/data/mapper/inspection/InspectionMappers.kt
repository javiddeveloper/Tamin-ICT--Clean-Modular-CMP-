package com.tamin.taminhamrah.data.mapper.inspection

import com.tamin.taminhamrah.model.inspection.BranchDTO
import com.tamin.taminhamrah.model.inspection.BranchDN
import com.tamin.taminhamrah.model.inspection.InspectionPerformedDTO
import com.tamin.taminhamrah.model.inspection.InspectionPerformedDN
import com.tamin.taminhamrah.model.inspection.JobDTO
import com.tamin.taminhamrah.model.inspection.JobDN
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestDTO
import com.tamin.taminhamrah.model.inspection.SubmitInspectionRequestDN

fun InspectionPerformedDTO.toDN() = InspectionPerformedDN(
    activityDesc = activityDesc ?: "",
    branchCode = branchCode ?: "",
    branchdesc = branchdesc ?: "",
    inspectionDate = inspectionDate ?: 0L,
    inspectionNo = inspectionNo ?: "",
    insuranceNo = insuranceNo ?: "",
    objectable = objectable ?: "",
    relationType = relationType ?: "",
    workshopName = workshopName ?: "",
    workshopNo = workshopNo ?: "",
    nationalCode = nationalCode ?: ""
)

fun BranchDTO.toDN() = BranchDN(
    operation = operation ?: "",
    code = code ?: "",
    name = name ?: "",
    minCode = minCode ?: "",
    maxCode = maxCode ?: "",
    type = type ?: "",
    branchAddress = branchAddress ?: "",
    cityCode = cityCode ?: "",
    status = status ?: ""
)

fun JobDTO.toDN() = JobDN(
    operation = operation ?: "",
    jobCode = jobCode ?: "",
    jobDescription = jobDescription ?: "",
    status = status ?: "",
    statusDate = statusDate ?: ""
)

fun SubmitInspectionRequestDN.toDTO() = SubmitInspectionRequestDTO(
    brchCode = brchCode,
    endDate = endDate,
    inspectionNumberOld = inspectionNumberOld,
    insuranceId = insuranceId,
    insuranceJob = insuranceJob,
    requestDescription = requestDescription,
    startDate = startDate,
    workshopAddress = workshopAddress,
    workshopManager = workshopManager,
    workshopName = workshopName,
    workshopNumber = workshopNumber,
    workshopTel = workshopTel
)

