package com.tamin.taminhamrah.feature.workshops.ui.managementDebit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import taminx.core.core_ui.ws_dialog_ok
import taminx.core.core_ui.article_sixteen_form_done_body
import taminx.core.core_ui.article_sixteen_form_done_title
import taminx.core.core_ui.ic_tamin_check
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.vectorResource
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFilterChips
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopCardButton
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopCardButtonTone
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopListScaffold
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopRecordCard
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopScreenShell
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSearchAction
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSearchCard
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSectionHeader
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopTextField
import com.tamin.taminhamrah.feature.workshops.ui.components.colors
import com.tamin.taminhamrah.feature.workshops.ui.components.tint
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.model.workshop.ArticleSixteenDebtPR
import com.tamin.taminhamrah.model.workshop.ArticleSixteenRequestStatus
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import taminx.core.core_ui.article_sixteen_pdf_file
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.BackHandler
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.remember
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopPickerField
import com.tamin.taminhamrah.feature.workshops.ui.components.label
import com.tamin.taminhamrah.feature.workshops.ui.sheets.ArticleSixteenStatusSheet
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import taminx.core.core_ui.Res
import taminx.core.core_ui.article_sixteen_action_expert_message
import taminx.core.core_ui.article_sixteen_action_fix_request
import taminx.core.core_ui.article_sixteen_action_request
import taminx.core.core_ui.article_sixteen_action_show_request
import taminx.core.core_ui.article_sixteen_debt_amount
import taminx.core.core_ui.article_sixteen_debt_remaining
import taminx.core.core_ui.article_sixteen_executive_notify_date
import taminx.core.core_ui.article_sixteen_request_status
import taminx.core.core_ui.payment_sheet_agreement_row
import taminx.core.core_ui.payment_sheet_debit_number
import taminx.core.core_ui.workshop_action_article_sixteen
import taminx.core.core_ui.workshop_debt_from_date
import taminx.core.core_ui.workshop_debt_to_date

/**
 * رسیدگی به بدهی ماده ۱۶.
 *
 * A debt with no request yet offers to open one; a debt already under review offers the reviewer's
 * message and the correction that answers it. The request's state is a colored line of the card,
 * not a pill — on this screen it is a field of the record, not a label on it.
 */
