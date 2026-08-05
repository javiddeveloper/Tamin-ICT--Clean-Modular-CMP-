package com.tamin.taminhamrah.model.activeRelation

import androidx.compose.runtime.Immutable

@Immutable
data class ActiveRelationPR(
    val id: Int,
    val organizationName: String,
    val insuranceId: String,
    val relationStatus: String,
    val startDate: String,
    val endDate: String?,
    val isActive: Boolean,
    val isVerified: Boolean
)
