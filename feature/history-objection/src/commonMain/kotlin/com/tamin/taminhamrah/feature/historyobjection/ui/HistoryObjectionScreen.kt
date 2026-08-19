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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
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
import com.tamin.taminhamrah.model.historyObjection.NotExistRequestPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.GlassIconTile
import com.tamin.taminhamrah.ui.components.IconBox
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.SectionLabel
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.dashedOutline
import com.tamin.taminhamrah.ui.components.rememberCopyAction
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.components.taminTopAppBarGradient
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import kotlinx.collections.immutable.persistentListOf
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
import taminx.core.core_ui.history_objection_delete
import taminx.core.core_ui.history_objection_edit
import taminx.core.core_ui.history_objection_empty_subtitle
import taminx.core.core_ui.history_objection_empty_title
import taminx.core.core_ui.history_objection_end_date
import taminx.core.core_ui.history_objection_insurance_number
import taminx.core.core_ui.history_objection_list_title
import taminx.core.core_ui.history_objection_start_date
import taminx.core.core_ui.history_objection_status_not_sent
import taminx.core.core_ui.history_objection_title
import taminx.core.core_ui.history_objection_workshop_code
import taminx.core.core_ui.history_objection_workshop_name
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_copy
import taminx.core.core_ui.ic_trash
import taminx.core.core_ui.send_history_access_denied_action
import taminx.feature.history_objection.generated.resources.ic_history_objection
import taminx.feature.history_objection.generated.resources.Res as FeatureRes

private val EmptyStateIconSize = 64.dp
private val AddButtonHeight = 50.dp
private val AddButtonIconSize = 17.dp
private val AddButtonBorderColor = Color(0xFFB9CBEF)
private val WorkshopCodeIconSize = 14.dp
private const val WorkshopCodeOutlineAlpha = 0.2f

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
            // Skeleton only — no edit/delete endpoint exists yet (docs/vault/History-Objection.md).
            is HistoryObjectionEvent.NavigateToEditNotExistRequest -> {}
            is HistoryObjectionEvent.ConfirmDeleteNotExistRequest -> {}
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
            if (state.notExistRequests.isEmpty()) {
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
            } else {
                SectionLabel(
                    text = stringResource(Res.string.history_objection_list_title),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
                state.notExistRequests.forEach { request ->
                    NotExistRequestCard(
                        request = request,
                        onEditClick = {
                            onIntent(HistoryObjectionIntent.OnEditNotExistRequestClicked(request.requestNumber))
                        },
                        onDeleteClick = {
                            onIntent(HistoryObjectionIntent.OnDeleteNotExistRequestClicked(request.requestNumber))
                        },
                    )
                    Spacer(modifier = Modifier.height(Spacing.md))
                }
                Spacer(modifier = Modifier.height(Spacing.sm))
            }
            AddNewObjectionButton(
                onClick = { onIntent(HistoryObjectionIntent.OnAddNewObjectionClicked) },
            )
        }
    }
}

@Composable
private fun NotExistRequestCard(
    request: NotExistRequestPR,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface()
            .padding(Spacing.lg),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column {
                Text(
                    text = request.branchName,
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.textPrimary,
                )
                Spacer(modifier = Modifier.height(Spacing.xxs))
                Text(
                    text = request.insuranceTypeDesc,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                )
            }
            // Fixed by design for now — not derived from `confirmed`, see docs/vault/History-Objection.md.
            StatusPill(
                text = stringResource(Res.string.history_objection_status_not_sent),
                containerColor = colors.orangeBg,
                contentColor = colors.orangeText,
            )
        }
        Spacer(modifier = Modifier.height(Spacing.md))
        TaminDivider()
        val workshopCode = request.workshopCode
        if (workshopCode != null) {
            WorkshopCodeRow(
                label = stringResource(Res.string.history_objection_workshop_code),
                code = workshopCode,
                modifier = Modifier.padding(vertical = Spacing.xs),
            )
        }
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.history_objection_workshop_name),
            value = request.workshopName,
            numeric = false,
            modifier = Modifier.padding(vertical = Spacing.xs),
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.history_objection_insurance_number),
            value = request.insuranceNumber,
            numeric = true,
            modifier = Modifier.padding(vertical = Spacing.xs),
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.history_objection_start_date),
            value = request.startDateLabel,
            numeric = true,
            modifier = Modifier.padding(vertical = Spacing.xs),
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.history_objection_end_date),
            value = request.endDateLabel,
            numeric = true,
            modifier = Modifier.padding(vertical = Spacing.xs),
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            TaminOutlinedButton(
                text = stringResource(Res.string.history_objection_delete),
                onClick = onDeleteClick,
                icon = vectorResource(Res.drawable.ic_trash),
                modifier = Modifier.weight(1f),
                containerColor = colors.dangerBorder,
                contentColor = colors.dangerText,
                borderColor = colors.dangerBorder,
            )
            TaminOutlinedButton(
                text = stringResource(Res.string.history_objection_edit),
                onClick = onEditClick,
                icon = Icons.Default.Edit,
                modifier = Modifier.weight(1f),
                containerColor = colors.blueBg,
                contentColor = colors.blueText,
                borderColor = Color.Transparent,
            )
        }
    }
}

/** The workshop code: a label above a dashed-outline chip that copies its value on tap. */
/** Label on the right, a copyable dashed-outline code chip flush to the opposite end. */
@Composable
private fun WorkshopCodeRow(
    label: String,
    code: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val copy = rememberCopyAction(code)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = colors.textMuted)
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(CornerRadius.lg))
                .background(colors.blueBg)
                .dashedOutline(colors.blueText.copy(alpha = WorkshopCodeOutlineAlpha), CornerRadius.lg, Thickness.medium)
                .clickable(onClick = copy)
                .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_copy),
                contentDescription = null,
                tint = colors.blueText,
                modifier = Modifier.size(WorkshopCodeIconSize),
            )
            NumericText(
                text = code,
                style = MaterialTheme.typography.titleSmall,
                color = colors.blueText,
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

@PreviewRtlTheme
@Composable
private fun HistoryObjectionScreenWithListPreview() {
    PreviewRtlThemeContent {
        HistoryObjectionContent(
            state = HistoryObjectionUiState(
                notExistRequests = persistentListOf(
                    NotExistRequestPR(
                        requestNumber = "1837710",
                        branchName = "هفت مشهد، توس",
                        insuranceTypeDesc = "بیمهٔ اجباری (کارگری)",
                        workshopName = "111",
                        insuranceNumber = "0081631829",
                        workshopCode = "1111111111",
                        startDateLabel = "۱۳۹۴/۰۵/۲۸",
                        endDateLabel = "۱۴۰۰/۰۵/۲۸",
                    ),
                ),
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = {},
            onNavigateBack = {},
        )
    }
}
