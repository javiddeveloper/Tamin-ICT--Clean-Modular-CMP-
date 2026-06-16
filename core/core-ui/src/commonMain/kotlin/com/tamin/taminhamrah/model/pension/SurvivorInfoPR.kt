package com.tamin.taminhamrah.model.pension

import androidx.compose.runtime.Immutable

@Immutable
data class SurvivorInfoPR(
    val pensionAfterIncrease: String,
    val previousPension: String,
    val originalHistoryDay: String,
    val originalHistoryMonth: String,
    val originalHistoryYear: String,
    val leniencyYear: String,
    val leniencyMonth: String,
    val leniencyDay: String,
    val edictDescription: String,
    val id: String,
    val insuranceType: String,
    val lastName: String,
    val firstName: String,
    val firstStageTotalPensionAndProportional: String,
    val firstStageTotalProportional: String,
    val differenceProportionalityBasedHistory: String,
    val nationalCode: String,
    val pensionerId: String,
    val quota: String,
    val totalAmount: String,
)
