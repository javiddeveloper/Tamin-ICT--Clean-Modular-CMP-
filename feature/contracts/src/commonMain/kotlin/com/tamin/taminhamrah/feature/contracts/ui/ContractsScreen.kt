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
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.back_content_description
import taminx.core.core_ui.contract_empty_list
import taminx.core.core_ui.contract_field_insurance_type
import taminx.core.core_ui.contract_field_job
import taminx.core.core_ui.contract_field_monthly_income
import taminx.core.core_ui.contract_field_monthly_premium
import taminx.core.core_ui.contract_field_number
import taminx.core.core_ui.contract_field_request_date
import taminx.core.core_ui.contract_field_treatment_support
import taminx.core.core_ui.contract_treatment_support_no
import taminx.core.core_ui.contract_treatment_support_yes
import taminx.core.core_ui.contract_list_title
import taminx.core.core_ui.contract_new_contract
import taminx.core.core_ui.contract_value_dash
import taminx.core.core_ui.error_unknown
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.alpha
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.MenuServiceStatusDN
import com.tamin.taminhamrah.feature.contracts.ui.contract.ContractsEvent

@Composable
fun ContractsScreen(
    onBackClicked: () -> Unit,
    onNavigateToService: (FeatureFlag) -> Unit,
    onOpenUrl: (String) -> Unit,
    viewModel: ContractsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.sendIntent(ContractsIntent.LoadContracts)
        viewModel.events.collect { event ->
            when (event) {
                is ContractsEvent.NavigateToService -> {
                    showBottomSheet = false
                    onNavigateToService(event.flag)
                }
                is ContractsEvent.NavigateToWeb -> {
                    onOpenUrl(event.url)
                }
                is ContractsEvent.ShowToast -> {
                    // In a real app we would show a snackbar here
                }
            }
        }
    }

    ContractsContent(
        uiState = uiState,
        onBackClicked = onBackClicked,
        onNewContractClicked = { showBottomSheet = true }
    )

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.contract_new_contract),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (uiState.newContractOptions.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(uiState.newContractOptions) { service ->
                            val isDisabled = service.status == MenuServiceStatusDN.DISABLED ||
                                             service.status == MenuServiceStatusDN.TEMPORARY_DISABLED ||
                                             service.status == MenuServiceStatusDN.COMPLETELY_DISABLED

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !isDisabled) {
                                        viewModel.sendIntent(ContractsIntent.OnServiceClick(service))
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp)
                                    .alpha(if (isDisabled) 0.5f else 1.0f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = service.name ?: "",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    if (isDisabled && !service.message.isNullOrEmpty()) {
                                        Text(
                                            text = service.message!!,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    } else if (service.status == MenuServiceStatusDN.ENABLED_WITH_ERROR && !service.message.isNullOrEmpty()) {
                                        Text(
                                            text = service.message!!,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ContractsContent(
    uiState: ContractsUiState,
    onBackClicked: () -> Unit,
    onNewContractClicked: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.contract_list_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back_content_description),
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNewContractClicked,
                text = { Text(stringResource(Res.string.contract_new_contract)) },
                icon = { }
            )
        }
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
                        text = uiState.error ?: stringResource(Res.string.error_unknown),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                    )
                }

                uiState.contracts.isEmpty() -> {
                    Text(
                        text = stringResource(Res.string.contract_empty_list),
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

            ContractRow(label = stringResource(Res.string.contract_field_number), value = contract.contractNumber)
            ContractRow(label = stringResource(Res.string.contract_field_request_date), value = contract.requestDate)
            ContractRow(label = stringResource(Res.string.contract_field_insurance_type), value = contract.insuranceType)
            ContractRow(label = stringResource(Res.string.contract_field_monthly_premium), value = contract.monthlyPremiumLabel)
            ContractRow(
                label = stringResource(Res.string.contract_field_monthly_income),
                value = contract.monthlyIncome,
                valueColor = MaterialTheme.colorScheme.primary,
            )
            ContractRow(
                label = stringResource(Res.string.contract_field_treatment_support),
                value = stringResource(
                    if (contract.hasTreatmentSupport) {
                        Res.string.contract_treatment_support_yes
                    } else {
                        Res.string.contract_treatment_support_no
                    },
                ),
                valueColor = if (contract.hasTreatmentSupport) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                },
            )
            ContractRow(
                label = stringResource(Res.string.contract_field_job),
                value = contract.jobTitle.ifBlank { stringResource(Res.string.contract_value_dash) },
            )
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
