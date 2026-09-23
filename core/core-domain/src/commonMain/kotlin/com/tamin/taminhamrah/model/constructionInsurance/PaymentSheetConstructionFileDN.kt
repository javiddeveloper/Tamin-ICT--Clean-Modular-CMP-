package com.tamin.taminhamrah.model.constructionInsurance

/** صدور و مدیریت برگه پرداخت — one payment sheet row already issued for a debit. */
data class PaymentSheetConstructionFileDN(
    val orderNumber: String?,
    val paymentCode: String?,
    val paymentSheetAmount: Long?,
    val status: String?,
    val paymentDate: String?,
    val buildingRequest: BuildingRequestSummaryDN?,
)

data class BuildingRequestSummaryDN(
    val debitNumber: String?,
    val fileNumber: Long?,
    val requestNumber: Long?,
    val requestDate: String?,
    val totalPayment: Long?,
    val paymentDeadLine: String?,
    val workshopInfo: WorkshopIdInfoDN?,
)
