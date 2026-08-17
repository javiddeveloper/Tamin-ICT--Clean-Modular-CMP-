package com.tamin.taminhamrah.feature.history.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.history.ui.model.CareerTotalPR
import com.tamin.taminhamrah.ui.components.StatColumn
import com.tamin.taminhamrah.ui.components.StatDivider
import com.tamin.taminhamrah.ui.components.StatRowCard
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import taminx.feature.history.Res
import taminx.feature.history.history_all_stat_days
import taminx.feature.history.history_all_stat_months
import taminx.feature.history.history_all_stat_years

/**
 * How long the person has been insured, as the three figures the service reports.
 *
 * The card itself is `core-ui`'s; this only says which figures go in it. Takes the already
 * normalised [CareerTotalPR] rather than raw counts — the 30-day-month arithmetic belongs in the
 * model, not in a composable that redraws.
 */
@Composable
fun CareerTotalCard(
    total: CareerTotalPR,
    modifier: Modifier = Modifier,
) {
    StatRowCard(modifier = modifier) {
        StatColumn(
            value = total.years.toString().toPersianDigits(),
            label = stringResource(Res.string.history_all_stat_years),
            highlight = true,
            modifier = Modifier.weight(1f),
        )
        StatDivider()
        StatColumn(
            value = total.months.toString().toPersianDigits(),
            label = stringResource(Res.string.history_all_stat_months),
            modifier = Modifier.weight(1f),
        )
        StatDivider()
        StatColumn(
            value = total.days.toString().toPersianDigits(),
            label = stringResource(Res.string.history_all_stat_days),
            modifier = Modifier.weight(1f),
        )
    }
}
