package com.tamin.taminhamrah.feature.workshops.ui.demandDocuments

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
import com.tamin.taminhamrah.model.workshop.WorkshopDemandDocPR
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.workshop_debt_documents
import taminx.core.core_ui.workshop_demand_doc_calculation
import taminx.core.core_ui.workshop_demand_doc_date
import taminx.core.core_ui.workshop_demand_doc_number
import taminx.core.core_ui.workshop_demand_doc_state
import taminx.core.core_ui.workshop_demand_doc_step
import taminx.core.core_ui.workshop_demand_doc_type

/** اسناد مطالبه of one debt, each openable as a PDF. */
@Composable
fun DemandDocumentsScreen(
    debitNumber: String,
    branchCode: String,
    onBack: () -> Unit,
    viewModel: DemandDocumentsViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(debitNumber, branchCode) {
        viewModel.sendIntent(DemandDocumentsIntent.Open(debitNumber, branchCode))
    }

    WorkshopScreenShell(
        title = stringResource(Res.string.workshop_debt_documents),
        onBack = onBack,
    ) {
        WorkshopListScaffold(
            state = state.list,
            onLoadMore = { viewModel.sendIntent(DemandDocumentsIntent.LoadMore) },
            key = { it.docNumber },
        ) { document ->
            DemandDocumentCard(
                document = document,
                onShowCalculation = {
                    viewModel.sendIntent(DemandDocumentsIntent.ShowCalculationPdf)
                },
            )
        }
    }
}

@Composable
private fun DemandDocumentCard(
    document: WorkshopDemandDocPR,
    onShowCalculation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.lg)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        DetailRow(stringResource(Res.string.workshop_demand_doc_number), document.docNumberLabel)
        DetailRow(stringResource(Res.string.workshop_demand_doc_date), document.docDate)
        DetailRow(
            label = stringResource(Res.string.workshop_demand_doc_type),
            value = document.docType,
            numeric = false,
        )
        DetailRow(
            label = stringResource(Res.string.workshop_demand_doc_step),
            value = document.step,
            numeric = false,
        )
        DetailRow(
            label = stringResource(Res.string.workshop_demand_doc_state),
            value = document.state,
            numeric = false,
        )

        // A row with no document number has nothing to open.
        if (document.isViewable) {
            TaminOutlinedButton(
                text = stringResource(Res.string.workshop_demand_doc_calculation),
                onClick = onShowCalculation,
            )
        }
    }
}
