package com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.components.DashedEmptyStateCard
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopListScaffold
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopTextField
import com.tamin.taminhamrah.feature.workshops.ui.components.colors
import com.tamin.taminhamrah.feature.workshops.ui.components.tint
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.components.labelRes
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionPR
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionStatus
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionType
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.IconPosition
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.HeaderDecoration
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toparea.TopAreaState
import com.tamin.taminhamrah.ui.toparea.driveTopArea
import com.tamin.taminhamrah.ui.toparea.rememberMeasuredTopAreaState
import com.tamin.taminhamrah.ui.toparea.reportTopAreaHeight
import com.tamin.taminhamrah.ui.toparea.topAreaContentPadding
import com.tamin.taminhamrah.ui.toparea.topAreaHide
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_down
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.ic_tamin_objection_document
import taminx.core.core_ui.ic_tamin_objection_sms
import taminx.core.core_ui.objection_status_action_document
import taminx.core.core_ui.objection_status_action_document_article16
import taminx.core.core_ui.objection_status_action_no_document
import taminx.core.core_ui.objection_status_action_sms
import taminx.core.core_ui.objection_status_applied_filter_debit_number
import taminx.core.core_ui.objection_status_applied_filter_objection_number
import taminx.core.core_ui.objection_status_applied_filter_workshop_id
import taminx.core.core_ui.objection_status_clear_filters
import taminx.core.core_ui.objection_status_debit_number
import taminx.core.core_ui.objection_status_description
import taminx.core.core_ui.objection_status_empty_message
import taminx.core.core_ui.objection_status_empty_title
import taminx.core.core_ui.objection_status_field_optional
import taminx.core.core_ui.objection_status_field_type
import taminx.core.core_ui.objection_status_filter_chip
import taminx.core.core_ui.objection_status_identity_name
import taminx.core.core_ui.objection_status_identity_national_id
import taminx.core.core_ui.objection_status_objection_date
import taminx.core.core_ui.objection_status_objection_number
import taminx.core.core_ui.objection_status_remove_filters
import taminx.core.core_ui.objection_status_search
import taminx.core.core_ui.objection_status_search_title
import taminx.core.core_ui.objection_status_stat_count
import taminx.core.core_ui.objection_status_subtitle
import taminx.core.core_ui.objection_status_title
import taminx.core.core_ui.objection_status_vote_type
import taminx.core.core_ui.objection_status_workshop_id
import taminx.core.core_ui.workshop_card_collapse
import taminx.core.core_ui.workshop_card_expand
import taminx.core.core_ui.workshop_code
import taminx.core.core_ui.workshop_filter_clear

@Composable
fun ObjectionStatusScreen(
    onBack: () -> Unit,
    onOpenSms: (objection: WorkShopObjectionPR) -> Unit,
    onOpenDocument: (objection: WorkShopObjectionPR) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ObjectionStatusViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ObjectionStatusContent(
        state = state,
        onBack = onBack,
        onOpenSms = onOpenSms,
        onOpenDocument = onOpenDocument,
        onIntent = viewModel::sendIntent,
        modifier = modifier,
    )
}

