package com.tamin.taminhamrah.model.pension

import androidx.compose.runtime.Immutable

@Immutable
data class EdictInfoPR(
    val pensionerId: String,
    val nationalCode: String,
    val firstName: String,
    val lastName: String,
    val fatherName: String,
    val birthDate: String,
    val idNumber: String,
    val gender: String,
    val insuranceType: String,
    val pensionStartDate: String,
    val originalHistoryYear: String,
    val originalHistoryMonth: String,
    val originalHistoryDay: String,
    val additionalYear: String,
    val additionalMonth: String,
    val additionalDay: String,
    val basisImplementation: String,
    val pensionBeforeIncrease: String,
    val pensionAfterIncrease: String,
    val edictDescription: String,
    val id: String,
    val firstStageTotalPensionAndProportional: String,
    val totalPensionBeforeIncrease: String,
    val firstStageTotalProportional: String,
    val totalAmount: String,
    val payableMonthly: String,
    val lettersPayableMonthly: String,
)
