package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.workshops.ui.model.SettlementDocumentTypes
import com.tamin.taminhamrah.feature.workshops.ui.model.SettlementDocumentTypesWithSubcontractor
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachment
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopDocumentType
import com.tamin.taminhamrah.model.workshop.AssignerContractPR
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminOptionSheetItem
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.ValidationUtils
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.abs_form_err_required
import taminx.core.core_ui.settlement_err_amount_required
import taminx.core.core_ui.settlement_err_build_sum
import taminx.core.core_ui.settlement_err_currency_rial
import taminx.core.core_ui.settlement_err_date_order
import taminx.core.core_ui.settlement_err_dates_required
import taminx.core.core_ui.settlement_err_documents
import taminx.core.core_ui.settlement_err_drivers
import taminx.core.core_ui.settlement_err_equipment
import taminx.core.core_ui.settlement_err_invalid
import taminx.core.core_ui.settlement_err_letter_number_invalid
import taminx.core.core_ui.settlement_err_letter_number_required
import taminx.core.core_ui.settlement_err_subcontractor
import taminx.core.core_ui.settlement_err_subject_required
import taminx.core.core_ui.settlement_err_terms_incomplete
import taminx.core.core_ui.settlement_owner_budget_type
import taminx.core.core_ui.settlement_owner_contractor
import taminx.core.core_ui.settlement_owner_employer
import taminx.core.core_ui.settlement_step_contract
import taminx.core.core_ui.settlement_step_documents
import taminx.core.core_ui.settlement_step_letter
import taminx.core.core_ui.settlement_step_terms
import taminx.core.core_ui.settlement_supply_assigner
import taminx.core.core_ui.settlement_supply_contractor
import taminx.core.core_ui.settlement_supply_shared
import taminx.core.core_ui.ws_form_err_docs

/**
 * درخواست مفاصاحساب — the old app's four-step `MafasaHesabRegisterFragment`, step for step, which is
 * also how the design lays it out.
 *
 * Each step's rules are the old app's, made binding where it only printed an error and let the user
 * carry on. Four go further than the old app did, as the design asks: the subcontractor question has
 * to be answered, subjects 01 and 29 need their image, and subject 11's four costs may not add up to
 * more than the gross amount.
 */
@Immutable
data class SettlementRequestUiState(
    /** The پیمان being settled — what the request's id is built from. */
    val contract: AssignerContractPR? = null,
    val step: SettlementStep = SettlementStep.CONTRACT,
    val isContractOpen: Boolean = true,
    /** ASCII digits. */
    val letterNumber: String = "",
    val letterDate: SettlementDate? = null,
    val startDate: SettlementDate? = null,
    val endDate: SettlementDate? = null,
    /** Null until the user answers — there is no default answer to «پیمانکار جزء». */
    val hasSubcontractor: Boolean? = null,
    /** Rials, ASCII digits — as are the two below. */
    val amount: String = "",
    val currencyAmount: String = "",
    val currencyInRial: String = "",
    val attachments: PersistentList<WorkshopAttachment> = persistentListOf(),
    val subjects: ImmutableList<TaminOptionSheetItem> = persistentListOf(),
    val isSubjectsLoading: Boolean = false,
    val subject: TaminOptionSheetItem? = null,
    val terms: SettlementTerms = SettlementTerms(),
    /**
     * What is stopping the current step, keyed to the control that draws it. Empty until the user
     * tries to go on — an incomplete form is not an error while it is still being filled in.
     */
    val errors: PersistentMap<SettlementField, StringResource> = persistentMapOf(),
    val isUploading: Boolean = false,
    val isSubmitting: Boolean = false,
    /** Filed. The screen answers with its confirmation, which is what leaves the form. */
    val isSubmitted: Boolean = false,
) {
    val termsForm: SettlementTermsForm get() = SettlementTermsForm.of(subject?.id)

    /** «مستندات لیست فهرست» is only offered once the پیمانکار says subcontractors were used. */
    val documentTypes: ImmutableList<WorkshopDocumentType>
        get() = if (hasSubcontractor == true) SettlementDocumentTypesWithSubcontractor else SettlementDocumentTypes

    val isBusy: Boolean get() = isUploading || isSubmitting

    sealed interface PartialState {
        data class Opened(val contract: AssignerContractPR) : PartialState
        data object ContractToggled : PartialState
        data class FieldChanged(val field: SettlementField, val value: String) : PartialState
        data class DateChanged(val field: SettlementField, val date: SettlementDate) : PartialState
        data class SubcontractorChanged(val hasSubcontractor: Boolean) : PartialState
        data object SubjectsLoading : PartialState
        data class SubjectsLoaded(val subjects: ImmutableList<TaminOptionSheetItem>) : PartialState
        data class SubjectSelected(val subject: TaminOptionSheetItem) : PartialState
        data class UploadingChanged(val isUploading: Boolean) : PartialState
        data class AttachmentAdded(
            val attachment: WorkshopAttachment,
            val isSubjectImage: Boolean,
        ) : PartialState

        data class DocumentRemoved(val index: Int) : PartialState
        data object SubjectImageRemoved : PartialState
        data class StepChanged(val step: SettlementStep) : PartialState
        data class Rejected(val errors: PersistentMap<SettlementField, StringResource>) : PartialState
        data class SubmittingChanged(val isSubmitting: Boolean) : PartialState
        data object Submitted : PartialState
        data object Failed : PartialState
    }
}

