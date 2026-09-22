package com.tamin.taminhamrah.feature.developerOptions.featureFlags

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.developerOptions.featureFlags.contract.FeatureFlagRowUi
import com.tamin.taminhamrah.feature.developerOptions.featureFlags.contract.FeatureFlagsEvent
import com.tamin.taminhamrah.feature.developerOptions.featureFlags.contract.FeatureFlagsIntent
import com.tamin.taminhamrah.feature.developerOptions.featureFlags.contract.FeatureFlagsUiState
import com.tamin.taminhamrah.feature.developerOptions.featureFlags.contract.OverrideKind
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminColors
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_cancel
import taminx.core.core_ui.action_save
import taminx.core.core_ui.developer_options_feature_flags_clear_all
import taminx.core.core_ui.developer_options_feature_flags_editor_hint
import taminx.core.core_ui.developer_options_feature_flags_editor_message_label
import taminx.core.core_ui.developer_options_feature_flags_kind_disabled
import taminx.core.core_ui.developer_options_feature_flags_kind_enabled
import taminx.core.core_ui.developer_options_feature_flags_kind_enabled_with_error
import taminx.core.core_ui.developer_options_feature_flags_kind_temporary_disabled
import taminx.core.core_ui.developer_options_feature_flags_overridden_badge
import taminx.core.core_ui.developer_options_feature_flags_reset_to_server
import taminx.core.core_ui.developer_options_feature_flags_row_hint
import taminx.core.core_ui.developer_options_feature_flags_status_disabled
import taminx.core.core_ui.developer_options_feature_flags_status_enabled
import taminx.core.core_ui.developer_options_feature_flags_status_enabled_with_error
import taminx.core.core_ui.developer_options_feature_flags_status_temporary_disabled
import taminx.core.core_ui.developer_options_feature_flags_status_web_view
import taminx.core.core_ui.developer_options_feature_flags_title
import taminx.core.core_ui.ic_tamin_chevron_back

@Composable
fun FeatureFlagsScreen(
    viewModel: FeatureFlagsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    HandleFeatureFlagsEvents(events = viewModel.events, onNavigateBack = onNavigateBack)

    FeatureFlagsContent(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
private fun HandleFeatureFlagsEvents(events: Flow<FeatureFlagsEvent>, onNavigateBack: () -> Unit) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            FeatureFlagsEvent.NavigateBack -> onNavigateBack()
        }
    }
}

@Composable
private fun FeatureFlagsContent(
    modifier: Modifier = Modifier,
    state: FeatureFlagsUiState,
    onIntent: (FeatureFlagsIntent) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val taminColors = LocalTaminColors.current
    val topBarGradient = remember(taminColors) { Brush.horizontalGradient(taminColors.profileGradientStops) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TaminTopAppBar(
                background = topBarGradient,
                title = stringResource(Res.string.developer_options_feature_flags_title),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = null,
                        onClick = onNavigateBack,
                        bordered = true
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(top = innerPadding.calculateTopPadding())) {
            Text(
                text = stringResource(Res.string.developer_options_feature_flags_row_hint),
                style = MaterialTheme.typography.bodySmall,
                color = taminColors.textSecondary,
                modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.page, vertical = Spacing.sm),
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding() + Spacing.xxl),
            ) {
                items(state.rows, key = { it.flag }) { row ->
                    FeatureFlagRow(row = row, onLongPress = { onIntent(FeatureFlagsIntent.OnRowLongPressed(row.flag)) })
                    HorizontalDivider(color = taminColors.divider)
                }
            }

            Column(modifier = Modifier.fillMaxWidth().padding(Spacing.page)) {
                TaminOutlinedButton(
                    text = stringResource(Res.string.developer_options_feature_flags_clear_all),
                    onClick = { onIntent(FeatureFlagsIntent.OnClearAllClicked) },
                    modifier = Modifier.fillMaxWidth(),
                    height = 46.dp,
                    shape = RoundedCornerShape(14.dp),
                )
            }
        }
    }

    val editingFlag = state.editingFlag
    if (editingFlag != null) {
        val editingRow = state.rows.firstOrNull { it.flag == editingFlag }
        FeatureFlagEditorDialog(
            flag = editingFlag,
            currentStatus = editingRow?.status,
            isOverridden = editingRow?.isOverridden == true,
            onConfirm = { kind, message -> onIntent(FeatureFlagsIntent.OnOverrideConfirmed(editingFlag, kind, message)) },
            onReset = { onIntent(FeatureFlagsIntent.OnOverrideCleared(editingFlag)) },
            onDismiss = { onIntent(FeatureFlagsIntent.OnEditorDismissed) },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FeatureFlagRow(row: FeatureFlagRowUi, onLongPress: () -> Unit) {
    val taminColors = LocalTaminColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = {}, onLongClick = onLongPress)
            .padding(horizontal = Spacing.page, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${row.flag.name} (${row.flag.id})",
                style = MaterialTheme.typography.bodyMedium,
                color = taminColors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = row.status.describe(),
                style = MaterialTheme.typography.bodySmall,
                color = row.status.statusColor(taminColors),
            )
        }
        if (row.isOverridden) {
            Box(
                modifier = Modifier
                    .background(taminColors.orangeBg, RoundedCornerShape(CornerRadius.lg))
                    .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
            ) {
                Text(
                    text = stringResource(Res.string.developer_options_feature_flags_overridden_badge),
                    style = MaterialTheme.typography.labelSmall,
                    color = taminColors.orangeText,
                )
            }
        }
    }
}

