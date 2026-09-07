package com.tamin.taminhamrah.feature.contracts.ui.affairs.paymentCalcDetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import taminx.core.core_ui.contract_payment_details_subtitle
import taminx.core.core_ui.contract_payment_details_title
import taminx.core.core_ui.ic_tamin_chevron_back

@Composable
internal fun ContractPaymentCalcDetailHeader(
    monthCountLabel: String,
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
            title = stringResource(Res.string.contract_payment_details_title),
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

        if (monthCountLabel.isNotBlank()) {
            TaminText(
                text = stringResource(
                    Res.string.contract_payment_details_subtitle,
                    monthCountLabel,
                ),
                style = MaterialTheme.typography.labelLarge,
                color = colors.textHeaderSubtitle,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.page)
                    .padding(top = Spacing.xs, bottom = Spacing.lg),
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ContractPaymentCalcDetailHeaderPreview() {
    PreviewRtlThemeContent {
        ContractPaymentCalcDetailHeader(
            monthCountLabel = "۲",
            onBackClicked = {},
        )
    }
}
