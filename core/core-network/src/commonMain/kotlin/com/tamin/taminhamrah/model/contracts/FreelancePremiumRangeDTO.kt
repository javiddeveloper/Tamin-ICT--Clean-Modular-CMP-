package com.tamin.taminhamrah.model.contracts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FreelancePremiumRangeDTO(
    @SerialName("paymentTabayi") val paymentTabayi: Long?,
    @SerialName("lowPremium") val lowPremium: Long?,
    @SerialName("history") val history: Int?,
    @SerialName("highPremium") val highPremium: Long?,
)
