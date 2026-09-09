package com.tamin.taminhamrah.model.inspection

data class SubmitInspectionRequestDN(
    val brchCode: String,
    val endDate: Long,
    val inspectionNumberOld: String,
    val insuranceId: String,
    val insuranceJob: String,
    val requestDescription: String,
    val startDate: Long,
    val workshopAddress: String,
    val workshopManager: String,
    val workshopName: String,
    val workshopNumber: String,
    val workshopTel: String
)

data class SubmitInspectionRequestResultDN(
    val id: Long?
)
