package com.tamin.taminhamrah.ui.khadamat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.ui.components.CustomSearchBar
import com.tamin.taminhamrah.ui.khadamat.contract.*
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(TaminLightBgPage)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                        clip = false
                    )
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    )
                    .padding(top = 24.dp, start = 20.dp, end = 20.dp, bottom = 28.dp)
            ) {

                Text(
                    text = "خدمات",
                    color = TaminNavy900,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                CustomSearchBar(
                    query = state.searchQuery,
                    onQueryChange = { onIntent(KhadamatIntent.OnSearchQueryChanged(it)) },
                    placeHolder = "جستجو در میان خدمات ...",
                    showNoResults = state.showNoResultsError
                )
            }




            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {

                TabSelector(
                    selectedTab = state.selectedTab,
                    onTabSelected = { onIntent(KhadamatIntent.OnTabSelected(it)) }
                )

                Spacer(modifier = Modifier.height(24.dp))


                val headerTitle = when (state.selectedTab) {
                    KhadamatTab.INSURED -> "خدمات بیمه‌شدگان"
                    KhadamatTab.PENSIONER -> "خدمات مستمری‌بگیران"
                    KhadamatTab.EMPLOYER -> "خدمات کارفرمایان"
                }
                KhadamatHeader(
                    title = headerTitle,
                    badgeText = "${state.filteredServices.size} خدمت"
                )

                Spacer(modifier = Modifier.height(16.dp))


                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = TaminNavy700)
                    }
                } else {
                    ServiceGrid(
                        services = state.filteredServices,
                        onServiceClick = { onIntent(KhadamatIntent.OnServiceClick(it)) },
                        modifier = Modifier.weight(1f)
                    )
                }
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
