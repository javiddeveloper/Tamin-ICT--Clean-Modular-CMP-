package com.tamin.taminhamrah.model.workshop

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.assigner_base_status_01
import taminx.core.core_ui.assigner_base_status_02
import taminx.core.core_ui.assigner_base_status_03
import taminx.core.core_ui.assigner_base_status_04
import taminx.core.core_ui.assigner_base_status_05
import taminx.core.core_ui.assigner_base_status_06
import taminx.core.core_ui.assigner_base_status_07
import taminx.core.core_ui.assigner_base_status_08
import taminx.core.core_ui.assigner_base_status_09
import taminx.core.core_ui.assigner_base_status_10
import taminx.core.core_ui.assigner_base_status_11
import taminx.core.core_ui.assigner_base_status_12
import taminx.core.core_ui.assigner_base_status_13
import taminx.core.core_ui.assigner_base_status_14
import taminx.core.core_ui.assigner_base_status_15
import taminx.core.core_ui.assigner_base_status_16
import taminx.core.core_ui.assigner_base_status_17
import taminx.core.core_ui.assigner_doc_category_other
import taminx.core.core_ui.assigner_doc_kind_image
import taminx.core.core_ui.assigner_doc_kind_pdf
import taminx.core.core_ui.settlement_doc_final_status
import taminx.core.core_ui.settlement_doc_letter
import taminx.core.core_ui.settlement_doc_subcontractor
import taminx.core.core_ui.settlement_doc_supplement

/**
 * One پیمان as the واگذارندگان screens draw it.
 *
 * Everything is pre-formatted — Persian digits applied, Jalali dates separated, values the service
 * omitted already dashed — so no composable calls a formatter per frame.
 *
 * [card] is a [ContractRowPR] rather than a flattened copy of its fields, because the list row *is*
 * the ردیف‌های پیمان card: same heading, same badge, same two tiles, same contact block. Composing
 * the two models is what lets one card composable serve both screens without either of them
 * growing a flag for the other.
 *
 * [contractRow] and [contractSequence] stay raw ASCII: they are the two of the four keys the
 * مبانی محاسباتی call is addressed with, not labels. The other two live on [card].
 */
@Immutable
data class AssignerContractPR(
    val card: ContractRowPR = ContractRowPR(),
    val contractRow: String = "",
    val contractSequence: String = "",
    /** The پیمان's own branch code, raw ASCII — the third key of a درخواست مفاصاحساب's id. */
    val branchCode: String = "",
    /** شمارهٔ قرارداد, Persian digits. */
    val contractNumber: String = "",
    /** The same number raw — what picks this پیمان out of its ردیف's مفاصاحساب certificates. */
    val rawContractNumber: String = "",
    /** توالی پیمان, Persian digits — [contractSequence] for reading. */
    val sequenceLabel: String = "",
    /** تاریخ قرارداد, `۱۴۰۱/۰۲/۱۰`. */
    val contractDate: String = "",
    /** موضوع پیمان — prose, so it is not digit-converted. */
    val contractSubject: String = "",
    /** Whether the پیمان's end date has passed — which tab it belongs to. False when it has none. */
    val isFinished: Boolean = false,
    /** «اطلاعات واگذارنده (کارگاه شما)» — the signed-in employer's own workshop. */
    val assigner: AssignerPartyPR = AssignerPartyPR(),
    /** «اطلاعات پیمانکار» — the counterparty, and the workshop [card] shows. */
    val employer: AssignerPartyPR = AssignerPartyPR(),
) {
    /**
     * Whether this row can address its own مبانی محاسباتی.
     *
     * All four keys are required: the service reads a missing one as "no filter on that column"
     * and answers with another contract's bases. False means the action is offered disabled rather
     * than opening a screen onto the wrong record.
     */
    val canOpenBases: Boolean
        get() = card.workshopId.isNotBlank() && card.branchCode.isNotBlank() &&
            contractRow.isNotBlank() && contractSequence.isNotBlank()

    /**
     * Whether a درخواست مفاصاحساب can be addressed for this row.
     *
     * The request is a PUT onto an id built from these four keys plus the subject; a blank one files
     * it under an id the service does not know, so the action is offered disabled instead.
     */
    val canRequestSettlement: Boolean
        get() = card.workshopId.isNotBlank() && branchCode.isNotBlank() &&
            contractRow.isNotBlank() && contractSequence.isNotBlank()
}

/** One side of a پیمان as جزئیات پیمان prints it — the same five cells for either party. */
@Immutable
data class AssignerPartyPR(
    val workshopName: String = "",
    /** کد کارگاه in Persian digits. */
    val workshopCode: String = "",
    /** کد ملی / شناسهٔ ملی in Persian digits. */
    val nationalId: String = "",
    val branchName: String = "",
    val address: String = "",
)

/** A مفاصاحساب certificate on file for a خاتمه‌یافته پیمان, as «گواهی صادرشده» reports it. */
@Immutable
data class SettlementCertificatePR(
    /** Persian digits, dashed when the service sent none. */
    val number: String = "",
    /** `۱۴۰۲/۰۱/۰۳`, dashed when the service sent none. */
    val date: String = "",
)

