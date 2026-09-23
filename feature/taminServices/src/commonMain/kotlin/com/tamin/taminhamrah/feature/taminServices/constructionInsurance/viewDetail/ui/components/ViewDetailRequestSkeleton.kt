package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.viewDetail.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.ShimmerSize
import com.tamin.taminhamrah.ui.theme.Spacing

private val SectionShadowBlur = 26.dp
private val SectionShadowOffsetY = 10.dp

/** Row counts per placeholder card — just enough visual variety to read as two distinct sections. */
private val SKELETON_ROW_COUNTS = listOf(5, 4)

/**
 * Loading placeholder for [com.tamin.taminhamrah.feature.taminServices.constructionInsurance.viewDetail.ui.ViewDetailRequestScreen],
 * shaped exactly like the loaded `ExpandableDetailCard`s it stands in for — same shadow, corner
 * radius and header row — the way `ConstructionInsuranceListSkeleton` mirrors `ConstructionFileCard`
 * for the list screen. Two cards, matching اطلاعات درخواست + اطلاعات محاسبه — the hero summary
 * card renders separately, inside the top bar, not as part of this list placeholder.
 */
@Composable
fun ViewDetailRequestSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        SKELETON_ROW_COUNTS.forEach { _ ->
            ExpandableDetailCardSkeleton()
        }
    }
}

@Composable
private fun ExpandableDetailCardSkeleton(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    val cardShape = RoundedCornerShape(CornerRadius.lg)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .coloredShadow(
                color = colors.shadowSubtle,
                borderRadius = CornerRadius.card,
                blurRadius = SectionShadowBlur,
                offsetY = SectionShadowOffsetY,
            )
            .background(colors.bgSurface, cardShape)
            .border(1.dp, colors.border, cardShape),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ShimmerBlock(modifier = Modifier.width(ShimmerSize.titleWidth).height(18.dp), cornerRadius = CornerRadius.xs)
            ShimmerBlock(modifier = Modifier.size(Spacing.lg), cornerRadius = CornerRadius.xs)
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ViewDetailRequestSkeletonPreviewLight() {
    PreviewRtlThemeContent {
        ViewDetailRequestSkeleton(modifier = Modifier.padding(Spacing.lg))
    }
}

@PreviewRtlTheme
@Composable
private fun ViewDetailRequestSkeletonPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        ViewDetailRequestSkeleton(modifier = Modifier.padding(Spacing.lg))
    }
}
