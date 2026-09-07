package com.tamin.taminhamrah.feature.contracts.ui.affairs.paymentHistory.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminTopAppBarGradient
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_payment_history_number_prefix
import taminx.core.core_ui.contract_payment_history_title
import taminx.core.core_ui.ic_tamin_chevron_back

@Composable
internal fun ContractPaymentHistoryHeader(
    insuranceType: String,
    contractNumber: String,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val gradient = remember(colors.profileGradientStops) {
        Brush.horizontalGradient(colors.profileGradientStops)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = CornerRadius.x3l, bottomEnd = CornerRadius.x3l))
            .background(taminTopAppBarGradient(colors.profileGradientStops)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TaminTopAppBar(
            title = stringResource(Res.string.contract_payment_history_title),
            background = gradient,
            bottomPadding = Spacing.none,
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = null,
                    onClick = onBackClicked,
                )
            },
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page)
                .padding(top = Spacing.xs, bottom = Spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TaminText(
                text = insuranceType,
                style = MaterialTheme.typography.labelLarge,
                color = colors.textHeaderSubtitle,
                textAlign = TextAlign.Center,
            )
            if (contractNumber.isNotBlank()) {
                TaminText(
                    text = "·  " + stringResource(Res.string.contract_payment_history_number_prefix),
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.textHeaderSubtitle,
                )
                NumericText(
                    text = contractNumber,
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.textHeaderSubtitle,
                )
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ContractPaymentHistoryHeaderPreviewLight() {
    PreviewRtlThemeContent {
        ContractPaymentHistoryHeader(
            insuranceType = "بیمهٔ اختیاری",
            contractNumber = "۴۸۳۲۲۲۲۶۸۶",
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractPaymentHistoryHeaderPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        ContractPaymentHistoryHeader(
            insuranceType = "بیمهٔ اختیاری",
            contractNumber = "۴۸۳۲۲۲۲۶۸۶",
            onBackClicked = {},
        )
    }
}
