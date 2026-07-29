package com.tamin.taminhamrah.data.remote.models.services.insuredInspectionPerformed.submit

data class SubmitResponse(
    val brchCode: String? = null,
    val endDate: Long? = null,
    val inspectionNumberOld: String? = null,
    val insuranceId: String? = null,
    val insuranceJob: String? = null,
    val requestDescription: String? = null,
    val startDate: Long? = null,
    val workshopAddress: String? = null,
    val workshopManager: String? = null,
    val workshopName: String? = null,
    val workshopNumber: String? = null,
    val workshopTel: String? = null
)