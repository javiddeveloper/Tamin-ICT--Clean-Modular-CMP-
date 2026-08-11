package com.tamin.taminhamrah.feature.treatment.ui.records

import com.tamin.taminhamrah.feature.treatment.ui.TreatmentDimens
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.treatment.ui.model.RecordSearchCriteria
import com.tamin.taminhamrah.feature.treatment.ui.model.RecordTab
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.btn_close
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.records_date_from
import taminx.core.core_ui.records_date_to
import taminx.core.core_ui.search_amount_range
import taminx.core.core_ui.search_apply
import taminx.core.core_ui.search_clear
import taminx.core.core_ui.search_custom_range
import taminx.core.core_ui.search_doctor_hint
import taminx.core.core_ui.search_doctor_or_center
import taminx.core.core_ui.search_from_placeholder
import taminx.core.core_ui.search_from_prefix
import taminx.core.core_ui.search_max
import taminx.core.core_ui.search_min
import taminx.core.core_ui.search_service_type
import taminx.core.core_ui.search_title
import taminx.core.core_ui.search_to_placeholder
import taminx.core.core_ui.search_to_prefix
import taminx.core.core_ui.tab_pharmacy

/** Which date field the picker is currently filling, if any. */
private enum class DateField { NONE, FROM, TO }

