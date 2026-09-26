package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.AssignerContractsViewModel
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.findContract
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopCardButton
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopCardButtonTone
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopDocumentsPanel
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFieldSlot
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormBanner
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormError
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormFooter
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopHeaderTitle
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopReviewGroup
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopReviewRow
import com.tamin.taminhamrah.feature.workshops.ui.model.SETTLEMENT_MAX_DOCUMENTS
import com.tamin.taminhamrah.feature.workshops.ui.model.SettlementDocumentTypesWithSubcontractor
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachment
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopDocumentType
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.workshop.AssignerContractDN
import com.tamin.taminhamrah.model.workshop.AssignerContractPR
import com.tamin.taminhamrah.model.workshop.AssignerPartyDN
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminHeroStepProgress
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminOptionSheetItem
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.assigner_contract_date
import taminx.core.core_ui.assigner_contract_subtitle
import taminx.core.core_ui.assigner_empty_no_search_body
import taminx.core.core_ui.assigner_empty_no_search_title
import taminx.core.core_ui.assigner_field_contract_number
import taminx.core.core_ui.assigner_field_contract_subject
import taminx.core.core_ui.ic_tamin_assigner_contracts
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.settlement_amount
import taminx.core.core_ui.settlement_amount_rial
import taminx.core.core_ui.settlement_contract_info_note
import taminx.core.core_ui.settlement_contract_row_sequence
import taminx.core.core_ui.settlement_contractor_branch
import taminx.core.core_ui.settlement_contractor_name
import taminx.core.core_ui.settlement_contractor_workshop
import taminx.core.core_ui.settlement_currency_amount
import taminx.core.core_ui.settlement_currency_in_rial
import taminx.core.core_ui.settlement_documents_hint
import taminx.core.core_ui.settlement_done_back
import taminx.core.core_ui.settlement_done_body
import taminx.core.core_ui.settlement_end_date
import taminx.core.core_ui.settlement_foreign_exchange_note
import taminx.core.core_ui.settlement_has_subcontractor
import taminx.core.core_ui.settlement_letter_date
import taminx.core.core_ui.settlement_letter_number
import taminx.core.core_ui.settlement_no
import taminx.core.core_ui.settlement_selected_contract
import taminx.core.core_ui.settlement_start_date
import taminx.core.core_ui.settlement_subcontractor_hint
import taminx.core.core_ui.settlement_submit
import taminx.core.core_ui.settlement_submitted
import taminx.core.core_ui.settlement_title
import taminx.core.core_ui.settlement_total_hint
import taminx.core.core_ui.settlement_total_label
import taminx.core.core_ui.settlement_yes
import taminx.core.core_ui.ws_form_next

/**
 * درخواست مفاصاحساب for one پیمان — the design's four steps under the hero progress bar.
 *
 * The پیمان is found in the list this screen was opened from, by the two keys its route carries —
 * the way جزئیات پیمان finds it — so nothing is fetched to show it and no stale selection can be
 * drawn. It is null only after process death, which the screen says rather than offering a form with
 * no پیمان behind it.
 *
 * @param onDone leaves for the list of پیمان‌ها — the close control, and the confirmation once filed.
 */
@Composable
fun SettlementRequestScreen(
    listViewModel: AssignerContractsViewModel,
    contractRow: String,
    contractSequence: String,
    onBack: () -> Unit,
    onDone: () -> Unit,
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
    HandleSettlementRequestEvents(events = viewModel.events)

    SettlementRequestContent(
        contract = contract,
        state = state,
        onIntent = viewModel::sendIntent,
        onBack = onBack,
        onDone = onDone,
        modifier = modifier,
    )
}

