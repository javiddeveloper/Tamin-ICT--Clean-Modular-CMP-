package com.tamin.taminhamrah.feature.khadamat.ui

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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.khadamat.ui.contract.*
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.CustomSearchBar
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.khadamat.*
import com.tamin.taminhamrah.ui.theme.*
import kotlinx.coroutines.flow.Flow

@Composable
fun KhadamatRoute(
    viewModel: KhadamatViewModel,
    onNavigateToRoute: (TaminScreens) -> Unit,
    onBackClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(KhadamatIntent.OnTabSelected(KhadamatTab.INSURED))
        viewModel.sendIntent(KhadamatIntent.OnSearchQueryChanged(""))
    }

    HandleKhadamatEvents(
        events = viewModel.events,
        onNavigateToRoute = onNavigateToRoute,
        onBackClicked = onBackClicked
    )

    KhadamatScreen(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onNavigateToRoute = onNavigateToRoute,
    )
}

@Composable
fun HandleKhadamatEvents(
    events: Flow<KhadamatEvent>,
    onNavigateToRoute: (TaminScreens) -> Unit,
    onBackClicked: () -> Unit
) {
    LaunchedEffect(events) {
        events.collect { event ->
            when (event) {
                is KhadamatEvent.NavigateToService -> {
                    val screen = when (event.flag) {
                        FeatureFlag.MERGE_HISTORY -> TaminScreens.History
                        FeatureFlag.WORKSHOPS -> TaminScreens.Workshops
                        FeatureFlag.CONTRACTS -> TaminScreens.Contracts
                        FeatureFlag.STUDENT_INSURANCE -> TaminScreens.StudentInsurance
                        FeatureFlag.FREELANCE_INSURANCE -> TaminScreens.FreelanceInsurance
                        FeatureFlag.OPTIONAL_INSURANCE -> TaminScreens.OptionalInsurance
                        FeatureFlag.HOUSEWIFE_INSURANCE -> TaminScreens.HousewifeInsurance
                        FeatureFlag.PENSION_INQUIRY -> TaminScreens.PensionInquiry
                        else -> null
                    }
                    screen?.let { onNavigateToRoute(it) }
                }
                is KhadamatEvent.NavigateToWeb -> {
                    onNavigateToRoute(TaminScreens.WebView(event.url))
                }
                is KhadamatEvent.ShowMessage -> {
                    onNavigateToRoute(TaminScreens.ShowMessage(event.message))
                }
            }
        }
    }
}

@Composable
fun KhadamatScreen(
    modifier: Modifier = Modifier,
    state: KhadamatUiState,
    onIntent: (KhadamatIntent) -> Unit,
    onNavigateToRoute: (TaminScreens) -> Unit,
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val headerTitle = when (state.selectedTab) {
            KhadamatTab.INSURED -> "خدمات بیمه‌شدگان"
            KhadamatTab.PENSIONER -> "خدمات مستمری‌بگیران"
            KhadamatTab.EMPLOYER -> "خدمات کارفرمایان"
        }

        //Quick Access
   /*     val featuredServices = remember(state.filteredServices, state.selectedTab) {
            val preferredIds = when (state.selectedTab) {
                KhadamatTab.INSURED -> listOf(7, 10, 34, 35, 36)
                KhadamatTab.PENSIONER -> listOf(105, 106, 107, 108, 112)
                KhadamatTab.EMPLOYER -> listOf(1001, 1002, 1004, 1006, 1010)
            }
            val featured = state.filteredServices.filter { it.id in preferredIds }
            if (featured.size < 5) {
                val remaining = state.filteredServices.filter { it.id !in preferredIds }
                (featured + remaining).take(5)
            } else {
                featured.take(5)
            }
        }*/

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = Elevation.xs,
                            shape = RoundedCornerShape(bottomStart = CornerRadius.x2l, bottomEnd = CornerRadius.x2l),
                            clip = false
                        )
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(bottomStart = CornerRadius.x2l, bottomEnd = CornerRadius.x2l)
                        )
                        .padding(top = Spacing.xl, start = Spacing.xlg, end = Spacing.xlg, bottom = Spacing.xxl)
                ) {
                    Text(
                        text = "خدمات",
                        style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Spacing.md)
                    )

                    Spacer(modifier = Modifier.height(Spacing.sm))

                    CustomSearchBar(
                        query = state.searchQuery,
                        onQueryChange = { onIntent(KhadamatIntent.OnSearchQueryChanged(it)) },
                        placeHolder = "جستجو در میان خدمات ..."
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.xlg))
            }

            // Tab Segmented Selector
            item {
                TabSelector(
                    selectedTab = state.selectedTab,
                    onTabSelected = { onIntent(KhadamatIntent.OnTabSelected(it)) },
                    modifier = Modifier.padding(horizontal = Spacing.xlg)
                )
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.xl))
            }

            // Quick Access Carousel Section
           /* item {
                KhadamatFeaturedCarousel(
                    featuredServices = featuredServices,
                    onServiceClick = { onIntent(KhadamatIntent.OnServiceClick(it)) }
                )
            }*/

            item {
                Spacer(modifier = Modifier.height(Spacing.xl))
            }

            item {
                KhadamatHeader(
                    title = headerTitle,
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
                                onClick = { onIntent(KhadamatIntent.OnServiceClick(service)) },
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

@PreviewRtlTheme
@Composable
fun PreviewKhadamatScreen() {
    PreviewRtlThemeContent {
        KhadamatScreen(
            onIntent = {},
            onNavigateToRoute = {},
            state = KhadamatUiState(
                isLoading = false,
                selectedTab = KhadamatTab.INSURED,
                filteredServices = emptyList()
            )
        )
    }
}

@PreviewRtlTheme
@Composable
fun PreviewKhadamatScreenDarkMode() {
    TaminHamrahTheme(darkTheme = true) {
        KhadamatScreen(
            onIntent = {},
            onNavigateToRoute = {},
            state = KhadamatUiState(
                isLoading = false,
                selectedTab = KhadamatTab.INSURED,
                filteredServices = emptyList()
            )
        )
    }
}
