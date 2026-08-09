package com.tamin.taminhamrah.model.activeRelation

data class ActiveRelationDN(
    val id: Int?,
    val firstName: String?,
    val lastName: String?,
    val nationalId: String?,
    val insuranceId: String?,
    val birthDate: String?,
    val relationWithTaminId: Int?,
    val startDate: String?,
    val endDate: String?,
    val workshopId: String?,
    val workshopName: String?,
    val organizationId: String?,
    val organizationName: String?,
    val relationDescription: String?,
)
