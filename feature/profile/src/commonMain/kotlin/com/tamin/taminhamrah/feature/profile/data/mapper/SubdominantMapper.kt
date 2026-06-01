package com.tamin.taminhamrah.feature.profile.data.mapper

import com.tamin.taminhamrah.model.dependent.SubdominantResponse
import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.model.subdominant.SubdominantItemDN

internal fun SubdominantResponse.toDomain(): SubdominantDN = SubdominantDN(
    list = data?.list?.map { item ->
        val relation = item.relationWithTamin
        val personal = relation?.personal
        val relationSub = relation?.relationWithTamin
        SubdominantItemDN(
            id = item.id,
            firstName = personal?.firstName,
            lastName = personal?.lastName,
            fatherName = personal?.fatherName,
            nationalCode = personal?.nationalId,
            relationDescription = relationSub?.relationDescription,
            status = relationSub?.status,
            insuranceId = relation?.insuranceId
        )
    },
    total = data?.total
)
