package com.tamin.taminhamrah.mapper.contractAffair

import com.tamin.taminhamrah.model.contractAffair.ContractDN
import com.tamin.taminhamrah.model.contractAffair.ContractDebitDN
import com.tamin.taminhamrah.model.contractAffair.ContractDebitPR
import com.tamin.taminhamrah.model.contractAffair.ContractLastPaymentDN
import com.tamin.taminhamrah.model.contractAffair.ContractLastPaymentPR
import com.tamin.taminhamrah.model.contractAffair.ContractPR
import com.tamin.taminhamrah.model.contractAffair.ContractPaymentHistoryItemDN
import com.tamin.taminhamrah.model.contractAffair.ContractPaymentHistoryItemPR
import com.tamin.taminhamrah.model.contractAffair.ContractStateDN
import com.tamin.taminhamrah.model.contractAffair.ContractStatePR
import com.tamin.taminhamrah.model.contractAffair.PaymentCalcLinePR
import com.tamin.taminhamrah.model.contractAffair.PaymentCalcMonthPR
import com.tamin.taminhamrah.model.contractAffair.PaymentCalculationRowDN
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toJalaliDateLabel
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.toImmutableList
import kotlin.jvm.JvmName
import kotlin.math.roundToInt

fun ContractDN.toPresentation(): ContractPR {
    val statusDesc = contractStatusObject?.selfIsuContStatDesc
        ?: (contractStatus ?: "")
    val hasTreatmentSupport = resolveTreatmentSupport()
    return ContractPR(
        contractNumber = contractNumber?.toString() ?: "",
        statusDesc = statusDesc,
        isActive = !statusDesc.contains("ابطال"),
        requestDate = PersianDateFormatter.formatTimestamp(
            createDate ?: contractDate ?: creatDate ?: startDate,
        ),
        insuranceType = premiumType?.insuranceDescription
            ?: premiumType?.insuranceKind
            ?: (premiumTypeCode ?: ""),
        monthlyPremiumLabel = premiumRate?.spcrateDescription ?: "",
        monthlyIncome = salary?.toString() ?: "",
        treatmentSupportText = if (hasTreatmentSupport) "حمایت درمان دارد" else "حمایت درمان ندارد",
        hasTreatmentSupport = hasTreatmentSupport,
        jobTitle = freeJob?.discrioption ?: "",
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

fun ContractDebitDN.toPresentation(): ContractDebitPR = ContractDebitPR(
    payableAmount = (total?.toString()).orEmpty(),
    periodPremiumAmount = (insurancePremiums?.toString()).orEmpty(),
    pastDebtAmount = (previousDebit?.toString()).orEmpty(),
    periodStartLabel = PersianDateFormatter.formatTimestamp(startDate),
    periodEndLabel = PersianDateFormatter.formatTimestamp(endDate),
    deadlineLabel = payPremiumDate?.takeIf { it.isNotBlank() }?.toJalaliDateLabel().orEmpty(),
    hasPastDebt = (previousDebit ?: 0L) > 0L,
    infoMessage = infoMessage?.takeIf { it.isNotBlank() },
    startDate = startDate ?: 0L,
    endDate = endDate ?: 0L,
)

fun ContractLastPaymentDN.toPresentation(): ContractLastPaymentPR {
    val label = PersianDateFormatter.formatTimestamp(lastPaymentTimestamp)
    return ContractLastPaymentPR(
        paidUntilLabel = label,
        hasHistory = label.isNotBlank(),
        warningMessage = checkReloLap?.takeIf { it.isNotBlank() && it != "1" },
    )
}

fun PaymentCalculationRowDN.toLine(): PaymentCalcLinePR {
    val amountValue = amount?.toLong() ?: 0L
    return PaymentCalcLinePR(
        label = description.orEmpty(),
        amountRaw = amountValue,
        isDeduction = amountValue < 0L,
    )
}

/**
 * جزئیات برگ پرداخت — groups the flat `payment-details` rows into one [PaymentCalcMonthPR] per
 * month, keeping every line (both حق بیمه and کمک دولت). دستمزد مبنا / نرخ حق بیمه are derived from
 * the month's primary (non-negative) line.
 */
fun List<PaymentCalculationRowDN>.toMonthPresentation(): List<PaymentCalcMonthPR> =
    groupBy { it.year to it.month }.map { (_, rows) ->
        val first = rows.first()
        val monthNumber = first.month?.trimStart('0')?.toIntOrNull()
        val days = first.day?.trimStart('0')?.toIntOrNull()
        val primary = rows.firstOrNull { (it.amount ?: 0.0) >= 0.0 } ?: first
        val primaryAmount = primary.amount?.toLong() ?: 0L
        // دستمزد مبنا is the monthly base wage: the endpoint sends the daily figure, so × the days.
        val baseWage = primary.wage?.let { w -> days?.let { (w * it).toLong() } } ?: 0L
        val rate = if (baseWage > 0L && primaryAmount != 0L) {
            ((primaryAmount.toDouble() / baseWage.toDouble()) * 100).roundToInt()
        } else {
            null
        }
        val lines = rows.map { it.toLine() }
        PaymentCalcMonthPR(
            monthTitle = buildMonthTitle(first.year, monthNumber),
            monthNumberLabel = (monthNumber?.toString() ?: first.month.orEmpty()).toPersianDigits(),
            daysLabel = (days?.toString() ?: first.day.orEmpty()).toPersianDigits(),
            baseWageRaw = baseWage,
            ratePercent = rate,
            lines = lines.toImmutableList(),
            netAmountRaw = lines.sumOf { it.amountRaw },
        )
    }

/** «مهر ۱۴۰۵» — Jalali month name + year, falling back to the raw values when out of range. */
private fun buildMonthTitle(year: String?, monthNumber: Int?): String {
    val name = monthNumber
        ?.takeIf { it in 1..PersianDateFormatter.monthNames.size }
        ?.let { PersianDateFormatter.monthNames[it - 1] }
    val yearLabel = year.orEmpty().toPersianDigits()
    return listOfNotNull(name, yearLabel.takeIf { it.isNotBlank() }).joinToString(" ")
}

private fun ContractDN.resolveTreatmentSupport(): Boolean = cntDrmn != "2"
