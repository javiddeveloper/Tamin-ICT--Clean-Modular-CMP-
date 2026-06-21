package com.tamin.taminhamrah.mapper.userRequest

import com.tamin.taminhamrah.model.request.UserRequestDN
import com.tamin.taminhamrah.model.request.UserRequestTypeDN
import com.tamin.taminhamrah.model.userRequest.UserRequestPR
import com.tamin.taminhamrah.model.userRequest.UserRequestTypePR

fun UserRequestDN.toPresentation(): UserRequestPR {
    return UserRequestPR(
        id = id,
        refCode = refCode ?: "",
        title = title ?: "",
        comment = comment ?: "",
        creationTime = creationTime?.toString() ?: "0",
        createByName = createByName ?: "",
        statusDesc = status?.requestDesc ?: "",
        requestTypeTitle = requestType?.title ?: "",
    )
}

fun List<UserRequestDN>.toPresentation(): List<UserRequestPR> = map { it.toPresentation() }

fun UserRequestTypeDN.toTypePresentation(): UserRequestTypePR {
    return UserRequestTypePR(
        id = id ?: 0L,
        title = title.orEmpty(),
        description = description.orEmpty(),
    )
}

fun List<UserRequestTypeDN>.toTypePresentation(): List<UserRequestTypePR> =
    map { it.toTypePresentation() }
