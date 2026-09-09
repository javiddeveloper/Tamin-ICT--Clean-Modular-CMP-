package com.tamin.taminhamrah.model.historyObjection

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotExistRequestDTO(
    @SerialName("reqno") val requestNumber: String? = null,
    @SerialName("reqtype") val requestType: String? = null,
    @SerialName("rowi") val rowIndex: String? = null,
    @SerialName("risuid") val insuredId: String? = null,
    @SerialName("rwshid") val workshopId: String? = null,
    @SerialName("rwshname") val workshopName: String? = null,
    @SerialName("workDays") val workDays: String? = null,
    @SerialName("startDate") val startDate: Long? = null,
    @SerialName("endDate") val endDate: Long? = null,
    @SerialName("provinceCode") val provinceCode: String? = null,
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("insuranceType") val insuranceType: String? = null,
    @SerialName("cityCode") val cityCode: String? = null,
    @SerialName("rwshManager") val workshopManager: String? = null,
    @SerialName("rwshAddress") val workshopAddress: String? = null,
    @SerialName("branchName") val branchName: String? = null,
    @SerialName("insuranceTypeDesc") val insuranceTypeDesc: String? = null,
    @SerialName("provinceName") val provinceName: String? = null,
    @SerialName("cityName") val cityName: String? = null,
    @SerialName("confirmed") val confirmed: Boolean? = null,
    @SerialName("userDesc") val userDesc: String? = null,
)
