package com.tamin.taminhamrah.model.pension

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class EdictPensionerDTO (
    var lastName: String? = null,
    @SerialName("bazmandeValues")
    val survivorInfo: List<SurvivorInfo>? = null,
    val branchName: String? = null,
    val detail: List<EdictPensionerDetailDTO>? = null,
    @SerialName("hokmValue")
    val edictInfo: EdictInfoDTO? = null,
    val insuranceId: String? = null,
    val title: String? = null,
    var edictYear: String = "0",
    var edictMonth: String = "0"
)
