package com.tamin.taminhamrah.model.contractFlow

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class SpcPremiumRateOptionPR(
    val code: String,
    val description: String,
    val insurancePercent: String?,
)
