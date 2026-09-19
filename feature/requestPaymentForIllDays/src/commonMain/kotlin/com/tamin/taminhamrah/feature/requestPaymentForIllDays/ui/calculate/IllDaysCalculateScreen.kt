package com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.calculate

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.PickerRow
import com.tamin.taminhamrah.ui.components.TaminBottomBar
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
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
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_calendar
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ill_days_calc_avg_wage
import taminx.core.core_ui.ill_days_calc_end_date
import taminx.core.core_ui.ill_days_calc_marital_married
import taminx.core.core_ui.ill_days_calc_marital_married_caption
import taminx.core.core_ui.ill_days_calc_marital_single
import taminx.core.core_ui.ill_days_calc_marital_single_caption
import taminx.core.core_ui.ill_days_calc_payable_disclaimer
import taminx.core.core_ui.ill_days_calc_payable_label
import taminx.core.core_ui.ill_days_calc_rest_duration
import taminx.core.core_ui.ill_days_calc_section_title
import taminx.core.core_ui.ill_days_calc_start_date
import taminx.core.core_ui.ill_days_calc_submit_btn
import taminx.core.core_ui.ill_days_calc_title
import taminx.core.core_ui.ill_days_cd_back
import taminx.core.core_ui.ill_days_day_count_badge

@Composable
fun IllDaysCalculateScreen(
    onBack: () -> Unit,
    viewModel: IllDaysCalculateViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current

    HandleIllDaysCalculateEvents(
        events = viewModel.events,
        onBack = onBack,
        onShowToast = { toaster.error(it) },
    )

    IllDaysCalculateContent(
        state = state,
        onIntent = viewModel::sendIntent,
    )
}

@Composable
private fun HandleIllDaysCalculateEvents(
    events: Flow<IllDaysCalculateEvent>,
    onBack: () -> Unit,
    onShowToast: (String) -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            IllDaysCalculateEvent.NavigateBack -> onBack()
            is IllDaysCalculateEvent.ShowToast -> onShowToast(event.message)
        }
    }
}

@Composable
private fun IllDaysCalculateContent(
    state: IllDaysCalculateUiState,
    onIntent: (IllDaysCalculateIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    val headerBrush = Brush.horizontalGradient(colors.profileGradientStops)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.bgPage,
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.ill_days_calc_title),
                background = headerBrush,
                shape = RoundedCornerShape(
                    bottomStart = CornerRadius.x3l,
                    bottomEnd = CornerRadius.x3l,
                ),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = stringResource(Res.string.ill_days_cd_back),
                        onClick = { onIntent(IllDaysCalculateIntent.Back) },
                        bordered = true,
                    )
                },
            )
        },
        bottomBar = {
            TaminBottomBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding(),
            ) {
                LoadingButton(
                    text = stringResource(Res.string.ill_days_calc_submit_btn),
                    onClick = { onIntent(IllDaysCalculateIntent.Calculate) },
                    enabled = state.canCalculate,
                    isLoading = state.isCalculating,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.page, vertical = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            IllnessDetailsCard(state = state, onIntent = onIntent)
            state.result?.let { result ->
                CalculationBreakdownCard(result = result)
                PayableResultCard(result = result)
            }
        }
    }

    when (state.activeDatePicker) {
        IllDaysDatePicker.Start -> TaminJalaliDatePicker(
            title = stringResource(Res.string.ill_days_calc_start_date),
            onDismiss = { onIntent(IllDaysCalculateIntent.DismissDatePicker) },
            onConfirm = { year, month, day ->
                onIntent(
                    IllDaysCalculateIntent.StartDatePicked(
                        millis = PersianDateFormatter.toEpochMillis(year, month, day),
                        label = PersianDateFormatter.format(year, month, day),
                    )
                )
            },
        )
        IllDaysDatePicker.End -> TaminJalaliDatePicker(
            title = stringResource(Res.string.ill_days_calc_end_date),
            onDismiss = { onIntent(IllDaysCalculateIntent.DismissDatePicker) },
            onConfirm = { year, month, day ->
                onIntent(
                    IllDaysCalculateIntent.EndDatePicked(
                        millis = PersianDateFormatter.toEpochMillis(year, month, day),
                        label = PersianDateFormatter.format(year, month, day),
                    )
                )
            },
        )
        IllDaysDatePicker.None -> Unit
    }
}

