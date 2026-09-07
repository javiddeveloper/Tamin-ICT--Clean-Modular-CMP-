package com.tamin.taminhamrah.mapper.contracts

import com.tamin.taminhamrah.model.contracts.BranchDN
import com.tamin.taminhamrah.model.contracts.BranchPR
import com.tamin.taminhamrah.model.contracts.ContractDN
import com.tamin.taminhamrah.model.contracts.ContractPR
import com.tamin.taminhamrah.model.contracts.ContractPaymentHistoryItemDN
import com.tamin.taminhamrah.model.contracts.ContractPaymentHistoryItemPR
import com.tamin.taminhamrah.model.contracts.ContractStateDN
import com.tamin.taminhamrah.model.contracts.ContractStatePR
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import kotlin.jvm.JvmName

fun ContractDN.toPresentation(): ContractPR {
    val statusDesc = contractStatusObject?.selfIsuContStatDesc
        ?: (contractStatus?:"")
    val hasTreatmentSupport = resolveTreatmentSupport()
    return ContractPR(
        contractNumber = contractNumber?.toString()?:"",
        statusDesc = statusDesc,
        isActive = !statusDesc.contains("ابطال"),
        requestDate = PersianDateFormatter.formatTimestamp(
            createDate ?: contractDate ?: creatDate ?: startDate,
        ),
        insuranceType = premiumType?.insuranceDescription
            ?: premiumType?.insuranceKind
            ?: (premiumTypeCode?:""),
        monthlyPremiumLabel = premiumRate?.spcrateDescription?:"",
        monthlyIncome = salary?.toString() ?: "",
        treatmentSupportText = if (hasTreatmentSupport) "حمایت درمان دارد" else "حمایت درمان ندارد",
        hasTreatmentSupport = hasTreatmentSupport,
        jobTitle = freeJob?.discrioption?:"",
        premiumTypeCode = premiumTypeCode
            ?: premiumType?.insuranceTypeCode
            ?: "",
        statusCode = contractStatusObject?.selfIsuContStatCode,
        freeJobCode = cntFreeJobCode ?: "",
        premiumRatePercentLabel = premiumRate?.insurDpercent
            ?.takeIf { it.isNotBlank() }
            ?.let { "${it.toPersianDigits()} درصد" }
            ?: "",
        // TODO(data): list-contracts-mobile carries no outstanding-debt figure; wire from the دیون
        //  endpoint in a later step so the بدهی معوق banner can render on active cards.
        deferredDebtLabel = null,
    )
}

fun List<ContractDN>.toPresentation(): List<ContractPR> = map { it.toPresentation() }

fun ContractStateDN.toPresentation(): ContractStatePR? {
    val stateCode = code ?: return null
    return ContractStatePR(code = stateCode, title = description.orEmpty())
}

@JvmName("contractStatesToPresentation")
fun List<ContractStateDN>.toPresentation(): List<ContractStatePR> = mapNotNull { it.toPresentation() }

fun ContractPaymentHistoryItemDN.toPresentation(): ContractPaymentHistoryItemPR {
    // Vazirmatn `ss01` renders these ASCII digits as Persian at draw time, so nothing is
    // digit-converted here — the raw strings stay copy-safe.
    val paid = when {
        statusContract?.contains("نشده") == true -> false
        statusContract?.contains("شده") == true -> true
        else -> (amountPayment ?: 0.0) > 0.0 && !datePayment.isNullOrBlank()
    }
    return ContractPaymentHistoryItemPR(
        debtNumber = debtNumber.orEmpty(),
        amountPayment = (amountPayment?.toLong()?.toString()).orEmpty(),
        datePayment = datePayment.orEmpty(),
        totalDebt = (totalDebt?.toLong()?.toString()).orEmpty(),
        paymentDeadline = paymentDeadline.orEmpty(),
        termStart = startTermPayment.orEmpty(),
        termEnd = endTermPayment.orEmpty(),
        collectionStatus = statusRecipient.orEmpty(),
        isPaid = paid,
        statusLabel = statusContract?.takeIf { it.isNotBlank() }
            ?: if (paid) "پرداخت شده" else "پرداخت نشده",
    )
}

@JvmName("contractPaymentHistoryToPresentation")
fun List<ContractPaymentHistoryItemDN>.toPresentation(): List<ContractPaymentHistoryItemPR> =
    map { it.toPresentation() }

fun BranchDN.toPresentation(): BranchPR = BranchPR(
    code = code.orEmpty(),
    name = displayName,
)

fun List<BranchDN>.toBranchPresentation(): List<BranchPR> = map { it.toPresentation() }

private fun ContractDN.resolveTreatmentSupport(): Boolean = cntDrmn != "2"


