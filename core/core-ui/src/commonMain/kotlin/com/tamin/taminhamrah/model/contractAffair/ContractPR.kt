package com.tamin.taminhamrah.model.contractAffair

import androidx.compose.runtime.Immutable

/**
 * امور قراردادها و پرداخت — dedicated presentation model for the standalone
 * `feature/contractsAndPaymentAffair` module. Kept separate from
 * `com.tamin.taminhamrah.model.contracts.ContractPR`.
 */
@Immutable
data class ContractPR(
    val contractNumber: String,
    val statusDesc: String,
    val isActive: Boolean,
    val requestDate: String,
    val insuranceType: String,
    val monthlyPremiumLabel: String,
    val monthlyIncome: String,
    val treatmentSupportText: String,
    val hasTreatmentSupport: Boolean,
    val jobTitle: String,
    /** Raw `premiumTypeCode` ("01"/"02"/"38"); drives which operation endpoints a card can call. */
    val premiumTypeCode: String,
    /** `contractStatusObject.selfIsuContStatDode`; `1` means the contract is currently active. */
    val statusCode: Int?,
    /** `cntFreeJobCode` — used to gate پرداخت حق بیمه / غیرفعال کردن for special job codes. */
    val freeJobCode: String,
    /** نرخ حق بیمه as a standalone percentage label, e.g. "۲۷ درصد" — from `premiumRate.insurDpercent`. */
    val premiumRatePercentLabel: String = "",
    /**
     * بدهی معوق banner text for an active card, already formatted with «ریال». `null` when there is
     * no outstanding debt — `list-contracts-mobile` does not currently carry it, so this stays null
     * today and a later step fills it from the دیون endpoint.
     */
    val deferredDebtLabel: String? = null,
)

/** One selectable علت خاتمه قرارداد row. [code] is sent as the cancel request path segment. */
@Immutable
data class ContractStatePR(
    val code: Int,
    val title: String,
)

/**
 * One سوابق پرداخت row.
 *
 * [amountPayment] / [totalDebt] are raw digit strings (format with `toRialAmount()` at the call
 * site); the date/term fields already carry Persian digits. [isPaid] drives the row's status icon
 * and whether it counts toward جمع پرداخت‌های موفق; [statusLabel] is the pill text.
 */
@Immutable
data class ContractPaymentHistoryItemPR(
    val debtNumber: String,
    val amountPayment: String,
    val datePayment: String,
    val totalDebt: String,
    val paymentDeadline: String,
    val termStart: String,
    val termEnd: String,
    /** وضعیت وصول — e.g. «وصول شده» / «در انتظار». */
    val collectionStatus: String,
    val isPaid: Boolean,
    /** «پرداخت شده» / «پرداخت نشده». */
    val statusLabel: String,
)
