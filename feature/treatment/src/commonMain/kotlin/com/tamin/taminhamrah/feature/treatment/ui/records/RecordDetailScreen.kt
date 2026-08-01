package com.tamin.taminhamrah.feature.treatment.ui.records

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.shimmer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import com.tamin.taminhamrah.feature.treatment.ui.components.CostTotalsBar
import com.tamin.taminhamrah.feature.treatment.ui.components.PrescriptionItemCard
import com.tamin.taminhamrah.feature.treatment.ui.components.RecordSummaryCard
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsIntent
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsUiState
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMocks
import com.tamin.taminhamrah.feature.treatment.ui.model.toJalaliDateLabel
import com.tamin.taminhamrah.feature.treatment.ui.prescriptions.PrescriptionsViewModel
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.SectionLabel
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_back
import taminx.core.core_ui.amount_total
import taminx.core.core_ui.detail_doctor
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_download
import taminx.core.core_ui.prescription_download_cd
import taminx.core.core_ui.prescription_empty
import taminx.core.core_ui.prescription_items
import taminx.core.core_ui.prescription_lab_result_cd
import taminx.core.core_ui.prescription_title
import taminx.core.core_ui.records_doctor_named
import taminx.core.core_ui.share_organization
import taminx.core.core_ui.share_yours
import com.tamin.taminhamrah.feature.treatment.ui.TreatmentDimens

/** Shown when a field has not loaded, so a blank never reads as missing data. */
private const val UNKNOWN_VALUE = "—"

/**
 * The exports this screen can show. Each names its own file, which is what lets the viewer
 * recognize one it has downloaded before and skip the request entirely.
 */
private enum class PdfExport {
    PRESCRIPTION,
    LAB_RESULT,
    ;

    fun fileName(noteHeadId: String): String = when (this) {
        PRESCRIPTION -> "prescription_$noteHeadId.pdf"
        LAB_RESULT -> "lab_result_$noteHeadId.pdf"
    }
}

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

    HandleRecordsEvents(
        events = viewModel.events,
        snackbarHostState = snackbarHostState,
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
        onDismissPdf = { viewModel.sendIntent(PrescriptionsIntent.DismissPdfViewer) },
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

    // Which export is on screen. Opening the viewer no longer means a download has happened: it
    // decides for itself whether the file needs fetching, so the tap only says which one to show.
    var showing by remember { mutableStateOf<PdfExport?>(null) }

    Scaffold(
        modifier = modifier,
        containerColor = colors.bgPage,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TaminTopAppBar(
                    title = stringResource(Res.string.prescription_title),
                    navigationIcon = {
                        TaminTopAppBarButton(
                            icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                            contentDescription = stringResource(Res.string.action_back),
                            onClick = onBack,
                        )
                    },
                    action = {
                        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                            TaminTopAppBarButton(
                                icon = vectorResource(Res.drawable.ic_tamin_download),
                                contentDescription = stringResource(Res.string.prescription_download_cd),
                                onClick = { showing = PdfExport.PRESCRIPTION },
                            )
                            TaminTopAppBarButton(
                                icon = vectorResource(Res.drawable.ic_tamin_download),
                                contentDescription = stringResource(Res.string.prescription_lab_result_cd),
                                onClick = { showing = PdfExport.LAB_RESULT },
                            )
                        }
                    },
                )

                when {
                    state.isLoading -> RecordDetailShimmerSkeleton()

                    // A failed lookup offers a retry; a genuinely empty prescription does not.
                    state.error != null -> ErrorStateView(
                        message = state.error,
                        onRetry = onRetry,
                    )

                    state.prescriptionDetailList.isEmpty() ->
                        TaminEmptyState(message = stringResource(Res.string.prescription_empty))

                    else -> Column(
                        modifier = Modifier.padding(Spacing.page),
                        verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
                    ) {
                        RecordSummaryCard(
                            metaLabel = stringResource(Res.string.detail_doctor),
                            metaValue = if (docName.isBlank()) {
            UNKNOWN_VALUE
        } else {
            stringResource(Res.string.records_doctor_named, docName)
        },
                            trackingCode = trackingCode.ifBlank { UNKNOWN_VALUE }.toPersianDigits(),
                            date = prescDate.ifBlank { UNKNOWN_VALUE }.toJalaliDateLabel(),
                        )

                        SectionLabel(text = stringResource(Res.string.prescription_items))
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

                Box(modifier = Modifier.height(TreatmentDimens.bottomBarClearance))
            }

            price?.let {
                CostTotalsBar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    insuredShareLabel = stringResource(Res.string.share_yours),
                    insuredShareAmount = it.headSsoPayment.toPriceFormat(),
                    organizationShareLabel = stringResource(Res.string.share_organization),
                    organizationShareAmount = it.headInsuPayment.toPriceFormat(),
                    totalLabel = stringResource(Res.string.amount_total),
                    totalAmount = it.requestPrice.toPriceFormat(),
                )
            }
        }
    }

    showing?.let { export ->
        TaminPdfViewer(
            fileName = export.fileName(noteHeadId),
            pdf = state.viewerPdf,
            downloadFailed = state.viewerDownloadFailed,
            onRequestDownload = {
                when (export) {
                    PdfExport.PRESCRIPTION -> onDownloadPdf()
                    PdfExport.LAB_RESULT -> onDownloadLabResult()
                }
            },
            onDismiss = {
                showing = null
                onDismissPdf()
            },
        )
    }
}

@Composable
private fun RecordDetailShimmerSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.page),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .taminSurface(CornerRadius.card)
                .shimmer(),
        )
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .taminSurface(CornerRadius.cardCompact)
                    .shimmer(),
            )
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
