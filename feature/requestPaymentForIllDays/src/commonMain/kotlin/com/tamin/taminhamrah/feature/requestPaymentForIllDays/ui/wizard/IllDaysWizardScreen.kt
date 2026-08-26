package com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.wizard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysBranchWorkshopPR
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.LoadingButtonIconPosition
import com.tamin.taminhamrah.ui.components.PickerRow
import com.tamin.taminhamrah.ui.components.StepIndicator
import com.tamin.taminhamrah.ui.components.StepIndicatorModel
import com.tamin.taminhamrah.ui.components.StepState
import com.tamin.taminhamrah.ui.components.TaminBottomBar
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.components.TaminSwitchButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_branch
import taminx.core.core_ui.ic_calculator
import taminx.core.core_ui.ic_place
import taminx.core.core_ui.ic_tamin_calendar
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ill_days_cd_back
import taminx.core.core_ui.ill_days_cd_calculator
import taminx.core.core_ui.ill_days_covid_end_label
import taminx.core.core_ui.ill_days_covid_info_prefix
import taminx.core.core_ui.ill_days_covid_start_label
import taminx.core.core_ui.ill_days_covid_toggle_caption
import taminx.core.core_ui.ill_days_covid_toggle_label
import taminx.core.core_ui.ill_days_day_count_badge
import taminx.core.core_ui.ill_days_next_step
import taminx.core.core_ui.ill_days_pick_date_placeholder
import taminx.core.core_ui.ill_days_rest_end_label
import taminx.core.core_ui.ill_days_rest_start_label
import taminx.core.core_ui.ill_days_title
import taminx.core.core_ui.ill_days_wizard_branch_label
import taminx.core.core_ui.ill_days_wizard_branch_placeholder
import taminx.core.core_ui.ill_days_wizard_city_label
import taminx.core.core_ui.ill_days_wizard_city_placeholder
import taminx.core.core_ui.ill_days_wizard_pick_branch_subtitle
import taminx.core.core_ui.ill_days_wizard_pick_branch_title
import taminx.core.core_ui.ill_days_wizard_pick_city_subtitle
import taminx.core.core_ui.ill_days_wizard_pick_city_title
import taminx.core.core_ui.ill_days_wizard_step1_description
import taminx.core.core_ui.ill_days_wizard_step1_title
import taminx.core.core_ui.ill_days_wizard_step2_description
import taminx.core.core_ui.ill_days_wizard_step2_title
import taminx.core.core_ui.ill_days_wizard_step_branch_city
import taminx.core.core_ui.ill_days_wizard_step_doctor
import taminx.core.core_ui.ill_days_wizard_step_documents
import taminx.core.core_ui.ill_days_wizard_step_rest_days

@Composable
fun IllDaysWizardScreen(
    onBack: () -> Unit,
    onOpenCalculate: () -> Unit,
    viewModel: IllDaysWizardViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current

    HandleIllDaysWizardEvents(
        events = viewModel.events,
        onBack = onBack,
        onOpenCalculate = onOpenCalculate,
        onShowToast = { toaster.error(it) },
    )

    IllDaysWizardContent(
        state = state,
        onIntent = viewModel::sendIntent,
    )
}

@Composable
private fun HandleIllDaysWizardEvents(
    events: Flow<IllDaysWizardEvent>,
    onBack: () -> Unit,
    onOpenCalculate: () -> Unit,
    onShowToast: (String) -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            IllDaysWizardEvent.NavigateBack -> onBack()
            IllDaysWizardEvent.NavigateToCalculate -> onOpenCalculate()
            is IllDaysWizardEvent.ShowToast -> onShowToast(event.message)
        }
    }
}

