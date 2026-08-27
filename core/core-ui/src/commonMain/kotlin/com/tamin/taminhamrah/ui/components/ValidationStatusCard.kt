package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.ShimmerSize
import com.tamin.taminhamrah.ui.theme.Spacing
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import org.jetbrains.compose.resources.painterResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_check
import taminx.core.core_ui.ic_tamin_cross

import com.tamin.taminhamrah.ui.theme.DarkTaminColors

@Composable
fun ValidationStatusCard(
    hazeState: HazeState,
    title: String,
    subtitle: String,
    badgeText: String,
    modifier: Modifier = Modifier,
    isValid: Boolean = true,
    isLoading: Boolean = false,
) {
    val colors = LocalTaminColors.current
    val isDark = colors == DarkTaminColors

    val cardGlassGradient = colors.validationCardGradient
    val cardBorderColor = if (isDark) {
        if (isValid) colors.greenBorder else colors.dangerBorder
    } else {
        colors.glassBorder
    }
    val cardShadowColor = colors.shadowPrimary

    Box(
        modifier = modifier
            .fillMaxWidth()
            .coloredShadow(
                color = cardShadowColor,
                borderRadius = CornerRadius.iconTile,
                blurRadius = Elevation.xxl,
                offsetY = Spacing.md,
            )
            .clip(RoundedCornerShape(CornerRadius.iconTile))
            .background(cardGlassGradient)
            .hazeEffect(
                state = hazeState,
                style = HazeStyle(
                    noiseFactor = 0.02f,
                    tint =  HazeTint(
                        color = colors.bgPage.copy(alpha = 0.8f),
                        blendMode = BlendMode.Luminosity
                    ),
                    blurRadius = 32.dp
                )
            )
            .border(0.5.dp, cardBorderColor, RoundedCornerShape(CornerRadius.iconTile))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page, vertical = Spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            if (isLoading) {
                ShimmerBlock(
                    modifier = Modifier.size(IconSize.largePlus),
                    cornerRadius = CornerRadius.listRow,
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
                ) {
                    ShimmerBlock(modifier = Modifier.width(ShimmerSize.titleWidth).height(ShimmerSize.titleHeight))
                    ShimmerBlock(modifier = Modifier.width(ShimmerSize.subtitleWidth).height(ShimmerSize.subtitleHeight))
                }

                ShimmerBlock(
                    modifier = Modifier.width(ShimmerSize.badgeWidth).height(ShimmerSize.badgeHeight),
                    cornerRadius = CornerRadius.full,
                )
            } else {
                StatusIconButton(isValid = isValid)

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                        )
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Normal,
                            color = colors.textSecondary,
                        )
                    )
                }
                StatusBadge(text = badgeText, isValid = isValid)
            }
        }
    }
}

@Composable
private fun StatusIconButton(isValid: Boolean) {
    val colors = LocalTaminColors.current
    val iconGradient = if (isValid) colors.iconGradientSuccess else colors.iconGradientDanger
    val iconShadowColor = if (isValid) colors.greenText.copy(alpha = 0.28f) else colors.dangerText.copy(alpha = 0.28f)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .coloredShadow(
                color = iconShadowColor,
                borderRadius = CornerRadius.listRow,
                blurRadius = Spacing.md,
                offsetY = Elevation.smPlus,
            )
            .size(IconSize.largePlus)
            .clip(RoundedCornerShape(CornerRadius.listRow))
            .background(iconGradient),
    ) {
        Icon(
            painter = painterResource(if (isValid) Res.drawable.ic_check else Res.drawable.ic_tamin_cross),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(IconSize.medium),
        )
    }
}

@Composable
private fun StatusBadge(text: String, isValid: Boolean) {
    val colors = LocalTaminColors.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(CornerRadius.full))
            .background(if (isValid) colors.greenBg else colors.dangerText.copy(alpha = 0.1f))
            .padding(horizontal = Spacing.cardGap, vertical = Spacing.badgeVertical),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = if (isValid) colors.greenText else colors.dangerText,
            )
        )
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun ValidationStatusCardPreview() {
    PreviewRtlThemeContent {
        val hazeState = remember { HazeState(initialBlurEnabled = true) }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LocalTaminColors.current.bgPage)
                .hazeSource(state = hazeState)
                .padding(Spacing.xl)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                ValidationStatusCard(
                    title = "نام نویسی شده",
                    subtitle = "حساب شما تأیید و فعال است",
                    badgeText = "معتبر",
                    hazeState = hazeState,
                    isValid = true
                )
                ValidationStatusCard(
                    title = "نامعتبر",
                    subtitle = "حساب شما نیاز به بررسی دارد",
                    badgeText = "نامعتبر",
                    hazeState = hazeState,
                    isValid = false
                )
                ValidationStatusCard(
                    title = "",
                    subtitle = "",
                    badgeText = "",
                    hazeState = hazeState,
                    isLoading = true
                )
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ValidationStatusCardPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        val hazeState = remember { HazeState(initialBlurEnabled = true) }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LocalTaminColors.current.bgPage)
                .hazeSource(state = hazeState)
                .padding(Spacing.xl)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                ValidationStatusCard(
                    title = "نام نویسی شده",
                    subtitle = "حساب شما تأیید و فعال است",
                    badgeText = "معتبر",
                    hazeState = hazeState,
                    isValid = true
                )
                ValidationStatusCard(
                    title = "نامعتبر",
                    subtitle = "حساب شما نیاز به بررسی دارد",
                    badgeText = "نامعتبر",
                    hazeState = hazeState,
                    isValid = false
                )
            }
        }
    }
}
