package com.tamin.taminhamrah.feature.workshops.ui.workshopDebit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopListScaffold
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopScreenShell
import com.tamin.taminhamrah.model.workshop.WorkShopDebtPR
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
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
import taminx.core.core_ui.payment_sheet_agreement_row
import taminx.core.core_ui.payment_sheet_debit_number

/**
 * جزئیات محاسبه گردش حساب بدهی — the debts of one workshop.
 *
 * Two things leave this screen: the documents behind a debt, and the payment page, which opens
 * outside the app.
 */
@Composable
fun WorkshopDebitScreen(
    workshopId: String,
    branchCode: String,
    onBack: () -> Unit,
    onOpenDocuments: (debitNumber: String, branchCode: String) -> Unit,
    onOpenUrl: (String) -> Unit,
    viewModel: WorkshopDebitViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(workshopId, branchCode) {
        viewModel.sendIntent(WorkshopDebitIntent.Open(workshopId, branchCode))
    }

    HandleWorkshopDebitEvents(events = viewModel.events, onOpenUrl = onOpenUrl)

    WorkshopScreenShell(
        title = stringResource(Res.string.workshop_action_debit_turnover),
        onBack = onBack,
    ) {
        WorkshopListScaffold(
            state = state.list,
            onLoadMore = { viewModel.sendIntent(WorkshopDebitIntent.LoadMore) },
            key = { it.debitNumber },
        ) { debt ->
            WorkshopDebtCard(
                debt = debt,
                onDocuments = { onOpenDocuments(debt.debitNumber, branchCode) },
                onPay = { viewModel.sendIntent(WorkshopDebitIntent.PayDebit(debt)) },
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
    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.lg)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        DetailRow(stringResource(Res.string.payment_sheet_debit_number), debt.debitNumberLabel)
        DetailRow(stringResource(Res.string.workshop_debt_notify_date), debt.notifyDate)
        DetailRow(stringResource(Res.string.workshop_debt_customer_code), debt.customerCode)
        DetailRow(
            label = stringResource(Res.string.workshop_debt_amount),
            value = debt.amount,
            valueColor = colors.orangeText,
        )
        DetailRow(stringResource(Res.string.workshop_debt_remaining), debt.remainingAmount)
        DetailRow(stringResource(Res.string.workshop_debt_from_date), debt.fromDate)
        DetailRow(stringResource(Res.string.workshop_debt_to_date), debt.toDate)
        DetailRow(stringResource(Res.string.payment_sheet_agreement_row), debt.agreementRow)

        // The بدوی vote block is part of the row only when the service actually sent one.
        if (debt.hasPrimaryVote) {
            DetailRow(
                stringResource(Res.string.workshop_debt_primary_vote_number),
                debt.primaryVoteNumber,
            )
            DetailRow(
                stringResource(Res.string.workshop_debt_primary_vote_date),
                debt.primaryVoteDate,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            TaminOutlinedButton(
                text = stringResource(Res.string.workshop_debt_documents),
                onClick = onDocuments,
                modifier = Modifier.weight(1f),
            )
            TaminPrimaryButton(
                text = stringResource(Res.string.workshop_debt_pay),
                onClick = onPay,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
