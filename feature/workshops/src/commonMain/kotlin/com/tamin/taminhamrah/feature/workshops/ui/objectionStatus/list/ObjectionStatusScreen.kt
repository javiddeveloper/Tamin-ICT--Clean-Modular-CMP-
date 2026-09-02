package com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.list

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.components.label
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopListScaffold
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopTextField
import com.tamin.taminhamrah.feature.workshops.ui.components.colors
import com.tamin.taminhamrah.feature.workshops.ui.components.tint
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
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
import com.tamin.taminhamrah.ui.components.rideUpIntoHeader
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.HeaderDecoration
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_down
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
import taminx.core.core_ui.objection_status_search_field
import taminx.core.core_ui.objection_status_search_title
import taminx.core.core_ui.objection_status_stat_count
import taminx.core.core_ui.objection_status_subtitle
import taminx.core.core_ui.objection_status_title
import taminx.core.core_ui.objection_status_vote_type
import taminx.core.core_ui.objection_status_workshop_id
import taminx.core.core_ui.workshop_card_collapse
import taminx.core.core_ui.workshop_card_expand
import taminx.core.core_ui.workshop_code

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
    Column(modifier = modifier.fillMaxWidth().background(colors.bgPage)) {
        TaminTopAppBar(
            title = stringResource(Res.string.objection_status_title),
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = null,
                    onClick = onBack,
                )
            },
            action = {
                TaminTopAppBarButton(
                    icon = Icons.Default.Search,
                    contentDescription = stringResource(Res.string.objection_status_search),
                    onClick = { onIntent(ObjectionStatusIntent.SearchOpenChanged(true)) },
                )
            },
            bottomPadding = WorkshopDimens.headerBottomPadding,
        ) {
            ObjectionStatusHeroContent()
        }

        // The identity card rides 42dp up into the navy, the same overlap the workshop list's own
        // stats strip uses — drawing outside the bar's bounds is why this sits here, a sibling of
        // it in a Column that does not clip, rather than inside the TaminTopAppBar's own content.
        IdentityCard(
            name = state.identityName,
            nationalId = state.identityNationalId,
            totalCount = state.totalCount,
            modifier = Modifier
                .padding(horizontal = Spacing.page)
                .rideUpIntoHeader(
                    progress = { 0f },
                    expandedOverlap = WorkshopDimens.statsCardOverlap,
                    collapsedOverlap = WorkshopDimens.statsCardOverlap,
                ),
        )

        WorkshopListScaffold(
            state = state.list,
            onLoadMore = { onIntent(ObjectionStatusIntent.LoadMore) },
            emptyMessage = stringResource(Res.string.objection_status_empty_title),
            key = { it.seqNo ?: it.hashCode() },
            header = {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    if (state.applied.isNotEmpty) {
                        AppliedFiltersRow(
                            applied = state.applied,
                            onRemoveFilter = { onIntent(ObjectionStatusIntent.RemoveFilter(it)) },
                        )
                    }
                }
            },
        ) { objection ->
            ObjectionRow(
                objection = objection,
                onOpenSms = onOpenSms,
                onOpenDocument = onOpenDocument,
            )
        }
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

/** The gradient hero's own content: the decorative wash, the ring-icon badge, and the subtitle. */
@Composable
private fun ObjectionStatusHeroContent(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Box(modifier = modifier.fillMaxWidth()) {
        DecorativeBackgroundCircle(
            size = HeaderDecoration.circleSize,
            xOffset = HeaderDecoration.circleXOffset,
            yOffset = HeaderDecoration.circleYOffset,
        )
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            AnimatedRingHeaderIcon(icon = Icons.Default.Description)
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
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
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
        Text(
            text = stringResource(Res.string.objection_status_remove_filters),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = colors.textSecondary,
        )
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(IconSize.small),
        )
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
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = colors.blueText,
            maxLines = 1,
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
                text = objection.objectionType.label(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            StatusPill(
                text = objection.status.label(),
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
        ObjectionExpandToggle(
            isExpanded = isExpanded,
            onToggle = { isExpanded = !isExpanded },
            modifier = Modifier.padding(top = Spacing.sm),
        )

        if (isExpanded) {
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
                    value = objection.objectionType.label(),
                    numeric = false,
                )
            }
        }

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
        containerColor = colors.bgSurface,
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
                    modifier = Modifier.weight(0.3f),
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
