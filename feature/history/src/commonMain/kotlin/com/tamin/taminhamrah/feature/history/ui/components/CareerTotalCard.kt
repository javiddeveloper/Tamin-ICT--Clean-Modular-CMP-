package com.tamin.taminhamrah.feature.history.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.history.ui.model.CareerTotalPR
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StatColumn
import com.tamin.taminhamrah.ui.components.StatDivider
import com.tamin.taminhamrah.ui.components.StatRowCard
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import taminx.feature.history.Res
import taminx.feature.history.history_combined_stat_days
import taminx.feature.history.history_combined_stat_months
import taminx.feature.history.history_combined_stat_years
import taminx.feature.history.history_combined_total_days

/**
 * How long the person has been insured, as the four figures the service reports.
 *
 * The card itself is `core-ui`'s; this only says which figures go in it. Takes the already
 * normalised [CareerTotalPR] rather than raw counts — the 30-day-month arithmetic belongs in the
 * model, not in a composable that redraws.
 *
 * The day count under the card is `sumHistoryYears`, the service's own figure — the previous app
 * printed it beside the three, and it is the number people quote when they ring the branch, so it
 * is shown rather than left for the reader to multiply out.
 */
@Composable
fun CareerTotalCard(
    total: CareerTotalPR,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        StatRowCard {
            StatColumn(
                value = total.years.toString().toPersianDigits(),
                label = stringResource(Res.string.history_combined_stat_years),
                highlight = true,
                modifier = Modifier.weight(1f),
            )
            StatDivider()
            StatColumn(
                value = total.months.toString().toPersianDigits(),
                label = stringResource(Res.string.history_combined_stat_months),
                modifier = Modifier.weight(1f),
            )
            StatDivider()
            StatColumn(
                value = total.days.toString().toPersianDigits(),
                label = stringResource(Res.string.history_combined_stat_days),
                modifier = Modifier.weight(1f),
            )
        }

        // Nothing to print before the first load answers, and a bare «۰ روز» reads as a real answer.
        if (total.totalDays > 0) {
            // Two texts rather than one interpolated string: NumericText forces LTR so the digits
            // read as printed, and a Persian label inside it would be laid out left-to-right too.
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.history_combined_total_days),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                )
                NumericText(
                    text = total.totalDays.toString().toPersianDigits(),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textPrimary,
                )
                Text(
                    text = stringResource(Res.string.history_combined_stat_days),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                )
            }
        }
    }
}
