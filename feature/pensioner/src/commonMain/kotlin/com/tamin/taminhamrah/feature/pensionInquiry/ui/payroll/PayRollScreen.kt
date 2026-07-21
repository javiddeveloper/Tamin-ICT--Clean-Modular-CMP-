package com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.contract.PayRollIntent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.contract.PayRollUiState
import com.tamin.taminhamrah.model.pension.PayRollPR
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayRollScreen(
    onBack: () -> Unit,
    viewModel: PayRollViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.payroll_title)) },
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
                        PayRollForm(
                            state = state,
                            onIntent = viewModel::sendIntent
                        )
                    }

                    items(state.payRollList) { payRoll ->
                        PayRollDetailCard(payRoll)
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

    if (state.showPdfDialog && state.payRollPDF != null) {
        PayRollPdfDialog(
            pdfData = state.payRollPDF!!,
            onDismiss = { viewModel.sendIntent(PayRollIntent.TogglePdfDialog(false)) }
        )
    }
}

@Composable
fun PayRollForm(
    state: PayRollUiState,
    onIntent: (PayRollIntent) -> Unit
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
                                onIntent(PayRollIntent.ChangeSelectedPensionerId(idItem.pensionerId))
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Start Date Input
            OutlinedTextField(
                value = state.startDate,
                onValueChange = { onIntent(PayRollIntent.ChangeStartDate(it)) },
                label = { Text(stringResource(Res.string.payroll_start_date_label)) },
                placeholder = { Text(stringResource(Res.string.payroll_start_date_placeholder)) },
                modifier = Modifier.fillMaxWidth()
            )

            // Payment Type Input
            OutlinedTextField(
                value = state.paymentType,
                onValueChange = { onIntent(PayRollIntent.ChangePaymentType(it)) },
                label = { Text(stringResource(Res.string.payroll_payment_type_label)) },
                placeholder = { Text(stringResource(Res.string.payroll_payment_type_placeholder)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onIntent(PayRollIntent.LoadPayRoll) },
                    enabled = !state.selectedPensionerId.isNullOrEmpty(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(Res.string.btn_load_payroll))
                }

                Button(
                    onClick = { onIntent(PayRollIntent.LoadPayRollPDF) },
                    enabled = !state.selectedPensionerId.isNullOrEmpty(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(Res.string.btn_show_payroll_pdf))
                }
            }
        }
    }
}

@Composable
fun PayRollDetailCard(item: PayRollPR) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(Res.string.payroll_header),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(text = stringResource(Res.string.payroll_desc, item.tprDesc ?: ""))
            Text(text = stringResource(Res.string.payroll_sum_amount, item.sumAmount ?: ""))
            Text(text = stringResource(Res.string.payroll_sum_pay, item.sumPay ?: ""))
            Text(text = stringResource(Res.string.payroll_year, item.hisYear ?: ""))
            Text(text = stringResource(Res.string.payroll_month, item.hisMon ?: ""))
            Text(text = stringResource(Res.string.payroll_type, item.clpType ?: ""))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayRollPdfDialog(
    pdfData: com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = { Text(stringResource(Res.string.pdf_viewer_title)) },
                    actions = {
                        TextButton(onClick = onDismiss) {
                            Text(stringResource(Res.string.btn_close))
                        }
                    }
                )
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(Res.string.pdf_loaded_successfully),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(Res.string.pdf_ready_rendering),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}
