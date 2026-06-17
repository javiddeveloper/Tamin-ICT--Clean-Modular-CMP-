package com.tamin.taminhamrah.mapper.request

import com.tamin.taminhamrah.model.request.RequestDN
import com.tamin.taminhamrah.model.request.RequestPR

fun RequestDN.toPresentation(): RequestPR {
    return RequestPR(
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

fun List<RequestDN>.toPresentation(): List<RequestPR> = map { it.toPresentation() }

private fun formatCreationTime(creationTime: Long?): String {
    if (creationTime == null) return ""
    return creationTime.toString()
}
