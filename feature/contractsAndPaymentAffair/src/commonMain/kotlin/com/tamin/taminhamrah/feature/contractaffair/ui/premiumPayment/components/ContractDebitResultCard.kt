package com.tamin.taminhamrah.feature.contractaffair.ui.premiumPayment.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.model.contractAffair.ContractDebitPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toRialAmount
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_premium_payment_deadline
import taminx.core.core_ui.contract_premium_payment_details_cta
import taminx.core.core_ui.contract_premium_payment_past_debt
import taminx.core.core_ui.contract_premium_payment_payable
import taminx.core.core_ui.contract_premium_payment_period_end
import taminx.core.core_ui.contract_premium_payment_period_premium
import taminx.core.core_ui.contract_premium_payment_period_start

/** محاسبهٔ حق بیمه result — six labelled readings + a «جزئیات برگ پرداخت» button. */
@Composable
internal fun ContractDebitResultCard(
    debit: ContractDebitPR,
    months: Int,
    onOpenDetails: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface()
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        DetailRow(
            label = stringResource(Res.string.contract_premium_payment_payable),
            value = debit.payableAmount,
            unit = "ریال",
            valueColor = colors.greenText,
            valueStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.contract_premium_payment_period_start),
            value = debit.periodStartLabel,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.contract_premium_payment_period_end),
            value = debit.periodEndLabel,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.contract_premium_payment_period_premium, months.toString()),
            value = debit.periodPremiumAmount,
            unit = "ریال",
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.contract_premium_payment_past_debt),
            value = debit.pastDebtAmount,
            unit = "ریال",
            valueColor = if (debit.hasPastDebt) colors.warning else colors.textPrimary,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.contract_premium_payment_deadline),
            value = debit.deadlineLabel,
            valueColor = colors.warning,
        )

        debit.infoMessage?.let {
            TaminDivider()
            TaminText(
                text = it,
                style = MaterialTheme.typography.labelMedium,
                color = colors.warning,
                modifier = Modifier.padding(vertical = Spacing.xs),
            )
        }

        TaminOutlinedButton(
            text = stringResource(Res.string.contract_premium_payment_details_cta),
            onClick = onOpenDetails,
            icon = Icons.Outlined.ReceiptLong,
            contentColor = colors.blueText,
            borderColor = colors.blueBorder,
            modifier = Modifier.padding(top = Spacing.sm),
        )
    }
}

private val PreviewDebit = ContractDebitPR(
    payableAmount = "53866782",
    periodPremiumAmount = "53866782",
    pastDebtAmount = "0",
    periodStartLabel = "۱۴۰۵/۰۷/۰۱",
    periodEndLabel = "۱۴۰۵/۰۷/۳۰",
    deadlineLabel = "۱۴۰۵/۱۰/۰۱",
    hasPastDebt = false,
    infoMessage = null,
    startDate = 0L,
    endDate = 0L,
)

@PreviewRtlTheme
@Composable
private fun ContractDebitResultCardPreviewLight() {
    PreviewRtlThemeContent {
        ContractDebitResultCard(
            debit = PreviewDebit,
            months = 1,
            onOpenDetails = {},
            modifier = Modifier.padding(Spacing.page),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractDebitResultCardPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        ContractDebitResultCard(
            debit = PreviewDebit.copy(
                pastDebtAmount = "1250000",
                hasPastDebt = true,
                infoMessage = "برای این دوره پیام اطلاع‌رسانی وجود دارد.",
            ),
            months = 3,
            onOpenDetails = {},
            modifier = Modifier.padding(Spacing.page),
        )
    }
}
