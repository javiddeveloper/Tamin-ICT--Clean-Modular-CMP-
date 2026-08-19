package com.tamin.taminhamrah.model.occurrence

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InsuredRelationDTO(
    @SerialName("isuType") val insuranceTypeCode: String? = null,
    @SerialName("isuTypeDesc") val insuranceType: String? = null,
    @SerialName("brhCode") val branchCode: String? = null,
    @SerialName("brhName") val branchName: String? = null,
)
