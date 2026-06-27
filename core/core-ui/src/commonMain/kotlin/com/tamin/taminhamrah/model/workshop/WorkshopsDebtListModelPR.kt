package com.tamin.taminhamrah.model.workshop

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable


@Immutable
@Serializable
data class WorkshopsDebtListModelPR(
    val debitNumber: String?,
    val debitAmount: Int?,
    val debitRemain: Int?,
    val status: String?,
    val debitCreateReasonCode: String?
)
