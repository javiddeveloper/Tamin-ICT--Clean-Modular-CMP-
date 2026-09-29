package com.tamin.taminhamrah.model.constructionInsurance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstallmentConstructionListDTO(
    @SerialName("workshopId") val workshopId: String? = null,
    @SerialName("debitNumber") val debitNumber: String? = null,
    @SerialName("debitSubCode") val debitSubCode: String? = null,
    @SerialName("dtnAmount") val dtnAmount: Long? = null,
    @SerialName("lastPaymentSheetAmount") val lastPaymentSheetAmount: Long? = null,
    @SerialName("dtnExpireDate") val dtnExpireDate: String? = null,
    @SerialName("lastPaymentSheetDescription") val lastPaymentSheetDescription: String? = null,
    @SerialName("paymentDate") val paymentDate: String? = null,
)
