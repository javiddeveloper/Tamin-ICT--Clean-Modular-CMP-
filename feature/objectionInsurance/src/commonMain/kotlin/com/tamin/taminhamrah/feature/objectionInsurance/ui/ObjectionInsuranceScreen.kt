package com.tamin.taminhamrah.feature.objectionInsurance.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.objectionInsurance.ui.components.ObjectionYearGrid
import com.tamin.taminhamrah.feature.objectionInsurance.ui.components.ObjectionYearGridSkeleton
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.ObjectionInsuranceEvent
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.ObjectionInsuranceIntent
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.ObjectionInsuranceUiState
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.StagedYearChipPR
import com.tamin.taminhamrah.feature.objectionInsurance.ui.preview.previewLoadedRecords
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahShapes
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_back
import taminx.core.core_ui.action_cancel
import taminx.core.core_ui.description_label
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.objection_insurance_ack_button
import taminx.core.core_ui.objection_insurance_active_request_message
import taminx.core.core_ui.objection_insurance_active_request_title
import taminx.core.core_ui.objection_insurance_banner
import taminx.core.core_ui.objection_insurance_description_placeholder
import taminx.core.core_ui.objection_insurance_empty_hint
import taminx.core.core_ui.objection_insurance_help_message
import taminx.core.core_ui.objection_insurance_help_title
import taminx.core.core_ui.objection_insurance_staged_section_title
import taminx.core.core_ui.objection_insurance_submit_button
import taminx.core.core_ui.objection_insurance_submit_confirmation_message
import taminx.core.core_ui.objection_insurance_submit_confirmation_title
import taminx.core.core_ui.objection_insurance_success_title
import taminx.core.core_ui.objection_insurance_success_tracking_label
import taminx.core.core_ui.objection_insurance_title
import taminx.core.core_ui.objection_insurance_years_count
import taminx.core.core_ui.objection_insurance_years_section_title

private val DescriptionFieldHeight = 96.dp

@Composable
fun ObjectionInsuranceScreen(
    viewModel: ObjectionInsuranceViewModel = koinViewModel(),
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(ObjectionInsuranceIntent.Load)
    }

    HandleObjectionInsuranceEvents(events = viewModel.events, onBack = onBack)

    val detailRecord = uiState.detailRecord
    val workshopPickerYear = uiState.workshopPickerYear
    when {
        detailRecord != null -> ObjectionRecordDetailScreen(
            record = detailRecord,
            seasonGroups = uiState.detailSeasonGroups,
            chartBars = uiState.detailChartBars,
            delta = uiState.detailDelta,
            showValidationError = uiState.detailShowValidationError,
            hasDraft = uiState.detailDraft.isNotEmpty(),
            onMonthValueChanged = { month, value ->
                viewModel.sendIntent(
                    ObjectionInsuranceIntent.OnMonthValueChanged(
                        month,
                        value
                    )
                )
            },
            onClearDraftClicked = { viewModel.sendIntent(ObjectionInsuranceIntent.OnDetailResetClicked) },
            onSaveClicked = { viewModel.sendIntent(ObjectionInsuranceIntent.OnDetailConfirmClicked) },
            onBack = { viewModel.sendIntent(ObjectionInsuranceIntent.OnDetailSheetDismissed) },
        )

        workshopPickerYear != null -> ObjectionWorkshopPickerScreen(
            year = workshopPickerYear,
            rows = uiState.workshopPickerRows,
            onRowClicked = { recordIndex ->
                viewModel.sendIntent(
                    ObjectionInsuranceIntent.OnWorkshopPicked(
                        recordIndex
                    )
                )
            },
            onBack = { viewModel.sendIntent(ObjectionInsuranceIntent.OnWorkshopPickerDismissed) },
        )

        else -> ObjectionInsuranceContent(
            state = uiState,
            onIntent = viewModel::sendIntent,
            onBack = onBack,
        )
    }

    if (uiState.showActiveRequestDialog) {
        ActiveRequestDialog(
            onDismiss = { viewModel.sendIntent(ObjectionInsuranceIntent.OnActiveRequestDialogDismissed) },
        )
    }

    if (uiState.showHelpDialog) {
        HelpDialog(onDismiss = { viewModel.sendIntent(ObjectionInsuranceIntent.OnHelpDismissed) })
    }

    if (uiState.showSubmitConfirmationDialog) {
        SubmitConfirmationDialog(
            onConfirm = { viewModel.sendIntent(ObjectionInsuranceIntent.OnSubmitConfirmed) },
            onDismiss = { viewModel.sendIntent(ObjectionInsuranceIntent.OnSubmitConfirmationDismissed) },
        )
    }

    uiState.trackingNumber?.let { trackingNumber ->
        TrackingNumberDialog(
            trackingNumber = trackingNumber,
            onAcknowledge = { viewModel.sendIntent(ObjectionInsuranceIntent.OnTrackingNumberAcknowledged) },
        )
    }

    ErrorStateView(
        message = uiState.error,
        onDismiss = { viewModel.sendIntent(ObjectionInsuranceIntent.OnErrorDismissed) },
    )
}

