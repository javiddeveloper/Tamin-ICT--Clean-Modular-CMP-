package com.tamin.taminhamrah.feature.contracts.ui

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.feature.contracts.ui.contract.ContractsEvent
import com.tamin.taminhamrah.feature.contracts.ui.contract.ContractsIntent
import com.tamin.taminhamrah.feature.contracts.ui.contract.ContractsUiState
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.MenuServiceStatusDN
import com.tamin.taminhamrah.model.contracts.ContractPR
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.coroutines.flow.Flow
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
import taminx.core.core_ui.contract_list_title
import taminx.core.core_ui.contract_new_contract
import taminx.core.core_ui.contract_treatment_support_no
import taminx.core.core_ui.contract_treatment_support_yes
import taminx.core.core_ui.contract_value_dash
import taminx.core.core_ui.error_unknown

@Composable
fun ContractsScreen(
    onBackClicked: () -> Unit,
    onNavigateToService: (FeatureFlag) -> Unit,
    onOpenUrl: (String) -> Unit,
    viewModel: ContractsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val taminColors = LocalTaminColors.current
    val toaster = LocalToaster.current

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        viewModel.sendIntent(ContractsIntent.LoadContracts)
    }

    HandleContractsEvents(
        events = viewModel.events,
        onShowToast = { toaster.error(it) },
        onNavigateToService = { flag ->
            showBottomSheet = false
            onNavigateToService(flag)
        },
        onNavigateToWeb = onOpenUrl,
    )

    ContractsContent(
        uiState = uiState,
        onBackClicked = onBackClicked,
        onNewContractClicked = { showBottomSheet = true },
    )

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = taminColors.bgSurface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.contract_new_contract),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = Spacing.lg),
                )

                if (uiState.newContractOptions.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.padding(Spacing.lg))
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
                                    .padding(vertical = Spacing.md, horizontal = Spacing.sm)
                                    .alpha(if (isDisabled) taminColors.disabledAlpha else 1f),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = service.name.orEmpty(),
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                    val message = service.message
                                    if (!message.isNullOrEmpty() &&
                                        (isDisabled || service.status == MenuServiceStatusDN.ENABLED_WITH_ERROR)
                                    ) {
                                        Text(
                                            text = message,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = taminColors.dangerText,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(Spacing.xxl))
            }
        }
    }
}

@Composable
private fun HandleContractsEvents(
    events: Flow<ContractsEvent>,
    onShowToast: (String) -> Unit,
    onNavigateToService: (FeatureFlag) -> Unit,
    onNavigateToWeb: (String) -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is ContractsEvent.ShowToast -> onShowToast(event.message)
            is ContractsEvent.NavigateToService -> onNavigateToService(event.flag)
            is ContractsEvent.NavigateToWeb -> onNavigateToWeb(event.url)
        }
    }
}

@Composable
fun ContractsContent(
    uiState: ContractsUiState,
    onBackClicked: () -> Unit,
    onNewContractClicked: () -> Unit,
) {
    val taminColors = LocalTaminColors.current

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
                icon = { },
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
                        text = uiState.error ?: stringResource(Res.string.error_unknown),
                        color = taminColors.dangerText,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(Spacing.lg),
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
                        contentPadding = PaddingValues(Spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md),
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
    val taminColors = LocalTaminColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.xs),
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Surface(
                    color = if (contract.isActive) taminColors.greenBg else taminColors.dangerBg,
                    shape = RoundedCornerShape(CornerRadius.chip),
                ) {
                    Text(
                        text = contract.statusDesc,
                        modifier = Modifier.padding(
                            horizontal = Spacing.md,
                            vertical = Spacing.tabSelector,
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (contract.isActive) taminColors.greenText else taminColors.dangerText,
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            if (contract.contractNumber.isNotBlank()) {
                DetailRow(
                    label = stringResource(Res.string.contract_field_number),
                    value = contract.contractNumber,
                )
            }
            if (contract.requestDate.isNotBlank()) {
                DetailRow(
                    label = stringResource(Res.string.contract_field_request_date),
                    value = contract.requestDate,
                    numeric = false,
                )
            }
            if (contract.insuranceType.isNotBlank()) {
                DetailRow(
                    label = stringResource(Res.string.contract_field_insurance_type),
                    value = contract.insuranceType,
                    numeric = false,
                )
            }
            if (contract.monthlyPremiumLabel.isNotBlank()) {
                DetailRow(
                    label = stringResource(Res.string.contract_field_monthly_premium),
                    value = contract.monthlyPremiumLabel,
                    numeric = false,
                )
            }
            if (contract.monthlyIncome.isNotBlank()) {
                DetailRow(
                    label = stringResource(Res.string.contract_field_monthly_income),
                    value = contract.monthlyIncome,
                    valueColor = taminColors.teal,
                )
            }
            DetailRow(
                label = stringResource(Res.string.contract_field_treatment_support),
                value = stringResource(
                    if (contract.hasTreatmentSupport) {
                        Res.string.contract_treatment_support_yes
                    } else {
                        Res.string.contract_treatment_support_no
                    },
                ),
                valueColor = if (contract.hasTreatmentSupport) taminColors.greenText else taminColors.dangerText,
                numeric = false,
            )
            DetailRow(
                label = stringResource(Res.string.contract_field_job),
                value = contract.jobTitle.ifBlank { stringResource(Res.string.contract_value_dash) },
                numeric = false,
            )
        }
    }
}
