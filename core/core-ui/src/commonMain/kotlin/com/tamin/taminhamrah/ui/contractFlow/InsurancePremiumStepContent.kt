package com.tamin.taminhamrah.ui.contractFlow

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import com.tamin.taminhamrah.model.contractFlow.FreelancePremiumRangePR
import com.tamin.taminhamrah.model.contractFlow.SpcPremiumRateOptionPR
import com.tamin.taminhamrah.model.contracts.FreeJobDN
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.BannerCard
import com.tamin.taminhamrah.ui.components.BannerType
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
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.vector.ImageVector
import com.tamin.taminhamrah.ui.theme.shimmer
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_field_job
import taminx.core.core_ui.contract_premium_annual_increase
import taminx.core.core_ui.contract_premium_base_wage_label
import taminx.core.core_ui.contract_premium_calculate_monthly
import taminx.core.core_ui.contract_premium_decrease
import taminx.core.core_ui.contract_premium_formula_hint
import taminx.core.core_ui.contract_premium_increase
import taminx.core.core_ui.contract_premium_monthly_salary
import taminx.core.core_ui.contract_premium_payable_label
import taminx.core.core_ui.contract_premium_range_hint
import taminx.core.core_ui.contract_premium_rate_12_info
import taminx.core.core_ui.contract_premium_rate_14_info
import taminx.core.core_ui.contract_premium_rate_18_info
import taminx.core.core_ui.contract_premium_rate_label
import taminx.core.core_ui.contract_premium_rate_not_found
import taminx.core.core_ui.contract_premium_rate_percent
import taminx.core.core_ui.contract_premium_treatment_per_person
import taminx.core.core_ui.ic_tamin_print
import taminx.core.core_ui.unit_rial

private const val PREMIUM_RATE_SINGLE_ROW_MAX = 4
private const val PREMIUM_RATE_ADD_STEP = 10_000L

