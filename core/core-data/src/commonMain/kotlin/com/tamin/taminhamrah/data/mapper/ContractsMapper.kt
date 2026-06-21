package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.data.local.entity.ContractEntity
import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.contracts.ContractDTO
import com.tamin.taminhamrah.model.contracts.ContractStatusObjectDN
import com.tamin.taminhamrah.model.contracts.ContractStatusObjectDTO
import com.tamin.taminhamrah.model.contracts.FreeJobDN
import com.tamin.taminhamrah.model.contracts.FreeJobDTO
import com.tamin.taminhamrah.model.contracts.PremiumRateDN
import com.tamin.taminhamrah.model.contracts.PremiumRateDTO
import com.tamin.taminhamrah.model.contracts.PremiumTypeDN
import com.tamin.taminhamrah.model.contracts.PremiumTypeDTO

fun ContractDTO.toDomain(): ContractDN {
    return ContractDN(
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
        wage = wage
    )
}

fun ContractStatusObjectDTO.toDomain(): ContractStatusObjectDN {
    return ContractStatusObjectDN(
        selfIsuContStatDesc = selfIsuContStatDesc,
        selfIsuContStatCode = selfIsuContStatCode
    )
}

fun PremiumTypeDTO.toDomain(): PremiumTypeDN {
    return PremiumTypeDN(
        insuranceDescription = insuranceDescription,
        insuranceKind = insuranceKind,
        insuranceTypeCode = insuranceTypeCode,
        status = status,
        statusDate = statusDate
    )
}

fun PremiumRateDTO.toDomain(): PremiumRateDN {
    return PremiumRateDN(
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
        treatmentPercap = treatmentPercap
    )
}

fun FreeJobDTO.toDomain(): FreeJobDN {
    return FreeJobDN(
        discrioption = discrioption,
        endDate = endDate,
        fixRank = fixRank,
        id = id,
        iscoCode = iscoCode,
        jobCode = jobCode,
        startDate = startDate,
        status = status
    )
}

internal fun ContractDTO.toEntity(): ContractEntity = toDomain().toEntity()

internal fun ContractDN.toEntity(): ContractEntity {
    return ContractEntity(
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
}

internal fun ContractEntity.toDomain(): ContractDN {
    return ContractDN(
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
        contractStatusObject = ContractStatusObjectDN(
            selfIsuContStatDesc = contractStatusDesc,
            selfIsuContStatCode = contractStatusCode,
        ),
        creatDate = creatDate,
        createDate = createDate,
        createUID = createUID,
        eligibilityStatus = eligibilityStatus,
        freeJob = FreeJobDN(
            discrioption = freeJobDescription,
            endDate = freeJobEndDate,
            fixRank = freeJobFixRank,
            id = freeJobId,
            iscoCode = freeJobIscoCode,
            jobCode = freeJobCode,
            startDate = freeJobStartDate,
            status = freeJobStatus,
        ),
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
        premiumRate = PremiumRateDN(
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
        ),
        premiumRateCode = premiumRateCode,
        premiumType = PremiumTypeDN(
            insuranceDescription = premiumTypeInsuranceDescription,
            insuranceKind = premiumTypeInsuranceKind,
            insuranceTypeCode = premiumTypeInsuranceTypeCode,
            status = premiumTypeStatus,
            statusDate = premiumTypeStatusDate,
        ),
        premiumTypeCode = premiumTypeCode,
        provinceCode = provinceCode,
        provinceName = provinceName,
        refCode = refCode,
        salary = salary,
        startDate = startDate,
        statusDate = statusDate,
        wage = wage,
    )
}