@Composable
private fun IllDaysWizardContent(
    state: IllDaysWizardUiState,
    onIntent: (IllDaysWizardIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    val headerBrush = Brush.horizontalGradient(colors.profileGradientStops)
    val step1 = stringResource(Res.string.ill_days_wizard_step_branch_city)
    val step2 = stringResource(Res.string.ill_days_wizard_step_rest_days)
    val step3 = stringResource(Res.string.ill_days_wizard_step_doctor)
    val step4 = stringResource(Res.string.ill_days_wizard_step_documents)
    val steps = remember(state.currentStep, step1, step2, step3, step4) {
        persistentListOf(
            StepIndicatorModel(
                title = step1,
                stepNumber = "۱",
                state = when (state.currentStep) {
                    IllDaysWizardStep.BranchCity -> StepState.Active
                    IllDaysWizardStep.RestDays -> StepState.Completed
                },
            ),
            StepIndicatorModel(
                title = step2,
                stepNumber = "۲",
                state = when (state.currentStep) {
                    IllDaysWizardStep.BranchCity -> StepState.Inactive
                    IllDaysWizardStep.RestDays -> StepState.Active
                },
            ),
            StepIndicatorModel(
                title = step3,
                stepNumber = "۳",
                state = StepState.Inactive,
            ),
            StepIndicatorModel(
                title = step4,
                stepNumber = "۴",
                state = StepState.Inactive,
            ),
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.bgPage,
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.ill_days_title),
                background = headerBrush,
                shape = RoundedCornerShape(
                    bottomStart = CornerRadius.x3l,
                    bottomEnd = CornerRadius.x3l,
                ),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = stringResource(Res.string.ill_days_cd_back),
                        onClick = { onIntent(IllDaysWizardIntent.Back) },
                        bordered = true,
                    )
                },
                action = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_calculator),
                        contentDescription = stringResource(Res.string.ill_days_cd_calculator),
                        onClick = { onIntent(IllDaysWizardIntent.OpenCalculate) },
                        bordered = true,
                    )
                },
            )
        },
        bottomBar = {
            if (!state.isLoading && state.errorMessage == null) {
                IllDaysWizardBottomBar(state = state, onIntent = onIntent)
            }
        },
    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = colors.blueText)
                }
            }
            state.errorMessage != null -> {
                ErrorStateView(
                    message = state.errorMessage,
                    onDismiss = { onIntent(IllDaysWizardIntent.Back) },
                    onRetry = { onIntent(IllDaysWizardIntent.Retry) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                )
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                ) {
                    StepIndicator(
                        steps = steps,
                        modifier = Modifier.padding(
                            start = Spacing.lg,
                            end = Spacing.lg,
                            top = Spacing.md,
                            bottom = Spacing.md,
                        ),
                    )
                    AnimatedContent(
                        targetState = state.currentStep,
                        modifier = Modifier.weight(1f),
                        transitionSpec = {
                            if (targetState == IllDaysWizardStep.RestDays) {
                                slideInHorizontally { -it } + fadeIn() togetherWith
                                    slideOutHorizontally { it } + fadeOut()
                            } else {
                                slideInHorizontally { it } + fadeIn() togetherWith
                                    slideOutHorizontally { -it } + fadeOut()
                            }
                        },
                        label = "illDaysWizardStep",
                    ) { step ->
                        when (step) {
                            IllDaysWizardStep.BranchCity -> BranchCityStep(
                                state = state,
                                onIntent = onIntent,
                            )
                            IllDaysWizardStep.RestDays -> RestDaysStep(
                                state = state,
                                onIntent = onIntent,
                            )
                        }
                    }
                }
            }
        }
    }

    IllDaysWizardPickers(state = state, onIntent = onIntent)
}

