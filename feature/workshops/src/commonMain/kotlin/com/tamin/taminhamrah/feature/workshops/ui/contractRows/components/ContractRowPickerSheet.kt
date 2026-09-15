package com.tamin.taminhamrah.feature.workshops.ui.contractRows.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.tamin.taminhamrah.feature.workshops.ui.WorkshopConstants
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopTextField
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.model.workshop.WorkshopPR
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_rows_apply
import taminx.core.core_ui.contract_rows_branch_code_required_hint
import taminx.core.core_ui.contract_rows_four_digits
import taminx.core.core_ui.contract_rows_my_workshops
import taminx.core.core_ui.contract_rows_pick_workshop
import taminx.core.core_ui.contract_rows_pick_workshop_hint
import taminx.core.core_ui.contract_rows_reset
import taminx.core.core_ui.contract_rows_workshop_code_required
import taminx.core.core_ui.workshop_code
import taminx.core.core_ui.workshop_branch_code
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
    myWorkshopsTotal: Int = 0,
    canReset: Boolean,
    onWorkshopIdChange: (String) -> Unit,
    onBranchCodeChange: (String) -> Unit,
    onQuickPick: (String, String) -> Unit,
    onApply: () -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = colors.bgSurface,
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
    myWorkshopsTotal: Int = 0,
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
            .padding(horizontal = Spacing.page)
            .padding(
                top = Spacing.smd,
                bottom = WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding() + Spacing.lg,
            ),
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

        // Absent rather than empty: with no workshops to offer, a heading over nothing reads as a
        // list that failed to load.
        if (myWorkshops.isNotEmpty()) {
            Text(
                text = stringResource(Res.string.contract_rows_my_workshops),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.smd, bottom = Spacing.sm),
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(WorkshopDimens.contractRowTileGap),
            ) {
                items(
                    items = myWorkshops,
                    key = { "${it.workshopId}_${it.branchCode}" },
                ) { workshop ->
                    QuickPickRow(
                        name = workshop.name,
                        codeLabel = workshop.codeLabel,
                        isSelected = workshop.workshopId == workshopId &&
                            (branchCode.isBlank() || workshop.branchCode == branchCode),
                        onPick = { onQuickPick(workshop.workshopId, workshop.branchCode) },
                    )
                }
            }
        }

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

/** One کارگاه‌های شما row: picking it fills both fields at once, branch included. */
@Composable
private fun QuickPickRow(
    name: String,
    codeLabel: String,
    isSelected: Boolean,
    onPick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val shape = remember { RoundedCornerShape(CornerRadius.md) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable(onClick = onPick)
            .background(if (isSelected) colors.blueBg else colors.chipBg)
            .then(
                if (isSelected) {
                    Modifier.border(Thickness.border, colors.blueBorder, shape)
                } else {
                    Modifier
                },
            )
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.smPlus),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) colors.blueText else colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        NumericText(
            text = codeLabel,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = if (isSelected) colors.blueText else colors.textMuted,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractRowPickerContentPreview() = PreviewRtlThemeContent {
    ContractRowPickerContent(
        workshopId = "9058214238",
        branchCode = "6310",
        showWorkshopIdError = false,
        showBranchCodeError = false,
        isApplying = false,
        myWorkshops = persistentListOf(
            WorkshopPR(
                workshopId = "6318210573",
                branchCode = "6310",
                name = "دبستان غیر دولتی کارن",
                codeLabel = "۶۳۱۸۲۱۰۵۷۳",
            ),
            WorkshopPR(
                workshopId = "9058214238",
                branchCode = "6310",
                name = "دبستان کارن ( مجتبی غلامیان )",
                codeLabel = "۹۰۵۸۲۱۴۲۳۸",
            ),
        ),
        myWorkshopsTotal = 8,
        canReset = true,
        onWorkshopIdChange = {},
        onBranchCodeChange = {},
        onQuickPick = { _, _ -> },
        onApply = {},
        onReset = {},
    )
}

