package com.tamin.taminhamrah.model.pension.disabilityRequest.medicalCommission

import androidx.compose.runtime.Immutable

@Immutable
data class RegisteredMedicalCommissionPR(
    val demandInfoId: String,
    val demandSaveDate: Long?,
    val verdictDescription: String,
)
