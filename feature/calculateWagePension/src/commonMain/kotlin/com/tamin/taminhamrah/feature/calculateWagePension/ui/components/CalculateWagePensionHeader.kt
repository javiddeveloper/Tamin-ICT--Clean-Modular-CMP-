package com.tamin.taminhamrah.feature.calculateWagePension.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.ui.components.CustomChip
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.back_content_description
import taminx.core.core_ui.calculate_wage_pension_amount_label
import taminx.core.core_ui.calculate_wage_pension_cd_info
import taminx.core.core_ui.calculate_wage_pension_chip_estimated
import taminx.core.core_ui.calculate_wage_pension_chip_legal_floor
import taminx.core.core_ui.calculate_wage_pension_chip_simultaneous
import taminx.core.core_ui.calculate_wage_pension_info_body
import taminx.core.core_ui.calculate_wage_pension_info_confirm
import taminx.core.core_ui.calculate_wage_pension_info_title
import taminx.core.core_ui.calculate_wage_pension_title
import taminx.core.core_ui.ic_info
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.unit_rial

/**
 * Hero header built on [TaminTopAppBar]: circular back/info in the bar row, large title +
 * amount + status chips in the content slot. Host must leave bottom spacer room so the
 * overlapping stats card can sit on the gradient (same pattern as profile + ValidationStatusCard).
 */
@Composable
internal fun CalculateWagePensionHeader(
    eligibleAmount: Long,
    legalFloorApplied: Boolean,
    isMultipleWorkshopsEnabled: Boolean,
    onBack: () -> Unit,
    onInfoClick: () -> Unit,
    showInfoDialog: Boolean,
    onDismissInfo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val gradient = remember(colors.profileGradientStops) {
        Brush.horizontalGradient(colors.profileGradientStops)
    }

    TaminTopAppBar(
        title = "",
        modifier = modifier,
        background = gradient,
        bottomPadding = Spacing.lg,
        navigationIcon = {
            TaminTopAppBarButton(
                icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                contentDescription = stringResource(Res.string.back_content_description),
                onClick = onBack,
                bordered = true,
            )
        },
        action = {
            TaminTopAppBarButton(
                icon = vectorResource(Res.drawable.ic_info),
                contentDescription = stringResource(Res.string.calculate_wage_pension_cd_info),
                onClick = onInfoClick,
                bordered = true,
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(Res.string.calculate_wage_pension_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = colors.onGradient,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            Text(
                text = stringResource(Res.string.calculate_wage_pension_amount_label),
                style = MaterialTheme.typography.labelLarge,
                color = colors.textHeaderSubtitle,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = eligibleAmount.toPriceFormat().toPersianDigits(),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = colors.onGradient,
                )
                Text(
                    text = stringResource(Res.string.unit_rial),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onGradient.copy(alpha = 0.85f),
                    modifier = Modifier.padding(start = Spacing.sm, bottom = Spacing.xs),
                )
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                val secondaryLabel = when {
                    isMultipleWorkshopsEnabled ->
                        stringResource(Res.string.calculate_wage_pension_chip_simultaneous)
                    legalFloorApplied ->
                        stringResource(Res.string.calculate_wage_pension_chip_legal_floor)
                    else -> null
                }
                if (secondaryLabel != null) {
                    CustomChip(
                        text = secondaryLabel,
                        containerColor = colors.onGradient.copy(alpha = 0.12f),
                        textColor = colors.onGradient,
                        border = BorderStroke(Thickness.border, colors.onGradient.copy(alpha = 0.35f)),
                    )
                }
                CustomChip(
                    text = stringResource(Res.string.calculate_wage_pension_chip_estimated),
                    containerColor = colors.onGradient.copy(alpha = 0.18f),
                    textColor = colors.onGradient,
                )
            }
        }
    }

    if (showInfoDialog) {
        TaminConfirmationDialog(
            title = stringResource(Res.string.calculate_wage_pension_info_title),
            description = stringResource(Res.string.calculate_wage_pension_info_body),
            icon = vectorResource(Res.drawable.ic_info),
            iconTint = colors.onGradient,
            iconBackgroundBrush = colors.iconGradientPrimary,
            onDismissRequest = onDismissInfo,
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(Res.string.calculate_wage_pension_info_confirm),
                    onClick = onDismissInfo,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            dismissButton = {},
        )
    }
}
