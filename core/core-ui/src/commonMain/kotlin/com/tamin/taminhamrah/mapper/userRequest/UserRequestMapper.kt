package com.tamin.taminhamrah.mapper.userRequest

import com.tamin.taminhamrah.model.request.UserRequestDN
import com.tamin.taminhamrah.model.userRequest.UserRequestPR

fun UserRequestDN.toPresentation(): UserRequestPR {
    return UserRequestPR(
        id = id,
        refCode = refCode.orEmpty(),
        title = title.orEmpty(),
        comment = comment.orEmpty(),
        creationTime = formatCreationTime(creationTime),
        createByName = createByName.orEmpty(),
        statusDesc = status?.requestDesc.orEmpty(),
        requestTypeTitle = requestType?.title.orEmpty(),
    )
}

fun List<UserRequestDN>.toPresentation(): List<UserRequestPR> = map { it.toPresentation() }

private fun formatCreationTime(creationTime: Long?): String {
    if (creationTime == null) return ""
    return creationTime.toString()
}
