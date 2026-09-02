package com.tamin.taminhamrah.feature.retirementPension.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementFormError
import com.tamin.taminhamrah.model.pension.retirement.RetirementBranchInfoPR
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.TaminTextArea
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.retirement_pension_field_activity_type
import taminx.core.core_ui.retirement_pension_field_activity_type_placeholder
import taminx.core.core_ui.retirement_pension_field_employer_name
import taminx.core.core_ui.retirement_pension_field_employer_name_placeholder
import taminx.core.core_ui.retirement_pension_field_workshop_address
import taminx.core.core_ui.retirement_pension_field_workshop_address_placeholder
import taminx.core.core_ui.retirement_pension_field_workshop_code
import taminx.core.core_ui.retirement_pension_field_workshop_code_placeholder
import taminx.core.core_ui.retirement_pension_field_workshop_name
import taminx.core.core_ui.retirement_pension_label_branch_code
import taminx.core.core_ui.retirement_pension_label_branch_name
import taminx.core.core_ui.retirement_pension_value_placeholder
import taminx.core.core_ui.retirement_pension_workshop_confirm

/** Digits in a workshop code. */
private const val WORKSHOP_CODE_SLOT_COUNT = 10

/** Step 4 — the branch handling the request, and the last workshop the insured person worked at. */
@Composable
internal fun RetirementWorkshopStep(
    branch: RetirementBranchInfoPR?,
    workshopName: String,
    workshopCode: String,
    workshopAddress: String,
    employerName: String,
    activityType: String,
    isConfirmed: Boolean,
    error: RetirementFormError?,
    onWorkshopNameChange: (String) -> Unit,
    onWorkshopCodeChange: (String) -> Unit,
    onWorkshopAddressChange: (String) -> Unit,
    onEmployerNameChange: (String) -> Unit,
    onActivityTypeChange: (String) -> Unit,
    onConfirmedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val placeholder = stringResource(Res.string.retirement_pension_value_placeholder)

    RetirementStepColumn(modifier = modifier) {
        RetirementCard {
            DetailRow(
                label = stringResource(Res.string.retirement_pension_label_branch_name),
                value = branch?.branchName?.ifBlank { placeholder } ?: placeholder,
                numeric = false,
            )
            DetailRow(
                label = stringResource(Res.string.retirement_pension_label_branch_code),
                value = branch?.branchCode?.ifBlank { placeholder } ?: placeholder,
            )
        }

        RetirementTextField(
            value = workshopName,
            onValueChange = onWorkshopNameChange,
            label = stringResource(Res.string.retirement_pension_field_workshop_name),
            isRequired = true,
            isError = error == RetirementFormError.WorkshopName,
            errorMessage = error?.takeIf { it == RetirementFormError.WorkshopName }?.text(),
        )

        // Ten slots need the full width; the design's narrow box beside the name cannot hold them.
        RetirementNumericField(
            value = workshopCode,
            onValueChange = onWorkshopCodeChange,
            label = stringResource(Res.string.retirement_pension_field_workshop_code),
            slotCount = WORKSHOP_CODE_SLOT_COUNT,
            isError = error == RetirementFormError.WorkshopCode,
            errorMessage = error?.takeIf { it == RetirementFormError.WorkshopCode }?.text(),
            placeholderText = stringResource(
                Res.string.retirement_pension_field_workshop_code_placeholder,
            ),
        )

        TaminTextArea(
            value = workshopAddress,
            onValueChange = onWorkshopAddressChange,
            label = stringResource(Res.string.retirement_pension_field_workshop_address),
            placeholder = stringResource(
                Res.string.retirement_pension_field_workshop_address_placeholder,
            ),
            isRequired = true,
            error = error == RetirementFormError.WorkshopAddress,
            errorMessage = error?.takeIf { it == RetirementFormError.WorkshopAddress }?.text(),
            minLines = 2,
            maxLines = 4,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.cardGap),
        ) {
            RetirementTextField(
                value = employerName,
                onValueChange = onEmployerNameChange,
                label = stringResource(Res.string.retirement_pension_field_employer_name),
                isRequired = false,
                placeholder = stringResource(
                    Res.string.retirement_pension_field_employer_name_placeholder,
                ),
                modifier = Modifier.weight(1f),
            )
            RetirementTextField(
                value = activityType,
                onValueChange = onActivityTypeChange,
                label = stringResource(Res.string.retirement_pension_field_activity_type),
                isRequired = false,
                placeholder = stringResource(
                    Res.string.retirement_pension_field_activity_type_placeholder,
                ),
                modifier = Modifier.weight(1f),
            )
        }

        RetirementConsentRow(
            text = stringResource(Res.string.retirement_pension_workshop_confirm),
            checked = isConfirmed,
            isError = error == RetirementFormError.WorkshopConfirm,
            onCheckedChange = onConfirmedChange,
        )
        if (error == RetirementFormError.WorkshopConfirm) RetirementErrorLine(error)
    }
}
