package com.tamin.taminhamrah.data.mapper.user

import com.tamin.taminhamrah.model.user.UserProfileDto
import com.tamin.taminhamrah.model.user.UserProfileDN

fun UserProfileDto.toDomain() = UserProfileDN(
    entityId = entityId,
    login = login,
    firstName = firstName,
    lastName = lastName,
    email = email,
    nationalCode = nationalCode,
    mobile = mobile
)
