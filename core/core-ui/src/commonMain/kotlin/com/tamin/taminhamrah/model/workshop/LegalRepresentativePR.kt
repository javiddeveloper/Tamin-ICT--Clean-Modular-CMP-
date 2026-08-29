package com.tamin.taminhamrah.model.workshop

import androidx.compose.runtime.Immutable

@Immutable
data class LegalRepresentativePR(
    val stakeId: Long,
    val nationalId: String,
    val mobile: String?,
    val fullName: String?,
    val hasElectronicNotification: Boolean,
    val hasInternetList: Boolean,
    val hasInsuredRegistration: Boolean,
    val startDateLabel: String,
    val workshopId: String,
    val branchCode: String,
    val special: Boolean,
)
