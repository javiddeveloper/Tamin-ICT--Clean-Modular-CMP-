package com.tamin.taminhamrah.ui.contractFlow

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import com.tamin.taminhamrah.model.contractFlow.FreelancePremiumRangePR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.theme.ButtonDimens
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerSize
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.ui.toPriceFormat
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_optional_base_wage_result_label
import taminx.core.core_ui.contract_optional_calc_done
import taminx.core.core_ui.contract_optional_calculate_base_wage
import taminx.core.core_ui.contract_optional_coverage_disability
import taminx.core.core_ui.contract_optional_coverage_retirement
import taminx.core.core_ui.contract_optional_coverage_survivors
import taminx.core.core_ui.contract_optional_coverage_treatment
import taminx.core.core_ui.contract_optional_fixed_rate_title
import taminx.core.core_ui.contract_optional_formula_footer
import taminx.core.core_ui.contract_optional_premium_decrease
import taminx.core.core_ui.contract_optional_premium_increase
import taminx.core.core_ui.contract_optional_preset_max
import taminx.core.core_ui.contract_optional_preset_mid
import taminx.core.core_ui.contract_optional_preset_min
import taminx.core.core_ui.contract_optional_rate_percent_full
import taminx.core.core_ui.contract_optional_rate_percent_label
import taminx.core.core_ui.contract_optional_rate_value
import taminx.core.core_ui.contract_optional_selected_premium_label
import taminx.core.core_ui.contract_optional_summary_legal_rate
import taminx.core.core_ui.contract_optional_summary_selected_premium
import taminx.core.core_ui.contract_optional_summary_treatment
import taminx.core.core_ui.contract_optional_summary_treatment_included
import taminx.core.core_ui.contract_premium_range_unavailable
import taminx.core.core_ui.ic_calculator
import taminx.core.core_ui.unit_rial

private enum class OptionalPremiumPreset { MIN, MID, MAX }

