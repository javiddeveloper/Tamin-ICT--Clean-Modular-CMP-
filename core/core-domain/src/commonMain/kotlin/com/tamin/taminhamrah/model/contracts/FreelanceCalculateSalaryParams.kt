package com.tamin.taminhamrah.model.contracts

data class FreelanceCalculateSalaryParams(
    val monthlyPremium: Long,
    val treatmentSupportCode: String,
    val spcRateCode: String,
)
