package com.tamin.taminhamrah.feature.treatment.ui.prescriptions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tamin.taminhamrah.feature.treatment.ui.components.EmptyState
import com.tamin.taminhamrah.feature.treatment.ui.components.SubFlowHeader
import com.tamin.taminhamrah.feature.treatment.ui.contract.PrescriptionsUiState
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMocks
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat

@Composable
fun PrescriptionsContent(
    state: PrescriptionsUiState,
    onBack: () -> Unit,
    onPrescriptionSelected: (String) -> Unit,
    onClearSelected: () -> Unit,
    onDownloadPdf: (String) -> Unit,
    onDownloadTestResult: (String, String) -> Unit,
    onDismissPdf: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (state.selectedNoteHeadId == null) {
            SubFlowHeader(title = "لیست نسخه‌های الکترونیک", onBack = onBack)

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.prescriptionList.isEmpty()) {
                EmptyState("هیچ نسخه‌ای یافت نشد.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.xs),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    items(state.prescriptionList) { presc ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPrescriptionSelected(presc.trackingCode) },
                            shape = RoundedCornerShape(CornerRadius.sm),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(Spacing.md)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "دکتر ${presc.docName}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = presc.prescDate,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                                Spacer(modifier = Modifier.height(Spacing.xs))
                                Text(
                                    text = "تخصص: ${presc.specDesc}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(Spacing.xs))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "نوع: ${presc.prescType}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "کد رهگیری: ${presc.trackingCode}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            SubFlowHeader(title = "جزئیات نسخه", onBack = onClearSelected)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.xs),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                val priceInfo = state.prescriptionPriceList.firstOrNull()
                if (priceInfo != null) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(CornerRadius.sm)
                        ) {
                            Column(modifier = Modifier.padding(Spacing.md)) {
                                Text(
                                    text = "هزینه کل نسخه",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "${priceInfo.requestPrice.toPriceFormat()} ریال",
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
                                            text = "سهم سازمان (تامین اجتماعی)",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = "${priceInfo.headSsoPayment.toPriceFormat()} ریال",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "سهم بیمار (پرداختی شما)",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = "${priceInfo.headInsuPayment.toPriceFormat()} ریال",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFC62828)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Button(
                            onClick = { onDownloadPdf(state.selectedNoteHeadId) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(CornerRadius.xs),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null)
                            Spacer(modifier = Modifier.width(Spacing.xs))
                            Text("دریافت PDF نسخه")
                        }
                        OutlinedButton(
                            onClick = { onDownloadTestResult("0", state.selectedNoteHeadId) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(CornerRadius.xs)
                        ) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null)
                            Spacer(modifier = Modifier.width(Spacing.xs))
                            Text("PDF جواب آزمایش")
                        }
                    }
                }

                item {
                    Text(
                        text = "اقلام تجویز شده",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = Spacing.xs)
                    )
                }

                if (state.isLoading && state.prescriptionDetailList.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }

                items(state.prescriptionDetailList) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(CornerRadius.sm),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(Spacing.md)) {
                            Text(
                                text = item.serviceName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "تعداد: ${item.serviceQuantity}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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
                                    text = "سهم بیمه شده: ${item.insurancePayment.toPriceFormat()} ریال",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFC62828)
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
                        }
                    }
                }
            }
        }
    }

    if (state.showPdfDialog && state.viewerPdf != null) {
        PdfViewerDialog(
            pdfData = state.viewerPdf,
            onDismiss = onDismissPdf
        )
    }
}

@Composable
fun PdfViewerDialog(
    pdfData: com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR,
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
                    // A multiplatform PDF renderer is not yet wired in; the stream is downloaded
                    // (see the shared PdfDownloadDN chain) and held in [pdfData] ready to render.
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
fun PrescriptionsContentListPreview() {
    PreviewRtlThemeContent {
        PrescriptionsContent(
            state = TreatmentMocks.prescriptionsUiState,
            onBack = {},
            onPrescriptionSelected = {},
            onClearSelected = {},
            onDownloadPdf = {},
            onDownloadTestResult = { _, _ -> },
            onDismissPdf = {}
        )
    }
}

@PreviewRtlTheme
@Composable
fun PrescriptionsContentDetailPreview() {
    PreviewRtlThemeContent {
        PrescriptionsContent(
            state = TreatmentMocks.prescriptionsUiState.copy(selectedNoteHeadId = "TRK123456"),
            onBack = {},
            onPrescriptionSelected = {},
            onClearSelected = {},
            onDownloadPdf = {},
            onDownloadTestResult = { _, _ -> },
            onDismissPdf = {}
        )
    }
}
