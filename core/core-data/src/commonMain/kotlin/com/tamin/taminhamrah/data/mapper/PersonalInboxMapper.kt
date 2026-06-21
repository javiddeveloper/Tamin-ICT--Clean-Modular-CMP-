package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.data.local.entity.PersonalInboxItemEntity
import com.tamin.taminhamrah.data.local.entity.PersonalInboxSizeEntity
import com.tamin.taminhamrah.model.inbox.InboxPermissionDN
import com.tamin.taminhamrah.model.inbox.InboxTypeDN
import com.tamin.taminhamrah.model.inbox.InboxPermissionDTO
import com.tamin.taminhamrah.model.inbox.InboxTypeDTO
import com.tamin.taminhamrah.model.inbox.PersonalInboxItemDN
import com.tamin.taminhamrah.model.inbox.PersonalInboxItemDTO
import com.tamin.taminhamrah.model.inbox.PersonalInboxSizeDN
import com.tamin.taminhamrah.model.inbox.PersonalInboxSizeDTO
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

fun PersonalInboxItemDTO.toDomain(): PersonalInboxItemDN {
    return PersonalInboxItemDN(
        id = id ?: 0L,
        nationalCode = nationalCode,
        mobileNumber = mobileNumber,
        email = email,
        read = read,
        data = data?.toStoredString(),
        sentDate = sentDate,
        receiveDate = receiveDate,
        seenDate = seenDate,
        seen = seen,
        hasImage = hasImage,
        hasText = hasText,
        hasPdf = hasPdf,
        updateable = updateable,
        status = status,
        referenceId = referenceId?.toStoredString(),
        pdf = pdf?.toStoredString(),
        type = type?.toDomain(),
        subType = subType?.toDomain(),
        permission = permission?.toDomain(),
    )
}

internal fun PersonalInboxItemDTO.toEntity(): PersonalInboxItemEntity {
    return PersonalInboxItemDN(
        id = id ?: 0L,
        nationalCode = nationalCode,
        mobileNumber = mobileNumber,
        email = email,
        read = read,
        data = data?.toStoredString(),
        sentDate = sentDate,
        receiveDate = receiveDate,
        seenDate = seenDate,
        seen = seen,
        hasImage = hasImage,
        hasText = hasText,
        hasPdf = hasPdf,
        updateable = updateable,
        status = status,
        referenceId = referenceId?.toStoredString(),
        pdf = pdf?.toStoredString(),
        type = type?.toDomain(),
        subType = subType?.toDomain(),
        permission = permission?.toDomain(),
    ).toEntity()
}

internal fun PersonalInboxItemEntity.toDomain(): PersonalInboxItemDN {
    return PersonalInboxItemDN(
        id = id,
        nationalCode = nationalCode,
        mobileNumber = mobileNumber,
        email = email,
        read = read,
        data = data,
        sentDate = sentDate,
        receiveDate = receiveDate,
        seenDate = seenDate,
        seen = seen,
        hasImage = hasImage,
        hasText = hasText,
        hasPdf = hasPdf,
        updateable = updateable,
        status = status,
        referenceId = referenceId,
        pdf = pdf,
        type = InboxTypeDN(typeDesc = typeDesc, typeCode = typeCode),
        subType = InboxTypeDN(typeDesc = subTypeDesc, typeCode = subTypeCode),
        permission = if (permissionPassword != null || permissionDateFrom != null || permissionDateTo != null) {
            InboxPermissionDN(
                password = permissionPassword,
                dateFrom = permissionDateFrom,
                dateTo = permissionDateTo,
            )
        } else {
            null
        },
    )
}

internal fun PersonalInboxItemDN.toEntity(): PersonalInboxItemEntity {
    return PersonalInboxItemEntity(
        id = id,
        nationalCode = nationalCode,
        mobileNumber = mobileNumber,
        email = email,
        read = read,
        data = data,
        sentDate = sentDate,
        receiveDate = receiveDate,
        seenDate = seenDate,
        seen = seen,
        hasImage = hasImage,
        hasText = hasText,
        hasPdf = hasPdf,
        updateable = updateable,
        status = status,
        referenceId = referenceId,
        pdf = pdf,
        typeDesc = type?.typeDesc,
        typeCode = type?.typeCode,
        subTypeDesc = subType?.typeDesc,
        subTypeCode = subType?.typeCode,
        permissionPassword = permission?.password,
        permissionDateFrom = permission?.dateFrom,
        permissionDateTo = permission?.dateTo,
    )
}

fun PersonalInboxSizeDTO.toDomain(): PersonalInboxSizeDN {
    return PersonalInboxSizeDN(
        usage = usage,
        total = total,
    )
}

internal fun PersonalInboxSizeDTO.toEntity(): PersonalInboxSizeEntity {
    return PersonalInboxSizeEntity(
        usage = usage,
        total = total,
    )
}

internal fun PersonalInboxSizeEntity.toDomain(): PersonalInboxSizeDN {
    return PersonalInboxSizeDN(
        usage = usage,
        total = total,
    )
}

private fun InboxTypeDTO.toDomain(): InboxTypeDN {
    return InboxTypeDN(
        typeDesc = typeDesc,
        typeCode = typeCode,
    )
}

private fun InboxPermissionDTO.toDomain(): InboxPermissionDN {
    return InboxPermissionDN(
        password = password,
        dateFrom = dateFrom,
        dateTo = dateTo,
    )
}

private fun JsonElement.toStoredString(): String? {
    return when (this) {
        is JsonPrimitive -> if (isString) content else toString()
        else -> toString()
    }
}
