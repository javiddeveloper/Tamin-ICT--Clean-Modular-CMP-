package com.tamin.taminhamrah.mapper.certificate

import com.tamin.taminhamrah.model.certificate.StatusCertificateReportDN
import com.tamin.taminhamrah.model.certificate.StatusCertificateReportPR
import com.tamin.taminhamrah.model.certificate.RecipientDN
import com.tamin.taminhamrah.model.certificate.RecipientPR

fun StatusCertificateReportDN.toUiModel(): StatusCertificateReportPR {
    return StatusCertificateReportPR(
        refCode = refCode
    )
}

fun RecipientDN.toUiModel(): RecipientPR {
    return RecipientPR(
        code = recipientCode ?: "",
        name = recipientName ?: ""
    )
}

fun List<RecipientDN>.toUiModelList(): List<RecipientPR> {
    return this.map { it.toUiModel() }
}
