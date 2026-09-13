package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.shimmer

/**
 * A list of card-shaped placeholders, for a page whose rows all measure the same.
 *
 * A skeleton exists to stop the layout jumping when the data lands, so the row height is the
 * caller's to state — it has to match the card it stands in for. A screen whose rows differ from
 * each other still draws its own.
 */
@Composable
fun ShimmerRows(
    rowHeight: Dp,
    modifier: Modifier = Modifier,
    rowCount: Int = DEFAULT_ROWS,
    spacing: Dp = Spacing.md,
    corner: Dp = CornerRadius.lg,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing),
    ) {
        repeat(rowCount) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(rowHeight)
                    .taminSurface(corner)
                    .shimmer(),
            )
        }
    }
}

/** Enough to fill a phone screen without measuring it. */
private const val DEFAULT_ROWS = 6
