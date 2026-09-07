package com.tamin.taminhamrah.feature.workshops.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopCardHeader
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopCodeRow
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopRecordCard
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopScreenShell
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSectionHeader
import com.tamin.taminhamrah.feature.workshops.ui.components.colors
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAction
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.model.workshop.WorkshopActivityStatus
import com.tamin.taminhamrah.model.workshop.WorkshopPR
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.workshop_actions
import taminx.core.core_ui.workshop_activity_type
import taminx.core.core_ui.workshop_approve_date
import taminx.core.core_ui.workshop_branch_code
import taminx.core.core_ui.workshop_branch_name
import taminx.core.core_ui.workshop_detail_collapse
import taminx.core.core_ui.workshop_detail_expand
import taminx.core.core_ui.workshop_detail_info
import taminx.core.core_ui.workshop_detail_title
import taminx.core.core_ui.workshop_employer_type
import taminx.core.core_ui.workshop_register_date
import taminx.core.core_ui.workshop_start_activity_date

/**
 * جزئیات کارگاه — the workshop the list picked, and the eight services it can be taken to.
 *
 * Everything drawn here already traveled with the workshop, so opening it costs no request. The
 * card starts on the two fields the list card showed and unfolds the rest, which is the design's
 * way of keeping the services above the fold rather than below seven rows of dates.
 */
@Composable
fun WorkshopDetailScreen(
    workshop: WorkshopPR,
    onBack: () -> Unit,
    onAction: (WorkshopAction) -> Unit,
    modifier: Modifier = Modifier,
    /** Already filtered by the server's feature flags — see `WorkshopsViewModel`. */
    actions: ImmutableList<WorkshopAction> = WorkshopAction.entries.toImmutableList(),
) {
    WorkshopScreenShell(
        title = stringResource(Res.string.workshop_detail_title),
        onBack = onBack,
        workshopName = workshop.name,
        workshopCode = workshop.codeLabel,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.page)
                .padding(top = Spacing.smd, bottom = Spacing.page),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            WorkshopSectionHeader(title = stringResource(Res.string.workshop_detail_info))

            WorkshopDetailCard(workshop = workshop)

            WorkshopActionList(actions = actions, onAction = onAction)
        }
    }
}

/** The one card جزئیات کارگاه is built around: the list card's head, then all of its fields. */
@Composable
private fun WorkshopDetailCard(
    workshop: WorkshopPR,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    var isExpanded by remember { mutableStateOf(false) }

    WorkshopRecordCard(
        modifier = modifier,
        isExpanded = isExpanded,
        onToggle = { isExpanded = !isExpanded },
        expandLabel = Res.string.workshop_detail_expand,
        collapseLabel = Res.string.workshop_detail_collapse,
    ) {
        WorkshopCardHeader(workshop = workshop)
        Box(modifier = Modifier.padding(top = Spacing.sm)) {
            WorkshopCodeRow(workshop = workshop)
        }

        Column(modifier = Modifier.padding(top = WorkshopDimens.cardCellsTopMargin)) {
            // The first two are what the list card already showed; the rest are what «مشاهدهٔ
            // همهٔ جزئیات» is for.
            DetailRow(
                label = stringResource(Res.string.workshop_employer_type),
                value = workshop.employerType,
                valueColor = colors.springGreenText,
                numeric = false,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.workshop_branch_name),
                value = workshop.branchOfficeName,
                numeric = false,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )

            if (isExpanded) {
                TaminDivider()
                DetailRow(
                    label = stringResource(Res.string.workshop_start_activity_date),
                    value = workshop.startDate,
                    valueColor = colors.blueText,
                    verticalPadding = WorkshopDimens.cellVerticalPadding,
                )
                TaminDivider()
                DetailRow(
                    label = stringResource(Res.string.workshop_activity_type),
                    value = workshop.activityType,
                    numeric = false,
                    verticalPadding = WorkshopDimens.cellVerticalPadding,
                    valueBoxed = true,
                )
                TaminDivider()
                DetailRow(
                    label = stringResource(Res.string.workshop_branch_code),
                    value = workshop.branchOfficeCode,
                    verticalPadding = WorkshopDimens.cellVerticalPadding,
                )
                TaminDivider()
                DetailRow(
                    label = stringResource(Res.string.workshop_register_date),
                    value = workshop.registerDate,
                    verticalPadding = WorkshopDimens.cellVerticalPadding,
                )
                TaminDivider()
                DetailRow(
                    label = stringResource(Res.string.workshop_approve_date),
                    value = workshop.approveDate,
                    verticalPadding = WorkshopDimens.cellVerticalPadding,
                )
            }
        }
    }
}

/** «عملیات این کارگاه» — one tappable card per service, in the enum's own order. */
@Composable
private fun WorkshopActionList(
    actions: ImmutableList<WorkshopAction>,
    onAction: (WorkshopAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(WorkshopDimens.serviceRowGap),
    ) {
        Text(
            text = stringResource(Res.string.workshop_actions),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            modifier = Modifier.padding(bottom = Spacing.xxs),
        )
        // Declaration order is menu order — see WorkshopAction.
        actions.forEach { action ->
            WorkshopActionRow(action = action, onClick = { onAction(action) })
        }
    }
}

@Composable
private fun WorkshopActionRow(
    action: WorkshopAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val (tileBackground, tileTint) = action.tint.colors()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.xl)
            .clickable(onClick = onClick)
            .padding(horizontal = WorkshopDimens.serviceRowHorizontalPadding, vertical = WorkshopDimens.serviceRowVerticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.cardGap),
    ) {
        Box(
            modifier = Modifier
                .size(IconSize.large)
                .clip(RoundedCornerShape(CornerRadius.listRow))
                .background(tileBackground),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = vectorResource(action.icon),
                contentDescription = null,
                tint = tileTint,
                modifier = Modifier.size(IconSize.banner),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            Text(
                text = stringResource(action.label),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            )
            Text(
                text = stringResource(action.description),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
            )
        }

        Icon(
            // Both chevrons are autoMirrored; under RTL "forward" is the "<" the design draws
            // at the leading edge of the row.
            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_forward),
            contentDescription = null,
            tint = colors.chevron,
            modifier = Modifier.size(WorkshopDimens.serviceRowChevronSize),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun WorkshopDetailScreenPreview() {
    PreviewRtlThemeContent {
        WorkshopDetailScreen(
            workshop = WorkshopPR(
                workshopId = "0968210170",
                branchCode = "0010",
                hasIdentity = true,
                name = "آموزشگاه کامپیوتر توکلی-ایمیل",
                codeLabel = "۰۹۶۸۲۱۰۱۷۰",
                status = WorkshopActivityStatus.ACTIVE,
                statusLabel = "فعال",
                employerType = "حقیقی",
                startDate = "۱۴۰۰/۰۹/۰۳",
                activityType = "غیردولتی غیرقراردادی ۲۷٪",
                branchOfficeCode = "۰۰۱۰",
                branchOfficeName = "شعبهٔ ۱۰ تهران",
                registerDate = "۱۳۸۷/۰۸/۱۶",
                approveDate = "۱۳۸۷/۰۸/۱۶",
            ),
            onBack = {},
            onAction = {},
        )
    }
}
