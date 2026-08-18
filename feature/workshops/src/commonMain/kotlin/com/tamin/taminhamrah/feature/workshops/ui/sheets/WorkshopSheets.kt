package com.tamin.taminhamrah.feature.workshops.ui.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.workshops.ui.components.tint
import com.tamin.taminhamrah.feature.workshops.ui.components.colors
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAction
import com.tamin.taminhamrah.model.workshop.WorkshopActivityStatus
import com.tamin.taminhamrah.model.workshop.WorkshopPR
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.workshop_activity_type
import taminx.core.core_ui.workshop_approve_date
import taminx.core.core_ui.workshop_branch_code
import taminx.core.core_ui.workshop_branch_name
import taminx.core.core_ui.workshop_employer_type
import taminx.core.core_ui.workshop_filter_clear
import taminx.core.core_ui.workshop_filter_title
import taminx.core.core_ui.workshop_register_date
import taminx.core.core_ui.workshop_start_activity_date
import taminx.core.core_ui.workshop_status_active
import taminx.core.core_ui.workshop_status_inactive
import taminx.core.core_ui.workshop_status_semi_active
import taminx.core.core_ui.workshop_actions

/**
 * جزئیات و عملیات — the rest of the workshop's details, then the services it can be taken to.
 *
 * The design puts both behind one button, which is what fixes the old screen's worst habit: there,
 * the action list only appeared once a card had been expanded, so the list's whole purpose was
 * hidden behind a toggle most people never pressed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkshopActionsSheet(
    workshop: WorkshopPR,
    onDismiss: () -> Unit,
    onAction: (WorkshopAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val (pillBackground, pillForeground) = workshop.status.tint.colors()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.bgSurface,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page)
                .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = workshop.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f, fill = false),
                )
                StatusPill(
                    text = workshop.statusLabel,
                    containerColor = pillBackground,
                    contentColor = pillForeground,
                )
            }

            DetailRow(
                label = stringResource(Res.string.workshop_employer_type),
                value = workshop.employerType,
                numeric = false,
            )
            DetailRow(
                label = stringResource(Res.string.workshop_start_activity_date),
                value = workshop.startDate,
            )
            DetailRow(
                label = stringResource(Res.string.workshop_activity_type),
                value = workshop.activityType,
                numeric = false,
            )
            DetailRow(
                label = stringResource(Res.string.workshop_branch_code),
                value = workshop.branchOfficeCode,
            )
            DetailRow(
                label = stringResource(Res.string.workshop_branch_name),
                value = workshop.branchOfficeName,
                numeric = false,
            )
            DetailRow(
                label = stringResource(Res.string.workshop_register_date),
                value = workshop.registerDate,
            )
            DetailRow(
                label = stringResource(Res.string.workshop_approve_date),
                value = workshop.approveDate,
            )

            TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))

            Text(
                text = stringResource(Res.string.workshop_actions),
                style = MaterialTheme.typography.titleSmall,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
            )

            // Declaration order is menu order — see WorkshopAction.
            WorkshopAction.entries.forEach { action ->
                SheetRow(
                    label = stringResource(action.label),
                    onClick = { onAction(action) },
                )
            }
        }
    }
}

/** فیلتر براساس فعالیت کارگاه. The chosen option is ticked, which the old sheet never showed. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkshopFilterSheet(
    selected: WorkshopActivityStatus?,
    onDismiss: () -> Unit,
    onSelect: (WorkshopActivityStatus?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.bgSurface,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page)
                .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = stringResource(Res.string.workshop_filter_title),
                style = MaterialTheme.typography.titleSmall,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = Spacing.sm),
            )

            SheetRow(
                label = stringResource(Res.string.workshop_filter_clear),
                isSelected = selected == null,
                onClick = { onSelect(null) },
            )
            WorkshopActivityStatus.entries.forEach { status ->
                SheetRow(
                    label = stringResource(status.label),
                    isSelected = selected == status,
                    onClick = { onSelect(status) },
                )
            }
        }
    }
}

/** One tappable line of a sheet: a label, and either a tick or a chevron. */
@Composable
private fun SheetRow(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean? = null,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.md))
            .clickable(onClick = onClick)
            .background(if (isSelected == true) colors.blueBg else colors.chipBg)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected == true) colors.blueText else colors.textPrimary,
        )
        when (isSelected) {
            null -> Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint = colors.chevron,
                modifier = Modifier.height(IconSize.small),
            )

            true -> Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = colors.blueText,
                modifier = Modifier.height(IconSize.small),
            )

            false -> Unit
        }
    }
}

/** The label each activity status is listed under — the wording of the filter, not of a card. */
private val WorkshopActivityStatus.label
    get() = when (this) {
        WorkshopActivityStatus.ACTIVE -> Res.string.workshop_status_active
        WorkshopActivityStatus.SEMI_ACTIVE -> Res.string.workshop_status_semi_active
        WorkshopActivityStatus.INACTIVE -> Res.string.workshop_status_inactive
    }
