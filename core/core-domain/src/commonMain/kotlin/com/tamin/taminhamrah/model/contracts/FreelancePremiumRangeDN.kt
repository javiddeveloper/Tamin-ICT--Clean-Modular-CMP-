package com.tamin.taminhamrah.model.contracts

data class FreelancePremiumRangeDN(
    val paymentTabayi: Long,
    val lowPremium: Long,
    val history: Int,
    val highPremium: Long,
)

data class FreelancePremiumRangeParams(
    val treatmentSupportCode: String,
    val spcRateCode: String,
    val insuranceId: String,
) {
    val id: String
        get() = "$treatmentSupportCode/$spcRateCode/$insuranceId"
}
