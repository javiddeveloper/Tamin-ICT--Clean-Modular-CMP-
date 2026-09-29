package com.tamin.taminhamrah.model.constructionInsurance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstallmentDebitListDTO(
    @SerialName("workshopId") val workshopId: String? = null,
    @SerialName("debitNumber") val debitNumber: String? = null,
    @SerialName("debitStepDescription") val debitStepDescription: String? = null,
    @SerialName("debitStatusDescription") val debitStatusDescription: String? = null,
    @SerialName("debitStartDate") val debitStartDate: String? = null,
    @SerialName("debitEndDate") val debitEndDate: String? = null,
    @SerialName("remainingAmount") val remainingAmount: Long? = null,
)
