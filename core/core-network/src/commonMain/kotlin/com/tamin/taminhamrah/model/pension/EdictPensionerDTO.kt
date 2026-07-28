package com.tamin.taminhamrah.model.pension

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class EdictPensionerDTO (
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("bazmandeValues") val survivorInfo: List<SurvivorInfoDTO>? = null,
    @SerialName("branchName") val branchName: String? = null,
    @SerialName("detail") val detail: List<EdictPensionerDetailDTO>? = null,
    @SerialName("hokmValue") val edictInfo: EdictInfoDTO? = null,
    @SerialName("insuranceId") val insuranceId: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("edictYear") val edictYear: String? = null,
    @SerialName("edictMonth") val edictMonth: String? = null,
)
