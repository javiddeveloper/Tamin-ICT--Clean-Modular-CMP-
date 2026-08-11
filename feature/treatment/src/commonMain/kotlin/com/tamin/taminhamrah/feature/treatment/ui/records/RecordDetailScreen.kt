package com.tamin.taminhamrah.feature.treatment.ui.records

import com.tamin.taminhamrah.feature.treatment.ui.components.raisedCard
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
import com.tamin.taminhamrah.ui.components.staggeredItemEntrance
import com.tamin.taminhamrah.ui.components.rememberStaggeredEntranceState
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
import com.tamin.taminhamrah.feature.treatment.ui.components.PrescriptionItemCard
import com.tamin.taminhamrah.feature.treatment.ui.components.RecordSummaryCard
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsIntent
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsUiState
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentRecordPdfExport
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMocks
import com.tamin.taminhamrah.feature.treatment.ui.model.hasLabResult
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
import com.tamin.taminhamrah.ui.components.taminTopAppBarGradient
import com.tamin.taminhamrah.ui.util.ExternalAppLauncher
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.lab_result_viewer_title
import taminx.core.core_ui.prescription_viewer_title
import taminx.core.core_ui.action_back
import taminx.core.core_ui.amount_total
import taminx.core.core_ui.detail_doctor
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_share
import taminx.core.core_ui.ic_tamin_download
import taminx.core.core_ui.ic_tamin_print
import taminx.core.core_ui.prescription_download_cd
import taminx.core.core_ui.prescription_empty
import taminx.core.core_ui.prescription_items
import taminx.core.core_ui.prescription_lab_result_cd
import taminx.core.core_ui.prescription_share_body
import taminx.core.core_ui.prescription_share_cd
import taminx.core.core_ui.prescription_title
import taminx.core.core_ui.records_doctor_named
import taminx.core.core_ui.share_organization
import taminx.core.core_ui.share_yours
import com.tamin.taminhamrah.feature.treatment.ui.TreatmentDimens
import com.tamin.taminhamrah.feature.treatment.ui.components.CostTotalsBar

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

    HandleRecordsEvents(
        events = viewModel.events,
        snackbarHostState = snackbarHostState,
    )

    RecordDetailContent(
        state = state,
        noteHeadId = noteHeadId,
        type = type,
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
    /** The record's `prescType`; decides whether a lab result exists to offer. */
    type: String = "",
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

    /*
     * The record's total, added up from the items on screen rather than read off the price
     * endpoint. Two reasons: that endpoint returns nothing for plenty of records -- which is why
     * the total was simply absent -- and a figure that disagrees with the tiles printed on each
     * item above it is worse than no figure at all.
     *
     * Inside a remember so a scroll or a dialog does not re-add the whole list.
     */
    val totals = remember(state.prescriptionDetailList) {
        state.prescriptionDetailList.fold(RecordCostTotals()) { running, item ->
            RecordCostTotals(
                insuredShare = running.insuredShare + (item.ssoPayment.toLongOrNull() ?: 0L),
                organizationShare = running.organizationShare + (item.insurancePayment.toLongOrNull() ?: 0L),
                total = running.total + (item.sumPriceItem.toLongOrNull() ?: 0L),
            )
        }
    }
    // Remembers item entrance animations for prescription items, keyed on the prescription note head ID.
    val staggerState = rememberStaggeredEntranceState(key = noteHeadId)

    // The share sheet sends the record as text: every target app accepts it, which a PDF blob
    // fetched into memory would not without a FileProvider on one platform and a temp URL on the other.
    val launcher = remember { ExternalAppLauncher() }
    val shareTitle = stringResource(Res.string.prescription_title)
    val shareBody = stringResource(
        Res.string.prescription_share_body,
        shareTitle,
        docName.ifBlank { UNKNOWN_VALUE },
        trackingCode.ifBlank { UNKNOWN_VALUE }.toPersianDigits(),
        prescDate.ifBlank { UNKNOWN_VALUE }.toJalaliDateLabel(),
        totals.total.toPriceFormat(),
    )

    // Which export is on screen. Opening the viewer no longer means a download has happened: it
    // decides for itself whether the file needs fetching, so the tap only says which one to show.
    var showing by remember { mutableStateOf<TreatmentRecordPdfExport?>(null) }

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
                                icon = vectorResource(Res.drawable.ic_share),
                                contentDescription = stringResource(Res.string.prescription_share_cd),
                                onClick = { launcher.shareText(shareBody, shareTitle) },
                            )
                            TaminTopAppBarButton(
                                icon = vectorResource(Res.drawable.ic_tamin_download),
                                contentDescription = stringResource(Res.string.prescription_download_cd),
                                onClick = { showing = TreatmentRecordPdfExport.PRESCRIPTION },
                            )
                            // Paraclinic records only. Two identical download glyphs sat here
                            // before, one of which could only fail on a drug prescription --
                            // invisible until the bar stopped clipping its own actions.
                            if (type.hasLabResult()) TaminTopAppBarButton(
                                icon = vectorResource(Res.drawable.ic_tamin_print),
                                contentDescription = stringResource(Res.string.prescription_lab_result_cd),
                                onClick = { showing = TreatmentRecordPdfExport.LAB_RESULT },
                            )
                        }
                    },
                )

                when {
                    state.isLoading -> RecordDetailShimmerSkeleton()

                    // Guarded on error: a failed lookup knows nothing about whether the
                    // prescription has items, and saying it is empty would be a lie the dialog
                    // then contradicts.
                    state.error == null && state.prescriptionDetailList.isEmpty() ->
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
                            trackingCodeRaw = trackingCode,
                            date = prescDate.ifBlank { UNKNOWN_VALUE }.toJalaliDateLabel(),
                        )

                        SectionLabel(text = stringResource(Res.string.prescription_items))
                        state.prescriptionDetailList.forEachIndexed { index, item ->
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
                                modifier = Modifier.staggeredItemEntrance(index = index, key = item, state = staggerState),
                            )
                        }

                    }
                }

                Box(modifier = Modifier.height(TreatmentDimens.bottomBarClearance))
            }

            // Over the page rather than instead of it: the body falls through to its empty
            // state, so dismissing the dialog does not leave a bare top bar behind.
            ErrorStateView(message = state.error, onDismiss = onBack, onRetry = onRetry)

            // Pinned, exactly as on the records timeline: the total is what the page is scrolled
            // for, and a card at the very end only shows itself once the reading is finished.
            CostTotalsBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                insuredShareLabel = stringResource(Res.string.share_yours),
                insuredShareAmount = totals.insuredShare.toPriceFormat(),
                organizationShareLabel = stringResource(Res.string.share_organization),
                organizationShareAmount = totals.organizationShare.toPriceFormat(),
                totalLabel = stringResource(Res.string.amount_total),
                totalAmount = totals.total.toPriceFormat(),
            )
        }
    }

    showing?.let { export ->
        TaminPdfViewer(
            fileName = export.fileName(noteHeadId),
            title = when (export) {
                TreatmentRecordPdfExport.PRESCRIPTION -> stringResource(Res.string.prescription_viewer_title)
                TreatmentRecordPdfExport.LAB_RESULT -> stringResource(Res.string.lab_result_viewer_title)
            },
            background = taminTopAppBarGradient(colors.topAppBarStops),
            pdf = state.viewerPdf,
            downloadFailed = state.viewerDownloadFailed,
            onRequestDownload = {
                when (export) {
                    TreatmentRecordPdfExport.PRESCRIPTION -> onDownloadPdf()
                    TreatmentRecordPdfExport.LAB_RESULT -> onDownloadLabResult()
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
                .raisedCard(CornerRadius.card)
                .shimmer(),
        )
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .raisedCard(CornerRadius.cardCompact)
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

/**
 * A record's money, added up across its items.
 *
 * Longs rather than the formatted strings the items carry: adding «۲٬۰۳۷٬۷۰۰» to «۶۱۱٬۳۱۰» is not
 * a thing you can do, and formatting once at the end is also one pass instead of three.
 *
 * Not a `*PR` model and deliberately not `@Immutable`: it never leaves this file, never crosses a
 * composable parameter, and is only ever the accumulator of the fold below — so there is no
 * stability for the annotation to promise anyone.
 */
private data class RecordCostTotals(
    val insuredShare: Long = 0L,
    val organizationShare: Long = 0L,
    val total: Long = 0L,
)
