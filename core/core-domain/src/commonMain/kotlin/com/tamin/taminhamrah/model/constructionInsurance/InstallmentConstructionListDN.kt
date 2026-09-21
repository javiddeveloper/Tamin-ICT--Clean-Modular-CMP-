package com.tamin.taminhamrah.model.constructionInsurance

/** مدیریت اقساط و برگ پرداخت — one individual installment under one debit letter. */
data class InstallmentConstructionListDN(
    val workshopId: String?,
    val debitNumber: String?,
    val debitSubCode: String?,
    val dtnAmount: Long?,
    val lastPaymentSheetAmount: Long?,
    val dtnExpireDate: String?,
    val lastPaymentSheetDescription: String?,
    val paymentDate: String?,
)
