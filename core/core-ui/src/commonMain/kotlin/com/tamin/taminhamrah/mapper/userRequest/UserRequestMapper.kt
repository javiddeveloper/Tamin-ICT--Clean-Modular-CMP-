package com.tamin.taminhamrah.mapper.userRequest

import com.tamin.taminhamrah.model.userRequest.RequestErrorDN
import com.tamin.taminhamrah.model.userRequest.RequestErrorPR
import com.tamin.taminhamrah.model.userRequest.SmartGuideDN
import com.tamin.taminhamrah.model.userRequest.SmartGuidePR
import com.tamin.taminhamrah.model.userRequest.UserRequestDN
import com.tamin.taminhamrah.model.userRequest.UserRequestPR
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeDN
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
        statusCode = status?.requestCode ?: "",
        requestTypeId = requestType?.id ?: 0L,
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

fun RequestErrorDN.toErrorPresentation(): RequestErrorPR {
    return RequestErrorPR(
        id = id ?: 0L,
        errorMessage = errorMessage.orEmpty(),
        errorType = errorType.orEmpty(),
        errorStatus = errorStatus.orEmpty(),
        creationTime = creationTime?.toString().orEmpty(),
    )
}

fun List<RequestErrorDN>.toErrorPresentation(): List<RequestErrorPR> =
    map { it.toErrorPresentation() }

fun SmartGuideDN.toSmartGuidePresentation(): SmartGuidePR {
    return SmartGuidePR(
        id = id ?: 0L,
        question = question.orEmpty(),
        reply = reply.orEmpty(),
        requestCode = requestCode.orEmpty(),
        requestDesc = requestDesc.orEmpty(),
        isPublic = isPublic ?: true,
        title = title.orEmpty(),
        description = description.orEmpty(),
    )
}

fun List<SmartGuideDN>.toSmartGuidePresentation(): List<SmartGuidePR> =
    map { it.toSmartGuidePresentation() }

