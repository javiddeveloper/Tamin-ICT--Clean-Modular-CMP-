package com.tamin.taminhamrah.mapper.contracts

import com.tamin.taminhamrah.model.contracts.BranchDN
import com.tamin.taminhamrah.model.contracts.BranchPR
import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.contracts.ContractPR
import com.tamin.taminhamrah.model.contracts.ContractStatusCode
import com.tamin.taminhamrah.util.PersianDateFormatter

fun ContractDN.toPresentation(): ContractPR {
    val statusDesc = contractStatusObject?.selfIsuContStatDesc
        ?: (contractStatus ?: "")
    val hasTreatmentSupport = resolveTreatmentSupport()
    return ContractPR(
        contractNumber = contractNumber?.toString() ?: "",
        statusDesc = statusDesc,
        isActive = contractStatusObject?.selfIsuContStatCode != ContractStatusCode.CANCELLED,
        requestDate = PersianDateFormatter.formatTimestamp(
            createDate ?: contractDate ?: creatDate ?: startDate,
        ),
        insuranceType = premiumType?.insuranceDescription
            ?: premiumType?.insuranceKind
            ?: (premiumTypeCode ?: ""),
        monthlyPremiumLabel = premiumRate?.spcrateDescription ?: "",
        monthlyIncome = salary?.toString() ?: "",
        hasTreatmentSupport = hasTreatmentSupport,
        jobTitle = freeJob?.discrioption ?: "",
    )
}

fun List<ContractDN>.toPresentation(): List<ContractPR> = map { it.toPresentation() }

fun BranchDN.toPresentation(): BranchPR = BranchPR(
    code = code.orEmpty(),
    name = displayName,
)

fun List<BranchDN>.toBranchPresentation(): List<BranchPR> = map { it.toPresentation() }

private fun ContractDN.resolveTreatmentSupport(): Boolean = cntDrmn != "2"
