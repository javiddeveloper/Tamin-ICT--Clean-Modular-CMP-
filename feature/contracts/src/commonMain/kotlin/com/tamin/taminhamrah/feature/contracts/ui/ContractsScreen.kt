package com.tamin.taminhamrah.feature.contracts.ui

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.contracts.ui.contract.ContractsIntent
import com.tamin.taminhamrah.feature.contracts.ui.contract.ContractsUiState
import com.tamin.taminhamrah.model.contracts.ContractPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ContractsScreen(
    onBackClicked: () -> Unit,
    viewModel: ContractsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(ContractsIntent.LoadContracts)
    }

    ContractsContent(
        uiState = uiState,
        onBackClicked = onBackClicked,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractsContent(
    uiState: ContractsUiState,
    onBackClicked: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("لیست قراردادها") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when {
                uiState.isLoading && uiState.contracts.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.error != null && uiState.contracts.isEmpty() -> {
                    Text(
                        text = uiState.error ?: "خطای ناشناخته",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                    )
                }

                uiState.contracts.isEmpty() -> {
                    Text(
                        text = "قراردادی یافت نشد",
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(uiState.contracts, key = { it.contractNumber }) { contract ->
                            ContractItem(contract)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContractItem(contract: ContractPR) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Surface(
                    color = if (contract.isActive) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.errorContainer
                    },
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text(
                        text = contract.statusDesc,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (contract.isActive) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onErrorContainer
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ContractRow(label = "شماره قرارداد", value = contract.contractNumber)
            ContractRow(label = "تاریخ درخواست", value = contract.requestDate)
            ContractRow(label = "نوع حق بیمه", value = contract.insuranceType)
            ContractRow(label = "مبلغ حق بیمه ماهانه", value = contract.monthlyPremiumLabel)
            ContractRow(
                label = "درآمد ماهانه",
                value = contract.monthlyIncome,
                valueColor = MaterialTheme.colorScheme.primary,
            )
            ContractRow(
                label = "حمایت درمان",
                value = contract.treatmentSupportText,
                valueColor = if (contract.hasTreatmentSupport) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                },
            )
            ContractRow(label = "شغل", value = contract.jobTitle.ifBlank { "—" })
        }
    }
}

@Composable
private fun ContractRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
) {
    if (value.isBlank()) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f).padding(start = 8.dp),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            textAlign = TextAlign.Start,
        )
    }
}
