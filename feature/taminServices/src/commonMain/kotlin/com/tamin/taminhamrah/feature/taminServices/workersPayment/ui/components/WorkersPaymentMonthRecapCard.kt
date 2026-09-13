package com.tamin.taminhamrah.feature.taminServices.workersPayment.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.taminServices.workersPayment.model.WorkersPaymentInfoPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.CustomChip
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toFormattedDate
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.jalali_months
import taminx.core.core_ui.workers_payment_date_range
import taminx.core.core_ui.workers_payment_date_range_to
import taminx.core.core_ui.workers_payment_days_format
import taminx.core.core_ui.workers_payment_job_title

/**
 * "مرداد ۱۴۰۴" recap — month + insured-day chip, then the date range and job title. The card the
 * detail screen rides up into the header.
 */
@Composable
internal fun WorkersPaymentMonthRecapCard(
    item: WorkersPaymentInfoPR,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val months = stringArrayResource(Res.array.jalali_months)
    val monthName = try {
        item.month
            .toInt()
            .minus(1)
            .let { months.getOrNull(it) }
            ?: item.monthTitle
    } catch (e: Exception) {
        item.monthTitle
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .coloredShadow(
                color = colors.shadowSubtle,
                borderRadius = CornerRadius.card,
                blurRadius = 26.dp,
                offsetY = 10.dp,
            )
            .taminSurface()
            .padding(Spacing.xlg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(
                text = "$monthName ${item.year}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            CustomChip(
                text = stringResource(Res.string.workers_payment_days_format, item.days),
                containerColor = colors.bgPage,
                textColor = colors.textSecondary,
            )
        }

        WorkersPaymentInfoBox(
            label = stringResource(Res.string.workers_payment_date_range),
            value = stringResource(
                Res.string.workers_payment_date_range_to,
                item.fromDatePersian.toFormattedDate(),
                item.toDatePersian.toFormattedDate(),
            ),
        )
        WorkersPaymentInfoBox(
            label = stringResource(Res.string.workers_payment_job_title),
            value = item.professionalTitle,
            numeric = false,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun WorkersPaymentMonthRecapCardPreview() {
    PreviewRtlThemeContent {
        WorkersPaymentMonthRecapCard(
            item = workersPaymentPreviewItem(),
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}
