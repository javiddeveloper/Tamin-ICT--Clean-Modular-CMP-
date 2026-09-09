package com.tamin.taminhamrah.feature.contractaffair.ui.paymentHistory.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_payment_history_summary_count_unit
import taminx.core.core_ui.contract_payment_history_summary_total

@Composable
internal fun PaymentHistorySummaryCard(
    successfulTotalLabel: String,
    paymentCount: Int,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.chipBg, RoundedCornerShape(CornerRadius.chip))
            .border(
                width = 1.dp,
                color = colors.blueBorder,
                shape = RoundedCornerShape((CornerRadius.chip))
            )
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {

        Box(
            modifier = Modifier
                .size(IconSize.xlarge)
                .background(colors.bgSurface, RoundedCornerShape(CornerRadius.lg)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.History,
                contentDescription = null,
                tint = colors.blueText,
                modifier = Modifier.size(IconSize.medium),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            TaminText(
                text = stringResource(Res.string.contract_payment_history_summary_total),
                style = MaterialTheme.typography.labelMedium,
                color = colors.textSecondary,
            )
            Text(
                text = successfulTotalLabel.ifBlank { "—" },
                style = MaterialTheme.typography.titleMedium,
                color = colors.greenText,
            )
        }

        Column(
            modifier = Modifier
                .background(colors.bgSurface, RoundedCornerShape(CornerRadius.lg))
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            NumericText(
                text = paymentCount.toString(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.blueText,
            )
            TaminText(
                text = stringResource(Res.string.contract_payment_history_summary_count_unit),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
            )
        }

    }
}

@PreviewRtlTheme
@Composable
private fun PaymentHistorySummaryCardPreviewLight() {
    PreviewRtlThemeContent {
        PaymentHistorySummaryCard(
            successfulTotalLabel = "۱۰۳٬۹۷۳٬۲۴۱ ریال",
            paymentCount = 3,
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PaymentHistorySummaryCardPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        PaymentHistorySummaryCard(
            successfulTotalLabel = "۱۰۳٬۹۷۳٬۲۴۱ ریال",
            paymentCount = 3,
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}
