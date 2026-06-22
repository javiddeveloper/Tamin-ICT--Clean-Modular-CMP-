package com.tamin.taminhamrah.model.workshop

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable


@Immutable
@Serializable
data class WorkshopDebtInquiryPR(
    val status: String?,
    val workshopId: String?,
    val branchCode: String?,
    val workshopName: String?,
    val result: String?,
    val amount1: String?,
    val sDate: String?,
    val amount2: String?,
    val amount3: String?
)
