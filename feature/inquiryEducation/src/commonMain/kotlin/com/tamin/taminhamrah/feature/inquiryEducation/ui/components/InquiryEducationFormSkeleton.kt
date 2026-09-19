package com.tamin.taminhamrah.feature.inquiryEducation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.ShimmerSize
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness

private const val SON_CARD_PLACEHOLDER_COUNT = 2

/**
 * Form-shaped shimmer shown while sons are loading, so the layout does not jump when data lands.
 */
@Composable
fun InquiryEducationFormSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        InfoSurfaceSkeleton()
        SonSectionSkeleton()
        EducationCodeSkeleton()
        Spacer(modifier = Modifier.height(Spacing.xl))
    }
}

@Composable
private fun InfoSurfaceSkeleton(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.listRow)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.blueBg, shape)
            .border(Thickness.border, colors.blueText.copy(alpha = 0.2f), shape)
            .padding(Spacing.smd),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.Top,
        ) {
            ShimmerBlock(
                modifier = Modifier.size(IconSize.medium),
                cornerRadius = CornerRadius.full,
            )
            ShimmerBlock(
                modifier = Modifier
                    .weight(1f)
                    .height(ShimmerSize.infoBodyHeight),
                cornerRadius = CornerRadius.sm,
            )
        }
        ShimmerBlock(
            modifier = Modifier
                .fillMaxWidth()
                .height(ShimmerSize.copyRowHeight),
            cornerRadius = CornerRadius.md,
        )
    }
}

@Composable
private fun SonSectionSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ShimmerBlock(
                modifier = Modifier
                    .width(ShimmerSize.sectionLabelWidth)
                    .height(ShimmerSize.valueHeight),
            )
            ShimmerBlock(
                modifier = Modifier
                    .width(ShimmerSize.hintWidth)
                    .height(ShimmerSize.valueHeight),
            )
        }
        repeat(SON_CARD_PLACEHOLDER_COUNT) {
            SonCardSkeleton()
        }
    }
}

@Composable
private fun SonCardSkeleton(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.lg)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(ShimmerSize.sonCardHeight)
            .clip(shape)
            .background(colors.bgSurface, shape)
            .border(Thickness.border, colors.border, shape)
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        ShimmerBlock(
            modifier = Modifier.size(IconSize.medium),
            cornerRadius = CornerRadius.full,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ShimmerBlock(
                    modifier = Modifier
                        .width(ShimmerSize.titleWidth)
                        .height(ShimmerSize.valueHeight),
                )
                ShimmerBlock(
                    modifier = Modifier
                        .width(ShimmerSize.chipWidth)
                        .height(IconSize.medium),
                    cornerRadius = CornerRadius.chip,
                )
            }
            ShimmerBlock(
                modifier = Modifier
                    .width(ShimmerSize.hintWidth)
                    .height(ShimmerSize.valueHeight),
            )
        }
    }
}

@Composable
private fun EducationCodeSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        ShimmerBlock(
            modifier = Modifier
                .width(ShimmerSize.titleWidth)
                .height(ShimmerSize.valueHeight),
        )
        ShimmerBlock(
            modifier = Modifier
                .fillMaxWidth()
                .height(ShimmerSize.fieldHeight),
            cornerRadius = CornerRadius.lg,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            ShimmerBlock(
                modifier = Modifier
                    .width(ShimmerSize.helperLineWidth)
                    .height(ShimmerSize.valueHeight),
            )
            ShimmerBlock(
                modifier = Modifier
                    .width(ShimmerSize.labelWidth)
                    .height(ShimmerSize.valueHeight),
            )
        }
    }
}