@Composable
fun ManagementDebitScreen(
    workshopId: String,
    branchCode: String,
    workshopName: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ManagementDebitViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(workshopId, branchCode) {
        viewModel.sendIntent(ManagementDebitIntent.Open(workshopId, branchCode, workshopName))
    }

    HandleManagementDebitEvents(viewModel.events)

    ManagementDebitContent(
        state = state,
        onIntent = viewModel::sendIntent,
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
fun ManagementDebitContent(
    state: ManagementDebitUiState,
    onIntent: (ManagementDebitIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSearchOpen = state.isSearchOpen
    val draft = state.draft
    var isStatusSheetOpen by rememberSaveable { mutableStateOf(false) }

    // درخواست رسیدگی is a page of this screen, not a route: only this ViewModel holds the domain
    // row the request is filed against.
    state.viewerPdf?.let { pdf ->
        TaminPdfViewer(
            fileName = stringResource(Res.string.article_sixteen_pdf_file, state.workshopId),
            pdf = pdf,
            downloadFailed = false,
            onRequestDownload = {},
            onDismiss = { onIntent(ManagementDebitIntent.DismissViewer) },
            title = stringResource(Res.string.workshop_action_article_sixteen),
        )
    }

    state.expertMessage?.let { message ->
        TaminConfirmationDialog(
            title = stringResource(Res.string.article_sixteen_action_expert_message),
            description = message,
            icon = Icons.Outlined.Info,
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(Res.string.ws_dialog_ok),
                    onClick = { onIntent(ManagementDebitIntent.DismissExpertMessage) },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            dismissButton = {},
            onDismissRequest = { onIntent(ManagementDebitIntent.DismissExpertMessage) },
        )
    }

    // A dialog rather than a toast, as the design and the old app both show it: the tracking code
    // is what the employer follows the request up with, and a toast is gone before it is copied.
    state.filedReferenceCode?.let { referenceCode ->
        val colors = LocalTaminColors.current
        TaminConfirmationDialog(
            title = stringResource(Res.string.article_sixteen_form_done_title),
            description = stringResource(
                Res.string.article_sixteen_form_done_body,
                referenceCode.toPersianDigits(),
            ),
            icon = vectorResource(Res.drawable.ic_tamin_check),
            iconTint = colors.teal,
            iconBackground = colors.greenBg,
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(Res.string.ws_dialog_ok),
                    onClick = { onIntent(ManagementDebitIntent.DismissFiled) },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            dismissButton = {},
            onDismissRequest = { onIntent(ManagementDebitIntent.DismissFiled) },
        )
    }

    state.form?.let { form ->
        BackHandler { onIntent(ManagementDebitIntent.FormDismissed) }
        ArticleSixteenFormPage(
            form = form,
            workshopName = state.workshopName,
            workshopCode = state.workshopId.takeIf { it.isNotBlank() }?.toPersianDigits(),
            onIntent = onIntent,
            onBack = { onIntent(ManagementDebitIntent.FormDismissed) },
            modifier = modifier,
        )
        return
    }

    // What the list is narrowed by, each removable on its own. The status filter is listed too:
    // it is picked inside the search panel, and once that folds away nothing else shows it is on.
    val applied = state.applied
    val statusLabel = state.statusFilter?.let { stringResource(it.label) }
    val filters = remember(applied, statusLabel) {
        buildList {
            applied.debitNumber.takeIf { it.isNotBlank() }?.let {
                add(it.toPersianDigits() to ManagementDebitIntent.ReplaceSearch(applied.copy(debitNumber = "")))
            }
            applied.agreementRow.takeIf { it.isNotBlank() }?.let {
                add(it.toPersianDigits() to ManagementDebitIntent.ReplaceSearch(applied.copy(agreementRow = "")))
            }
            statusLabel?.let { add(it to ManagementDebitIntent.StatusFilterChanged(null)) }
        }
    }
    val filterChips = remember(filters) { filters.map { it.first }.toImmutableList() }

    Box(modifier = modifier) {
        WorkshopScreenShell(
            title = stringResource(Res.string.workshop_action_article_sixteen),
            onBack = onBack,
            workshopName = state.workshopName.takeIf { it.isNotBlank() },
            workshopCode = state.workshopId.takeIf { it.isNotBlank() }?.toPersianDigits(),
            action = {
                WorkshopSearchAction(
                    onClick = { onIntent(ManagementDebitIntent.SearchOpenChanged(!isSearchOpen)) },
                )
            },
        ) {
            // visibleDebts filters on every read; narrow once per list or filter change.
            val visibleDebts = remember(state.list.items, state.statusFilter) { state.visibleDebts }
            val displayedList = remember(state.list, visibleDebts) {
                state.list.copy(items = visibleDebts)
            }
            WorkshopListScaffold(
                state = displayedList,
                onLoadMore = { onIntent(ManagementDebitIntent.LoadMore) },
                key = { it.debitNumber },
                header = {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.cardGap)) {
                        AnimatedVisibility(
                            visible = isSearchOpen,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut(),
                        ) {
                            WorkshopSearchCard(
                                onSearch = { onIntent(ManagementDebitIntent.ApplySearch) },
                                onClear = {
                                    onIntent(ManagementDebitIntent.ClearSearch)
                                    onIntent(ManagementDebitIntent.StatusFilterChanged(null))
                                },
                            ) {
                                WorkshopPickerField(
                                    label = stringResource(Res.string.article_sixteen_request_status),
                                    value = state.statusFilter?.let { stringResource(it.label) },
                                    onClick = { isStatusSheetOpen = true },
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(WorkshopDimens.fieldGap),
                                ) {
                                    WorkshopTextField(
                                        label = stringResource(Res.string.payment_sheet_debit_number),
                                        value = draft.debitNumber,
                                        onValueChange = {
                                            onIntent(
                                                ManagementDebitIntent.DraftChanged(
                                                    draft.copy(debitNumber = it.digitsOnly()),
                                                ),
                                            )
                                        },
                                        modifier = Modifier.weight(1f),
                                    )
                                    WorkshopTextField(
                                        label = stringResource(Res.string.payment_sheet_agreement_row),
                                        value = draft.agreementRow,
                                        onValueChange = {
                                            onIntent(
                                                ManagementDebitIntent.DraftChanged(
                                                    draft.copy(agreementRow = it.digitsOnly()),
                                                ),
                                            )
                                        },
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                            }
                        }
                        WorkshopSectionHeader(
                            title = stringResource(Res.string.workshop_action_article_sixteen),
                            count = visibleDebts.size,
                        )
                        WorkshopFilterChips(
                            chips = filterChips,
                            onRemove = { index -> onIntent(filters[index].second) },
                        )
                    }
                },
            ) { debt, rowModifier ->
                ArticleSixteenDebtCard(
                    debt = debt,
                    onRequest = { onIntent(ManagementDebitIntent.RequestReview(debt)) },
                    onFix = { onIntent(ManagementDebitIntent.FixRequest(debt)) },
                    onExpertMessage = { onIntent(ManagementDebitIntent.ShowExpertMessage(debt)) },
                    onShowPdf = { onIntent(ManagementDebitIntent.ShowRequestPdf(debt)) },
                    modifier = rowModifier,
                )
            }
        }
        // مشاهده درخواست and پیام کارشناس each wait on a request before anything opens.
        if (state.isBusy) LoadingStateOverlay()
    }

    if (isStatusSheetOpen) {
        ArticleSixteenStatusSheet(
            selected = state.statusFilter,
            onDismiss = { isStatusSheetOpen = false },
            onSelect = { status ->
                onIntent(ManagementDebitIntent.StatusFilterChanged(status))
                isStatusSheetOpen = false
            },
        )
    }
}

@Composable
private fun ArticleSixteenDebtCard(
    debt: ArticleSixteenDebtPR,
    onRequest: () -> Unit,
    onFix: () -> Unit,
    onExpertMessage: () -> Unit,
    onShowPdf: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    var isExpanded by rememberSaveable(debt.debitNumber) { mutableStateOf(false) }
    val (_, statusColor) = debt.status.tint.colors()

    WorkshopRecordCard(
        modifier = modifier,
        isExpanded = isExpanded,
        onToggle = { isExpanded = !isExpanded },
        buttons = {
            when (debt.status) {
                ArticleSixteenRequestStatus.NONE -> {
                    WorkshopCardButton(
                        text = stringResource(Res.string.article_sixteen_action_request),
                        tone = WorkshopCardButtonTone.PRIMARY,
                        onClick = onRequest,
                    )
                }
                ArticleSixteenRequestStatus.DOCUMENT_DEFECT -> {
                    WorkshopCardButton(
                        text = stringResource(Res.string.article_sixteen_action_expert_message),
                        tone = WorkshopCardButtonTone.NOTICE,
                        onClick = onExpertMessage,
                    )
                    WorkshopCardButton(
                        text = stringResource(Res.string.article_sixteen_action_fix_request),
                        tone = WorkshopCardButtonTone.PRIMARY,
                        onClick = onFix,
                    )
                }
                else -> {
                    WorkshopCardButton(
                        text = stringResource(Res.string.article_sixteen_action_show_request),
                        tone = WorkshopCardButtonTone.PRIMARY,
                        onClick = onShowPdf,
                    )
                }
            }
        },
    ) {
        DetailRow(
            label = stringResource(Res.string.payment_sheet_debit_number),
            value = debt.debitNumberLabel,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.article_sixteen_executive_notify_date),
            value = debt.executiveNotifyDateLabel,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.article_sixteen_request_status),
            value = stringResource(debt.status.label),
            valueColor = statusColor,
            numeric = false,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )

        if (isExpanded) {
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.article_sixteen_debt_amount),
                value = debt.amount,
                valueColor = colors.blueText,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.article_sixteen_debt_remaining),
                value = debt.remainingAmount,
                valueColor = colors.orangeText,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.workshop_debt_from_date),
                value = debt.fromDate,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.workshop_debt_to_date),
                value = debt.toDate,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.payment_sheet_agreement_row),
                value = debt.agreementRow,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ManagementDebitScreenPreview() {
    PreviewRtlThemeContent {
        ManagementDebitContent(
            state = ManagementDebitUiState(
                workshopId = "0968210170",
                workshopName = "آموزشگاه کامپیوتر توکلی-ایمیل",
                applied = ArticleSixteenSearch(agreementRow = "09600002"),
                list = PagedListState(
                    items = persistentListOf(
                        ArticleSixteenDebtPR(
                            debitNumber = "0960961008971",
                            debitNumberLabel = "۰۹۶۰۹۶۱۰۰۸۹۷۱",
                            executiveNotifyDateLabel = "۱۴۰۵/۰۵/۲۵",
                            amount = "۱۴٬۲۰۳٬۳۱۱",
                            remainingAmount = "۱۴٬۲۰۳٬۳۱۱",
                            fromDate = "۱۳۹۶/۰۷/۰۱",
                            toDate = "۱۳۹۷/۰۶/۳۱",
                            agreementRow = "۰۹۶۰۰۰۰۲",
                            status = ArticleSixteenRequestStatus.NONE,
                        ),
                        ArticleSixteenDebtPR(
                            debitNumber = "0960961008972",
                            debitNumberLabel = "۰۹۶۰۹۶۱۰۰۸۹۷۲",
                            executiveNotifyDateLabel = "۱۴۰۵/۰۴/۱۵",
                            amount = "۲۵٬۰۰۰٬۰۰۰",
                            remainingAmount = "۲۰٬۰۰۰٬۰۰۰",
                            fromDate = "۱۳۹۷/۰۱/۰۱",
                            toDate = "۱۳۹۷/۱۲/۲۹",
                            agreementRow = "۰۹۶۰۰۰۰۳",
                            status = ArticleSixteenRequestStatus.DOCUMENT_DEFECT,
                            seqNo = 101L,
                        ),
                        ArticleSixteenDebtPR(
                            debitNumber = "0960961008973",
                            debitNumberLabel = "۰۹۶۰۹۶۱۰۰۸۹۷۳",
                            executiveNotifyDateLabel = "۱۴۰۵/۰۳/۱۰",
                            amount = "۵۰٬۰۰۰٬۰۰۰",
                            remainingAmount = "۵۰٬۰۰۰٬۰۰۰",
                            fromDate = "۱۳۹۸/۰۱/۰۱",
                            toDate = "۱۳۹۸/۰۶/۳۱",
                            agreementRow = "۰۹۶۰۰۰۰۴",
                            status = ArticleSixteenRequestStatus.SUBMITTED,
                            seqNo = 102L,
                        ),
                    ),
                ),
            ),
            onIntent = {},
            onBack = {},
        )
    }
}

/** What the screen says back, through the app's toast host. */
@Composable
private fun HandleManagementDebitEvents(events: Flow<ManagementDebitEvent>) {
    val toaster = LocalToaster.current
    LaunchedEffect(events, toaster) {
        events.collect { event ->
            when (event) {
                is ManagementDebitEvent.ShowServerMessage -> toaster.error(event.message)
                is ManagementDebitEvent.ShowMessage -> toaster.error(getString(event.message))
            }
        }
    }
}
