package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui

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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.contract.ConstructionInsuranceEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.contract.ConstructionInsuranceIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.contract.ConstructionInsuranceState
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components.ConstructionFileCard
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components.ConstructionHeader
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components.ConstructionSearchCard
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components.NoticeCard
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.no_construction_files_found
import taminx.core.core_ui.retry

@Composable
fun ConstructionInsuranceRoute(
    viewModel: ConstructionInsuranceViewModel,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is ConstructionInsuranceEvent.ShowToast -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is ConstructionInsuranceEvent.NavigateToDetails -> {
                    snackbarHostState.showSnackbar(event.item.fileNumber?.toString() ?: "")
                }
            }
        }
    }

    ConstructionInsuranceScreen(
        state = uiState,
        onBackClicked = onBackClicked,
        onIntent = viewModel::sendIntent,
        snackbarHostState = snackbarHostState,
        modifier = modifier
    )
}

@Composable
fun ConstructionInsuranceScreen(
    state: ConstructionInsuranceState,
    onBackClicked: () -> Unit,
    onIntent: (ConstructionInsuranceIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF1F5F9),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header Component
            ConstructionHeader(
                userName = state.userName,
                nationalCode = state.nationalCode,
                onBackClicked = onBackClicked
            )

            // Content Area
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Search Component
                item {
                    ConstructionSearchCard(
                        itemCount = state.items.size,
                        isExpanded = state.isSearchExpanded,
                        fileNoQuery = state.fileNoQuery,
                        reqNoQuery = state.reqNoQuery,
                        workshopIdQuery = state.workshopIdQuery,
                        branchCodeQuery = state.branchCodeQuery,
                        onToggleExpanded = { onIntent(ConstructionInsuranceIntent.ToggleSearchExpanded(it)) },
                        onFileNoChanged = { onIntent(ConstructionInsuranceIntent.OnFileNoQueryChanged(it)) },
                        onReqNoChanged = { onIntent(ConstructionInsuranceIntent.OnReqNoQueryChanged(it)) },
                        onWorkshopIdChanged = { onIntent(ConstructionInsuranceIntent.OnWorkshopIdQueryChanged(it)) },
                        onBranchCodeChanged = { onIntent(ConstructionInsuranceIntent.OnBranchCodeQueryChanged(it)) },
                        onExecuteSearch = { onIntent(ConstructionInsuranceIntent.ExecuteSearch) },
                        onResetSearch = { onIntent(ConstructionInsuranceIntent.ResetSearch) }
                    )
                }

                // Important Notice Card
                item {
                    NoticeCard()
                }

                // Loading State
                if (state.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF1D5BBF))
                        }
                    }
                }

                // Error State
                if (state.error != null && !state.isLoading) {
                    item {
                        CardErrorView(
                            error = state.error,
                            onRetry = { onIntent(ConstructionInsuranceIntent.LoadData) }
                        )
                    }
                }

                // Empty State
                if (!state.isLoading && state.error == null && state.items.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(Res.string.no_construction_files_found),
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                // Construction Files Items
                if (!state.isLoading && state.error == null && state.items.isNotEmpty()) {
                    items(
                        items = state.items,
                        key = { it.fileNumber ?: it.hashCode() }
                    ) { file ->
                        ConstructionFileCard(
                            item = file,
                            onDetailClick = { onIntent(ConstructionInsuranceIntent.OnDetailClick(file)) },
                            onActionClick = { onIntent(ConstructionInsuranceIntent.OnActionClick(file)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CardErrorView(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = error, color = Color.Red, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text(stringResource(Res.string.retry))
        }
    }
}
