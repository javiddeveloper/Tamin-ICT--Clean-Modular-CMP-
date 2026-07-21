package com.tamin.taminhamrah.feature.workshops.ui.objectionableDebit

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
import androidx.compose.ui.unit.dp

@Composable
fun ObjectionableDebitScreen(
    workshopId: String,
    branchCode: String,
    viewModel: ObjectionableDebitViewModel = koinInject()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(workshopId, branchCode) {
        viewModel.sendIntent(ObjectionableDebitIntent.Load(workshopId, branchCode))
    }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (state.isLoading) CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        else if (!state.error.isNullOrEmpty()) Text("خطا: ${state.error}", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
        else {
            if (state.list.isEmpty()) Text("لیست خالی است", modifier = Modifier.align(Alignment.Center))
            else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.list) { item ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("شماره بدهی: ${item.debitNumber ?: "ندارد"}")
                                Text("مبلغ: ${item.debitAmount ?: 0}")
                                Text("وضعیت: ${item.debitStatDesc ?: "نامشخص"}")
                            }
                        }
                    }
                }
            }
        }
    }
}
