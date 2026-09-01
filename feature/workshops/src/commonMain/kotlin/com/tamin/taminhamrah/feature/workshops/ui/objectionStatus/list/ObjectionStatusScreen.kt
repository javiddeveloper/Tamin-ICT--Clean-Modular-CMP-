package com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.list

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.components.label
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopListScaffold
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopRecordCard
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
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.ui.theme.CornerRadius
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
        ) {
            ObjectionStatusGradientHeader(
                identityName = state.identityName,
                identityNationalId = state.identityNationalId,
            )
        }

        WorkshopListScaffold(
            state = state.list,
            onLoadMore = { onIntent(ObjectionStatusIntent.LoadMore) },
            emptyMessage = stringResource(Res.string.objection_status_empty_title),
            key = { it.seqNo ?: it.hashCode() },
            header = {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    ObjectionStatusStatRow(
                        totalCount = state.totalCount,
                        onOpenSearch = { onIntent(ObjectionStatusIntent.SearchOpenChanged(true)) },
                    )
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

/** The gradient hero: icon badge, subtitle, and the identity card floating into its lower edge. */
@Composable
private fun ObjectionStatusGradientHeader(
    identityName: String,
    identityNationalId: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Box(
                modifier = Modifier
                    .size(WorkshopDimens.identityIconTile)
                    .border(1.dp, Color.White.copy(alpha = 0.28f), RoundedCornerShape(CornerRadius.lg))
                    .background(Color.White.copy(alpha = 0.13f), RoundedCornerShape(CornerRadius.lg)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(IconSize.banner),
                )
            }
            Text(
                text = stringResource(Res.string.objection_status_subtitle),
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.72f),
            )
        }

        IdentityCard(name = identityName, nationalId = identityNationalId)
    }
}

/** The stat chip + tappable search field, on the plain page background below the gradient. */
@Composable
private fun ObjectionStatusStatRow(
    totalCount: Int,
    onOpenSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .taminSurface(CornerRadius.xl)
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            NumericText(
                text = totalCount.toString().toPersianDigits(),
                style = MaterialTheme.typography.titleMedium,
                color = LocalTaminColors.current.blueText,
            )
            Text(
                text = stringResource(Res.string.objection_status_stat_count),
                style = MaterialTheme.typography.labelSmall,
                color = LocalTaminColors.current.blueText,
            )
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .taminSurface(CornerRadius.xl)
                .clickable(onClick = onOpenSearch)
                .padding(horizontal = Spacing.md, vertical = Spacing.smPlus),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.objection_status_search_field),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = LocalTaminColors.current.textSecondary,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = LocalTaminColors.current.textSecondary,
                modifier = Modifier.size(IconSize.small),
            )
        }
    }
}

@Composable
private fun IdentityCard(name: String, nationalId: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.xl)
            .padding(horizontal = Spacing.sm, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
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
            value = name,
            label = stringResource(Res.string.objection_status_identity_name),
            modifier = Modifier.weight(1f),
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
            text = stringResource(Res.string.objection_status_filter_chip),
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
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

/** One objection row: collapsed identity cells, expandable detail, two action buttons. */
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

    WorkshopRecordCard(
        modifier = modifier,
        isExpanded = isExpanded,
        onToggle = { isExpanded = !isExpanded },
        buttons = {
            TaminOutlinedButton(
                text = if (objection.seqNo == null) {
                    stringResource(Res.string.objection_status_action_no_document)
                } else if (objection.objectionType == WorkShopObjectionType.ARTICLE_SIXTEEN) {
                    stringResource(Res.string.objection_status_action_document_article16)
                } else {
                    stringResource(Res.string.objection_status_action_document)
                },
                onClick = { onOpenDocument(objection) },
                enabled = objection.seqNo != null,
                icon = Icons.Default.Description,
                modifier = Modifier.weight(1f),
            )
            TaminOutlinedButton(
                text = stringResource(Res.string.objection_status_action_sms),
                onClick = { onOpenSms(objection) },
                enabled = objection.seqNo != null,
                modifier = Modifier.weight(1f),
            )
        },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.Top,
        ) {
            StatusPill(
                text = objection.status.label(),
                containerColor = pillBackground,
                contentColor = pillForeground,
                icon = Icons.Default.Circle,
                borderColor = pillForeground.copy(alpha = WorkshopDimens.statusPillBorderAlpha),
            )
            Text(
                text = objection.objectionType.label(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f),
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
        ObjectionSplitCell(
            leftLabel = stringResource(Res.string.objection_status_objection_number),
            leftValue = objection.objectionNumber,
            rightLabel = stringResource(Res.string.objection_status_objection_date),
            rightValue = objection.objectionDate,
            modifier = Modifier.padding(top = Spacing.xs),
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

@Composable
private fun ObjectionSplitCell(
    leftLabel: String,
    leftValue: String,
    rightLabel: String,
    rightValue: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(LocalTaminColors.current.bgPage, RoundedCornerShape(CornerRadius.lg))
            .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
            Text(text = leftLabel, style = MaterialTheme.typography.labelSmall, color = LocalTaminColors.current.textMuted)
            NumericText(text = leftValue, style = MaterialTheme.typography.labelLarge, color = LocalTaminColors.current.textPrimary)
        }
        Box(
            modifier = Modifier
                .padding(horizontal = Spacing.xs)
                .background(LocalTaminColors.current.divider)
                .size(width = 1.dp, height = 28.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
        ) {
            Text(text = rightLabel, style = MaterialTheme.typography.labelSmall, color = LocalTaminColors.current.textMuted)
            NumericText(text = rightValue, style = MaterialTheme.typography.labelLarge, color = LocalTaminColors.current.textPrimary)
        }
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
                modifier = Modifier.fillMaxWidth(),
            )
            val placeholder = stringResource(Res.string.objection_status_field_optional)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                WorkshopTextField(
                    label = stringResource(Res.string.objection_status_objection_number),
                    value = filters.objectionNumber,
                    onValueChange = { onFiltersChange(filters.copy(objectionNumber = it.digitsOnly())) },
                    placeholder = placeholder,
                    modifier = Modifier.weight(1f),
                )
                WorkshopTextField(
                    label = stringResource(Res.string.objection_status_workshop_id),
                    value = filters.workshopId,
                    onValueChange = { onFiltersChange(filters.copy(workshopId = it.digitsOnly())) },
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
                TaminPrimaryButton(
                    text = stringResource(Res.string.objection_status_search),
                    onClick = onSearch,
                    icon = Icons.Default.Search,
                    background = colors.buttonGradient,
                    shape = RoundedCornerShape(CornerRadius.xl),
                    modifier = Modifier.weight(1f),
                )
                TaminOutlinedButton(
                    text = stringResource(Res.string.objection_status_clear_filters),
                    onClick = onClear,
                    shape = RoundedCornerShape(CornerRadius.xl),
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
