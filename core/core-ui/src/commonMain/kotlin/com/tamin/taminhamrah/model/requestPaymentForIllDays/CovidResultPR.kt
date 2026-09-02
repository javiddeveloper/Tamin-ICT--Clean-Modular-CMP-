package com.tamin.taminhamrah.model.requestPaymentForIllDays

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class CovidResultPR(
    val startDateTimeStamp: String = "",
    val endDateTimeStamp: String = "",
    val timestamps: List<String> = emptyList(),
)
