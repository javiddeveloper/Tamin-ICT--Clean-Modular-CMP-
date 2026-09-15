package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.contract.BeneficiariesEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.contract.BeneficiariesIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.contract.BeneficiariesUiState
import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res as CoreRes
import taminx.core.core_ui.action_back
import taminx.core.core_ui.action_retry
import taminx.core.core_ui.beneficiaries_empty
import taminx.core.core_ui.beneficiaries_title
import taminx.core.core_ui.label_mobile
import taminx.core.core_ui.label_national_code
import taminx.core.core_ui.owner_type_applicant
import taminx.core.core_ui.owner_type_owner

@Composable
fun BeneficiariesRoute(
    viewModel: BeneficiariesViewModel,
    requestNumber: Long?,
    fileNumber: Long?,
    requestDate: String?,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(BeneficiariesIntent.Load(requestNumber, fileNumber, requestDate))
    }

    viewModel.events.collectWithLifecycleAware { event ->
        when (event) {
            BeneficiariesEvent.NavigateBack -> onBackClicked()
        }
    }

    BeneficiariesScreen(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onBackClicked = onBackClicked,
        modifier = modifier,
    )
}

@Composable
fun BeneficiariesScreen(
    state: BeneficiariesUiState,
    onIntent: (BeneficiariesIntent) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colors.bgPage,
        topBar = {
            TaminTopAppBar(
                title = stringResource(CoreRes.string.beneficiaries_title),
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
                state.error != null && state.items.isEmpty() -> BeneficiariesErrorState(
                    message = state.error,
                    onRetry = { onIntent(BeneficiariesIntent.Retry) },
                )

                state.items.isEmpty() && !state.isLoading -> TaminEmptyState(
                    message = stringResource(CoreRes.string.beneficiaries_empty),
                    modifier = Modifier.padding(top = Spacing.xxl),
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = Spacing.page, vertical = Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    items(items = state.items, key = { it.nationalCode ?: it.hashCode() }) { beneficiary ->
                        BeneficiaryCard(item = beneficiary)
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
private fun BeneficiaryCard(item: BeneficiaryConstructionPR, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .coloredShadow(color = colors.shadowSubtle, borderRadius = CornerRadius.card, blurRadius = 20.dp, offsetY = 8.dp)
            .taminSurface()
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = listOfNotNull(item.name, item.lastName).joinToString(" ").ifBlank { "-" },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            )
            StatusPill(
                text = stringResource(
                    if (item.isOwner) CoreRes.string.owner_type_owner else CoreRes.string.owner_type_applicant,
                ),
                containerColor = if (item.isOwner) colors.blueBg else colors.orangeBg,
                contentColor = if (item.isOwner) colors.blueText else colors.orangeText,
            )
        }
        DetailRow(label = stringResource(CoreRes.string.label_national_code), value = item.nationalCode ?: "-")
        DetailRow(label = stringResource(CoreRes.string.label_mobile), value = item.mobile ?: "-")
    }
}

@Composable
private fun BeneficiariesErrorState(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(Spacing.page),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Text(text = message, color = LocalTaminColors.current.dangerText)
        TaminOutlinedButton(text = stringResource(CoreRes.string.action_retry), onClick = onRetry)
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

private val PreviewBeneficiaries = kotlinx.collections.immutable.persistentListOf(
    BeneficiaryConstructionPR(
        nationalCode = "0930123451",
        ownerType = "01",
        name = "علی",
        lastName = "توکلی",
        mobile = "09123456701",
    ),
    BeneficiaryConstructionPR(
        nationalCode = "0930123452",
        ownerType = "02",
        name = "زهرا",
        lastName = "احمدی",
        mobile = "09123456702",
    ),
)

@PreviewRtlTheme
@Composable
private fun BeneficiariesScreenPreview() {
    PreviewRtlThemeContent {
        BeneficiariesScreen(
            state = BeneficiariesUiState(items = PreviewBeneficiaries),
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun BeneficiariesScreenEmptyPreview() {
    PreviewRtlThemeContent {
        BeneficiariesScreen(
            state = BeneficiariesUiState(),
            onIntent = {},
            onBackClicked = {},
        )
    }
}
