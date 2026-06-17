package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.data.local.entity.RequestEntity
import com.tamin.taminhamrah.model.request.RequestDN
import com.tamin.taminhamrah.model.request.RequestDTO
import com.tamin.taminhamrah.model.request.RequestStatusDN
import com.tamin.taminhamrah.model.request.RequestStatusDTO
import com.tamin.taminhamrah.model.request.RequestTypeDN
import com.tamin.taminhamrah.model.request.RequestTypeDTO

fun RequestDTO.toDomain(): RequestDN {
    return RequestDN(
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

internal fun RequestDTO.toEntity(): RequestEntity {
    return RequestEntity(
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

internal fun RequestEntity.toDomain(): RequestDN {
    return RequestDN(
        id = id,
        refCode = refCode,
        title = title,
        comment = comment,
        creationTime = creationTime,
        createByName = createByName,
        status = RequestStatusDN(
            requestCode = statusCode,
            requestDesc = statusDesc,
        ),
        requestType = RequestTypeDN(
            id = requestTypeId,
            title = requestTypeTitle,
            description = requestTypeDescription,
        ),
        referenceId = referenceId,
    )
}

internal fun RequestDN.toEntity(): RequestEntity {
    return RequestEntity(
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

fun RequestStatusDTO.toDomain(): RequestStatusDN {
    return RequestStatusDN(
        requestCode = requestCode,
        requestDesc = requestDesc,
    )
}

fun RequestTypeDTO.toDomain(): RequestTypeDN {
    return RequestTypeDN(
        id = id,
        title = title,
        description = description,
    )
}
