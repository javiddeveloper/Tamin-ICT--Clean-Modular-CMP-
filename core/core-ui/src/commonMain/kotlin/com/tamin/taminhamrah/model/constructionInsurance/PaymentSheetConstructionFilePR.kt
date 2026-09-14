package com.tamin.taminhamrah.model.constructionInsurance

/** صدور و مدیریت برگه پرداخت — one already-issued payment sheet row. */
data class PaymentSheetConstructionFilePR(
    val orderNumber: String? = null,
    val paymentCode: String? = null,
    val paymentSheetAmount: Long? = null,
    val status: String? = null,
    val paymentDate: String? = null,
    val buildingRequest: BuildingRequestSummaryPR? = null,
)

data class BuildingRequestSummaryPR(
    val debitNumber: String? = null,
    val fileNumber: Long? = null,
    val requestNumber: Long? = null,
    val requestDate: String? = null,
    val totalPayment: Long? = null,
    val paymentDeadLine: String? = null,
    val workshopInfo: WorkshopIdInfoPR? = null,
)