/** A Jalali day as the pickers hand it back. */
@Immutable
data class SettlementDate(val year: Int, val month: Int, val day: Int) {
    /** `۱۴۰۳/۰۱/۲۰`, what the field prints. */
    val label: String get() = PersianDateFormatter.format(year, month, day)
}

/** The four steps, in order, titled as the old app and the design both title them. */
enum class SettlementStep(val label: StringResource) {
    CONTRACT(Res.string.settlement_step_contract),
    LETTER(Res.string.settlement_step_letter),
    DOCUMENTS(Res.string.settlement_step_documents),
    TERMS(Res.string.settlement_step_terms),
}

/** Every input that can be changed or be wrong, so an error lands on the control that draws it. */
enum class SettlementField {
    LETTER_NUMBER, LETTER_DATE, START_DATE, END_DATE, SUBCONTRACTOR, AMOUNT, CURRENCY_AMOUNT,
    CURRENCY_IN_RIAL, DOCUMENTS, SUBJECT, OWNER, TEXT1, TEXT2, AMOUNT1, AMOUNT2, AMOUNT3, AMOUNT4,
    SUBJECT_IMAGE,
}

/**
 * Which conditions a موضوع کار asks for, keyed by its code — the old app's
 * `setUiVisibilityCordingContractSubject`. Every code it does not list asks for nothing more; 13
 * draws a yes/no there that is never read or sent, so it asks for nothing here either.
 */
enum class SettlementTermsForm {
    /** 01 — who supplies the materials, the project's credit line, budget row and premium paid. */
    PRICE_LIST,

    /** 02 — who supplies the materials, and the واگذارنده's share when it is split. */
    MATERIALS_SUPPLY,

    /** 03 — the mechanical share; the manual share is what is left of 100. */
    MECHANICAL_SHARE,

    /** 04, 05, 06 — the drivers' work, taken out of the gross amount. */
    DRIVERS,

    /** 07 — equipment bought, taken out of the gross amount. */
    EQUIPMENT,

    /** 11 — the four costs of building, carrying, installing and running. */
    BUILD_COSTS,

    /** 29 — foreign equipment, in currency and in rials. */
    FOREIGN_EQUIPMENT,
    NONE;

    companion object {
        fun of(subjectCode: String?): SettlementTermsForm = when (subjectCode) {
            "01" -> PRICE_LIST
            "02" -> MATERIALS_SUPPLY
            "03" -> MECHANICAL_SHARE
            "04", "05", "06" -> DRIVERS
            "07" -> EQUIPMENT
            "11" -> BUILD_COSTS
            "29" -> FOREIGN_EQUIPMENT
            else -> NONE
        }
    }
}

/** One answer a conditions picker offers: the code the service files and the wording shown. */
@Immutable
data class SettlementOption(val code: String, val label: StringResource)

/** «انعقاد قرارداد (تهیه مصالح به عهده)» for subject 01, in the old app's order. */
val PriceListOwnerOptions: ImmutableList<SettlementOption> = persistentListOf(
    SettlementOption("1", Res.string.settlement_owner_contractor),
    SettlementOption("2", Res.string.settlement_owner_employer),
    SettlementOption("3", Res.string.settlement_owner_budget_type),
)

/** «تهیه و تأمین مصالح مصرفی به عهده» for subject 02, in the old app's order. */
val SupplyOwnerOptions: ImmutableList<SettlementOption> = persistentListOf(
    SettlementOption("1", Res.string.settlement_supply_contractor),
    SettlementOption("2", Res.string.settlement_supply_assigner),
    SettlementOption(SHARED_SUPPLY_CODE, Res.string.settlement_supply_shared),
)

/** The one supply answer that also asks for the value of the واگذارنده's materials. */
const val SHARED_SUPPLY_CODE = "3"

