package com.tamin.taminhamrah.feature.treatment.ui.records

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tamin.taminhamrah.feature.treatment.ui.components.CostBreakdownCard
import com.tamin.taminhamrah.feature.treatment.ui.components.PrescriptionItemCard
import com.tamin.taminhamrah.feature.treatment.ui.components.RecordSummaryCard
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsIntent
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsUiState
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMocks
import com.tamin.taminhamrah.feature.treatment.ui.model.toJalaliDateLabel
import com.tamin.taminhamrah.feature.treatment.ui.prescriptions.PrescriptionsViewModel
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.SectionLabel
import com.tamin.taminhamrah.ui.components.TaminBottomBar
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.icons.TaminIcons
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.toPersianDigits
import org.koin.compose.viewmodel.koinViewModel

/** Clearance so the last card is not hidden behind the pinned action bar. */
private val BOTTOM_BAR_CLEARANCE = 100.dp

/** Shown when a field has not loaded, so a blank never reads as missing data. */
private const val UNKNOWN_VALUE = "—"

/**
 * One medical record: the prescribed items, the cost breakdown, and the PDF exports.
 *
 * Opening the record drives `getElectronicPrescriptionDetail` + `getElectronicPrescriptionPrice`;
 * the actions drive `getPrescriptionPdfFile` and `downloadTestResultPdf`.
 */
@Composable
fun RecordDetailScreen(
    nationalCode: String,
    noteHeadId: String,
    type: String,
    flagSata: String,
    docName: String,
    prescDate: String,
    trackingCode: String,
    onBack: () -> Unit,
    viewModel: PrescriptionsViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(noteHeadId, nationalCode, type, flagSata) {
        viewModel.sendIntent(
            PrescriptionsIntent.SelectPrescription(noteHeadId, nationalCode, type, flagSata),
        )
    }

    HandleRecordsEvents(events = viewModel.events, snackbarHostState = snackbarHostState)

    RecordDetailContent(
        state = state,
        noteHeadId = noteHeadId,
        docName = docName,
        prescDate = prescDate,
        trackingCode = trackingCode,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onDownloadPdf = { viewModel.sendIntent(PrescriptionsIntent.DownloadPdf(noteHeadId)) },
        onDownloadTestResult = {
            // The list reports the patient via patientID; "0" means the insured themselves.
            val patientId = state.prescriptionList
                .firstOrNull { it.noteHeadEprescID == noteHeadId }
                ?.patientID
                ?: "0"
            viewModel.sendIntent(PrescriptionsIntent.DownloadTestResult(patientId, noteHeadId))
        },
        onDismissPdf = { viewModel.sendIntent(PrescriptionsIntent.TogglePdfDialog(false)) },
        onRetry = {
            viewModel.sendIntent(
                PrescriptionsIntent.SelectPrescription(noteHeadId, nationalCode, type, flagSata),
            )
        },
    )
}

