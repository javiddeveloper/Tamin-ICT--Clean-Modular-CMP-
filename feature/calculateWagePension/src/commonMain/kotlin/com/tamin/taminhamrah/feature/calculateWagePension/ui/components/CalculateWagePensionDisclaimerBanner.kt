package com.tamin.taminhamrah.feature.calculateWagePension.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.calculate_wage_pension_disclaimer
import taminx.core.core_ui.calculate_wage_pension_disclaimer_link
import taminx.core.core_ui.ic_info
import taminx.core.core_ui.ic_tamin_chevron_forward

@Composable
internal fun CalculateWagePensionDisclaimerBanner(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val disclaimer = stringResource(Res.string.calculate_wage_pension_disclaimer)
    val link = stringResource(Res.string.calculate_wage_pension_disclaimer_link)
    val annotated = buildAnnotatedString {
        append(disclaimer)
        withStyle(
            SpanStyle(
                color = colors.blueText,
                fontWeight = FontWeight.Bold,
            )
        ) {
            append(link)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.greenBg, RoundedCornerShape(CornerRadius.listRow))
            .border(Thickness.border, colors.greenText.copy(alpha = 0.15f), RoundedCornerShape(CornerRadius.listRow))
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.smd, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        // RTL: info on the start (right), copy, chevron on the end (left).
        Icon(
            imageVector = vectorResource(Res.drawable.ic_info),
            contentDescription = null,
            tint = colors.greenText,
            modifier = Modifier.size(IconSize.banner),
        )
        Text(
            text = annotated,
            style = MaterialTheme.typography.bodySmall,
            color = colors.greenText,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_forward),
            contentDescription = null,
            tint = colors.greenText,
            modifier = Modifier.size(IconSize.small),
        )
    }
}
