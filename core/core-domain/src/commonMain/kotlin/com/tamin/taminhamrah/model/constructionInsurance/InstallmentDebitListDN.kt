package com.tamin.taminhamrah.model.constructionInsurance

/** بدهی‌های تقسیط‌شده — one flat per-installment debit detail row for one debit letter. */
data class InstallmentDebitListDN(
    val workshopId: String?,
    val debitNumber: String?,
    val debitStepDescription: String?,
    val debitStatusDescription: String?,
    val debitStartDate: String?,
    val debitEndDate: String?,
    val remainingAmount: Long?,
)
