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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.taminServices.ui.contract.*
import com.tamin.taminhamrah.feature.taminServices.ui.contract.TaminScreens.*
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.CustomSearchBar
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.taminServices.ServiceCard
import com.tamin.taminhamrah.ui.components.taminServices.TabSelector
import com.tamin.taminhamrah.ui.components.taminServices.TaminServicesHeader
import com.tamin.taminhamrah.ui.components.taminServices.TaminServicesTab
import com.tamin.taminhamrah.ui.theme.*
import kotlinx.coroutines.flow.Flow

@Composable
fun TaminServicesRoute(
    viewModel: TamminServicesViewModel,
    onNavigateToRoute: (TaminScreens) -> Unit,
    onBackClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(TaminServicesIntent.OnTabSelected(TaminServicesTab.INSURED))
        viewModel.sendIntent(TaminServicesIntent.OnSearchQueryChanged(""))
    }

    HandleTaminServicesEvents(
        events = viewModel.events,
        onNavigateToRoute = onNavigateToRoute,
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
    onNavigateToRoute: (TaminScreens) -> Unit,
    onBackClicked: () -> Unit
) {
    LaunchedEffect(events) {
        events.collect { event ->
            when (event) {
                is TaminSericesEvent.NavigateToService -> {
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
                is TaminSericesEvent.NavigateToWeb -> {
                    onNavigateToRoute(WebView(event.url))
                }
                is TaminSericesEvent.NavigateBack -> { onBackClicked()}
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
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val headerTitle = when (state.selectedTab) {
            TaminServicesTab.INSURED -> "خدمات بیمه‌شدگان"
            TaminServicesTab.PENSIONER -> "خدمات مستمری‌بگیران"
            TaminServicesTab.EMPLOYER -> "خدمات کارفرمایان"
        }

        //Quick Access
        /*     val featuredServices = remember(state.filteredServices, state.selectedTab) {
                 val preferredIds = when (state.selectedTab) {
                     TaminServicesTab.INSURED -> listOf(7, 10, 34, 35, 36)
                     TaminServicesTab.PENSIONER -> listOf(105, 106, 107, 108, 112)
                     TaminServicesTab.EMPLOYER -> listOf(1001, 1002, 1004, 1006, 1010)
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
                        style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Spacing.md)
                    )

                    Spacer(modifier = Modifier.height(Spacing.sm))

                    CustomSearchBar(
                        query = state.searchQuery,
                        onQueryChange = { onIntent(TaminServicesIntent.OnSearchQueryChanged(it)) },
                        placeHolder = "جستجو در میان خدمات ..."
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.xlg))
            }

            item {
                TabSelector(
                    selectedTab = state.selectedTab,
                    onTabSelected = { onIntent(TaminServicesIntent.OnTabSelected(it)) },
                    modifier = Modifier.padding(horizontal = Spacing.xlg)
                )
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.xl))
            }

            // Quick Access Carousel Section
            /* item {
                 TaminServicesFeaturedCarousel(
                     featuredServices = featuredServices,
                     onServiceClick = { onIntent(TaminServicesIntent.OnServiceClick(it)) }
                 )
             }*/

            item {
                Spacer(modifier = Modifier.height(Spacing.xl))
            }

            item {
                TaminServicesHeader(
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

@PreviewRtlTheme
@Composable
fun PreviewTaminServicesScreen() {
    PreviewRtlThemeContent {
        TaminServicesScreen(
            onIntent = {},
            state = TaminServicesUiState(
                isLoading = false,
                selectedTab = TaminServicesTab.INSURED,
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
                selectedTab = TaminServicesTab.INSURED,
                filteredServices = emptyList()
            )
        )
    }
}
