package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.DarkTaminColors
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerSize
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.shimmer
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.home_chip_darman_covered
import taminx.core.core_ui.home_chip_darman_uncovered
import taminx.core.core_ui.home_chip_relation_active
import taminx.core.core_ui.home_chip_relation_inactive
import taminx.core.core_ui.home_header_shield_cd
import taminx.core.core_ui.home_header_support_cd
import taminx.core.core_ui.home_header_title
import taminx.core.core_ui.ic_support
import taminx.core.core_ui.ic_tamin_shield_check

/**
 * A lighter blue than the shared `heroGradient` (`Primary700..Primary900`), which reads too dark
 * across a surface this large. Bespoke to the home header; the shared token is unchanged for the
 * other hero surfaces that use it.
 */
private val HomeHeaderGradient = Brush.linearGradient(
    listOf(Color(0xFF3E72D6), Color(0xFF1F3E86)),
)

/**
 * The home screen's gradient header: a support / shield icon row over the app title, the user's
 * name and two data-backed status chips.
 *
 * Draws full-bleed behind the status bar — the caller is expected to hand it a full-width slot;
 * the status-bar inset is added here. All nullable inputs render nothing (or a shimmer for the
 * name) until their backing call resolves, so the header never shows a guessed value.
 *
 * The AI ask-bar is composed *over* this header's bottom edge by the caller (so it straddles the
 * gradient the way the profile screen's status card does), not drawn here.
 */
@Composable
fun HomeHeader(
    fullName: String?,
    hasDarmanCoverage: Boolean?,
    hasActiveRelation: Boolean?,
    modifier: Modifier = Modifier,
    onSupportClick: () -> Unit = {},
    onShieldClick: () -> Unit = {},
) {
    val colors = LocalTaminColors.current
    val isDark = colors == DarkTaminColors

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = CornerRadius.x3l, bottomEnd = CornerRadius.x3l))
            .background(if (isDark) colors.heroGradient else HomeHeaderGradient)
            .statusBarsPadding()
            .padding(horizontal = Spacing.lg)
            // Bottom room so the status chips clear the ask-bar the caller pulls up over this edge.
            .padding(top = Spacing.sm, bottom = Spacing.xxxl),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HeaderIconButton(
                painter = painterResource(Res.drawable.ic_tamin_shield_check),
                contentDescription = stringResource(Res.string.home_header_shield_cd),
                onClick = onShieldClick,
            )
            Text(
                text = stringResource(Res.string.home_header_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colors.onGradient,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Spacing.sm),
            )
            HeaderIconButton(
                painter = painterResource(Res.drawable.ic_support),
                contentDescription = stringResource(Res.string.home_header_support_cd),
                onClick = onSupportClick,
            )
        }

        Box(modifier = Modifier.padding(top = Spacing.xs)) {
            Text(
                text = fullName ?: "نام و نام خانوادگی",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = if (fullName == null) Color.Transparent else colors.onGradient,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            if (fullName == null) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(CornerRadius.x2l))
                        .shimmer(
                            colorBase = Color.White.copy(alpha = 0.14f),
                            colorHighlight = Color.White.copy(alpha = 0.32f),
                        )
                )
            }
        }

        val darmanLabel = when (hasDarmanCoverage) {
            true -> stringResource(Res.string.home_chip_darman_covered)
            false -> stringResource(Res.string.home_chip_darman_uncovered)
            null -> null
        }
        val relationLabel = when (hasActiveRelation) {
            true -> stringResource(Res.string.home_chip_relation_active)
            false -> stringResource(Res.string.home_chip_relation_inactive)
            null -> null
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            when (hasActiveRelation) {
                true, false -> relationLabel?.let { HeaderStatusChip(text = it, dot = true, isPositive = hasActiveRelation) }
                null -> HeaderStatusChip(text = stringResource(Res.string.home_chip_relation_active), dot = true, isPositive = true, isLoading = true)
            }
            when (hasDarmanCoverage) {
                true, false -> darmanLabel?.let { HeaderStatusChip(text = it, dot = false, isPositive = hasDarmanCoverage) }
                null -> HeaderStatusChip(text = stringResource(Res.string.home_chip_darman_covered), dot = false, isPositive = true, isLoading = true)
            }
        }
    }
}

@Composable
private fun HeaderIconButton(
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(IconSize.badge)
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(Color.White.copy(alpha = 0.14f))
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(CornerRadius.lg),
                color = LocalTaminColors.current.glassIconTileBorder
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClickLabel = contentDescription,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            tint = LocalTaminColors.current.onGradient,
            modifier = Modifier.size(IconSize.small),
        )
    }
}

@Composable
private fun HeaderStatusChip(text: String, dot: Boolean, isPositive: Boolean, isLoading: Boolean = false) {
    val toneColor = if (isPositive) LocalTaminColors.current.greenText else LocalTaminColors.current.dangerText
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(CornerRadius.max))
            .background(Color.White.copy(alpha = 0.14f))
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(CornerRadius.max),
                color = LocalTaminColors.current.glassIconTileBorder
            )
            .then(
                if (isLoading) Modifier.shimmer(
                    colorBase = Color.White.copy(alpha = 0.14f),
                    colorHighlight = Color.White.copy(alpha = 0.32f),
                ) else Modifier
            )
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        if (dot) {
            Box(
                modifier = Modifier
                    .size(Spacing.sm)
                    .clip(CircleShape)
                    .background(if (isLoading) Color.Transparent else toneColor),
            )
        } else {
            Icon(
                painter = painterResource(Res.drawable.ic_tamin_shield_check),
                contentDescription = null,
                tint = if (isLoading) Color.Transparent else toneColor,
                modifier = Modifier.size(IconSize.small),
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (isLoading) Color.Transparent else LocalTaminColors.current.onGradient,
            maxLines = 1,
        )
    }
}



@PreviewRtlTheme
@Composable
private fun HomeHeaderPreview() {
    PreviewRtlThemeContent {
        HomeHeader(
            fullName = "سنا حقیقی",
            hasDarmanCoverage = true,
            hasActiveRelation = true,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun HomeHeaderLoadingPreview() {
    PreviewRtlThemeContent(darkTheme = true) {
        HomeHeader(
            fullName = null,
            hasDarmanCoverage = null,
            hasActiveRelation = null,
        )
    }
}