/**
 * Advanced search bottom sheet for سوابق درمانی.
 * Features title on the right, cross icon on the left, gradient chosen chips, and theme adaptation.
 */
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

    val filterChips = remember { RecordTab.chips }
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Smoothly scroll to selected chip when criteria changes or initial sheet opens
    LaunchedEffect(criteria.tab) {
        val index = filterChips.indexOf(criteria.tab)
        if (index >= 0) {
            lazyListState.animateScrollToItem(index)
        }
    }
        ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.bgSurface,
        shape = RoundedCornerShape(topStart = TreatmentDimens.sheetCornerRadius, topEnd = TreatmentDimens.sheetCornerRadius),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = Spacing.sm, bottom = Spacing.xs)
                    .width(TreatmentDimens.sheetHandleWidth)
                    .height(TreatmentDimens.sheetHandleHeight)
                    .background(colors.border, CircleShape),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.page)
                .padding(bottom = Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            // Header row: title on the right (first child under RTL), close button on the left.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.xs),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.search_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                )

                // The app's one icon button, recolored for a sheet: same 36dp chip and touch
                // target every other icon button in the app gets, rather than a bare glyph.
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_cross),
                    contentDescription = stringResource(Res.string.btn_close),
                    onClick = onDismiss,
                    containerColor = colors.bgPage,
                    contentColor = colors.textSecondary,
                )
            }

            // Section 1: Service Type (نوع خدمت)
            SectionHeader(text = stringResource(Res.string.search_service_type))
            LazyRow(
                state = lazyListState,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                itemsIndexed(filterChips) { index, tab ->
                    ServiceTypeChip(
                        label = stringResource(tab.label),
                        // A tab and داروخانه are the same choice, so picking one clears the other.
                        isSelected = criteria.prescType == null && criteria.tab == tab,
                        onClick = {
                            criteria = criteria.copy(tab = tab, prescType = null)
                            coroutineScope.launch {
                                lazyListState.animateScrollToItem(index)
                            }
                        },
                    )
                }

                // داروخانه has no tab of its own — it is only reachable from here.
                item {
                    ServiceTypeChip(
                        label = stringResource(Res.string.tab_pharmacy),
                        isSelected = criteria.prescType == RecordTab.pharmacyTypeId,
                        onClick = {
                            criteria = criteria.copy(
                                tab = RecordTab.Default,
                                prescType = RecordTab.pharmacyTypeId,
                            )
                        },
                    )
                }
            }

            // Section 2: Custom Date Range (بازهٔ تاریخ دلخواه)
            SectionHeader(text = stringResource(Res.string.search_custom_range))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                DateFieldButton(
                    placeholder = stringResource(Res.string.search_from_placeholder),
                    prefix = stringResource(Res.string.search_from_prefix),
                    value = criteria.startDate,
                    onClick = { editingDate = DateField.FROM },
                    modifier = Modifier.weight(1f),
                )
                DateFieldButton(
                    placeholder = stringResource(Res.string.search_to_placeholder),
                    prefix = stringResource(Res.string.search_to_prefix),
                    value = criteria.endDate,
                    onClick = { editingDate = DateField.TO },
                    modifier = Modifier.weight(1f),
                )
            }

            // Section 3: Doctor or Center Name (نام پزشک یا مرکز)
            SectionHeader(text = stringResource(Res.string.search_doctor_or_center))
            OutlinedTextField(
                value = criteria.nameQuery,
                onValueChange = { criteria = criteria.copy(nameQuery = it) },
                placeholder = {
                    Text(
                        text = stringResource(Res.string.search_doctor_hint),
                        color = colors.textMuted,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors(),
                modifier = Modifier.fillMaxWidth(),
            )

            // Section 4: Cost Range (بازهٔ مبلغ هزینه (ریال))
            SectionHeader(text = stringResource(Res.string.search_amount_range))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                OutlinedTextField(
                    value = criteria.minAmount,
                    onValueChange = { criteria = criteria.copy(minAmount = it) },
                    placeholder = {
                        Text(
                            text = stringResource(Res.string.search_min),
                            color = colors.textMuted,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = textFieldColors(),
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = criteria.maxAmount,
                    onValueChange = { criteria = criteria.copy(maxAmount = it) },
                    placeholder = {
                        Text(
                            text = stringResource(Res.string.search_max),
                            color = colors.textMuted,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = textFieldColors(),
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xs))

            // Action Buttons: Clear on the right (first child in RTL), Apply Search Gradient on the left (second child in RTL)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.xs),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                OutlinedButton(
                    onClick = { criteria = RecordSearchCriteria() },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, colors.border),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.search_clear),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = colors.textSecondary,
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1.5f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(colors.medicalGradient)
                        .clickable { onApply(criteria) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.search_apply),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
            }
        }
    }

    if (editingDate != DateField.NONE) {
        val isFrom = editingDate == DateField.FROM
        TaminJalaliDatePicker(
            title = stringResource(
            if (isFrom) Res.string.records_date_from else Res.string.records_date_to,
        ),
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

@Composable
private fun SectionHeader(text: String) {
    val colors = LocalTaminColors.current
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = colors.textPrimary,
        modifier = Modifier.padding(top = Spacing.xs),
    )
}

/** One «نوع خدمت» pill. Extracted so the tabs and داروخانه cannot drift apart visually. */
@Composable
private fun ServiceTypeChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalTaminColors.current
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .then(
                if (isSelected) Modifier.background(colors.medicalGradient)
                else Modifier
                    .background(colors.bgSurface)
                    .border(1.dp, colors.border, CircleShape),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else colors.textSecondary,
        )
    }
}

/** A date field button matching the input box design in the mockup. */
@Composable
private fun DateFieldButton(
    placeholder: String,
    prefix: String,
    value: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val displayText = remember(value) {
        if (value.isNullOrBlank()) {
            placeholder
        } else {
            val formatted = PersianDateFormatter.formatTimestamp(value.toLongOrNull())
            if (formatted.isNotBlank()) "$prefix ($formatted)" else placeholder
        }
    }

    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colors.bgSurface)
            .border(1.dp, colors.border, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = displayText,
            style = MaterialTheme.typography.bodyMedium,
            color = if (value.isNullOrBlank()) colors.textMuted else colors.textPrimary,
            textAlign = TextAlign.Start,
        )
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = LocalTaminColors.current.teal,
    unfocusedBorderColor = LocalTaminColors.current.border,
    focusedContainerColor = LocalTaminColors.current.bgSurface,
    unfocusedContainerColor = LocalTaminColors.current.bgSurface,
    focusedTextColor = LocalTaminColors.current.textPrimary,
    unfocusedTextColor = LocalTaminColors.current.textPrimary,
)
