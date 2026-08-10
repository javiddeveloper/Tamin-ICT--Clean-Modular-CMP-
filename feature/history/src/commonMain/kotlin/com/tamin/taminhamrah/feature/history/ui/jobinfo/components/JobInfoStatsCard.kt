package com.tamin.taminhamrah.feature.history.ui.jobinfo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import taminx.feature.history.Res
import taminx.feature.history.history_job_info_stat_first_employment
import taminx.feature.history.history_job_info_stat_job_title
import taminx.feature.history.history_job_info_stat_workshop
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing

@Composable
fun JobInfoStatsCard(
    workshopCount: Int,
    firstEmploymentYear: String,
    jobTitleCount: Int,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(taminColors.bgSurface)
            .border(
                width = 1.dp,
                color = taminColors.border,
                shape = RoundedCornerShape(CornerRadius.lg)
            )
            .padding(vertical = Spacing.md),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatColumn(
            value = jobTitleCount.toString(),
            label = stringResource(Res.string.history_job_info_stat_job_title),
            highlight = true,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(32.dp)
                .background(taminColors.divider)
        )
        StatColumn(
            value = firstEmploymentYear,
            label = stringResource(Res.string.history_job_info_stat_first_employment),
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(32.dp)
                .background(taminColors.divider)
        )
        StatColumn(
            value = workshopCount.toString(),
            label = stringResource(Res.string.history_job_info_stat_workshop),
            modifier = Modifier.weight(1f)
        )
    }
}
