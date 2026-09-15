package com.tamin.taminhamrah.model.workshop

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.assigner_doc_category_final_status
import taminx.core.core_ui.assigner_doc_category_letter
import taminx.core.core_ui.assigner_doc_category_other
import taminx.core.core_ui.assigner_doc_category_subcontractor
import taminx.core.core_ui.assigner_doc_category_supplement
import taminx.core.core_ui.assigner_doc_kind_image
import taminx.core.core_ui.assigner_doc_kind_pdf

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
)

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
    LETTER("1", Res.string.assigner_doc_category_letter),
    SUBCONTRACTOR("2", Res.string.assigner_doc_category_subcontractor),
    SUPPLEMENT("3", Res.string.assigner_doc_category_supplement),
    FINAL_STATUS("4", Res.string.assigner_doc_category_final_status),

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
