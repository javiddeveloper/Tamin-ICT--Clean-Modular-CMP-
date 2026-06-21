package com.tamin.taminhamrah.feature.workshops.ui.paymentSheets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.workshop.PaymentSheetPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent

@Composable
fun PaymentSheetsScreen(
    workshopId: String,
    branchCode: String,
    viewModel: PaymentSheetsViewModel = koinInject()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(workshopId, branchCode) {
        viewModel.sendIntent(
            PaymentSheetsIntent.LoadPaymentSheets(
                workshopId = workshopId,
                branchCode = branchCode
            )
        )
    }

    PaymentSheetsContent(state = state)
}

@Composable
fun PaymentSheetsContent(state: PaymentSheetsUiState) {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (!state.error.isNullOrEmpty()) {
            Text(
                text = "خطا: ${state.error}",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            if (state.paymentSheets.isEmpty()) {
                Text(
                    text = "لیست پرداخت‌ها خالی است",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.paymentSheets) { sheet ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "شماره پیگیری: ${sheet.orderNo ?: "ندارد"}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "مبلغ: ${sheet.paySeqAmount ?: 0}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "وضعیت: ${sheet.orpStatusDesc ?: "نامشخص"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun PaymentSheetsContentPreview() {
    PreviewRtlThemeContent {
        PaymentSheetsContent(
            state = PaymentSheetsUiState(
                isLoading = false,
                paymentSheets = listOf(
                    PaymentSheetPR(
                        orderNo = "9900020917749",
                        orderRow = "01",
                        payId = "990002091774901",
                        mastCustomerCode = "0968210170",
                        rcntrow = null,
                        mastCustomerName = "اموزشکاه کامپيوتر توکلي",
                        debitCreateReasonCode = "81",
                        debitCreateReasonDesc = "دريافت ليست",
                        debitNo = "9000020392980",
                        docDate = 1702931400000,
                        paySeqAmount = 21924852,
                        orpStatusCode = "2",
                        orpStatusDesc = "وصول",
                        cardDate = 1702931400000,
                        payKindCode = "02",
                        payKindDesc = "نقدي",
                        ouragGno = null,
                        ouragSDate = null
                    ),
                    PaymentSheetPR(
                        orderNo = "0960020791095",
                        orderRow = "01",
                        payId = "096002079109501",
                        mastCustomerCode = "0968210170",
                        rcntrow = null,
                        mastCustomerName = "اموزشکاه کامپيوتر توکلي",
                        debitCreateReasonCode = "81",
                        debitCreateReasonDesc = "دريافت ليست",
                        debitNo = "0960022213825",
                        docDate = 1696969800000,
                        paySeqAmount = 22455680,
                        orpStatusCode = "2",
                        orpStatusDesc = "وصول",
                        cardDate = 1697056200000,
                        payKindCode = "02",
                        payKindDesc = "نقدي",
                        ouragGno = null,
                        ouragSDate = null
                    )
                ),
                error = null
            )
        )
    }
}
