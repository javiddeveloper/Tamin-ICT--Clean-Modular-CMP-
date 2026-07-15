package com.tamin.taminhamrah.feature.treatment.ui.records

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tamin.taminhamrah.feature.treatment.ui.components.EmptyState
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsIntent
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsUiState
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMocks
import com.tamin.taminhamrah.feature.treatment.ui.prescriptions.PrescriptionsViewModel
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDetailPR
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionPricePR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat
import org.koin.compose.viewmodel.koinViewModel

/**
 * One medical record: the prescribed items, the cost breakdown, and the two PDF exports.
 *
 * Selecting the record drives `getElectronicPrescriptionDetail` + `getElectronicPrescriptionPrice`;
 * the buttons drive `getPrescriptionPdfFile` and `downloadTestResultPdf`.
 */
@Composable
fun RecordDetailScreen(
    nationalCode: String,
    noteHeadId: String,
    onBack: () -> Unit,
    viewModel: PrescriptionsViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(noteHeadId, nationalCode) {
        viewModel.sendIntent(PrescriptionsIntent.SelectPrescription(noteHeadId, nationalCode))
    }

    HandleRecordsEvents(events = viewModel.events, snackbarHostState = snackbarHostState)

    RecordDetailContent(
        state = state,
        noteHeadId = noteHeadId,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onDownloadPdf = { viewModel.sendIntent(PrescriptionsIntent.DownloadPdf(noteHeadId)) },
        onDownloadTestResult = {
            // The list reports the patient via patientID; "0" means the insured themselves.
            val patientId = state.prescriptionList
                .firstOrNull { it.trackingCode == noteHeadId }
                ?.patientID
                ?: "0"
            viewModel.sendIntent(PrescriptionsIntent.DownloadTestResult(patientId, noteHeadId))
        },
        onDismissPdf = { viewModel.sendIntent(PrescriptionsIntent.TogglePdfDialog(false)) }
    )
}

@Composable
fun RecordDetailContent(
    state: PrescriptionsUiState,
    noteHeadId: String,
    onBack: () -> Unit,
    onDownloadPdf: () -> Unit,
    onDownloadTestResult: () -> Unit,
    onDismissPdf: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("جزئیات نسخه", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        when {
            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            state.prescriptionDetailList.isEmpty() ->
                Box(modifier = Modifier.padding(padding)) { EmptyState("جزئیاتی برای این نسخه یافت نشد.") }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                state.prescriptionPriceList.firstOrNull()?.let { price ->
                    item { PrescriptionCostCard(price = price) }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Button(onClick = onDownloadPdf, modifier = Modifier.weight(1f)) {
                            Text("دریافت PDF نسخه")
                        }
                        OutlinedButton(onClick = onDownloadTestResult, modifier = Modifier.weight(1f)) {
                            Text("PDF جواب آزمایش")
                        }
                    }
                }

                item {
                    Text(
                        text = "اقلام تجویز شده",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                items(state.prescriptionDetailList) { item -> PrescriptionItemCard(item = item) }
            }
        }
    }

    if (state.showPdfDialog && state.viewerPdf != null) {
        PdfViewerDialog(pdfData = state.viewerPdf, onDismiss = onDismissPdf)
    }
}

/** Head-level cost breakdown for the whole prescription. */
@Composable
private fun PrescriptionCostCard(price: ElectronicPrescriptionPricePR) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(
                text = "هزینه کل نسخه",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "${price.requestPrice.toPriceFormat()} ریال",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(Spacing.xs))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "سهم سازمان",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "${price.headSsoPayment.toPriceFormat()} ریال",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "سهم بیمه‌شده",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "${price.headInsuPayment.toPriceFormat()} ریال",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

/** One prescribed item: name, counts, dosage and its share of the cost. */
@Composable
private fun PrescriptionItemCard(item: ElectronicPrescriptionDetailPR) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(
                text = item.serviceName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                Text(
                    text = "تجویزی: ${item.serviceQuantity}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "دریافتی: ${item.deliveredNo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (item.drugInstruction.isNotEmpty()) {
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = "دستور مصرف: ${item.drugInstruction}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(Spacing.xs))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(Spacing.xs))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "قیمت کل: ${item.sumPriceItem.toPriceFormat()} ریال",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "سهم بیمه‌شده: ${item.insurancePayment.toPriceFormat()} ریال",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * PDF preview. Mirrors the app's existing viewer (see pensioner's): the bytes are downloaded but a
 * multiplatform renderer is not wired in yet, so this reports the download rather than drawing it.
 */
@Composable
fun PdfViewerDialog(
    pdfData: PdfDownloadPR,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = { Text("نمایش PDF") },
                    actions = {
                        TextButton(onClick = onDismiss) {
                            Text("بستن")
                        }
                    }
                )
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "فایل PDF با موفقیت دریافت شد",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                        Text(
                            text = "آماده نمایش (نیاز به پیاده‌سازی رندرینگ)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
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
            onDismissPdf = {}
        )
    }
}
