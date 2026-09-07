package com.tamin.taminhamrah.feature.workshops.ui.paymentSheets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopListScaffold
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopPickerField
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopRecordCard
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopScreenShell
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSearchCard
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSectionHeader
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopTextField
import com.tamin.taminhamrah.feature.workshops.ui.components.colors
import com.tamin.taminhamrah.feature.workshops.ui.components.tint
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.model.workshop.DebitReasonPR
import com.tamin.taminhamrah.model.workshop.PaymentSheetPR
import com.tamin.taminhamrah.model.workshop.PaymentSheetStatus
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_search
import taminx.core.core_ui.payment_sheet_agreement_row
import taminx.core.core_ui.payment_sheet_amount_rial
import taminx.core.core_ui.payment_sheet_collect_date
import taminx.core.core_ui.payment_sheet_date_from
import taminx.core.core_ui.payment_sheet_date_to
import taminx.core.core_ui.payment_sheet_debit_number
import taminx.core.core_ui.payment_sheet_debit_reason
import taminx.core.core_ui.payment_sheet_document_number
import taminx.core.core_ui.payment_sheet_issue_date
import taminx.core.core_ui.payment_sheet_number_from
import taminx.core.core_ui.payment_sheet_number_to
import taminx.core.core_ui.payment_sheet_pay_kind
import taminx.core.core_ui.payment_sheet_status
import taminx.core.core_ui.payment_sheet_type
import taminx.core.core_ui.payment_sheet_type_collected
import taminx.core.core_ui.payment_sheet_type_effective
import taminx.core.core_ui.payment_sheet_type_void
import taminx.core.core_ui.workshop_action_payment_sheets
import taminx.core.core_ui.workshop_all_items
import taminx.core.core_ui.workshop_search
import taminx.core.core_ui.workshop_select_date

/**
 * برگ پرداخت‌ها — receipt list of one workshop.
 *
 * Starts with three cells (debit number, collect date, amount in blue) and unfolds the rest on
 * demand. A search panel sits in the header and folds away when not in use.
 */
@Composable
fun PaymentSheetsScreen(
    workshopId: String,
    branchCode: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    workshopName: String? = null,
    viewModel: PaymentSheetsViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(workshopId, branchCode) {
        viewModel.sendIntent(PaymentSheetsIntent.Open(workshopId, branchCode))
    }

    PaymentSheetsContent(
        state = state,
        onBack = onBack,
        workshopName = workshopName,
        onIntent = viewModel::sendIntent,
        modifier = modifier,
    )
}

@Composable
fun PaymentSheetsContent(
    state: PaymentSheetsUiState,
    onBack: () -> Unit,
    workshopName: String? = null,
    onIntent: (PaymentSheetsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showStatusSheet by remember { mutableStateOf(false) }
    var showReasonSheet by remember { mutableStateOf(false) }
    var showDateFromPicker by remember { mutableStateOf(false) }
    var showDateToPicker by remember { mutableStateOf(false) }

    WorkshopScreenShell(
        title = stringResource(Res.string.workshop_action_payment_sheets),
        onBack = onBack,
        workshopName = workshopName,
        workshopCode = state.workshopId.toPersianDigits(),
        action = {
            TaminTopAppBarButton(
                icon = vectorResource(Res.drawable.ic_tamin_search),
                contentDescription = stringResource(Res.string.workshop_search),
                onClick = {
                    onIntent(PaymentSheetsIntent.SearchOpenChanged(!state.isSearchOpen))
                },
            )
        },
        modifier = modifier,
    ) {
        WorkshopListScaffold(
            state = state.list,
            onLoadMore = { onIntent(PaymentSheetsIntent.LoadMore) },
            onRetry = { onIntent(PaymentSheetsIntent.Load) },
            key = { it.debitNumber + it.agreementRow },
            header = {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    AnimatedVisibility(
                        visible = state.isSearchOpen,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut(),
                    ) {
                        PaymentSheetsSearchPanel(
                            filters = state.draft,
                            onFiltersChange = {
                                onIntent(PaymentSheetsIntent.DraftChanged(it))
                            },
                            onSearch = { onIntent(PaymentSheetsIntent.ApplyFilters) },
                            onClear = { onIntent(PaymentSheetsIntent.ClearFilters) },
                            onOpenStatusPicker = { showStatusSheet = true },
                            onOpenReasonPicker = {
                                onIntent(PaymentSheetsIntent.LoadDebitReasons)
                                showReasonSheet = true
                            },
                            onOpenDateFromPicker = { showDateFromPicker = true },
                            onOpenDateToPicker = { showDateToPicker = true },
                        )
                    }
                    WorkshopSectionHeader(
                        title = stringResource(Res.string.workshop_action_payment_sheets),
                        count = state.list.items.size,
                    )
                }
            },
        ) { sheet, itemModifier ->
            PaymentSheetCard(sheet = sheet, modifier = itemModifier)
        }
    }

    if (showStatusSheet) {
        PaymentSheetStatusSheet(
            selected = state.draft.type,
            onDismiss = { showStatusSheet = false },
            onSelect = {
                onIntent(PaymentSheetsIntent.DraftChanged(state.draft.copy(type = it)))
                showStatusSheet = false
            },
        )
    }

    if (showReasonSheet) {
        DebitReasonSheet(
            reasons = state.debitReasons,
            selected = state.draft.debitReason,
            onDismiss = { showReasonSheet = false },
            onSelect = {
                onIntent(PaymentSheetsIntent.DraftChanged(state.draft.copy(debitReason = it)))
                showReasonSheet = false
            },
        )
    }

    if (showDateFromPicker) {
        TaminJalaliDatePicker(
            title = stringResource(Res.string.payment_sheet_date_from),
            onDismiss = { showDateFromPicker = false },
            onConfirm = { y, m, d ->
                val millis = PersianDateFormatter.toEpochMillis(y, m, d)
                onIntent(PaymentSheetsIntent.DraftChanged(state.draft.copy(docDateFrom = millis)))
                showDateFromPicker = false
            },
        )
    }

    if (showDateToPicker) {
        TaminJalaliDatePicker(
            title = stringResource(Res.string.payment_sheet_date_to),
            onDismiss = { showDateToPicker = false },
            onConfirm = { y, m, d ->
                val millis = PersianDateFormatter.toEpochMillis(y, m, d)
                onIntent(PaymentSheetsIntent.DraftChanged(state.draft.copy(docDateTo = millis)))
                showDateToPicker = false
            },
        )
    }
}

