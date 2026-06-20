package com.tamin.taminhamrah.model.history

data class TalfighInfoDN(
    val list: List<TalfighInfoItemDN>?,
    val total: Int?
)

data class TalfighInfoItemDN(
    val months: List<String?>,
    val risuid: String?,
    val historyYears: Int?,
    val historyMonths: Int?,
    val sumYear: Int?,
    val historyDays: Int?,
    val sumHistoryYears: Int?,
    val id: Int?,
    val hisYear: String?
)
