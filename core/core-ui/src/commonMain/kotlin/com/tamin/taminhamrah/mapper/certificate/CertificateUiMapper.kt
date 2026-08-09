package com.tamin.taminhamrah.mapper.certificate

import com.tamin.taminhamrah.model.certificate.RecipientDN
import com.tamin.taminhamrah.model.certificate.RecipientPR

fun RecipientDN.toUiModel(): RecipientPR {
    return RecipientPR(
        code = recipientCode ?: "",
        name = recipientName ?: ""
    )
}

fun List<RecipientDN>.toUiModelList(): List<RecipientPR> {
    return this.map { it.toUiModel() }
}
