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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.calculateWagePension.ui.components.CalculateWagePensionDisclaimerBanner
import com.tamin.taminhamrah.feature.calculateWagePension.ui.components.CalculateWagePensionHeader
import com.tamin.taminhamrah.feature.calculateWagePension.ui.components.CalculateWagePensionHistoryCard
import com.tamin.taminhamrah.feature.calculateWagePension.ui.components.CalculateWagePensionStatsCard
import com.tamin.taminhamrah.feature.calculateWagePension.ui.components.CalculateWagePensionWorkshopSwitchCard
import com.tamin.taminhamrah.feature.calculateWagePension.ui.components.CalculateWagePensionYearDetailSheet
import com.tamin.taminhamrah.feature.calculateWagePension.ui.contract.CalculateWagePensionEvent
import com.tamin.taminhamrah.feature.calculateWagePension.ui.contract.CalculateWagePensionIntent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.ButtonDimens
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    val colors = LocalTaminColors.current
    val hazeState = remember { HazeState(initialBlurEnabled = true) }

    viewModel.events.collectWithLifecycleAware { event ->
        when (event) {
            is CalculateWagePensionEvent.ShowToast -> toaster.error(event.message)
        }
    }

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
                        onInfoClick = { viewModel.sendIntent(CalculateWagePensionIntent.ShowInfo) },
                        showInfoDialog = state.showInfoDialog,
                        onDismissInfo = { viewModel.sendIntent(CalculateWagePensionIntent.DismissInfo) },
                    )
                    // Room for the glass stats card to sit on the gradient edge.
                    Spacer(modifier = Modifier.height(Spacing.xxxl))
                }

                CalculateWagePensionStatsCard(
                    hazeState = hazeState,
                    premiumYears = state.calculation?.premiumPaymentHistoryYear ?: 0.0,
                    averageSalary = state.calculation?.averageSalaryLastTwoYears ?: 0L,
                    isLoading = state.isLoading && state.calculation == null,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = Spacing.lg),
                )
            }
        },
    ) { padding ->
        when {
            state.isLoading && state.calculation == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        color = colors.blueText,
                        strokeWidth = ButtonDimens.loadingIndicatorStroke,
                    )
                }
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
                        onClick = { viewModel.sendIntent(CalculateWagePensionIntent.Retry) },
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
                            viewModel.sendIntent(
                                CalculateWagePensionIntent.MultipleWorkshopsToggled(it)
                            )
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
                                viewModel.sendIntent(
                                    CalculateWagePensionIntent.ChartYearSelected(it)
                                )
                            },
                        )
                    }

                    CalculateWagePensionDisclaimerBanner(
                        onClick = { viewModel.sendIntent(CalculateWagePensionIntent.ShowInfo) },
                    )
                }
            }
        }
    }

    state.selectedChartYearIndex?.let { index ->
        state.chartItems.getOrNull(index)?.let { item ->
            CalculateWagePensionYearDetailSheet(
                item = item,
                onDismiss = {
                    viewModel.sendIntent(CalculateWagePensionIntent.ChartYearSelected(null))
                },
            )
        }
    }

    if (state.showMultipleWorkshopsInfoDialog) {
        TaminConfirmationDialog(
            title = stringResource(Res.string.calculate_wage_pension_multiple_info_title),
            description = stringResource(Res.string.calculate_wage_pension_multiple_info_body),
            icon = vectorResource(Res.drawable.ic_info),
            iconTint = colors.onGradient,
            iconBackgroundBrush = colors.iconGradientPrimary,
            onDismissRequest = {
                viewModel.sendIntent(CalculateWagePensionIntent.DismissMultipleWorkshopsInfo)
            },
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(Res.string.calculate_wage_pension_info_confirm),
                    onClick = {
                        viewModel.sendIntent(CalculateWagePensionIntent.DismissMultipleWorkshopsInfo)
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            dismissButton = {},
        )
    }
}
