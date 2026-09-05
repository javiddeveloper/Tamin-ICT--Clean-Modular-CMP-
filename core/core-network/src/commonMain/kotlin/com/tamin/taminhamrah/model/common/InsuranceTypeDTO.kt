package com.tamin.taminhamrah.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InsuranceTypeDTO(
    @SerialName("insuranceTypeCode") val insuranceTypeCode: String?,
    @SerialName("insuranceTypeDesc") val insuranceTypeDesc: String?,
    @SerialName("financialCode") val financialCode: String?,
    @SerialName("telCode") val telCode: String?,
    @SerialName("status") val status: String?,
    @SerialName("statusDate") val statusDate: String?,
)
