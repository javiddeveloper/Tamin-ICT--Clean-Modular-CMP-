package com.tamin.taminhamrah.mapper.historyObjection

import com.tamin.taminhamrah.model.historyObjection.NotExistRequestDN
import com.tamin.taminhamrah.model.historyObjection.NotExistRequestPR
import com.tamin.taminhamrah.util.PersianDateFormatter

fun NotExistRequestDN.toPresentation(): NotExistRequestPR = NotExistRequestPR(
    requestNumber = requestNumber ?: "-",
    branchName = branchName ?: "-",
    insuranceTypeDesc = insuranceTypeDesc ?: "-",
    workshopName = workshopName ?: "-",
    insuranceNumber = insuredId?.takeIf { it.isNotBlank() } ?: "0",
    workshopCode = workshopId?.takeIf { it.isNotBlank() },
    startDateLabel = PersianDateFormatter.formatTimestamp(startDate),
    endDateLabel = PersianDateFormatter.formatTimestamp(endDate),
)

fun List<NotExistRequestDN>.toPresentation(): List<NotExistRequestPR> = map { it.toPresentation() }
