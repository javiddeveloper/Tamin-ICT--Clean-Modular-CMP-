package com.tamin.taminhamrah.feature.history.ui.jobinfo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.history.HistoryJobInfoItemPR
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing

private fun String.formatAsYearMonth(): String {
    return try {
        if (length < 6) return this
        "${take(4)}/${drop(4)}"
    } catch (e: Exception) {
        this
    }
}

@Composable
fun JobInfoCard(
    jobInfo: HistoryJobInfoItemPR,
    onCopy: (String) -> Unit
) {
    val taminColors = LocalTaminColors.current
    val formattedStartDate = jobInfo.startDate.formatAsYearMonth()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.card))
            .background(taminColors.bgSurface)
            .border(
                width = 1.dp,
                color = taminColors.border,
                shape = RoundedCornerShape(CornerRadius.card)
            )
            .padding(Spacing.md)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(
                        width = 1.dp,
                        color = taminColors.blueText.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .background(taminColors.chipBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Work,
                    contentDescription = null,
                    tint = taminColors.blueText,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(Spacing.sm))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = jobInfo.jobDesc,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = taminColors.textPrimary
                )
                if (jobInfo.rwshName.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = jobInfo.rwshName,
                        style = MaterialTheme.typography.bodySmall,
                        color = taminColors.textMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.md))
        DashedDivider(color = taminColors.divider)
        Spacer(modifier = Modifier.height(Spacing.md))

        if (formattedStartDate.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccessTime,
                    contentDescription = null,
                    tint = taminColors.textMuted,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(Spacing.xs))
                Text(
                    text = "شروع اشتغال $formattedStartDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = taminColors.textMuted
                )
            }
            Spacer(modifier = Modifier.height(Spacing.md))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            InfoChip(
                modifier = Modifier.weight(1f),
                label = "کد کارگاه",
                value = jobInfo.rwshId,
                onCopy = onCopy
            )
            InfoChip(
                modifier = Modifier.weight(1f),
                label = "شماره بیمه",
                value = jobInfo.risuid,
                onCopy = onCopy
            )
        }
    }
}
