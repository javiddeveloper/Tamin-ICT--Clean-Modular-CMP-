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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.PickerRow
import com.tamin.taminhamrah.ui.components.TaminBottomBar
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_calendar
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ill_days_calc_end_date
import taminx.core.core_ui.ill_days_calc_marital_married
import taminx.core.core_ui.ill_days_calc_marital_married_caption
import taminx.core.core_ui.ill_days_calc_marital_single
import taminx.core.core_ui.ill_days_calc_marital_single_caption
import taminx.core.core_ui.ill_days_calc_result_confirm
import taminx.core.core_ui.ill_days_calc_result_empty
import taminx.core.core_ui.ill_days_calc_result_title
import taminx.core.core_ui.ill_days_calc_section_title
import taminx.core.core_ui.ill_days_calc_start_date
import taminx.core.core_ui.ill_days_calc_submit_btn
import taminx.core.core_ui.ill_days_calc_title
import taminx.core.core_ui.ill_days_cd_back

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
        ) {
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

    if (state.showResultDialog) {
        val description = state.resultLines
            .filter { it.isNotBlank() }
            .joinToString("\n")
            .ifBlank { stringResource(Res.string.ill_days_calc_result_empty) }
        TaminConfirmationDialog(
            title = stringResource(Res.string.ill_days_calc_result_title),
            description = description,
            onDismissRequest = { onIntent(IllDaysCalculateIntent.DismissResult) },
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(Res.string.ill_days_calc_result_confirm),
                    onClick = { onIntent(IllDaysCalculateIntent.DismissResult) },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            dismissButton = {},
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
            color = colors.textPrimary,
        )
        Text(
            text = caption,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
        )
    }
}
