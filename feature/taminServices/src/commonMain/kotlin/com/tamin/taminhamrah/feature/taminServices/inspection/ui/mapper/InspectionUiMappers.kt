package com.tamin.taminhamrah.feature.taminServices.inspection.ui.mapper

import com.tamin.taminhamrah.model.inspection.BranchDN
import com.tamin.taminhamrah.model.inspection.InspectionPerformedDN
import com.tamin.taminhamrah.model.inspection.JobDN
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.model.BranchPR
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.model.InspectionPerformedPR
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.model.JobPR

fun InspectionPerformedDN.toPR() = InspectionPerformedPR(
    activityDesc = activityDesc,
    branchCode = branchCode,
    branchdesc = branchdesc,
    inspectionDate = inspectionDate,
    inspectionNo = inspectionNo,
    insuranceNo = insuranceNo,
    objectable = objectable,
    relationType = relationType,
    workshopName = workshopName,
    workshopNo = workshopNo,
    nationalCode = nationalCode
)

fun BranchDN.toPR() = BranchPR(
    operation = operation,
    code = code,
    name = name,
    minCode = minCode,
    maxCode = maxCode,
    type = type,
    branchAddress = branchAddress,
    cityCode = cityCode,
    status = status
)

fun JobDN.toPR() = JobPR(
    operation = operation,
    jobCode = jobCode,
    jobDescription = jobDescription,
    status = status,
    statusDate = statusDate
)
