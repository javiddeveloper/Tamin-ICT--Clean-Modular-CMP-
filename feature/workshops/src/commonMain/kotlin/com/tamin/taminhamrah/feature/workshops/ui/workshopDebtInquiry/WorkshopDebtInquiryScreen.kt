package com.tamin.taminhamrah.feature.workshops.ui.workshopDebtInquiry

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.collectAsState
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryPR
import org.koin.compose.koinInject

@Composable
fun WorkshopDebtInquiryScreen(
    workshopId: String,
    branchCode: String,
    viewModel: WorkshopDebtInquiryViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(workshopId, branchCode) {
        viewModel.sendIntent(WorkshopDebtInquiryIntent.LoadDebtInquiry(workshopId, branchCode))
    }

    WorkshopDebtInquiryContent(uiState)
}

@Composable
fun WorkshopDebtInquiryContent(uiState: WorkshopDebtInquiryUiState) {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.error != null) {
            Text(
                text = "خطا: ${uiState.error}",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            uiState.inquiryResult?.let { result ->
                Column(modifier = Modifier.fillMaxSize()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "کارگاه: ${result.workshopName ?: "نامشخص"}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "کد کارگاه: ${result.workshopId ?: "ندارد"}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "کد شعبه: ${result.branchCode ?: "ندارد"}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "نتیجه: ${result.result ?: "ندارد"}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "مبلغ ۱: ${result.amount1 ?: "ندارد"}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "مبلغ ۲: ${result.amount2 ?: "ندارد"}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "مبلغ ۳: ${result.amount3 ?: "ندارد"}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "تاریخ: ${result.sDate ?: "ندارد"}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            } ?: run {
                Text(
                    text = "اطلاعاتی یافت نشد",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun WorkshopDebtInquiryContentPreview() {
    PreviewRtlThemeContent {
        WorkshopDebtInquiryContent(
            uiState = WorkshopDebtInquiryUiState(
                isLoading = false,
                error = null,
                inquiryResult = WorkshopDebtInquiryPR(
                    status = null,
                    workshopId = "1071410004",
                    branchCode = "1070",
                    workshopName = "سنگ بري سعيد",
                    result = "کارگاه دارای بدهی قطعی",
                    amount1 = "19895251",
                    sDate = "1405/04/01",
                    amount2 = "0",
                    amount3 = "19895251"
                )
            )
        )
    }
}
