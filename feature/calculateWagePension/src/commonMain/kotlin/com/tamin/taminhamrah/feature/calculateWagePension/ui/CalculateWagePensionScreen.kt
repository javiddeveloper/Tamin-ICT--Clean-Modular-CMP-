package com.tamin.taminhamrah.feature.calculateWagePension.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.calculateWagePension.ui.components.CalculateWagePensionBodyShimmer
import com.tamin.taminhamrah.feature.calculateWagePension.ui.components.CalculateWagePensionDisclaimerBanner
import com.tamin.taminhamrah.feature.calculateWagePension.ui.components.CalculateWagePensionHeader
import com.tamin.taminhamrah.feature.calculateWagePension.ui.components.CalculateWagePensionHistoryCard
import com.tamin.taminhamrah.feature.calculateWagePension.ui.components.CalculateWagePensionInfoSheet
import com.tamin.taminhamrah.feature.calculateWagePension.ui.components.CalculateWagePensionStatsCard
import com.tamin.taminhamrah.feature.calculateWagePension.ui.components.CalculateWagePensionWorkshopSwitchCard
import com.tamin.taminhamrah.feature.calculateWagePension.ui.components.CalculateWagePensionYearDetailSheet
import com.tamin.taminhamrah.feature.calculateWagePension.ui.contract.CalculateWagePensionEvent
import com.tamin.taminhamrah.feature.calculateWagePension.ui.contract.CalculateWagePensionIntent
import com.tamin.taminhamrah.feature.calculateWagePension.ui.contract.CalculateWagePensionUiState
import com.tamin.taminhamrah.model.calculateWagePension.BASIC_WAGE
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toparea.TopAreaState
import com.tamin.taminhamrah.ui.toparea.driveTopArea
import com.tamin.taminhamrah.ui.toparea.rememberMeasuredTopAreaState
import com.tamin.taminhamrah.ui.toparea.reportTopAreaHeight
import com.tamin.taminhamrah.ui.toparea.straddlePreviousSibling
import com.tamin.taminhamrah.ui.toparea.topAreaContentPadding
import com.tamin.taminhamrah.ui.toparea.topAreaContentSpacer
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.calculate_wage_pension_info_confirm
import taminx.core.core_ui.calculate_wage_pension_multiple_info_body
import taminx.core.core_ui.calculate_wage_pension_multiple_info_title
import taminx.core.core_ui.calculate_wage_pension_retry
import taminx.core.core_ui.ic_info

/** How far [CalculateWagePensionStatsCard] rides up into the header's reserved bottom space. */
private val StatsCardOverlap = Spacing.xxxl

@Composable
fun CalculateWagePensionScreen(
    onBack: () -> Unit,
    viewModel: CalculateWagePensionViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current

    viewModel.events.collectWithLifecycleAware { event ->
        when (event) {
            is CalculateWagePensionEvent.ShowToast -> toaster.error(event.message)
        }
    }

    CalculateWagePensionScreenContent(
        state = state,
        onBack = onBack,
        onIntent = viewModel::sendIntent,
    )
}

