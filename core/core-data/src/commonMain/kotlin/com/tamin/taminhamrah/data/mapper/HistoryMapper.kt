package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.DastmozdInfoDTO
import com.tamin.taminhamrah.model.history.DastmozdInfoItemDN
import com.tamin.taminhamrah.model.history.DastmozdInfoItemDTO
import com.tamin.taminhamrah.model.history.TalfighInfoItemDTO
import com.tamin.taminhamrah.model.history.TalfighInfoDTO
import com.tamin.taminhamrah.model.history.TalfighInfoItemDN
import com.tamin.taminhamrah.model.history.TalfighInfoDN

fun TalfighInfoItemDTO.toDomain(): TalfighInfoItemDN {
    return TalfighInfoItemDN(
        hisMonth8 = hisMonth8,
        hisMonth9 = hisMonth9,
        hisMonth6 = hisMonth6,
        hisMonth7 = hisMonth7,
        hisMonth1 = hisMonth1,
        hisMonth4 = hisMonth4,
        hisMonth5 = hisMonth5,
        hisMonth2 = hisMonth2,
        hisMonth3 = hisMonth3,
        hisMonth10 = hisMonth10,
        risuid = risuid,
        hisMonth11 = hisMonth11,
        hisMonth12 = hisMonth12,
        historyYears = historyYears,
        historyMonths = historyMonths,
        sumYear = sumYear,
        historyDays = historyDays,
        sumHistoryYears = sumHistoryYears,
        id = id,
        hisYear = hisYear
    )
}

fun TalfighInfoDTO.toDomain(): TalfighInfoDN {
    return TalfighInfoDN(
        list = list?.map { it.toDomain() },
        total = total
    )
}

fun DastmozdInfoItemDTO.toDomain(): DastmozdInfoItemDN {
    return DastmozdInfoItemDN(
        hismon1 = hismon1,
        hismon2 = hismon2,
        hismon3 = hismon3,
        hismon4 = hismon4,
        hismon5 = hismon5,
        hismon6 = hismon6,
        hismon7 = hismon7,
        hismon8 = hismon8,
        hismon9 = hismon9,
        hismon10 = hismon10,
        hismon11 = hismon11,
        hismon12 = hismon12,
        hiswage1 = hiswage1,
        hiswage2 = hiswage2,
        hiswage3 = hiswage3,
        hiswage4 = hiswage4,
        hiswage5 = hiswage5,
        hiswage6 = hiswage6,
        hiswage7 = hiswage7,
        hiswage8 = hiswage8,
        hiswage9 = hiswage9,
        hiswage10 = hiswage10,
        hiswage11 = hiswage11,
        hiswage12 = hiswage12,
        hisyear = hisyear,
        id = id,
        risufname = risufname,
        risubirthdate = risubirthdate,
        risuidserial2 = risuidserial2,
        risuidserial1 = risuidserial1,
        rwshname = rwshname,
        expcitycode = expcitycode,
        brhcode = brhcode,
        risuidno = risuidno,
        risudname = risudname,
        risuid = risuid,
        risulname = risulname,
        risunatcode = risunatcode,
        brhname = brhname,
        historytypedesc = historytypedesc,
        rwshid = rwshid
    )
}

fun DastmozdInfoDTO.toDomain(): DastmozdInfoDN {
    return DastmozdInfoDN(
        list = list?.map { it.toDomain() },
        total = total
    )
}
