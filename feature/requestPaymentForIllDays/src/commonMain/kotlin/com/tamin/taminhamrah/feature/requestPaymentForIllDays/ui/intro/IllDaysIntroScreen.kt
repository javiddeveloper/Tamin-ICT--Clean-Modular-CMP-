package com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.intro

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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysInsuredMainInfoPR
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.TaminBottomBar
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
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_calculator
import taminx.core.core_ui.ic_info
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ill_days_calc_estimate_row
import taminx.core.core_ui.ill_days_cd_back
import taminx.core.core_ui.ill_days_cd_calculator
import taminx.core.core_ui.ill_days_intro_tip
import taminx.core.core_ui.ill_days_label_full_name
import taminx.core.core_ui.ill_days_label_insurance_number
import taminx.core.core_ui.ill_days_label_insurance_type
import taminx.core.core_ui.ill_days_label_mobile
import taminx.core.core_ui.ill_days_label_national_id
import taminx.core.core_ui.ill_days_request_details_title
import taminx.core.core_ui.ill_days_start_request_btn
import taminx.core.core_ui.ill_days_title

@Composable
fun IllDaysIntroScreen(
    onBack: () -> Unit,
    onOpenCalculate: () -> Unit,
    onStartRequest: () -> Unit,
    viewModel: IllDaysIntroViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current

    HandleIllDaysIntroEvents(
        events = viewModel.events,
        onBack = onBack,
        onOpenCalculate = onOpenCalculate,
        onStartRequest = onStartRequest,
        onShowToast = { toaster.error(it) },
    )

    IllDaysIntroContent(
        state = state,
        onIntent = viewModel::sendIntent,
    )
}

@Composable
private fun HandleIllDaysIntroEvents(
    events: Flow<IllDaysIntroEvent>,
    onBack: () -> Unit,
    onOpenCalculate: () -> Unit,
    onStartRequest: () -> Unit,
    onShowToast: (String) -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            IllDaysIntroEvent.NavigateBack -> onBack()
            IllDaysIntroEvent.NavigateToCalculate -> onOpenCalculate()
            IllDaysIntroEvent.NavigateToWizard -> onStartRequest()
            is IllDaysIntroEvent.ShowToast -> onShowToast(event.message)
        }
    }
}

@Composable
private fun IllDaysIntroContent(
    state: IllDaysIntroUiState,
    onIntent: (IllDaysIntroIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    val headerBrush = Brush.horizontalGradient(colors.profileGradientStops)

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
                        onClick = { onIntent(IllDaysIntroIntent.Back) },
                        bordered = true,
                    )
                },
                action = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_calculator),
                        contentDescription = stringResource(Res.string.ill_days_cd_calculator),
                        onClick = { onIntent(IllDaysIntroIntent.OpenCalculate) },
                        bordered = true,
                    )
                },
            )
        },
        bottomBar = {
            if (!state.isLoading && state.insuredInfo != null) {
                TaminBottomBar(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .imePadding(),
                ) {
                    LoadingButton(
                        text = stringResource(Res.string.ill_days_start_request_btn),
                        onClick = { onIntent(IllDaysIntroIntent.StartRequest) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
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
            state.errorMessage != null && state.insuredInfo == null -> {
                ErrorStateView(
                    message = state.errorMessage,
                    onDismiss = { onIntent(IllDaysIntroIntent.Back) },
                    onRetry = { onIntent(IllDaysIntroIntent.Retry) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                )
            }
            else -> {
                val info = state.insuredInfo ?: return@Scaffold
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = Spacing.page, vertical = Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.lg),
                ) {
                    RequestDetailsCard(info = info)
                    IntroTipBox(text = stringResource(Res.string.ill_days_intro_tip))
                    CalculateEstimateRow(
                        onClick = { onIntent(IllDaysIntroIntent.OpenCalculate) },
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                }
            }
        }
    }
}

@Composable
private fun RequestDetailsCard(info: IllDaysInsuredMainInfoPR) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .taminSurface(cornerRadius = CornerRadius.cardCompact)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(
            text = stringResource(Res.string.ill_days_request_details_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
        )
        DetailRow(
            label = stringResource(Res.string.ill_days_label_full_name),
            value = info.fullName,
            numeric = false,
        )
        DetailRow(
            label = stringResource(Res.string.ill_days_label_insurance_number),
            value = info.risuid,
        )
        DetailRow(
            label = stringResource(Res.string.ill_days_label_national_id),
            value = info.nationalCode,
        )
        DetailRow(
            label = stringResource(Res.string.ill_days_label_insurance_type),
            value = info.insuranceTypeDesc,
            numeric = false,
        )
        DetailRow(
            label = stringResource(Res.string.ill_days_label_mobile),
            value = info.mobileNumber,
        )
    }
}

@Composable
private fun IntroTipBox(text: String) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.blueBg)
            .border(
                width = Thickness.border,
                color = colors.blueText.copy(alpha = 0.25f),
                shape = RoundedCornerShape(CornerRadius.lg),
            )
            .padding(Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_info),
            contentDescription = null,
            tint = colors.blueText,
            modifier = Modifier.size(IconSize.banner),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = colors.blueText,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun CalculateEstimateRow(onClick: () -> Unit) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.cardCompact))
            .background(colors.bgSurface)
            .border(
                width = Thickness.border,
                color = colors.border,
                shape = RoundedCornerShape(CornerRadius.cardCompact),
            )
            .clickable(onClick = onClick)
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(IconSize.badge)
                .clip(RoundedCornerShape(CornerRadius.md))
                .background(colors.blueBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_calculator),
                contentDescription = null,
                tint = colors.blueText,
                modifier = Modifier.size(IconSize.small),
            )
        }
        Text(
            text = stringResource(Res.string.ill_days_calc_estimate_row),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_forward),
            contentDescription = null,
            tint = colors.textMuted,
            modifier = Modifier.size(IconSize.small),
        )
    }
}
