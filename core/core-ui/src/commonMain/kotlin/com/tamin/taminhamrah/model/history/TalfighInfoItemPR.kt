package com.tamin.taminhamrah.model.history

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable


@Immutable
@Serializable
data class TalfighInfoPR(
    val list: List<TalfighInfoItemPR>,
    val total: Int
)


@Immutable
@Serializable
data class TalfighInfoItemPR(
    val months: List<String>,
    val risuid: String,
    val historyYears: Int,
    val historyMonths: Int,
    val sumYear: Int,
    val historyDays: Int,
    val sumHistoryYears: Int,
    val id: Int,
    val hisYear: String
)
