package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.paymentSheet.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

/** Shaped like the loaded [PaymentSheetSummaryCard] — three stacked label/value rows. */
@Composable
private fun PaymentSheetSummaryCardSkeleton(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .coloredShadow(
                color = colors.shadowSubtle,
                borderRadius = CornerRadius.card,
                blurRadius = 20.dp,
                offsetY = 8.dp,
            )
            .taminSurface()
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        repeat(3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ShimmerBlock(modifier = Modifier.width(96.dp).height(14.dp), cornerRadius = CornerRadius.sm)
                ShimmerBlock(modifier = Modifier.width(110.dp).height(16.dp), cornerRadius = CornerRadius.sm)
            }
        }
    }
}

/** Shaped like the loaded [PaymentSheetCard] — title/pill header, two info tiles, an amount row. */
@Composable
private fun PaymentSheetCardSkeleton(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .coloredShadow(
                color = colors.shadowSubtle,
                borderRadius = CornerRadius.card,
                blurRadius = 20.dp,
                offsetY = 8.dp,
            )
            .taminSurface()
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ShimmerBlock(
                modifier = Modifier.width(140.dp).height(18.dp),
                cornerRadius = CornerRadius.sm
            )
            ShimmerBlock(
                modifier = Modifier.width(72.dp).height(24.dp),
                cornerRadius = CornerRadius.chip
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            ShimmerBlock(modifier = Modifier.weight(1f).height(48.dp), cornerRadius = CornerRadius.xl)
            ShimmerBlock(modifier = Modifier.weight(1f).height(48.dp), cornerRadius = CornerRadius.xl)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ShimmerBlock(
                modifier = Modifier.width(48.dp).height(14.dp),
                cornerRadius = CornerRadius.sm
            )
            ShimmerBlock(
                modifier = Modifier.width(100.dp).height(16.dp),
                cornerRadius = CornerRadius.sm
            )
        }
    }
}

private const val PLACEHOLDER_CARDS = 3


@Composable
fun PaymentSheetSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.page, vertical = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        PaymentSheetSummaryCardSkeleton()
        ShimmerBlock(modifier = Modifier.width(150.dp).height(18.dp), cornerRadius = CornerRadius.sm)
        repeat(PLACEHOLDER_CARDS) {
            PaymentSheetCardSkeleton()
        }
    }
}

@PreviewRtlTheme
@Composable
private fun PaymentSheetSkeletonPreviewLight() {
    PreviewRtlThemeContent {
        PaymentSheetSkeleton(modifier = Modifier.padding(Spacing.lg))
    }
}

@PreviewRtlTheme
@Composable
private fun PaymentSheetSkeletonPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        PaymentSheetSkeleton(modifier = Modifier.padding(Spacing.lg))
    }
}
