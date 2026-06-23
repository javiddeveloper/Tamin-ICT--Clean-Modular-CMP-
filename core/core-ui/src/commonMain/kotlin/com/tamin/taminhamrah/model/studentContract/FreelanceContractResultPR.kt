package com.tamin.taminhamrah.model.studentContract

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class FreelanceContractResultPR(
    val contractNumber: String,
    val contractDate: String,
)
