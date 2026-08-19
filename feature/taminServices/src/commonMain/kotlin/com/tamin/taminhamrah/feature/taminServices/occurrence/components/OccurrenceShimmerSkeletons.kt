package com.tamin.taminhamrah.feature.taminServices.occurrence.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing

@Composable
private fun ShimmerFormField(modifier: Modifier = Modifier, fieldHeight: Dp = 50.dp) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        ShimmerBlock(modifier = Modifier.width(80.dp).height(12.dp), cornerRadius = 4.dp)
        ShimmerBlock(modifier = Modifier.fillMaxWidth().height(fieldHeight), cornerRadius = 13.dp)
    }
}

@Composable
private fun ShimmerGridItem(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        ShimmerBlock(modifier = Modifier.width(70.dp).height(10.dp), cornerRadius = 4.dp)
        ShimmerBlock(modifier = Modifier.width(100.dp).height(14.dp), cornerRadius = 4.dp)
    }
}

/**
 * Page-level shimmer skeleton for Step 1 (person info), shown only while the step's
 * initial data is loading.
 */
@Composable
fun Step1PersonInfoShimmerSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Spacer(modifier = Modifier.height(Spacing.md))

        ShimmerBlock(modifier = Modifier.width(200.dp).height(14.dp), cornerRadius = 4.dp)

        Spacer(modifier = Modifier.height(Spacing.xxs))

        ShimmerBlock(
            modifier = Modifier.fillMaxWidth().height(48.dp),
            cornerRadius = 13.dp,
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        ShimmerFormField()

        Spacer(modifier = Modifier.height(Spacing.lg))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .taminSurface()
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ShimmerBlock(
                    modifier = Modifier.size(IconSize.tile),
                    cornerRadius = IconSize.tile / 2,
                )
                Spacer(modifier = Modifier.width(Spacing.md))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ShimmerBlock(modifier = Modifier.width(120.dp).height(16.dp), cornerRadius = 4.dp)
                    ShimmerBlock(modifier = Modifier.width(90.dp).height(12.dp), cornerRadius = 4.dp)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.lg),
                ) {
                    ShimmerGridItem()
                    ShimmerGridItem()
                    ShimmerGridItem()
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.lg),
                ) {
                    ShimmerGridItem()
                    ShimmerGridItem()
                    ShimmerGridItem()
                }
            }
        }
    }
}

/**
 * Page-level shimmer skeleton for Step 2 (workshop info), shown only while the step's
 * initial data is loading. The workshop-name lookup triggered from selecting a workshop
 * uses its own inline shimmer (see Step2WorkshopStep) instead of this full-page skeleton.
 */
@Composable
fun Step2WorkshopShimmerSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Spacer(modifier = Modifier.height(Spacing.md))

        ShimmerFormField()

        ShimmerBlock(
            modifier = Modifier.fillMaxWidth().height(48.dp),
            cornerRadius = 13.dp,
        )

        ShimmerFormField()
        ShimmerFormField()
        ShimmerFormField()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            ShimmerFormField(modifier = Modifier.weight(1f))
            ShimmerFormField(modifier = Modifier.weight(1f))
        }
    }
}

/**
 * Page-level shimmer skeleton for Step 3 (job details), shown only while the step's
 * initial data is loading.
 */
@Composable
fun Step3JobDetailsShimmerSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Spacer(modifier = Modifier.height(Spacing.md))

        ShimmerBlock(
            modifier = Modifier.fillMaxWidth().height(40.dp),
            cornerRadius = 13.dp,
        )

        Spacer(modifier = Modifier.height(Spacing.xs))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
            ) {
                ShimmerGridItem(modifier = Modifier.weight(1f))
                ShimmerGridItem(modifier = Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
            ) {
                ShimmerGridItem(modifier = Modifier.weight(1f))
                ShimmerGridItem(modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            ShimmerFormField(modifier = Modifier.weight(1f))
            ShimmerFormField(modifier = Modifier.weight(1f))
        }

        ShimmerFormField()
        ShimmerFormField()
    }
}

/**
 * Page-level shimmer skeleton for Step 4 (work hours), shown only while the step's
 * initial data is loading.
 */
@Composable
fun Step4WorkHoursShimmerSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Spacer(modifier = Modifier.height(Spacing.md))

        ShimmerFormField()
        ShimmerFormField()
        ShimmerFormField()
        ShimmerFormField()
        ShimmerFormField()
        ShimmerFormField()
    }
}

/**
 * Page-level shimmer skeleton for Step 5 (accident details), shown only while the step's
 * initial data is loading.
 */
@Composable
fun Step5AccidentShimmerSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Spacer(modifier = Modifier.height(Spacing.md))

        ShimmerFormField()
        ShimmerFormField()
        ShimmerFormField()
        ShimmerFormField()
        ShimmerFormField(fieldHeight = 96.dp)
    }
}
