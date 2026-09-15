package com.tamin.taminhamrah.feature.taminServices.workersPayment.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing

/**
 * A muted label over a bold value on a tinted rounded field — the small key/value block used
 * throughout the workers-payment cards (بازهٔ زمانی, عنوان شغل, …). [numeric] keeps codes and
 * dates left-to-right via [NumericText].
 */
@Composable
internal fun WorkersPaymentInfoBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    numeric: Boolean = true,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.bgPage, RoundedCornerShape(CornerRadius.xl))
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = colors.textMuted,
        )
        val valueStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        if (numeric) {
            NumericText(text = value, style = valueStyle, color = colors.textPrimary)
        } else {
            Text(text = value, style = valueStyle, color = colors.textPrimary)
        }
    }
}
