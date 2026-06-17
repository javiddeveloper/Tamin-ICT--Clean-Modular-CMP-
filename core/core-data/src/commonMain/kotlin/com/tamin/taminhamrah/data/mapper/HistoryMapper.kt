package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.history.TalfighInfoItemDTO
import com.tamin.taminhamrah.model.history.TalfighInfoDTO
import com.tamin.taminhamrah.model.history.TalfighInfoItemDN
import com.tamin.taminhamrah.model.history.TalfighInfoDN

fun TalfighInfoItemDTO.toDomain(): TalfighInfoItemDN {
    return TalfighInfoItemDN(
        hisMonth8 = hisMonth8 ?: "",
        hisMonth9 = hisMonth9 ?: "",
        hisMonth6 = hisMonth6 ?: "",
        hisMonth7 = hisMonth7 ?: "",
        hisMonth1 = hisMonth1 ?: "",
        hisMonth4 = hisMonth4 ?: "",
        hisMonth5 = hisMonth5 ?: "",
        hisMonth2 = hisMonth2 ?: "",
        hisMonth3 = hisMonth3 ?: "",
        hisMonth10 = hisMonth10 ?: "",
        risuid = risuid ?: "",
        hisMonth11 = hisMonth11 ?: "",
        hisMonth12 = hisMonth12 ?: "",
        historyYears = historyYears ?: 0,
        historyMonths = historyMonths ?: 0,
        sumYear = sumYear ?: 0,
        historyDays = historyDays ?: 0,
        sumHistoryYears = sumHistoryYears ?: 0,
        id = id ?: 0,
        hisYear = hisYear ?: ""
    )
}

fun TalfighInfoDTO.toDomain(): TalfighInfoDN {
    return TalfighInfoDN(
        list = list?.map { it.toDomain() } ?: emptyList(),
        total = total ?: 0
    )
}
