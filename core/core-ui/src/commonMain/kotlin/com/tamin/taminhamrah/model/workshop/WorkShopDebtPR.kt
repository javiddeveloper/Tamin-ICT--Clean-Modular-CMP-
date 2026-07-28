package com.tamin.taminhamrah.model.workshop

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable


@Immutable
@Serializable
data class WorkShopDebtPR(
    val rowNum: Long?,
    val debitNumber: String?,
    val debitAmount: Long?,
    val debitRemain: Long?,
    val debitCreateReasonDesc: String?,
    val debitStatDesc: String?
)