@Composable
fun ObjectionStatusContent(
    state: ObjectionStatusUiState,
    onBack: () -> Unit,
    onOpenSms: (objection: WorkShopObjectionPR) -> Unit,
    onOpenDocument: (objection: WorkShopObjectionPR) -> Unit,
    onIntent: (ObjectionStatusIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    // Folds the header's icon/subtitle from the list's own drag, snapping on release. The
    // identity card below it is never wrapped in a topArea behavior, so it stays fully shown and
    // pinned above the list -- only the header's own content folds and fades away. The drag
    // budget is measured from this exact header+card block, so it can't drift out of sync with a
    // copy or font change to either. Same scenario as `LegalRepresentativeWorkshopsScreen`'s hub
    // page — see docs/vault/TopArea-System.md.
    val topArea = rememberMeasuredTopAreaState { topAreaState ->
        ObjectionStatusTopArea(
            identityName = state.identityName,
            identityNationalId = state.identityNationalId,
            totalCount = state.totalCount,
            onBack = onBack,
            onSearchClick = { onIntent(ObjectionStatusIntent.SearchOpenChanged(true)) },
            topAreaState = topAreaState,
        )
    }
    val listState = rememberLazyListState()

    // Overlaid rather than a plain Column so the header keeps drawing edge-to-edge behind the
    // status bar while the list passes underneath it as it scrolls.
    Box(modifier = modifier.fillMaxSize().background(colors.bgPage)) {
        WorkshopListScaffold(
            state = state.list,
            onLoadMore = { onIntent(ObjectionStatusIntent.LoadMore) },
            listState = listState,
            modifier = Modifier.fillMaxSize().driveTopArea(topArea, listState),
            contentPadding = topAreaContentPadding(state = topArea, rest = WorkshopDimens.listContentPadding),
            emptyContent = { ObjectionEmptyState() },
            key = { it.seqNo ?: it.hashCode() },
            header = {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    if (state.applied.isNotEmpty) {
                        AppliedFiltersRow(
                            applied = state.applied,
                            onRemoveFilter = { onIntent(ObjectionStatusIntent.RemoveFilter(it)) },
                            onClearAll = { onIntent(ObjectionStatusIntent.ClearFilters) },
                        )
                    }
                }
            },
        ) { objection, itemModifier ->
            ObjectionRow(
                objection = objection,
                onOpenSms = onOpenSms,
                onOpenDocument = onOpenDocument,
                modifier = itemModifier,
            )
        }

        ObjectionStatusTopArea(
            identityName = state.identityName,
            identityNationalId = state.identityNationalId,
            totalCount = state.totalCount,
            onBack = onBack,
            onSearchClick = { onIntent(ObjectionStatusIntent.SearchOpenChanged(true)) },
            topAreaState = topArea,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .reportTopAreaHeight(topArea),
        )
    }

    if (state.isSearchOpen) {
        ObjectionSearchSheet(
            filters = state.draft,
            onFiltersChange = { onIntent(ObjectionStatusIntent.DraftChanged(it)) },
            onSearch = { onIntent(ObjectionStatusIntent.ApplyFilters) },
            onClear = { onIntent(ObjectionStatusIntent.ClearFilters) },
            onDismiss = { onIntent(ObjectionStatusIntent.SearchOpenChanged(false)) },
        )
    }
}

/**
 * The list screen's floating top area: the folding gradient hero (back button, search action,
 * icon, subtitle) plus the identity card, which stays fully visible and pinned beneath it
 * regardless of scroll — riding up by [WorkshopDimens.statsCardOverlap] to straddle the header's
 * seam, same as before the header could fold.
 */
@Composable
private fun ObjectionStatusTopArea(
    identityName: String,
    identityNationalId: String,
    totalCount: Int,
    onBack: () -> Unit,
    onSearchClick: () -> Unit,
    topAreaState: TopAreaState,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        TaminTopAppBar(
            title = stringResource(Res.string.objection_status_title),
            background = Brush.horizontalGradient(LocalTaminColors.current.profileGradientStops),
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = null,
                    onClick = onBack,
                    bordered = true
                )
            },
            action = {
                TaminTopAppBarButton(
                    icon = Icons.Default.Search,
                    contentDescription = stringResource(Res.string.objection_status_search),
                    bordered = true,
                    onClick = onSearchClick,
                )
            },
            bottomPadding = WorkshopDimens.headerBottomPadding,
        ) {
            ObjectionStatusHeroContent(topAreaState = topAreaState)
        }
        IdentityCard(
            name = identityName,
            nationalId = identityNationalId,
            totalCount = totalCount,
            // Rides up into the header's reserved bottom space, rather than sitting right after
            // it, so the card visually straddles the header's seam. Reports a height reduced by
            // the same overlap so reportTopAreaHeight sees the true visual footprint of this
            // whole block, not the overlap counted twice as reserved list space.
            modifier = Modifier
                .straddlePreviousSibling(WorkshopDimens.statsCardOverlap)
                .padding(horizontal = Spacing.page),
        )
    }
}

/**
 * Shifts this child up by [overlap] to overlap the previous sibling's bottom edge, while
 * reporting a height reduced by that same amount — so a parent measuring total column height
 * (here, [reportTopAreaHeight]) sees the true visual footprint instead of double-counting the
 * overlap as reserved space. Same file-local idiom `LegalRepresentativeWorkshopsScreen` uses for
 * its own identity card.
 */
private fun Modifier.straddlePreviousSibling(overlap: Dp): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val overlapPx = overlap.roundToPx()
    val reportedHeight = (placeable.height - overlapPx).coerceAtLeast(0)
    layout(placeable.width, reportedHeight) {
        placeable.placeRelative(0, -overlapPx)
    }
}

