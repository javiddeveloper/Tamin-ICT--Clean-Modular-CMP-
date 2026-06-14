package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.user.EditMobileResponseDN
import com.tamin.taminhamrah.model.user.EditMobileDN
import com.tamin.taminhamrah.model.user.ExpirationTimeDN
import com.tamin.taminhamrah.model.user.ChronologyDN
import com.tamin.taminhamrah.model.user.EditMobileResponseDto
import com.tamin.taminhamrah.model.user.EditMobileDto
import com.tamin.taminhamrah.model.user.ExpirationTimeDto
import com.tamin.taminhamrah.model.user.ChronologyDto

fun EditMobileResponseDto.toDomain(): EditMobileResponseDN = EditMobileResponseDN(
    traceId = traceId,
    data = data?.toDomain()
)

fun EditMobileDto.toDomain(): EditMobileDN = EditMobileDN(
    hash = hash,
    expirationTime = expirationTime?.toDomain()
)

fun ExpirationTimeDto.toDomain(): ExpirationTimeDN = ExpirationTimeDN(
    year = year,
    month = month,
    nano = nano,
    monthValue = monthValue,
    dayOfMonth = dayOfMonth,
    hour = hour,
    minute = minute,
    second = second,
    dayOfWeek = dayOfWeek,
    dayOfYear = dayOfYear,
    chronology = chronology?.toDomain()
)

fun ChronologyDto.toDomain(): ChronologyDN = ChronologyDN(
    calendarType = calendarType,
    id = id
)

