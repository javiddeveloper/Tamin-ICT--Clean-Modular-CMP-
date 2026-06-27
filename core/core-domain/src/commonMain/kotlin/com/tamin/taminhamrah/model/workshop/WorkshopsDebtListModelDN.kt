package com.tamin.taminhamrah.model.workshop

data class WorkshopsDebtListModelDN(
    val indebtednessAmount: Int?,
    val insuranceAmount: Int?,
    val debitAmount: Int?,
    val debitCreateReasonCode: String?,
    val debitEndDate: String?,
    val debitNumber: String?,
    val debitRemain: Int?,
    val debitStartDate: String?,
    val status: String?
)

data class WorkshopsDebtListDN(
    val list: List<WorkshopsDebtListModelDN>?,
    val total: Int
)
