package com.tamin.taminhamrah.ui.home.services.employer.debt

data class InstallmentRequestModel(
    val debitInstallmentDetail: MutableSet<Debt>,
    val firstInstallmentPer: Int,
    val installmentNumber: Int,
    val workshopId: String,
    val branchCode: String,
    val peymanSequence: String
)

data class Debt(val debitNumber: String)