/** The conditions typed so far — ASCII digits for amounts. Cleared whenever the subject changes. */
@Immutable
data class SettlementTerms(
    val owner: String = "",
    val text1: String = "",
    val text2: String = "",
    val amount1: String = "",
    val amount2: String = "",
    val amount3: String = "",
    val amount4: String = "",
    /** The conditions' own image, subjects 01 and 29 — at most one. */
    val image: PersistentList<WorkshopAttachment> = persistentListOf(),
)

/**
 * The gross amount less [deduction] — the second amount subjects 04–07 send. Computed rather than
 * stored, so changing an amount on the letter step can never leave it stale.
 */
fun settlementRemainder(amount: String, currencyInRial: String, deduction: String): Long =
    settlementGross(amount, currencyInRial) - (deduction.toLongOrNull() ?: 0L)

/** مبلغ ناخالص کارکرد plus the rial value of the currency part — what every guard is measured against. */
fun settlementGross(amount: String, currencyInRial: String): Long =
    (amount.toLongOrNull() ?: 0L) + (currencyInRial.toLongOrNull() ?: 0L)

sealed interface SettlementRequestIntent {
    data class Open(val contract: AssignerContractPR) : SettlementRequestIntent
    data object ContractToggled : SettlementRequestIntent
    data class FieldChanged(val field: SettlementField, val value: String) : SettlementRequestIntent
    data class DateChanged(val field: SettlementField, val date: SettlementDate) : SettlementRequestIntent
    data class SubcontractorChanged(val hasSubcontractor: Boolean) : SettlementRequestIntent
    data object LoadSubjects : SettlementRequestIntent
    data class SubjectSelected(val subject: TaminOptionSheetItem) : SettlementRequestIntent

    /** A picked file, with the heading the user filed it under. */
    class AddDocument(val fileName: String, val bytes: ByteArray, val typeCode: String) :
        SettlementRequestIntent

    data class RemoveDocument(val index: Int) : SettlementRequestIntent
    class AddSubjectImage(val fileName: String, val bytes: ByteArray) : SettlementRequestIntent
    data object RemoveSubjectImage : SettlementRequestIntent

    /** The footer: checks the step, then moves on — or, on the last one, files the request. */
    data object Next : SettlementRequestIntent
    data object Previous : SettlementRequestIntent

    /** A tapped segment of the progress bar; only a step already passed can be gone back to. */
    data class StepSelected(val step: SettlementStep) : SettlementRequestIntent
}

sealed interface SettlementRequestEvent {
    /** Something the service refused, in its own words. */
    data class ShowServerMessage(val message: String) : SettlementRequestEvent
}

/** The old app's check on شماره نامه: anything shorter was reported as wrong. */
internal const val MIN_LETTER_NUMBER_LENGTH = 5

/**
 * What stops [step] from being left.
 *
 * Kept a pure function of the state, so the screen, the ViewModel and a test all read the same rules.
 */
