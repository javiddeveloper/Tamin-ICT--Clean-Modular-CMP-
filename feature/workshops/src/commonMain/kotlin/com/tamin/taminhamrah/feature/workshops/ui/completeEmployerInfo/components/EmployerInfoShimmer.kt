package com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing

/**
 * High-performance full screen shimmer skeleton for initial loading of Employer Info.
 * Pure theme-driven without hardcoded colors.
 */
@Composable
fun EmployerInfoScreenShimmer(
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(modifier = modifier.fillMaxWidth()) {
        // Hero Area Skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
                .background(colors.heroGradient)
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    ShimmerBlock(
                        modifier = Modifier.size(36.dp),
                        cornerRadius = 14.dp,
                        colorBase = colors.glassIconTileBg,
                        colorHighlight = colors.glassA1,
                    )
                    ShimmerBlock(
                        modifier = Modifier.width(140.dp).height(20.dp),
                        cornerRadius = 6.dp,
                        colorBase = colors.glassIconTileBg,
                        colorHighlight = colors.glassA1,
                    )
                    Spacer(modifier = Modifier.size(36.dp))
                }

                Spacer(modifier = Modifier.height(Spacing.lg))

                // Centered Hero Icon skeleton
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    ShimmerBlock(
                        modifier = Modifier.size(52.dp),
                        cornerRadius = 16.dp,
                        colorBase = colors.glassIconTileBg,
                        colorHighlight = colors.glassA1,
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.sm))

                // Subtitle skeleton
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    ShimmerBlock(
                        modifier = Modifier.width(180.dp).height(14.dp),
                        cornerRadius = 4.dp,
                        colorBase = colors.glassIconTileBg,
                        colorHighlight = colors.glassA1,
                    )
                }

                Spacer(modifier = Modifier.height(34.dp))
            }
        }

        // Overlapping User Card Skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg)
                .offset(y = (-28).dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(CornerRadius.lg),
                        ambientColor = colors.shadowSubtle,
                        spotColor = colors.shadowSubtle,
                    )
                    .clip(RoundedCornerShape(CornerRadius.lg))
                    .background(colors.bgSurface)
                    .border(1.dp, colors.border, RoundedCornerShape(CornerRadius.lg))
                    .padding(horizontal = Spacing.sm, vertical = Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ShimmerBlock(
                        modifier = Modifier.width(100.dp).height(16.dp),
                        cornerRadius = 4.dp,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ShimmerBlock(
                        modifier = Modifier.width(70.dp).height(11.dp),
                        cornerRadius = 3.dp,
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(32.dp)
                        .background(colors.divider),
                )

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ShimmerBlock(
                        modifier = Modifier.width(90.dp).height(16.dp),
                        cornerRadius = 4.dp,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    ShimmerBlock(
                        modifier = Modifier.width(50.dp).height(11.dp),
                        cornerRadius = 3.dp,
                    )
                }
            }
        }

        // Tabs Skeleton
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg)
                .offset(y = (-14).dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.bgSurface)
                    .border(1.dp, colors.border, RoundedCornerShape(16.dp))
                    .padding(5.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                ShimmerBlock(
                    modifier = Modifier.weight(1f).height(40.dp),
                    cornerRadius = 12.dp,
                )
                ShimmerBlock(
                    modifier = Modifier.weight(1f).height(40.dp),
                    cornerRadius = 12.dp,
                )
            }
        }

        // Workshop List Skeletons
        EmployerInfoWorkshopListShimmer()
    }
}

/**
 * Shimmer skeleton for workshop cards list.
 */
@Composable
fun EmployerInfoWorkshopListShimmer(
    itemCount: Int = 3,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        repeat(itemCount) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(18.dp),
                        ambientColor = colors.shadowSubtle,
                        spotColor = colors.shadowSubtle,
                    )
                    .clip(RoundedCornerShape(18.dp))
                    .background(colors.bgSurface)
                    .border(1.dp, colors.border, RoundedCornerShape(18.dp))
                    .padding(13.dp),
            ) {
                // Title + Badge Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    ShimmerBlock(
                        modifier = Modifier.width(160.dp).height(18.dp),
                        cornerRadius = 4.dp,
                    )
                    ShimmerBlock(
                        modifier = Modifier.width(60.dp).height(20.dp),
                        cornerRadius = 10.dp,
                    )
                }

                Spacer(modifier = Modifier.height(11.dp))

                // 2-column Code & Branch boxes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                ) {
                    ShimmerBlock(
                        modifier = Modifier.weight(1f).height(48.dp),
                        cornerRadius = 12.dp,
                    )
                    ShimmerBlock(
                        modifier = Modifier.weight(1f).height(48.dp),
                        cornerRadius = 12.dp,
                    )
                }

                Spacer(modifier = Modifier.height(9.dp))

                // Details Button
                ShimmerBlock(
                    modifier = Modifier.fillMaxWidth().height(34.dp),
                    cornerRadius = 11.dp,
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Action Button
                ShimmerBlock(
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    cornerRadius = 14.dp,
                )
            }
        }
    }
}

/**
 * Shimmer card shown inline when legal workshop inquiry is in flight.
 */
@Composable
fun EmployerInfoLegalInquiryShimmer(
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(13.dp))
            .background(colors.blueBg)
            .border(1.dp, colors.hawkesBlue, RoundedCornerShape(13.dp))
            .padding(horizontal = 11.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ShimmerBlock(
            modifier = Modifier.width(80.dp).height(14.dp),
            cornerRadius = 4.dp,
            colorBase = colors.blueBg,
            colorHighlight = colors.bgSurface,
        )
        ShimmerBlock(
            modifier = Modifier.weight(1f).height(14.dp),
            cornerRadius = 4.dp,
            colorBase = colors.blueBg,
            colorHighlight = colors.bgSurface,
        )
    }
}

/**
 * Shimmer card shown inline when CEO inquiry is in flight.
 */
@Composable
fun EmployerInfoCeoInquiryShimmer(
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(13.dp))
            .background(colors.greenBg)
            .border(1.dp, colors.greenBorder, RoundedCornerShape(13.dp))
            .padding(horizontal = 11.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ShimmerBlock(
            modifier = Modifier.width(90.dp).height(14.dp),
            cornerRadius = 4.dp,
            colorBase = colors.greenBg,
            colorHighlight = colors.bgSurface,
        )
        ShimmerBlock(
            modifier = Modifier.weight(1f).height(14.dp),
            cornerRadius = 4.dp,
            colorBase = colors.greenBg,
            colorHighlight = colors.bgSurface,
        )
    }
}

/**
 * Shimmer rows shown inside the bottom sheet picker when items are loading.
 */
@Composable
fun EmployerInfoSheetShimmer(
    itemCount: Int = 5,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        repeat(itemCount) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.xl, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ShimmerBlock(
                    modifier = Modifier.fillMaxWidth(0.6f).height(18.dp),
                    cornerRadius = 4.dp,
                )
            }
        }
    }
}
