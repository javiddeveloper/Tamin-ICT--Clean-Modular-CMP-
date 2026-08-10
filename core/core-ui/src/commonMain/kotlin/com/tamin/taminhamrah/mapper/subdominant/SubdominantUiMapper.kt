package com.tamin.taminhamrah.mapper.subdominant

import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.model.subdominant.SubdominantItemDN
import com.tamin.taminhamrah.model.subdominant.SubdominantItemPR
import com.tamin.taminhamrah.model.subdominant.SubdominantPR
import com.tamin.taminhamrah.util.PersianDateFormatter

fun SubdominantDN.toPresentation(): SubdominantPR = SubdominantPR(
    list = list?.map { it.toPresentation() }.orEmpty(),
    total = total.orEmpty()
)

fun SubdominantItemDN.toPresentation(): SubdominantItemPR = SubdominantItemPR(
    id = id ?: 0L,
    firstName = firstName.orEmpty(),
    lastName = lastName.orEmpty(),
    fullName = "${firstName.orEmpty()} ${lastName.orEmpty()}".trim(),
    fatherName = fatherName.orEmpty(),
    nationalCode = nationalCode.orEmpty(),
    birthDateJalali = PersianDateFormatter.formatTimestamp(dateOfBirthTimestamp),
    relationDescription = relationDescription.orEmpty(),
    status = status.orEmpty(),
    insuranceId = insuranceId.orEmpty()
)
