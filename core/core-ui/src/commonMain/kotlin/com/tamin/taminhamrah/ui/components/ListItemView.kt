package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.painterResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_arrow_show_more
import taminx.core.core_ui.ic_tamin_logo
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf


@Immutable
data class ListItemBadge(
    val text: String,
    val backgroundColor: Color? = null,
    val textColor: Color? = null
)


@Immutable
data class ListItemColors(
    val titleColor: Color = Color.Unspecified,
    val subtitleColor: Color = Color.Unspecified,
    val leadingIconBackgroundColor: Color = Color.Unspecified,
    val leadingIconBackgroundGradient: Brush? = null,
    val leadingIconTintColor: Color = Color.Unspecified,
    val itemBackgroundColor: Color = Color.Transparent
)


@Immutable
data class ListItemData(
    val title: String,
    val subtitle: String? = null,
    val leadingIconPainter: Painter? = null,
    val leadingText: String? = null,
    val leadingIconShape: Shape = RoundedCornerShape(CornerRadius.lg),
    val leadingIconContainerSize: Dp = IconSize.large,
    val leadingIconPadding: Dp = Spacing.sm,
    val leadingIconBorder: BorderStroke? = null,
    val leadingIconElevation: Dp = Elevation.none,
    val badge: ListItemBadge? = null,
    val showArrow: Boolean = true,
    val customTrailingContent: (@Composable () -> Unit)? = null,
    val onClick: (() -> Unit)? = null,
    val enabled: Boolean = true,
    val titleStyle: TextStyle? = null,
    val subtitleStyle: TextStyle? = null,
    val colors: com.tamin.taminhamrah.ui.components.ListItemColors = ListItemColors(),
    val showDivider: Boolean? = null,
    val itemBorder: BorderStroke? = null
)


@Composable
fun ListGroupView(
    items: ImmutableList<ListItemData>,
    modifier: Modifier = Modifier,
    containerShape: Shape = RoundedCornerShape(CornerRadius.lg),
    containerBackgroundColor: Color? = null,
    containerBorder: BorderStroke? = null,
    elevation: Dp = Elevation.none,
    internalPadding: PaddingValues = PaddingValues(Spacing.xxs),
    itemContentPadding: PaddingValues = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.md),
    showDividers: Boolean = true,
    dividerColor: Color? = null,
    dividerStartIndent: Dp? = null
) {
    val taminColors = LocalTaminColors.current
    val bgColor = containerBackgroundColor ?: taminColors.bgSurface
    val resolvedDividerColor = dividerColor ?: taminColors.divider

    Surface(
        modifier = modifier,
        shape = containerShape,
        color = bgColor,
        border = containerBorder,
        shadowElevation = elevation,
        contentColor = taminColors.textPrimary
    ) {
        Column(modifier = Modifier.padding(internalPadding)) {
            items.forEachIndexed { index, item ->
                ListItemRow(
                    item = item,
                    contentPadding = itemContentPadding,
                    isLastItem = index == items.lastIndex,
                    defaultShowDivider = showDividers,
                    dividerColor = resolvedDividerColor,
                    dividerStartIndent = dividerStartIndent
                )
            }
        }
    }
}

