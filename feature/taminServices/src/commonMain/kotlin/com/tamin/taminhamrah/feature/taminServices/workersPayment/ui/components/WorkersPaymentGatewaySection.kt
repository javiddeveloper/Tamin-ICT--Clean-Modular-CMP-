package com.tamin.taminhamrah.feature.taminServices.workersPayment.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.IconPosition
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.workers_payment_gateway_action
import taminx.core.core_ui.workers_payment_gateway_notice

/** The light-blue "you'll be redirected to the bank gateway" note above the pay action. */
@Composable
internal fun WorkersPaymentGatewayNoticeBanner(
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.blueBg, RoundedCornerShape(CornerRadius.lg))
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = colors.blueText,
            modifier = Modifier.size(18.dp),
        )
        TaminText(
            text = stringResource(Res.string.workers_payment_gateway_notice),
            style = MaterialTheme.typography.bodySmall,
            lineHeight = 22.sp,
            color = colors.blueText,
            modifier = Modifier.weight(1f),
        )
    }
}

/** Sticky bottom bar carrying the single "انتقال به درگاه پرداخت" action. */
@Composable
internal fun WorkersPaymentGatewayBar(
    enabled: Boolean,
    onPay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.bgSurface)
            .navigationBarsPadding()
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
    ) {
        TaminFilledButton(
            text = stringResource(Res.string.workers_payment_gateway_action),
            onClick = onPay,
            enabled = enabled,
            icon = Icons.Default.CreditCard,
            iconPosition = IconPosition.End,
            background = colors.iconGradientSuccess,
            height = 52.dp,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun WorkersPaymentGatewaySectionPreview() {
    PreviewRtlThemeContent {
        Column {
            WorkersPaymentGatewayNoticeBanner(modifier = Modifier.padding(Spacing.lg))
            WorkersPaymentGatewayBar(enabled = true, onPay = {})
        }
    }
}
