package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.subDominant.SubDominantResponseDTO
import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.model.subdominant.SubdominantItemDN

internal fun SubDominantResponseDTO.toDomain(): SubdominantDN = SubdominantDN(
    list = list?.map { item ->
        val relation = item.relationWithTamin
        val personal = relation?.personal
        val relationSub = relation?.relationWithTamin
        SubdominantItemDN(
            id = null,
            firstName = personal?.firstName,
            lastName = personal?.lastName,
            fatherName = null,
            nationalCode = personal?.nationalId,
            relationDescription = null,
            status = null,
            insuranceId = null
        )
    },
    total = null
)
