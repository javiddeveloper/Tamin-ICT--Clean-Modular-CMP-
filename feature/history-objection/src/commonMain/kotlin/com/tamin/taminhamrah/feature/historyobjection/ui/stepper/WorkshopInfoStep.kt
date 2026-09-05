package com.tamin.taminhamrah.feature.historyobjection.ui.stepper

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.tamin.taminhamrah.feature.historyobjection.ui.stepper.contract.HistoryObjectionStepperIntent
import com.tamin.taminhamrah.feature.historyobjection.ui.stepper.contract.HistoryObjectionStepperState
import com.tamin.taminhamrah.ui.components.SegmentedInputField
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.history_objection_employer_name_label
import taminx.core.core_ui.history_objection_employer_name_placeholder
import taminx.core.core_ui.history_objection_step_workshop_title
import taminx.core.core_ui.history_objection_workshop_address_label
import taminx.core.core_ui.history_objection_workshop_address_placeholder
import taminx.core.core_ui.history_objection_workshop_id_label
import taminx.core.core_ui.history_objection_workshop_id_placeholder
import taminx.core.core_ui.history_objection_workshop_name
import taminx.core.core_ui.history_objection_workshop_name_placeholder

private const val WORKSHOP_ID_LENGTH = 10

@Composable
fun WorkshopInfoStep(
    state: HistoryObjectionStepperState,
    onIntent: (HistoryObjectionStepperIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.lg),
    ) {
        HistoryObjectionStepTitle(stringResource(Res.string.history_objection_step_workshop_title))
        Spacer(modifier = Modifier.height(Spacing.lg))

        HistoryObjectionFieldLabel(stringResource(Res.string.history_objection_workshop_id_label))
        Spacer(modifier = Modifier.height(Spacing.sm))
        SegmentedInputField(
            value = state.workshopId,
            onValueChange = { if (it.length <= WORKSHOP_ID_LENGTH) onIntent(HistoryObjectionStepperIntent.OnWorkshopIdChanged(it)) },
            slotCount = WORKSHOP_ID_LENGTH,
            placeholderText = stringResource(Res.string.history_objection_workshop_id_placeholder),
            keyboardType = KeyboardType.Number,
        )
        Spacer(modifier = Modifier.height(Spacing.smd))

        HistoryObjectionTextFieldRow(
            label = stringResource(Res.string.history_objection_workshop_name),
            value = state.workshopName,
            placeholder = stringResource(Res.string.history_objection_workshop_name_placeholder),
            onValueChange = { onIntent(HistoryObjectionStepperIntent.OnWorkshopNameChanged(it)) },
        )
        Spacer(modifier = Modifier.height(Spacing.smd))

        HistoryObjectionTextFieldRow(
            label = stringResource(Res.string.history_objection_employer_name_label),
            value = state.employerName,
            placeholder = stringResource(Res.string.history_objection_employer_name_placeholder),
            onValueChange = { onIntent(HistoryObjectionStepperIntent.OnEmployerNameChanged(it)) },
        )
        Spacer(modifier = Modifier.height(Spacing.smd))

        HistoryObjectionTextFieldRow(
            label = stringResource(Res.string.history_objection_workshop_address_label),
            value = state.workshopAddress,
            placeholder = stringResource(Res.string.history_objection_workshop_address_placeholder),
            onValueChange = { onIntent(HistoryObjectionStepperIntent.OnWorkshopAddressChanged(it)) },
        )
    }
}
