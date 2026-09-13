package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.AssignerContractsViewModel
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.findContract
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopDocumentsPanel
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFieldSlot
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormBanner
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormFooter
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormSection
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormStep
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopReviewGroup
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopReviewRow
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopScreenShell
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopStepper
import com.tamin.taminhamrah.feature.workshops.ui.model.SETTLEMENT_MAX_DOCUMENTS
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachment
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopDocumentType
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.workshop.AssignerContractDN
import com.tamin.taminhamrah.model.workshop.AssignerContractPR
import com.tamin.taminhamrah.model.workshop.AssignerPartyDN
import com.tamin.taminhamrah.feature.workshops.ui.model.SettlementDocumentTypesWithSubcontractor
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.BackHandler
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.components.TaminSegmentedTabs
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminOptionSheetItem
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.components.toast.success
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.assigner_contract_date
import taminx.core.core_ui.assigner_empty_no_search_body
import taminx.core.core_ui.assigner_empty_no_search_title
import taminx.core.core_ui.assigner_field_contract_number
import taminx.core.core_ui.assigner_field_contract_row
import taminx.core.core_ui.assigner_field_contract_subject
import taminx.core.core_ui.ic_tamin_assigner_contracts
import taminx.core.core_ui.settlement_amount
import taminx.core.core_ui.settlement_amount_hint
import taminx.core.core_ui.settlement_contractor_name
import taminx.core.core_ui.settlement_contractor_workshop
import taminx.core.core_ui.settlement_currency_amount
import taminx.core.core_ui.settlement_currency_in_rial
import taminx.core.core_ui.settlement_end_date
import taminx.core.core_ui.settlement_foreign_exchange_note
import taminx.core.core_ui.settlement_has_subcontractor
import taminx.core.core_ui.settlement_letter_date
import taminx.core.core_ui.settlement_letter_number
import taminx.core.core_ui.settlement_letter_section
import taminx.core.core_ui.settlement_no
import taminx.core.core_ui.settlement_selected_contract
import taminx.core.core_ui.settlement_start_date
import taminx.core.core_ui.settlement_step_documents
import taminx.core.core_ui.settlement_submit
import taminx.core.core_ui.settlement_submitted
import taminx.core.core_ui.settlement_title
import taminx.core.core_ui.settlement_total
import taminx.core.core_ui.settlement_yes
import taminx.core.core_ui.ws_form_next

/**
 * درخواست مفاصاحساب for one پیمان.
 *
 * The پیمان is found in the list this screen was opened from, by the two keys its route carries —
 * the way جزئیات پیمان finds it — so nothing is fetched to show it and no stale selection can be
 * drawn. It is null only after process death, which the screen says rather than offering a form with
 * no پیمان behind it.
 */
