package com.tamin.taminhamrah.feature.developerOptions.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.developerOptions.ui.contract.BaseUrlItemUi
import com.tamin.taminhamrah.feature.developerOptions.ui.contract.DeveloperOptionsEvent
import com.tamin.taminhamrah.feature.developerOptions.ui.contract.DeveloperOptionsIntent
import com.tamin.taminhamrah.feature.developerOptions.ui.contract.DeveloperOptionsUiState
import com.tamin.taminhamrah.feature.developerOptions.ui.model.BaseUrlPresets
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.ListGroupView
import com.tamin.taminhamrah.ui.components.ListItemColors
import com.tamin.taminhamrah.ui.components.ListItemData
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.DarkTaminColors
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_cancel
import taminx.core.core_ui.action_save
import taminx.core.core_ui.developer_options_custom_url_hint
import taminx.core.core_ui.developer_options_dialog_title
import taminx.core.core_ui.developer_options_reset_to_default
import taminx.core.core_ui.developer_options_restart_notice
import taminx.core.core_ui.developer_options_title
import taminx.core.core_ui.ic_tamin_chevron_back

@Composable
fun DeveloperOptionsScreen(
    viewModel: DeveloperOptionsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    HandleDeveloperOptionsEvents(
        events = viewModel.events,
        onNavigateBack = onNavigateBack
    )

    DeveloperOptionsContent(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onNavigateBack = onNavigateBack
    )
}

@Composable
private fun HandleDeveloperOptionsEvents(
    events: Flow<DeveloperOptionsEvent>,
    onNavigateBack: () -> Unit
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is DeveloperOptionsEvent.NavigateBack -> onNavigateBack()
        }
    }
}

@Composable
private fun DeveloperOptionsContent(
    modifier: Modifier = Modifier,
    state: DeveloperOptionsUiState,
    onIntent: (DeveloperOptionsIntent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val isDark = taminColors == DarkTaminColors
    val topBarGradient = remember(isDark) { Brush.horizontalGradient(taminColors.profileGradientStops) }
    val defaultBorder = remember(taminColors) { BorderStroke(1.dp, taminColors.border) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TaminTopAppBar(
                background = topBarGradient,
                title = stringResource(Res.string.developer_options_title),
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    top = innerPadding.calculateTopPadding() + Spacing.lg,
                    bottom = innerPadding.calculateBottomPadding() + Spacing.xxl
                ),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.page)
            ) {
                Text(
                    text = stringResource(Res.string.developer_options_restart_notice),
                    style = MaterialTheme.typography.bodySmall,
                    color = taminColors.textSecondary
                )

                Spacer(modifier = Modifier.height(Spacing.lg))

                ListGroupView(
                    containerBorder = defaultBorder,
                    items = persistentListOf(
                        *state.items.map { item ->
                            ListItemData(
                                title = item.displayName,
                                subtitle = item.currentUrl,
                                leadingIconPainter = rememberVectorPainter(Icons.Rounded.Code),
                                colors = ListItemColors(
                                    leadingIconTintColor = taminColors.bgIconProfile,
                                    leadingIconBackgroundGradient = taminColors.iconGradientNeutral
                                ),
                                showArrow = true,
                                onClick = { onIntent(DeveloperOptionsIntent.OnItemClicked(item.key)) }
                            )
                        }.toTypedArray()
                    )
                )
            }
        }
    }

    val editingKey = state.editingKey
    if (editingKey != null) {
        val editingItem = state.items.firstOrNull { it.key == editingKey }
        if (editingItem != null) {
            EditBaseUrlDialog(
                item = editingItem,
                onConfirm = { url -> onIntent(DeveloperOptionsIntent.OnUrlConfirmed(editingKey, url)) },
                onReset = { onIntent(DeveloperOptionsIntent.OnResetClicked(editingKey)) },
                onDismiss = { onIntent(DeveloperOptionsIntent.OnDialogDismissed) }
            )
        }
    }
}

@Composable
private fun EditBaseUrlDialog(
    item: BaseUrlItemUi,
    onConfirm: (String) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    var customUrl by remember(item.key) { mutableStateOf(item.currentUrl) }
    val presets = BaseUrlPresets.presets[item.key].orEmpty()

    TaminConfirmationDialog(
        title = item.displayName,
        description = stringResource(Res.string.developer_options_dialog_title),
        onDismissRequest = onDismiss,
        confirmButton = {
            TaminFilledButton(
                text = stringResource(Res.string.action_save),
                onClick = { onConfirm(customUrl) },
                modifier = Modifier.fillMaxWidth(),
                height = 50.dp,
                shape = RoundedCornerShape(14.dp)
            )
        },
        dismissButton = {
            TaminOutlinedButton(
                text = stringResource(Res.string.action_cancel),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                height = 50.dp,
                shape = RoundedCornerShape(14.dp)
            )
        },
        content = {
            Column(modifier = Modifier.fillMaxWidth()) {
                presets.forEach { preset ->
                    val selected = preset.url == customUrl
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { customUrl = preset.url }
                            .padding(vertical = Spacing.sm),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = preset.label,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = taminColors.textPrimary
                            )
                            Text(
                                text = preset.url,
                                style = MaterialTheme.typography.bodySmall,
                                color = taminColors.textSecondary
                            )
                        }
                        if (selected) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                tint = taminColors.blueText
                            )
                        }
                    }
                    HorizontalDivider(color = taminColors.border)
                }

                Spacer(modifier = Modifier.height(Spacing.md))

                OutlinedTextField(
                    value = customUrl,
                    onValueChange = { customUrl = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(Res.string.developer_options_custom_url_hint)) },
                    singleLine = true
                )

                if (item.isOverridden) {
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onReset() }
                            .padding(vertical = Spacing.sm),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Restore,
                            contentDescription = null,
                            tint = taminColors.dangerText
                        )
                        Text(
                            text = stringResource(Res.string.developer_options_reset_to_default),
                            style = MaterialTheme.typography.bodyMedium,
                            color = taminColors.dangerText
                        )
                    }
                }
            }
        }
    )
}
