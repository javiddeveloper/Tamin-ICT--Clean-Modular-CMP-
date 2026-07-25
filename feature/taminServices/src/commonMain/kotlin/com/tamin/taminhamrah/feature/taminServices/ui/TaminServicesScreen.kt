package com.tamin.taminhamrah.feature.taminServices.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.taminServices.ui.contract.*
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.CustomSearchBar
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.feature.taminServices.ui.components.ServiceCard
import com.tamin.taminhamrah.feature.taminServices.ui.components.TabSelector
import com.tamin.taminhamrah.feature.taminServices.ui.components.TaminServicesHeader
import com.tamin.taminhamrah.ui.theme.*
import kotlinx.coroutines.flow.Flow

@Composable
fun TaminServicesRoute(
    viewModel: TamminServicesViewModel,
    onNavigateToService: (FeatureFlag) -> Unit,
    onOpenUrl: (String) -> Unit,
    onBackClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(TaminServicesIntent.OnSearchQueryChanged(""))
    }

    HandleTaminServicesEvents(
        events = viewModel.events,
        onNavigateToService = onNavigateToService,
        onOpenUrl = onOpenUrl,
        onBackClicked = onBackClicked
    )

    TaminServicesScreen(
        state = uiState,
        onIntent = viewModel::sendIntent,
    )
}

@Composable
fun HandleTaminServicesEvents(
    events: Flow<TaminSericesEvent>,
    onNavigateToService: (FeatureFlag) -> Unit,
    onOpenUrl: (String) -> Unit,
    onBackClicked: () -> Unit
) {
    LaunchedEffect(events) {
        events.collect { event ->
            when (event) {
                is TaminSericesEvent.NavigateToService -> {
                    onNavigateToService(event.flag)
                }

                is TaminSericesEvent.NavigateToWeb -> {
                    onOpenUrl(event.url)
                }

                is TaminSericesEvent.NavigateBack -> {
                    onBackClicked()
                }

                is TaminSericesEvent.ShowMessage -> {
                    //TODO display toast
                }
            }
        }
    }
}

@Composable
fun TaminServicesScreen(
    modifier: Modifier = Modifier,
    state: TaminServicesUiState,
    onIntent: (TaminServicesIntent) -> Unit,
) {

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            bottom = 80.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        )
    ) {

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = Elevation.lg,
                        shape = RoundedCornerShape(
                            bottomStart = CornerRadius.x2l,
                            bottomEnd = CornerRadius.x2l
                        ),
                        clip = false
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(
                            bottomStart = CornerRadius.x2l,
                            bottomEnd = CornerRadius.x2l
                        )
                    )
                    .padding(
                        top = Spacing.xl,
                        start = Spacing.xlg,
                        end = Spacing.xlg,
                        bottom = Spacing.xxl
                    )
            ) {
                Text(
                    text = "خدمات",
                    style = MaterialTheme.typography.headlineSmall.copy(color = MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.md)
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                CustomSearchBar(
                    query = state.searchQuery,
                    onQueryChange = { onIntent(TaminServicesIntent.OnSearchQueryChanged(it)) },
                    placeHolder = "جست‌وجو در میان خدمات ..."
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(Spacing.xlg))
        }

        item {
            if (state.tabs.isNotEmpty()) {
                TabSelector(
                    tabs = state.tabs,
                    selectedTab = state.selectedTab,
                    onTabSelected = { onIntent(TaminServicesIntent.OnTabSelected(it)) },
                    modifier = Modifier.padding(horizontal = Spacing.xlg)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(Spacing.xl))
        }

        item {
            Spacer(modifier = Modifier.height(Spacing.xl))
        }

        item {
            TaminServicesHeader(
                title = "خدمات ${state.selectedTab?.title ?: ""}",
                badgeText = "${state.filteredServices.size} خدمت",
                modifier = Modifier.padding(horizontal = Spacing.xlg)
            )
        }

        item {
            Spacer(modifier = Modifier.height(Spacing.lg))
        }

        if (state.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        } else if (state.showNoResultsError) {
            item {
                EmptyStateMessage(
                    icon = Icons.Outlined.SearchOff,
                    title = "نتیجه‌ای یافت نشد",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = Spacing.xlg)
                )
            }
        } else {
            val chunkedServices = state.filteredServices.chunked(2)
            items(
                chunkedServices,
                key = { chunk -> chunk.firstOrNull()?.id ?: 0 }
            ) { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.xlg, vertical = Spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    rowItems.forEach { service ->
                        ServiceCard(
                            service = service,
                            onClick = { onIntent(TaminServicesIntent.OnServiceClick(service)) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    val emptySlots = 2 - rowItems.size
                    repeat(emptySlots) {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(Spacing.xl))
        }
    }
}

@PreviewRtlTheme
@Composable
fun PreviewTaminServicesScreen() {
    PreviewRtlThemeContent {
        TaminServicesScreen(
            onIntent = {},
            state = TaminServicesUiState(
                isLoading = false,
                tabs = emptyList(),
                selectedTab = null,
                filteredServices = emptyList()
            )
        )
    }
}

@PreviewRtlTheme
@Composable
fun PreviewTaminServicesScreenDarkMode() {
    TaminHamrahTheme(darkTheme = true) {
        TaminServicesScreen(
            onIntent = {},
            state = TaminServicesUiState(
                isLoading = false,
                tabs = emptyList(),
                selectedTab = null,
                filteredServices = emptyList()
            )
        )
    }
}