@Composable
private fun HandleObjectionInsuranceEvents(
    events: Flow<ObjectionInsuranceEvent>,
    onBack: () -> Unit
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            ObjectionInsuranceEvent.NavigateBack -> onBack()
        }
    }
}

@Composable
private fun ObjectionInsuranceContent(
    state: ObjectionInsuranceUiState,
    onIntent: (ObjectionInsuranceIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.objection_insurance_title),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = stringResource(Res.string.action_back),
                        onClick = onBack,
                        bordered = true,
                    )
                },
                action = {
                    TaminTopAppBarButton(
                        icon = Icons.Outlined.Info,
                        contentDescription = stringResource(Res.string.objection_insurance_help_title),
                        onClick = { onIntent(ObjectionInsuranceIntent.OnHelpClicked) },
                        bordered = true,
                    )
                },
            ) {
                ObjectionInsuranceBanner()
            }
        },
        bottomBar = {
            if (!state.isLoading && !state.hasActiveRequest) {
                SubmitButton(onClick = { onIntent(ObjectionInsuranceIntent.OnSubmitClicked) })
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.bgPage)
                .verticalScroll(rememberScrollState())
                .padding(
                    top = innerPadding.calculateTopPadding() + Spacing.md,
                    bottom = innerPadding.calculateBottomPadding() + Spacing.lg,
                    start = Spacing.page,
                    end = Spacing.page,
                ),
        ) {
            if (state.isLoading) {
                ObjectionYearGridSkeleton()
            } else if (!state.hasActiveRequest) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    SectionTitle(
                        stringResource(
                            Res.string.objection_insurance_years_section_title
                        )
                    )
                    StatusPill(
                        text = stringResource(
                            Res.string.objection_insurance_years_count,
                            state.yearCards.size.toString().toPersianDigits(),
                        ),
                        containerColor = colors.blueBg,
                        contentColor = colors.blueText,
                    )
                }
                Spacer(modifier = Modifier.height(Spacing.sm))
                ObjectionYearGrid(
                    cards = state.yearCards,
                    onCardClick = { card -> onIntent(ObjectionInsuranceIntent.OnYearCardClicked(card.recordIndices)) },
                )

                Spacer(modifier = Modifier.height(Spacing.lg))
                if (state.stagedYearChips.isNotEmpty()) {
                    SectionTitle(stringResource(Res.string.objection_insurance_staged_section_title))
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    StagedYearsRow(
                        chips = state.stagedYearChips,
                        onRemove = { indices ->
                            onIntent(
                                ObjectionInsuranceIntent.OnStagedChipRemoveClicked(
                                    indices
                                )
                            )
                        },
                    )
                    Spacer(modifier = Modifier.height(Spacing.lg))
                } else {
                    EmptyHintBanner()
                    Spacer(modifier = Modifier.height(Spacing.lg))
                }

                DescriptionField(
                    value = state.description,
                    onValueChange = { onIntent(ObjectionInsuranceIntent.OnDescriptionChanged(it)) },
                )
            }
        }
    }
}

@Composable
private fun ObjectionInsuranceBanner() {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Spacing.md)
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.onGradient.copy(alpha = 0.1f))
            .padding(Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = Icons.Outlined.CalendarMonth,
            contentDescription = null,
            tint = colors.onGradient,
            modifier = Modifier.size(20.dp),
        )
        TaminText(
            text = stringResource(Res.string.objection_insurance_banner),
            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
            color = colors.onGradient,
        )
    }
}

@Composable
private fun DescriptionField(value: String, onValueChange: (String) -> Unit) {
    val colors = LocalTaminColors.current
    Column(modifier = Modifier.fillMaxWidth()) {
        TaminText(
            text = stringResource(Res.string.description_label),
            style = MaterialTheme.typography.labelMedium,
            color = colors.textSecondary,
            modifier = Modifier.padding(bottom = Spacing.xs),
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(DescriptionFieldHeight)
                .clip(TaminHamrahShapes.large)
                .border(1.dp, colors.border, TaminHamrahShapes.large),
            placeholder = {
                TaminText(
                    text = stringResource(Res.string.objection_insurance_description_placeholder),
                    color = colors.textMuted
                )
            },
            shape = TaminHamrahShapes.large,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = colors.bgSurface,
                unfocusedContainerColor = colors.bgSurface,
            ),
            maxLines = 4,
            textStyle = MaterialTheme.typography.bodySmall.copy(color = colors.textPrimary),
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    val colors = LocalTaminColors.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 3.dp, height = 15.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(colors.blueText),
        )
        TaminText(
            text = text,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
            color = colors.textPrimary,
        )
    }
}