@Composable
private fun IllDaysWizardBottomBar(
    state: IllDaysWizardUiState,
    onIntent: (IllDaysWizardIntent) -> Unit,
) {
    TaminBottomBar(
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding(),
    ) {
        when (state.currentStep) {
            IllDaysWizardStep.BranchCity -> {
                LoadingButton(
                    text = stringResource(Res.string.ill_days_next_step),
                    onClick = { onIntent(IllDaysWizardIntent.NextStep) },
                    enabled = state.canGoNextFromStep1,
                    modifier = Modifier.fillMaxWidth(),
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                    iconPosition = LoadingButtonIconPosition.TRAILING,
                )
            }
            IllDaysWizardStep.RestDays -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.smd),
                ) {
                    LoadingButton(
                        text = stringResource(Res.string.ill_days_next_step),
                        onClick = { onIntent(IllDaysWizardIntent.NextStep) },
                        enabled = state.canGoNextFromStep2 && !state.isCovidLoading,
                        isLoading = state.isCovidLoading,
                        modifier = Modifier.weight(1f),
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                        iconPosition = LoadingButtonIconPosition.TRAILING,
                    )
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = stringResource(Res.string.ill_days_cd_back),
                        onClick = { onIntent(IllDaysWizardIntent.PreviousStep) },
                        bordered = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun BranchCityStep(
    state: IllDaysWizardUiState,
    onIntent: (IllDaysWizardIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.page, vertical = Spacing.lg)
            .taminSurface(cornerRadius = CornerRadius.cardCompact)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Text(
            text = stringResource(Res.string.ill_days_wizard_step1_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
        )
        Text(
            text = stringResource(Res.string.ill_days_wizard_step1_description),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted,
        )
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Text(
                text = stringResource(Res.string.ill_days_wizard_branch_label),
                style = MaterialTheme.typography.labelLarge,
                color = colors.textPrimary,
            )
            PickerRow(
                text = state.selectedBranch?.label
                    ?: stringResource(Res.string.ill_days_wizard_branch_placeholder),
                onClick = { onIntent(IllDaysWizardIntent.OpenBranchPicker) },
                isPlaceholder = state.selectedBranch == null,
                icon = vectorResource(Res.drawable.ic_branch),
                showChevron = true,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Text(
                text = stringResource(Res.string.ill_days_wizard_city_label),
                style = MaterialTheme.typography.labelLarge,
                color = colors.textPrimary,
            )
            PickerRow(
                text = state.selectedCity?.cityName
                    ?: stringResource(Res.string.ill_days_wizard_city_placeholder),
                onClick = { onIntent(IllDaysWizardIntent.OpenCityPicker) },
                isPlaceholder = state.selectedCity == null,
                icon = vectorResource(Res.drawable.ic_place),
                showChevron = true,
            )
        }
    }
}

@Composable
private fun RestDaysStep(
    state: IllDaysWizardUiState,
    onIntent: (IllDaysWizardIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.page, vertical = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Text(
            text = stringResource(Res.string.ill_days_wizard_step2_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
        )
        Text(
            text = stringResource(Res.string.ill_days_wizard_step2_description),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CornerRadius.cardCompact))
                .background(colors.bgSurface)
                .border(
                    width = Thickness.border,
                    color = if (state.isCovid) colors.blueText else colors.border,
                    shape = RoundedCornerShape(CornerRadius.cardCompact),
                )
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.ill_days_covid_toggle_label),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary,
                    )
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = stringResource(Res.string.ill_days_covid_toggle_caption),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted,
                    )
                }
                TaminSwitchButton(
                    checked = state.isCovid,
                    onCheckedChange = { onIntent(IllDaysWizardIntent.CovidChanged(it)) },
                    enabled = !state.isCovidLoading,
                )
            }
        }

        if (state.isCovid) {
            CovidDatesPanel(state = state)
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    Text(
                        text = stringResource(Res.string.ill_days_rest_start_label),
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.textPrimary,
                    )
                    PickerRow(
                        text = state.startDateLabel.ifBlank {
                            stringResource(Res.string.ill_days_pick_date_placeholder)
                        },
                        onClick = { onIntent(IllDaysWizardIntent.OpenStartDatePicker) },
                        isPlaceholder = state.startDateLabel.isBlank(),
                        icon = vectorResource(Res.drawable.ic_tamin_calendar),
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    Text(
                        text = stringResource(Res.string.ill_days_rest_end_label),
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.textPrimary,
                    )
                    PickerRow(
                        text = state.endDateLabel.ifBlank {
                            stringResource(Res.string.ill_days_pick_date_placeholder)
                        },
                        onClick = { onIntent(IllDaysWizardIntent.OpenEndDatePicker) },
                        isPlaceholder = state.endDateLabel.isBlank(),
                        icon = vectorResource(Res.drawable.ic_tamin_calendar),
                    )
                }
            }
            state.dayCount?.let { DayCountBadge(dayCount = it) }
        }
    }
}

@Composable
private fun CovidDatesPanel(state: IllDaysWizardUiState) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.greenBg)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Text(
            text = stringResource(Res.string.ill_days_covid_info_prefix),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textPrimary,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            CovidDateBox(
                label = stringResource(Res.string.ill_days_covid_start_label),
                value = state.startDateLabel,
                modifier = Modifier.weight(1f),
            )
            CovidDateBox(
                label = stringResource(Res.string.ill_days_covid_end_label),
                value = state.endDateLabel,
                modifier = Modifier.weight(1f),
            )
        }
        state.dayCount?.let { DayCountBadge(dayCount = it) }
    }
}

@Composable
private fun CovidDateBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(CornerRadius.md))
            .background(colors.bgSurface)
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
        )
    }
}

