package com.tamin.taminhamrah.feature.payment.ui.checkout.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.tamin.taminhamrah.model.payment.PayerType
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.payment_payer_current_user
import taminx.core.core_ui.payment_payer_foreign_national
import taminx.core.core_ui.payment_payer_legal_entity
import taminx.core.core_ui.payment_payer_other_person

/** The four payer types the gateway accepts, as one radio group. */
@Composable
fun PayerTypeSelector(
    selected: PayerType,
    onSelect: (PayerType) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Column(
        modifier = modifier.fillMaxWidth().selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        PayerType.entries.forEach { payerType ->
            PayerTypeRow(
                payerType = payerType,
                isSelected = payerType == selected,
                enabled = enabled,
                onSelect = { onSelect(payerType) },
            )
        }
    }
}

@Composable
private fun PayerTypeRow(
    payerType: PayerType,
    isSelected: Boolean,
    enabled: Boolean,
    onSelect: () -> Unit,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.listRow))
            .background(if (isSelected) colors.blueBg else colors.bgSurface)
            .border(
                width = Thickness.border,
                color = if (isSelected) colors.blueBorder else colors.border,
                shape = RoundedCornerShape(CornerRadius.listRow),
            )
            .clickable(enabled = enabled, onClick = onSelect)
            .padding(horizontal = Spacing.md, vertical = Spacing.smPlus),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            enabled = enabled,
            colors = RadioButtonDefaults.colors(selectedColor = colors.blueText),
        )
        TaminText(
            text = payerType.label(),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textPrimary,
        )
    }
}

@Composable
private fun PayerType.label(): String = when (this) {
    PayerType.CURRENT_USER -> stringResource(Res.string.payment_payer_current_user)
    PayerType.OTHER_PERSON -> stringResource(Res.string.payment_payer_other_person)
    PayerType.LEGAL_ENTITY -> stringResource(Res.string.payment_payer_legal_entity)
    PayerType.FOREIGN_NATIONAL -> stringResource(Res.string.payment_payer_foreign_national)
}
