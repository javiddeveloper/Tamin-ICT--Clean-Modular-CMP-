package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.data.local.entity.UserRequestEntity
import com.tamin.taminhamrah.model.request.UserRequestDN
import com.tamin.taminhamrah.model.request.UserRequestDTO
import com.tamin.taminhamrah.model.request.UserRequestStatusDN
import com.tamin.taminhamrah.model.request.UserRequestStatusDTO
import com.tamin.taminhamrah.model.request.UserRequestTypeDN
import com.tamin.taminhamrah.model.request.UserRequestTypeDTO

fun UserRequestDTO.toDomain(): UserRequestDN {
    return UserRequestDN(
        id = id ?: 0L,
        refCode = refCode,
        title = title,
        comment = comment,
        creationTime = creationTime,
        createByName = createByName,
        status = status?.toDomain(),
        requestType = requestType?.toDomain(),
        referenceId = referenceId,
    )
}

internal fun UserRequestDTO.toEntity(): UserRequestEntity {
    return UserRequestEntity(
        id = id ?: 0L,
        refCode = refCode,
        title = title,
        comment = comment,
        creationTime = creationTime,
        createByName = createByName,
        statusCode = status?.requestCode,
        statusDesc = status?.requestDesc,
        requestTypeId = requestType?.id,
        requestTypeTitle = requestType?.title,
        requestTypeDescription = requestType?.description,
        referenceId = referenceId,
    )
}

internal fun UserRequestEntity.toDomain(): UserRequestDN {
    return UserRequestDN(
        id = id,
        refCode = refCode,
        title = title,
        comment = comment,
        creationTime = creationTime,
        createByName = createByName,
        status = UserRequestStatusDN(
            requestCode = statusCode,
            requestDesc = statusDesc,
        ),
        requestType = UserRequestTypeDN(
            id = requestTypeId,
            title = requestTypeTitle,
            description = requestTypeDescription,
        ),
        referenceId = referenceId,
    )
}

internal fun UserRequestDN.toEntity(): UserRequestEntity {
    return UserRequestEntity(
        id = id,
        refCode = refCode,
        title = title,
        comment = comment,
        creationTime = creationTime,
        createByName = createByName,
        statusCode = status?.requestCode,
        statusDesc = status?.requestDesc,
        requestTypeId = requestType?.id,
        requestTypeTitle = requestType?.title,
        requestTypeDescription = requestType?.description,
        referenceId = referenceId,
    )
}

fun UserRequestStatusDTO.toDomain(): UserRequestStatusDN {
    return UserRequestStatusDN(
        requestCode = requestCode,
        requestDesc = requestDesc,
    )
}

fun UserRequestTypeDTO.toDomain(): UserRequestTypeDN {
    return UserRequestTypeDN(
        id = id,
        title = title,
        description = description,
    )
}