/** One مبنای محاسباتی row, and the documents its detail screen lists. */
@Immutable
data class ComputationalBasePR(
    /** شمارهٔ سند, Persian digits — also this row's identity within a پیمان. */
    val letterNumber: String = "",
    /** تاریخ ارسال, `۱۴۰۲/۰۷/۰۳`. */
    val sendDate: String = "",
    /** مبلغ with «ریال», grouped and in Persian digits. */
    val amount: String = "",
    /** The same amount in rials, for adding the bases up; null when the service sent none. */
    val amountRials: Long? = null,
    /** Where the base's period starts, `۱۴۰۲/۰۱/۰۱`; blank when the service sent none. */
    val periodStart: String = "",
    /** Where it ends; blank when the service sent none. */
    val periodEnd: String = "",
    /** How many documents are attached, in Persian digits — the row prints «N سند». */
    val documentCount: String = "",
    val documents: ImmutableList<BaseDocumentPR> = persistentListOf(),
    /** The workflow stage; null when the service sent a code the table does not hold. */
    val status: ComputationalBaseStatus? = null,
    /** شمارهٔ برگهٔ پرداخت بدهی قطعی, Persian digits; dashed until one is issued. */
    val finalOrderNumber: String = "",
    /** شمارهٔ برگهٔ پرداخت بدهی برآوردی, Persian digits; dashed until one is issued. */
    val estimatedOrderNumber: String = "",
)

/**
 * Where a مبنای محاسباتی stands in the workflow, keyed by the `status` the service sends.
 *
 * One table, as [BaseDocumentCategory] is: the code and its wording are columns of the same row. The
 * seventeen codes and their wording are the old app's own (`ComputationalBaseResponse.statusDescription`),
 * the only place they are written down.
 */
enum class ComputationalBaseStatus(val code: String, val title: StringResource) {
    SMS_SENT("01", Res.string.assigner_base_status_01),
    BASES_REGISTERED("02", Res.string.assigner_base_status_02),
    CONFIRMED("03", Res.string.assigner_base_status_03),
    REJECTED("04", Res.string.assigner_base_status_04),
    FACTOR_SET("05", Res.string.assigner_base_status_05),
    CALCULATED("06", Res.string.assigner_base_status_06),
    FORM_ONE_ISSUED("07", Res.string.assigner_base_status_07),
    FORM_ONE_SERVED("08", Res.string.assigner_base_status_08),
    FORM_TWO_ISSUED("09", Res.string.assigner_base_status_09),
    FORM_TWO_SERVED("10", Res.string.assigner_base_status_10),
    FINAL_ORDER_ISSUED("11", Res.string.assigner_base_status_11),
    ESTIMATED_ORDER_ISSUED("12", Res.string.assigner_base_status_12),
    FINAL_DEBT_COLLECTED("13", Res.string.assigner_base_status_13),
    ESTIMATED_DEBT_COLLECTED("14", Res.string.assigner_base_status_14),
    BOTH_DEBTS_COLLECTED("15", Res.string.assigner_base_status_15),
    SETTLEMENT_ISSUED("16", Res.string.assigner_base_status_16),
    SETTLEMENT_SERVED("17", Res.string.assigner_base_status_17);

    companion object {
        /**
         * The stage [code] names, or null for a code outside the table — the old app prints a dash
         * then, and so does the detail screen. A code that arrives as a bare number (`1`) is read as
         * its two-digit form, since the lenient decoder hands it over without the leading zero.
         */
        fun fromCode(code: String?): ComputationalBaseStatus? {
            val normalized = code?.trim()?.padStart(CODE_LENGTH, '0') ?: return null
            return entries.firstOrNull { it.code == normalized }
        }

        private const val CODE_LENGTH = 2
    }
}

/**
 * One attached document.
 *
 * The service sends no file name, so [category] *is* the name — the old app groups these four
 * codes under four fixed headings and titles each attachment by its group.
 */
@Immutable
data class BaseDocumentPR(
    val documentId: String = "",
    val kind: BaseDocumentKind = BaseDocumentKind.PDF,
    val category: BaseDocumentCategory = BaseDocumentCategory.OTHER,
)

/**
 * What a document is called, keyed by the `documentCode` the service sends.
 *
 * One table: the code and the heading are columns of the same row, so a renamed heading cannot
 * drift away from the code it belongs to. The four codes and their wording are the old app's own
 * (`ContractInfoViewModel.getComputationalBaseList`), which is the only place they are written
 * down — the service sends the digit and nothing else.
 */
enum class BaseDocumentCategory(val code: String, val title: StringResource) {
    /**
     * Identified by its code alone. The old app also required `documentType == "1"` for this
     * heading, which dropped a letter filed as a PDF from every section; it is shown here instead,
     * opened by the viewer its type calls for. `AssignerUiMapperTest` pins the choice.
     */
    LETTER("1", Res.string.settlement_doc_letter),
    SUBCONTRACTOR("2", Res.string.settlement_doc_subcontractor),
    SUPPLEMENT("3", Res.string.settlement_doc_supplement),
    FINAL_STATUS("4", Res.string.settlement_doc_final_status),

    /**
     * A code outside the four the old app groups.
     *
     * Named rather than dropped: the old app's four filters silently swallow anything else, and a
     * document the user cannot see is worse than one with a generic heading.
     */
    OTHER("", Res.string.assigner_doc_category_other);

    companion object {
        fun fromCode(code: String?): BaseDocumentCategory =
            entries.firstOrNull { it.code == code } ?: OTHER
    }
}

/** «تصویر» or «PDF», the kind label the document row prints under its name. */
val BaseDocumentKind.label: StringResource
    get() = when (this) {
        BaseDocumentKind.IMAGE -> Res.string.assigner_doc_kind_image
        BaseDocumentKind.PDF -> Res.string.assigner_doc_kind_pdf
    }
