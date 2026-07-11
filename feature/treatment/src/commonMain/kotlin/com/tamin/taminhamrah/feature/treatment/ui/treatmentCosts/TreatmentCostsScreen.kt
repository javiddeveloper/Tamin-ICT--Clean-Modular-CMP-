package com.tamin.taminhamrah.feature.treatment.ui.treatmentCosts

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tamin.taminhamrah.feature.treatment.ui.components.EmptyState
import com.tamin.taminhamrah.feature.treatment.ui.components.SubFlowHeader
import com.tamin.taminhamrah.feature.treatment.ui.components.SuccessStateCard
import com.tamin.taminhamrah.feature.treatment.ui.contract.CostsUiState
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMocks
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat

@Composable
fun TreatmentCostsContent(
    state: CostsUiState,
    onBack: () -> Unit,
    onSendToInbox: (String) -> Unit,
    onDownloadPdf: (String) -> Unit,
    onDismissPdf: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SubFlowHeader(title = "هزینه‌های درمان و خسارت", onBack = onBack)

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.xs),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                if (state.sendToInboxResult != null) {
                    item {
                        SuccessStateCard(
                            title = "ارسال به صندوق پیام",
                            message = state.sendToInboxResult
                        )
                    }
                }
                if (state.treatmentCostList.isEmpty()) {
                    item { EmptyState("هیچ هزینه‌ای یافت نشد.") }
                } else {
                    items(state.treatmentCostList) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
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
                                        text = item.healthcenterName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${item.payPrice.toPriceFormat()} ریال",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
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
                                        text = "تاریخ خدمت: ${item.serviceDate}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "کد رهگیری: ${item.rahgiriCode}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "بیمار: ${item.nameFamil}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = item.payStatusDesc,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                                Spacer(modifier = Modifier.height(Spacing.sm))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                                ) {
                                    Button(
                                        onClick = { onSendToInbox(item.repId) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(CornerRadius.xs),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                    ) {
                                        Icon(imageVector = Icons.Default.MailOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(Spacing.xs))
                                        Text("ارسال به صندوق", fontSize = 12.sp)
                                    }
                                    OutlinedButton(
                                        onClick = { onDownloadPdf(item.repId) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(CornerRadius.xs)
                                    ) {
                                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(Spacing.xs))
                                        Text("دریافت PDF", fontSize = 12.sp)
                                    }
                                }
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
fun TreatmentCostsContentPreview() {
    PreviewRtlThemeContent {
        TreatmentCostsContent(
            state = TreatmentMocks.costsUiState,
            onBack = {},
            onSendToInbox = {},
            onDownloadPdf = {},
            onDismissPdf = {}
        )
    }
}
