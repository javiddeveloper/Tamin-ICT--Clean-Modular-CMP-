package com.tamin.taminhamrah.feature.workshops.ui.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.Row
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
import com.tamin.taminhamrah.model.workshop.WorkshopActivityStatus
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.workshop_filter_clear
import taminx.core.core_ui.workshop_filter_title
import taminx.core.core_ui.workshop_status_active
import taminx.core.core_ui.workshop_status_inactive
import taminx.core.core_ui.workshop_status_semi_active

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
