package com.tamin.taminhamrah.model.pension

import androidx.compose.runtime.Immutable

@Immutable
data class EdictPensionerPR(
    val lastName: String,
    val branchName: String,
    val insuranceId: String,
    val title: String,
    val edictYear: String,
    val edictMonth: String,
    val edictInfo: EdictInfoPR?,
    val survivorInfo: List<SurvivorInfoPR>,
    val detail: List<EdictPensionerDetailPR>,
)
