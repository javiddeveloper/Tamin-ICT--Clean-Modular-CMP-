package com.tamin.taminhamrah.model.workshop

import androidx.compose.runtime.Immutable

/** استعلام بدهی کارگاه — one record of five formatted lines. */
@Immutable
data class WorkshopDebtInquiryPR(
    val result: String = "",
    val date: String = "",
    val definitiveDebt: String = "",
    val divisibleDebt: String = "",
    val indivisibleDebt: String = "",
)