@Composable
private fun IllnessDetailsCard(
    state: IllDaysCalculateUiState,
    onIntent: (IllDaysCalculateIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .taminSurface(cornerRadius = CornerRadius.cardCompact)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Text(
            text = stringResource(Res.string.ill_days_calc_section_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            PickerRow(
                text = state.startDateLabel.ifBlank {
                    stringResource(Res.string.ill_days_calc_start_date)
                },
                onClick = { onIntent(IllDaysCalculateIntent.OpenStartDatePicker) },
                isPlaceholder = state.startDateLabel.isBlank(),
                icon = vectorResource(Res.drawable.ic_tamin_calendar),
                modifier = Modifier.weight(1f),
            )
            PickerRow(
                text = state.endDateLabel.ifBlank {
                    stringResource(Res.string.ill_days_calc_end_date)
                },
                onClick = { onIntent(IllDaysCalculateIntent.OpenEndDatePicker) },
                isPlaceholder = state.endDateLabel.isBlank(),
                icon = vectorResource(Res.drawable.ic_tamin_calendar),
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            MaritalOptionCard(
                title = stringResource(Res.string.ill_days_calc_marital_single),
                caption = stringResource(Res.string.ill_days_calc_marital_single_caption),
                selected = state.maritalStatus == IllDaysMaritalStatus.Single,
                onClick = {
                    onIntent(IllDaysCalculateIntent.SelectMarital(IllDaysMaritalStatus.Single))
                },
                modifier = Modifier.weight(1f),
            )
            MaritalOptionCard(
                title = stringResource(Res.string.ill_days_calc_marital_married),
                caption = stringResource(Res.string.ill_days_calc_marital_married_caption),
                selected = state.maritalStatus == IllDaysMaritalStatus.Married,
                onClick = {
                    onIntent(IllDaysCalculateIntent.SelectMarital(IllDaysMaritalStatus.Married))
                },
                modifier = Modifier.weight(1f),
            )
        }
        state.dayCount?.let { DayCountBadge(dayCount = it) }
    }
}

@Composable
private fun CalculationBreakdownCard(result: IllDaysCalcResultUi) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .taminSurface(cornerRadius = CornerRadius.cardCompact)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
    ) {
        DetailRow(
            label = stringResource(Res.string.ill_days_calc_rest_duration),
            value = result.restDaysLabel,
            numeric = false,
        )
        HorizontalDivider(color = colors.divider, thickness = Thickness.border)
        DetailRow(
            label = stringResource(Res.string.ill_days_calc_avg_wage),
            value = result.averageWageLabel,
            numeric = false,
        )
        HorizontalDivider(color = colors.divider, thickness = Thickness.border)
        DetailRow(
            label = result.rateTitle,
            value = result.rateCaption,
            numeric = false,
        )
    }
}

@Composable
private fun PayableResultCard(result: IllDaysCalcResultUi) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.cardCompact))
            .background(colors.buttonGradient)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Text(
            text = stringResource(Res.string.ill_days_calc_payable_label),
            style = MaterialTheme.typography.labelLarge,
            color = colors.blueText,
        )
        Text(
            text = result.payableAmountLabel,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
        )
        Text(
            text = stringResource(Res.string.ill_days_calc_payable_disclaimer),
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.9f),
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

@Composable
private fun MaritalOptionCard(
    title: String,
    caption: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val borderColor = if (selected) colors.blueText else colors.border
    val background = if (selected) colors.blueBg else colors.bgSurface
    val titleColor = if (selected) colors.blueText else colors.textPrimary
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(background)
            .border(
                width = Thickness.border,
                color = borderColor,
                shape = RoundedCornerShape(CornerRadius.lg),
            )
            .clickable(onClick = onClick)
            .padding(Spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = titleColor,
        )
        Text(
            text = caption,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) colors.blueText else colors.textMuted,
        )
    }
}