/** One card representing a payment receipt. Shows 3 rows collapsed, 9 rows when expanded. */
@Composable
private fun PaymentSheetCard(
    sheet: PaymentSheetPR,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    var isExpanded by remember { mutableStateOf(false) }
    val (_, foregroundColor) = sheet.status.tint.colors()

    WorkshopRecordCard(
        modifier = modifier,
        isExpanded = isExpanded,
        onToggle = { isExpanded = !isExpanded },
    ) {
        DetailRow(
            label = stringResource(Res.string.payment_sheet_debit_number),
            value = sheet.debitNumber,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.payment_sheet_collect_date),
            value = sheet.collectDate,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.payment_sheet_amount_rial),
            value = sheet.amount,
            valueColor = colors.blueText,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )

        if (isExpanded) {
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.payment_sheet_status),
                value = sheet.statusLabel,
                valueColor = foregroundColor,
                numeric = false,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.payment_sheet_issue_date),
                value = sheet.issueDate,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.payment_sheet_pay_kind),
                value = sheet.payKind,
                numeric = false,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.payment_sheet_debit_reason),
                value = sheet.debitReason,
                numeric = false,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.payment_sheet_agreement_row),
                value = sheet.agreementRow,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.payment_sheet_document_number),
                value = sheet.documentNumber,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
        }
    }
}

/**
 * The design's search grid for برگ پرداخت‌ها: two full-width pickers, then the number pair and
 * the date pair side by side.
 */
