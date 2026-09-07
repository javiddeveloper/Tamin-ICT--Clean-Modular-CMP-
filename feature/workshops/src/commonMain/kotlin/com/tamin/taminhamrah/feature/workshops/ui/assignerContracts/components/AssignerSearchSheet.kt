package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.workshops.ui.WorkshopConstants
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFieldError
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSheetBody
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopTextField
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.assigner_filter_clear
import taminx.core.core_ui.assigner_search_branch_code_optional
import taminx.core.core_ui.assigner_search_branch_hint
import taminx.core.core_ui.assigner_search_contract_row_optional
import taminx.core.core_ui.assigner_search_row_hint
import taminx.core.core_ui.assigner_search_workshop
import taminx.core.core_ui.assigner_search_workshop_hint
import taminx.core.core_ui.contract_rows_workshop_code_required
import taminx.core.core_ui.workshop_code

/**
 * جست‌وجوی کارگاه — the three codes the واگذارندگان list is read for.
 *
 * The screen has no other way in: with no کد کارگاه the list would be every پیمان the employer
 * holds, so this raises itself when the screen is opened from the services grid and stays closed
 * when it is opened from جزئیات کارگاه with the workshop already known.
 *
 * کد شعبه and ردیف پیمان really are optional here, unlike on ردیف‌های پیمان: all three travel as
 * filter clauses, so a blank one widens the search rather than addressing a route that 404s.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignerSearchSheet(
    workshopId: String,
    branchCode: String,
    contractRow: String,
    showWorkshopIdError: Boolean,
    isApplying: Boolean,
    canReset: Boolean,
    onWorkshopIdChange: (String) -> Unit,
    onBranchCodeChange: (String) -> Unit,
    onContractRowChange: (String) -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
    ) {
        AssignerSearchSheetContent(
            workshopId = workshopId,
            branchCode = branchCode,
            contractRow = contractRow,
            showWorkshopIdError = showWorkshopIdError,
            isApplying = isApplying,
            canReset = canReset,
            onWorkshopIdChange = onWorkshopIdChange,
            onBranchCodeChange = onBranchCodeChange,
            onContractRowChange = onContractRowChange,
            onApply = onApply,
            onReset = onReset,
        )
    }
}

/**
 * The sheet's body, apart from the sheet.
 *
 * A [ModalBottomSheet] renders as a full-screen scrim in a preview, so what is worth previewing
 * lives here and the wrapper above stays a shell.
 */
@Composable
fun AssignerSearchSheetContent(
    workshopId: String,
    branchCode: String,
    contractRow: String,
    showWorkshopIdError: Boolean,
    isApplying: Boolean,
    canReset: Boolean,
    onWorkshopIdChange: (String) -> Unit,
    onBranchCodeChange: (String) -> Unit,
    onContractRowChange: (String) -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    WorkshopSheetBody(
        title = stringResource(Res.string.assigner_search_workshop),
        modifier = modifier,
    ) {
        WorkshopTextField(
            label = stringResource(Res.string.workshop_code),
            value = workshopId,
            onValueChange = { onWorkshopIdChange(it.digitsOnly()) },
            placeholder = stringResource(Res.string.assigner_search_workshop_hint),
            maxLength = WorkshopConstants.ASSIGNER_WORKSHOP_CODE_LENGTH,
            isRequired = true,
            // Only ever false once the button has been pressed on a blank field: the border must
            // not turn red while the field is still being typed into.
            isValid = if (showWorkshopIdError) false else null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.smd),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.Top,
        ) {
            WorkshopTextField(
                label = stringResource(Res.string.assigner_search_branch_code_optional),
                value = branchCode,
                onValueChange = { onBranchCodeChange(it.digitsOnly()) },
                placeholder = stringResource(Res.string.assigner_search_branch_hint),
                maxLength = WorkshopConstants.ASSIGNER_BRANCH_CODE_LENGTH,
                modifier = Modifier.weight(1f),
            )
            WorkshopTextField(
                label = stringResource(Res.string.assigner_search_contract_row_optional),
                value = contractRow,
                onValueChange = { onContractRowChange(it.digitsOnly()) },
                placeholder = stringResource(Res.string.assigner_search_row_hint),
                maxLength = WorkshopConstants.ASSIGNER_CONTRACT_ROW_LENGTH,
                modifier = Modifier.weight(1f),
            )
        }

        // One message under all three, not on the field: the design puts it there, and only one of
        // the three can be wrong.
        if (showWorkshopIdError) {
            WorkshopFieldError(
                text = stringResource(Res.string.contract_rows_workshop_code_required),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.sm),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.smd),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            TaminPrimaryButton(
                text = stringResource(Res.string.assigner_search_workshop),
                onClick = onApply,
                // The ViewModel drops an apply that arrives while a page is in flight. Disabling
                // the button is that same guard made visible, so the tap does not read as dead.
                enabled = !isApplying,
                background = colors.buttonGradient,
                height = WorkshopDimens.panelButtonHeight,
                modifier = Modifier.weight(1f),
            )
            if (canReset) {
                // An explicit width, because TaminOutlinedButton applies `fillMaxWidth()` after
                // the caller's modifier and would swallow the row given a `weight()`.
                TaminOutlinedButton(
                    text = stringResource(Res.string.assigner_filter_clear),
                    onClick = onReset,
                    height = WorkshopDimens.panelButtonHeight,
                    borderWidth = WorkshopDimens.panelButtonBorderWidth,
                    borderColor = colors.border,
                    containerColor = colors.bgSurface,
                    contentColor = colors.textSecondary,
                    textStyle = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.width(WorkshopDimens.contractRowResetButtonWidth),
                )
            }
        }
    }
}
