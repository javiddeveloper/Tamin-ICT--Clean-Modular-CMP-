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
import com.tamin.taminhamrah.model.pension.PensionInquiryPR
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PensionInquiryScreen(
    viewModel: PensionInquiryViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(PensionInquiryIntent.LoadPensionInquiry)
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
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
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
                    items(state.pensionList) { item ->
                        PensionItem(item)
                    }
                }
            }
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
