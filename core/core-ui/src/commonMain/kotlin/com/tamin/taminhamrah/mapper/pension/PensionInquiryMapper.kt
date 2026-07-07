package com.tamin.taminhamrah.mapper.pension

import kotlin.jvm.JvmName
import com.tamin.taminhamrah.model.pension.PensionIdDN
import com.tamin.taminhamrah.model.pension.PensionIdPR
import com.tamin.taminhamrah.model.pension.PensionInquiryDN
import com.tamin.taminhamrah.model.pension.PensionInquiryPR
import com.tamin.taminhamrah.model.common.RecipientDN
import com.tamin.taminhamrah.model.pension.RecipientPR
import com.tamin.taminhamrah.model.pension.*
import com.tamin.taminhamrah.model.pension.installment.*

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

fun RecipientDN.toPresentation(): RecipientPR {
    return RecipientPR(
        recipientCode = recipientCode,
        recipientName = recipientName
    )
}

@JvmName("toPresentationRecipientDN")
fun List<RecipientDN>.toPresentation(): List<RecipientPR> {
    return this.map { it.toPresentation() }
}

fun EdictPensionerDN.toPresentation(): EdictPensionerPR {
    return EdictPensionerPR(
        lastName = lastName ?: "",
        branchName = branchName ?: "",
        insuranceId = insuranceId ?: "",
        title = title ?: "",
        edictYear = edictYear,
        edictMonth = edictMonth,
        edictInfo = edictInfo?.toPresentation(),
        survivorInfo = survivorInfo?.map { it.toPresentation() } ?: emptyList(),
        detail = detail?.map { it.toPresentation() } ?: emptyList()
    )
}

fun EdictInfoDN.toPresentation(): EdictInfoPR {
    return EdictInfoPR(
        pensionerId = pensionerId ?: "",
        nationalCode = nationalCode ?: "",
        firstName = firstName ?: "",
        lastName = lastName ?: "",
        fatherName = fatherName ?: "",
        birthDate = birthDate ?: "",
        idNumber = idNumber ?: "",
        gender = gender ?: "",
        insuranceType = insuranceType ?: "",
        pensionStartDate = pensionStartDate ?: "",
        originalHistoryYear = originalHistoryYear ?: "0",
        originalHistoryMonth = originalHistoryMonth ?: "0",
        originalHistoryDay = originalHistoryDay ?: "0",
        additionalYear = additionalYear ?: "0",
        additionalMonth = additionalMonth ?: "0",
        additionalDay = additionalDay ?: "0",
        basisImplementation = basisImplementation ?: "",
        pensionBeforeIncrease = pensionBeforeIncrease ?: "0",
        pensionAfterIncrease = pensionAfterIncrease ?: "0",
        edictDescription = edictDescription ?: "",
        id = id ?: "",
        firstStageTotalPensionAndProportional = firstStageTotalPensionAndProportional ?: "0",
        totalPensionBeforeIncrease = totalPensionBeforeIncrease ?: "0",
        firstStageTotalProportional = firstStageTotalProportional ?: "0",
        totalAmount = totalAmount ?: "0",
        payableMonthly = payableMonthly ?: "0",
        lettersPayableMonthly = lettersPayableMonthly ?: ""
    )
}

fun SurvivorInfoDN.toPresentation(): SurvivorInfoPR {
    return SurvivorInfoPR(
        pensionAfterIncrease = pensionAfterIncrease ?: "0",
        previousPension = previousPension ?: "0",
        originalHistoryDay = originalHistoryDay ?: "0",
        originalHistoryMonth = originalHistoryMonth ?: "0",
        originalHistoryYear = originalHistoryYear ?: "0",
        leniencyYear = leniencyYear ?: "0",
        leniencyMonth = leniencyMonth ?: "0",
        leniencyDay = leniencyDay ?: "0",
        edictDescription = edictDescription ?: "",
        id = id ?: "",
        insuranceType = insuranceType ?: "",
        lastName = lastName ?: "",
        firstName = firstName ?: "",
        firstStageTotalPensionAndProportional = firstStageTotalPensionAndProportional ?: "0",
        firstStageTotalProportional = firstStageTotalProportional ?: "0",
        differenceProportionalityBasedHistory = differenceProportionalityBasedHistory ?: "0",
        nationalCode = nationalCode ?: "",
        pensionerId = pensionerId ?: "",
        quota = quota,
        totalAmount = totalAmount ?: "0"
    )
}

fun EdictPensionerDetailDN.toPresentation(): EdictPensionerDetailPR {
    return EdictPensionerDetailPR(
        fieldDesc = fieldDesc ?: "",
        fieldValue = fieldValue ?: "0",
        index = index ?: "",
        packageName = packageName ?: ""
    )
}

fun DeferredInstallmentRequestPR.toDomain(): DeferredInstallmentRequestDN {
    return DeferredInstallmentRequestDN(
        bank = bank?.toDomain(),
        bankBranch = bankBranch,
        garanteeType = garanteeType,
        guaranteeAmount = guaranteeAmount,
        installmentAmount = installmentAmount,
        installmentCount = installmentCount,
        loanAmount = loanAmount,
        pensionerId = pensionerId,
        birthDate = birthDate,
        firstName = firstName,
        lastName = lastName,
        nationalId = nationalId
    )
}

fun BankPR.toDomain(): BankDN {
    return BankDN(
        bankCode = bankCode
    )
}

fun DeferredInstallmentCertificateDN.toPresentation(): DeferredInstallmentCertificatePR {
    return DeferredInstallmentCertificatePR(
        request = request?.toPresentation()
    )
}

fun RequestCertificateDN.toPresentation(): RequestCertificatePR {
    return RequestCertificatePR(
        refCode = refCode
    )
}

fun PayRollDN.toPresentation(): PayRollPR {
    return PayRollPR(
        id = id ?: 0,
        clpType = clpType ?: "",
        tprDesc = tprDesc ?: "",
        sumAmount = sumAmount ?: 0,
        textNumber = textNumber ?: "",
        sumPay = sumPay ?: 0,
        hisYear = hisYear ?: "",
        hisMon = hisMon ?: "",
        hisDay = hisDay ?: "",
        hisYearPlus = hisYearPlus ?: "",
        hisMonPlus = hisMonPlus ?: "",
        hisDayPlus = hisDayPlus ?: ""
    )
}

fun InquirePensionCertificateDN.toPresentation(): InquirePensionCertificatePR {
    return InquirePensionCertificatePR(
        message = message ?: ""
    )
}
