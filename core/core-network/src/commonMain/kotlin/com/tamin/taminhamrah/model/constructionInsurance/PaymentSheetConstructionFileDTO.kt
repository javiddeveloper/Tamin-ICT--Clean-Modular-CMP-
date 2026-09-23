package com.tamin.taminhamrah.model.constructionInsurance

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentSheetConstructionFileDTO(
    @SerialName("orderNumber") val orderNumber: String? = null,
    @SerialName("shenase") val paymentCode: String? = null,
    @SerialName("paymentSheetAmount") val paymentSheetAmount: Long? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("paymentDate") val paymentDate: String? = null,
    @SerialName("buildingRequest") val buildingRequest: BuildingRequestSummaryDTO? = null,
)

@Serializable
data class BuildingRequestSummaryDTO(
    @SerialName("debitNumber") val debitNumber: String? = null,
    @SerialName("fileNumber") val fileNumber: Long? = null,
    @SerialName("requestNumber") val requestNumber: Long? = null,
    @SerialName("requestDate") val requestDate: String? = null,
    @SerialName("totalPayment") val totalPayment: Long? = null,
    @SerialName("paymentDeadLine") val paymentDeadLine: String? = null,
    @SerialName("workshopId") val workshopInfo: WorkshopIdInfoDTO? = null,
)
