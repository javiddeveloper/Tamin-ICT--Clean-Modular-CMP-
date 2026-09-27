package com.tamin.taminhamrah.feature.taminServices.ui

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.snapFlingBehavior
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.tamin.taminhamrah.ui.components.rememberStaggeredEntranceState
import com.tamin.taminhamrah.ui.components.staggeredItemEntrance
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.warning
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import kotlinx.coroutines.flow.Flow
import kotlin.math.abs

private val HeaderSnapAnimationSpec: AnimationSpec<Float> = spring(stiffness = Spring.StiffnessLow)

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

    val toaster = LocalToaster.current
    HandleTaminServicesEvents(
        events = viewModel.events,
        onNavigateToService = onNavigateToService,
        onOpenUrl = onOpenUrl,
        onShowMessage = { toaster.warning(it) },
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
    onShowMessage: (String) -> Unit,
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

                // The server's reason a service is off; without it the tap did nothing.
                is TaminSericesEvent.ShowMessage -> {
                    onShowMessage(event.message)
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
    val scrollState = rememberTaminServicesScrollState()
    val motionState = rememberTaminServicesMotionState(scrollState)
    // Remembers completed entrance animation keys across list scrolls and back-navigation to eliminate glitches.
    val staggerState = rememberStaggeredEntranceState(key = state.selectedTab?.roleId to state.searchQuery)
    val headerShape = remember {
        RoundedCornerShape(bottomStart = CornerRadius.x2l, bottomEnd = CornerRadius.x2l)
    }

    val decaySpec = rememberSplineBasedDecay<Float>()
    val snapLayoutInfoProvider = remember(scrollState) {
        object : SnapLayoutInfoProvider {
            override fun calculateSnapOffset(velocity: Float): Float {
                val lazyListState = scrollState.lazyListState
                if (lazyListState.firstVisibleItemIndex == 0) {
                    val currentOffset = lazyListState.firstVisibleItemScrollOffset.toFloat()
                    val maxOffset = scrollState.collapseDistancePx
                    if (currentOffset > 0 && currentOffset < maxOffset) {
                        val targetOffset = if (abs(velocity) > 500f) {
                            if (velocity > 0) maxOffset else 0f
                        } else {
                            if (currentOffset < maxOffset / 2f) 0f else maxOffset
                        }
                        return targetOffset - currentOffset
                    }
                }
                return 0f
            }
        }
    }

    val snapFlingBehavior = snapFlingBehavior(
        snapLayoutInfoProvider = snapLayoutInfoProvider,
        decayAnimationSpec = decaySpec,
        snapAnimationSpec = HeaderSnapAnimationSpec
    )
    val chunkedServices = remember(state.filteredServices) {
        state.filteredServices.chunked(2)
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = Elevation.lg,
                    shape = headerShape,
                    clip = false
                )
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = headerShape
                )
                .collapsibleHeaderPadding(
                    top = { motionState.headerTopPadding },
                    horizontal = Spacing.xlg,
                    bottom = Spacing.xlg
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = 1f - motionState.headerProgress
                    }
                    .layout { measurable, constraints ->
                        val progress = motionState.headerProgress
                        val placeable = measurable.measure(constraints)
                        val height = (placeable.height * (1f - progress)).toInt().coerceAtLeast(0)
                        layout(placeable.width, height) {
                            placeable.placeRelative(0, height - placeable.height)
                        }
                    }
            ) {
                Column {
                    Text(
                        text = "خدمات",
                        style = MaterialTheme.typography.headlineSmall.copy(color = MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Spacing.md)
                    )

                    Spacer(modifier = Modifier.height(Spacing.sm))
                }
            }

            CustomSearchBar(
                query = state.searchQuery,
                onQueryChange = { onIntent(TaminServicesIntent.OnSearchQueryChanged(it)) },
                placeHolder = "جست‌وجو در میان خدمات ..."
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xlg))

        if (state.tabs.isNotEmpty()) {
            TabSelector(
                tabs = state.tabs,
                selectedTab = state.selectedTab,
                onTabSelected = { onIntent(TaminServicesIntent.OnTabSelected(it)) },
                modifier = Modifier.padding(horizontal = Spacing.xlg)
            )
        }

        LazyColumn(
            state = scrollState.lazyListState,
            flingBehavior = snapFlingBehavior,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(
                top = Spacing.xlg,
                bottom = 80.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
        ) {
            item {
                TaminServicesHeader(
                    title = "خدمات ${state.selectedTab?.title ?: ""}",
                    badgeText = "${state.filteredServices.size} خدمت",
                    modifier = Modifier
                        .padding(horizontal = Spacing.xlg)
                        .onSizeChanged { size ->
                            scrollState.collapseDistancePx = size.height.toFloat()
                        }
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
                        title = "نتیجه‌ای یافت نشد",
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
    }
}

private fun Modifier.collapsibleHeaderPadding(
    top: () -> Dp,
    horizontal: Dp,
    bottom: Dp,
): Modifier = this.layout { measurable, constraints ->
    val topPx = top().roundToPx()
    val horizontalPx = horizontal.roundToPx()
    val bottomPx = bottom.roundToPx()

    val horizontalTotal = horizontalPx * 2
    val verticalTotal = topPx + bottomPx

    val loosenedConstraints = Constraints(
        minWidth = (constraints.minWidth - horizontalTotal).coerceAtLeast(0),
        maxWidth = if (constraints.hasBoundedWidth) {
            (constraints.maxWidth - horizontalTotal).coerceAtLeast(0)
        } else constraints.maxWidth,
        minHeight = (constraints.minHeight - verticalTotal).coerceAtLeast(0),
        maxHeight = if (constraints.hasBoundedHeight) {
            (constraints.maxHeight - verticalTotal).coerceAtLeast(0)
        } else constraints.maxHeight
    )

    val placeable = measurable.measure(loosenedConstraints)

    val width = (placeable.width + horizontalTotal).coerceIn(constraints.minWidth, constraints.maxWidth)
    val height = (placeable.height + verticalTotal).coerceIn(constraints.minHeight, constraints.maxHeight)

    layout(width, height) {
        placeable.placeRelative(horizontalPx, topPx)
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
