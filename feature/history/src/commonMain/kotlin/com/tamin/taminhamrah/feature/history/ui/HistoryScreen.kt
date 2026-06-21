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
import com.tamin.taminhamrah.feature.history.ui.contract.HistoryUiState
import com.tamin.taminhamrah.model.history.DastmozdInfoItemPR
import com.tamin.taminhamrah.model.history.TalfighInfoItemPR
import com.tamin.taminhamrah.model.history.WageDetailPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }

    LaunchedEffect(selectedTabIndex) {
        if (selectedTabIndex == 0 && uiState.talfighInfos.isEmpty()) {
            viewModel.sendIntent(HistoryIntent.LoadTalfighiData)
        } else if (selectedTabIndex == 1 && uiState.dastmozdInfos.isEmpty()) {
            viewModel.sendIntent(HistoryIntent.LoadDastmozdData)
        }
    }

    HistoryContent(
        uiState = uiState,
        selectedTabIndex = selectedTabIndex,
        onTabSelected = { selectedTabIndex = it }
    )
}

@Composable
fun HistoryContent(
    uiState: com.tamin.taminhamrah.feature.history.ui.contract.HistoryUiState,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("سوابق تلفیقی", "سوابق دستمزد")

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
                        onClick = { onTabSelected(index) },
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
                                        Text("مجموع دستمزد ماه اول: ${info.wageDetails.firstOrNull()?.wage ?: ""}", style = MaterialTheme.typography.bodySmall)
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

@PreviewRtlTheme
@Composable
private fun HistoryScreenPreview() {
    PreviewRtlThemeContent {
        HistoryContent(
            uiState = HistoryUiState(
                talfighInfos = listOf(
                    TalfighInfoItemPR(
                        months = listOf("30", "31", "30"),
                        risuid = "1234567890",
                        historyYears = 1,
                        historyMonths = 0,
                        sumYear = 1400,
                        historyDays = 365,
                        sumHistoryYears = 10,
                        id = 1,
                        hisYear = "1402"
                    )
                ),
                dastmozdInfos = listOf(
                    DastmozdInfoItemPR(
                        wageDetails = listOf(
                            WageDetailPR(month = "01", wage = "12000000"),
                            WageDetailPR(month = "02", wage = "12000000")
                        ),
                        hisyear = "1402",
                        id = 1,
                        risufname = "علی",
                        risubirthdate = "1360/01/01",
                        risuidserial2 = "12",
                        risuidserial1 = "34",
                        rwshname = "شعبه یک",
                        expcitycode = "021",
                        brhcode = "123",
                        risuidno = "123456",
                        risudname = "محمد",
                        risuid = "1234567890",
                        risulname = "رضایی",
                        risunatcode = "0012345678",
                        brhname = "شعبه مرکزی",
                        historytypedesc = "عادی",
                        rwshid = "1"
                    )
                )
            ),
            selectedTabIndex = 0,
            onTabSelected = {}
        )
    }
}




