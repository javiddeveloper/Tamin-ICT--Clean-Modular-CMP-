package com.tamin.taminhamrah.feature.developerOptions.tokens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.developerOptions.tokens.contract.TokenCardUi
import com.tamin.taminhamrah.feature.developerOptions.tokens.contract.TokenManagerEvent
import com.tamin.taminhamrah.feature.developerOptions.tokens.contract.TokenManagerIntent
import com.tamin.taminhamrah.feature.developerOptions.tokens.contract.TokenManagerUiState
import com.tamin.taminhamrah.model.auth.TokenSlot
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.token_manager_activate
import taminx.core.core_ui.token_manager_active
import taminx.core.core_ui.token_manager_agent_hint
import taminx.core.core_ui.token_manager_back_to_back_hint
import taminx.core.core_ui.token_manager_clear
import taminx.core.core_ui.token_manager_empty
import taminx.core.core_ui.token_manager_expired
import taminx.core.core_ui.token_manager_expires_in
import taminx.core.core_ui.token_manager_expiry_unknown
import taminx.core.core_ui.token_manager_refresh
import taminx.core.core_ui.token_manager_slot_agent
import taminx.core.core_ui.token_manager_slot_back_to_back
import taminx.core.core_ui.token_manager_slot_user
import taminx.core.core_ui.token_manager_title
import kotlin.time.Duration

@Composable
fun TokenManagerScreen(
    viewModel: TokenManagerViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    viewModel.events.collectWithLifecycleAware { event: TokenManagerEvent ->
        when (event) {
            TokenManagerEvent.NavigateBack -> onNavigateBack()
        }
    }

    TokenManagerContent(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
private fun TokenManagerContent(
    state: TokenManagerUiState,
    onIntent: (TokenManagerIntent) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val taminColors = LocalTaminColors.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.token_manager_title),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = null,
                        onClick = onNavigateBack,
                        bordered = true,
                    )
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    top = innerPadding.calculateTopPadding() + Spacing.lg,
                    bottom = innerPadding.calculateBottomPadding() + Spacing.xxl,
                )
                .padding(horizontal = Spacing.page),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            state.cards.forEach { card ->
                TokenCard(
                    card = card,
                    isBusy = state.busySlot == card.slot,
                    isAnyBusy = state.busySlot != null,
                    onIntent = onIntent,
                )
            }

            state.statusText?.let { statusText ->
                SelectionContainer {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                        color = taminColors.textSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun TokenCard(
    card: TokenCardUi,
    isBusy: Boolean,
    isAnyBusy: Boolean,
    onIntent: (TokenManagerIntent) -> Unit,
) {
    val taminColors = LocalTaminColors.current
    val hasToken = !card.token.isNullOrBlank()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(taminColors.bgSurface, RoundedCornerShape(CornerRadius.lg))
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = stringResource(card.slot.titleRes()),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = taminColors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            if (card.isActive) {
                Text(
                    text = stringResource(Res.string.token_manager_active),
                    style = MaterialTheme.typography.labelSmall,
                    color = taminColors.blueText,
                    modifier = Modifier
                        .background(taminColors.blueBg, RoundedCornerShape(CornerRadius.chip))
                        .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                )
            }
        }

        Text(
            text = card.expiryLabel(),
            style = MaterialTheme.typography.bodySmall,
            color = if (card.expiry is JwtExpiry.Expired) taminColors.dangerText else taminColors.textSecondary,
        )

        SelectionContainer {
            Text(
                text = card.token?.takeIf { it.isNotBlank() }
                    ?: stringResource(Res.string.token_manager_empty),
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = taminColors.textMuted,
                maxLines = 3,
            )
        }

        when (card.slot) {
            TokenSlot.AGENT -> CardHint(stringResource(Res.string.token_manager_agent_hint))
            TokenSlot.BACK_TO_BACK -> CardHint(stringResource(Res.string.token_manager_back_to_back_hint))
            TokenSlot.USER -> Unit
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            if (card.slot != TokenSlot.AGENT && !card.isActive) {
                TaminFilledButton(
                    text = stringResource(Res.string.token_manager_activate),
                    onClick = { onIntent(TokenManagerIntent.OnActivateClicked(card.slot)) },
                    enabled = hasToken && !isAnyBusy,
                    modifier = Modifier.weight(1f),
                    height = 44.dp,
                )
            }
            TaminOutlinedButton(
                text = stringResource(Res.string.token_manager_refresh),
                onClick = { onIntent(TokenManagerIntent.OnRefreshClicked(card.slot)) },
                enabled = !isAnyBusy || isBusy,
                modifier = Modifier.weight(1f),
                height = 44.dp,
            )
            TaminOutlinedButton(
                text = stringResource(Res.string.token_manager_clear),
                onClick = { onIntent(TokenManagerIntent.OnClearClicked(card.slot)) },
                enabled = hasToken && !isAnyBusy,
                modifier = Modifier.weight(1f),
                height = 44.dp,
            )
        }
    }
}

@Composable
private fun CardHint(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = LocalTaminColors.current.textMuted,
    )
}

private fun TokenSlot.titleRes() = when (this) {
    TokenSlot.USER -> Res.string.token_manager_slot_user
    TokenSlot.BACK_TO_BACK -> Res.string.token_manager_slot_back_to_back
    TokenSlot.AGENT -> Res.string.token_manager_slot_agent
}

@Composable
private fun TokenCardUi.expiryLabel(): String = when (val expiry = expiry) {
    JwtExpiry.Unknown -> stringResource(Res.string.token_manager_expiry_unknown)
    is JwtExpiry.Valid ->
        stringResource(Res.string.token_manager_expires_in, expiry.remaining.wholeMinutesLabel())

    is JwtExpiry.Expired ->
        stringResource(Res.string.token_manager_expired, expiry.since.wholeMinutesLabel())
}

/** Rounded up, so a token with 20 seconds left reads «۱ دقیقه» rather than «۰ دقیقه». */
private fun Duration.wholeMinutesLabel(): String {
    val minutes = (inWholeSeconds + 59) / 60
    return minutes.toString().toPersianDigits()
}