@Composable
private fun HandleSettlementRequestEvents(events: Flow<SettlementRequestEvent>) {
    val toaster = LocalToaster.current
    LaunchedEffect(events, toaster) {
        events.collect { event ->
            when (event) {
                is SettlementRequestEvent.ShowServerMessage -> toaster.error(event.message)
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
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val step = state.step
    val colors = LocalTaminColors.current
    // The documents step's image previews. Up here, outside the steps, so going on to the next step
    // and back does not throw them away with the panel.
    val documentPreviews = remember { mutableStateMapOf<String, ByteArray>() }
    Column(modifier = modifier.fillMaxSize()) {
        SettlementHeader(
            step = step,
            workshopName = contract?.card?.name,
            rowLabel = contract?.card?.rowLabel,
            onIntent = onIntent,
            onBack = onBack,
            onClose = onDone,
        )

        if (contract == null) {
            EmptyStateMessage(
                icon = vectorResource(Res.drawable.ic_tamin_assigner_contracts),
                title = stringResource(Res.string.assigner_empty_no_search_title),
                subtitle = stringResource(Res.string.assigner_empty_no_search_body),
                showIconTile = true,
            )
            return@Column
        }

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
                SettlementStep.CONTRACT -> {
                    WorkshopReviewGroup(
                        title = stringResource(Res.string.settlement_selected_contract),
                        rows = rememberContractRows(contract),
                        isOpen = state.isContractOpen,
                        onToggle = { onIntent(SettlementRequestIntent.ContractToggled) },
                    )
                    WorkshopFormBanner(text = stringResource(Res.string.settlement_contract_info_note))
                }

                SettlementStep.LETTER -> SettlementLetterStep(
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
                    previewCache = documentPreviews,
                    onIntent = onIntent,
                )

                SettlementStep.TERMS -> {
                    val amount = state.amount
                    val currencyInRial = state.currencyInRial
                    val deduction = state.terms.amount1
                    val gross = remember(amount, currencyInRial) { settlementGross(amount, currencyInRial) }
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
                        gross = gross,
                        isUploading = state.isUploading,
                        errors = state.errors,
                        onIntent = onIntent,
                    )
                }
            }
        }

        // The design's one line above the buttons: the first problem, in its own words.
        val bannerError = state.bannerError()
        if (bannerError != null) {
            WorkshopFormError(
                text = stringResource(bannerError),
                modifier = Modifier.padding(horizontal = Spacing.page, vertical = Spacing.sm),
            )
        }

        val isLastStep = step == SettlementStep.TERMS
        val checkIcon = vectorResource(Res.drawable.ic_tamin_check)
        WorkshopFormFooter(
            nextLabel = stringResource(if (isLastStep) Res.string.settlement_submit else Res.string.ws_form_next),
            onNext = { onIntent(SettlementRequestIntent.Next) },
            onPrev = if (step == SettlementStep.CONTRACT) {
                null
            } else {
                { onIntent(SettlementRequestIntent.Previous) }
            },
            isBusy = state.isBusy,
            // واگذارندگان shows every wait as a shimmer, the submit included.
            shimmerWhileBusy = true,
            // Filing is the green, ticked button; every step before it points on.
            nextIcon = if (isLastStep) checkIcon else null,
            nextBackground = if (isLastStep) colors.successGradient else null,
        )
    }

    if (state.isSubmitted) SettlementDoneDialog(onDone = onDone)
}

/**
 * The gradient bar with the design's hero progress: the request's title and the پیمان it is for,
 * then the step's title, «مرحلهٔ N از ۴», and four segments of which the passed ones go back.
 */
@Composable
private fun SettlementHeader(
    step: SettlementStep,
    workshopName: String?,
    rowLabel: String?,
    onIntent: (SettlementRequestIntent) -> Unit,
    onBack: () -> Unit,
    onClose: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val headerGradient = remember(colors.profileGradientStops) {
        Brush.horizontalGradient(colors.profileGradientStops)
    }
    val title = stringResource(Res.string.settlement_title)
    val subtitle = if (workshopName != null && rowLabel != null) {
        stringResource(Res.string.assigner_contract_subtitle, workshopName, rowLabel)
    } else {
        null
    }
    TaminTopAppBar(
        title = title,
        titleContent = subtitle?.let { line -> { WorkshopHeaderTitle(title = title, subtitle = line) } },
        background = headerGradient,
        navigationIcon = {
            TaminTopAppBarButton(
                icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                contentDescription = null,
                onClick = onBack,
                bordered = true,
            )
        },
        action = {
            TaminTopAppBarButton(
                icon = vectorResource(Res.drawable.ic_tamin_cross),
                contentDescription = null,
                onClick = onClose,
                bordered = true,
            )
        },
    ) {
        TaminHeroStepProgress(
            stepTitle = stringResource(step.label),
            currentStep = step.ordinal + 1,
            totalSteps = SettlementStep.entries.size,
            // Every segment up to this one is lit, as the design fills them.
            maxReachedStep = step.ordinal + 1,
            onStepClick = { onIntent(SettlementRequestIntent.StepSelected(SettlementStep.entries[it - 1])) },
            modifier = Modifier.padding(top = Spacing.md),
        )
    }
}

