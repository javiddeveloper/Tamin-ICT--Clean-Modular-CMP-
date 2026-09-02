package com.tamin.taminhamrah.model.workshop

import androidx.compose.runtime.Immutable

@Immutable
data class LegalRepresentativeWorkshopPR(
    val workshopId: String,
    val branchCode: String,
    val workshopName: String,
    val branchName: String?,
    val special: Boolean,
    val representativeCount: Int?,
)
