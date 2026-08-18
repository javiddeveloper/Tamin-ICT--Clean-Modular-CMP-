package com.tamin.taminhamrah.feature.workshops.ui.objectionableDebit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopListScaffold
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopScreenShell
import com.tamin.taminhamrah.model.workshop.ObjectionKind
import com.tamin.taminhamrah.model.workshop.WorkShopDebtPR
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.objection_estimate
import taminx.core.core_ui.objection_primary_vote
import taminx.core.core_ui.objection_view
import taminx.core.core_ui.payment_sheet_agreement_row
import taminx.core.core_ui.payment_sheet_debit_number
import taminx.core.core_ui.workshop_action_objection
import taminx.core.core_ui.workshop_debt_amount
import taminx.core.core_ui.workshop_debt_from_date
import taminx.core.core_ui.workshop_debt_notify_date
import taminx.core.core_ui.workshop_debt_to_date

/**
 * اعتراض به بدهی.
 *
 * Each row carries one action, and the debt itself decides which: the button is labelled from
 * the objection kind rather than from anything the screen keeps.
 */
@Composable
fun ObjectionableDebitScreen(
    workshopId: String,
    branchCode: String,
    onBack: () -> Unit,
    viewModel: ObjectionableDebitViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(workshopId, branchCode) {
        viewModel.sendIntent(ObjectionableDebitIntent.Open(workshopId, branchCode))
    }

    WorkshopScreenShell(
        title = stringResource(Res.string.workshop_action_objection),
        onBack = onBack,
    ) {
        WorkshopListScaffold(
            state = state.list,
            onLoadMore = { viewModel.sendIntent(ObjectionableDebitIntent.LoadMore) },
            key = { it.debitNumber },
        ) { debt ->
            ObjectionableDebtCard(
                debt = debt,
                onAction = { viewModel.sendIntent(ObjectionableDebitIntent.RowAction(debt)) },
            )
        }
    }
}

@Composable
private fun ObjectionableDebtCard(
    debt: WorkShopDebtPR,
    onAction: () -> Unit,
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
        DetailRow(
            label = stringResource(Res.string.workshop_debt_amount),
            value = debt.amount,
            valueColor = colors.orangeText,
        )
        DetailRow(stringResource(Res.string.workshop_debt_from_date), debt.fromDate)
        DetailRow(stringResource(Res.string.workshop_debt_to_date), debt.toDate)
        DetailRow(stringResource(Res.string.payment_sheet_agreement_row), debt.agreementRow)

        TaminPrimaryButton(
            text = stringResource(debt.objectionKind.label),
            onClick = onAction,
        )
    }
}

/** What the single row action is called: the objection it would file, or viewing the filed one. */
private val ObjectionKind.label
    get() = when (this) {
        ObjectionKind.ESTIMATE -> Res.string.objection_estimate
        ObjectionKind.PRIMARY_VOTE -> Res.string.objection_primary_vote
        ObjectionKind.FILED -> Res.string.objection_view
    }