/** The seven cells the design's «پیمان انتخاب‌شده» card lists, rebuilt only when the پیمان changes. */
@Composable
private fun rememberContractRows(contract: AssignerContractPR): ImmutableList<WorkshopReviewRow> {
    val number = stringResource(Res.string.assigner_field_contract_number)
    val date = stringResource(Res.string.assigner_contract_date)
    val rowSequence = stringResource(Res.string.settlement_contract_row_sequence)
    val subject = stringResource(Res.string.assigner_field_contract_subject)
    val contractorName = stringResource(Res.string.settlement_contractor_name)
    val contractorWorkshop = stringResource(Res.string.settlement_contractor_workshop)
    val contractorBranch = stringResource(Res.string.settlement_contractor_branch)
    return remember(contract, number, date, rowSequence, subject, contractorName, contractorWorkshop, contractorBranch) {
        persistentListOf(
            WorkshopReviewRow(number, contract.contractNumber),
            WorkshopReviewRow(date, contract.contractDate),
            WorkshopReviewRow(rowSequence, contract.card.rowLabel + ROW_SEQUENCE_SEPARATOR + contract.sequenceLabel),
            WorkshopReviewRow(subject, contract.contractSubject, isNumeric = false),
            WorkshopReviewRow(contractorName, contract.employer.workshopName, isNumeric = false),
            WorkshopReviewRow(contractorWorkshop, contract.employer.workshopCode),
            WorkshopReviewRow(contractorBranch, contract.employer.branchName, isNumeric = false),
        )
    }
}

/** The letter the request is filed under, the period it covers and the amounts it declares. */
@Composable
private fun SettlementLetterStep(
    letterNumber: String,
    letterDate: SettlementDate?,
    startDate: SettlementDate?,
    endDate: SettlementDate?,
    hasSubcontractor: Boolean?,
    amount: String,
    currencyAmount: String,
    currencyInRial: String,
    errors: ImmutableMap<SettlementField, StringResource>,
    onIntent: (SettlementRequestIntent) -> Unit,
) {
    SettlementFieldPair {
        SettlementTextField(
            field = SettlementField.LETTER_NUMBER,
            label = stringResource(Res.string.settlement_letter_number),
            value = letterNumber,
            error = errors[SettlementField.LETTER_NUMBER],
            onIntent = onIntent,
            maxLength = LETTER_NUMBER_MAX_LENGTH,
            // A number, not an amount — grouping it would change how it reads against the letter.
            groupsThousands = false,
            modifier = Modifier.weight(1f),
        )
        SettlementDateField(
            field = SettlementField.LETTER_DATE,
            label = stringResource(Res.string.settlement_letter_date),
            date = letterDate,
            error = errors[SettlementField.LETTER_DATE],
            onIntent = onIntent,
            modifier = Modifier.weight(1f),
        )
    }
    SettlementFieldPair {
        SettlementDateField(
            field = SettlementField.START_DATE,
            label = stringResource(Res.string.settlement_start_date),
            date = startDate,
            error = errors[SettlementField.START_DATE],
            onIntent = onIntent,
            modifier = Modifier.weight(1f),
        )
        SettlementDateField(
            field = SettlementField.END_DATE,
            label = stringResource(Res.string.settlement_end_date),
            date = endDate,
            error = errors[SettlementField.END_DATE],
            onIntent = onIntent,
            // A پیمان's operations can end after today; the old app opened this one picker 100 years out.
            allowFuture = true,
            modifier = Modifier.weight(1f),
        )
    }

    SettlementTextField(
        field = SettlementField.AMOUNT,
        label = stringResource(Res.string.settlement_amount),
        value = amount,
        error = errors[SettlementField.AMOUNT],
        onIntent = onIntent,
    )
    SettlementFieldPair {
        SettlementTextField(
            field = SettlementField.CURRENCY_AMOUNT,
            label = stringResource(Res.string.settlement_currency_amount),
            value = currencyAmount,
            error = null,
            onIntent = onIntent,
            isRequired = false,
            modifier = Modifier.weight(1f),
        )
        SettlementTextField(
            field = SettlementField.CURRENCY_IN_RIAL,
            label = stringResource(Res.string.settlement_currency_in_rial),
            value = currencyInRial,
            error = errors[SettlementField.CURRENCY_IN_RIAL],
            onIntent = onIntent,
            isRequired = false,
            modifier = Modifier.weight(1f),
        )
    }
    SettlementHint(stringResource(Res.string.settlement_foreign_exchange_note))

    SettlementSubcontractorField(
        hasSubcontractor = hasSubcontractor,
        onIntent = onIntent,
    )
    SettlementHint(stringResource(Res.string.settlement_subcontractor_hint))

    SettlementTotalCard(amount = amount, currencyInRial = currencyInRial)
}

