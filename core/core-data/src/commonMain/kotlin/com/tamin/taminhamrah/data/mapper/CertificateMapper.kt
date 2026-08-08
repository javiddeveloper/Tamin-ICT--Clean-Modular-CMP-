package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.certificate.RecipientDTO
import com.tamin.taminhamrah.model.certificate.RecipientDN

fun RecipientDTO.toDomain(): RecipientDN {
    return RecipientDN(
        recipientCode = recipientCode,
        recipientName = recipientName
    )
}
