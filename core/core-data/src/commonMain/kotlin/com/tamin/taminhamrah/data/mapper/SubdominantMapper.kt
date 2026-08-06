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
            dateOfBirth = personal?.dateOfBirth,
            // "status" and "relationDescription" both come from the same relationDescription
            // field on the payload (e.g. "تبعي - تحت پوشش بيمه شده اصلي - کفالت زن توسط شوهر-
            // عقد دائم") — there's a separate numeric "status" code ("1") alongside it, but the
            // description is what's actually meant to display as either. Revisit if a
            // machine-readable status code turns out to be needed instead.
            relationDescription = relationSub?.relationDescription,
            status = relationSub?.relationDescription,
            insuranceId = relation?.insuranceId
        )
    },
    total = totalCount?.toString()
)
