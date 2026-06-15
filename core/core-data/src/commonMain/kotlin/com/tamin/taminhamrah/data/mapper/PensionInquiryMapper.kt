package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.pension.PensionIdDN
import com.tamin.taminhamrah.model.pension.PensionIdDTO
import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import com.tamin.taminhamrah.model.pension.PensionInquiryDTO

fun PensionInquiryDTO.toDomain(): PensionInquiryDN {
    return PensionInquiryDN(
        branchCode = branchCode,
        insuranceNumber = insuranceNumber,
        pensionerRisUid = pensionerRisUid,
        pensionerType = pensionerType,
        paymentDate = paymentDate,
        pensionerBaseDate = pensionerBaseDate,
        fullName = fullName,
        statusDesc = statusDesc,
        sexDesc = sexDesc,
        branchName = branchName,
        pensionEndDate = pensionEndDate,
        nationalId = nationalId,
        paymentAmount = paymentAmount
    )
}

fun PensionIdDTO.toDomain(): PensionIdDN {
    return PensionIdDN(
        pensionerId = pensionerId
    )
}
