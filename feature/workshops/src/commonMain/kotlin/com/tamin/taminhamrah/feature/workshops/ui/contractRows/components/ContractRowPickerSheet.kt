package com.tamin.taminhamrah.feature.workshops.ui.contractRows.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.feature.workshops.ui.WorkshopConstants
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopQuickPickList
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopTextField
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.model.workshop.WorkshopPR
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_rows_apply
import taminx.core.core_ui.contract_rows_branch_code_required_hint
import taminx.core.core_ui.contract_rows_four_digits
import taminx.core.core_ui.contract_rows_pick_workshop
import taminx.core.core_ui.contract_rows_pick_workshop_hint
import taminx.core.core_ui.contract_rows_reset
import taminx.core.core_ui.contract_rows_workshop_code_required
import taminx.core.core_ui.workshop_branch_code
import taminx.core.core_ui.workshop_code
import taminx.core.core_ui.workshop_ten_digits

/**
 * انتخاب کارگاه — the two codes the list is read for.
 *
 * The screen has no other way in: both endpoints take the workshop and branch as path segments, so
 * until this sheet is answered there is no request to make. It is therefore raised automatically
 * when the screen is opened from the services grid, and left closed when it is opened from
 * جزئیات کارگاه with the identity already known.
 *
 * کد شعبه is optional because the service accepts the workshop alone; کد کارگاه is not, and
 * submitting without it puts the message on that field rather than doing nothing.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContractRowPickerSheet(
    workshopId: String,
    branchCode: String,
    showWorkshopIdError: Boolean,
    showBranchCodeError: Boolean,
    isApplying: Boolean,
    myWorkshops: ImmutableList<WorkshopPR>,
    myWorkshopsTotal: Int,
    canReset: Boolean,
    onWorkshopIdChange: (String) -> Unit,
    onBranchCodeChange: (String) -> Unit,
    onQuickPick: (String, String) -> Unit,
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
        ContractRowPickerContent(
            workshopId = workshopId,
            branchCode = branchCode,
            showWorkshopIdError = showWorkshopIdError,
            showBranchCodeError = showBranchCodeError,
            isApplying = isApplying,
            myWorkshops = myWorkshops,
            myWorkshopsTotal = myWorkshopsTotal,
            canReset = canReset,
            onWorkshopIdChange = onWorkshopIdChange,
            onBranchCodeChange = onBranchCodeChange,
            onQuickPick = onQuickPick,
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
fun ContractRowPickerContent(
    workshopId: String,
    branchCode: String,
    showWorkshopIdError: Boolean,
    showBranchCodeError: Boolean,
    isApplying: Boolean,
    myWorkshops: ImmutableList<WorkshopPR>,
    myWorkshopsTotal: Int,
    canReset: Boolean,
    onWorkshopIdChange: (String) -> Unit,
    onBranchCodeChange: (String) -> Unit,
    onQuickPick: (String, String) -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            // کارگاه‌های شما is up to a full page of workshops, and on a short screen that pushed
            // «مشاهدهٔ ردیف پیمان‌ها» past the bottom edge with no way to reach it — the sheet had
            // no scroll of its own. Bounded at one page, so a plain scroll is right here; a lazy
            // list inside a sheet that already scrolls would nest two scrollers.
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.page)
            .padding(
                top = Spacing.smd,
                bottom = Spacing.page,
            )
            .padding(WindowInsets.navigationBars.asPaddingValues()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // The sheet's own handle is switched off above, so the design's grabber is drawn here.
        Box(
            modifier = Modifier
                .padding(bottom = Spacing.smd)
                .size(
                    width = WorkshopDimens.contractRowGrabberWidth,
                    height = WorkshopDimens.contractRowGrabberHeight,
                )
                .background(colors.border, RoundedCornerShape(CornerRadius.full)),
        )

        Text(
            text = stringResource(Res.string.contract_rows_pick_workshop),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(Res.string.contract_rows_pick_workshop_hint),
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = Spacing.xs, bottom = Spacing.smd),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.Top,
        ) {
            WorkshopTextField(
                label = stringResource(Res.string.workshop_code),
                value = workshopId,
                onValueChange = { onWorkshopIdChange(it.digitsOnly()) },
                placeholder = stringResource(Res.string.workshop_ten_digits),
                maxLength = WorkshopConstants.CONTRACT_ROW_WORKSHOP_CODE_LENGTH,
                isRequired = true,
                // Only ever false once the button has been pressed on a blank field: the border
                // must not turn red while the field is still being typed into.
                isValid = if (showWorkshopIdError) false else null,
                errorText = stringResource(Res.string.contract_rows_workshop_code_required)
                    .takeIf { showWorkshopIdError },
                modifier = Modifier.weight(WorkshopDimens.contractRowWorkshopFieldWeight),
            )
            WorkshopTextField(
                label = stringResource(Res.string.workshop_branch_code),
                value = branchCode,
                onValueChange = { onBranchCodeChange(it.digitsOnly()) },
                placeholder = stringResource(Res.string.contract_rows_four_digits),
                maxLength = WorkshopConstants.CONTRACT_ROW_BRANCH_CODE_LENGTH,
                // Required, despite reading like a filter: it is a path segment, and a blank one
                // addresses `…/{workshopId}/`, which the service answers with 404.
                isRequired = true,
                isValid = if (showBranchCodeError) false else null,
                errorText = stringResource(Res.string.contract_rows_branch_code_required_hint)
                    .takeIf { showBranchCodeError },
                modifier = Modifier.weight(WorkshopDimens.contractRowBranchFieldWeight),
            )
        }

        WorkshopQuickPickList(
            workshops = myWorkshops,
            total = myWorkshopsTotal,
            selectedWorkshopId = workshopId,
            onPick = onQuickPick,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.smd),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            TaminPrimaryButton(
                text = stringResource(Res.string.contract_rows_apply),
                onClick = onApply,
                // The ViewModel drops an apply that arrives while a page is in flight. Disabling
                // the button is that same guard made visible, so the tap does not read as dead.
                enabled = !isApplying,
                background = colors.buttonGradient,
                height = WorkshopDimens.panelButtonHeight,
                modifier = Modifier.weight(1f),
            )
            if (canReset) {
                TaminOutlinedButton(
                    text = stringResource(Res.string.contract_rows_reset),
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
