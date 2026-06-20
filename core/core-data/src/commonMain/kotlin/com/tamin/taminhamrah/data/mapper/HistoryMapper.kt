package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.DastmozdInfoDTO
import com.tamin.taminhamrah.model.history.DastmozdInfoItemDN
import com.tamin.taminhamrah.model.history.DastmozdInfoItemDTO
import com.tamin.taminhamrah.model.history.TalfighInfoItemDTO
import com.tamin.taminhamrah.model.history.TalfighInfoDTO
import com.tamin.taminhamrah.model.history.TalfighInfoItemDN
import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.history.WageDetailDN

fun TalfighInfoItemDTO.toDomain(): TalfighInfoItemDN {
    return TalfighInfoItemDN(
        months = listOf(
            hisMonth1, hisMonth2, hisMonth3, hisMonth4,
            hisMonth5, hisMonth6, hisMonth7, hisMonth8,
            hisMonth9, hisMonth10, hisMonth11, hisMonth12
        ),
        risuid = risuid,
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
        wageDetails = listOf(
            WageDetailDN(hismon1, hiswage1),
            WageDetailDN(hismon2, hiswage2),
            WageDetailDN(hismon3, hiswage3),
            WageDetailDN(hismon4, hiswage4),
            WageDetailDN(hismon5, hiswage5),
            WageDetailDN(hismon6, hiswage6),
            WageDetailDN(hismon7, hiswage7),
            WageDetailDN(hismon8, hiswage8),
            WageDetailDN(hismon9, hiswage9),
            WageDetailDN(hismon10, hiswage10),
            WageDetailDN(hismon11, hiswage11),
            WageDetailDN(hismon12, hiswage12)
        ),
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
