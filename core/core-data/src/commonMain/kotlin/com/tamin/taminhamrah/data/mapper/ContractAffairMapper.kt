package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.data.local.entity.ContractEntity
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

// ---- Offline cache (contract_affair_pages) ----
// Reuses ContractEntity's flattened columns. The read mapper has its own name because
// ContractsMapper already defines ContractEntity.toDomain() for the contracts feature's model.

internal fun ContractDN.toEntity(): ContractEntity = ContractEntity(
    contractNumber = contractNumber ?: 0,
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
    contractStatus = contractStatus,
    contractStatusDesc = contractStatusObject?.selfIsuContStatDesc,
    contractStatusCode = contractStatusObject?.selfIsuContStatCode,
    creatDate = creatDate,
    createDate = createDate,
    createUID = createUID,
    eligibilityStatus = eligibilityStatus,
    freeJobDescription = freeJob?.discrioption,
    freeJobEndDate = freeJob?.endDate,
    freeJobFixRank = freeJob?.fixRank,
    freeJobId = freeJob?.id,
    freeJobIscoCode = freeJob?.iscoCode,
    freeJobCode = freeJob?.jobCode,
    freeJobStartDate = freeJob?.startDate,
    freeJobStatus = freeJob?.status,
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
    premiumRateGovermentPercent = premiumRate?.govermentPercent,
    premiumRateInsurDpercent = premiumRate?.insurDpercent,
    premiumRatePayrespitelOne = premiumRate?.payrespitelOne,
    premiumRatePayrespitelTwo = premiumRate?.payrespitelTwo,
    premiumRateSelfIsuTypeCode = premiumRate?.selfIsuTypeCode,
    premiumRateSpcLowDayWage = premiumRate?.spcLowDayWage,
    premiumRateSpcrateCode = premiumRate?.spcrateCode,
    premiumRateSpcrateDescription = premiumRate?.spcrateDescription,
    premiumRateStatus = premiumRate?.status,
    premiumRateStatusStDate = premiumRate?.statusStDate,
    premiumRateTreatmentPercap = premiumRate?.treatmentPercap,
    premiumRateCode = premiumRateCode,
    premiumTypeInsuranceDescription = premiumType?.insuranceDescription,
    premiumTypeInsuranceKind = premiumType?.insuranceKind,
    premiumTypeInsuranceTypeCode = premiumType?.insuranceTypeCode,
    premiumTypeStatus = premiumType?.status,
    premiumTypeStatusDate = premiumType?.statusDate,
    premiumTypeCode = premiumTypeCode,
    provinceCode = provinceCode,
    provinceName = provinceName,
    refCode = refCode,
    salary = salary,
    startDate = startDate,
    statusDate = statusDate,
    wage = wage,
)

internal fun ContractEntity.toContractAffairDomain(): ContractDN = ContractDN(
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
    // A nested object whose columns are all empty was null on the network model; keep it null.
    contractStatusObject = ifAnyPresent(contractStatusDesc, contractStatusCode) {
        ContractStatusObjectDN(
            selfIsuContStatDesc = contractStatusDesc,
            selfIsuContStatCode = contractStatusCode,
        )
    },
    creatDate = creatDate,
    createDate = createDate,
    createUID = createUID,
    eligibilityStatus = eligibilityStatus,
    freeJob = ifAnyPresent(
        freeJobDescription, freeJobEndDate, freeJobFixRank, freeJobId,
        freeJobIscoCode, freeJobCode, freeJobStartDate, freeJobStatus,
    ) {
        FreeJobDN(
            discrioption = freeJobDescription,
            endDate = freeJobEndDate,
            fixRank = freeJobFixRank,
            id = freeJobId,
            iscoCode = freeJobIscoCode,
            jobCode = freeJobCode,
            startDate = freeJobStartDate,
            status = freeJobStatus,
        )
    },
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
    premiumRate = ifAnyPresent(
        premiumRateGovermentPercent, premiumRateInsurDpercent, premiumRatePayrespitelOne,
        premiumRatePayrespitelTwo, premiumRateSelfIsuTypeCode, premiumRateSpcLowDayWage,
        premiumRateSpcrateCode, premiumRateSpcrateDescription, premiumRateStatus,
        premiumRateStatusStDate, premiumRateTreatmentPercap,
    ) {
        PremiumRateDN(
            govermentPercent = premiumRateGovermentPercent,
            insurDpercent = premiumRateInsurDpercent,
            payrespitelOne = premiumRatePayrespitelOne,
            payrespitelTwo = premiumRatePayrespitelTwo,
            selfIsuTypeCode = premiumRateSelfIsuTypeCode,
            spcLowDayWage = premiumRateSpcLowDayWage,
            spcrateCode = premiumRateSpcrateCode,
            spcrateDescription = premiumRateSpcrateDescription,
            status = premiumRateStatus,
            statusStDate = premiumRateStatusStDate,
            treatmentPercap = premiumRateTreatmentPercap,
        )
    },
    premiumRateCode = premiumRateCode,
    premiumType = ifAnyPresent(
        premiumTypeInsuranceDescription, premiumTypeInsuranceKind, premiumTypeInsuranceTypeCode,
        premiumTypeStatus, premiumTypeStatusDate,
    ) {
        PremiumTypeDN(
            insuranceDescription = premiumTypeInsuranceDescription,
            insuranceKind = premiumTypeInsuranceKind,
            insuranceTypeCode = premiumTypeInsuranceTypeCode,
            status = premiumTypeStatus,
            statusDate = premiumTypeStatusDate,
        )
    },
    premiumTypeCode = premiumTypeCode,
    provinceCode = provinceCode,
    provinceName = provinceName,
    refCode = refCode,
    salary = salary,
    startDate = startDate,
    statusDate = statusDate,
    wage = wage,
)

private inline fun <T> ifAnyPresent(vararg columns: Any?, build: () -> T): T? =
    if (columns.any { it != null }) build() else null
