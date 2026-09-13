package com.tamin.taminhamrah.model.workersPayment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * One row of `GET workers/payment-info` -> `data.list[]`.
 * Construction-worker premium/penalty item the insured can pay.
 */
@Serializable
data class WorkersPaymentInfoDTO(
    @SerialName("pay") val pay: Boolean? = null,
    @SerialName("payable") val payable: Boolean? = null,
    @SerialName("fines") val fines: Boolean? = null,
    @SerialName("year") val year: String? = null,
    @SerialName("month") val month: String? = null,
    @SerialName("days") val days: String? = null,
    @SerialName("professional") val professional: String? = null,
    @SerialName("professionalTitle") val professionalTitle: String? = null,
    @SerialName("rate") val rate: String? = null,
    @SerialName("amount") val amount: Long? = null,
    // String in the wire payload ("22111981") and often null.
    @SerialName("amountFines") val amountFines: String? = null,
    @SerialName("fromDate") val fromDate: Long? = null,
    @SerialName("toDate") val toDate: Long? = null,
    @SerialName("fromDatePersian") val fromDatePersian: String? = null,
    @SerialName("toDatePersian") val toDatePersian: String? = null,
    @SerialName("intDate") val intDate: Int? = null,
    @SerialName("paymentDate") val paymentDate: String? = null,
    @SerialName("monthTitle") val monthTitle: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("dastMoazd") val salary: Long? = null,
    @SerialName("payDay") val payDay: Long? = null,
    @SerialName("paymentStatus") val paymentStatus: String? = null,
    @SerialName("fromDateToDate") val fromDateToDate: String? = null,
    @SerialName("payableDes") val payableDes: String? = null,
    @SerialName("fishStatus") val fishStatus: String? = null,
    @SerialName("maharatStatus") val maharatStatus: String? = null,
    @SerialName("bazresiStatus") val bazresiStatus: String? = null,
    @SerialName("kargarStatus") val kargarStatus: String? = null,
)

/**
 * `data` envelope of `GET workers/payment-info`. The standard [com.tamin.taminhamrah.model.utils.ListData]
 * only carries `total` + `list`; this endpoint also returns three aggregate totals.
 */
@Serializable
data class WorkersPaymentInfoDataDTO(
    @SerialName("totalAmount") val totalAmount: Long? = null,
    @SerialName("totalPenalty") val totalPenalty: Long? = null,
    @SerialName("totalPremium") val totalPremium: Long? = null,
    @SerialName("total") val total: Int = 0,
    @SerialName("list") val list: List<WorkersPaymentInfoDTO>? = null,
)

/** Body of `POST workers/payDebit`. */
@Serializable
data class WorkersPayDebitRequestDTO(
    @SerialName("amount") val amount: Long,
    @SerialName("dates") val dates: List<String?> = emptyList(),
    @SerialName("fromToDate") val fromToDate: List<String?> = emptyList(),
)

/**
 * `data` of `POST workers/payDebit`. `list` is a positional string array:
 * `[0]` = payment page URL, `[1]` = ticket, `[3]` = encrypted paymentInfo.
 */
@Serializable
data class WorkersPayDebitDTO(
    @SerialName("total") val total: Int? = null,
    @SerialName("list") val list: List<String> = emptyList(),
)
