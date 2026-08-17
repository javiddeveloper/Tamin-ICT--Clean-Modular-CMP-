package com.tamin.taminhamrah.feature.history.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.history.ui.model.YearHistoryPR
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import taminx.feature.history.Res
import taminx.feature.history.history_combined_year_complete
import taminx.feature.history.history_combined_year_days
import taminx.feature.history.history_combined_year_partial

private val BorderWidth = 1.dp

/**
 * One insurance year: the year, how many days of it were insured, and whether that is all of it.
 *
 * [onClick] takes no argument on purpose — the caller already holds the year it drew, so the row
 * cannot hand the sheet a position that has drifted from the merged list.
 */
@Composable
fun YearHistoryCard(
    year: YearHistoryPR,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val complete = year.isComplete

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.bgSurface)
            .border(BorderWidth, colors.border, RoundedCornerShape(CornerRadius.lg))
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            NumericText(
                text = year.year.toPersianDigits(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
            )
            Text(
                text = stringResource(
                    Res.string.history_combined_year_days,
                    year.totalDays.toString().toPersianDigits(),
                ),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(CornerRadius.chip))
                .background(if (complete) colors.greenBg else colors.orangeBg)
                .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        ) {
            Text(
                text = stringResource(
                    if (complete) Res.string.history_combined_year_complete
                    else Res.string.history_combined_year_partial,
                ),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (complete) colors.greenText else colors.orangeText,
            )
        }
    }
}