@Composable
fun RecordDetailContent(
    state: PrescriptionsUiState,
    noteHeadId: String,
    docName: String = "",
    prescDate: String = "",
    trackingCode: String = "",
    onBack: () -> Unit,
    onDownloadPdf: () -> Unit,
    onDownloadTestResult: () -> Unit,
    onDismissPdf: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val colors = LocalTaminColors.current
    val record = state.prescriptionList.firstOrNull { it.noteHeadEprescID == noteHeadId }
    val price = state.prescriptionPriceList.firstOrNull()

    Scaffold(
        modifier = modifier,
        containerColor = colors.bgPage,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TaminTopAppBar(
                    title = "نسخهٔ الکترونیک",
                    navigationIcon = {
                        TaminTopAppBarButton(
                            icon = TaminIcons.ChevronBack,
                            contentDescription = "بازگشت",
                            onClick = onBack,
                        )
                    },
                    action = {
                        TaminTopAppBarButton(
                            icon = TaminIcons.Download,
                            contentDescription = "دریافت جواب آزمایش",
                            onClick = onDownloadTestResult,
                        )
                    },
                )

                when {
                    state.isLoading -> Box(
                        modifier = Modifier.fillMaxSize().padding(Spacing.page),
                        contentAlignment = Alignment.Center,
                    ) { CircularProgressIndicator(color = colors.teal) }

                    // A failed lookup offers a retry; a genuinely empty prescription does not.
                    state.error != null -> ErrorStateView(
                        message = state.error,
                        onRetry = onRetry,
                    )

                    state.prescriptionDetailList.isEmpty() ->
                        TaminEmptyState(message = "برای این نسخه قلمی ثبت نشده است.")

                    else -> Column(
                        modifier = Modifier.padding(Spacing.page),
                        verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
                    ) {
                        RecordSummaryCard(
                            metaLabel = "پزشک",
                            metaValue = if (docName.isBlank()) UNKNOWN_VALUE else "دکتر $docName",
                            trackingCode = trackingCode.ifBlank { UNKNOWN_VALUE }.toPersianDigits(),
                            date = prescDate.ifBlank { UNKNOWN_VALUE }.toJalaliDateLabel(),
                        )

                        SectionLabel(text = "اقلام دارویی")
                        state.prescriptionDetailList.forEach { item ->
                            PrescriptionItemCard(
                                name = item.serviceName,
                                dose = item.drugInstruction.ifBlank { item.drugAmount },
                                prescribedCount = item.serviceQuantity.toPersianDigits(),
                                receivedCount = item.deliveredNo.toPersianDigits(),
                                centerName = item.serverName,
                                actionDate = item.registerDate.toJalaliDateLabel(),
                                itemTotal = item.sumPriceItem.toPriceFormat(),
                                insuredShare = item.insurancePayment.toPriceFormat(),
                            )
                        }

                        price?.let {
                            CostBreakdownCard(
                                total = it.requestPrice.toPriceFormat(),
                                organizationShare = it.headSsoPayment.toPriceFormat(),
                                insuredShare = it.headInsuPayment.toPriceFormat(),
                            )
                        }
                    }
                }

                Box(modifier = Modifier.height(BOTTOM_BAR_CLEARANCE))
            }

            TaminBottomBar(modifier = Modifier.align(Alignment.BottomCenter)) {
                TaminPrimaryButton(
                    text = "دریافت نسخهٔ الکترونیک",
                    icon = TaminIcons.Download,
                    onClick = onDownloadPdf,
                )
            }
        }
    }

    if (state.showPdfDialog && state.viewerPdf != null) {
        PdfViewerDialog(pdfData = state.viewerPdf, onDismiss = onDismissPdf)
    }
}

/**
 * PDF preview. Mirrors the app's existing viewer (see pensioner's): the file is downloaded but a
 * multiplatform renderer is not wired in yet, so this reports the download rather than drawing it.
 */
@Composable
fun PdfViewerDialog(
    pdfData: PdfDownloadPR,
    onDismiss: () -> Unit,
) {
    val colors = LocalTaminColors.current
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = colors.bgPage) {
            Column(modifier = Modifier.fillMaxSize()) {
                TaminTopAppBar(
                    title = "نمایش نسخه",
                    navigationIcon = {
                        TaminTopAppBarButton(
                            icon = TaminIcons.Cross,
                            contentDescription = "بستن",
                            onClick = onDismiss,
                        )
                    },
                )
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "فایل PDF با موفقیت دریافت شد",
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.textPrimary,
                        )
                        Box(modifier = Modifier.height(Spacing.sm))
                        Text(
                            text = "آماده نمایش (نیاز به پیاده‌سازی رندرینگ)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary,
                        )
                    }
                }
            }
        }
    }
}

@PreviewRtlTheme
@Composable
fun RecordDetailPreview() {
    PreviewRtlThemeContent {
        RecordDetailContent(
            state = TreatmentMocks.prescriptionsUiState.copy(selectedNoteHeadId = "TRK123456"),
            noteHeadId = "TRK123456",
            onBack = {},
            onDownloadPdf = {},
            onDownloadTestResult = {},
            onDismissPdf = {},
            onRetry = {},
        )
    }
}
