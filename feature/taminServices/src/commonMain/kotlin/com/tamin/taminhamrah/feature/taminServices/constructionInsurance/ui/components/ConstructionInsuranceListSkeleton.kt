package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing

private const val PLACEHOLDER_CARDS = 3

@Composable
fun ConstructionInsuranceListSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        repeat(PLACEHOLDER_CARDS) {
            ConstructionFileCardSkeleton()
        }
    }
}

@Composable
private fun ConstructionFileCardSkeleton(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .coloredShadow(color = colors.shadowSubtle, borderRadius = CornerRadius.card, blurRadius = 26.dp, offsetY = 10.dp)
            .taminSurface(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xlg)
                .padding(top = Spacing.lg, bottom = Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            ShimmerBlock(modifier = Modifier.weight(1f).height(20.dp), cornerRadius = CornerRadius.sm)
            ShimmerBlock(modifier = Modifier.width(80.dp).height(20.dp), cornerRadius = CornerRadius.chip)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xlg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                ShimmerBlock(modifier = Modifier.weight(1f).height(48.dp), cornerRadius = CornerRadius.xl)
                ShimmerBlock(modifier = Modifier.weight(1f).height(48.dp), cornerRadius = CornerRadius.xl)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                ShimmerBlock(modifier = Modifier.weight(1f).height(48.dp), cornerRadius = CornerRadius.xl)
                ShimmerBlock(modifier = Modifier.weight(2f).height(48.dp), cornerRadius = CornerRadius.xl)
            }

            ShimmerBlock(modifier = Modifier.width(96.dp).height(14.dp), cornerRadius = CornerRadius.sm)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xlg)
                .padding(top = Spacing.md, bottom = Spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            ShimmerBlock(modifier = Modifier.weight(2f).height(44.dp), cornerRadius = CornerRadius.lg)
            ShimmerBlock(modifier = Modifier.weight(1f).height(44.dp), cornerRadius = CornerRadius.lg)
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionInsuranceListSkeletonPreviewLight() {
    PreviewRtlThemeContent {
        ConstructionInsuranceListSkeleton(modifier = Modifier.padding(Spacing.lg))
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionInsuranceListSkeletonPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        ConstructionInsuranceListSkeleton(modifier = Modifier.padding(Spacing.lg))
    }
}
