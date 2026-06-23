package com.tamin.taminhamrah.mapper.contracts

import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.contracts.ContractPR
import com.tamin.taminhamrah.util.PersianDateFormatter

fun ContractDN.toPresentation(): ContractPR {
    val statusDesc = contractStatusObject?.selfIsuContStatDesc
        ?: contractStatus.orEmpty()
    val hasTreatmentSupport = resolveTreatmentSupport()
    return ContractPR(
        contractNumber = contractNumber?.toString().orEmpty(),
        statusDesc = statusDesc,
        isActive = !statusDesc.contains("ابطال"),
        requestDate = PersianDateFormatter.formatTimestamp(
            createDate ?: contractDate ?: creatDate ?: startDate,
        ),
        insuranceType = premiumType?.insuranceDescription
            ?: premiumType?.insuranceKind
            ?: premiumTypeCode.orEmpty(),
        monthlyPremiumLabel = premiumRate?.spcrateDescription.orEmpty(),
        monthlyIncome = salary?.toString()?:"",
        treatmentSupportText = if (hasTreatmentSupport) "حمایت درمان دارد" else "حمایت درمان ندارد",
        hasTreatmentSupport = hasTreatmentSupport,
        jobTitle = freeJob?.discrioption.orEmpty(),
    )
}

fun List<ContractDN>.toPresentation(): List<ContractPR> = map { it.toPresentation() }

private fun ContractDN.resolveTreatmentSupport(): Boolean = cntDrmn != "2"