/** The gradient hero's own content: the decorative wash, the ring-icon badge, and the subtitle. */
@Composable
private fun ObjectionStatusHeroContent(topAreaState: TopAreaState, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Box(modifier = modifier.fillMaxWidth()) {
        DecorativeBackgroundCircle(
            size = HeaderDecoration.circleSize,
            xOffset = HeaderDecoration.circleXOffset,
            yOffset = HeaderDecoration.circleYOffset,
        )
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm).topAreaHide(topAreaState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            // Statically rendered while this composable is one of rememberMeasuredTopAreaState's
            // off-screen measure probes — an infinite-repeat animation there would otherwise keep
            // requesting frames for a slot that's never actually drawn. See TopAreaState.isMeasureProbe.
            AnimatedRingHeaderIcon(icon = Icons.Default.Description, animated = !topAreaState.isMeasureProbe)
            Text(
                text = stringResource(Res.string.objection_status_subtitle),
                style = MaterialTheme.typography.labelMedium,
                color = colors.textHeaderSubtitle,
            )
        }
    }
}

@Composable
private fun IdentityCard(name: String, nationalId: String,totalCount: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.xl)
            .padding(horizontal = Spacing.sm, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IdentityCell(
            value = name,
            label = stringResource(Res.string.objection_status_identity_name),
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .padding(horizontal = Spacing.xs)
                .background(LocalTaminColors.current.divider)
                .size(width = 1.dp, height = 32.dp),
        )
        IdentityCell(
            value = nationalId,
            label = stringResource(Res.string.objection_status_identity_national_id),
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .padding(horizontal = Spacing.xs)
                .background(LocalTaminColors.current.divider)
                .size(width = 1.dp, height = 32.dp),
        )
        IdentityCell(
            value = totalCount.toString().toPersianDigits(),
            label = stringResource(Res.string.objection_status_stat_count),
            modifier = Modifier.weight(0.5f),
        )
    }
}

@Composable
private fun IdentityCell(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        NumericText(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = LocalTaminColors.current.blueText,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = LocalTaminColors.current.textMuted,
        )
    }
}

@Composable
private fun AppliedFiltersRow(
    applied: ObjectionStatusFilters,
    onRemoveFilter: (ObjectionStatusFilterField) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = Spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(CornerRadius.chip))
                .background(colors.bgSurface)
                .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.objection_status_filter_chip),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
            )
        }

        if (applied.workshopId.isNotBlank()) {
            FilterChip(
                text = stringResource(Res.string.objection_status_applied_filter_workshop_id, applied.workshopId),
                onRemove = { onRemoveFilter(ObjectionStatusFilterField.WORKSHOP_ID) },
                modifier = Modifier.weight(1f),
            )
        }
        if (applied.objectionNumber.isNotBlank()) {
            FilterChip(
                text = stringResource(Res.string.objection_status_applied_filter_objection_number, applied.objectionNumber),
                onRemove = { onRemoveFilter(ObjectionStatusFilterField.OBJECTION_NUMBER) },
                modifier = Modifier.weight(1f),
            )
        }
        if (applied.debitNumber.isNotBlank()) {
            FilterChip(
                text = stringResource(Res.string.objection_status_applied_filter_debit_number, applied.debitNumber),
                onRemove = { onRemoveFilter(ObjectionStatusFilterField.DEBIT_NUMBER) },
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(CornerRadius.chip))
                .clickable(onClick = onClearAll)
                .padding(horizontal = Spacing.xs, vertical = Spacing.xxs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            Text(
                text = stringResource(Res.string.objection_status_remove_filters),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textSecondary,
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(Res.string.objection_status_remove_filters),
                tint = colors.textSecondary,
                modifier = Modifier.size(IconSize.small),
            )
        }
    }
}

@Composable
private fun FilterChip(text: String, onRemove: () -> Unit, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(CornerRadius.chip))
            .background(colors.blueBg)
            .clickable(onClick = onRemove)
            .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = colors.blueText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false),
        )
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_cross),
            contentDescription = stringResource(Res.string.workshop_filter_clear),
            tint = colors.blueText,
            modifier = Modifier.size(WorkshopDimens.chipCrossSize),
        )
    }
}

/**
 * «اعتراضی با این مشخصات یافت نشد» — the empty state for a search/filter that matched nothing,
 * a dashed-border card rather than [com.tamin.taminhamrah.ui.components.TaminEmptyState]'s plain
 * icon+text, per the design (node 1788:906).
 */
@Composable
private fun ObjectionEmptyState(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    DashedEmptyStateCard(modifier = modifier) {
        Text(
            text = stringResource(Res.string.objection_status_empty_title),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(Res.string.objection_status_empty_message),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textMuted,
            textAlign = TextAlign.Center,
        )
    }
}

