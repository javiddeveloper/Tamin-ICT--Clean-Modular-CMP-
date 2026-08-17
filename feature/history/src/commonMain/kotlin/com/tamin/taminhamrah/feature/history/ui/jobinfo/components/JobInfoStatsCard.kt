package com.tamin.taminhamrah.feature.history.ui.jobinfo.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.ui.components.StatColumn
import com.tamin.taminhamrah.ui.components.StatDivider
import com.tamin.taminhamrah.ui.components.StatRowCard
import org.jetbrains.compose.resources.stringResource
import taminx.feature.history.Res
import taminx.feature.history.history_job_info_stat_first_employment
import taminx.feature.history.history_job_info_stat_job_title
import taminx.feature.history.history_job_info_stat_workshop

/**
 * The job-titles page's header figures.
 *
 * Draws through `core-ui`'s [StatRowCard], the same card «مجموع سوابق» hangs its career total in, so
 * the two pages of this feature cannot drift apart in radius, border or divider.
 */
@Composable
fun JobInfoStatsCard(
    workshopCount: Int,
    firstEmploymentYear: String,
    jobTitleCount: Int,
    modifier: Modifier = Modifier
) {
    StatRowCard(modifier = modifier) {
        StatColumn(
            value = jobTitleCount.toString(),
            label = stringResource(Res.string.history_job_info_stat_job_title),
            highlight = true,
            modifier = Modifier.weight(1f)
        )
        StatDivider()
        StatColumn(
            value = firstEmploymentYear,
            label = stringResource(Res.string.history_job_info_stat_first_employment),
            modifier = Modifier.weight(1f)
        )
        StatDivider()
        StatColumn(
            value = workshopCount.toString(),
            label = stringResource(Res.string.history_job_info_stat_workshop),
            modifier = Modifier.weight(1f)
        )
    }
}