@Composable
fun SettlementRequestScreen(
    listViewModel: AssignerContractsViewModel,
    contractRow: String,
    contractSequence: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettlementRequestViewModel = koinViewModel(),
) {
    val listState by listViewModel.uiState.collectAsStateWithLifecycle()
    val contracts = listState.list.items
    val contract = remember(contracts, contractRow, contractSequence) {
        contracts.findContract(contractRow, contractSequence)
    }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(contract) {
        if (contract != null) viewModel.sendIntent(SettlementRequestIntent.Open(contract))
    }
    HandleSettlementRequestEvents(events = viewModel.events, onSubmitted = onBack)

    SettlementRequestContent(
        contract = contract,
        state = state,
        onIntent = viewModel::sendIntent,
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
private fun HandleSettlementRequestEvents(
    events: Flow<SettlementRequestEvent>,
    onSubmitted: () -> Unit,
) {
    val toaster = LocalToaster.current
    val latestOnSubmitted by rememberUpdatedState(onSubmitted)
    LaunchedEffect(events, toaster) {
        events.collect { event ->
            when (event) {
                is SettlementRequestEvent.ShowServerMessage -> toaster.error(event.message)
                SettlementRequestEvent.Submitted -> {
                    toaster.success(getString(Res.string.settlement_submitted))
                    latestOnSubmitted()
                }
            }
        }
    }
}

/**
 * The form, stateless.
 *
 * [contract] is taken apart from [state] so the first frame already draws the پیمان, before the
 * ViewModel has been told which one it is.
 */
@Composable
fun SettlementRequestContent(
    contract: AssignerContractPR?,
    state: SettlementRequestUiState,
    onIntent: (SettlementRequestIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val step = state.step
    // The system back gesture walks the steps as «مرحلهٔ قبل» does, and leaves only from the first.
    BackHandler(enabled = step != SettlementStep.CONTRACT) {
        onIntent(SettlementRequestIntent.Previous)
    }

    WorkshopScreenShell(
        title = stringResource(Res.string.settlement_title),
        onBack = onBack,
        workshopName = contract?.card?.name,
        workshopCode = contract?.card?.workshopCodeLabel,
        modifier = modifier,
    ) {
        if (contract == null) {
            EmptyStateMessage(
                icon = vectorResource(Res.drawable.ic_tamin_assigner_contracts),
                title = stringResource(Res.string.assigner_empty_no_search_title),
                subtitle = stringResource(Res.string.assigner_empty_no_search_body),
                showIconTile = true,
            )
            return@WorkshopScreenShell
        }

        SettlementStepper(step = step)

        val scrollState = rememberScrollState()
        // Each step opens at its own top, not wherever the previous one was left.
        LaunchedEffect(step) { scrollState.scrollTo(0) }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                // A fixed form, not a data list — the scroll is for a tall step on a short screen.
                .verticalScroll(scrollState)
                .padding(horizontal = Spacing.page)
                .padding(top = Spacing.md, bottom = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            when (step) {
                SettlementStep.CONTRACT -> SettlementContractStep(
                    contract = contract,
                    isContractOpen = state.isContractOpen,
                    letterNumber = state.letterNumber,
                    letterDate = state.letterDate,
                    startDate = state.startDate,
                    endDate = state.endDate,
                    hasSubcontractor = state.hasSubcontractor,
                    amount = state.amount,
                    currencyAmount = state.currencyAmount,
                    currencyInRial = state.currencyInRial,
                    errors = state.errors,
                    onIntent = onIntent,
                )

                SettlementStep.DOCUMENTS -> SettlementDocumentsStep(
                    attachments = state.attachments,
                    types = state.documentTypes,
                    isUploading = state.isUploading,
                    isError = SettlementField.DOCUMENTS in state.errors,
                    onIntent = onIntent,
                )

                SettlementStep.TERMS -> {
                    val amount = state.amount
                    val currencyInRial = state.currencyInRial
                    val deduction = state.terms.amount1
                    val remainder = remember(amount, currencyInRial, deduction) {
                        settlementRemainder(amount, currencyInRial, deduction)
                    }
                    SettlementTermsStep(
                        contractor = contract.employer,
                        subjects = state.subjects,
                        isSubjectsLoading = state.isSubjectsLoading,
                        subject = state.subject,
                        form = state.termsForm,
                        terms = state.terms,
                        remainder = remainder,
                        isUploading = state.isUploading,
                        errors = state.errors,
                        onIntent = onIntent,
                    )
                }
            }
        }

        WorkshopFormFooter(
            nextLabel = stringResource(
                if (step == SettlementStep.TERMS) Res.string.settlement_submit else Res.string.ws_form_next,
            ),
            onNext = { onIntent(SettlementRequestIntent.Next) },
            onPrev = if (step == SettlementStep.CONTRACT) {
                null
            } else {
                { onIntent(SettlementRequestIntent.Previous) }
            },
            isBusy = state.isBusy,
        )
    }
}

/** اطلاعات پیمان — مستندات — شرایط قرارداد, with the finished rungs ticked. */
@Composable
private fun SettlementStepper(step: SettlementStep) {
    val labels = SettlementStep.entries.map { stringResource(it.label) }
    val steps = remember(step, labels) {
        SettlementStep.entries.mapIndexed { index, entry ->
            WorkshopFormStep(label = labels[index], isDone = entry < step, isCurrent = entry == step)
        }.toImmutableList()
    }
    WorkshopStepper(steps = steps)
}

/** «پیمان انتخاب‌شده», then the letter the request is filed under and the amounts it declares. */
@Composable
private fun SettlementContractStep(
    contract: AssignerContractPR,
    isContractOpen: Boolean,
    letterNumber: String,
    letterDate: SettlementDate?,
    startDate: SettlementDate?,
    endDate: SettlementDate?,
    hasSubcontractor: Boolean,
    amount: String,
    currencyAmount: String,
    currencyInRial: String,
    errors: kotlinx.collections.immutable.ImmutableMap<SettlementField, StringResource>,
    onIntent: (SettlementRequestIntent) -> Unit,
) {
    WorkshopReviewGroup(
        title = stringResource(Res.string.settlement_selected_contract),
        rows = rememberContractRows(contract),
        isOpen = isContractOpen,
        onToggle = { onIntent(SettlementRequestIntent.ContractToggled) },
    )

    WorkshopFormSection(title = stringResource(Res.string.settlement_letter_section))
    SettlementTextField(
        field = SettlementField.LETTER_NUMBER,
        label = stringResource(Res.string.settlement_letter_number),
        value = letterNumber,
        error = errors[SettlementField.LETTER_NUMBER],
        onIntent = onIntent,
        maxLength = LETTER_NUMBER_MAX_LENGTH,
    )
    SettlementDateField(
        field = SettlementField.LETTER_DATE,
        label = stringResource(Res.string.settlement_letter_date),
        date = letterDate,
        error = errors[SettlementField.LETTER_DATE],
        onIntent = onIntent,
    )
    SettlementDateField(
        field = SettlementField.START_DATE,
        label = stringResource(Res.string.settlement_start_date),
        date = startDate,
        error = errors[SettlementField.START_DATE],
        onIntent = onIntent,
    )
    SettlementDateField(
        field = SettlementField.END_DATE,
        label = stringResource(Res.string.settlement_end_date),
        date = endDate,
        error = errors[SettlementField.END_DATE],
        onIntent = onIntent,
        // A پیمان's operations can end after today; the old app opened this one picker 100 years out.
        allowFuture = true,
    )

    WorkshopFieldSlot(label = stringResource(Res.string.settlement_has_subcontractor)) {
        TaminSegmentedTabs(
            options = YesNo,
            selected = hasSubcontractor,
            onSelect = { onIntent(SettlementRequestIntent.SubcontractorChanged(it)) },
            label = { stringResource(if (it) Res.string.settlement_yes else Res.string.settlement_no) },
        )
    }

    SettlementTextField(
        field = SettlementField.AMOUNT,
        label = stringResource(Res.string.settlement_amount),
        value = amount,
        error = errors[SettlementField.AMOUNT],
        onIntent = onIntent,
        placeholder = stringResource(Res.string.settlement_amount_hint),
    )
    WorkshopFormBanner(text = stringResource(Res.string.settlement_foreign_exchange_note))
    SettlementTextField(
        field = SettlementField.CURRENCY_AMOUNT,
        label = stringResource(Res.string.settlement_currency_amount),
        value = currencyAmount,
        error = null,
        onIntent = onIntent,
        isRequired = false,
    )
    SettlementTextField(
        field = SettlementField.CURRENCY_IN_RIAL,
        label = stringResource(Res.string.settlement_currency_in_rial),
        value = currencyInRial,
        error = errors[SettlementField.CURRENCY_IN_RIAL],
        onIntent = onIntent,
        isRequired = false,
    )
    SettlementTotal(amount = amount, currencyInRial = currencyInRial)
}

/** The six cells the design's «پیمان انتخاب‌شده» card lists, rebuilt only when the پیمان changes. */
@Composable
private fun rememberContractRows(contract: AssignerContractPR): ImmutableList<WorkshopReviewRow> {
    val row = stringResource(Res.string.assigner_field_contract_row)
    val number = stringResource(Res.string.assigner_field_contract_number)
    val date = stringResource(Res.string.assigner_contract_date)
    val subject = stringResource(Res.string.assigner_field_contract_subject)
    val contractorName = stringResource(Res.string.settlement_contractor_name)
    val contractorWorkshop = stringResource(Res.string.settlement_contractor_workshop)
    return remember(contract, row, number, date, subject, contractorName, contractorWorkshop) {
        persistentListOf(
            WorkshopReviewRow(row, contract.card.rowLabel),
            WorkshopReviewRow(number, contract.contractNumber),
            WorkshopReviewRow(date, contract.contractDate),
            WorkshopReviewRow(subject, contract.contractSubject, isNumeric = false),
            WorkshopReviewRow(contractorName, contract.employer.workshopName, isNumeric = false),
            WorkshopReviewRow(contractorWorkshop, contract.employer.workshopCode),
        )
    }
}

/** A date the request needs, picked on the app's Jalali wheels. */
@Composable
private fun SettlementDateField(
    field: SettlementField,
    label: String,
    date: SettlementDate?,
    error: StringResource?,
    onIntent: (SettlementRequestIntent) -> Unit,
    allowFuture: Boolean = false,
) {
    var isPicking by rememberSaveable { mutableStateOf(false) }
    SettlementChoiceField(
        label = label,
        value = date?.label,
        error = error,
        onClick = { isPicking = true },
        isDate = true,
    )
    if (isPicking) {
        TaminJalaliDatePicker(
            title = label,
            onDismiss = { isPicking = false },
            onConfirm = { year, month, day ->
                isPicking = false
                onIntent(SettlementRequestIntent.DateChanged(field, SettlementDate(year, month, day)))
            },
            initial = date?.let { Triple(it.year, it.month, it.day) } ?: PersianDateFormatter.today(),
            allowFuture = allowFuture,
        )
    }
}

/** «مجموع کل ناخالص کارکرد», worked out as the two rial amounts are typed. */
@Composable
private fun SettlementTotal(amount: String, currencyInRial: String) {
    val total = remember(amount, currencyInRial) {
        ((amount.toLongOrNull() ?: 0L) + (currencyInRial.toLongOrNull() ?: 0L)).toPriceFormat()
    }
    SettlementFigure(text = stringResource(Res.string.settlement_total, total))
}

/** The request's evidence, as images or PDFs, each filed under one of the four headings. */
@Composable
private fun SettlementDocumentsStep(
    attachments: ImmutableList<WorkshopAttachment>,
    types: ImmutableList<WorkshopDocumentType>,
    isUploading: Boolean,
    isError: Boolean,
    onIntent: (SettlementRequestIntent) -> Unit,
) {
    WorkshopFormSection(title = stringResource(Res.string.settlement_step_documents))
    WorkshopDocumentsPanel(
        attachments = attachments,
        types = types,
        capacity = SETTLEMENT_MAX_DOCUMENTS,
        onAdd = { fileName, bytes, typeCode ->
            onIntent(SettlementRequestIntent.AddDocument(fileName, bytes, typeCode))
        },
        onRemove = { onIntent(SettlementRequestIntent.RemoveDocument(it)) },
        isUploading = isUploading,
        isError = isError,
        acceptsPdf = true,
    )
}

/** بله first, so the RTL row puts it on the right, where the old app's radio group had it. */
private val YesNo: ImmutableList<Boolean> = persistentListOf(true, false)

private const val LETTER_NUMBER_MAX_LENGTH = 20

// ------------------------------------------------------------------------------- previews

/** Run through the real mapper, so the preview's digits and dates are the app's own formatting. */
private val PreviewContract = AssignerContractDN(
    contractRow = "02100001",
    contractSequence = "01",
    branchCode = "0210",
    contractNumber = "44122",
    contractDate = "14010210",
    contractSubject = "خدمات نظافت و پشتیبانی",
    employer = AssignerPartyDN(
        workshopId = "0082810145",
        workshopName = "دبستان کارن ۲ مجتبی غلامیان",
        nationalId = "1022334455",
        address = "بجنورد، خیابان طالقانی، کوچهٔ ۱۲",
        branchCode = "0210",
    ),
).toPresentation()

private val PreviewFilled = SettlementRequestUiState(
    contract = PreviewContract,
    letterNumber = "140312",
    letterDate = SettlementDate(1403, 1, 20),
    startDate = SettlementDate(1401, 2, 10),
    endDate = SettlementDate(1402, 12, 29),
    hasSubcontractor = true,
    amount = "1250000000",
    currencyAmount = "1200",
    currencyInRial = "84000000",
)

@PreviewRtlTheme
@Composable
private fun SettlementContractStepPreview() = PreviewRtlThemeContent {
    SettlementRequestContent(contract = PreviewContract, state = PreviewFilled, onIntent = {}, onBack = {})
}

/** «مرحلهٔ بعد» pressed on an empty form — every error comes from the real rules. */
@PreviewRtlTheme
@Composable
private fun SettlementContractStepErrorsPreview() = PreviewRtlThemeContent {
    val empty = SettlementRequestUiState(contract = PreviewContract, currencyAmount = "1200")
    SettlementRequestContent(
        contract = PreviewContract,
        state = empty.copy(errors = empty.errorsOf(SettlementStep.CONTRACT)),
        onIntent = {},
        onBack = {},
    )
}

@PreviewRtlTheme
@Composable
private fun SettlementDocumentsStepPreview() = PreviewRtlThemeContent {
    SettlementRequestContent(
        contract = PreviewContract,
        state = PreviewFilled.copy(
            step = SettlementStep.DOCUMENTS,
            attachments = persistentListOf(
                WorkshopAttachment("guid-1", SettlementDocumentTypesWithSubcontractor[0], "۲۴۸"),
                WorkshopAttachment("pdf-1", SettlementDocumentTypesWithSubcontractor[3], "۱٬۱۰۲", isPdf = true),
            ),
        ),
        onIntent = {},
        onBack = {},
    )
}

/** Subject 04, with the drivers' share taken out of the gross amount. */
@PreviewRtlTheme
@Composable
private fun SettlementTermsDriversPreview() = PreviewRtlThemeContent {
    SettlementRequestContent(
        contract = PreviewContract,
        state = PreviewFilled.copy(
            step = SettlementStep.TERMS,
            subject = TaminOptionSheetItem(id = "04", label = "پیمان‌های حمل و نقل"),
            terms = SettlementTerms(amount1 = "300000000"),
        ),
        onIntent = {},
        onBack = {},
    )
}

/** Subject 11 — the پیمانکار's own address and id, then four costs. */
@PreviewRtlTheme
@Composable
private fun SettlementTermsBuildCostsPreview() = PreviewRtlThemeContent {
    SettlementRequestContent(
        contract = PreviewContract,
        state = PreviewFilled.copy(
            step = SettlementStep.TERMS,
            subject = TaminOptionSheetItem(id = "11", label = "ساخت، حمل و نصب تجهیزات"),
            isSubmitting = true,
        ),
        onIntent = {},
        onBack = {},
    )
}

/** After process death, with the list gone. */
@PreviewRtlTheme
@Composable
private fun SettlementMissingContractPreview() = PreviewRtlThemeContent {
    SettlementRequestContent(contract = null, state = SettlementRequestUiState(), onIntent = {}, onBack = {})
}
