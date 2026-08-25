package com.tamin.taminhamrah.feature.workshops.ui.managementDebit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
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
import com.tamin.taminhamrah.model.workshop.Article16DebtPR
import com.tamin.taminhamrah.model.workshop.Article16RequestStatus
import com.tamin.taminhamrah.ui.PreviewRtlTheme
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
import taminx.core.core_ui.Res
import taminx.core.core_ui.article16_action_expert_message
import taminx.core.core_ui.article16_action_fix_request
import taminx.core.core_ui.article16_action_request
import taminx.core.core_ui.article16_debt_amount
import taminx.core.core_ui.article16_debt_remaining
import taminx.core.core_ui.article16_executive_notify_date
import taminx.core.core_ui.article16_request_status
import taminx.core.core_ui.article16_status_approved
import taminx.core.core_ui.article16_status_document_defect
import taminx.core.core_ui.article16_status_none
import taminx.core.core_ui.article16_status_rejected
import taminx.core.core_ui.article16_status_submitted
import taminx.core.core_ui.article16_status_unknown
import taminx.core.core_ui.payment_sheet_agreement_row
import taminx.core.core_ui.payment_sheet_debit_number
import taminx.core.core_ui.workshop_action_article16
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

    // درخواست رسیدگی is a page of this screen, not a route: only this ViewModel holds the domain
    // row the request is filed against.
    state.form?.let { form ->
        BackHandler { onIntent(ManagementDebitIntent.FormDismissed) }
        Article16FormPage(
            form = form,
            workshopName = state.workshopName,
            workshopCode = state.workshopId.takeIf { it.isNotBlank() }?.toPersianDigits(),
            onIntent = onIntent,
            onBack = { onIntent(ManagementDebitIntent.FormDismissed) },
            modifier = modifier,
        )
        return
    }

    WorkshopScreenShell(
        title = stringResource(Res.string.workshop_action_article16),
        onBack = onBack,
        workshopName = state.workshopName.takeIf { it.isNotBlank() },
        workshopCode = state.workshopId.takeIf { it.isNotBlank() }?.toPersianDigits(),
        action = {
            WorkshopSearchAction(
                onClick = { onIntent(ManagementDebitIntent.SearchOpenChanged(!isSearchOpen)) },
            )
        },
        modifier = modifier,
    ) {
        WorkshopListScaffold(
            state = state.list,
            onLoadMore = { onIntent(ManagementDebitIntent.LoadMore) },
            key = { it.debitNumber },
            header = {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.cardGap)) {
                    if (isSearchOpen) {
                        WorkshopSearchCard(
                            onSearch = { onIntent(ManagementDebitIntent.ApplySearch) },
                            onClear = { onIntent(ManagementDebitIntent.ClearSearch) },
                        ) {
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
                        title = stringResource(Res.string.workshop_action_article16),
                        count = state.visibleDebts.size,
                    )
                }
            },
        ) { debt ->
            Article16DebtCard(
                debt = debt,
                onRequest = { onIntent(ManagementDebitIntent.RequestReview(debt)) },
                onFix = { onIntent(ManagementDebitIntent.FixRequest(debt)) },
                onExpertMessage = { onIntent(ManagementDebitIntent.ShowExpertMessage(debt)) },
            )
        }
    }
}

@Composable
private fun Article16DebtCard(
    debt: Article16DebtPR,
    onRequest: () -> Unit,
    onFix: () -> Unit,
    onExpertMessage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    var isExpanded by rememberSaveable(debt.debitNumber) { mutableStateOf(false) }
    val hasRequest = debt.status != Article16RequestStatus.NONE
    val (_, statusColor) = debt.status.tint.colors()

    WorkshopRecordCard(
        modifier = modifier,
        isExpanded = isExpanded,
        onToggle = { isExpanded = !isExpanded },
        buttons = {
            if (hasRequest) {
                WorkshopCardButton(
                    text = stringResource(Res.string.article16_action_expert_message),
                    tone = WorkshopCardButtonTone.NOTICE,
                    onClick = onExpertMessage,
                )
                WorkshopCardButton(
                    text = stringResource(Res.string.article16_action_fix_request),
                    tone = WorkshopCardButtonTone.PRIMARY,
                    onClick = onFix,
                )
            } else {
                WorkshopCardButton(
                    text = stringResource(Res.string.article16_action_request),
                    tone = WorkshopCardButtonTone.PRIMARY,
                    onClick = onRequest,
                )
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
            label = stringResource(Res.string.article16_executive_notify_date),
            value = debt.executiveNotifyDateLabel,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.article16_request_status),
            value = stringResource(debt.status.label),
            valueColor = statusColor,
            numeric = false,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )

        if (isExpanded) {
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.article16_debt_amount),
                value = debt.amount,
                valueColor = colors.blueText,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.article16_debt_remaining),
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

/** The wording each request state is listed under. */
private val Article16RequestStatus.label
    get() = when (this) {
        Article16RequestStatus.SUBMITTED -> Res.string.article16_status_submitted
        Article16RequestStatus.DOCUMENT_DEFECT -> Res.string.article16_status_document_defect
        Article16RequestStatus.REJECTED -> Res.string.article16_status_rejected
        Article16RequestStatus.APPROVED -> Res.string.article16_status_approved
        Article16RequestStatus.NONE -> Res.string.article16_status_none
        Article16RequestStatus.UNKNOWN -> Res.string.article16_status_unknown
    }

@PreviewRtlTheme
@Composable
private fun ManagementDebitScreenPreview() {
    PreviewRtlThemeContent {
        ManagementDebitContent(
            state = ManagementDebitUiState(
                workshopId = "0968210170",
                workshopName = "آموزشگاه کامپیوتر توکلی-ایمیل",
                list = PagedListState(
                    items = persistentListOf(
                        Article16DebtPR(
                            debitNumber = "0960961008971",
                            debitNumberLabel = "۰۹۶۰۹۶۱۰۰۸۹۷۱",
                            executiveNotifyDateLabel = "۱۴۰۵/۰۵/۲۵",
                            amount = "۱۴٬۲۰۳٬۳۱۱",
                            remainingAmount = "۱۴٬۲۰۳٬۳۱۱",
                            fromDate = "۱۳۹۶/۰۷/۰۱",
                            toDate = "۱۳۹۷/۰۶/۳۱",
                            agreementRow = "۰۹۶۰۰۰۰۲",
                            status = Article16RequestStatus.NONE,
                        ),
                    ),
                ),
            ),
            onIntent = {},
            onBack = {},
        )
    }
}
