package com.tamin.taminhamrah.feature.addDependent.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.ShimmerSize
import com.tamin.taminhamrah.ui.theme.Spacing

@Composable
internal fun SelectableFieldRow(
    value: String,
    placeholder: String,
    trailingIcon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val colors = LocalTaminColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.bgSurface)
            .border(1.dp, colors.border, RoundedCornerShape(CornerRadius.lg))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = Spacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = value.ifBlank { placeholder },
            color = if (value.isBlank()) colors.textMuted else colors.textPrimary,
            style = MaterialTheme.typography.bodyMedium
        )
        Icon(
            imageVector = trailingIcon,
            contentDescription = null,
            tint = colors.textMuted,
            modifier = Modifier.size(19.dp)
        )
    }
}

@Composable
internal fun StepSectionTitle(title: String, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Text(
        text = title,
        color = colors.textPrimary,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.ExtraBold,
        modifier = modifier
    )
}



@Composable
internal fun AddDependentShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Spacing.lg)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ShimmerBlock(
                        modifier = Modifier.size(IconSize.stepperCircle),
                        cornerRadius = CornerRadius.full
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    ShimmerBlock(
                        modifier = Modifier
                            .width(ShimmerSize.chipWidth)
                            .height(ShimmerSize.subtitleHeight)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        ShimmerBlock(
            modifier = Modifier
                .width(ShimmerSize.titleWidth)
                .height(ShimmerSize.badgeHeight)
        )
        Spacer(modifier = Modifier.height(Spacing.md))
        ShimmerBlock(
            modifier = Modifier
                .width(ShimmerSize.labelWidth)
                .height(ShimmerSize.subtitleHeight)
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        ShimmerBlock(
            modifier = Modifier
                .fillMaxWidth()
                .height(ShimmerSize.fieldHeight),
            cornerRadius = CornerRadius.lg
        )
        Spacer(modifier = Modifier.height(Spacing.smd))
        ShimmerBlock(
            modifier = Modifier
                .fillMaxWidth()
                .height(ShimmerSize.fieldHeight),
            cornerRadius = CornerRadius.lg
        )
        Spacer(modifier = Modifier.height(Spacing.smd))
        ShimmerBlock(
            modifier = Modifier
                .fillMaxWidth()
                .height(ShimmerSize.fieldHeight),
            cornerRadius = CornerRadius.lg
        )
    }
}
