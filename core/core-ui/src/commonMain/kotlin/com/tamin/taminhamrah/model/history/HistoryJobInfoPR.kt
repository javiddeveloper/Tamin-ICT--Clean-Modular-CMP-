package com.tamin.taminhamrah.model.history

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class HistoryJobInfoPR(
    val list: List<HistoryJobInfoItemPR>,
    val total: Int
)

@Immutable
@Serializable
data class HistoryJobInfoItemPR(
    val risuid: String,
    val rwshName: String,
    val brhcode: String,
    val id: Int,
    val jobDesc: String,
    val startDate: String,
    val rwshId: String
)
