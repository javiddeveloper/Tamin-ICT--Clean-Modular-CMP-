package com.tamin.taminhamrah.model.workersPayment

/** One payable construction-worker premium/penalty item. */
data class WorkersPaymentInfoDN(
    val pay: Boolean,
    val payable: Boolean,
    val fines: Boolean,
    val year: String,
    val month: String,
    val days: String,
    val professional: String,          // job code
    val professionalTitle: String,
    val rate: String,
    val amount: Long,                  // premium
    val amountFines: Long?,            // parsed from the string wire field
    val fromDatePersian: String,
    val toDatePersian: String,
    val fromDateToDate: String,        // concatenated range, sent back in the pay request
    val monthTitle: String,
    val type: String,                 // e.g. "Premium"
    val salary: Long,                 // dastMoazd
    val payDay: Long,
    val paymentStatus: String,
    val payableDes: String,
    val fishStatus: String,
    val maharatStatus: String,
    val bazresiStatus: String,
    val kargarStatus: String,
) {
    /** Premium plus penalty — the value actually charged. */
    val totalPayable: Long get() = amount + (amountFines ?: 0L)
}

data class WorkersPaymentInfoListDN(
    val totalAmount: Long,
    val totalPenalty: Long,
    val totalPremium: Long,
    val total: Int,
    val list: List<WorkersPaymentInfoDN>,
)

/** Everything needed to POST `workers/payDebit`; [redirectUrl] goes on the `?type=` query, not the body. */
data class WorkersPayDebitParamsDN(
    val amount: Long,
    val dates: List<String>,
    val fromToDate: List<String>,
    val redirectUrl: String,
)

data class WorkersPayDebitResultDN(
    val paymentUrl: String?,   // wire list[0]
    val ticket: String?,       // wire list[1]
    val paymentInfo: String?,  // wire list[3]
)

/** Builds the single-item pay request, matching legacy `WorkersPaymentInfo.makePayDebitRequest()`. */
fun WorkersPaymentInfoDN.toPayDebitParams(redirectUrl: String): WorkersPayDebitParamsDN =
    WorkersPayDebitParamsDN(
        amount = totalPayable,
        dates = listOf(fromDatePersian),
        fromToDate = listOf(fromDateToDate),
        redirectUrl = redirectUrl,
    )
