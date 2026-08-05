package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.certificate.StatusCertificateReportDTO
import com.tamin.taminhamrah.model.certificate.StatusCertificateReportDN
import com.tamin.taminhamrah.model.certificate.RecipientDTO
import com.tamin.taminhamrah.model.certificate.RecipientDN

fun StatusCertificateReportDTO.toDomain(): StatusCertificateReportDN {
    return StatusCertificateReportDN(
        refCode = refCode
    )
}

fun RecipientDTO.toDomain(): RecipientDN {
    return RecipientDN(
        recipientCode = recipientCode,
        recipientName = recipientName
    )
}