@Composable
private fun StagedYearsRow(
    chips: ImmutableList<StagedYearChipPR>,
    onRemove: (ImmutableList<Int>) -> Unit
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        chips.forEach { chip ->
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(colors.blueBg)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { onRemove(chip.recordIndices) },
                    )
                    .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TaminText(
                    text = chip.label,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.blueText,
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = colors.blueText,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}

@Composable
private fun EmptyHintBanner() {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.blueBg)
            .padding(Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = colors.blueText,
            modifier = Modifier.size(16.dp),
        )
        TaminText(
            text = stringResource(Res.string.objection_insurance_empty_hint),
            style = MaterialTheme.typography.labelSmall.copy(lineHeight = 20.sp),
            color = colors.textSecondary,
        )
    }
}

@Composable
private fun SubmitButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.bgPage)
            .padding(Spacing.page)
            .navigationBarsPadding()
            .imePadding(),
    ) {
        TaminFilledButton(
            text = stringResource(Res.string.objection_insurance_submit_button),
            onClick = onClick,
            background = colors.buttonGradient,
        )
    }
}

@Composable
private fun ActiveRequestDialog(onDismiss: () -> Unit) {
    TaminConfirmationDialog(
        title = stringResource(Res.string.objection_insurance_active_request_title),
        description = stringResource(Res.string.objection_insurance_active_request_message),
        confirmButton = {
            TaminFilledButton(
                text = stringResource(Res.string.objection_insurance_ack_button),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        dismissButton = {},
        onDismissRequest = onDismiss,
        icon = Icons.Outlined.Info,
    )
}

@Composable
private fun HelpDialog(onDismiss: () -> Unit) {
    TaminConfirmationDialog(
        title = stringResource(Res.string.objection_insurance_help_title),
        description = stringResource(Res.string.objection_insurance_help_message),
        confirmButton = {
            TaminFilledButton(
                text = stringResource(Res.string.objection_insurance_ack_button),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        dismissButton = {},
        onDismissRequest = onDismiss,
        icon = Icons.Outlined.Info,
    )
}

@Composable
private fun SubmitConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val colors = LocalTaminColors.current
    TaminConfirmationDialog(
        title = stringResource(Res.string.objection_insurance_submit_confirmation_title),
        description = stringResource(Res.string.objection_insurance_submit_confirmation_message),
        confirmButton = {
            TaminFilledButton(
                text = stringResource(Res.string.objection_insurance_submit_button),
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                background = colors.buttonGradient,
            )
        },
        dismissButton = {
            TaminOutlinedButton(
                text = stringResource(Res.string.action_cancel),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        onDismissRequest = onDismiss,
        icon = Icons.Default.Warning,
        iconTint = colors.orangeText,
        iconBackground = colors.orangeBg,
    )
}

@Composable
private fun TrackingNumberDialog(trackingNumber: String, onAcknowledge: () -> Unit) {
    val colors = LocalTaminColors.current
    TaminConfirmationDialog(
        title = stringResource(Res.string.objection_insurance_success_title),
        description = stringResource(Res.string.objection_insurance_success_tracking_label),
        confirmButton = {
            TaminFilledButton(
                text = stringResource(Res.string.objection_insurance_ack_button),
                onClick = onAcknowledge,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        dismissButton = {},
        onDismissRequest = onAcknowledge,
        icon = Icons.Default.Check,
        iconTint = colors.greenText,
        iconBackground = colors.greenBg,
        content = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(CornerRadius.lg))
                    .background(colors.blueBg)
                    .padding(vertical = Spacing.md),
                contentAlignment = Alignment.Center,
            ) {
                NumericText(
                    text = trackingNumber,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.blueText,
                )
            }
        },
    )
}

@PreviewRtlTheme
@Composable
private fun ObjectionInsuranceLoadingPreview() {
    PreviewRtlThemeContent {
        ObjectionInsuranceContent(
            state = ObjectionInsuranceUiState(isLoading = true),
            onIntent = {},
            onBack = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ObjectionInsuranceEmptyPreview() {
    PreviewRtlThemeContent {
        ObjectionInsuranceContent(
            state = ObjectionInsuranceUiState(),
            onIntent = {},
            onBack = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ObjectionInsuranceLoadedPreview() {
    PreviewRtlThemeContent {
        ObjectionInsuranceContent(
            state = ObjectionInsuranceUiState(records = previewLoadedRecords()),
            onIntent = {},
            onBack = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ObjectionInsuranceStagedPreview() {
    PreviewRtlThemeContent {
        ObjectionInsuranceContent(
            state = ObjectionInsuranceUiState(
                records = previewLoadedRecords(),
                edits = persistentMapOf(
                    0 to persistentMapOf(0 to "28"),
                ),
            ),
            onIntent = {},
            onBack = {},
        )
    }
}
