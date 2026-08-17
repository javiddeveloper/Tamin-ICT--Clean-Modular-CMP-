package com.tamin.taminhamrah.model.history

data class HistoryJobInfoDN(
    val list: List<HistoryJobInfoItemDN>?,
    val total: Int?
)

data class HistoryJobInfoItemDN(
    val risuid: String?,
    val rwshName: String?,
    val brhcode: String?,
    val id: Int?,
    val jobDesc: String?,
    val startDate: String?,
    val rwshId: String?
)
