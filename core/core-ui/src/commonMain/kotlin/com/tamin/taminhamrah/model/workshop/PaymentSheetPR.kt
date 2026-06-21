package com.tamin.taminhamrah.model.workshop

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable


@Immutable
@Serializable
data class PaymentSheetPR(
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


@Immutable
@Serializable
data class PaymentSheetListPR(
    val list: List<PaymentSheetPR>?,
    val total: Int?
)
