package com.tamin.taminhamrah.feature.historyobjection.ui.stepper

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.tamin.taminhamrah.feature.historyobjection.ui.stepper.contract.HistoryObjectionStepperIntent
import com.tamin.taminhamrah.feature.historyobjection.ui.stepper.contract.HistoryObjectionStepperState
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.history_objection_date_placeholder
import taminx.core.core_ui.history_objection_step_record_title
import taminx.core.core_ui.history_objection_work_days_label
import taminx.core.core_ui.history_objection_work_days_placeholder
import taminx.core.core_ui.history_objection_work_end_date_label
import taminx.core.core_ui.history_objection_work_start_date_label
import taminx.core.core_ui.ic_tamin_calendar

@Composable
fun RecordInfoStep(
    state: HistoryObjectionStepperState,
    onIntent: (HistoryObjectionStepperIntent) -> Unit,
) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val calendarIcon = vectorResource(Res.drawable.ic_tamin_calendar)
    val datePlaceholder = stringResource(Res.string.history_objection_date_placeholder)

    if (showStartDatePicker) {
        TaminJalaliDatePicker(
            title = stringResource(Res.string.history_objection_work_start_date_label),
            onDismiss = { showStartDatePicker = false },
            onConfirm = { year, month, day ->
                onIntent(HistoryObjectionStepperIntent.OnStartDateSelected(year, month, day))
                showStartDatePicker = false
            },
        )
    }
    if (showEndDatePicker) {
        TaminJalaliDatePicker(
            title = stringResource(Res.string.history_objection_work_end_date_label),
            onDismiss = { showEndDatePicker = false },
            onConfirm = { year, month, day ->
                onIntent(HistoryObjectionStepperIntent.OnEndDateSelected(year, month, day))
                showEndDatePicker = false
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.lg),
    ) {
        HistoryObjectionStepTitle(stringResource(Res.string.history_objection_step_record_title))
        Spacer(modifier = Modifier.height(Spacing.lg))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(Spacing.smd),
        ) {
            HistoryObjectionSelectableFieldRow(
                label = stringResource(Res.string.history_objection_work_start_date_label),
                value = state.startDateLabel,
                placeholder = datePlaceholder,
                trailingIcon = calendarIcon,
                onClick = { showStartDatePicker = true },
                modifier = Modifier.weight(1f),
            )
            HistoryObjectionSelectableFieldRow(
                label = stringResource(Res.string.history_objection_work_end_date_label),
                value = state.endDateLabel,
                placeholder = datePlaceholder,
                trailingIcon = calendarIcon,
                onClick = { showEndDatePicker = true },
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(modifier = Modifier.height(Spacing.smd))

        HistoryObjectionTextFieldRow(
            label = stringResource(Res.string.history_objection_work_days_label),
            value = state.workDays,
            placeholder = stringResource(Res.string.history_objection_work_days_placeholder),
            onValueChange = { onIntent(HistoryObjectionStepperIntent.OnWorkDaysChanged(it.toAsciiDigitsOnly())) },
            keyboardType = KeyboardType.Number,
        )
    }
}
private fun String.toAsciiDigitsOnly(): String = mapNotNull { char ->
    when (char) {
        in '0'..'9' -> char
        in '۰'..'۹' -> '0' + (char - '۰')
        else -> null
    }
}.joinToString("")
