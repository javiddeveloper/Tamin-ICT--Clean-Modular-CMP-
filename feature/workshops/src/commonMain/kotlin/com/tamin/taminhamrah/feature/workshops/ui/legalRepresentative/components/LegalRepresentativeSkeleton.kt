package com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.ShimmerSize
import com.tamin.taminhamrah.ui.theme.Spacing

private const val WORKSHOP_PLACEHOLDER_CARDS = 3
private const val REPRESENTATIVE_PLACEHOLDER_CARDS = 3
private const val CONTRACT_PLACEHOLDER_ROWS = 4

/**
 * Stands in for the workshop list while [LegalRepresentativeWorkshopsViewModel][com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.workshops.LegalRepresentativeWorkshopsViewModel]
 * loads. Shaped like the real workshop card (same [taminSurface] outline, same row layout) so
 * nothing jumps when the real cards land.
 */
@Composable
fun LegalRepresentativeWorkshopsSkeleton(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        items(WORKSHOP_PLACEHOLDER_CARDS) {
            LegalRepresentativeWorkshopCardSkeleton()
        }
    }
}

@Composable
private fun LegalRepresentativeWorkshopCardSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .taminSurface()
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ShimmerBlock(modifier = Modifier.width(ShimmerSize.titleWidth).height(20.dp))
            ShimmerBlock(modifier = Modifier.width(64.dp).height(24.dp), cornerRadius = CornerRadius.full)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            ShimmerBlock(modifier = Modifier.weight(1f).height(54.dp), cornerRadius = CornerRadius.md)
            ShimmerBlock(modifier = Modifier.weight(1f).height(54.dp), cornerRadius = CornerRadius.md)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            ShimmerBlock(modifier = Modifier.width(ShimmerSize.subtitleWidth).height(ShimmerSize.subtitleHeight))
            ShimmerBlock(modifier = Modifier.width(96.dp).height(32.dp), cornerRadius = CornerRadius.lg)
        }
    }
}

/**
 * Stands in for a workshop's representative list while it loads. Shaped like the real
 * `LegalRepresentativeCard` (name/mobile block, access-level row, action-buttons row).
 */
@Composable
fun LegalRepresentativeListSkeleton(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        items(REPRESENTATIVE_PLACEHOLDER_CARDS) {
            LegalRepresentativeCardSkeleton()
        }
    }
}

@Composable
private fun LegalRepresentativeCardSkeleton() {
    Column(modifier = Modifier.fillMaxWidth().taminSurface().padding(Spacing.lg)) {
        ShimmerBlock(modifier = Modifier.width(ShimmerSize.titleWidth).height(ShimmerSize.titleHeight))
        Spacer(Modifier.height(Spacing.xs))
        ShimmerBlock(modifier = Modifier.width(ShimmerSize.valueWidth).height(ShimmerSize.subtitleHeight))

        Spacer(Modifier.height(Spacing.md))
        ShimmerBlock(modifier = Modifier.fillMaxWidth().height(48.dp), cornerRadius = CornerRadius.md)

        Spacer(Modifier.height(Spacing.md))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            ShimmerBlock(modifier = Modifier.weight(1f).height(44.dp), cornerRadius = CornerRadius.chip)
            ShimmerBlock(modifier = Modifier.weight(0.5f).height(44.dp), cornerRadius = CornerRadius.chip)
        }
    }
}

/**
 * Stands in for the "special" workshop's contract-row picker while it loads. Shaped like the real
 * `ContractRow` (checkbox + two-line label), minus the checkbox's interactivity.
 */
@Composable
fun LegalRepresentativeContractsSkeleton(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        repeat(CONTRACT_PLACEHOLDER_ROWS) {
            LegalRepresentativeContractRowSkeleton()
        }
    }
}

@Composable
private fun LegalRepresentativeContractRowSkeleton() {
    val taminColors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(taminColors.bgSurface, RoundedCornerShape(CornerRadius.xl))
            .border(1.dp, taminColors.border, RoundedCornerShape(CornerRadius.xl))
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        ShimmerBlock(modifier = Modifier.size(24.dp), cornerRadius = CornerRadius.xs)
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
            ShimmerBlock(modifier = Modifier.width(160.dp).height(ShimmerSize.subtitleHeight))
            ShimmerBlock(modifier = Modifier.width(70.dp).height(ShimmerSize.valueHeight))
        }
    }
}
