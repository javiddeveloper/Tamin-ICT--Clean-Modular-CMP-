package com.tamin.taminhamrah.model.studentContract

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class SpcPremiumRateOptionPR(
    val code: String,
    val description: String,
    val insurancePercent: String?,
)
