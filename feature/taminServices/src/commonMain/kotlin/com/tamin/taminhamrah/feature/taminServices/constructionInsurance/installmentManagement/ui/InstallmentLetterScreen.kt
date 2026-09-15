package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.contract.InstallmentLetterEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.contract.InstallmentLetterIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.contract.InstallmentLetterUiState
import com.tamin.taminhamrah.model.constructionInsurance.InstallmentLetterPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res as CoreRes
import taminx.core.core_ui.action_back
import taminx.core.core_ui.action_retry
import taminx.core.core_ui.installment_letter_empty
import taminx.core.core_ui.installment_letter_title
import taminx.core.core_ui.label_debit_end_date
import taminx.core.core_ui.label_debit_number
import taminx.core.core_ui.label_debit_start_date
import taminx.core.core_ui.label_debit_status
import taminx.core.core_ui.label_debit_step
import taminx.core.core_ui.label_remaining_amount

private const val RIAL_UNIT = "ریال"

@Composable
fun InstallmentLetterRoute(
    viewModel: InstallmentLetterViewModel,
    workshopId: String,
    branchId: String,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(InstallmentLetterIntent.Load(workshopId, branchId))
    }

    viewModel.events.collectWithLifecycleAware { event ->
        when (event) {
            InstallmentLetterEvent.NavigateBack -> onBackClicked()
        }
    }

    InstallmentLetterScreen(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onBackClicked = onBackClicked,
        modifier = modifier,
    )
}

@Composable
fun InstallmentLetterScreen(
    state: InstallmentLetterUiState,
    onIntent: (InstallmentLetterIntent) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.bgPage,
        topBar = {
            TaminTopAppBar(
                title = stringResource(CoreRes.string.installment_letter_title),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(CoreRes.string.action_back),
                        onClick = onBackClicked,
                        bordered = true,
                    )
                },
            )
        },
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                state.error != null && state.items.isEmpty() -> InstallmentLetterErrorState(
                    message = state.error,
                    onRetry = { onIntent(InstallmentLetterIntent.Retry) },
                )

                state.items.isEmpty() && !state.isLoading -> TaminEmptyState(
                    message = stringResource(CoreRes.string.installment_letter_empty),
                    modifier = Modifier.padding(top = Spacing.xxl),
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = Spacing.page, vertical = Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    items(items = state.items, key = { it.debitNumber ?: it.hashCode() }) { letter ->
                        InstallmentLetterCard(item = letter)
                    }
                }
            }

            if (state.isLoading) {
                LoadingStateOverlay()
            }
        }
    }
}

@Composable
private fun InstallmentLetterCard(item: InstallmentLetterPR, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .coloredShadow(color = colors.shadowSubtle, borderRadius = CornerRadius.card, blurRadius = 20.dp, offsetY = 8.dp)
            .taminSurface()
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = item.debitStepDescription ?: "-",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
        )
        DetailRow(label = stringResource(CoreRes.string.label_debit_number), value = item.debitNumber ?: "-")
        DetailRow(
            label = stringResource(CoreRes.string.label_debit_status),
            value = item.debitStatusDescription ?: "-",
            numeric = false,
        )
        DetailRow(label = stringResource(CoreRes.string.label_debit_start_date), value = item.debitStartDate ?: "-")
        DetailRow(label = stringResource(CoreRes.string.label_debit_end_date), value = item.debitEndDate ?: "-")
        DetailRow(
            label = stringResource(CoreRes.string.label_remaining_amount),
            value = (item.remainingAmount ?: 0L).toPriceFormat(),
            unit = RIAL_UNIT,
            valueColor = colors.blueText,
        )
    }
}

@Composable
private fun InstallmentLetterErrorState(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(Spacing.page),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Text(text = message, color = LocalTaminColors.current.dangerText)
        TaminOutlinedButton(text = stringResource(CoreRes.string.action_retry), onClick = onRetry)
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

private val PreviewLetters = kotlinx.collections.immutable.persistentListOf(
    InstallmentLetterPR(
        workshopId = "9028222442",
        debitNumber = "7764000001",
        debitStepDescription = "قسط اول",
        debitStatusDescription = "پرداخت شده",
        debitStartDate = "14021001",
        debitEndDate = "14031001",
        remainingAmount = 2_000_000L,
    ),
    InstallmentLetterPR(
        workshopId = "9028222442",
        debitNumber = "7764000002",
        debitStepDescription = "قسط دوم",
        debitStatusDescription = "سررسید نشده",
        debitStartDate = "14031001",
        debitEndDate = "14041001",
        remainingAmount = 1_600_000L,
    ),
)

@PreviewRtlTheme
@Composable
private fun InstallmentLetterScreenPreview() {
    PreviewRtlThemeContent {
        InstallmentLetterScreen(
            state = InstallmentLetterUiState(items = PreviewLetters),
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun InstallmentLetterScreenEmptyPreview() {
    PreviewRtlThemeContent {
        InstallmentLetterScreen(
            state = InstallmentLetterUiState(),
            onIntent = {},
            onBackClicked = {},
        )
    }
}