@Composable
private fun PaymentSheetsSearchPanel(
    filters: PaymentSheetFilters,
    onFiltersChange: (PaymentSheetFilters) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    onOpenStatusPicker: () -> Unit,
    onOpenReasonPicker: () -> Unit,
    onOpenDateFromPicker: () -> Unit,
    onOpenDateToPicker: () -> Unit,
    modifier: Modifier = Modifier,
) {
    WorkshopSearchCard(onSearch = onSearch, onClear = onClear, modifier = modifier) {
        WorkshopPickerField(
            label = stringResource(Res.string.payment_sheet_status),
            value = filters.type?.let { stringResource(it.labelRes) },
            onClick = onOpenStatusPicker,
        )
        WorkshopPickerField(
            label = stringResource(Res.string.payment_sheet_debit_reason),
            value = filters.debitReason?.title,
            onClick = onOpenReasonPicker,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(WorkshopDimens.fieldGap),
        ) {
            WorkshopTextField(
                label = stringResource(Res.string.payment_sheet_number_from),
                value = filters.payIdFrom,
                onValueChange = { onFiltersChange(filters.copy(payIdFrom = it.digitsOnly())) },
                modifier = Modifier.weight(1f),
            )
            WorkshopTextField(
                label = stringResource(Res.string.payment_sheet_number_to),
                value = filters.payIdTo,
                onValueChange = { onFiltersChange(filters.copy(payIdTo = it.digitsOnly())) },
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(WorkshopDimens.fieldGap),
        ) {
            WorkshopPickerField(
                label = stringResource(Res.string.payment_sheet_date_from),
                value = PersianDateFormatter.formatTimestamp(filters.docDateFrom)
                    .takeIf { it.isNotBlank() },
                onClick = onOpenDateFromPicker,
                placeholder = stringResource(Res.string.workshop_select_date),
                isDate = true,
                modifier = Modifier.weight(1f),
            )
            WorkshopPickerField(
                label = stringResource(Res.string.payment_sheet_date_to),
                value = PersianDateFormatter.formatTimestamp(filters.docDateTo)
                    .takeIf { it.isNotBlank() },
                onClick = onOpenDateToPicker,
                placeholder = stringResource(Res.string.workshop_select_date),
                isDate = true,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentSheetStatusSheet(
    selected: PaymentSheetStatus?,
    onDismiss: () -> Unit,
    onSelect: (PaymentSheetStatus?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.bgSurface,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page)
                .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = stringResource(Res.string.payment_sheet_type),
                style = MaterialTheme.typography.titleSmall,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = Spacing.sm),
            )
            SheetOptionRow(
                label = stringResource(Res.string.workshop_all_items),
                isSelected = selected == null,
                onClick = { onSelect(null) },
            )
            val statuses = listOf(
                PaymentSheetStatus.COLLECTED,
                PaymentSheetStatus.EFFECTIVE,
                PaymentSheetStatus.VOID,
            )
            statuses.forEach { status ->
                SheetOptionRow(
                    label = stringResource(status.labelRes),
                    isSelected = selected == status,
                    onClick = { onSelect(status) },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DebitReasonSheet(
    reasons: ImmutableList<DebitReasonPR>,
    selected: DebitReasonPR?,
    onDismiss: () -> Unit,
    onSelect: (DebitReasonPR?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.bgSurface,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page)
                .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = stringResource(Res.string.payment_sheet_debit_reason),
                style = MaterialTheme.typography.titleSmall,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = Spacing.sm),
            )
            SheetOptionRow(
                label = stringResource(Res.string.workshop_all_items),
                isSelected = selected == null,
                onClick = { onSelect(null) },
            )
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                items(reasons) { reason ->
                    SheetOptionRow(
                        label = reason.title,
                        isSelected = selected?.code == reason.code,
                        onClick = { onSelect(reason) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SheetOptionRow(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.md))
            .clickable(onClick = onClick)
            .background(if (isSelected) colors.blueBg else colors.chipBg)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) colors.blueText else colors.textPrimary,
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = colors.blueText,
                modifier = Modifier.size(IconSize.small),
            )
        }
    }
}

private val PaymentSheetStatus.labelRes
    get() = when (this) {
        PaymentSheetStatus.COLLECTED -> Res.string.payment_sheet_type_collected
        PaymentSheetStatus.EFFECTIVE -> Res.string.payment_sheet_type_effective
        PaymentSheetStatus.VOID -> Res.string.payment_sheet_type_void
        PaymentSheetStatus.UNKNOWN -> Res.string.workshop_all_items
    }

@PreviewRtlTheme
@Composable
private fun PaymentSheetsScreenPreview() {
    PreviewRtlThemeContent {
        PaymentSheetsContent(
            state = PaymentSheetsUiState(
                workshopId = "0968210170",
                list = PagedListState(
                    items = persistentListOf(
                        PaymentSheetPR(
                            debitNumber = "۱۴۰۲۰۰۰۱۲۳",
                            collectDate = "۱۴۰۲/۰۵/۱۲",
                            amount = "۱۲٬۴۵۰٬۰۰۰",
                            statusLabel = "وصول شده",
                            status = PaymentSheetStatus.COLLECTED,
                            issueDate = "۱۴۰۲/۰۴/۳۰",
                            payKind = "اینترنتی",
                            debitReason = "حق بیمه لیست",
                            agreementRow = "۰۰۱",
                            documentNumber = "۸۸۲۱۴",
                        ),
                    ),
                ),
            ),
            onBack = {},
            workshopName = "آموزشگاه کامپیوتر توکلی-ایمیل",
            onIntent = {},
        )
    }
}

