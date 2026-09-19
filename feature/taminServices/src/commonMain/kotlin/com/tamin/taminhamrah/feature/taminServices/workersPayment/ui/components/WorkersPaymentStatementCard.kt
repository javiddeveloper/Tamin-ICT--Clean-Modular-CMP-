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
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.unit_rial
import taminx.core.core_ui.workers_payment_no_penalty
import taminx.core.core_ui.workers_payment_penalty
import taminx.core.core_ui.workers_payment_premium
import taminx.core.core_ui.workers_payment_statement_title
import taminx.core.core_ui.workers_payment_total_payable

/** "صورت‌حساب" — premium, late penalty, and the emphasized amount payable. */
@Composable
internal fun WorkersPaymentStatementCard(
    item: WorkersPaymentInfoPR,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val rial = stringResource(Res.string.unit_rial)
    val hasFine = item.amountFines > 0L

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
            .padding(horizontal = Spacing.xlg, vertical = Spacing.lg),
    ) {
        Text(
            text = stringResource(Res.string.workers_payment_statement_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary,
            modifier = Modifier.padding(bottom = Spacing.sm),
        )

        DetailRow(
            label = stringResource(Res.string.workers_payment_premium),
            value = item.amount.toPriceFormat(),
            unit = rial,
            valueStyle = MaterialTheme.typography.bodyMedium,
            verticalPadding = Spacing.sm,
        )
        DetailRow(
            label = stringResource(Res.string.workers_payment_penalty),
            value = if (hasFine) {
                item.amountFines.toPriceFormat()
            } else {
                stringResource(Res.string.workers_payment_no_penalty)
            },
            unit = if (hasFine) rial else null,
            numeric = hasFine,
            valueColor = if (hasFine) colors.dangerText else colors.textMuted,
            valueStyle = MaterialTheme.typography.bodyMedium,
            verticalPadding = Spacing.sm,
        )

        TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.xs),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.workers_payment_total_payable),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
            ) {
                NumericText(
                    text = item.totalPayable.toPriceFormat(),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.greenText,
                )
                Text(
                    text = rial,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.greenText,
                )
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun WorkersPaymentStatementCardPreview() {
    PreviewRtlThemeContent {
        WorkersPaymentStatementCard(
            item = workersPaymentPreviewItem(),
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun WorkersPaymentStatementCardWithFinePreview() {
    PreviewRtlThemeContent(darkTheme = true) {
        WorkersPaymentStatementCard(
            item = workersPaymentPreviewItem(amountFines = 1341000),
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}
