package com.tamin.taminhamrah.feature.contracts.ui.affairs.premiumPayment.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_premium_payment_period_label

/** «دورهٔ پرداخت (ماه)» − / + selector, 1..12. */
@Composable
internal fun MonthStepper(
    months: Int,
    canDecrement: Boolean,
    canIncrement: Boolean,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {

        StepperTile(
            icon = Icons.Rounded.Add,
            enabled = canIncrement,
            containerColor = colors.greenBg,
            contentColor = colors.greenText,
            onClick = onIncrement,
        )
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            TaminText(
                text = stringResource(Res.string.contract_premium_payment_period_label),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
            )
            NumericText(
                text = months.toString(),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
            )
        }
        StepperTile(
            icon = Icons.Rounded.Remove,
            enabled = canDecrement,
            containerColor = colors.dangerBg,
            contentColor = colors.dangerText,
            onClick = onDecrement,
        )
    }
}

@Composable
private fun StepperTile(
    icon: ImageVector,
    enabled: Boolean,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.lg)
    Box(
        modifier = Modifier
            .size(52.dp)
            .alpha(if (enabled) 1f else 0.4f)
            .background(containerColor, shape)
            .border(1.dp, colors.border, shape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = contentColor)
    }
}

@PreviewRtlTheme
@Composable
private fun MonthStepperPreviewLight() {
    PreviewRtlThemeContent {
        MonthStepper(
            months = 3,
            canDecrement = true,
            canIncrement = true,
            onDecrement = {},
            onIncrement = {},
            modifier = Modifier.padding(Spacing.page),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun MonthStepperPreviewMinDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        MonthStepper(
            months = 1,
            canDecrement = false,
            canIncrement = true,
            onDecrement = {},
            onIncrement = {},
            modifier = Modifier.padding(Spacing.page),
        )
    }
}