@Composable
private fun DayCountBadge(dayCount: Int) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(CornerRadius.full))
            .background(colors.blueBg)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Icon(
            imageVector = Icons.Outlined.AccessTime,
            contentDescription = null,
            tint = colors.blueText,
            modifier = Modifier.size(IconSize.small),
        )
        Text(
            text = stringResource(
                Res.string.ill_days_day_count_badge,
                dayCount.toString().toPersianDigits(),
            ),
            style = MaterialTheme.typography.labelMedium,
            color = colors.blueText,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IllDaysWizardPickers(
    state: IllDaysWizardUiState,
    onIntent: (IllDaysWizardIntent) -> Unit,
) {
    when (state.picker) {
        IllDaysWizardPicker.Branch -> OptionSheet(
            title = stringResource(Res.string.ill_days_wizard_pick_branch_title),
            subtitle = stringResource(Res.string.ill_days_wizard_pick_branch_subtitle),
            onDismiss = { onIntent(IllDaysWizardIntent.DismissPicker) },
        ) {
            BranchOptions(
                options = state.branchOptions,
                selectedId = state.selectedBranch?.id,
                onSelect = { onIntent(IllDaysWizardIntent.BranchPicked(it)) },
            )
        }
        IllDaysWizardPicker.City -> OptionSheet(
            title = stringResource(Res.string.ill_days_wizard_pick_city_title),
            subtitle = stringResource(Res.string.ill_days_wizard_pick_city_subtitle),
            onDismiss = { onIntent(IllDaysWizardIntent.DismissPicker) },
        ) {
            CityOptions(
                options = state.cityOptions,
                selectedCode = state.selectedCity?.cityCode,
                onSelect = { onIntent(IllDaysWizardIntent.CityPicked(it)) },
            )
        }
        IllDaysWizardPicker.StartDate -> TaminJalaliDatePicker(
            title = stringResource(Res.string.ill_days_rest_start_label),
            onDismiss = { onIntent(IllDaysWizardIntent.DismissPicker) },
            onConfirm = { year, month, day ->
                onIntent(
                    IllDaysWizardIntent.StartDatePicked(
                        millis = PersianDateFormatter.toEpochMillis(year, month, day),
                        label = PersianDateFormatter.format(year, month, day),
                    )
                )
            },
        )
        IllDaysWizardPicker.EndDate -> TaminJalaliDatePicker(
            title = stringResource(Res.string.ill_days_rest_end_label),
            onDismiss = { onIntent(IllDaysWizardIntent.DismissPicker) },
            onConfirm = { year, month, day ->
                onIntent(
                    IllDaysWizardIntent.EndDatePicked(
                        millis = PersianDateFormatter.toEpochMillis(year, month, day),
                        label = PersianDateFormatter.format(year, month, day),
                    )
                )
            },
        )
        IllDaysWizardPicker.None -> Unit
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OptionSheet(
    title: String,
    subtitle: String,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    val colors = LocalTaminColors.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.bgPage,
        shape = RoundedCornerShape(topStart = CornerRadius.sheet, topEnd = CornerRadius.sheet),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = Spacing.page)
                .padding(bottom = Spacing.md),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
            )
            Spacer(modifier = Modifier.height(Spacing.xxs))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
            )
            Spacer(modifier = Modifier.height(Spacing.lg))
            content()
        }
    }
}

@Composable
private fun BranchOptions(
    options: ImmutableList<IllDaysBranchWorkshopPR>,
    selectedId: String?,
    onSelect: (IllDaysBranchWorkshopPR) -> Unit,
) {
    val colors = LocalTaminColors.current
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        options.forEach { option ->
            val selected = option.id == selectedId
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(CornerRadius.lg))
                    .background(if (selected) colors.blueBg else colors.bgSurface)
                    .border(
                        width = Thickness.border,
                        color = if (selected) colors.blueText else colors.border,
                        shape = RoundedCornerShape(CornerRadius.lg),
                    )
                    .clickable(onClick = { onSelect(option) })
                    .padding(Spacing.lg),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = option.label,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                if (selected) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_tamin_check),
                        contentDescription = null,
                        tint = colors.blueText,
                        modifier = Modifier.size(IconSize.small),
                    )
                }
            }
        }
    }
}

@Composable
private fun CityOptions(
    options: ImmutableList<CityPR>,
    selectedCode: String?,
    onSelect: (CityPR) -> Unit,
) {
    val colors = LocalTaminColors.current
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        options.forEach { option ->
            val selected = option.cityCode == selectedCode
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(CornerRadius.lg))
                    .background(if (selected) colors.blueBg else colors.bgSurface)
                    .border(
                        width = Thickness.border,
                        color = if (selected) colors.blueText else colors.border,
                        shape = RoundedCornerShape(CornerRadius.lg),
                    )
                    .clickable(onClick = { onSelect(option) })
                    .padding(Spacing.lg),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = option.cityName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
                if (selected) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_tamin_check),
                        contentDescription = null,
                        tint = colors.blueText,
                        modifier = Modifier.size(IconSize.small),
                    )
                }
            }
        }
    }
}
