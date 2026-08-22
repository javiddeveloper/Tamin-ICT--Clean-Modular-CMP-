package com.tamin.taminhamrah.model.occurrence

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InsuredRelationDTO(
    @SerialName("isuType") val insuranceTypeCode: String?,
    @SerialName("isuTypeDesc") val insuranceType: String?,
    @SerialName("brhCode") val branchCode: String?,
    @SerialName("brhName") val branchName: String?,
)
