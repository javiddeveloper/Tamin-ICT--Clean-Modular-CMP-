package com.tamin.taminhamrah.feature.workshops.ui.paymentSheets

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
import com.tamin.taminhamrah.model.workshop.PaymentSheetPR
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.payment_sheet_agreement_row
import taminx.core.core_ui.payment_sheet_amount
import taminx.core.core_ui.payment_sheet_collect_date
import taminx.core.core_ui.payment_sheet_debit_number
import taminx.core.core_ui.payment_sheet_debit_reason
import taminx.core.core_ui.payment_sheet_document_number
import taminx.core.core_ui.payment_sheet_issue_date
import taminx.core.core_ui.payment_sheet_pay_kind
import taminx.core.core_ui.workshop_action_payment_sheets

/** برگ پرداخت‌ها of one workshop. */
@Composable
fun PaymentSheetsScreen(
    workshopId: String,
    branchCode: String,
    onBack: () -> Unit,
    viewModel: PaymentSheetsViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(workshopId, branchCode) {
        viewModel.sendIntent(PaymentSheetsIntent.Open(workshopId, branchCode))
    }

    WorkshopScreenShell(
        title = stringResource(Res.string.workshop_action_payment_sheets),
        onBack = onBack,
    ) {
        WorkshopListScaffold(
            state = state.list,
            onLoadMore = { viewModel.sendIntent(PaymentSheetsIntent.LoadMore) },
            key = { it.debitNumber + it.agreementRow },
        ) { sheet -> PaymentSheetCard(sheet) }
    }
}

@Composable
private fun PaymentSheetCard(sheet: PaymentSheetPR, modifier: Modifier = Modifier) {
    val (background, foreground) = sheet.status.tint.colors()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.lg)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        StatusPill(
            text = sheet.statusLabel,
            containerColor = background,
            contentColor = foreground,
        )
        DetailRow(stringResource(Res.string.payment_sheet_debit_number), sheet.debitNumber)
        DetailRow(stringResource(Res.string.payment_sheet_collect_date), sheet.collectDate)
        DetailRow(stringResource(Res.string.payment_sheet_amount), sheet.amount)
        DetailRow(stringResource(Res.string.payment_sheet_agreement_row), sheet.agreementRow)
        DetailRow(stringResource(Res.string.payment_sheet_issue_date), sheet.issueDate)
        DetailRow(
            label = stringResource(Res.string.payment_sheet_pay_kind),
            value = sheet.payKind,
            numeric = false,
        )
        DetailRow(
            label = stringResource(Res.string.payment_sheet_debit_reason),
            value = sheet.debitReason,
            numeric = false,
        )
        DetailRow(stringResource(Res.string.payment_sheet_document_number), sheet.documentNumber)
    }
}
