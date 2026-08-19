package com.tamin.taminhamrah.model.historyObjection

import androidx.compose.runtime.Immutable

@Immutable
data class NotExistRequestPR(
    val requestNumber: String,
    val branchName: String,
    val insuranceTypeDesc: String,
    val workshopName: String,
    val insuranceNumber: String,
    val workshopCode: String?,
    val startDateLabel: String,
    val endDateLabel: String,
)
