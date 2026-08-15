package com.tamin.taminhamrah.feature.settings.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.settings.ui.contract.FontSizeOption
import com.tamin.taminhamrah.feature.settings.ui.contract.SettingsEvent
import com.tamin.taminhamrah.feature.settings.ui.contract.SettingsIntent
import com.tamin.taminhamrah.feature.settings.ui.contract.SettingsUiState
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.ListGroupView
import com.tamin.taminhamrah.ui.components.ListItemColors
import com.tamin.taminhamrah.ui.components.ListItemData
import com.tamin.taminhamrah.ui.components.SectionHeaderTitle
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.DarkTaminColors
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_back
import taminx.core.core_ui.ic_moon
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.profile_settings
import taminx.core.core_ui.settings_appearance_section
import taminx.core.core_ui.settings_font_size
import taminx.core.core_ui.settings_font_size_large
import taminx.core.core_ui.settings_font_size_medium
import taminx.core.core_ui.settings_font_size_small
import taminx.core.core_ui.settings_night_mode

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    HandleSettingsEvents(
        events = viewModel.events,
        onNavigateBack = onNavigateBack
    )

    SettingsContent(
        state = uiState,
        onIntent = viewModel::sendIntent
    )
}

@Composable
private fun HandleSettingsEvents(
    events: Flow<SettingsEvent>,
    onNavigateBack: () -> Unit
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            SettingsEvent.NavigateBack -> onNavigateBack()
        }
    }
}

@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
    state: SettingsUiState,
    onIntent: (SettingsIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    val isDark = colors == DarkTaminColors
    val defaultBorder = remember(colors) { BorderStroke(1.dp, colors.border) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.profile_settings),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = stringResource(Res.string.action_back),
                        onClick = { onIntent(SettingsIntent.OnBackClicked) },
                        bordered = true,
                    )
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.bgPage)
                .verticalScroll(rememberScrollState())
                .padding(
                    top = innerPadding.calculateTopPadding() + Spacing.lg,
                    bottom = innerPadding.calculateBottomPadding() + Spacing.xxl,
                ),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.page),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                SectionHeaderTitle(title = stringResource(Res.string.settings_appearance_section))

                ListGroupView(
                    containerBorder = defaultBorder,
                    items = persistentListOf(
                        ListItemData(
                            title = stringResource(Res.string.settings_night_mode),
                            leadingIconPainter = painterResource(Res.drawable.ic_moon),
                            showArrow = false,
                            customTrailingContent = {
                                TaminSwitchButtonTest(
                                    checked = isDark,
                                    onCheckedChange = { onIntent(SettingsIntent.ToggleNightMode(it)) },
                                )
                            },
                        ),
                        ListItemData(
                            title = stringResource(Res.string.settings_font_size),
                            leadingIconPainter = painterResource(Res.drawable.ic_moon),
                            showArrow = false,
                            customTrailingContent = {
                                FontSizeSelector(
                                    selected = state.fontSize,
                                    onSelect = { onIntent(SettingsIntent.SelectFontSize(it)) },
                                )
                            },
                        ),
                    )
                )
            }
        }
    }
}

/**
 * Three-way segmented control, sized to its own content — it sits as trailing content next
 * to a row title (see the font-size [ListItemData] above), not spanning the card's width.
 */
@Composable
private fun FontSizeSelector(
    selected: FontSizeOption,
    onSelect: (FontSizeOption) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier
            .background(colors.bgPage, RoundedCornerShape(CornerRadius.md))
            .padding(Spacing.xxs),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        FontSizeOption.entries.forEach { option ->
            val isSelected = option == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(CornerRadius.sm))
                    .background(if (isSelected) colors.bgSurface else Color.Transparent)
                    .clickable { onSelect(option) }
                    .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = option.label(),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isSelected) colors.blueText else colors.textMuted,
                )
            }
        }
    }
}

@Composable
private fun FontSizeOption.label(): String = when (this) {
    FontSizeOption.SMALL -> stringResource(Res.string.settings_font_size_small)
    FontSizeOption.MEDIUM -> stringResource(Res.string.settings_font_size_medium)
    FontSizeOption.LARGE -> stringResource(Res.string.settings_font_size_large)
}

@PreviewRtlTheme
@Composable
private fun SettingsScreenLightPreview() {
    PreviewRtlThemeContent {
        SettingsContent(
            state = SettingsUiState(),
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun SettingsScreenDarkPreview() {
    PreviewRtlThemeContent(darkTheme = true) {
        SettingsContent(
            state = SettingsUiState(fontSize = FontSizeOption.LARGE),
            onIntent = {},
        )
    }
}


/** On/off toggle — a settings row's trailing control, colored off the theme's brand blue. */
@Composable
fun TaminSwitchButtonTest(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = LocalTaminColors.current
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = colors.blueText,
            checkedBorderColor = Color.Transparent,
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = colors.border,
            uncheckedBorderColor = Color.Transparent,
        ),
    )
}

