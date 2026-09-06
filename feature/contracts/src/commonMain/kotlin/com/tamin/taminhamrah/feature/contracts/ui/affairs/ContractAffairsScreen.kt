package com.tamin.taminhamrah.feature.contracts.ui.affairs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.contracts.ui.affairs.contract.ContractAffairsEvent
import com.tamin.taminhamrah.model.common.FeatureFlag
import org.koin.compose.viewmodel.koinViewModel

/**
 * امور قراردادها و پرداخت.
 *
 * Placeholder shell — the real layout (search sheet, contract cards, امور قرارداد bottom sheet,
 * cancel flow, payment-history list, PDF viewer) is designed later. Everything it needs already
 * lives on [ContractAffairsViewModel] / its contract.
 */
@Composable
fun ContractAffairsScreen(
    onBackClicked: () -> Unit,
    onNavigateToService: (FeatureFlag) -> Unit,
    onOpenUrl: (String) -> Unit,
    viewModel: ContractAffairsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ContractAffairsEvent.NavigateToService -> onNavigateToService(event.flag)
                is ContractAffairsEvent.NavigateToWeb -> onOpenUrl(event.url)
                is ContractAffairsEvent.NavigateToPremiumPayment -> Unit // TODO(ui): SEP payment flow
                is ContractAffairsEvent.NavigateToEditContract -> Unit // TODO(ui): edit-contract flow
                is ContractAffairsEvent.ContractCancelled -> Unit // TODO(ui): success confirmation
                is ContractAffairsEvent.ShowToast -> Unit // TODO(ui): snackbar
                is ContractAffairsEvent.ShowError -> Unit // TODO(ui): error snackbar
            }
        }
    }

    Scaffold { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading && uiState.contracts.isEmpty() ->
                    CircularProgressIndicator(Modifier.align(Alignment.Center))

                uiState.contracts.isEmpty() ->
                    Text("قراردادی یافت نشد", Modifier.align(Alignment.Center))

                else -> LazyColumn(Modifier.fillMaxSize()) {
                    items(uiState.contracts, key = { it.contractNumber }) { contract ->
                        Text(
                            text = "${contract.contractNumber} — ${contract.statusDesc}",
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }
            }
        }
    }
}
