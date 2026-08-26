package com.tamin.taminhamrah.model.historyObjection

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaveNotExistRequestDTO(
    @SerialName("branchCode") val branchCode: String,
    @SerialName("branchName") val branchName: String,
    @SerialName("cityCode") val cityCode: String,
    @SerialName("cityName") val cityName: String,
    @SerialName("endDate") val endDate: String,
    @SerialName("insuranceType") val insuranceType: String,
    @SerialName("provinceCode") val provinceCode: String,
    @SerialName("provinceName") val provinceName: String,
    @SerialName("rwshAddress") val rwshAddress: String,
    @SerialName("rwshManager") val rwshManager: String,
    @SerialName("rwshid") val rwshid: String,
    @SerialName("rwshname") val rwshname: String,
    @SerialName("startDate") val startDate: String,
    @SerialName("workDays") val workDays: String,
)
