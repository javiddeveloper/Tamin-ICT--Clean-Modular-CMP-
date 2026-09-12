package com.tamin.taminhamrah.feature.contractaffair.ui.paymentCalcDetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.contractAffair.PaymentCalcLinePR
import com.tamin.taminhamrah.model.contractAffair.PaymentCalcMonthPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_payment_details_base_wage
import taminx.core.core_ui.contract_payment_details_day_unit
import taminx.core.core_ui.contract_payment_details_month_unit
import taminx.core.core_ui.contract_payment_details_net
import taminx.core.core_ui.contract_payment_details_rate
import taminx.core.core_ui.contract_payment_details_rate_value

private const val ABSENT = "—"

/** One month card: month tile · title · every line (حق بیمه / کمک دولت) · دستمزد مبنا / نرخ حق بیمه. */
@Composable
internal fun PaymentCalcDetailRow(
    month: PaymentCalcMonthPR,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface()
            .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            MonthTile(monthNumberLabel = month.monthNumberLabel)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
            ) {
                TaminText(
                    text = month.monthTitle,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary,
                )
                TaminText(
                    text = stringResource(
                        Res.string.contract_payment_details_day_unit,
                        month.daysLabel,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            month.lines.forEach { line -> LineRow(line = line) }

            if (month.lines.size > 1) {
                TaminDivider()
                LineRow(
                    line = PaymentCalcLinePR(
                        label = stringResource(Res.string.contract_payment_details_net),
                        amountRaw = month.netAmountRaw,
                        isDeduction = month.netAmountRaw < 0L,
                    ),
                    emphasised = true,
                )
            }
        }

        val rateValue = if (month.ratePercent != null) {
            stringResource(Res.string.contract_payment_details_rate_value, month.ratePercent.toString())
        } else {
            ABSENT
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            InfoTile(
                label = stringResource(Res.string.contract_payment_details_base_wage),
                value = if (month.baseWageRaw > 0L) month.baseWageRaw.toPriceFormat() else ABSENT,
                modifier = Modifier.weight(1f),
            )
            InfoTile(
                label = stringResource(Res.string.contract_payment_details_rate),
                value = rateValue,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun LineRow(line: PaymentCalcLinePR, emphasised: Boolean = false) {
    val colors = LocalTaminColors.current
    val labelWeight = if (emphasised) FontWeight.Bold else FontWeight.Normal
    val amountColor = if (line.isDeduction) colors.dangerText else colors.greenText
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        TaminText(
            text = line.label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = labelWeight),
            color = colors.textMuted,
        )
        NumericText(
            text = line.amountRaw.toPriceFormat(),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = amountColor,
        )
    }
}

@Composable
private fun MonthTile(monthNumberLabel: String) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier
            .size(48.dp)
            .background(colors.blueBg, RoundedCornerShape(CornerRadius.xl)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        NumericText(
            text = monthNumberLabel,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.blueText,
        )
        TaminText(
            text = stringResource(Res.string.contract_payment_details_month_unit),
            style = MaterialTheme.typography.labelSmall,
            color = colors.blueText,
        )
    }
}

@Composable
private fun InfoTile(label: String, value: String, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .background(colors.bgPage, RoundedCornerShape(CornerRadius.md))
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        TaminText(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted,
        )
        NumericText(
            text = value,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
        )
    }
}

private val PreviewMonth = PaymentCalcMonthPR(
    monthTitle = "شهریور ۱۴۰۵",
    monthNumberLabel = "۶",
    daysLabel = "۲۳",
    baseWageRaw = 152_955_060L,
    ratePercent = 30,
    lines = persistentListOf(
        PaymentCalcLinePR(label = "حق بیمه", amountRaw = 45_886_518L, isDeduction = false),
        PaymentCalcLinePR(label = "کمک دولت", amountRaw = -4_588_652L, isDeduction = true),
    ),
    netAmountRaw = 41_297_866L,
)

@PreviewRtlTheme
@Composable
private fun PaymentCalcDetailRowPreviewLight() {
    PreviewRtlThemeContent {
        Column(modifier = Modifier.padding(Spacing.page)) {
            PaymentCalcDetailRow(month = PreviewMonth)
        }
    }
}

@PreviewRtlTheme
@Composable
private fun PaymentCalcDetailRowPreviewSingleLineDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        Column(modifier = Modifier.padding(Spacing.page)) {
            PaymentCalcDetailRow(
                month = PreviewMonth.copy(
                    monthTitle = "مهر ۱۴۰۵",
                    monthNumberLabel = "۷",
                    lines = persistentListOf(
                        PaymentCalcLinePR(label = "حق بیمه", amountRaw = 53_866_782L, isDeduction = false),
                    ),
                    netAmountRaw = 53_866_782L,
                    baseWageRaw = 0L,
                    ratePercent = null,
                ),
            )
        }
    }
}
