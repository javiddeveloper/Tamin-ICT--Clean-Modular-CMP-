package com.tamin.taminhamrah.feature.workshops.ui.detail

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopCardHeader
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopCodeRow
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopScreenShell
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSectionHeader
import com.tamin.taminhamrah.feature.workshops.ui.components.colors
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAction
import com.tamin.taminhamrah.model.workshop.WorkshopActivityStatus
import com.tamin.taminhamrah.model.workshop.WorkshopPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_down
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.workshop_activity_type
import taminx.core.core_ui.workshop_actions
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

            WorkshopActionList(onAction = onAction)
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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CardCorner)
            .padding(
                start = CardHorizontalPadding,
                end = CardHorizontalPadding,
                top = CardTopPadding,
                bottom = CardBottomPadding,
            ),
    ) {
        WorkshopCardHeader(workshop = workshop)
        Box(modifier = Modifier.padding(top = Spacing.sm)) {
            WorkshopCodeRow(workshop = workshop)
        }

        Column(modifier = Modifier.padding(top = CellsTopMargin)) {
            // The first two are what the list card already showed; the rest are what «مشاهدهٔ
            // همهٔ جزئیات» is for.
            DetailRow(
                label = stringResource(Res.string.workshop_employer_type),
                value = workshop.employerType,
                valueColor = colors.springGreenText,
                numeric = false,
                verticalPadding = CellVerticalPadding,
            )
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.workshop_branch_name),
                value = workshop.branchOfficeName,
                numeric = false,
                verticalPadding = CellVerticalPadding,
            )

            if (isExpanded) {
                TaminDivider()
                DetailRow(
                    label = stringResource(Res.string.workshop_start_activity_date),
                    value = workshop.startDate,
                    valueColor = colors.blueText,
                    verticalPadding = CellVerticalPadding,
                )
                TaminDivider()
                DetailRow(
                    label = stringResource(Res.string.workshop_activity_type),
                    value = workshop.activityType,
                    numeric = false,
                    verticalPadding = CellVerticalPadding,
                    valueBoxed = true,
                )
                TaminDivider()
                DetailRow(
                    label = stringResource(Res.string.workshop_branch_code),
                    value = workshop.branchOfficeCode,
                    verticalPadding = CellVerticalPadding,
                )
                TaminDivider()
                DetailRow(
                    label = stringResource(Res.string.workshop_register_date),
                    value = workshop.registerDate,
                    verticalPadding = CellVerticalPadding,
                )
                TaminDivider()
                DetailRow(
                    label = stringResource(Res.string.workshop_approve_date),
                    value = workshop.approveDate,
                    verticalPadding = CellVerticalPadding,
                )
            }
        }

        ExpandToggle(
            isExpanded = isExpanded,
            onToggle = { isExpanded = !isExpanded },
        )
    }
}

/** «مشاهدهٔ همهٔ جزئیات کارگاه» — a dashed rule, a label, and a chevron that turns over. */
@Composable
private fun ExpandToggle(
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val rotation by animateFloatAsState(if (isExpanded) HalfTurn else 0f, label = "chevron")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = ToggleTopMargin)
            .drawBehind {
                drawLine(
                    color = colors.divider,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = Thickness.border.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(ToggleDashOn.toPx(), ToggleDashOff.toPx()),
                    ),
                )
            }
            .clickable(onClick = onToggle)
            .padding(top = ToggleTopPadding),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(
                if (isExpanded) Res.string.workshop_detail_collapse
                else Res.string.workshop_detail_expand,
            ),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = colors.blueText,
        )
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_down),
            contentDescription = null,
            tint = colors.blueText,
            // Read the animated value in the layer, not in composition: a turning chevron must
            // not recompose the card it sits in.
            modifier = Modifier
                .padding(start = Spacing.tabSelector)
                .size(ToggleChevronSize)
                .graphicsLayer { rotationZ = rotation },
        )
    }
}

/** «عملیات این کارگاه» — one tappable card per service, in the enum's own order. */
@Composable
private fun WorkshopActionList(
    onAction: (WorkshopAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ActionGap),
    ) {
        Text(
            text = stringResource(Res.string.workshop_actions),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            modifier = Modifier.padding(bottom = Spacing.xxs),
        )
        // Declaration order is menu order — see WorkshopAction.
        WorkshopAction.entries.forEach { action ->
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
            .padding(horizontal = ActionHorizontalPadding, vertical = ActionVerticalPadding),
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
            modifier = Modifier.size(ActionChevronSize),
        )
    }
}

/** `border-radius:18px; padding:13px 14px 12px` on the design's card. */
private val CardCorner = 18.dp
private val CardHorizontalPadding = 14.dp
private val CardTopPadding = 13.dp
private val CardBottomPadding = 12.dp
private val CellsTopMargin = 10.dp

/** `padding:8px 0` per cell, i.e. 8 above and below the value. */
private val CellVerticalPadding = 8.dp

private val ToggleTopMargin = 10.dp
private val ToggleTopPadding = 9.dp
private val ToggleDashOn = 3.dp
private val ToggleDashOff = 3.dp
private val ToggleChevronSize = 14.dp
private const val HalfTurn = 180f

/** `gap:9px` between action cards, `padding:11px 13px` inside one, `15px` chevron. */
private val ActionGap = 9.dp
private val ActionHorizontalPadding = 13.dp
private val ActionVerticalPadding = 11.dp
private val ActionChevronSize = 15.dp

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
