package com.tamin.taminhamrah.feature.calculateWagePension.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Velocity
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.SheetDimens
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_close
import taminx.core.core_ui.calculate_wage_pension_info_avg_label
import taminx.core.core_ui.calculate_wage_pension_info_estimated_label
import taminx.core.core_ui.calculate_wage_pension_info_floor_label
import taminx.core.core_ui.calculate_wage_pension_info_formula
import taminx.core.core_ui.calculate_wage_pension_info_formula_label
import taminx.core.core_ui.calculate_wage_pension_info_note_1
import taminx.core.core_ui.calculate_wage_pension_info_note_2
import taminx.core.core_ui.calculate_wage_pension_info_note_3
import taminx.core.core_ui.calculate_wage_pension_info_note_4
import taminx.core.core_ui.calculate_wage_pension_info_title
import taminx.core.core_ui.calculate_wage_pension_info_years_label
import taminx.core.core_ui.ic_info
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CalculateWagePensionInfoSheet(
    averageSalary: Long,
    premiumYears: Double,
    basicWage: Long,
    estimatedAmount: Long,
    onDismiss: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val dismissSheet: () -> Unit = {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.bgSurface,
        shape = RoundedCornerShape(topStart = CornerRadius.sheet, topEnd = CornerRadius.sheet),
    ) {
        InfoSheetContent(
            averageSalary = averageSalary,
            premiumYears = premiumYears,
            basicWage = basicWage,
            estimatedAmount = estimatedAmount,
            onDismiss = dismissSheet,
            modifier = Modifier.navigationBarsPadding(),
        )
    }
}

@Composable
internal fun InfoSheetContent(
    averageSalary: Long,
    premiumYears: Double,
    basicWage: Long,
    estimatedAmount: Long,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val notes = listOf(
        stringResource(Res.string.calculate_wage_pension_info_note_1),
        stringResource(Res.string.calculate_wage_pension_info_note_2),
        stringResource(Res.string.calculate_wage_pension_info_note_3),
        stringResource(Res.string.calculate_wage_pension_info_note_4),
    )
    // Leftover fling at scroll bounds must not start a sheet dismiss / bounce.
    val consumeOverscroll = remember {
        object : NestedScrollConnection {
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity =
                available
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.page)
            .padding(bottom = Spacing.page),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Text(
            text = stringResource(Res.string.calculate_wage_pension_info_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = SheetDimens.contentMaxHeight)
                .nestedScroll(consumeOverscroll),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                FormulaCard(
                    averageSalary = averageSalary,
                    premiumYears = premiumYears,
                    basicWage = basicWage,
                    estimatedAmount = estimatedAmount,
                )
                notes.forEach { note ->
                    NoteCard(text = note)
                }
            }
        }

        TaminFilledButton(
            text = stringResource(Res.string.action_close),
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun FormulaCard(
    averageSalary: Long,
    premiumYears: Double,
    basicWage: Long,
    estimatedAmount: Long,
) {
    val colors = LocalTaminColors.current
    val cardBrush = remember(colors.profileGradientStops) {
        Brush.verticalGradient(colors.profileGradientStops)
    }
    val rows = listOf(
        FormulaRow(
            label = stringResource(Res.string.calculate_wage_pension_info_avg_label),
            value = averageSalary.toPriceFormat().toPersianDigits(),
            emphasize = false,
        ),
        FormulaRow(
            label = stringResource(Res.string.calculate_wage_pension_info_years_label),
            value = formatPremiumYears(premiumYears).toPersianDigits(),
            emphasize = false,
        ),
        FormulaRow(
            label = stringResource(Res.string.calculate_wage_pension_info_floor_label),
            value = basicWage.toPriceFormat().toPersianDigits(),
            emphasize = false,
        ),
        FormulaRow(
            label = stringResource(Res.string.calculate_wage_pension_info_estimated_label),
            value = estimatedAmount.toPriceFormat().toPersianDigits(),
            emphasize = true,
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(cardBrush, RoundedCornerShape(CornerRadius.cardCompact))
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Text(
            text = stringResource(Res.string.calculate_wage_pension_info_formula_label),
            style = MaterialTheme.typography.labelMedium,
            color = colors.onGradient.copy(alpha = 0.8f),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
        )

        Text(
            text = stringResource(Res.string.calculate_wage_pension_info_formula),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.onGradient,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    colors.onGradient.copy(alpha = 0.12f),
                    RoundedCornerShape(CornerRadius.lg),
                )
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        )

        rows.forEachIndexed { index, row ->
            FormulaValueRow(
                label = row.label,
                value = row.value,
                valueColor = if (row.emphasize) colors.blueBg else colors.onGradient,
            )
            if (index < rows.lastIndex) {
                DashedDivider(color = colors.onGradient.copy(alpha = 0.25f))
            }
        }
    }
}

@Composable
private fun FormulaValueRow(
    label: String,
    value: String,
    valueColor: Color,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = colors.onGradient.copy(alpha = 0.85f),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = valueColor,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = Spacing.sm),
        )
    }
}

@Composable
private fun DashedDivider(color: Color) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(Thickness.border),
    ) {
        val dash = PathEffect.dashPathEffect(
            floatArrayOf(Thickness.border.toPx() * 4f, Thickness.border.toPx() * 4f),
        )
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2f),
            end = Offset(size.width, size.height / 2f),
            strokeWidth = size.height,
            pathEffect = dash,
        )
    }
}

@Composable
private fun NoteCard(text: String) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(Thickness.border, colors.border, RoundedCornerShape(CornerRadius.listRow))
            .background(colors.bgSurface, RoundedCornerShape(CornerRadius.listRow))
            .padding(Spacing.md),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_info),
            contentDescription = null,
            tint = colors.blueText,
            modifier = Modifier.size(IconSize.banner),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f),
        )
    }
}

private data class FormulaRow(
    val label: String,
    val value: String,
    val emphasize: Boolean,
)

private fun formatPremiumYears(value: Double): String {
    val rounded = round(value * 100.0) / 100.0
    val asLong = rounded.toLong()
    return if (rounded == asLong.toDouble()) asLong.toString() else rounded.toString()
}

@PreviewRtlTheme
@Composable
private fun CalculateWagePensionInfoSheetContentPreview() {
    PreviewRtlThemeContent {
        InfoSheetContent(
            averageSalary = 87_980_000L,
            premiumYears = 7.33,
            basicWage = 104_300_000L,
            estimatedAmount = 25_483_967L,
            onDismiss = {},
            modifier = Modifier.padding(Spacing.page),
        )
    }
}
