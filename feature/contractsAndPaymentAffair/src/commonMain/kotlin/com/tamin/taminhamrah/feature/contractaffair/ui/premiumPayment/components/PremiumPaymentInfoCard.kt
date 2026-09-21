package com.tamin.taminhamrah.feature.contractaffair.ui.premiumPayment.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_premium_payment_info_deadline
import taminx.core.core_ui.contract_premium_payment_info_freelance
import taminx.core.core_ui.contract_premium_payment_info_max_month
import taminx.core.core_ui.contract_premium_payment_info_optional
import taminx.core.core_ui.contract_premium_payment_paid_until

@Composable
internal fun PremiumPaymentInfoCard(
    isFreelance: Boolean,
    paidUntilLabel: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        if (paidUntilLabel.isNotBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        colors.greenBg,
                        RoundedCornerShape(CornerRadius.lg)
                    )
                    .border(
                        width = 1.dp,
                        color = colors.greenBorder,
                        shape = RoundedCornerShape(
                            CornerRadius.lg
                        )
                    )
                    .padding(horizontal = Spacing.md, vertical = Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = colors.greenText,
                    modifier = Modifier.size(18.dp),
                )
                TaminText(
                    text = stringResource(
                        Res.string.contract_premium_payment_paid_until,
                        paidUntilLabel,
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.greenText,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.blueBg, RoundedCornerShape(CornerRadius.lg))
                .border(
                    width = 1.dp,
                    color = colors.blueBorder,
                    shape = RoundedCornerShape(
                        CornerRadius.lg
                    )
                )
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            InfoBullet(
                text = listOf(
                    stringResource(
                        if (isFreelance) {
                            Res.string.contract_premium_payment_info_freelance
                        } else {
                            Res.string.contract_premium_payment_info_optional
                        },
                    ),
                    stringResource(Res.string.contract_premium_payment_info_deadline),
                    stringResource(Res.string.contract_premium_payment_info_max_month),
                ).joinToString("\n"),
            )
        }
    }
}

@Composable
private fun InfoBullet(text: String) {
    val colors = LocalTaminColors.current
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = colors.blueText,
            modifier = Modifier.size(16.dp),
        )
        TaminText(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = colors.blueText,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PremiumPaymentInfoCardPreviewLight() {
    PreviewRtlThemeContent {
        PremiumPaymentInfoCard(
            isFreelance = false,
            paidUntilLabel = "۱۴۰۵/۰۶/۳۱",
            modifier = Modifier.padding(Spacing.page),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PremiumPaymentInfoCardPreviewNoHistoryDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        PremiumPaymentInfoCard(
            isFreelance = true,
            paidUntilLabel = "",
            modifier = Modifier.padding(Spacing.page),
        )
    }
}
