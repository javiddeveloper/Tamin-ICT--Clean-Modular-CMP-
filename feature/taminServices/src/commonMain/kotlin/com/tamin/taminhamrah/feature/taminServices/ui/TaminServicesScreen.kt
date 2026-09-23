package com.tamin.taminhamrah.feature.taminServices.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.taminServices.model.RolePR
import com.tamin.taminhamrah.feature.taminServices.ui.components.ServiceCard
import com.tamin.taminhamrah.feature.taminServices.ui.components.TabSelector
import com.tamin.taminhamrah.feature.taminServices.ui.components.TaminServicesHeader
import com.tamin.taminhamrah.feature.taminServices.ui.contract.TaminSericesEvent
import com.tamin.taminhamrah.feature.taminServices.ui.contract.TaminServicesIntent
import com.tamin.taminhamrah.feature.taminServices.ui.contract.TaminServicesUiState
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.CustomSearchBar
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.components.rememberStaggeredEntranceState
import com.tamin.taminhamrah.ui.components.staggeredItemEntrance
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import com.tamin.taminhamrah.ui.toparea.TopAreaState
import com.tamin.taminhamrah.ui.toparea.driveTopArea
import com.tamin.taminhamrah.ui.toparea.rememberMeasuredTopAreaState
import com.tamin.taminhamrah.ui.toparea.reportTopAreaHeight
import com.tamin.taminhamrah.ui.toparea.topAreaContentPadding
import com.tamin.taminhamrah.ui.toparea.topAreaHide
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.tamin_services_badge_count
import taminx.core.core_ui.tamin_services_no_results_title
import taminx.core.core_ui.tamin_services_search_placeholder
import taminx.core.core_ui.tamin_services_section_title
import taminx.core.core_ui.tamin_services_title

@Composable
fun TaminServicesRoute(
    viewModel: TamminServicesViewModel,
    onNavigateToService: (FeatureFlag) -> Unit,
    onOpenUrl: (String) -> Unit,
    onBackClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
        onIntent = { intent -> viewModel.sendIntent(intent) },
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
    val taminColors = LocalTaminColors.current
    val staggerState = rememberStaggeredEntranceState(key = state.selectedTab?.roleId to state.searchQuery)
    val chunkedServices = remember(state.filteredServices) {
        state.filteredServices.chunked(2)
    }

    val topArea = rememberMeasuredTopAreaState(key = state.tabs) { topAreaState ->
        TaminServicesTopArea(
            query = state.searchQuery,
            onQueryChange = { onIntent(TaminServicesIntent.OnSearchQueryChanged(it)) },
            tabs = state.tabs,
            selectedTab = state.selectedTab,
            onTabSelected = { onIntent(TaminServicesIntent.OnTabSelected(it)) },
            topAreaState = topAreaState,
        )
    }
    val lazyListState = rememberLazyListState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(taminColors.bgPage),
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .driveTopArea(topArea, lazyListState),
            contentPadding = topAreaContentPadding(
                state = topArea,
                rest = PaddingValues(
                    bottom = 80.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
                ),
            ),
            overscrollEffect = rememberJellyOverscroll(),
        ) {
            item {
                TaminServicesHeader(
                    title = stringResource(Res.string.tamin_services_section_title, state.selectedTab?.title ?: ""),
                    badgeText = stringResource(Res.string.tamin_services_badge_count, state.filteredServices.size.toString()),
                    modifier = Modifier.padding(horizontal = Spacing.xlg)
                )
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
                        title = stringResource(Res.string.tamin_services_no_results_title),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(horizontal = Spacing.xlg)
                    )
                }
            } else {
                itemsIndexed(
                    chunkedServices,
                    key = { _, chunk -> "${state.selectedTab?.roleId}_${chunk.firstOrNull()?.id ?: 0}" }
                ) { rowIndex, rowItems ->
                    val rowKey = "${state.selectedTab?.roleId}_${rowItems.firstOrNull()?.id ?: rowIndex}"

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem(
                                fadeInSpec = null,
                                fadeOutSpec = tween(100),
                                placementSpec = spring(stiffness = Spring.StiffnessLow)
                            )
                            .staggeredItemEntrance(index = rowIndex, key = rowKey, state = staggerState)
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

        TaminServicesTopArea(
            query = state.searchQuery,
            onQueryChange = { onIntent(TaminServicesIntent.OnSearchQueryChanged(it)) },
            tabs = state.tabs,
            selectedTab = state.selectedTab,
            onTabSelected = { onIntent(TaminServicesIntent.OnTabSelected(it)) },
            topAreaState = topArea,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .reportTopAreaHeight(topArea),
        )
    }
}

@Composable
private fun TaminServicesTopArea(
    query: String,
    onQueryChange: (String) -> Unit,
    tabs: List<RolePR>,
    selectedTab: RolePR?,
    onTabSelected: (RolePR) -> Unit,
    topAreaState: TopAreaState,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val gradient = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    Column(
        // Opaque below the gradient bar (which paints its own background) so the list scrolling
        // underneath this floating area never shows through the gaps around TabSelector.
        modifier = modifier
            .fillMaxWidth()
            .background(taminColors.bgPage),
    ) {
        TaminTopAppBar(
            title = stringResource(Res.string.tamin_services_title),
            background = gradient,
            bottomPadding = Spacing.lg,
        ) {
            CustomSearchBar(
                query = query,
                onQueryChange = onQueryChange,
                placeHolder = stringResource(Res.string.tamin_services_search_placeholder),
                containerColor = taminColors.onGradient.copy(alpha = 0.15f),
                textColor = taminColors.onGradient,
                placeholderColor = taminColors.textHeaderSubtitle,
                iconTint = taminColors.onGradient,
                modifier = Modifier
                    .fillMaxWidth()
                    .topAreaHide(topAreaState)
            )
        }
        if (tabs.isNotEmpty()) {
            Spacer(modifier = Modifier.height(Spacing.xlg))
            TabSelector(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = onTabSelected,
                modifier = Modifier.padding(horizontal = Spacing.xlg)
            )
        }
        Spacer(modifier = Modifier.height(Spacing.xlg))
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
