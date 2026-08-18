package com.tamin.taminhamrah.feature.workshops.ui.managementDebit

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
import com.tamin.taminhamrah.feature.workshops.ui.components.colors
import com.tamin.taminhamrah.feature.workshops.ui.components.tint
import com.tamin.taminhamrah.model.workshop.Article16DebtPR
import com.tamin.taminhamrah.model.workshop.Article16RequestStatus
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.article16_account_code
import taminx.core.core_ui.article16_action_request
import taminx.core.core_ui.article16_action_view_request
import taminx.core.core_ui.article16_debt_amount
import taminx.core.core_ui.article16_debt_remaining
import taminx.core.core_ui.article16_executive_notify_date
import taminx.core.core_ui.article16_status_approved
import taminx.core.core_ui.article16_status_document_defect
import taminx.core.core_ui.article16_status_rejected
import taminx.core.core_ui.article16_status_submitted
import taminx.core.core_ui.article16_status_unknown
import taminx.core.core_ui.payment_sheet_agreement_row
import taminx.core.core_ui.workshop_action_article16
import taminx.core.core_ui.workshop_debt_from_date
import taminx.core.core_ui.workshop_debt_to_date

/**
 * رسیدگی به بدهی ماده ۱۶.
 *
 * What a row offers follows from its request state: a debt with no request yet can have one filed,
 * one held for نقص مدارک can be corrected, and anything else can only be viewed.
 */
@Composable
fun ManagementDebitScreen(
    workshopId: String,
    branchCode: String,
    workshopName: String,
    onBack: () -> Unit,
    viewModel: ManagementDebitViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(workshopId, branchCode) {
        viewModel.sendIntent(ManagementDebitIntent.Open(workshopId, branchCode, workshopName))
    }

    WorkshopScreenShell(
        title = stringResource(Res.string.workshop_action_article16),
        subtitle = state.workshopName.takeIf { it.isNotBlank() },
        onBack = onBack,
    ) {
        WorkshopListScaffold(
            state = state.list,
            onLoadMore = { viewModel.sendIntent(ManagementDebitIntent.LoadMore) },
            key = { it.debitNumber },
        ) { debt ->
            Article16DebtCard(
                debt = debt,
                onAction = { viewModel.sendIntent(ManagementDebitIntent.ActionsRequested(debt)) },
            )
        }
    }
}

@Composable
private fun Article16DebtCard(
    debt: Article16DebtPR,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val (background, foreground) = debt.status.tint.colors()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.lg)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        StatusPill(
            text = stringResource(debt.status.label),
            containerColor = background,
            contentColor = foreground,
        )
        DetailRow(stringResource(Res.string.article16_account_code), debt.debitNumberLabel)
        DetailRow(
            label = stringResource(Res.string.article16_debt_amount),
            value = debt.amount,
            valueColor = colors.blueText,
        )
        DetailRow(
            label = stringResource(Res.string.article16_debt_remaining),
            value = debt.remainingAmount,
            valueColor = colors.orangeText,
        )
        DetailRow(stringResource(Res.string.workshop_debt_from_date), debt.fromDate)
        DetailRow(stringResource(Res.string.workshop_debt_to_date), debt.toDate)
        DetailRow(stringResource(Res.string.payment_sheet_agreement_row), debt.agreementRow)
        DetailRow(
            stringResource(Res.string.article16_executive_notify_date),
            debt.executiveNotifyDateLabel,
        )

        TaminPrimaryButton(
            text = stringResource(
                if (debt.status == Article16RequestStatus.NONE) {
                    Res.string.article16_action_request
                } else {
                    Res.string.article16_action_view_request
                }
            ),
            onClick = onAction,
        )
    }
}

/** The wording each request state is listed under. */
private val Article16RequestStatus.label
    get() = when (this) {
        Article16RequestStatus.SUBMITTED -> Res.string.article16_status_submitted
        Article16RequestStatus.DOCUMENT_DEFECT -> Res.string.article16_status_document_defect
        Article16RequestStatus.REJECTED -> Res.string.article16_status_rejected
        Article16RequestStatus.APPROVED -> Res.string.article16_status_approved
        Article16RequestStatus.NONE, Article16RequestStatus.UNKNOWN ->
            Res.string.article16_status_unknown
    }
