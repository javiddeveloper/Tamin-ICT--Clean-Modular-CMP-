package com.tamin.taminhamrah.feature.cartable.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.cartable.ui.contract.CartableEvent
import com.tamin.taminhamrah.feature.cartable.ui.contract.CartableIntent
import com.tamin.taminhamrah.feature.cartable.ui.model.CartableMenuItem
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CartableScreen(
    viewModel: CartableViewModel = koinViewModel(),
    onNavigateToMyRequests: () -> Unit = {},
    onNavigateToPersonalInbox: () -> Unit = {},
    onBackClicked: () -> Unit = {},
) {
    HandleCartableEvents(
        events = viewModel.events,
        onNavigateToMyRequests = onNavigateToMyRequests,
        onNavigateToPersonalInbox = onNavigateToPersonalInbox,
        onBackClicked = onBackClicked,
    )

    CartableContent(
        onIntent = viewModel::sendIntent,
    )
}

@Composable
fun HandleCartableEvents(
    events: Flow<CartableEvent>,
    onNavigateToMyRequests: () -> Unit,
    onNavigateToPersonalInbox: () -> Unit,
    onBackClicked: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    events.collectWithLifecycleAware {
        when (it) {
            CartableEvent.NavigateBack -> {
                scope.launch { onBackClicked() }
            }

            CartableEvent.NavigateToMyRequests -> {
                scope.launch { onNavigateToMyRequests() }
            }

            CartableEvent.NavigateToPersonalInbox -> {
                scope.launch { onNavigateToPersonalInbox() }
            }

            is CartableEvent.ShowToast -> Unit
        }
    }
}

@Composable
fun CartableContent(
    modifier: Modifier = Modifier,
    onIntent: (CartableIntent) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(title = { Text("کارتابل") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CartableMenuCard(
                title = "درخواست های من",
                description = "کارتابل پیگیری درخواست ها، اطلاع از نتیجه اقدامات و ...",
                icon = { Icon(Icons.Default.Description, contentDescription = null) },
                onClick = { onIntent(CartableIntent.OnItemClick(CartableMenuItem.MY_REQUESTS)) }
            )
            CartableMenuCard(
                title = "صندوق شخصی من",
                description = "فضایی برای نگهداری و اشتراک گذاری اسناد و مکاتبات",
                icon = { Icon(Icons.Default.Inbox, contentDescription = null) },
                onClick = { onIntent(CartableIntent.OnItemClick(CartableMenuItem.PERSONAL_INBOX)) }
            )
        }
    }
}

@Composable
private fun CartableMenuCard(
    title: String,
    description: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            icon()
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