/**
 * One objection row: status pill + type, two identity cells, a date/number split cell, a
 * «جزئیات بیشتر» toggle and finally the two actions — in that order, matching the design.
 *
 * Drawn locally rather than through [com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopRecordCard]
 * because that shared shell always places its `buttons` row *before* the expand toggle; this
 * design puts the toggle before the buttons, and gives the toggle a filled chip instead of the
 * shared shell's dashed rule.
 */
@Composable
private fun ObjectionRow(
    objection: WorkShopObjectionPR,
    onOpenSms: (objection: WorkShopObjectionPR) -> Unit,
    onOpenDocument: (WorkShopObjectionPR) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val (pillBackground, pillForeground) = objection.status.tint.colors()
    val colors = LocalTaminColors.current
    val hasDocument = objection.seqNo != null
    val buttonShape = RoundedCornerShape(CornerRadius.lg)
    val buttonTextStyle = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(WorkshopDimens.cardCorner)
            .padding(
                start = WorkshopDimens.cardHorizontalPadding,
                end = WorkshopDimens.cardHorizontalPadding,
                top = WorkshopDimens.cardTopPadding,
                bottom = WorkshopDimens.cardBottomPadding,
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = stringResource(objection.objectionType.labelRes),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            StatusPill(
                text = stringResource(objection.status.labelRes),
                containerColor = pillBackground,
                contentColor = pillForeground,
                fontWeight = FontWeight.ExtraBold,
                borderColor = pillForeground.copy(alpha = WorkshopDimens.statusPillBorderAlpha),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            ObjectionCell(
                label = stringResource(Res.string.objection_status_debit_number),
                value = objection.debitNumber,
                modifier = Modifier.weight(1f),
            )
            ObjectionCell(
                label = stringResource(Res.string.workshop_code),
                value = objection.workshopId,
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            ObjectionCell(
                label = stringResource(Res.string.objection_status_objection_number),
                value = objection.objectionNumber,
                modifier = Modifier.weight(1f),
            )
            ObjectionCell(
                label = stringResource(Res.string.objection_status_objection_date),
                value = objection.objectionDate,
                modifier = Modifier.weight(1f),
            )
        }
        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
            ) {
                DetailRow(
                    label = stringResource(Res.string.objection_status_description),
                    value = objection.objectionDescription,
                    numeric = false,
                )
                DetailRow(
                    label = stringResource(Res.string.objection_status_vote_type),
                    value = objection.voteTypeDescription,
                    numeric = false,
                )
                DetailRow(
                    label = stringResource(Res.string.objection_status_field_type),
                    value = stringResource(objection.objectionType.labelRes),
                    numeric = false,
                )
            }
        }

        ObjectionExpandToggle(
            isExpanded = isExpanded,
            onToggle = { isExpanded = !isExpanded },
            modifier = Modifier.padding(top = Spacing.sm),
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(WorkshopDimens.cardButtonGap),
        ) {
            TaminOutlinedButton(
                text = stringResource(Res.string.objection_status_action_sms),
                onClick = { onOpenSms(objection) },
                enabled = hasDocument,
                icon = vectorResource(Res.drawable.ic_tamin_objection_sms),
                iconModifier = Modifier.size(IconSize.small),
                iconPosition = IconPosition.End,
                height = WorkshopDimens.cardButtonHeight,
                shape = buttonShape,
                containerColor = colors.bgSurface,
                contentColor = colors.textSecondary,
                borderColor = colors.border,
                disabledContainerColor = colors.bgPage,
                disabledContentColor = colors.textMuted,
                disabledBorderColor = colors.border,
                textStyle = buttonTextStyle,
                modifier = Modifier.weight(1f),
            )
            TaminOutlinedButton(
                text = when {
                    !hasDocument -> stringResource(Res.string.objection_status_action_no_document)
                    objection.objectionType == WorkShopObjectionType.ARTICLE_SIXTEEN ->
                        stringResource(Res.string.objection_status_action_document_article16)
                    else -> stringResource(Res.string.objection_status_action_document)
                },
                onClick = { onOpenDocument(objection) },
                enabled = hasDocument,
                icon = vectorResource(Res.drawable.ic_tamin_objection_document),
                iconModifier = Modifier.size(IconSize.small),
                iconPosition = IconPosition.End,
                height = WorkshopDimens.cardButtonHeight,
                shape = buttonShape,
                containerColor = colors.blueBg,
                contentColor = colors.blueText,
                borderColor = colors.blueBorder,
                disabledContainerColor = colors.bgPage,
                disabledContentColor = colors.textMuted,
                disabledBorderColor = colors.border,
                textStyle = buttonTextStyle,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/** «جزئیات بیشتر» / «بستن» on a filled chip, matching this screen's design (the shared
 * [com.tamin.taminhamrah.feature.workshops.ui.components.CardExpandToggle] draws a dashed rule instead). */
@Composable
private fun ObjectionExpandToggle(
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val rotation by animateFloatAsState(if (isExpanded) WorkshopDimens.toggleHalfTurn else 0f, label = "objectionToggleChevron")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.bgPage)
            .clickable(onClick = onToggle)
            .padding(vertical = Spacing.smPlus),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(if (isExpanded) Res.string.workshop_card_collapse else Res.string.workshop_card_expand),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = colors.textSecondary,
        )
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_down),
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier
                .padding(start = Spacing.tabSelector)
                .size(WorkshopDimens.toggleChevronSize)
                .graphicsLayer { rotationZ = rotation },
        )
    }
}

