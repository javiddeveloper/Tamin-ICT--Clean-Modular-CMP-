package com.tamin.taminhamrah.model.inspection

data class InspectionPerformedDN(
    val activityDesc: String,
    val branchCode: String,
    val branchdesc: String,
    val inspectionDate: Long,
    val inspectionNo: String,
    val insuranceNo: String,
    val objectable: String,
    val relationType: String,
    val workshopName: String,
    val workshopNo: String,
    val nationalCode: String
)
