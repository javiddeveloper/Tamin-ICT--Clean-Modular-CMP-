package com.tamin.taminhamrah.feature.calculateWagePension.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminSwitchButton
import com.tamin.taminhamrah.ui.theme.ButtonDimens
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.calculate_wage_pension_workshop_subtitle_off
import taminx.core.core_ui.calculate_wage_pension_workshop_subtitle_on
import taminx.core.core_ui.calculate_wage_pension_workshop_title
import taminx.core.core_ui.ic_tamin_workshop_badge

@Composable
internal fun CalculateWagePensionWorkshopSwitchCard(
    enabled: Boolean,
    isLoading: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.cardCompact),
        colors = CardDefaults.cardColors(containerColor = colors.bgSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.sm),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            // RTL: first child sits on the start (right) — icon, then copy, switch on the end (left).
            Box(
                modifier = Modifier
                    .size(IconSize.badge)
                    .background(colors.blueBg, RoundedCornerShape(CornerRadius.md)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_workshop_badge),
                    contentDescription = null,
                    tint = colors.blueText,
                    modifier = Modifier.size(IconSize.small),
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Text(
                    text = stringResource(Res.string.calculate_wage_pension_workshop_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = stringResource(
                        if (enabled) Res.string.calculate_wage_pension_workshop_subtitle_on
                        else Res.string.calculate_wage_pension_workshop_subtitle_off
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(ButtonDimens.loadingIndicatorSize),
                    strokeWidth = ButtonDimens.loadingIndicatorStroke,
                    color = colors.blueText,
                )
            } else {
                TaminSwitchButton(
                    checked = enabled,
                    onCheckedChange = onToggle,
                )
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun CalculateWagePensionWorkshopSwitchCardPreview() {
    PreviewRtlThemeContent {
        Column(
            modifier = Modifier.padding(Spacing.page),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            CalculateWagePensionWorkshopSwitchCard(
                enabled = false,
                isLoading = false,
                onToggle = {},
            )
            CalculateWagePensionWorkshopSwitchCard(
                enabled = true,
                isLoading = false,
                onToggle = {},
            )
            CalculateWagePensionWorkshopSwitchCard(
                enabled = false,
                isLoading = true,
                onToggle = {},
            )
        }
    }
}