@Composable
private fun ObjectionCell(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(LocalTaminColors.current.bgPage, RoundedCornerShape(CornerRadius.lg))
            .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = LocalTaminColors.current.textMuted)
        NumericText(text = value, style = MaterialTheme.typography.labelLarge, color = LocalTaminColors.current.textPrimary)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ObjectionSearchSheet(
    filters: ObjectionStatusFilters,
    onFiltersChange: (ObjectionStatusFilters) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.bgPage,
        shape = RoundedCornerShape(topStart = CornerRadius.sheet, topEnd = CornerRadius.sheet),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page)
                .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(
                text = stringResource(Res.string.objection_status_search_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            val placeholder = stringResource(Res.string.objection_status_field_optional)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                WorkshopTextField(
                    label = stringResource(Res.string.objection_status_workshop_id),
                    value = filters.workshopId,
                    onValueChange = { onFiltersChange(filters.copy(workshopId = it.digitsOnly())) },
                    placeholder = placeholder,
                    modifier = Modifier.weight(1f),
                )
                WorkshopTextField(
                    label = stringResource(Res.string.objection_status_objection_number),
                    value = filters.objectionNumber,
                    onValueChange = { onFiltersChange(filters.copy(objectionNumber = it.digitsOnly())) },
                    placeholder = placeholder,
                    modifier = Modifier.weight(1f),
                )
            }
            WorkshopTextField(
                label = stringResource(Res.string.objection_status_debit_number),
                value = filters.debitNumber,
                onValueChange = { onFiltersChange(filters.copy(debitNumber = it.digitsOnly())) },
                placeholder = placeholder,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                TaminOutlinedButton(
                    text = stringResource(Res.string.objection_status_clear_filters),
                    onClick = onClear,
                    shape = RoundedCornerShape(CornerRadius.xl),
                    modifier = Modifier.weight(0.3f).background(colors.bgSurface),
                )
                TaminPrimaryButton(
                    text = stringResource(Res.string.objection_status_search),
                    onClick = onSearch,
                    icon = Icons.Default.Search,
                    iconAtStart = true,
                    background = colors.buttonGradient,
                    shape = RoundedCornerShape(CornerRadius.xl),
                    modifier = Modifier.weight(0.7f),
                )
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ObjectionStatusScreenPreview() {
    PreviewRtlThemeContent {
        ObjectionStatusContent(
            state = ObjectionStatusUiState(
                identityName = "حسین توکلی کرمانی",
                identityNationalId = "۴۴۷۹۸۹۰۸۸۲",
                totalCount = 4,
                list = PagedListState(
                    items = persistentListOf(
                        WorkShopObjectionPR(
                            seqNo = 1403008720,
                            workshopId = "۲۳۶۱۸۴۷",
                            debitNumber = "۱۴۰۲/۴۴۱۹۰",
                            objectionNumber = "۱۴۰۳۰۰۸۷۲",
                            objectionDate = "۱۴۰۳/۰۹/۱۲",
                            objectionDescription = "بدهی برآوردی دورهٔ فروردین تا اسفند ۱۴۰۱ با فهرست ارسالی کارگاه مطابقت ندارد.",
                            voteTypeDescription = "رای هیئت بدوی",
                            objectionType = WorkShopObjectionType.ESTIMATE,
                            status = WorkShopObjectionStatus.BOARD_REVIEW,
                        ),
                    ),
                ),
            ),
            onBack = {},
            onOpenSms = {},
            onOpenDocument = {},
            onIntent = {},
        )
    }
}
