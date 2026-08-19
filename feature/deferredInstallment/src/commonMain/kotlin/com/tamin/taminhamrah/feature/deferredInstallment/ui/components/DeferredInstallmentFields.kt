package com.tamin.taminhamrah.feature.deferredInstallment.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.rememberCopyAction
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.ui.toPriceFormat
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.deferred_installment_copy
import taminx.core.core_ui.deferred_installment_pensioner_number
import taminx.core.core_ui.deferred_installment_repayment_empty
import taminx.core.core_ui.deferred_installment_rial
import taminx.core.core_ui.ic_identity
import taminx.core.core_ui.ic_tamin_copy

@Composable
internal fun PensionerNumberCard(
    pensionerId: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val copy = rememberCopyAction(pensionerId)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.bgSurface)
            .border(Thickness.border, colors.border, RoundedCornerShape(CornerRadius.lg))
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_identity),
            contentDescription = null,
            tint = colors.blueText,
            modifier = Modifier.size(IconSize.small),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.deferred_installment_pensioner_number),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
            )
            Spacer(Modifier.height(Spacing.xxs))
            Text(
                text = pensionerId,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textPrimary,
            )
        }
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(CornerRadius.md))
                .clickable(onClick = copy)
                .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_copy),
                contentDescription = null,
                tint = colors.blueText,
                modifier = Modifier.size(IconSize.small),
            )
            Text(
                text = stringResource(Res.string.deferred_installment_copy),
                style = MaterialTheme.typography.labelMedium,
                color = colors.blueText,
            )
        }
    }
}

@Composable
internal fun DeferredInstallmentTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onClick: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    readOnly: Boolean = false,
    helperText: String? = null,
    formatAsAmount: Boolean = false,
) {
    val colors = LocalTaminColors.current
    val borderColor = if (isError) colors.dangerText else colors.border
    val displayed = if (formatAsAmount && value.isNotEmpty()) {
        value.toLongOrNull()?.toPriceFormat() ?: value
    } else {
        value
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CornerRadius.lg))
                .background(colors.bgSurface)
                .border(Thickness.border, borderColor, RoundedCornerShape(CornerRadius.lg))
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted,
                )
                Spacer(Modifier.height(Spacing.xxs))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (leadingIcon != null) {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = null,
                            tint = colors.blueText,
                            modifier = Modifier.size(IconSize.small),
                        )
                        Spacer(Modifier.width(Spacing.sm))
                    }
                    BasicTextField(
                        value = displayed,
                        onValueChange = { incoming ->
                            if (formatAsAmount || keyboardType == KeyboardType.Number) {
                                onValueChange(incoming.filter { it.isDigit() || it in '۰'..'۹' })
                            } else {
                                onValueChange(incoming)
                            }
                        },
                        readOnly = readOnly || onClick != null,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = colors.textPrimary),
                        cursorBrush = SolidColor(colors.blueText),
                        modifier = Modifier.weight(1f),
                        decorationBox = { inner ->
                            if (displayed.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.textMuted,
                                )
                            }
                            inner()
                        },
                    )
                    if (trailingIcon != null) {
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = null,
                            tint = colors.blueText,
                            modifier = Modifier.size(IconSize.small),
                        )
                    }
                }
            }
        }
        val below = if (isError) errorMessage else helperText
        if (!below.isNullOrBlank()) {
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = below,
                style = MaterialTheme.typography.labelSmall,
                color = if (isError) colors.dangerText else colors.textMuted,
            )
        }
    }
}

@Composable
internal fun CalculatedAmountBox(
    label: String,
    amount: Long?,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val value = if (amount == null) {
        stringResource(Res.string.deferred_installment_repayment_empty)
    } else {
        "${amount.toPriceFormat()} ${stringResource(Res.string.deferred_installment_rial)}"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                drawRoundRect(
                    color = colors.border,
                    style = Stroke(
                        width = Thickness.medium.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f),
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(CornerRadius.lg.toPx()),
                )
            }
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = colors.textPrimary,
        )
    }
}