/**
 * «آیا پیمان، پیمانکار جزء دارد؟» — neither answer chosen until the user picks one.
 *
 * The card buttons rather than the segmented tabs: the tabs always light one option, and this
 * question has no default answer.
 */
@Composable
private fun SettlementSubcontractorField(
    hasSubcontractor: Boolean?,
    onIntent: (SettlementRequestIntent) -> Unit,
) {
    WorkshopFieldSlot(
        label = stringResource(Res.string.settlement_has_subcontractor),
        isRequired = true,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(WorkshopDimens.cardButtonGap),
        ) {
            // بله first, so the RTL row puts it on the right, where the old app's radio group had it.
            WorkshopCardButton(
                text = stringResource(Res.string.settlement_yes),
                tone = if (hasSubcontractor == true) WorkshopCardButtonTone.PRIMARY else WorkshopCardButtonTone.OUTLINE,
                onClick = { onIntent(SettlementRequestIntent.SubcontractorChanged(true)) },
            )
            WorkshopCardButton(
                text = stringResource(Res.string.settlement_no),
                tone = if (hasSubcontractor == false) WorkshopCardButtonTone.PRIMARY else WorkshopCardButtonTone.OUTLINE,
                onClick = { onIntent(SettlementRequestIntent.SubcontractorChanged(false)) },
            )
        }
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
    modifier: Modifier = Modifier,
    allowFuture: Boolean = false,
) {
    var isPicking by rememberSaveable { mutableStateOf(false) }
    SettlementChoiceField(
        label = label,
        value = date?.label,
        error = error,
        onClick = { isPicking = true },
        isDate = true,
        modifier = modifier,
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

/**
 * «جمع کل کارکرد پیمان» on the design's navy card — worked out as the two rial amounts are typed, and
 * absent until there is one.
 */
@Composable
private fun SettlementTotalCard(amount: String, currencyInRial: String) {
    val total = remember(amount, currencyInRial) { settlementGross(amount, currencyInRial) }
    if (total <= 0) return
    val colors = LocalTaminColors.current
    val background = remember(colors.profileGradientStops) {
        Brush.horizontalGradient(colors.profileGradientStops)
    }
    val shape = remember { RoundedCornerShape(CornerRadius.lg) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(background)
            .padding(horizontal = Spacing.md, vertical = Spacing.smPlus),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            Text(
                text = stringResource(Res.string.settlement_total_label),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textHeaderSubtitle,
            )
            Text(
                text = stringResource(Res.string.settlement_total_hint),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textHeaderSubtitle,
            )
        }
        NumericText(
            text = stringResource(Res.string.settlement_amount_rial, total.toPriceFormat()),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = colors.onGradient,
        )
    }
}

/** The request's evidence, as images or PDFs, each filed under one of the design's headings. */
@Composable
private fun SettlementDocumentsStep(
    attachments: ImmutableList<WorkshopAttachment>,
    types: ImmutableList<WorkshopDocumentType>,
    isUploading: Boolean,
    isError: Boolean,
    previewCache: SnapshotStateMap<String, ByteArray>,
    onIntent: (SettlementRequestIntent) -> Unit,
) {
    SettlementHint(stringResource(Res.string.settlement_documents_hint))
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
        previewCache = previewCache,
    )
}

