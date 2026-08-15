package com.tamin.taminhamrah.feature.settings.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.FontSizeOption
import com.tamin.taminhamrah.feature.settings.ui.contract.SettingsEvent
import com.tamin.taminhamrah.feature.settings.ui.contract.SettingsIntent
import com.tamin.taminhamrah.feature.settings.ui.contract.SettingsUiState
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.ListGroupView
import com.tamin.taminhamrah.ui.components.ListItemData
import com.tamin.taminhamrah.ui.components.SectionHeaderTitle
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.DarkTaminColors
import com.tamin.taminhamrah.ui.theme.Elevation
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
import taminx.core.core_ui.ic_font_scale
import taminx.core.core_ui.ic_moon
import taminx.core.core_ui.ic_sun
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
                                TaminSwitchButton(
                                    checked = isDark,
                                    onCheckedChange = { onIntent(SettingsIntent.ToggleNightMode(it)) },
                                    showThemeIcon = true,
                                )
                            },
                        ),
                        ListItemData(
                            title = stringResource(Res.string.settings_font_size),
                            leadingIconPainter = painterResource(Res.drawable.ic_font_scale),
                            showArrow = false,
                        ),
                    ),
                    footerContent = {
                        FontSizeSelector(
                            selected = state.fontSize,
                            onSelect = { onIntent(SettingsIntent.SelectFontSize(it)) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                )
            }
        }
    }
}

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
            .padding(Spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        FontSizeOption.entries.forEach { option ->
            val isSelected = option == selected
            Box(
                modifier = Modifier
                    .weight(1f)
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



private val SwitchTrackWidth = 46.dp
private val SwitchTrackHeight = 26.dp
private val SwitchThumbSize = 20.dp
private val SwitchThumbPadding = 3.dp
private val SwitchThumbIconSize = 13.dp
private const val ThemeIconAnimationDurationMillis = 250
private val SunIconTint = Color(0xFF1F4FA3)

@Immutable
data class TaminSwitchColors(
    val checkedTrackColor: Color,
    val uncheckedTrackColor: Color,
    val checkedThumbColor: Color,
    val uncheckedThumbColor: Color,
    val disabledCheckedTrackColor: Color,
    val disabledUncheckedTrackColor: Color,
    val disabledCheckedThumbColor: Color,
    val disabledUncheckedThumbColor: Color,
)

object TaminSwitchDefaults {
    @Composable
    fun colors(
        checkedTrackColor: Color = run {
            val colors = LocalTaminColors.current
            if (colors == DarkTaminColors) Color(0xFF1F4FA3) else colors.blueText
        },
        uncheckedTrackColor: Color = run {
            val colors = LocalTaminColors.current
            if (colors == DarkTaminColors) colors.outerBorder else colors.grey900
        },
        checkedThumbColor: Color = run {
            val colors = LocalTaminColors.current
            if (colors == DarkTaminColors) colors.textPrimary else Color.White
        },
        uncheckedThumbColor: Color = run {
            val colors = LocalTaminColors.current
            if (colors == DarkTaminColors) colors.textSecondary else Color.White
        },
        disabledCheckedTrackColor: Color = checkedTrackColor.copy(alpha = LocalTaminColors.current.disabledAlpha),
        disabledUncheckedTrackColor: Color = uncheckedTrackColor.copy(alpha = LocalTaminColors.current.disabledAlpha),
        disabledCheckedThumbColor: Color = checkedThumbColor,
        disabledUncheckedThumbColor: Color = uncheckedThumbColor,
    ): TaminSwitchColors = TaminSwitchColors(
        checkedTrackColor = checkedTrackColor,
        uncheckedTrackColor = uncheckedTrackColor,
        checkedThumbColor = checkedThumbColor,
        uncheckedThumbColor = uncheckedThumbColor,
        disabledCheckedTrackColor = disabledCheckedTrackColor,
        disabledUncheckedTrackColor = disabledUncheckedTrackColor,
        disabledCheckedThumbColor = disabledCheckedThumbColor,
        disabledUncheckedThumbColor = disabledUncheckedThumbColor,
    )
}


@Composable
fun TaminSwitchButton(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: TaminSwitchColors = TaminSwitchDefaults.colors(),
    showThemeIcon: Boolean = false,
) {
    val trackColor by animateColorAsState(
        targetValue = when {
            checked && enabled -> colors.checkedTrackColor
            checked && !enabled -> colors.disabledCheckedTrackColor
            !checked && enabled -> colors.uncheckedTrackColor
            else -> colors.disabledUncheckedTrackColor
        },
        label = "TaminSwitchTrackColor",
    )
    val thumbColor by animateColorAsState(
        targetValue = when {
            checked && enabled -> colors.checkedThumbColor
            checked && !enabled -> colors.disabledCheckedThumbColor
            !checked && enabled -> colors.uncheckedThumbColor
            else -> colors.disabledUncheckedThumbColor
        },
        label = "TaminSwitchThumbColor",
    )
    val thumbOffset: Dp by animateDpAsState(
        targetValue = if (checked) SwitchTrackWidth - SwitchThumbSize - SwitchThumbPadding else SwitchThumbPadding,
        label = "TaminSwitchThumbOffset",
    )
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(width = SwitchTrackWidth, height = SwitchTrackHeight)
            .toggleable(
                value = checked,
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && onCheckedChange != null,
                role = Role.Switch,
                onValueChange = { onCheckedChange?.invoke(it) },
            )
            .background(trackColor, CircleShape),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .padding(start = thumbOffset)
                .size(SwitchThumbSize)
                .shadow(elevation = Elevation.xxs, shape = CircleShape, clip = false)
                .background(thumbColor, CircleShape)
                .clip(CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (showThemeIcon) {
                SwitchThumbThemeIcon(checked = checked, tint = trackColor)
            }
        }
    }
}

/**
 * Sun/moon glyph shown inside the thumb when [TaminSwitchButton.showThemeIcon] is opted in.
 * The outgoing icon fades out while rotating ~90° one way; the incoming icon fades in from the
 * opposite rotation and settles at 0°, so the two swaps never share the same spin direction.
 */
@Composable
private fun SwitchThumbThemeIcon(
    checked: Boolean,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    val animationSpec = remember {
        tween<Float>(durationMillis = ThemeIconAnimationDurationMillis, easing = FastOutSlowInEasing)
    }
    val moonAlpha by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = animationSpec,
        label = "ThemeIconMoonAlpha",
    )
    val moonRotation by animateFloatAsState(
        targetValue = if (checked) 0f else 90f,
        animationSpec = animationSpec,
        label = "ThemeIconMoonRotation",
    )
    val sunAlpha by animateFloatAsState(
        targetValue = if (checked) 0f else 1f,
        animationSpec = animationSpec,
        label = "ThemeIconSunAlpha",
    )
    val sunRotation by animateFloatAsState(
        targetValue = if (checked) -90f else 0f,
        animationSpec = animationSpec,
        label = "ThemeIconSunRotation",
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_moon),
            contentDescription = null,
            tint = tint,
            modifier = Modifier
                .size(SwitchThumbIconSize)
                .graphicsLayer {
                    alpha = moonAlpha
                    rotationZ = moonRotation
                },
        )
        Icon(
            imageVector = vectorResource(Res.drawable.ic_sun),
            contentDescription = null,
            tint = SunIconTint,
            modifier = Modifier
                .size(SwitchThumbIconSize)
                .graphicsLayer {
                    alpha = sunAlpha
                    rotationZ = sunRotation
                },
        )
    }
}

@PreviewRtlTheme
@Composable
private fun TaminSwitchPreviewLight() {
    PreviewRtlThemeContent {
        TaminSwitchPreviewContent()
    }
}

@PreviewRtlTheme
@Composable
private fun TaminSwitchPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        TaminSwitchPreviewContent()
    }
}

@Composable
private fun TaminSwitchPreviewContent() {
    var checkedOn by remember { mutableStateOf(true) }
    var checkedOff by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .background(LocalTaminColors.current.bgPage)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        TaminSwitchButton(checked = checkedOn, onCheckedChange = { checkedOn = it })
        TaminSwitchButton(checked = checkedOff, onCheckedChange = { checkedOff = it })
        TaminSwitchButton(checked = true, onCheckedChange = null, enabled = false)
        TaminSwitchButton(checked = false, onCheckedChange = null, enabled = false)
    }
}

