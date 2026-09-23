package com.tamin.taminhamrah.feature.objectionInsurance.ui.preview

import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.ObjectionYearCardPR
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.ObjectionYearSubtitle
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.YearCompletionStatus
import com.tamin.taminhamrah.model.objectionInsurance.ObjectionInsuranceHistoryPR
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/** Sample records for Compose previews only. */
internal fun previewHistoryRecord(
    year: String,
    workshopName: String = "کارگاه نمونه",
    branchName: String = "شعبه مرکزی",
    historyTypeName: String = "اجباری",
    monthDays: List<Int> = List(12) { 31 },
): ObjectionInsuranceHistoryPR = ObjectionInsuranceHistoryPR(
    year = year,
    workshopName = workshopName,
    branchName = branchName,
    historyTypeName = historyTypeName,
    workshopId = "1234567890",
    oldMonth1 = monthDays[0].toString(),
    oldMonth2 = monthDays[1].toString(),
    oldMonth3 = monthDays[2].toString(),
    oldMonth4 = monthDays[3].toString(),
    oldMonth5 = monthDays[4].toString(),
    oldMonth6 = monthDays[5].toString(),
    oldMonth7 = monthDays[6].toString(),
    oldMonth8 = monthDays[7].toString(),
    oldMonth9 = monthDays[8].toString(),
    oldMonth10 = monthDays[9].toString(),
    oldMonth11 = monthDays[10].toString(),
    oldMonth12 = monthDays[11].toString(),
)

internal fun previewLoadedRecords() = persistentListOf(
    previewHistoryRecord(year = "1404"),
    previewHistoryRecord(year = "1403", monthDays = List(12) { 20 }),
    previewHistoryRecord(year = "1389", workshopName = "کارگاه الف"),
    previewHistoryRecord(year = "1389", workshopName = "کارگاه ب", monthDays = List(12) { 15 }),
)

internal fun previewYearCards() = persistentListOf(
    ObjectionYearCardPR(
        year = "1404",
        subtitle = ObjectionYearSubtitle.TotalDays(372),
        days = "372",
        fraction = 1f,
        status = YearCompletionStatus.Complete,
        recordIndices = listOf(0).toImmutableList(),
    ),
    ObjectionYearCardPR(
        year = "1403",
        subtitle = ObjectionYearSubtitle.TotalDays(240),
        days = "240",
        fraction = 0.66f,
        status = YearCompletionStatus.Deficit,
        recordIndices = listOf(1).toImmutableList(),
    ),
    ObjectionYearCardPR(
        year = "1389",
        subtitle = ObjectionYearSubtitle.Edited,
        days = "200",
        fraction = 0.55f,
        status = YearCompletionStatus.Edited,
        recordIndices = listOf(2, 3).toImmutableList(),
    ),
)
