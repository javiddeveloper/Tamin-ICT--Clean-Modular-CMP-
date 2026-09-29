package com.tamin.taminhamrah.model.constructionInsurance

/** مدیریت پرداخت اقساط — one installment (debit) letter head for a workshop. */
data class InstallmentLetterDN(
    val workshopId: String?,
    val debitNumber: String?,
    val debitStepDescription: String?,
    val debitStatusDescription: String?,
    val debitStartDate: String?,
    val debitEndDate: String?,
    val remainingAmount: Long?,
    val debitNumberOld: String?,
)
