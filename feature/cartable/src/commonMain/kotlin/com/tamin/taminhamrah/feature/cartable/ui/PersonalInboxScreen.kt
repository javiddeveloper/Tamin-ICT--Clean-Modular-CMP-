package com.tamin.taminhamrah.feature.cartable.ui

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
import com.tamin.taminhamrah.feature.cartable.ui.contract.PersonalInboxIntent
import com.tamin.taminhamrah.feature.cartable.ui.contract.PersonalInboxUiState
import com.tamin.taminhamrah.model.inbox.PersonalInboxItemPR
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PersonalInboxScreen(
    viewModel: PersonalInboxViewModel = koinViewModel(),
    onBackClicked: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(PersonalInboxIntent.LoadInbox)
    }

    PersonalInboxContent(
        state = uiState,
        onBackClicked = onBackClicked,
    )
}

@Composable
fun PersonalInboxContent(
    state: PersonalInboxUiState,
    onBackClicked: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("صندوق شخصی من") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
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
            when {
                state.isLoading && state.items.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.error != null && state.items.isEmpty() -> {
                    Text(
                        text = state.error ?: "خطای ناشناخته",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        state.size?.let { size ->
                            item(key = "inbox-size") {
                                Text(
                                    text = "${size.usageLabel} / ${size.totalLabel}",
                                    style = MaterialTheme.typography.titleSmall,
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }

                        items(state.items, key = { it.id }) { item ->
                            PersonalInboxItemCard(item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PersonalInboxItemCard(item: PersonalInboxItemPR) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "کد پیگیری: ${item.refCode}")
            Text(text = "تاریخ درخواست: ${item.requestDate}")
            Text(text = "سیستم: ${item.system}")
            Text(text = "موضوع: ${item.subject}")
            Text(text = "کد رمز: ${item.passwordCode}")
        }
    }
}