@Composable
private fun ListItemRow(
    item: ListItemData,
    contentPadding: PaddingValues,
    isLastItem: Boolean,
    defaultShowDivider: Boolean,
    dividerColor: Color,
    dividerStartIndent: Dp?
) {
    val taminColors = LocalTaminColors.current

    val showDivider = item.showDivider ?: (defaultShowDivider && !isLastItem)
    val titleStyle = item.titleStyle ?: MaterialTheme.typography.titleSmall
    val subtitleStyle = item.subtitleStyle ?: MaterialTheme.typography.bodySmall

    val resolvedTitleColor = if (item.colors.titleColor != Color.Unspecified) item.colors.titleColor else taminColors.textPrimary
    val resolvedSubtitleColor = if (item.colors.subtitleColor != Color.Unspecified) item.colors.subtitleColor else taminColors.textSecondary
    val resolvedIconBgColor = if (item.colors.leadingIconBackgroundColor != Color.Unspecified) item.colors.leadingIconBackgroundColor else taminColors.blueBg
    val resolvedIconTintColor = if (item.colors.leadingIconTintColor != Color.Unspecified) item.colors.leadingIconTintColor else taminColors.blueText

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(item.colors.itemBackgroundColor)
            .then(
                if (item.itemBorder != null) Modifier.border(item.itemBorder) else Modifier
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    enabled = item.enabled && item.onClick != null,
                    onClick = { item.onClick?.invoke() }
                )
                .alpha(if (item.enabled) 1f else taminColors.disabledAlpha)
                .padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading Icon or Text
            if (item.leadingIconPainter != null || !item.leadingText.isNullOrBlank()) {
                val baseModifier = Modifier
                    .size(item.leadingIconContainerSize)
                    .then(
                        if (item.leadingIconElevation > 0.dp) {
                            Modifier.shadow(item.leadingIconElevation, item.leadingIconShape)
                        } else Modifier
                    )

                val boxModifier = if (item.colors.leadingIconBackgroundGradient != null) {
                    baseModifier
                        .background(item.colors.leadingIconBackgroundGradient, item.leadingIconShape)
                        .background(
                            brush = taminColors.iconGlassShine,
                            shape = item.leadingIconShape
                        )
                        .border(
                            width = 1.dp,
                            brush = taminColors.iconGlassBorder,
                            shape = item.leadingIconShape
                        )
                } else {
                    baseModifier
                        .clip(item.leadingIconShape)
                        .background(resolvedIconBgColor)
                        .then(
                            if (item.leadingIconBorder != null) Modifier.border(item.leadingIconBorder, item.leadingIconShape) else Modifier
                        )
                }

                Box(
                    modifier = boxModifier,
                    contentAlignment = Alignment.Center
                ) {
                    if (item.leadingIconPainter != null) {
                        Icon(
                            painter = item.leadingIconPainter,
                            contentDescription = item.title,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(item.leadingIconPadding),
                            tint = resolvedIconTintColor
                        )
                    } else if (!item.leadingText.isNullOrBlank()) {
                        Text(
                            text = item.leadingText,
                            color = resolvedIconTintColor,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                Spacer(modifier = Modifier.width(Spacing.md))
            }

            // Center Text
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.title,
                    style = titleStyle,
                    color = resolvedTitleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!item.subtitle.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = item.subtitle,
                        style = subtitleStyle,
                        color = resolvedSubtitleColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Trailing Content
            if (item.customTrailingContent != null) {
                Spacer(modifier = Modifier.width(Spacing.md))
                item.customTrailingContent.invoke()
            } else if (item.badge != null || item.showArrow) {
                Spacer(modifier = Modifier.width(Spacing.md))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    if (item.badge != null) {
                        val badgeBg = item.badge.backgroundColor ?: taminColors.dangerBg
                        val badgeText = item.badge.textColor ?: taminColors.dangerText
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(CornerRadius.lg))
                                .background(badgeBg)
                                .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.badge.text,
                                style = MaterialTheme.typography.labelMedium,
                                color = badgeText
                            )
                        }
                    }

                    if (item.showArrow) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_show_more),
                            contentDescription = null,
                            tint = taminColors.chevron,
                            modifier = Modifier.size(IconSize.medium)
                        )
                    }
                }
            }
        }

        // Divider Logic
        if (showDivider) {
            val indent = dividerStartIndent ?: if (item.leadingIconPainter != null || !item.leadingText.isNullOrBlank()) {
                item.leadingIconContainerSize + Spacing.md + Spacing.lg
            } else {
                Spacing.lg
            }

            HorizontalDivider(
                modifier = Modifier.padding(start = indent, end = Spacing.lg),
                color = dividerColor,
                thickness = 1.dp
            )
        }
    }
}


@PreviewRtlTheme
@Composable
private fun SettingsListPreviewLight() {
    TaminHamrahTheme(darkTheme = false) {
        SettingsListPreviewContent()
    }
}

@PreviewRtlTheme
@Composable
private fun SettingsListPreviewDark() {
    TaminHamrahTheme(darkTheme = true) {
        SettingsListPreviewContent()
    }
}

@Composable
private fun SettingsListPreviewContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalTaminColors.current.bgPage)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val taminColors = LocalTaminColors.current

        ListGroupView(
            items = persistentListOf(
                ListItemData(
                    title = "تنظیمات پیشرفته",
                    leadingText = "1"
                ),
                ListItemData(
                    title = "تماس با من",
                    subtitle = "شماره تماس و اطلاعات ارتباطی",
                    leadingIconPainter = painterResource(Res.drawable.ic_tamin_logo),
                    colors = ListItemColors(
                        leadingIconBackgroundColor = taminColors.glassB1,
                        leadingIconTintColor = taminColors.textPrimary
                    )
                ),
                ListItemData(
                    title = "اشتراک‌گذاری",
                    leadingIconPainter = painterResource(Res.drawable.ic_tamin_logo),
                    colors = ListItemColors(
                        leadingIconBackgroundGradient = taminColors.iconGradientSecondary,
                        leadingIconTintColor = taminColors.bgIconProfile
                    ),
                    leadingIconElevation = Elevation.md,
                    badge = ListItemBadge(
                        text = "جدید",
                        backgroundColor = taminColors.greenBg,
                        textColor = taminColors.greenText
                    ),
                    showArrow = true
                ),
                ListItemData(
                    title = "تاریخچه نسخه",
                    leadingIconPainter = painterResource(Res.drawable.ic_tamin_logo),
                    colors = ListItemColors(
                        leadingIconBackgroundColor = taminColors.glassB1,
                        leadingIconTintColor = taminColors.textPrimary
                    ),
                    enabled = false
                )
            )
        )

        ListGroupView(
            containerBorder = BorderStroke(1.dp, taminColors.dangerBorder),
            items = persistentListOf(
                ListItemData(
                    title = "خروج از حساب کاربری",
                    leadingIconPainter = painterResource(Res.drawable.ic_tamin_logo),
                    colors = ListItemColors(
                        titleColor = taminColors.dangerText,
                        leadingIconBackgroundColor = taminColors.dangerBg,
                        leadingIconTintColor = taminColors.dangerText
                    ),
                    showArrow = false
                )
            )
        )
    }
}
