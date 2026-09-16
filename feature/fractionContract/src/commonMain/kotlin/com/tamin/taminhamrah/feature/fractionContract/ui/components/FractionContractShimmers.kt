package com.tamin.taminhamrah.feature.fractionContract.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.TaminBottomBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.HeaderDecoration
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.ShimmerSize
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.ui.theme.TaminOnAccentInkFaint

@Composable
internal fun FractionContractScreenShimmer(
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val profileGradientBrush = Brush.horizontalGradient(taminColors.profileGradientStops)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TaminTopAppBar(
                title = "",
                background = profileGradientBrush,
                bottomPadding = Spacing.xl,
                shape = RoundedCornerShape(
                    bottomStart = CornerRadius.x3l,
                    bottomEnd = CornerRadius.x3l,
                ),
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    DecorativeBackgroundCircle(
                        size = HeaderDecoration.circleSize,
                        xOffset = HeaderDecoration.circleXOffset,
                        yOffset = HeaderDecoration.circleYOffset,
                    )
                    FractionContractIdentityHeaderShimmer()
                }
            }
        },
        bottomBar = {
            FractionContractBottomBarShimmer()
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.page, vertical = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            FractionContractStepperShimmer()
            FractionEligibilityStepShimmer()
        }
    }
}

@Composable
internal fun FractionContractIdentityHeaderShimmer(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        ShimmerBlock(
            modifier = Modifier.size(IconSize.tile),
            cornerRadius = CornerRadius.iconTile,
            colorBase = TaminOnAccentInkFaint,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            ShimmerBlock(
                modifier = Modifier
                    .width(ShimmerSize.titleWidth)
                    .height(ShimmerSize.badgeHeight),
                colorBase = TaminOnAccentInkFaint,
            )
            ShimmerBlock(
                modifier = Modifier
                    .width(ShimmerSize.subtitleWidth)
                    .height(ShimmerSize.subtitleHeight),
                colorBase = TaminOnAccentInkFaint,
            )
            ShimmerBlock(
                modifier = Modifier
                    .width(ShimmerSize.hintWidth)
                    .height(ShimmerSize.subtitleHeight),
                colorBase = TaminOnAccentInkFaint,
            )
        }
    }
}

@Composable
internal fun FractionContractStepperShimmer(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(4) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ShimmerBlock(
                    modifier = Modifier.size(IconSize.stepperCircle),
                    cornerRadius = CornerRadius.full,
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
                ShimmerBlock(
                    modifier = Modifier
                        .width(ShimmerSize.chipWidth)
                        .height(ShimmerSize.subtitleHeight),
                )
            }
        }
    }
}

@Composable
internal fun FractionEligibilityStepShimmer(
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val cardShape = RoundedCornerShape(CornerRadius.cardCompact)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        ShimmerBlock(
            modifier = Modifier
                .width(ShimmerSize.titleWidth)
                .height(ShimmerSize.badgeHeight),
        )
        ShimmerBlock(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(ShimmerSize.subtitleHeight),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(colors.bgSurface)
                .border(Thickness.border, colors.border, cardShape)
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            repeat(3) {
                FractionDetailRowShimmer()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(colors.bgSurface)
                .border(Thickness.border, colors.border, cardShape)
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            ShimmerBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ShimmerSize.bannerHeight),
                cornerRadius = CornerRadius.listRow,
            )
            repeat(3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ShimmerBlock(
                        modifier = Modifier.size(IconSize.small),
                        cornerRadius = CornerRadius.full,
                    )
                    ShimmerBlock(
                        modifier = Modifier
                            .weight(1f)
                            .height(ShimmerSize.subtitleHeight),
                    )
                }
            }
        }
    }
}

@Composable
internal fun FractionPlaceholderStepShimmer(
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val cardShape = RoundedCornerShape(CornerRadius.cardCompact)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.page, vertical = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        ShimmerBlock(
            modifier = Modifier
                .width(ShimmerSize.titleWidth)
                .height(ShimmerSize.badgeHeight),
        )
        ShimmerBlock(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(ShimmerSize.subtitleHeight),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(colors.bgSurface)
                .border(Thickness.border, colors.border, cardShape)
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            repeat(4) {
                ShimmerBlock(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ShimmerSize.fieldHeight),
                    cornerRadius = CornerRadius.lg,
                )
            }
        }
    }
}

@Composable
internal fun FractionContractBottomBarShimmer(
    modifier: Modifier = Modifier,
) {
    TaminBottomBar(modifier = modifier) {
        ShimmerBlock(
            modifier = Modifier
                .fillMaxWidth()
                .height(ShimmerSize.fieldHeight),
            cornerRadius = CornerRadius.md,
        )
    }
}

@Composable
private fun FractionDetailRowShimmer() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ShimmerBlock(
            modifier = Modifier
                .width(ShimmerSize.labelWidth)
                .height(ShimmerSize.subtitleHeight),
        )
        ShimmerBlock(
            modifier = Modifier
                .width(ShimmerSize.hintWidth)
                .height(ShimmerSize.valueHeight),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun FractionContractScreenShimmerPreview() {
    PreviewRtlThemeContent {
        FractionContractScreenShimmer()
    }
}

@PreviewRtlTheme
@Composable
private fun FractionContractScreenShimmerDarkPreview() {
    PreviewRtlThemeContent(darkTheme = true) {
        FractionContractScreenShimmer()
    }
}

@PreviewRtlTheme
@Composable
private fun FractionEligibilityStepShimmerPreview() {
    PreviewRtlThemeContent {
        FractionEligibilityStepShimmer(modifier = Modifier.padding(Spacing.page))
    }
}

@PreviewRtlTheme
@Composable
private fun FractionPlaceholderStepShimmerPreview() {
    PreviewRtlThemeContent {
        FractionPlaceholderStepShimmer()
    }
}