internal fun SettlementRequestUiState.errorsOf(
    step: SettlementStep,
): PersistentMap<SettlementField, StringResource> {
    val required = Res.string.abs_form_err_required
    val errors = persistentMapOf<SettlementField, StringResource>().builder()
    fun requireFilled(field: SettlementField, value: String) {
        if (value.isBlank()) errors[field] = required
    }

    when (step) {
        // Read back to the user, not asked for.
        SettlementStep.CONTRACT -> Unit

        SettlementStep.LETTER -> {
            when {
                letterNumber.isBlank() -> errors[SettlementField.LETTER_NUMBER] = required
                letterNumber.length < MIN_LETTER_NUMBER_LENGTH ->
                    errors[SettlementField.LETTER_NUMBER] = Res.string.settlement_err_invalid
            }
            if (letterDate == null) errors[SettlementField.LETTER_DATE] = required
            if (startDate == null) errors[SettlementField.START_DATE] = required
            when {
                endDate == null -> errors[SettlementField.END_DATE] = required
                !ValidationUtils.isDateRangeValid(startDate?.epochMillis(), endDate.epochMillis()) ->
                    errors[SettlementField.END_DATE] = Res.string.settlement_err_date_order
            }
            when {
                amount.isBlank() -> errors[SettlementField.AMOUNT] = required
                amount.toLongOrNull() == 0L -> errors[SettlementField.AMOUNT] = Res.string.settlement_err_invalid
            }
            if ((currencyAmount.toLongOrNull() ?: 0L) > 0 && (currencyInRial.toLongOrNull() ?: 0L) == 0L) {
                errors[SettlementField.CURRENCY_IN_RIAL] = Res.string.settlement_err_currency_rial
            }
            if (hasSubcontractor == null) {
                errors[SettlementField.SUBCONTRACTOR] = Res.string.settlement_err_subcontractor
            }
        }

        SettlementStep.DOCUMENTS ->
            if (attachments.isEmpty()) errors[SettlementField.DOCUMENTS] = Res.string.ws_form_err_docs

        SettlementStep.TERMS -> {
            if (subject == null) errors[SettlementField.SUBJECT] = required
            when (termsForm) {
                SettlementTermsForm.PRICE_LIST -> {
                    requireFilled(SettlementField.OWNER, terms.owner)
                    requireFilled(SettlementField.TEXT1, terms.text1)
                    requireFilled(SettlementField.TEXT2, terms.text2)
                    requireFilled(SettlementField.AMOUNT1, terms.amount1)
                    if (terms.image.isEmpty()) errors[SettlementField.SUBJECT_IMAGE] = required
                }

                SettlementTermsForm.MATERIALS_SUPPLY -> {
                    requireFilled(SettlementField.OWNER, terms.owner)
                    if (terms.owner == SHARED_SUPPLY_CODE) requireFilled(SettlementField.AMOUNT1, terms.amount1)
                }

                SettlementTermsForm.MECHANICAL_SHARE -> requireFilled(SettlementField.TEXT1, terms.text1)

                SettlementTermsForm.DRIVERS, SettlementTermsForm.EQUIPMENT -> when {
                    terms.amount1.isBlank() -> errors[SettlementField.AMOUNT1] = required
                    settlementRemainder(amount, currencyInRial, terms.amount1) <= 0 ->
                        errors[SettlementField.AMOUNT1] = if (termsForm == SettlementTermsForm.DRIVERS) {
                            Res.string.settlement_err_drivers
                        } else {
                            Res.string.settlement_err_equipment
                        }
                }

                SettlementTermsForm.BUILD_COSTS -> {
                    requireFilled(SettlementField.AMOUNT1, terms.amount1)
                    requireFilled(SettlementField.AMOUNT2, terms.amount2)
                    requireFilled(SettlementField.AMOUNT3, terms.amount3)
                    requireFilled(SettlementField.AMOUNT4, terms.amount4)
                    val costs = listOf(terms.amount1, terms.amount2, terms.amount3, terms.amount4)
                        .sumOf { it.toLongOrNull() ?: 0L }
                    if (SettlementField.AMOUNT1 !in errors && costs > settlementGross(amount, currencyInRial)) {
                        errors[SettlementField.AMOUNT1] = Res.string.settlement_err_build_sum
                    }
                }

                SettlementTermsForm.FOREIGN_EQUIPMENT -> {
                    requireFilled(SettlementField.AMOUNT1, terms.amount1)
                    requireFilled(SettlementField.AMOUNT2, terms.amount2)
                    if (terms.image.isEmpty()) errors[SettlementField.SUBJECT_IMAGE] = required
                }

                SettlementTermsForm.NONE -> Unit
            }
        }
    }
    return errors.build()
}

/** The share the مکانیکی and دستی percentages divide between them. */
internal const val SETTLEMENT_FULL_SHARE = 100

/**
 * The one line the footer prints when a step is refused: the design's own sentence for the first
 * problem, in the order the design checks them. The fields themselves only turn red.
 */
internal fun SettlementRequestUiState.bannerError(): StringResource? {
    if (errors.isEmpty()) return null
    val required = Res.string.abs_form_err_required
    return when (step) {
        SettlementStep.CONTRACT -> null
        SettlementStep.LETTER -> when {
            SettlementField.LETTER_NUMBER in errors ->
                if (errors[SettlementField.LETTER_NUMBER] == required) {
                    Res.string.settlement_err_letter_number_required
                } else {
                    Res.string.settlement_err_letter_number_invalid
                }

            DateFields.any { errors[it] == required } -> Res.string.settlement_err_dates_required
            SettlementField.END_DATE in errors -> errors[SettlementField.END_DATE]
            SettlementField.AMOUNT in errors -> Res.string.settlement_err_amount_required
            SettlementField.CURRENCY_IN_RIAL in errors -> errors[SettlementField.CURRENCY_IN_RIAL]
            else -> errors[SettlementField.SUBCONTRACTOR] ?: errors.values.first()
        }

        SettlementStep.DOCUMENTS -> Res.string.settlement_err_documents
        SettlementStep.TERMS -> when {
            SettlementField.SUBJECT in errors -> Res.string.settlement_err_subject_required
            errors.values.any { it == required } -> Res.string.settlement_err_terms_incomplete
            else -> errors.values.first()
        }
    }
}

private val DateFields = listOf(SettlementField.LETTER_DATE, SettlementField.START_DATE, SettlementField.END_DATE)

/** Midnight UTC of the day, which is what the range check compares. */
internal fun SettlementDate.epochMillis(): Long = PersianDateFormatter.toEpochMillisUtc(year, month, day)
