package com.tamin.taminhamrah.feature.history.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryIntent
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("سوابق تلفیقی", "سوابق دستمزد")

    LaunchedEffect(selectedTabIndex) {
        if (selectedTabIndex == 0 && uiState.talfighInfos.isEmpty()) {
            viewModel.sendIntent(HistoryIntent.LoadTalfighiData)
        } else if (selectedTabIndex == 1 && uiState.dastmozdInfos.isEmpty()) {
            viewModel.sendIntent(HistoryIntent.LoadDastmozdData)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("سوابق بیمه") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (!uiState.error.isNullOrEmpty()) {
                    Text(
                        text = "خطا: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    if (selectedTabIndex == 0) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.talfighInfos) { info ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("سال: ${info.hisYear}", style = MaterialTheme.typography.titleMedium)
                                        Text("مجموع روزهای سابقه: ${info.historyDays}", style = MaterialTheme.typography.bodyMedium)
                                        Text("مجموع سال‌های سابقه: ${info.sumHistoryYears}", style = MaterialTheme.typography.bodyMedium)
                                        Text("کد ملی مرتبط (risuid): ${info.risuid}", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.dastmozdInfos) { info ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("سال: ${info.hisyear}", style = MaterialTheme.typography.titleMedium)
                                        Text("شعبه: ${info.brhname}", style = MaterialTheme.typography.bodyMedium)
                                        Text("توضیحات: ${info.historytypedesc}", style = MaterialTheme.typography.bodyMedium)
                                        Text("مجموع دستمزد ماه اول: ${info.hiswage1}", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
