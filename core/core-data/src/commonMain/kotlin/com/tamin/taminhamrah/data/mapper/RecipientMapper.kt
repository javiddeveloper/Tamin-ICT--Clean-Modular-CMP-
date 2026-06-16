package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.data.local.entity.RecipientEntity
import com.tamin.taminhamrah.model.common.RecipientDTO
import com.tamin.taminhamrah.model.common.RecipientDN

internal fun RecipientDTO.toDomain(): RecipientDN = RecipientDN(
    recipientCode = recipientCode ?: "",
    recipientName = recipientName ?: ""
)

internal fun RecipientEntity.toDomain(): RecipientDN = RecipientDN(
    recipientCode = recipientCode,
    recipientName = recipientName ?: ""
)

internal fun RecipientDN.toEntity(): RecipientEntity = RecipientEntity(
    recipientCode = recipientCode,
    recipientName = recipientName
)