@Composable
private fun PremiumRateGrid(
    premiumRates: List<SpcPremiumRateOptionPR>,
    selectedCode: String?,
    isRateSelectionEnabled: Boolean,
    onRateSelected: (SpcPremiumRateOptionPR) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rows = if (premiumRates.size <= PREMIUM_RATE_SINGLE_ROW_MAX) {
        listOf(premiumRates)
    } else {
        val itemsPerRow = (premiumRates.size + 1) / 2
        premiumRates.chunked(itemsPerRow)
    }
    val columnsInWidestRow = rows.maxOf { it.size }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        rows.forEach { rowRates ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                rowRates.forEach { rate ->
                    PremiumRateChip(
                        rate = rate,
                        selected = rate.code == selectedCode,
                        enabled = isRateSelectionEnabled,
                        onClick = { onRateSelected(rate) },
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(columnsInWidestRow - rowRates.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun InsurancePremiumStepContent(
    premiumRates: List<SpcPremiumRateOptionPR>,
    selectedCode: String?,
    isLoading: Boolean,
    onRateSelected: (SpcPremiumRateOptionPR) -> Unit,
    isRateSelectionEnabled: Boolean = true,
    showFreeJobSelector: Boolean = false,
    freeJobs: List<FreeJobDN> = emptyList(),
    selectedFreeJobCode: String? = null,
    selectedFreeJobName: String? = null,
    isFreeJobsLoading: Boolean = false,
    onFreeJobSelected: (FreeJobDN) -> Unit = {},
    premiumRange: FreelancePremiumRangePR? = null,
    selectedPremium: Long? = null,
    calculatedMonthlySalary: Long? = null,
    isPremiumRangeLoading: Boolean = false,
    isCalculating: Boolean = false,
    isPremiumCalculated: Boolean = false,
    showPremiumSlider: Boolean = true,
    showTreatmentCostHint: Boolean = false,
    onPremiumChange: (Long) -> Unit = {},
    onCalculate: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    when {
        isLoading && premiumRates.isEmpty() -> {
            InsurancePremiumStepShimmerSkeleton(modifier = modifier)
        }

        premiumRates.isEmpty() -> {
            Text(
                text = stringResource(Res.string.contract_premium_rate_not_found),
                style = MaterialTheme.typography.bodyMedium,
                modifier = modifier,
            )
        }

        else -> {
            val colors = LocalTaminColors.current
            val cardShape = RoundedCornerShape(CornerRadius.x2l)
            val selectedRate = premiumRates.firstOrNull { it.code == selectedCode }
            val showWageSection = showPremiumSlider && selectedRate != null
            val wageLoading = showWageSection && isPremiumRangeLoading && premiumRange == null

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(cardShape)
                    .background(colors.bgSurface)
                    .border(Thickness.border, colors.border, cardShape)
                    .padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                if (showFreeJobSelector) {
                    SelectableField(
                        label = stringResource(Res.string.contract_field_job),
                        options = freeJobs,
                        selectedCode = selectedFreeJobCode.orEmpty(),
                        selectedName = selectedFreeJobName.orEmpty(),
                        optionCode = { it.jobCode.orEmpty() },
                        optionName = { it.discrioption.orEmpty() },
                        isLoading = isFreeJobsLoading,
                        onSelected = onFreeJobSelected,
                    )
                }

                PremiumRateBenefitHints()

                PremiumRateLabel()

                PremiumRateGrid(
                    premiumRates = premiumRates,
                    selectedCode = selectedCode,
                    isRateSelectionEnabled = isRateSelectionEnabled,
                    onRateSelected = onRateSelected,
                )

                if (showWageSection) {
                    if (wageLoading) {
                        PremiumWageSectionShimmer()
                    } else if (premiumRange != null) {
                        PremiumWageSection(
                            premiumRange = premiumRange,
                            selectedPremium = selectedPremium,
                            calculatedMonthlySalary = calculatedMonthlySalary,
                            isCalculating = isCalculating,
                            isPremiumCalculated = isPremiumCalculated,
                            selectedRatePercent = selectedRate?.insurancePercent,
                            showTreatmentCostHint = showTreatmentCostHint,
                            onPremiumChange = onPremiumChange,
                            onCalculate = onCalculate,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumRateBenefitHints(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        BannerCard(
            message = stringResource(Res.string.contract_premium_rate_12_info),
            type = BannerType.Info,
        )
        BannerCard(
            message = stringResource(Res.string.contract_premium_rate_14_info),
            type = BannerType.Info,
        )
        BannerCard(
            message = stringResource(Res.string.contract_premium_rate_18_info),
            type = BannerType.Info,
        )
    }
}

@Composable
private fun PremiumRateLabel(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    val label = buildAnnotatedString {
        append(stringResource(Res.string.contract_premium_rate_label))
        withStyle(SpanStyle(color = colors.dangerText)) {
            append(" *")
        }
    }
    Text(
        text = label,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.Bold,
        color = colors.textTertiary,
        modifier = modifier,
    )
}

@Composable
private fun PremiumRateChip(
    rate: SpcPremiumRateOptionPR,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val chipShape = RoundedCornerShape(CornerRadius.lg)
    val percentLabel = rate.insurancePercent?.let {
        stringResource(Res.string.contract_premium_rate_percent, it)
    } ?: rate.description

    Box(
        modifier = modifier
            .clip(chipShape)
            .background(if (selected) colors.blueBg else colors.bgSurface)
            .border(
                width = if (selected) Thickness.medium else Thickness.border,
                color = if (selected) colors.blueText else colors.border,
                shape = chipShape,
            )
            .selectable(
                selected = selected,
                enabled = enabled,
                onClick = onClick,
                role = Role.RadioButton,
            )
            .padding(vertical = Spacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = percentLabel,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = when {
                !enabled -> colors.textMuted
                selected -> colors.blueText
                else -> colors.textPrimary
            },
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PremiumWageSection(
    premiumRange: FreelancePremiumRangePR,
    selectedPremium: Long?,
    calculatedMonthlySalary: Long?,
    isCalculating: Boolean,
    isPremiumCalculated: Boolean,
    selectedRatePercent: String?,
    showTreatmentCostHint: Boolean,
    onPremiumChange: (Long) -> Unit,
    onCalculate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val low = premiumRange.lowPremium
    val high = premiumRange.highPremium
    val step = PREMIUM_RATE_ADD_STEP
    val current = (selectedPremium ?: low).coerceIn(low, high)
    val treatmentCost = premiumRange.paymentTabayi.takeIf { it > 0L }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        BannerCard(
            message = stringResource(
                Res.string.contract_premium_range_hint,
                low.toPriceFormat(),
                high.toPriceFormat(),
            ),
            type = BannerType.Info,
        )
        if (!selectedRatePercent.isNullOrBlank()) {
            BannerCard(
                message = stringResource(
                    Res.string.contract_premium_formula_hint,
                    selectedRatePercent,
                ),
                type = BannerType.Info,
            )
        }
        BannerCard(
            message = stringResource(Res.string.contract_premium_annual_increase),
            type = BannerType.Info,
        )
        if (showTreatmentCostHint && treatmentCost != null) {
            BannerCard(
                message = stringResource(
                    Res.string.contract_premium_treatment_per_person,
                    treatmentCost.toPriceFormat(),
                ),
                type = BannerType.Info,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.contract_premium_base_wage_label),
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
                PremiumStepperButton(
                    icon = Icons.Outlined.Remove,
                    contentDescription = stringResource(Res.string.contract_premium_decrease),
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
                PremiumStepperButton(
                    icon = Icons.Outlined.Add,
                    contentDescription = stringResource(Res.string.contract_premium_increase),
                    enabled = current < high,
                    onClick = { onPremiumChange((current + step).coerceAtMost(high)) },
                )
            }
        }

        if (high > low) {
            val sliderValue = current.toFloat()
            Slider(
                value = sliderValue,
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

        TaminOutlinedButton(
            text = stringResource(Res.string.contract_premium_calculate_monthly),
            onClick = onCalculate,
            enabled = !isCalculating,
            icon = vectorResource(Res.drawable.ic_tamin_print),
            borderColor = colors.blueText.copy(alpha = 0.35f),
            containerColor = colors.blueBg,
            contentColor = colors.blueText,
            height = ButtonDimens.height,
            shape = RoundedCornerShape(CornerRadius.xl),
        )

        AnimatedVisibility(
            visible = isPremiumCalculated && calculatedMonthlySalary != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            PremiumResultCard(
                payablePremium = current,
                baseWage = calculatedMonthlySalary ?: 0L,
            )
        }
    }
}

@Composable
private fun PremiumStepperButton(
    icon: ImageVector,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.md)

    Box(
        modifier = modifier
            .size(ShimmerSize.stepperButtonSize)
            .clip(shape)
            .background(if (enabled) colors.bgSurface else colors.bgSurface.copy(alpha = 0.6f))
            .border(Thickness.border, colors.border, shape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.material3.Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) colors.textPrimary else colors.textMuted,
            modifier = Modifier.size(IconSize.small),
        )
    }
}

@Composable
private fun PremiumResultCard(
    payablePremium: Long,
    baseWage: Long,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val resultShape = RoundedCornerShape(CornerRadius.lg)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(resultShape)
            .background(colors.greenBg)
            .border(Thickness.border, colors.greenText.copy(alpha = 0.12f), resultShape)
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(
            text = stringResource(Res.string.contract_premium_payable_label),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = colors.greenText,
        )
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            NumericText(
                text = payablePremium.toPriceFormat(),
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
        Text(
            text = stringResource(
                Res.string.contract_premium_monthly_salary,
                baseWage.toPriceFormat(),
            ),
            style = MaterialTheme.typography.bodySmall,
            color = colors.greenText.copy(alpha = 0.85f),
        )
    }
}

@Composable
private fun PremiumWageSectionShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Box(
            modifier = Modifier
                .width(ShimmerSize.sectionLabelWidth)
                .height(ShimmerSize.titleHeight)
                .clip(RoundedCornerShape(CornerRadius.xs))
                .shimmer(),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(ShimmerSize.stepperButtonSize)
                        .clip(RoundedCornerShape(CornerRadius.md))
                        .shimmer(),
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(ShimmerSize.wageValueHeight),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(ShimmerSize.wageValueHeight)
                        .clip(RoundedCornerShape(CornerRadius.md))
                        .shimmer(),
                )
            }
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(ShimmerSize.stepperButtonSize)
                        .clip(RoundedCornerShape(CornerRadius.md))
                        .shimmer(),
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ShimmerSize.sliderTrackHeight)
                .clip(RoundedCornerShape(CornerRadius.full))
                .shimmer(),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ButtonDimens.height)
                .clip(RoundedCornerShape(CornerRadius.xl))
                .shimmer(),
        )
    }
}
// -------------------------------------------------------------------------
// Previews
// -------------------------------------------------------------------------

private val PreviewRates = listOf(
    SpcPremiumRateOptionPR(code = "1", description = "۱۲ درصد", insurancePercent = "12"),
    SpcPremiumRateOptionPR(code = "2", description = "۱۴ درصد", insurancePercent = "14"),
    SpcPremiumRateOptionPR(code = "3", description = "۱۸ درصد", insurancePercent = "18"),
)

private val PreviewWageScaleRange = FreelancePremiumRangePR(
    lowPremium = 104_400_000L,
    highPremium = 216_578_072L,
    paymentTabayi = 1_000_000L,
    history = 12,
)

private val PreviewPremiumScaleRange = FreelancePremiumRangePR(
    lowPremium = 14_000_000L,
    highPremium = 50_000_000L,
    paymentTabayi = 500_000L,
    history = 12,
)

@PreviewRtlTheme
@Composable
private fun InsurancePremiumStepContentEmptyPreview() {
    PreviewRtlThemeContent {
        InsurancePremiumStepContent(
            premiumRates = PreviewRates,
            selectedCode = null,
            isLoading = false,
            onRateSelected = {},
            premiumRange = null,
            showPremiumSlider = true,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun InsurancePremiumStepContentRateSelectedPreview() {
    PreviewRtlThemeContent {
        InsurancePremiumStepContent(
            premiumRates = PreviewRates,
            selectedCode = "2",
            isLoading = false,
            onRateSelected = {},
            premiumRange = PreviewWageScaleRange,
            selectedPremium = 104_400_000L,
            showPremiumSlider = true,
            onPremiumChange = {},
            onCalculate = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun InsurancePremiumStepContentCalculatedPreview() {
    PreviewRtlThemeContent {
        InsurancePremiumStepContent(
            premiumRates = PreviewRates,
            selectedCode = "2",
            isLoading = false,
            onRateSelected = {},
            premiumRange = PreviewPremiumScaleRange,
            selectedPremium = 26_516_000L,
            calculatedMonthlySalary = 189_400_000L,
            isPremiumCalculated = true,
            showPremiumSlider = true,
            onPremiumChange = {},
            onCalculate = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun InsurancePremiumStepContentShimmerPreview() {
    PreviewRtlThemeContent {
        InsurancePremiumStepShimmerSkeleton()
    }
}

@PreviewRtlTheme
@Composable
private fun InsurancePremiumStepContentWageLoadingPreview() {
    PreviewRtlThemeContent {
        InsurancePremiumStepContent(
            premiumRates = PreviewRates,
            selectedCode = "2",
            isLoading = false,
            onRateSelected = {},
            isPremiumRangeLoading = true,
            premiumRange = null,
            showPremiumSlider = true,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun InsurancePremiumStepContentSixRatesPreview() {
    PreviewRtlThemeContent {
        InsurancePremiumStepContent(
            premiumRates = listOf(
                SpcPremiumRateOptionPR(code = "1", description = "۱۲ درصد", insurancePercent = "12"),
                SpcPremiumRateOptionPR(code = "2", description = "۱۴ درصد", insurancePercent = "14"),
                SpcPremiumRateOptionPR(code = "3", description = "۱۸ درصد", insurancePercent = "18"),
                SpcPremiumRateOptionPR(code = "4", description = "۲۰ درصد", insurancePercent = "20"),
                SpcPremiumRateOptionPR(code = "5", description = "۲۲ درصد", insurancePercent = "22"),
                SpcPremiumRateOptionPR(code = "6", description = "۲۴ درصد", insurancePercent = "24"),
            ),
            selectedCode = "2",
            isLoading = false,
            onRateSelected = {},
            premiumRange = PreviewWageScaleRange,
            selectedPremium = 104_400_000L,
            showPremiumSlider = true,
            onPremiumChange = {},
            onCalculate = {},
        )
    }
}
