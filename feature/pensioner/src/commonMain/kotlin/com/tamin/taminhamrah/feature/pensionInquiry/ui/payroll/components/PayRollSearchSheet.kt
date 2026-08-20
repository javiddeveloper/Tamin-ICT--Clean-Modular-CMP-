package com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.pension.PaymentTypeDN
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_search
import taminx.core.core_ui.payroll_date_picker_title
import taminx.core.core_ui.payroll_search_clear
import taminx.core.core_ui.payroll_search_date_label
import taminx.core.core_ui.payroll_search_payment_type_label
import taminx.core.core_ui.payroll_search_submit
import taminx.core.core_ui.payroll_search_title

/**
 * "جست‌وجوی فیش" — a date row (opens [PayRollDatePickerSheet]) + a payment-type single-select
 * list + submit. Single-step, unlike Edict's two-step year→month flow, matching the mockup.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayRollSearchSheet(
    searchYear: String,
    searchMonth: String,
    searchPaymentType: String,
    onYearChanged: (String) -> Unit,
    onMonthChanged: (String) -> Unit,
    onPaymentTypeChanged: (String) -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit,
    onClear: () -> Unit = {},
) {
    val taminColors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDatePicker by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = taminColors.bgPage,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg)
                .navigationBarsPadding()
                .padding(bottom = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TaminText(
                    text = stringResource(Res.string.payroll_search_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = taminColors.textPrimary,
                )
                TaminText(
                    text = stringResource(Res.string.payroll_search_clear),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = taminColors.blueText,
                    modifier = Modifier
                        .clip(RoundedCornerShape(CornerRadius.full))
                        .clickable(onClick = onClear)
                        .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                TaminText(
                    text = stringResource(Res.string.payroll_search_date_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = taminColors.textSecondary,
                    modifier = Modifier.padding(start = 8.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            color = taminColors.bgSurface,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(1.dp, taminColors.border, RoundedCornerShape(12.dp))
                        .clickable { showDatePicker = true }
                        .padding(Spacing.md),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val label = if (searchYear.isNotEmpty() && searchMonth.isNotEmpty()) {
                        formatPayRollDateLabel("$searchYear$searchMonth")
                    } else {
                        stringResource(Res.string.payroll_date_picker_title)
                    }
                    TaminText(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = taminColors.textPrimary,
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = taminColors.textSecondary,
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                TaminText(
                    text = stringResource(Res.string.payroll_search_payment_type_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = taminColors.textSecondary,
                    modifier = Modifier.padding(start = 8.dp),
                )
                PaymentTypeDN.entries.forEach { type ->
                    PaymentTypeOptionRow(
                        label = payRollPaymentTypeLabel(type.code),
                        isSelected = type.code == searchPaymentType,
                        onClick = { onPaymentTypeChanged(type.code) },
                    )
                }
            }

            TaminFilledButton(
                text = stringResource(Res.string.payroll_search_submit),
                onClick = onApply,
                modifier = Modifier.fillMaxWidth(),
                enabled = searchYear.isNotEmpty() && searchMonth.isNotEmpty(),
                painter = painterResource(Res.drawable.ic_tamin_search),
            )
        }
    }

    if (showDatePicker) {
        PayRollDatePickerSheet(
            searchYear = searchYear,
            searchMonth = searchMonth,
            onYearChanged = onYearChanged,
            onMonthChanged = onMonthChanged,
            onConfirm = { showDatePicker = false },
            onDismiss = { showDatePicker = false },
        )
    }
}

@Composable
private fun PaymentTypeOptionRow(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val taminColors = LocalTaminColors.current
    val borderColor = if (isSelected) taminColors.blueText else taminColors.border
    val bgColor = if (isSelected) taminColors.blueBg else taminColors.bgSurface
    val titleColor = if (isSelected) taminColors.blueText else taminColors.textPrimary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = taminColors.blueText,
                unselectedColor = taminColors.border,
            ),
        )
        Spacer(Modifier.width(Spacing.sm))
        TaminText(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = titleColor,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PayRollSearchSheetPreview() {
    PreviewRtlThemeContent {
        PayRollSearchSheet(
            searchYear = "1404",
            searchMonth = "05",
            searchPaymentType = PaymentTypeDN.MONTHLY.code,
            onYearChanged = {},
            onMonthChanged = {},
            onPaymentTypeChanged = {},
            onApply = {},
            onDismiss = {},
        )
    }
}
