package com.tamin.taminhamrah.feature.workshops.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.model.workshop.EmployerAgreementPR
import org.koin.compose.viewmodel.koinViewModel

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsIntent

@Composable
fun WorkshopsScreen(
    viewModel: WorkshopsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var workshopId by remember { mutableStateOf("") }
    var branchCode by remember { mutableStateOf("") }
    var workshopStatus by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = workshopId,
            onValueChange = { workshopId = it },
            label = { Text("کد کارگاه (workshopId)") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = branchCode,
            onValueChange = { branchCode = it },
            label = { Text("کد شعبه (branchCode)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = workshopStatus,
            onValueChange = { workshopStatus = it },
            label = { Text("وضعیت کارگاه (workshopStatus)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.sendIntent(
                    WorkshopsIntent.LoadWorkshops(
                        workshopId = workshopId.takeIf { it.isNotBlank() },
                        branchCode = branchCode.takeIf { it.isNotBlank() },
                        workshopStatus = workshopStatus.takeIf { it.isNotBlank() }
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ارسال و دریافت اطلاعات")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(
                    text = "خطا: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                if (uiState.agreements.isEmpty()) {
                    Text(
                        text = "هیچ کارگاهی یافت نشد",
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.agreements) { agreement ->
                            WorkshopItem(agreement)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorkshopItem(agreement: EmployerAgreementPR) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "کارگاه: ${agreement.workshop?.workshopName ?: "نامشخص"}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "کد کارگاه: ${agreement.workshop?.workshopId ?: "ندارد"}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "نام کارفرما: ${agreement.workshop?.employerName ?: "ندارد"}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