@Composable
private fun FeatureStatus.describe(): String = when (this) {
    FeatureStatus.Enabled -> stringResource(Res.string.developer_options_feature_flags_status_enabled)
    is FeatureStatus.Disabled -> message?.let {
        stringResource(Res.string.developer_options_feature_flags_status_disabled) + " — $it"
    } ?: stringResource(Res.string.developer_options_feature_flags_status_disabled)
    is FeatureStatus.TemporaryDisabled -> message?.let {
        stringResource(Res.string.developer_options_feature_flags_status_temporary_disabled) + " — $it"
    } ?: stringResource(Res.string.developer_options_feature_flags_status_temporary_disabled)
    is FeatureStatus.EnabledWithError -> message?.let {
        stringResource(Res.string.developer_options_feature_flags_status_enabled_with_error) + " — $it"
    } ?: stringResource(Res.string.developer_options_feature_flags_status_enabled_with_error)
    is FeatureStatus.WebView -> stringResource(Res.string.developer_options_feature_flags_status_web_view) + " — $url"
}

private fun FeatureStatus.statusColor(colors: TaminColors) = when (this) {
    FeatureStatus.Enabled, is FeatureStatus.WebView -> colors.greenText
    is FeatureStatus.EnabledWithError -> colors.orangeText
    is FeatureStatus.Disabled, is FeatureStatus.TemporaryDisabled -> colors.dangerText
}

/**
 * Long-press editor: pick the status kind to force, an optional message/warning, or reset to
 * whatever the real menu (or no override at all) would say.
 */
@Composable
private fun FeatureFlagEditorDialog(
    flag: FeatureFlag,
    currentStatus: FeatureStatus?,
    isOverridden: Boolean,
    onConfirm: (OverrideKind, String) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit,
) {
    val taminColors = LocalTaminColors.current
    var selectedKind by remember(flag) {
        mutableStateOf(
            when (currentStatus) {
                is FeatureStatus.Disabled -> OverrideKind.DISABLED
                is FeatureStatus.TemporaryDisabled -> OverrideKind.TEMPORARY_DISABLED
                is FeatureStatus.EnabledWithError -> OverrideKind.ENABLED_WITH_ERROR
                else -> OverrideKind.ENABLED
            }
        )
    }
    var message by remember(flag) {
        mutableStateOf(
            when (currentStatus) {
                is FeatureStatus.Disabled -> currentStatus.message
                is FeatureStatus.TemporaryDisabled -> currentStatus.message
                is FeatureStatus.EnabledWithError -> currentStatus.message
                else -> null
            }.orEmpty()
        )
    }

    TaminConfirmationDialog(
        title = "${flag.name} (${flag.id})",
        description = stringResource(Res.string.developer_options_feature_flags_editor_hint),
        onDismissRequest = onDismiss,
        confirmButton = {
            TaminFilledButton(
                text = stringResource(Res.string.action_save),
                onClick = { onConfirm(selectedKind, message) },
                modifier = Modifier.fillMaxWidth(),
                height = 50.dp,
                shape = RoundedCornerShape(14.dp),
            )
        },
        dismissButton = {
            TaminOutlinedButton(
                text = stringResource(Res.string.action_cancel),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                height = 50.dp,
                shape = RoundedCornerShape(14.dp),
            )
        },
        content = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OverrideKind.entries.forEach { kind ->
                    val selected = kind == selectedKind
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(role = Role.RadioButton) { selectedKind = kind }
                            .padding(vertical = Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    ) {
                        RadioButton(
                            selected = selected,
                            onClick = { selectedKind = kind },
                            colors = RadioButtonDefaults.colors(selectedColor = taminColors.blueText),
                        )
                        Text(
                            text = kind.label(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = taminColors.textPrimary,
                        )
                    }
                }

                if (selectedKind != OverrideKind.ENABLED) {
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(Res.string.developer_options_feature_flags_editor_message_label)) },
                    )
                }

                if (isOverridden) {
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onReset() }
                            .padding(vertical = Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(Res.string.developer_options_feature_flags_reset_to_server),
                            style = MaterialTheme.typography.bodyMedium,
                            color = taminColors.dangerText,
                        )
                    }
                }
            }
        },
    )
}

@Composable
private fun OverrideKind.label(): String = when (this) {
    OverrideKind.ENABLED -> stringResource(Res.string.developer_options_feature_flags_kind_enabled)
    OverrideKind.DISABLED -> stringResource(Res.string.developer_options_feature_flags_kind_disabled)
    OverrideKind.TEMPORARY_DISABLED -> stringResource(Res.string.developer_options_feature_flags_kind_temporary_disabled)
    OverrideKind.ENABLED_WITH_ERROR -> stringResource(Res.string.developer_options_feature_flags_kind_enabled_with_error)
}

