package com.tamin.taminhamrah.model.contracts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FreelanceMakeContractRequestDTO(
    @SerialName("brchCodeNew") val brchCodeNew: String,
    @SerialName("cityCode") val cityCode: String,
    @SerialName("cntDrmn") val cntDrmn: String,
    @SerialName("cntFreeJobCode") val cntFreeJobCode: String,
    @SerialName("guid") val guid: String,
    @SerialName("guidName") val guidName: String,
    @SerialName("premiumRateCode") val premiumRateCode: String,
    @SerialName("provinceCode") val provinceCode: String,
)

@Serializable
data class FreelanceContractResultDTO(
    @SerialName("contractNumber") val contractNumber: Long?,
    @SerialName("contractDate") val contractDate: Long?,
)
