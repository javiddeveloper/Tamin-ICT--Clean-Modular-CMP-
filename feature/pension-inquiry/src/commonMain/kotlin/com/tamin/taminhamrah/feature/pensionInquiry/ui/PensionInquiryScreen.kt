package com.tamin.taminhamrah.feature.pensionInquiry.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.pensionInquiry.ui.contract.PensionInquiryIntent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.contract.PensionInquiryUiState
import com.tamin.taminhamrah.model.pension.PensionIdPR
import com.tamin.taminhamrah.model.pension.PensionInquiryPR
import com.tamin.taminhamrah.model.pension.RecipientPR
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PensionInquiryScreen(
    viewModel: PensionInquiryViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(PensionInquiryIntent.LoadPensionInquiry)
        viewModel.sendIntent(PensionInquiryIntent.LoadPensionerIds)
        viewModel.sendIntent(PensionInquiryIntent.LoadRecipients)
    }

    PensionInquiryContent(state)
}

@Composable
fun PensionInquiryContent(state: PensionInquiryUiState) {
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(title = { Text("استعلام مستمری") })
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading && state.pensionList.isEmpty() && state.pensionerIds.isEmpty() && state.recipients.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null && state.pensionList.isEmpty() && state.pensionerIds.isEmpty() && state.recipients.isEmpty()) {
                Text(
                    text = state.error ?: "خطای ناشناخته",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (state.recipients.isNotEmpty()) {
                        item {
                            Text(
                                text = "لیست دریافت‌کنندگان:",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(state.recipients) { recipient ->
                            RecipientItem(recipient)
                        }
                    }

                    if (state.pensionerIds.isNotEmpty()) {
                        item {
                            Text(
                                text = "شناسه‌های مستمری‌بگیر:",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(state.pensionerIds) { idItem ->
                            PensionIdItem(idItem)
                        }
                    }

                    if (state.pensionList.isNotEmpty()) {
                        item {
                            Text(
                                text = "لیست استعلام مستمری:",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(state.pensionList) { item ->
                            PensionItem(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PensionIdItem(item: PensionIdPR) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "شناسه مستمری‌بگیر: ${item.pensionerId}")
        }
    }
}

@Composable
fun RecipientItem(item: RecipientPR) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "نام دریافت‌کننده: ${item.recipientName}")
            Text(text = "کد دریافت‌کننده: ${item.recipientCode}")
        }
    }
}

@Composable
fun PensionItem(item: PensionInquiryPR) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "نام: ${item.fullName}", style = MaterialTheme.typography.titleMedium)
            Text(text = "کد شعبه: ${item.branchCode}")
            Text(text = "شماره بیمه: ${item.insuranceNumber}")
            Text(text = "مبلغ پرداختی: ${item.paymentAmount}")
            Text(text = "وضعیت: ${item.statusDesc}")
        }
    }
}
