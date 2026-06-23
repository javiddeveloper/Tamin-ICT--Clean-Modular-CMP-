package com.tamin.taminhamrah.model.workshop

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class WorkshopDebitPR(
    val debitNumber: String?,
    val debitCreateReasonCode: String?,
    val debitCreateReasonDesc: String?,
    val debitStartDate: String?,
    val debitEndDate: String?,
    val debitAmount: Long?,
    val debitRemain: Long?,
    val withoutPentaltyAmount: Long?,
    val penaltyList: Long?,
    val penaltyPay: Long?,
    val sum: Long?,
    val nimOshr: Long?,
    val debitStepDesc: String?,
    val debitStatDesc: String?,
    val debitStepCode: String?,
    val debitStatCode: String?,
    val mastCustomerTypeCode: String?,
    val mastCustomerCode: String?,
    val peymanSequence: String?,
    val debitCreateDate: String?,
    val cludatCode: String?,
    val cludatDesc: String?,
    val nimOshrKol: Long?,
    val docDate: String?,
    val stepCat: String?,
)

@Immutable
@Serializable
data class WorkshopDebitListPR(
    val list: List<WorkshopDebitPR>?,
    val total: Int
)
