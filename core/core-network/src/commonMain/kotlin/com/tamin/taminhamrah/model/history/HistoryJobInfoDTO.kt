package com.tamin.taminhamrah.model.history

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HistoryJobInfoDTO(
    @SerialName("list") val list: List<HistoryJobInfoItemDTO>?,
    @SerialName("total") val total: Int?
)

@Serializable
data class HistoryJobInfoItemDTO(
    @SerialName("risuid") val risuid: String?,
    @SerialName("rwshName") val rwshName: String?,
    @SerialName("brhcode") val brhcode: String?,
    @SerialName("id") val id: Int?,
    @SerialName("jobDesc") val jobDesc: String?,
    @SerialName("startDate") val startDate: String?,
    @SerialName("rwshId") val rwshId: String?
)
