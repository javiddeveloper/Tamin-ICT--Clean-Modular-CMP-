package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.contractAffair.CancelContractParamsDN
import com.tamin.taminhamrah.model.contractAffair.CancelContractRequestDTO
import com.tamin.taminhamrah.model.contractAffair.ContractDN
import com.tamin.taminhamrah.model.contractAffair.ContractDTO
import com.tamin.taminhamrah.model.contractAffair.ContractDebitDN
import com.tamin.taminhamrah.model.contractAffair.ContractDebitDTO
import com.tamin.taminhamrah.model.contractAffair.ContractLastPaymentDN
import com.tamin.taminhamrah.model.contractAffair.ContractLastPaymentDTO
import com.tamin.taminhamrah.model.contractAffair.ContractPaymentHistoryItemDN
import com.tamin.taminhamrah.model.contractAffair.ContractPaymentHistoryItemDTO
import com.tamin.taminhamrah.model.contractAffair.ContractStateDN
import com.tamin.taminhamrah.model.contractAffair.ContractStateDTO
import com.tamin.taminhamrah.model.contractAffair.ContractStatusObjectDN
import com.tamin.taminhamrah.model.contractAffair.ContractStatusObjectDTO
import com.tamin.taminhamrah.model.contractAffair.FreeJobDN
import com.tamin.taminhamrah.model.contractAffair.FreeJobDTO
import com.tamin.taminhamrah.model.contractAffair.PaymentCalculationRowDN
import com.tamin.taminhamrah.model.contractAffair.PaymentCalculationRowDTO
import com.tamin.taminhamrah.model.contractAffair.PremiumRateDN
import com.tamin.taminhamrah.model.contractAffair.PremiumRateDTO
import com.tamin.taminhamrah.model.contractAffair.PremiumTypeDN
import com.tamin.taminhamrah.model.contractAffair.PremiumTypeDTO

fun ContractDTO.toDomain(): ContractDN = ContractDN(
    adultLetterDate = adultLetterDate,
    adultLetterNumber = adultLetterNumber,
    age = age,
    branchCode = branchCode,
    brchCodeNew = brchCodeNew,
    cancelDate = cancelDate,
    cancelUID = cancelUID,
    canceldesc = canceldesc,
    cityCode = cityCode,
    cntDrmn = cntDrmn,
    cntFreeJobCode = cntFreeJobCode,
    cntIncPayDate3t4 = cntIncPayDate3t4,
    cntMedicalFlag = cntMedicalFlag,
    comment = comment,
    commissionStatus = commissionStatus,
    confirmDate = confirmDate,
    confirmUID = confirmUID,
    contractDate = contractDate,
    contractNumber = contractNumber,
    contractStatus = contractStatus,
    contractStatusObject = contractStatusObject?.toDomain(),
    creatDate = creatDate,
    createDate = createDate,
    createUID = createUID,
    eligibilityStatus = eligibilityStatus,
    freeJob = freeJob?.toDomain(),
    guid = guid,
    guidName = guidName,
    history = history,
    insuranceId = insuranceId,
    isStudent = isStudent,
    medicalExemptionStatus = medicalExemptionStatus,
    militaryServiceLicense = militaryServiceLicense,
    mobileNumber = mobileNumber,
    natinoalCode = natinoalCode,
    physicalStatus = physicalStatus,
    premiumRate = premiumRate?.toDomain(),
    premiumRateCode = premiumRateCode,
    premiumType = premiumType?.toDomain(),
    premiumTypeCode = premiumTypeCode,
    provinceCode = provinceCode,
    provinceName = provinceName,
    refCode = refCode,
    salary = salary,
    startDate = startDate,
    statusDate = statusDate,
    wage = wage,
)

fun ContractStatusObjectDTO.toDomain(): ContractStatusObjectDN = ContractStatusObjectDN(
    selfIsuContStatDesc = selfIsuContStatDesc,
    selfIsuContStatCode = selfIsuContStatCode,
)

fun PremiumTypeDTO.toDomain(): PremiumTypeDN = PremiumTypeDN(
    insuranceDescription = insuranceDescription,
    insuranceKind = insuranceKind,
    insuranceTypeCode = insuranceTypeCode,
    status = status,
    statusDate = statusDate,
)

fun PremiumRateDTO.toDomain(): PremiumRateDN = PremiumRateDN(
    govermentPercent = govermentPercent,
    insurDpercent = insurDpercent,
    payrespitelOne = payrespitelOne,
    payrespitelTwo = payrespitelTwo,
    selfIsuTypeCode = selfIsuTypeCode,
    spcLowDayWage = spcLowDayWage,
    spcrateCode = spcrateCode,
    spcrateDescription = spcrateDescription,
    status = status,
    statusStDate = statusStDate,
    treatmentPercap = treatmentPercap,
)

fun FreeJobDTO.toDomain(): FreeJobDN = FreeJobDN(
    discrioption = discrioption,
    endDate = endDate,
    fixRank = fixRank,
    id = id,
    iscoCode = iscoCode,
    jobCode = jobCode,
    startDate = startDate,
    status = status,
)

fun ContractStateDTO.toDomain(): ContractStateDN = ContractStateDN(
    code = selfIsuContStatCode,
    description = selfIsuContStatDesc,
)

fun ContractPaymentHistoryItemDTO.toDomain(): ContractPaymentHistoryItemDN =
    ContractPaymentHistoryItemDN(
        nationalId = nationalId,
        insuranceId = insuranceId,
        debtNumber = debtNumber,
        startTermPayment = startTermPayment,
        endTermPayment = endTermPayment,
        totalDebt = totalDebt,
        paymentDeadline = paymentDeadline,
        amountPayment = amountPayment,
        datePayment = datePayment,
        statusContract = statusContract,
        statusRecipient = statusRecipient,
    )

fun ContractDebitDTO.toDomain(): ContractDebitDN = ContractDebitDN(
    total = total,
    insurancePremiums = insurancePremiums,
    previousDebit = previousDebit,
    startDate = startDate,
    endDate = endDate,
    payPremiumDate = payPremiumDate,
    infoMessage = messageInformation,
)

fun ContractLastPaymentDTO.toDomain(): ContractLastPaymentDN = ContractLastPaymentDN(
    lastPaymentTimestamp = lastPaymentTimestamp?.toLongOrNull()?.takeIf { it != 0L },
    checkReloLap = chekReloLap,
    medicalResultResend = medicalRsltResend,
)

fun PaymentCalculationRowDTO.toDomain(): PaymentCalculationRowDN = PaymentCalculationRowDN(
    year = year,
    month = month,
    day = day,
    description = description,
    wage = wage,
    amount = amount,
)

fun List<PaymentCalculationRowDTO>.toDomain(): List<PaymentCalculationRowDN> = map { it.toDomain() }

fun CancelContractParamsDN.toRequestDto(): CancelContractRequestDTO = CancelContractRequestDTO(
    canceldesc = description,
    contractStatus = stateChange.value,
)