@Composable
internal fun CalculateWagePensionScreenContent(
    state: CalculateWagePensionUiState,
    onBack: () -> Unit,
    onIntent: (CalculateWagePensionIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    val isInitialLoading = state.isLoading && state.calculation == null

    val topArea = rememberMeasuredTopAreaState(key = isInitialLoading) { topAreaState ->
        CalculateWagePensionTopArea(
            state = state,
            isLoading = isInitialLoading,
            topAreaState = topAreaState,
            onBack = onBack,
            onInfoClick = { onIntent(CalculateWagePensionIntent.ShowInfo) },
        )
    }
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgPage),
    ) {
        when {
            isInitialLoading -> {
                CalculateWagePensionBodyShimmer(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
                        .padding(topAreaContentPadding(state = topArea))
                        .padding(horizontal = Spacing.page)
                        .padding(top = Spacing.lg, bottom = Spacing.lg),
                )
            }

            state.error != null && state.calculation == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
                        .padding(topAreaContentPadding(state = topArea))
                        .padding(Spacing.page),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    TaminText(
                        text = state.error.orEmpty(),
                        color = colors.dangerText,
                    )
                    Spacer(modifier = Modifier.height(Spacing.lg))
                    TaminFilledButton(
                        text = stringResource(Res.string.calculate_wage_pension_retry),
                        onClick = { onIntent(CalculateWagePensionIntent.Retry) },
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .driveTopArea(topArea, scrollState)
                        .verticalScroll(scrollState)
                        .navigationBarsPadding()
                        .padding(horizontal = Spacing.page)
                        .padding(bottom = Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    Spacer(modifier = Modifier.topAreaContentSpacer(topArea))

                    CalculateWagePensionWorkshopSwitchCard(
                        enabled = state.isMultipleWorkshopsEnabled,
                        isLoading = state.isMultipleWorkshopLoading,
                        onToggle = {
                            onIntent(CalculateWagePensionIntent.MultipleWorkshopsToggled(it))
                        },
                    )

                    state.calculation?.let { calculation ->
                        CalculateWagePensionHistoryCard(
                            years = calculation.historyYears,
                            months = calculation.historyMonths,
                            days = calculation.historyDays,
                            totalDays = calculation.totalHistoryDays,
                            chartItems = state.chartItems,
                            selectedIndex = state.selectedChartYearIndex,
                            onYearSelected = {
                                onIntent(CalculateWagePensionIntent.ChartYearSelected(it))
                            },
                        )
                    }

                    CalculateWagePensionDisclaimerBanner(
                        onClick = { onIntent(CalculateWagePensionIntent.ShowInfo) },
                    )
                }
            }
        }

        CalculateWagePensionTopArea(
            state = state,
            isLoading = isInitialLoading,
            topAreaState = topArea,
            onBack = onBack,
            onInfoClick = { onIntent(CalculateWagePensionIntent.ShowInfo) },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .reportTopAreaHeight(topArea),
        )
    }

    state.selectedChartYearIndex?.let { index ->
        state.chartItems.getOrNull(index)?.let { item ->
            CalculateWagePensionYearDetailSheet(
                item = item,
                onDismiss = { onIntent(CalculateWagePensionIntent.ChartYearSelected(null)) },
            )
        }
    }

    if (state.showInfoDialog) {
        CalculateWagePensionInfoSheet(
            averageSalary = state.calculation?.averageSalaryLastTwoYears ?: 0L,
            premiumYears = state.calculation?.premiumPaymentHistoryYear ?: 0.0,
            basicWage = BASIC_WAGE,
            estimatedAmount = state.displayedEligibleAmount,
            onDismiss = { onIntent(CalculateWagePensionIntent.DismissInfo) },
        )
    }

    if (state.showMultipleWorkshopsInfoDialog) {
        TaminConfirmationDialog(
            title = stringResource(Res.string.calculate_wage_pension_multiple_info_title),
            description = stringResource(Res.string.calculate_wage_pension_multiple_info_body),
            icon = vectorResource(Res.drawable.ic_info),
            iconTint = colors.onGradient,
            iconBackgroundBrush = colors.iconGradientPrimary,
            onDismissRequest = {
                onIntent(CalculateWagePensionIntent.DismissMultipleWorkshopsInfo)
            },
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(Res.string.calculate_wage_pension_info_confirm),
                    onClick = {
                        onIntent(CalculateWagePensionIntent.DismissMultipleWorkshopsInfo)
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            dismissButton = {},
        )
    }
}

@Composable
private fun CalculateWagePensionTopArea(
    state: CalculateWagePensionUiState,
    isLoading: Boolean,
    topAreaState: TopAreaState,
    onBack: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        CalculateWagePensionHeader(
            eligibleAmount = state.displayedEligibleAmount,
            legalFloorApplied = state.calculation?.legalFloorApplied == true,
            isMultipleWorkshopsEnabled = state.isMultipleWorkshopsEnabled,
            onBack = onBack,
            onInfoClick = onInfoClick,
            isLoading = isLoading,
            topAreaState = topAreaState,
            bottomPadding = Spacing.lg + StatsCardOverlap,
            modifier = Modifier,
        )
        CalculateWagePensionStatsCard(
            premiumYears = state.calculation?.premiumPaymentHistoryYear ?: 0.0,
            averageSalary = state.calculation?.averageSalaryLastTwoYears ?: 0L,
            isLoading = isLoading,
            modifier = Modifier
                .straddlePreviousSibling(StatsCardOverlap)
                .padding(horizontal = Spacing.lg),
        )
        Spacer(Modifier.height(Spacing.lg))
    }
}

@PreviewRtlTheme
@Composable
private fun CalculateWagePensionScreenLoadedPreview() {
    PreviewRtlThemeContent {
        CalculateWagePensionScreenContent(
            state = CalculateWagePensionPreviewData.loadedState,
            onBack = {},
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun CalculateWagePensionScreenLoadingPreview() {
    PreviewRtlThemeContent {
        CalculateWagePensionScreenContent(
            state = CalculateWagePensionPreviewData.loadingState,
            onBack = {},
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun CalculateWagePensionScreenErrorPreview() {
    PreviewRtlThemeContent {
        CalculateWagePensionScreenContent(
            state = CalculateWagePensionPreviewData.errorState,
            onBack = {},
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun CalculateWagePensionScreenDarkPreview() {
    PreviewRtlThemeContent(darkTheme = true) {
        CalculateWagePensionScreenContent(
            state = CalculateWagePensionPreviewData.loadedState,
            onBack = {},
            onIntent = {},
        )
    }
}
