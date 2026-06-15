package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.user.EditMobileResponseDN
import com.tamin.taminhamrah.model.user.EditMobileDN
import com.tamin.taminhamrah.model.user.EditMobileResponseDto
import com.tamin.taminhamrah.model.user.EditMobileDto

fun EditMobileResponseDto.toDomain(): EditMobileResponseDN = EditMobileResponseDN(
    traceId = traceId,
    data = data?.toDomain()
)

fun EditMobileDto.toDomain(): EditMobileDN = EditMobileDN(
    hash = hash,
    expirationTime = expirationTime
)
