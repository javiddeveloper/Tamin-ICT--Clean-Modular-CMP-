package com.tamin.taminhamrah.model.workshop

/**
 * استعلام بدهی کارگاه — a single record, not a list.
 *
 * The amounts are parsed from strings that are not guaranteed numeric; a non-numeric answer
 * arrives here as null rather than throwing inside the mapper, and the screen shows the raw
 * [result] text the service sent instead.
 */
data class WorkshopDebtInquiryDN(
    val result: String = "",
    val date: String = "",
    val definitiveDebt: Long? = null,
    val divisibleDebt: Long? = null,
    val indivisibleDebt: Long? = null,
)
