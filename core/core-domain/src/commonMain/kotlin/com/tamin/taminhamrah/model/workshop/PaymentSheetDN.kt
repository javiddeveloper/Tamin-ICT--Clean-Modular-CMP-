package com.tamin.taminhamrah.model.workshop

data class PaymentSheetDN(
    val orderNo: String?,
    val orderRow: String?,
    val payId: String?,
    val mastCustomerCode: String?,
    val rcntrow: String?,
    val mastCustomerName: String?,
    val debitCreateReasonCode: String?,
    val debitCreateReasonDesc: String?,
    val debitNo: String?,
    val docDate: Long?,
    val paySeqAmount: Long?,
    val orpStatusCode: String?,
    val orpStatusDesc: String?,
    val cardDate: Long?,
    val payKindCode: String?,
    val payKindDesc: String?,
    val ouragGno: String?,
    val ouragSDate: String?
)

data class PaymentSheetListDN(
    val list: List<PaymentSheetDN>?,
    val total: Int?
)
