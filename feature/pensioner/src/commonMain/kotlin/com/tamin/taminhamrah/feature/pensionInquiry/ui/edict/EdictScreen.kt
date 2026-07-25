package com.tamin.taminhamrah.feature.pensionInquiry.ui.edict

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.contract.EdictIntent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.contract.EdictUiState
import com.tamin.taminhamrah.model.pension.EdictPensionerPR
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.*

@Composable
fun EdictScreen(
    onBack: () -> Unit,
    viewModel: EdictViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.edict_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.btn_close)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading && state.pensionerIds.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        EdictForm(
                            state = state,
                            onIntent = viewModel::sendIntent
                        )
                    }

                    if (state.edictPensioner != null) {
                        item {
                            EdictDetailCard(state.edictPensioner!!)
                        }

                        if (state.edictPensioner!!.detail.isNotEmpty()) {
                            items(state.edictPensioner!!.detail) { detailItem ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = detailItem.fieldDesc)
                                        Text(text = detailItem.fieldValue, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (state.error != null) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(text = stringResource(Res.string.error_generic, state.error ?: ""))
                }
            }
        }
    }
}

@Composable
fun EdictForm(
    state: EdictUiState,
    onIntent: (EdictIntent) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Pensioner ID Selector
            Text(
                text = stringResource(Res.string.payroll_pensioner_id_label),
                style = MaterialTheme.typography.labelMedium
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = state.selectedPensionerId ?: stringResource(Res.string.error_empty_pensioner_id),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    state.pensionerIds.forEach { idItem ->
                        DropdownMenuItem(
                            text = { Text(idItem.pensionerId) },
                            onClick = {
                                onIntent(EdictIntent.ChangeSelectedPensionerId(idItem.pensionerId))
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Start Date Input
            OutlinedTextField(
                value = state.startDate,
                onValueChange = { onIntent(EdictIntent.ChangeStartDate(it)) },
                label = { Text(stringResource(Res.string.payroll_start_date_label)) },
                placeholder = { Text(stringResource(Res.string.payroll_start_date_placeholder)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action Button
            Button(
                onClick = { onIntent(EdictIntent.LoadEdict) },
                enabled = !state.selectedPensionerId.isNullOrEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.btn_load_edict))
            }
        }
    }
}

@Composable
fun EdictDetailCard(item: EdictPensionerPR) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(Res.string.edict_header),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            item.edictInfo?.let { info ->
                Text(text = stringResource(Res.string.edict_first_name, info.firstName))
                Text(text = stringResource(Res.string.edict_last_name, info.lastName))
                Text(text = stringResource(Res.string.edict_national_code, info.nationalCode))
                Text(text = stringResource(Res.string.edict_father_name, info.fatherName))
                Text(text = stringResource(Res.string.edict_birth_date, info.birthDate))
                Text(text = stringResource(Res.string.edict_pension_start_date, info.pensionStartDate))
                Text(text = stringResource(Res.string.edict_payable_monthly, info.payableMonthly))
            }

            Text(text = stringResource(Res.string.edict_title_label, item.title))
            Text(text = stringResource(Res.string.edict_branch, item.branchName))
            Text(text = stringResource(Res.string.edict_insurance_id, item.insuranceId))
            Text(text = stringResource(Res.string.edict_year_month, item.edictYear ?: "", item.edictMonth ?: ""))
        }
    }
}
