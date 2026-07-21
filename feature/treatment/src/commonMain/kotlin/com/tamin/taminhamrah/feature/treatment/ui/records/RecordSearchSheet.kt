package com.tamin.taminhamrah.feature.treatment.ui.records

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.feature.treatment.ui.components.TreatmentFilterChipRow
import com.tamin.taminhamrah.feature.treatment.ui.model.RecordSearchCriteria
import com.tamin.taminhamrah.feature.treatment.ui.model.RecordTab
import com.tamin.taminhamrah.ui.components.SectionLabel
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.PersianDateFormatter

/** Which date field the picker is currently filling, if any. */
private enum class DateField { NONE, FROM, TO }

/**
 * Advanced search for سوابق درمانی.
 *
 * The category and the date range become query parameters; the name and amount bounds are applied
 * to the results, because the endpoint cannot filter on them.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordSearchSheet(
    initial: RecordSearchCriteria,
    onDismiss: () -> Unit,
    onApply: (RecordSearchCriteria) -> Unit,
) {
    val colors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var criteria by remember { mutableStateOf(initial) }
    var editingDate by remember { mutableStateOf(DateField.NONE) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.bgSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.page)
                .padding(bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = "جست‌وجوی پیشرفته",
                style = androidx.compose.material3.MaterialTheme.typography.titleSmall,
                color = colors.textPrimary,
            )

            SectionLabel(text = "نوع خدمت")
            TreatmentFilterChipRow(
                categories = RecordTab.chips.map { it.label },
                selectedIndex = RecordTab.chips.indexOf(criteria.tab).coerceAtLeast(0),
                onSelect = { criteria = criteria.copy(tab = RecordTab.chips[it]) },
            )

            SectionLabel(text = "بازهٔ تاریخ دلخواه")
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                DateFieldButton(
                    label = "از",
                    value = criteria.startDate,
                    onClick = { editingDate = DateField.FROM },
                    modifier = Modifier.weight(1f),
                )
                DateFieldButton(
                    label = "تا",
                    value = criteria.endDate,
                    onClick = { editingDate = DateField.TO },
                    modifier = Modifier.weight(1f),
                )
            }

            SectionLabel(text = "نام پزشک یا مرکز")
            OutlinedTextField(
                value = criteria.nameQuery,
                onValueChange = { criteria = criteria.copy(nameQuery = it) },
                placeholder = { Text("مثلاً دکتر محمدی یا آزمایشگاه مرکزی") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            SectionLabel(text = "بازهٔ مبلغ هزینه (ریال)")
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                OutlinedTextField(
                    value = criteria.minAmount,
                    onValueChange = { criteria = criteria.copy(minAmount = it) },
                    placeholder = { Text("حداقل") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = criteria.maxAmount,
                    onValueChange = { criteria = criteria.copy(maxAmount = it) },
                    placeholder = { Text("حداکثر") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                OutlinedButton(
                    onClick = { criteria = RecordSearchCriteria() },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = "پاک کردن", color = colors.textSecondary)
                }
                TaminPrimaryButton(
                    text = "اعمال جست‌وجو",
                    onClick = { onApply(criteria) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }

    if (editingDate != DateField.NONE) {
        val isFrom = editingDate == DateField.FROM
        TaminJalaliDatePicker(
            title = if (isFrom) "از تاریخ" else "تا تاریخ",
            onDismiss = { editingDate = DateField.NONE },
            onConfirm = { y, m, d ->
                val millis = PersianDateFormatter.toEpochMillis(y, m, d).toString()
                criteria = if (isFrom) {
                    criteria.copy(startDate = millis)
                } else {
                    criteria.copy(endDate = millis)
                }
                editingDate = DateField.NONE
            },
        )
    }
}

/** A date slot that reads as a field but opens the Jalali picker. */
@Composable
private fun DateFieldButton(
    label: String,
    value: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    OutlinedButton(onClick = onClick, modifier = modifier) {
        Text(
            text = value?.let { PersianDateFormatter.formatTimestamp(it.toLongOrNull()) } ?: label,
            color = if (value == null) colors.textTertiary else colors.textPrimary,
            textAlign = TextAlign.Center,
        )
    }
}