/** «اطلاعات با موفقیت ارسال شد» — the one way out of a filed request, back to the پیمان‌ها. */
@Composable
private fun SettlementDoneDialog(onDone: () -> Unit) {
    val colors = LocalTaminColors.current
    TaminConfirmationDialog(
        title = stringResource(Res.string.settlement_submitted),
        description = stringResource(Res.string.settlement_done_body),
        icon = vectorResource(Res.drawable.ic_tamin_check),
        iconTint = colors.greenText,
        iconBackground = colors.greenBg,
        confirmButton = {
            TaminPrimaryButton(
                text = stringResource(Res.string.settlement_done_back),
                onClick = onDone,
                background = colors.buttonGradient,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        dismissButton = {},
        onDismissRequest = onDone,
    )
}

private const val LETTER_NUMBER_MAX_LENGTH = 20

/** «ردیف / توالی» reads as one value, the design's `row + ' / ' + seq`. */
private const val ROW_SEQUENCE_SEPARATOR = " / "

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
    SettlementRequestContent(
        contract = PreviewContract,
        state = PreviewFilled,
        onIntent = {},
        onBack = {},
        onDone = {},
    )
}

@PreviewRtlTheme
@Composable
private fun SettlementLetterStepPreview() = PreviewRtlThemeContent {
    SettlementRequestContent(
        contract = PreviewContract,
        state = PreviewFilled.copy(step = SettlementStep.LETTER),
        onIntent = {},
        onBack = {},
        onDone = {},
    )
}

/** «مرحلهٔ بعد» pressed on an empty letter step — red fields, and the banner from the real rules. */
@PreviewRtlTheme
@Composable
private fun SettlementLetterStepErrorsPreview() = PreviewRtlThemeContent {
    val empty = SettlementRequestUiState(
        contract = PreviewContract,
        step = SettlementStep.LETTER,
        currencyAmount = "1200",
    )
    SettlementRequestContent(
        contract = PreviewContract,
        state = empty.copy(errors = empty.errorsOf(SettlementStep.LETTER)),
        onIntent = {},
        onBack = {},
        onDone = {},
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
                WorkshopAttachment("pdf-1", SettlementDocumentTypesWithSubcontractor[2], "۱٬۱۰۲", isPdf = true),
            ),
        ),
        onIntent = {},
        onBack = {},
        onDone = {},
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
        onDone = {},
    )
}

/** Subject 11 — the پیمانکار's own address and id, then four costs, while the request is filed. */
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
        onDone = {},
    )
}

/** Subject 03 — the مکانیکی share typed, the دستی share worked out beside it. */
@PreviewRtlTheme
@Composable
private fun SettlementTermsMechanicalPreview() = PreviewRtlThemeContent {
    SettlementRequestContent(
        contract = PreviewContract,
        state = PreviewFilled.copy(
            step = SettlementStep.TERMS,
            subject = TaminOptionSheetItem(id = "03", label = "پیمان‌های مکانیکی"),
            terms = SettlementTerms(text1 = "35", text2 = "65"),
        ),
        onIntent = {},
        onBack = {},
        onDone = {},
    )
}

/** A subject that asks for nothing more — the green note that says so. */
@PreviewRtlTheme
@Composable
private fun SettlementTermsNonePreview() = PreviewRtlThemeContent {
    SettlementRequestContent(
        contract = PreviewContract,
        state = PreviewFilled.copy(
            step = SettlementStep.TERMS,
            subject = TaminOptionSheetItem(id = "08", label = "خدمات مشاوره"),
        ),
        onIntent = {},
        onBack = {},
        onDone = {},
    )
}

@PreviewRtlTheme
@Composable
private fun SettlementDoneDialogPreview() = PreviewRtlThemeContent {
    SettlementDoneDialog(onDone = {})
}

/** After process death, with the list gone. */
@PreviewRtlTheme
@Composable
private fun SettlementMissingContractPreview() = PreviewRtlThemeContent {
    SettlementRequestContent(
        contract = null,
        state = SettlementRequestUiState(),
        onIntent = {},
        onBack = {},
        onDone = {},
    )
}
