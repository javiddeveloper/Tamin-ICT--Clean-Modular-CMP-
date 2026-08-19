package com.tamin.taminhamrah.feature.historyobjection.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius as CanvasCornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionEvent
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionIntent
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionUiState
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.GlassIconTile
import com.tamin.taminhamrah.ui.components.IconBox
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminTopAppBarGradient
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_back
import taminx.core.core_ui.history_objection_active_request_message
import taminx.core.core_ui.history_objection_active_request_title
import taminx.core.core_ui.history_objection_add_new
import taminx.core.core_ui.history_objection_empty_subtitle
import taminx.core.core_ui.history_objection_empty_title
import taminx.core.core_ui.history_objection_title
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.send_history_access_denied_action
import taminx.feature.history_objection.generated.resources.ic_history_objection
import taminx.feature.history_objection.generated.resources.Res as FeatureRes

private val EmptyStateIconSize = 64.dp
private val AddButtonHeight = 50.dp
private val AddButtonIconSize = 17.dp
private val AddButtonBorderColor = Color(0xFFB9CBEF)

@Composable
fun HistoryObjectionScreen(
    viewModel: HistoryObjectionViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    HandleHistoryObjectionEvents(
        events = viewModel.events,
        snackbarHostState = snackbarHostState,
        onNavigateBack = onNavigateBack,
    )

    HistoryObjectionContent(
        state = uiState,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::sendIntent,
        onNavigateBack = onNavigateBack,
    )

    if (uiState.showActiveRequestDialog) {
        ActiveRequestDialog(
            onDismiss = {
                viewModel.sendIntent(HistoryObjectionIntent.OnActiveRequestDialogDismissed)
                onNavigateBack()
            },
        )
    }
}

@Composable
private fun ActiveRequestDialog(onDismiss: () -> Unit) {
    TaminConfirmationDialog(
        title = stringResource(Res.string.history_objection_active_request_title),
        description = stringResource(Res.string.history_objection_active_request_message),
        confirmButton = {
            TaminFilledButton(
                text = stringResource(Res.string.send_history_access_denied_action),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        dismissButton = {},
        onDismissRequest = onDismiss,
        icon = Icons.Default.Info,
    )
}

@Composable
private fun HandleHistoryObjectionEvents(
    events: Flow<HistoryObjectionEvent>,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            HistoryObjectionEvent.NavigateToAddNewObjection -> {}
        }
    }
}

@Composable
private fun HistoryObjectionContent(
    state: HistoryObjectionUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (HistoryObjectionIntent) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.history_objection_title),
                background = taminTopAppBarGradient(colors.profileGradientStops),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = stringResource(Res.string.action_back),
                        onClick = onNavigateBack,
                        bordered = true,
                    )
                },
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    DecorativeBackgroundCircle(
                        size = 190.dp,
                        xOffset = (-50).dp,
                        yOffset = (-70).dp,
                    )
                    GlassIconTile(
                        icon = vectorResource(FeatureRes.drawable.ic_history_objection),
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = Spacing.lg),
                    )
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.bgPage)
                .verticalScroll(rememberScrollState())
                .padding(
                    top = innerPadding.calculateTopPadding() + Spacing.xl,
                    bottom = innerPadding.calculateBottomPadding() + Spacing.xxl,
                    start = Spacing.page,
                    end = Spacing.page,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IconBox(
                painter = painterResource(FeatureRes.drawable.ic_history_objection),
                backgroundColor = colors.chipBg,
                cornerRadius = CornerRadius.card,
                size = EmptyStateIconSize,
            )
            Spacer(modifier = Modifier.height(Spacing.lg))
            Text(
                text = stringResource(Res.string.history_objection_empty_title),
                style = MaterialTheme.typography.labelLarge,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = stringResource(Res.string.history_objection_empty_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(Spacing.xl))
            AddNewObjectionButton(
                onClick = { onIntent(HistoryObjectionIntent.OnAddNewObjectionClicked) },
            )
        }
    }
}

/**
 * The design's dashed-outline CTA — no other screen in the app uses a dashed border today, so
 * this draws its own stroke rather than pulling a one-off shape into core-ui for a single caller.
 */
@Composable
private fun AddNewObjectionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.xl)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(AddButtonHeight)
            .clip(shape)
            .background(colors.bgSurface)
            .drawBehind {
                drawRoundRect(
                    color = AddButtonBorderColor,
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 5f), 0f),
                    ),
                    cornerRadius = CanvasCornerRadius(CornerRadius.xl.toPx()),
                )
            }
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = colors.blueText,
            modifier = Modifier.size(AddButtonIconSize),
        )
        Spacer(modifier = Modifier.width(Spacing.sm))
        Text(
            text = stringResource(Res.string.history_objection_add_new),
            style = MaterialTheme.typography.labelLarge,
            color = colors.blueText,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun HistoryObjectionScreenPreview() {
    PreviewRtlThemeContent {
        HistoryObjectionContent(
            state = HistoryObjectionUiState(),
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = {},
            onNavigateBack = {},
        )
    }
}
