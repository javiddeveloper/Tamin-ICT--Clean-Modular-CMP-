package com.tamin.taminhamrah.feature.treatment.ui.records

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tamin.taminhamrah.feature.treatment.ui.components.CostTotalsBar
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
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.PdfDocumentView
import com.tamin.taminhamrah.ui.components.rememberPdfDownloader
import com.tamin.taminhamrah.ui.components.SectionLabel
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.ic_tamin_download

/** Clearance so the last card is not hidden behind the pinned action bar. */
private val BOTTOM_BAR_CLEARANCE = 100.dp

/** Shown when a field has not loaded, so a blank never reads as missing data. */
private const val UNKNOWN_VALUE = "—"

/**
 * One medical record: the prescribed items, the cost breakdown, and the PDF exports.
 *
 * Opening the record drives `getElectronicPrescriptionDetail` + `getElectronicPrescriptionPrice`;
 * the actions drive `getPrescriptionPdfFile` and `downloadLabResultPdf`.
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

    val pdfDownloader = rememberPdfDownloader()
    HandleRecordsEvents(
        events = viewModel.events,
        snackbarHostState = snackbarHostState,
        onPdfReady = { pdf, fileName -> pdfDownloader.download(fileName, pdf) },
    )

    RecordDetailContent(
        state = state,
        noteHeadId = noteHeadId,
        docName = docName,
        prescDate = prescDate,
        trackingCode = trackingCode,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onDownloadPdf = { viewModel.sendIntent(PrescriptionsIntent.DownloadPdf(noteHeadId)) },
        onDownloadLabResult = {
            // The list reports the patient via patientID; "0" means the insured themselves.
            val patientId = state.prescriptionList
                .firstOrNull { it.noteHeadEprescID == noteHeadId }
                ?.patientID
                ?: "0"
            viewModel.sendIntent(PrescriptionsIntent.DownloadLabResult(patientId, noteHeadId))
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
    onDownloadLabResult: () -> Unit,
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
                            icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                            contentDescription = "بازگشت",
                            onClick = onBack,
                        )
                    },
                    action = {
                        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                            TaminTopAppBarButton(
                                icon = vectorResource(Res.drawable.ic_tamin_download),
                                contentDescription = "دریافت نسخهٔ الکترونیک",
                                onClick = onDownloadPdf,
                            )
                            TaminTopAppBarButton(
                                icon = vectorResource(Res.drawable.ic_tamin_download),
                                contentDescription = "دریافت جواب آزمایش",
                                onClick = onDownloadLabResult,
                            )
                        }
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
                                patientShare = item.ssoPayment.toPriceFormat(),
                                organizationShare = item.insurancePayment.toPriceFormat(),
                            )
                        }


                    }
                }

                Box(modifier = Modifier.height(BOTTOM_BAR_CLEARANCE))
            }

            price?.let {
                CostTotalsBar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    insuredShareLabel = "سهم شما",
                    insuredShareAmount = it.headSsoPayment.toPriceFormat(),
                    organizationShareLabel = "سهم سازمان",
                    organizationShareAmount = it.headInsuPayment.toPriceFormat(),
                    totalLabel = "جمع کل",
                    totalAmount = it.requestPrice.toPriceFormat(),
                )
            }
        }
    }

    if (state.showPdfDialog && state.viewerPdf != null) {
        PdfViewerDialog(pdfData = state.viewerPdf, onDismiss = onDismissPdf)
    }
}

/**
 * Full-screen PDF preview. The downloaded bytes are drawn by the shared multiplatform
 * [PdfDocumentView] (Android PdfRenderer / iOS PDFKit); a missing file shows a short notice.
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
                            icon = vectorResource(Res.drawable.ic_tamin_cross),
                            contentDescription = "بستن",
                            onClick = onDismiss,
                        )
                    },
                )
                // core-ui drains the channel and renders; the feature never touches ktor.
                PdfDocumentView(pdf = pdfData, modifier = Modifier.fillMaxSize())
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
            onDownloadLabResult = {},
            onDismissPdf = {},
            onRetry = {},
        )
    }
}