@Composable
fun PremiumSalaryStepContent(
    premiumRange: FreelancePremiumRangePR?,
    selectedPremium: Long?,
    calculatedMonthlySalary: Long?,
    isLoading: Boolean,
    isCalculating: Boolean,
    isPremiumCalculated: Boolean,
    onPremiumChange: (Long) -> Unit,
    onCalculate: () -> Unit,
) {
    when {
        isLoading && premiumRange == null -> PremiumSalaryStepShimmerSkeleton()

        premiumRange == null -> {
            Text(
                text = stringResource(Res.string.contract_premium_range_unavailable),
                style = MaterialTheme.typography.bodyMedium,
                color = LocalTaminColors.current.textPrimary,
            )
        }

        else -> {
            OptionalPremiumSalaryContent(
                premiumRange = premiumRange,
                selectedPremium = selectedPremium,
                calculatedMonthlySalary = calculatedMonthlySalary,
                isCalculating = isCalculating,
                isPremiumCalculated = isPremiumCalculated,
                onPremiumChange = onPremiumChange,
                onCalculate = onCalculate,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OptionalPremiumSalaryContent(
    premiumRange: FreelancePremiumRangePR,
    selectedPremium: Long?,
    calculatedMonthlySalary: Long?,
    isCalculating: Boolean,
    isPremiumCalculated: Boolean,
    onPremiumChange: (Long) -> Unit,
    onCalculate: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val cardShape = RoundedCornerShape(CornerRadius.card)
    val low = premiumRange.lowPremium
    val high = premiumRange.highPremium
    val mid = optionalPremiumMid(low, high)
    val step = premiumRange.paymentTabayi.takeIf { it > 0L }
        ?: OPTIONAL_PREMIUM_STEP
    val current = (selectedPremium ?: mid).coerceIn(low, high)
    val selectedPreset = when (current) {
        low -> OptionalPremiumPreset.MIN
        mid -> OptionalPremiumPreset.MID
        high -> OptionalPremiumPreset.MAX
        else -> null
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(colors.bgSurface)
            .border(Thickness.border, colors.border, cardShape)
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        OptionalFixedRateHeader()

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.contract_optional_selected_premium_label),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.textTertiary,
                )
                Text(
                    text = stringResource(Res.string.unit_rial),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    OptionalPremiumStepperButton(
                        icon = Icons.Outlined.Remove,
                        contentDescription = stringResource(Res.string.contract_optional_premium_decrease),
                        enabled = current > low,
                        onClick = { onPremiumChange((current - step).coerceAtLeast(low)) },
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = Spacing.sm),
                    contentAlignment = Alignment.Center,
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Text(
                            text = current.toPriceFormat(),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    OptionalPremiumStepperButton(
                        icon = Icons.Outlined.Add,
                        contentDescription = stringResource(Res.string.contract_optional_premium_increase),
                        enabled = current < high,
                        onClick = { onPremiumChange((current + step).coerceAtMost(high)) },
                    )
                }
            }

            if (high > low) {
                Slider(
                    value = current.toFloat(),
                    onValueChange = { onPremiumChange(it.toLong().coerceIn(low, high)) },
                    valueRange = low.toFloat()..high.toFloat(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = colors.blueText,
                        activeTrackColor = colors.blueText,
                        inactiveTrackColor = colors.border,
                    ),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    NumericText(
                        text = low.toPriceFormat(),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted,
                    )
                    NumericText(
                        text = high.toPriceFormat(),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                OptionalPremiumPresetChip(
                    label = stringResource(Res.string.contract_optional_preset_max),
                    selected = selectedPreset == OptionalPremiumPreset.MAX,
                    onClick = { onPremiumChange(high) },
                    modifier = Modifier.weight(1f),
                )
                OptionalPremiumPresetChip(
                    label = stringResource(Res.string.contract_optional_preset_mid),
                    selected = selectedPreset == OptionalPremiumPreset.MID,
                    onClick = { onPremiumChange(mid) },
                    modifier = Modifier.weight(1f),
                )
                OptionalPremiumPresetChip(
                    label = stringResource(Res.string.contract_optional_preset_min),
                    selected = selectedPreset == OptionalPremiumPreset.MIN,
                    onClick = { onPremiumChange(low) },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        AnimatedContent(
            targetState = isPremiumCalculated && calculatedMonthlySalary != null,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "optionalPremiumCalcState",
        ) { calculated ->
            if (calculated) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    OptionalCalcDoneBanner()
                    OptionalBaseWageResultCard(
                        selectedPremium = current,
                        baseWage = calculatedMonthlySalary ?: 0L,
                    )
                }
            } else {
                TaminOutlinedButton(
                    text = stringResource(Res.string.contract_optional_calculate_base_wage),
                    onClick = onCalculate,
                    enabled = !isCalculating,
                    icon = if (isCalculating) {
                        null
                    } else {
                        vectorResource(Res.drawable.ic_calculator)
                    },
                    borderColor = colors.blueText.copy(alpha = 0.35f),
                    containerColor = colors.blueBg,
                    contentColor = colors.blueText,
                    height = ButtonDimens.height,
                    shape = RoundedCornerShape(CornerRadius.xl),
                )
                if (isCalculating) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Spacing.xs),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(IconSize.medium),
                            color = colors.blueText,
                            strokeWidth = Thickness.border,
                        )
                    }
                }
            }
        }

        Text(
            text = stringResource(Res.string.contract_optional_formula_footer),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted,
            textAlign = TextAlign.Start,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OptionalFixedRateHeader() {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.lg)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.blueBg)
            .padding(Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(IconSize.xlarge)
                .clip(RoundedCornerShape(CornerRadius.md))
                .background(colors.blueText),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(Res.string.contract_optional_rate_value),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.bgSurface,
                )
                Text(
                    text = stringResource(Res.string.contract_optional_rate_percent_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.bgSurface,
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = stringResource(Res.string.contract_optional_fixed_rate_title),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                OptionalCoverageChip(stringResource(Res.string.contract_optional_coverage_retirement))
                OptionalCoverageChip(stringResource(Res.string.contract_optional_coverage_disability))
                OptionalCoverageChip(stringResource(Res.string.contract_optional_coverage_survivors))
                OptionalCoverageChip(stringResource(Res.string.contract_optional_coverage_treatment))
            }
        }
    }
}

@Composable
private fun OptionalCoverageChip(label: String) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.chip)
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = colors.blueText,
        modifier = Modifier
            .clip(shape)
            .background(colors.bgSurface)
            .border(Thickness.border, colors.blueText.copy(alpha = 0.2f), shape)
            .padding(horizontal = Spacing.sm, vertical = Spacing.xxs),
    )
}

