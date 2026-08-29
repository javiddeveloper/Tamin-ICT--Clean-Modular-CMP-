package com.tamin.taminhamrah.feature.workshops.ui.workshopDebit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.workshop.WorkshopDebitPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import kotlinx.collections.immutable.persistentListOf
import org.koin.compose.koinInject

@Composable
fun WorkshopDebitScreen(
    workshopId: String,
    branchCode: String,
    viewModel: WorkshopDebitViewModel = koinInject()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(workshopId, branchCode) {
        viewModel.sendIntent(
            WorkshopDebitIntent.LoadWorkshopDebit(
                workshopId = workshopId,
                branchCode = branchCode
            )
        )
    }

    WorkshopDebitContent(state = state)
}

@Composable
fun WorkshopDebitContent(state: WorkshopDebitUiState) {
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
            if (state.workshopDebits.isEmpty()) {
                Text(
                    text = "لیست بدهی کارگاه خالی است",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.workshopDebits) { debit ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "شماره بدهی: ${debit.debitNumber ?: "ندارد"}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "شرح دلیل ایجاد: ${debit.debitCreateReasonDesc ?: "ندارد"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "مبلغ بدهی: ${debit.debitAmount ?: 0}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "شرح مرحله: ${debit.debitStepDesc ?: "نامشخص"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "شرح وضعیت: ${debit.debitStatDesc ?: "نامشخص"}",
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
private fun WorkshopDebitContentPreview() {
    PreviewRtlThemeContent {
        WorkshopDebitContent(
            state = WorkshopDebitUiState(
                isLoading = false,
                workshopDebits = persistentListOf(
                    WorkshopDebitPR(
                        debitNumber = "1070040032789",
                        debitCreateReasonCode = "05",
                        debitCreateReasonDesc = "محاسبات رياضي فني",
                        debitStartDate = "14040101",
                        debitEndDate = "14040631",
                        debitAmount = 1348557L,
                        debitRemain = 1348557L,
                        withoutPentaltyAmount = 0L,
                        penaltyList = 0L,
                        penaltyPay = 1284340L,
                        sum = 1284340L,
                        nimOshr = 64217L,
                        debitStepDesc = "اجرائيه",
                        debitStatDesc = "ابلاغ حكم",
                        debitStepCode = "07",
                        debitStatCode = "03",
                        mastCustomerTypeCode = "04",
                        mastCustomerCode = "1071410004",
                        peymanSequence = null,
                        debitCreateDate = "14040805",
                        cludatCode = "1",
                        cludatDesc = "براوردي",
                        nimOshrKol = 0L,
                        docDate = null,
                        stepCat = "3"
                    ),
                    WorkshopDebitPR(
                        debitNumber = "1070880064198",
                        debitCreateReasonCode = "24",
                        debitCreateReasonDesc = "سند 4% بدهي حق بيمه سخت و زيان آور",
                        debitStartDate = "13830201",
                        debitEndDate = "13881002",
                        debitAmount = 18546691L,
                        debitRemain = 18546691L,
                        withoutPentaltyAmount = 15770995L,
                        penaltyList = 0L,
                        penaltyPay = 1892520L,
                        sum = 1892520L,
                        nimOshr = 883176L,
                        debitStepDesc = "اجرائيه",
                        debitStatDesc = "ابلاغ حكم",
                        debitStepCode = "07",
                        debitStatCode = "03",
                        mastCustomerTypeCode = "04",
                        mastCustomerCode = "1071410004",
                        peymanSequence = null,
                        debitCreateDate = "14040904",
                        cludatCode = "2",
                        cludatDesc = "قطعي",
                        nimOshrKol = 0L,
                        docDate = null,
                        stepCat = "3"
                    )
                ),
                error = null
            )
        )
    }
}
