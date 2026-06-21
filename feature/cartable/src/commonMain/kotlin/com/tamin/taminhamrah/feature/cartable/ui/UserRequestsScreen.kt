package com.tamin.taminhamrah.feature.cartable.ui

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.cartable.ui.contract.MyRequestsIntent
import com.tamin.taminhamrah.feature.cartable.ui.contract.MyRequestsUiState
import com.tamin.taminhamrah.model.userRequest.UserRequestPR
import com.tamin.taminhamrah.model.userRequest.UserRequestTypePR
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UserRequestsScreen(
    viewModel: UserRequestsViewModel = koinViewModel(),
    onBackClicked: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(MyRequestsIntent.LoadRequestTypes)
        viewModel.sendIntent(MyRequestsIntent.LoadRequests)
    }

    MyRequestsContent(
        state = uiState,
        onBackClicked = onBackClicked,
        onRefCodeChange = { viewModel.sendIntent(MyRequestsIntent.UpdateRefCode(it)) },
        onRequestTypeChange = { viewModel.sendIntent(MyRequestsIntent.UpdateRequestType(it)) },
        onSearchClick = { viewModel.sendIntent(MyRequestsIntent.SearchRequests) },
    )
}

@Composable
fun MyRequestsContent(
    state: MyRequestsUiState,
    onBackClicked: () -> Unit,
    onRefCodeChange: (String) -> Unit,
    onRequestTypeChange: (String?) -> Unit,
    onSearchClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("درخواست های من") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("جستجوی درخواست های من", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = state.refCode,
                onValueChange = onRefCodeChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("کد پیگیری") },
                singleLine = true,
            )

            RequestTypeDropdown(
                requestTypes = state.requestTypes,
                selectedRequestTypeId = state.selectedRequestTypeId,
                isLoading = state.isLoadingTypes,
                onRequestTypeChange = onRequestTypeChange,
            )

            Button(
                onClick = onSearchClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
            ) {
                Text("جستجوی درخواست های من")
            }

            when {
                state.isLoading && state.requests.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.error != null && state.requests.isEmpty() -> {
                    Text(
                        text = state.error ?: "خطای ناشناخته",
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                state.requests.isEmpty() -> {
                    Text(
                        text = "نتیجه‌ای یافت نشد",
                        modifier = Modifier.padding(top = 16.dp),
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.requests, key = { it.id }) { request ->
                            RequestItem(request)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RequestTypeDropdown(
    requestTypes: List<UserRequestTypePR>,
    selectedRequestTypeId: String?,
    isLoading: Boolean,
    onRequestTypeChange: (String?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedType = requestTypes.firstOrNull { it.id.toString() == selectedRequestTypeId }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedType?.displayLabel.orEmpty(),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("نوع درخواست") },
            placeholder = {
                Text(if (isLoading) "در حال بارگذاری..." else "انتخاب کنید")
            },
            enabled = false,
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(enabled = !isLoading) { expanded = true },
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f),
        ) {
            DropdownMenuItem(
                text = { Text("همه") },
                onClick = {
                    onRequestTypeChange(null)
                    expanded = false
                },
            )
            requestTypes.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.displayLabel) },
                    onClick = {
                        onRequestTypeChange(type.id.toString())
                        expanded = false
                    },
                )
            }
        }
    }
}
@Composable
private fun RequestItem(request: UserRequestPR) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = request.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "کد پیگیری: ${request.refCode}")
            Text(text = "عنوان درخواست: ${request.requestTypeTitle}")
            Text(text = "وضعیت درخواست: ${request.statusDesc}")
        }
    }
}
