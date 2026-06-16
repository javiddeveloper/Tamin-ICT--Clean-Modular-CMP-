package com.tamin.taminhamrah.mapper.pension

import kotlin.jvm.JvmName
import com.tamin.taminhamrah.model.pension.PensionIdDN
import com.tamin.taminhamrah.model.pension.PensionIdPR
import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import com.tamin.taminhamrah.model.pension.PensionInquiryPR

fun PensionInquiryDN.toPresentation(): PensionInquiryPR {
    return PensionInquiryPR(
        branchCode = branchCode ?: "",
        insuranceNumber = insuranceNumber ?: "",
        pensionerRisUid = pensionerRisUid ?: "",
        pensionerType = pensionerType ?: "",
        paymentDate = paymentDate ?: "",
        pensionerBaseDate = pensionerBaseDate ?: "",
        fullName = fullName ?: "نامشخص",
        statusDesc = statusDesc ?: "نامشخص",
        sexDesc = sexDesc ?: "نامشخص",
        branchName = branchName ?: "",
        pensionEndDate = pensionEndDate ?: "",
        nationalId = nationalId ?: "",
        paymentAmount = paymentAmount?.toString() ?: "0"
    )
}

fun List<PensionInquiryDN>.toPresentation(): List<PensionInquiryPR> {
    return this.map { it.toPresentation() }
}

fun PensionIdDN.toPresentation(): PensionIdPR {
    return PensionIdPR(
        pensionerId = pensionerId ?: ""
    )
}

@JvmName("toPresentationPensionIdDN")
fun List<PensionIdDN>.toPresentation(): List<PensionIdPR> {
    return this.map { it.toPresentation() }
}
