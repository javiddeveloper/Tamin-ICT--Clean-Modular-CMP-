package com.tamin.taminhamrah.mapper.activeRelation

import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationPR

fun ActiveRelationDN.toUiModel(): ActiveRelationPR {
    return ActiveRelationPR(
        id = id ?: workshopName?.hashCode()?:0,
        organizationName = organizationName ?: "—",
        insuranceId = insuranceId ?: "—",
        branchCode = organizationId ?: "",
        relationStatus = relationDescription ?: "—",
        startDate = startDate ?: "—",
        endDate = endDate,
        isActive = relationDescription != null,
        isVerified = relationDescription != null
    )
}

fun List<ActiveRelationDN>.toUiModelList(): List<ActiveRelationPR> {
    return this.map { it.toUiModel() }
}
