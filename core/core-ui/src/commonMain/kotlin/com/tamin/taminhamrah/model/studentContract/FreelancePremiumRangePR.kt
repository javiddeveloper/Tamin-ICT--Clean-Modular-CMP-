package com.tamin.taminhamrah.model.studentContract

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class FreelancePremiumRangePR(
    val lowPremium: Long,
    val highPremium: Long,
    val paymentTabayi: Long,
    val history: Int,
)
