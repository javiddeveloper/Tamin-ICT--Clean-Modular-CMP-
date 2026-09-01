package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * `workshop-services/workshop-debit/{workshopId}/{branchCode}` — استعلام بدهی کارگاه.
 *
 * The three amounts are strings on the wire and are *not* always numeric, so they stay strings
 * here; the mapper parses defensively rather than with `toLong()`, which is what threw in the old
 * app when the service answered with a message instead of a figure.
 */
@Serializable
data class WorkshopDebtInquiryDTO(
    @SerialName("status") val status: String? = null,
    @SerialName("workshopId") val workshopId: String? = null,
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("workshopName") val workshopName: String? = null,
    @SerialName("result") val result: String? = null,
    @SerialName("sDate") val date: String? = null,
    /** بدهی قطعی */
    @SerialName("amount1") val definitiveDebt: String? = null,
    /** قابل تقسیط */
    @SerialName("amount2") val divisibleDebt: String? = null,
    /** غیر قابل تقسیط */
    @SerialName("amount3") val indivisibleDebt: String? = null,
)
