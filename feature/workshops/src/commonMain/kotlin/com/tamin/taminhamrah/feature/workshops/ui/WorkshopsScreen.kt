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

@Composable
fun WorkshopsScreen(
    viewModel: WorkshopsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                    contentPadding = PaddingValues(16.dp),
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
