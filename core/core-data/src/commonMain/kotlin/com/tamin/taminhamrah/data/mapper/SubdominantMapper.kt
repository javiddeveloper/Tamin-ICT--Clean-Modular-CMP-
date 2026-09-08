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
            id = item.id ?: relation?.id,
            firstName = personal?.firstName,
            lastName = personal?.lastName,
            fatherName = personal?.fatherName,
            nationalCode = personal?.nationalId,
            dateOfBirthTimestamp = personal?.dateOfBirthTimestamp,
            // Short relation label for chips (e.g. "همسر") comes from baseTendency.
            // relationDescription is the long coverage sentence and is kept on status.
            relationDescription = relationSub?.baseTendency?.tendencyDescription
                ?: relationSub?.relationDescription,
            status = relationSub?.relationDescription,
            insuranceId = relation?.insuranceId
        )
    },
    total = totalCount?.toString()
)
