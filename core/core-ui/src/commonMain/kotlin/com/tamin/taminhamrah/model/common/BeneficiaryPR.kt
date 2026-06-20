package com.tamin.taminhamrah.model.common

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class BeneficiaryPR(
    val bankCode: String?,
    val bankName: String?,
)
