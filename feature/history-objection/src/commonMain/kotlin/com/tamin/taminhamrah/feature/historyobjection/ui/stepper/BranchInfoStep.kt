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
import com.tamin.taminhamrah.feature.historyobjection.ui.stepper.contract.HistoryObjectionStepperIntent
import com.tamin.taminhamrah.feature.historyobjection.ui.stepper.contract.HistoryObjectionStepperState
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.history_objection_branch_label
import taminx.core.core_ui.history_objection_branch_placeholder
import taminx.core.core_ui.history_objection_city_label
import taminx.core.core_ui.history_objection_city_placeholder
import taminx.core.core_ui.history_objection_insurance_type_label
import taminx.core.core_ui.history_objection_insurance_type_placeholder
import taminx.core.core_ui.history_objection_province_label
import taminx.core.core_ui.history_objection_province_placeholder
import taminx.core.core_ui.history_objection_step_branch_title
import taminx.core.core_ui.ic_arrow_down
import taminx.core.core_ui.ic_tamin_chevron_back

@Composable
fun BranchInfoStep(
    state: HistoryObjectionStepperState,
    onIntent: (HistoryObjectionStepperIntent) -> Unit,
) {
    val chevron = vectorResource(Res.drawable.ic_tamin_chevron_back)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.lg),
    ) {
        HistoryObjectionStepTitle(stringResource(Res.string.history_objection_step_branch_title))
        Spacer(modifier = Modifier.height(Spacing.lg))

        HistoryObjectionSelectableFieldRow(
            label = stringResource(Res.string.history_objection_province_label),
            value = state.selectedProvince?.provinceName.orEmpty(),
            placeholder = stringResource(Res.string.history_objection_province_placeholder),
            trailingIcon = chevron,
            onClick = { onIntent(HistoryObjectionStepperIntent.OnShowProvincePicker) },
        )
        Spacer(modifier = Modifier.height(Spacing.smd))

        HistoryObjectionSelectableFieldRow(
            label = stringResource(Res.string.history_objection_city_label),
            value = state.selectedCity?.cityName.orEmpty(),
            placeholder = stringResource(Res.string.history_objection_city_placeholder),
            trailingIcon = chevron,
            onClick = { onIntent(HistoryObjectionStepperIntent.OnShowCityPicker) },
        )
        Spacer(modifier = Modifier.height(Spacing.smd))

        HistoryObjectionSelectableFieldRow(
            label = stringResource(Res.string.history_objection_branch_label),
            value = state.selectedBranch?.displayName.orEmpty(),
            placeholder = stringResource(Res.string.history_objection_branch_placeholder),
            trailingIcon = chevron,
            onClick = { onIntent(HistoryObjectionStepperIntent.OnShowBranchPicker) },
        )
        Spacer(modifier = Modifier.height(Spacing.smd))

        HistoryObjectionSelectableFieldRow(
            label = stringResource(Res.string.history_objection_insurance_type_label),
            value = state.selectedInsuranceType?.insuranceTypeDesc.orEmpty(),
            placeholder = stringResource(Res.string.history_objection_insurance_type_placeholder),
            trailingIcon = chevron,
            onClick = { onIntent(HistoryObjectionStepperIntent.OnShowInsuranceTypePicker) },
        )
    }
}
