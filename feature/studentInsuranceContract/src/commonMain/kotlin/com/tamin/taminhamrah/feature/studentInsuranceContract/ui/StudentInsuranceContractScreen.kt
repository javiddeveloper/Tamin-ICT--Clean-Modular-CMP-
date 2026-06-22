package com.tamin.taminhamrah.feature.studentInsuranceContract.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.contract.StudentInsuranceContractIntent
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.contract.StudentInsuranceContractUiState
import com.tamin.taminhamrah.model.contracts.ContractPR
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun StudentInsuranceContractScreen(
    onBack: () -> Unit,
    viewModel: StudentInsuranceContractViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(StudentInsuranceContractIntent.LoadInitialData)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("انعقاد قرارداد بیمه دانشجویی") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                },
            )
        },
    ) { padding ->
        StudentInsuranceContractContent(
            state = state,
            modifier = Modifier.padding(padding),
        )
    }
}

@Composable
private fun StudentInsuranceContractContent(
    state: StudentInsuranceContractUiState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when {
            state.isLoading && state.registrationInfo == null -> {
                CircularProgressIndicator()
            }

            state.error != null && state.registrationInfo == null -> {
                Text(
                    text = state.error ?: "خطای ناشناخته",
                    color = MaterialTheme.colorScheme.error,
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    state.registrationInfo?.let { info ->
                        item {
                            RegistrationHeaderCard(info)
                        }
                        item {
                            RegistrationStepCard(info)
                        }
                    }

                    if (state.existingContracts.isNotEmpty()) {
                        item {
                            Text(
                                text = "قراردادهای موجود",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                        items(state.existingContracts) { contract ->
                            ExistingContractCard(contract)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RegistrationHeaderCard(info: RegistrationInfoPR) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = info.fullName, style = MaterialTheme.typography.titleLarge)
            Text(text = "کد ملی: ${info.nationalId}")
            Text(text = "تاریخ تولد: ${info.birthDateFormatted}")
        }
    }
}

@Composable
private fun RegistrationStepCard(info: RegistrationInfoPR) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "۱. نام‌نویسی", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "متقاضی محترم، نام‌نویسی شما با شماره بیمه تأمین اجتماعی ${info.insuranceId} انجام شده است.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Button(
                onClick = { /* step 2 — next iteration */ },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("مرحله بعد")
            }
        }
    }
}

@Composable
private fun ExistingContractCard(contract: ContractPR) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "شماره قرارداد: ${contract.contractNumber}")
            Text(text = "نوع بیمه: ${contract.insuranceType}")
            Text(text = "وضعیت: ${contract.statusDesc}")
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
