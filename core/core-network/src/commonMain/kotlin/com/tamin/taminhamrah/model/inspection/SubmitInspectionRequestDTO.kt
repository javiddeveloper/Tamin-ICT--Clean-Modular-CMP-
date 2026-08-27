package com.tamin.taminhamrah.model.inspection

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubmitInspectionRequestDTO(
    @SerialName("brchCode") val brchCode: String? = null,
    @SerialName("endDate") val endDate: Long? = null,
    @SerialName("inspectionNumberOld") val inspectionNumberOld: String? = null,
    @SerialName("insuranceId") val insuranceId: String? = null,
    @SerialName("insuranceJob") val insuranceJob: String? = null,
    @SerialName("requestDescription") val requestDescription: String? = null,
    @SerialName("startDate") val startDate: Long? = null,
    @SerialName("workshopAddress") val workshopAddress: String? = null,
    @SerialName("workshopManager") val workshopManager: String? = null,
    @SerialName("workshopName") val workshopName: String? = null,
    @SerialName("workshopNumber") val workshopNumber: String? = null,
    @SerialName("workshopTel") val workshopTel: String? = null
)

@Serializable
data class SubmitInspectionRequestModelDTO(
    @SerialName("request") val request: RequestSubmitInspectionDTO? = null
)

@Serializable
data class RequestSubmitInspectionDTO(
    @SerialName("id") val id: Long? = null
)
