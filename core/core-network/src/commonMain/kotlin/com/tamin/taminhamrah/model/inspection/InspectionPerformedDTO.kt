package com.tamin.taminhamrah.model.inspection

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InspectionPerformedDTO(
    @SerialName("activityDesc") val activityDesc: String? = null,
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("branchdesc") val branchdesc: String? = null,
    @SerialName("inspectionDate") val inspectionDate: Long? = null,
    @SerialName("inspectionNo") val inspectionNo: String? = null,
    @SerialName("insuranceNo") val insuranceNo: String? = null,
    @SerialName("objectable") val objectable: String? = null,
    @SerialName("relationType") val relationType: String? = null,
    @SerialName("workshopName") val workshopName: String? = null,
    @SerialName("workshopNo") val workshopNo: String? = null,
    @SerialName("nationalCode") val nationalCode: String? = null
)
