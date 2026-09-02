package com.tamin.taminhamrah.feature.workshops.ui.workshopDebit

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
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSectionHeader
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.model.workshop.WorkShopDebtPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.payment_sheet_agreement_row
import taminx.core.core_ui.payment_sheet_debit_number
import taminx.core.core_ui.workshop_action_debit_turnover
import taminx.core.core_ui.workshop_debt_amount
import taminx.core.core_ui.workshop_debt_customer_code
import taminx.core.core_ui.workshop_debt_documents
import taminx.core.core_ui.workshop_debt_from_date
import taminx.core.core_ui.workshop_debt_notify_date
import taminx.core.core_ui.workshop_debt_pay
import taminx.core.core_ui.workshop_debt_primary_vote_date
import taminx.core.core_ui.workshop_debt_primary_vote_number
import taminx.core.core_ui.workshop_debt_remaining
import taminx.core.core_ui.workshop_debt_to_date

/**
 * گردش حساب بدهی — every debt raised against one workshop.
 *
 * Four cells identify a debt and the amount owed; the dates and the agreement row are behind
 * «جزئیات بیشتر». Both actions stay visible either way, because they are the reason the row is
 * being read.
 */
@Composable
fun WorkshopDebitScreen(
    workshopId: String,
    branchCode: String,
    onBack: () -> Unit,
    onOpenDocuments: (String, String) -> Unit,
    onOpenUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
    workshopName: String = "",
    viewModel: WorkshopDebitViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(workshopId, branchCode) {
        viewModel.sendIntent(WorkshopDebitIntent.Open(workshopId, branchCode))
    }

    HandleWorkshopDebitEvents(events = viewModel.events, onOpenUrl = onOpenUrl)

    WorkshopDebitContent(
        state = state,
        workshopName = workshopName,
        onIntent = viewModel::sendIntent,
        onBack = onBack,
        onOpenDocuments = { debitNumber -> onOpenDocuments(debitNumber, branchCode) },
        modifier = modifier,
    )
}

@Composable
fun WorkshopDebitContent(
    state: WorkshopDebitUiState,
    workshopName: String,
    onIntent: (WorkshopDebitIntent) -> Unit,
    onBack: () -> Unit,
    onOpenDocuments: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    WorkshopScreenShell(
        title = stringResource(Res.string.workshop_action_debit_turnover),
        onBack = onBack,
        workshopName = workshopName.takeIf { it.isNotBlank() },
        workshopCode = state.workshopId.takeIf { it.isNotBlank() }?.toPersianDigits(),
        modifier = modifier,
    ) {
        WorkshopListScaffold(
            state = state.list,
            onLoadMore = { onIntent(WorkshopDebitIntent.LoadMore) },
            key = { it.debitNumber },
            header = {
                WorkshopSectionHeader(
                    title = stringResource(Res.string.workshop_action_debit_turnover),
                    count = state.list.items.size,
                )
            },
        ) { debt ->
            WorkshopDebtCard(
                debt = debt,
                onDocuments = { onOpenDocuments(debt.debitNumber) },
                onPay = { onIntent(WorkshopDebitIntent.PayDebit(debt)) },
            )
        }
    }
}

@Composable
private fun WorkshopDebtCard(
    debt: WorkShopDebtPR,
    onDocuments: () -> Unit,
    onPay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    var isExpanded by rememberSaveable(debt.debitNumber) { mutableStateOf(false) }

    WorkshopRecordCard(
        modifier = modifier,
        isExpanded = isExpanded,
        onToggle = { isExpanded = !isExpanded },
        buttons = {
            WorkshopCardButton(
                text = stringResource(Res.string.workshop_debt_documents),
                tone = WorkshopCardButtonTone.OUTLINE,
                onClick = onDocuments,
            )
            WorkshopCardButton(
                text = stringResource(Res.string.workshop_debt_pay),
                tone = WorkshopCardButtonTone.PRIMARY,
                onClick = onPay,
            )
        },
    ) {
        DetailRow(
            label = stringResource(Res.string.payment_sheet_debit_number),
            value = debt.debitNumberLabel,
            copyValue = debt.debitNumber,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.workshop_debt_notify_date),
            value = debt.notifyDate,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.workshop_debt_customer_code),
            value = debt.customerCode,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.workshop_debt_amount),
            value = debt.amount,
            valueColor = colors.orangeText,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )

        if (isExpanded) {
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.workshop_debt_remaining),
                value = debt.remainingAmount,
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
                value = debt.agreementRowLabel,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )

            // The بدوی vote block is part of the row only when the service actually sent one.
            if (debt.hasPrimaryVote) {
                TaminDivider()
                DetailRow(
                    label = stringResource(Res.string.workshop_debt_primary_vote_number),
                    value = debt.primaryVoteNumber,
                    verticalPadding = WorkshopDimens.cellVerticalPadding,
                )
                TaminDivider()
                DetailRow(
                    label = stringResource(Res.string.workshop_debt_primary_vote_date),
                    value = debt.primaryVoteDate,
                    verticalPadding = WorkshopDimens.cellVerticalPadding,
                )
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun WorkshopDebitScreenPreview() {
    PreviewRtlThemeContent {
        WorkshopDebitContent(
            state = WorkshopDebitUiState(
                workshopId = "0968210170",
                list = PagedListState(
                    items = persistentListOf(
                        WorkShopDebtPR(
                            debitNumber = "0960961008971",
                            debitNumberLabel = "۰۹۶۰۹۶۱۰۰۸۹۷۱",
                            notifyDate = "۱۴۰۵/۰۵/۲۵",
                            customerCode = "۰۹۶۰۰۰۰۲",
                            amount = "۱۴٬۲۰۳٬۳۱۱",
                            remainingAmount = "۱۴٬۲۰۳٬۳۱۱",
                            fromDate = "۱۳۹۶/۰۷/۰۱",
                            toDate = "۱۳۹۷/۰۶/۳۱",
                            agreementRow = "09600002",
                            agreementRowLabel = "۰۹۶۰۰۰۰۲",
                        ),
                    ),
                ),
            ),
            workshopName = "آموزشگاه کامپیوتر توکلی-ایمیل",
            onIntent = {},
            onBack = {},
            onOpenDocuments = {},
        )
    }
}
