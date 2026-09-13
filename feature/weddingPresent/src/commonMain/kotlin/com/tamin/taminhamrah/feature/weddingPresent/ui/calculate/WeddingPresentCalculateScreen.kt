package com.tamin.taminhamrah.feature.weddingPresent.ui.calculate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.PickerRow
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.HeaderDecoration
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_calendar
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.wedding_present_calc_avg_salary
import taminx.core.core_ui.wedding_present_calc_payable_disclaimer
import taminx.core.core_ui.wedding_present_calc_payable_label
import taminx.core.core_ui.wedding_present_calc_submit
import taminx.core.core_ui.wedding_present_calc_subtitle
import taminx.core.core_ui.wedding_present_calc_title
import taminx.core.core_ui.wedding_present_cd_back
import taminx.core.core_ui.wedding_present_marriage_date
import taminx.core.core_ui.wedding_present_marriage_date_placeholder

@Composable
fun WeddingPresentCalculateScreen(
    onBack: () -> Unit,
    viewModel: WeddingPresentCalculateViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current

    HandleEvents(
        events = viewModel.events,
        onBack = onBack,
        onShowToast = { toaster.error(it) },
    )

    WeddingPresentCalculateContent(
        state = state,
        onIntent = viewModel::sendIntent,
    )
}

@Composable
private fun HandleEvents(
    events: Flow<WeddingPresentCalculateEvent>,
    onBack: () -> Unit,
    onShowToast: (String) -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            WeddingPresentCalculateEvent.NavigateBack -> onBack()
            is WeddingPresentCalculateEvent.ShowToast -> onShowToast(event.message)
            is WeddingPresentCalculateEvent.ShowToastRes -> onShowToast(getString(event.message))
        }
    }
}

@Composable
private fun WeddingPresentCalculateContent(
    state: WeddingPresentCalculateUiState,
    onIntent: (WeddingPresentCalculateIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    val headerBrush = remember(colors.profileGradientStops) {
        Brush.horizontalGradient(colors.profileGradientStops)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.bgPage,
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.wedding_present_calc_title),
                background = headerBrush,
                shape = RoundedCornerShape(
                    bottomStart = CornerRadius.x3l,
                    bottomEnd = CornerRadius.x3l,
                ),
                bottomPadding = Spacing.smPlus,
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = stringResource(Res.string.wedding_present_cd_back),
                        onClick = { onIntent(WeddingPresentCalculateIntent.Back) },
                        bordered = true,
                    )
                },
            ) {
                DecorativeBackgroundCircle(
                    size = HeaderDecoration.circleSize,
                    xOffset = HeaderDecoration.circleXOffset,
                    yOffset = HeaderDecoration.circleYOffset,
                )
                AnimatedRingHeaderIcon(
                    icon = Icons.Filled.CardGiftcard,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text(
                    text = stringResource(Res.string.wedding_present_calc_subtitle),
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.textHeaderSubtitle,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
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
            CalculateInputCard(state = state, onIntent = onIntent)
            state.result?.let { result ->
                CalculationBreakdownCard(result = result)
                PayableResultCard(result = result)
            }
        }
    }

    if (state.showDatePicker) {
        TaminJalaliDatePicker(
            title = stringResource(Res.string.wedding_present_marriage_date),
            onDismiss = { onIntent(WeddingPresentCalculateIntent.DismissDatePicker) },
            onConfirm = { year, month, day ->
                onIntent(
                    WeddingPresentCalculateIntent.MarriageDatePicked(
                        millis = PersianDateFormatter.toEpochMillis(year, month, day),
                        label = PersianDateFormatter.format(year, month, day),
                    ),
                )
            },
        )
    }
}

@Composable
private fun CalculateInputCard(
    state: WeddingPresentCalculateUiState,
    onIntent: (WeddingPresentCalculateIntent) -> Unit,
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
            text = stringResource(Res.string.wedding_present_marriage_date),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
        )
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            PickerRow(
                text = state.marriageDateLabel.ifBlank {
                    stringResource(Res.string.wedding_present_marriage_date_placeholder)
                },
                onClick = { onIntent(WeddingPresentCalculateIntent.OpenDatePicker) },
                isPlaceholder = state.marriageDateLabel.isBlank(),
                icon = vectorResource(Res.drawable.ic_tamin_calendar),
                modifier = Modifier.fillMaxWidth(),
            )
            state.marriageDateError?.let { error ->
                Text(
                    text = stringResource(error),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.dangerText,
                )
            }
        }
        LoadingButton(
            text = stringResource(Res.string.wedding_present_calc_submit),
            onClick = { onIntent(WeddingPresentCalculateIntent.Calculate) },
            isLoading = state.isCalculating,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun CalculationBreakdownCard(result: WeddingPresentCalcResultUi) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .taminSurface(cornerRadius = CornerRadius.cardCompact)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
    ) {
        DetailRow(
            label = stringResource(Res.string.wedding_present_marriage_date),
            value = result.marriageDateLabel,
            numeric = false,
        )
        HorizontalDivider(color = colors.divider, thickness = Thickness.border)
        DetailRow(
            label = stringResource(Res.string.wedding_present_calc_avg_salary),
            value = result.averageSalaryLabel,
            numeric = false,
        )
    }
}

@Composable
private fun PayableResultCard(result: WeddingPresentCalcResultUi) {
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
            text = stringResource(Res.string.wedding_present_calc_payable_label),
            style = MaterialTheme.typography.labelLarge,
            color = colors.blueText,
        )
        Text(
            text = result.payableAmountLabel,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = colors.onGradient,
        )
        Text(
            text = stringResource(Res.string.wedding_present_calc_payable_disclaimer),
            style = MaterialTheme.typography.bodySmall,
            color = colors.onGradient.copy(alpha = 0.9f),
        )
    }
}
