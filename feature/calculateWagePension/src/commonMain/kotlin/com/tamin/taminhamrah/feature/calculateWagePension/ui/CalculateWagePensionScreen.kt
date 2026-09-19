package com.tamin.taminhamrah.feature.calculateWagePension.ui

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
import androidx.compose.material3.Scaffold
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
    val hazeState = remember { HazeState(initialBlurEnabled = true) }
    val isInitialLoading = state.isLoading && state.calculation == null

    Scaffold(
        containerColor = colors.bgPage,
        topBar = {
            Box {
                Column(modifier = Modifier.hazeSource(state = hazeState)) {
                    CalculateWagePensionHeader(
                        eligibleAmount = state.displayedEligibleAmount,
                        legalFloorApplied = state.calculation?.legalFloorApplied == true,
                        isMultipleWorkshopsEnabled = state.isMultipleWorkshopsEnabled,
                        onBack = onBack,
                        onInfoClick = { onIntent(CalculateWagePensionIntent.ShowInfo) },
                        isLoading = isInitialLoading,
                    )
                    Spacer(modifier = Modifier.height(Spacing.xxxl))
                }

                CalculateWagePensionStatsCard(
                    hazeState = hazeState,
                    premiumYears = state.calculation?.premiumPaymentHistoryYear ?: 0.0,
                    averageSalary = state.calculation?.averageSalaryLastTwoYears ?: 0L,
                    isLoading = isInitialLoading,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = Spacing.lg),
                )
            }
        },
    ) { padding ->
        when {
            isInitialLoading -> {
                CalculateWagePensionBodyShimmer(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .navigationBarsPadding()
                        .padding(horizontal = Spacing.page)
                        .padding(top = Spacing.lg, bottom = Spacing.lg),
                )
            }

            state.error != null && state.calculation == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
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
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .navigationBarsPadding()
                        .padding(horizontal = Spacing.page)
                        .padding(top = Spacing.lg, bottom = Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
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
