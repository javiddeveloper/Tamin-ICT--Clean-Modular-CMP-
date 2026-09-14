package com.tamin.taminhamrah.model.constructionInsurance

/** مدیریت پرداخت اقساط — one installment (debit) letter head for a workshop. */
data class InstallmentLetterPR(
    val workshopId: String? = null,
    val debitNumber: String? = null,
    val debitStepDescription: String? = null,
    val debitStatusDescription: String? = null,
    val debitStartDate: String? = null,
    val debitEndDate: String? = null,
    val remainingAmount: Long? = null,
    val debitNumberOld: String? = null,
)
