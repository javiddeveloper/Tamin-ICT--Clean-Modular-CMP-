package com.tamin.taminhamrah.feature.workshops.ui.demandDocuments

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopCardButton
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopCardButtonTone
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopListScaffold
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopRecordCard
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopScreenShell
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSectionHeader
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.model.workshop.WorkshopDemandDocPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.persistentListOf
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
import taminx.core.core_ui.workshop_demand_docs_empty
import taminx.core.core_ui.workshop_docs_debit_heading
import taminx.core.core_ui.workshop_turnover_filename_format
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.ic_tamin_document_lines

/**
 * اسناد مطالبه of one debt, each openable as a PDF.
 *
 * The design heads the list with the debt these documents belong to rather than with the screen's
 * own title — this screen is only ever reached from one row of گردش حساب بدهی.
 */
@Composable
fun DemandDocumentsScreen(
    debitNumber: String,
    branchCode: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    workshopName: String = "",
    viewModel: DemandDocumentsViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(debitNumber, branchCode) {
        viewModel.sendIntent(DemandDocumentsIntent.Open(debitNumber, branchCode))
    }

    DemandDocumentsContent(
        state = state,
        debitNumber = debitNumber,
        workshopName = workshopName,
        onIntent = viewModel::sendIntent,
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
fun DemandDocumentsContent(
    state: DemandDocumentsUiState,
    debitNumber: String,
    workshopName: String,
    onIntent: (DemandDocumentsIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Opening is a screen concern: the view model only owns the bytes, and the viewer asks for
    // those itself once it finds nothing cached.
    var isViewerOpen by remember(debitNumber) { mutableStateOf(false) }
    val colors = LocalTaminColors.current

    WorkshopScreenShell(
        title = stringResource(Res.string.workshop_debt_documents),
        onBack = onBack,
        workshopName = workshopName.takeIf { it.isNotBlank() },
        modifier = modifier,
    ) {
        val heading = stringResource(
            Res.string.workshop_docs_debit_heading,
            debitNumber.toPersianDigits(),
        )
        WorkshopListScaffold(
            emptyIcon = vectorResource(Res.drawable.ic_tamin_document_lines),
            emptyMessage = stringResource(Res.string.workshop_demand_docs_empty),
            state = state.list,
            onLoadMore = { onIntent(DemandDocumentsIntent.LoadMore) },
            onRetry = { onIntent(DemandDocumentsIntent.Retry) },
            key = { it.docNumber },
            header = {
                WorkshopSectionHeader(
                    title = heading,
                    count = state.list.items.size,
                    copyValue = debitNumber,
                )
            },
        ) { document, rowModifier ->
            DemandDocumentCard(
                document = document,
                onShowCalculation = { isViewerOpen = true },
                modifier = rowModifier,
            )
        }
    }

    if (isViewerOpen) {
        TaminPdfViewer(
            fileName = stringResource(
                Res.string.workshop_turnover_filename_format,
                debitNumber,
            ),
            // The viewer opens over this feature, so it keeps this feature's bar rather than the
            // app-wide default, which is a different hue entirely.
            background = Brush.horizontalGradient(colors.profileGradientStops),
            pdf = state.viewerPdf,
            downloadFailed = state.downloadFailed,
            onRequestDownload = { onIntent(DemandDocumentsIntent.ShowCalculationPdf) },
            onDismiss = {
                isViewerOpen = false
                onIntent(DemandDocumentsIntent.DismissViewer)
            },
        )
    }
}

@Composable
private fun DemandDocumentCard(
    document: WorkshopDemandDocPR,
    onShowCalculation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    WorkshopRecordCard(
        modifier = modifier,
        // A row with no document number has nothing to open, so it gets no action row at all.
        buttons = if (document.isViewable) {
            {
                WorkshopCardButton(
                    text = stringResource(Res.string.workshop_demand_doc_calculation),
                    tone = WorkshopCardButtonTone.OUTLINE,
                    onClick = onShowCalculation,
                )
            }
        } else {
            null
        },
    ) {
        DetailRow(
            label = stringResource(Res.string.workshop_demand_doc_number),
            value = document.docNumberLabel,
            copyValue = document.docNumber,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.workshop_demand_doc_date),
            value = document.docDate,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.workshop_demand_doc_type),
            value = document.docType,
            numeric = false,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.workshop_demand_doc_step),
            value = document.step,
            numeric = false,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.workshop_demand_doc_state),
            value = document.state,
            valueColor = colors.blueText,
            numeric = false,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun DemandDocumentsScreenPreview() {
    PreviewRtlThemeContent {
        DemandDocumentsContent(
            state = DemandDocumentsUiState(
                list = PagedListState(
                    items = persistentListOf(
                        WorkshopDemandDocPR(
                            docNumber = "1405002391",
                            docNumberLabel = "۱۴۰۵۰۰۲۳۹۱",
                            docDate = "۱۴۰۵/۰۴/۱۸",
                            docType = "برگ تشخیص بدهی",
                            step = "مرحلهٔ بدوی",
                            state = "ابلاغ شده",
                        ),
                    ),
                ),
            ),
            debitNumber = "1405002391",
            workshopName = "آموزشگاه کامپیوتر توکلی-ایمیل",
            onIntent = {},
            onBack = {},
        )
    }
}