@Composable
private fun OptionalPremiumPresetChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.xl)
    Text(
        text = label,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        color = if (selected) colors.blueText else colors.textPrimary,
        textAlign = TextAlign.Center,
        modifier = modifier
            .clip(shape)
            .border(
                width = Thickness.border,
                color = if (selected) colors.blueText else colors.border,
                shape = shape,
            )
            .background(if (selected) colors.blueBg else colors.bgSurface)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = Spacing.sm),
    )
}

@Composable
private fun OptionalPremiumStepperButton(
    icon: ImageVector,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val shape = CircleShape
    Box(
        modifier = Modifier
            .size(ShimmerSize.stepperButtonSize)
            .clip(shape)
            .background(if (enabled) colors.bgSurface else colors.bgSurface.copy(alpha = 0.6f))
            .border(Thickness.border, colors.border, shape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) colors.textPrimary else colors.textMuted,
            modifier = Modifier.size(IconSize.small),
        )
    }
}

@Composable
private fun OptionalCalcDoneBanner() {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.xl)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.greenBg)
            .border(Thickness.border, colors.greenText.copy(alpha = 0.2f), shape)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = colors.greenText,
            modifier = Modifier.size(IconSize.medium),
        )
        Text(
            text = stringResource(Res.string.contract_optional_calc_done),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = colors.greenText,
        )
    }
}

@Composable
private fun OptionalBaseWageResultCard(
    selectedPremium: Long,
    baseWage: Long,
) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.lg)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.greenBg)
            .border(Thickness.border, colors.greenText.copy(alpha = 0.12f), shape)
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(
            text = stringResource(Res.string.contract_optional_base_wage_result_label),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = colors.greenText,
        )
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            NumericText(
                text = baseWage.toPriceFormat(),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.greenText,
            )
            Text(
                text = stringResource(Res.string.unit_rial),
                style = MaterialTheme.typography.bodySmall,
                color = colors.greenText,
                modifier = Modifier.padding(bottom = Spacing.xxs),
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xxs))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Thickness.border)
                .background(colors.greenText.copy(alpha = 0.15f)),
        )

        OptionalResultSummaryRow(
            label = stringResource(Res.string.contract_optional_summary_selected_premium),
            value = selectedPremium.toPriceFormat(),
        )
        OptionalResultSummaryRow(
            label = stringResource(Res.string.contract_optional_summary_legal_rate),
            value = stringResource(Res.string.contract_optional_rate_percent_full),
        )
        OptionalResultSummaryRow(
            label = stringResource(Res.string.contract_optional_summary_treatment),
            value = stringResource(Res.string.contract_optional_summary_treatment_included),
        )
    }
}

@Composable
private fun OptionalResultSummaryRow(
    label: String,
    value: String,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = colors.greenText.copy(alpha = 0.85f),
        )
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = colors.greenText,
            )
        }
    }
}

internal fun optionalPremiumMid(low: Long, high: Long): Long =
    ((low + high) / 2L)

private const val OPTIONAL_PREMIUM_STEP = 10_000L

@PreviewRtlTheme
@Composable
private fun PremiumSalaryStepContentPreview() {
    PreviewRtlThemeContent {
        PremiumSalaryStepContent(
            premiumRange = FreelancePremiumRangePR(
                lowPremium = 18_900_000L,
                highPremium = 132_300_000L,
                paymentTabayi = 10_000L,
                history = 0,
            ),
            selectedPremium = 75_600_000L,
            calculatedMonthlySalary = null,
            isLoading = false,
            isCalculating = false,
            isPremiumCalculated = false,
            onPremiumChange = {},
            onCalculate = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PremiumSalaryStepContentCalculatedPreview() {
    PreviewRtlThemeContent {
        PremiumSalaryStepContent(
            premiumRange = FreelancePremiumRangePR(
                lowPremium = 18_900_000L,
                highPremium = 132_300_000L,
                paymentTabayi = 10_000L,
                history = 0,
            ),
            selectedPremium = 75_600_000L,
            calculatedMonthlySalary = 280_000_000L,
            isLoading = false,
            isCalculating = false,
            isPremiumCalculated = true,
            onPremiumChange = {},
            onCalculate = {},
        )
    }
}
