package com.tamin.taminhamrah.feature.workshops.ui.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import com.tamin.taminhamrah.feature.workshops.ui.components.label
import com.tamin.taminhamrah.model.workshop.ArticleSixteenRequestStatus
import com.tamin.taminhamrah.model.workshop.NewMemberRequestStatus
import com.tamin.taminhamrah.model.workshop.WorkshopActivityStatus
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.article_sixteen_request_status
import taminx.core.core_ui.new_member_request_status
import taminx.core.core_ui.new_member_status_awaiting_confirmation
import taminx.core.core_ui.new_member_status_closed_approved
import taminx.core.core_ui.new_member_status_closed_rejected
import taminx.core.core_ui.new_member_status_invalid
import taminx.core.core_ui.new_member_status_needs_branch_review
import taminx.core.core_ui.new_member_status_submitted
import taminx.core.core_ui.new_member_status_under_review
import taminx.core.core_ui.workshop_all_items
import taminx.core.core_ui.workshop_filter_clear
import taminx.core.core_ui.workshop_filter_title
import taminx.core.core_ui.workshop_status_active
import taminx.core.core_ui.workshop_status_inactive
import taminx.core.core_ui.workshop_status_semi_active

/** فیلتر براساس فعالیت کارگاه. The chosen option is ticked, which the old sheet never showed. */
@Composable
fun WorkshopFilterSheet(
    selected: WorkshopActivityStatus?,
    onDismiss: () -> Unit,
    onSelect: (WorkshopActivityStatus?) -> Unit,
    modifier: Modifier = Modifier,
) {
    OptionSheet(
        title = stringResource(Res.string.workshop_filter_title),
        onDismiss = onDismiss,
        modifier = modifier,
    ) {
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

/**
 * وضعیت درخواست — what نام‌نویسی غیرحضوری بیمه‌شده is searched by. Listed in the old app's order,
 * which is the enum's; null is «همهٔ موارد».
 */
@Composable
fun NewMemberStatusSheet(
    selected: NewMemberRequestStatus?,
    onDismiss: () -> Unit,
    onSelect: (NewMemberRequestStatus?) -> Unit,
    modifier: Modifier = Modifier,
) {
    OptionSheet(
        title = stringResource(Res.string.new_member_request_status),
        onDismiss = onDismiss,
        modifier = modifier,
    ) {
        SheetRow(
            label = stringResource(Res.string.workshop_all_items),
            isSelected = selected == null,
            onClick = { onSelect(null) },
        )
        NewMemberRequestStatus.entries.forEach { status ->
            SheetRow(
                label = stringResource(status.labelRes),
                isSelected = selected == status,
                onClick = { onSelect(status) },
            )
        }
    }
}

/**
 * وضعیت درخواست رسیدگی به بدهی ماده ۱۶.
 */
@Composable
fun ArticleSixteenStatusSheet(
    selected: ArticleSixteenRequestStatus?,
    onDismiss: () -> Unit,
    onSelect: (ArticleSixteenRequestStatus?) -> Unit,
    modifier: Modifier = Modifier,
) {
    OptionSheet(
        title = stringResource(Res.string.article_sixteen_request_status),
        onDismiss = onDismiss,
        modifier = modifier,
    ) {
        SheetRow(
            label = stringResource(Res.string.workshop_all_items),
            isSelected = selected == null,
            onClick = { onSelect(null) },
        )
        ArticleSixteenRequestStatus.entries.filter { it != ArticleSixteenRequestStatus.UNKNOWN }.forEach { status ->
            SheetRow(
                label = stringResource(status.label),
                isSelected = selected == status,
                onClick = { onSelect(status) },
            )
        }
    }
}

/** A titled sheet of [SheetRow]s — the frame every option sheet here shares. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OptionSheet(
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    rows: @Composable ColumnScope.() -> Unit,
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
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = Spacing.sm),
            )
            rows()
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

/** The label each registration state is listed under — in its sheet and on the field it fills. */
internal val NewMemberRequestStatus.labelRes: StringResource
    get() = when (this) {
        NewMemberRequestStatus.AWAITING_CONFIRMATION -> Res.string.new_member_status_awaiting_confirmation
        NewMemberRequestStatus.SUBMITTED -> Res.string.new_member_status_submitted
        NewMemberRequestStatus.INVALID -> Res.string.new_member_status_invalid
        NewMemberRequestStatus.NEEDS_BRANCH_REVIEW -> Res.string.new_member_status_needs_branch_review
        NewMemberRequestStatus.UNDER_REVIEW -> Res.string.new_member_status_under_review
        NewMemberRequestStatus.CLOSED_APPROVED -> Res.string.new_member_status_closed_approved
        NewMemberRequestStatus.CLOSED_REJECTED -> Res.string.new_member_status_closed_rejected
    }
