package com.tamin.taminhamrah.model.constructionInsurance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BeneficiaryConstructionDTO(
    @SerialName("nationalCode") val nationalCode: String? = null,
    @SerialName("ownerType") val ownerType: String? = null,
    @SerialName("requestNumber") val requestNumber: Long? = null,
    @SerialName("fileNumber") val fileNumber: Long? = null,
    @SerialName("requestDate") val requestDate: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("mobile") val mobile: String? = null,
)
