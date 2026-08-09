package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDTO
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN

fun ActiveRelationDTO.toDomain(): ActiveRelationDN {
    return ActiveRelationDN(
        id = id,
        firstName = firstName,
        lastName = lastName,
        nationalId = nationalId,
        insuranceId = insuranceId,
        birthDate = birthDate,
        relationWithTaminId = relationWithTaminId,
        startDate = startDate,
        endDate = endDate,
        workshopId = workshopId,
        workshopName = workshopName,
        organizationId = organizationId,
        organizationName = organization?.organizationName,
        relationDescription = relationWithTamin?.relationDescription
    )
}
