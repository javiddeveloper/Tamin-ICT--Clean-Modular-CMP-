package com.tamin.taminhamrah.feature.workshops.ui.workshopStackholders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.components.PersonSearchPanel
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFilterChips
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopListScaffold
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopRecordCard
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopScreenShell
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSearchAction
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSectionHeader
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderDN
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.member_father_name
import taminx.core.core_ui.member_full_name
import taminx.core.core_ui.member_national_id
import taminx.core.core_ui.stackholder_birth_date
import taminx.core.core_ui.stackholder_type
import taminx.core.core_ui.workshop_action_stackholders
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import taminx.core.core_ui.beneficiaries_empty
import taminx.core.core_ui.workshop_empty_list

/**
 * ذینفعان — the employer, the partners and the representatives behind one workshop.
 *
 * The design's collapsed card also lists شماره بیمه, which this service does not return for a
 * stakeholder — the row carries only the person's registration record — so the card identifies
 * them by name and national id instead.
 */
@Composable
fun WorkshopStackholdersScreen(
    workshopId: String,
    branchCode: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    workshopName: String = "",
    viewModel: WorkshopStackholdersViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(workshopId, branchCode) {
        viewModel.sendIntent(WorkshopStackholdersIntent.Open(workshopId, branchCode))
    }

    WorkshopStackholdersContent(
        state = state,
        workshopName = workshopName,
        onIntent = viewModel::sendIntent,
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
fun WorkshopStackholdersContent(
    state: WorkshopStackholdersUiState,
    workshopName: String,
    onIntent: (WorkshopStackholdersIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSearchOpen = state.isSearchOpen
    val draft = state.draft
    val applied = state.applied
    val filters = remember(applied) {
        buildList {
            applied.nationalId.takeIf { it.isNotBlank() }?.let {
                add(it.toPersianDigits() to WorkshopStackholdersIntent.ReplaceSearch(applied.copy(nationalId = "")))
            }
        }
    }
    val filterChips = remember(filters) { filters.map { it.first }.toImmutableList() }

    WorkshopScreenShell(
        title = stringResource(Res.string.workshop_action_stackholders),
        onBack = onBack,
        workshopName = workshopName.takeIf { it.isNotBlank() },
        workshopCode = state.workshopId.takeIf { it.isNotBlank() }?.toPersianDigits(),
        action = {
            WorkshopSearchAction(
                onClick = {
                    onIntent(WorkshopStackholdersIntent.SearchOpenChanged(!isSearchOpen))
                },
            )
        },
        modifier = modifier,
    ) {
        WorkshopListScaffold(
            emptyIcon = Icons.Filled.Groups,
            emptyMessage = stringResource(if (applied.isNotEmpty) Res.string.workshop_empty_list else Res.string.beneficiaries_empty),
            state = state.list,
            onLoadMore = { onIntent(WorkshopStackholdersIntent.LoadMore) },
            header = {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.cardGap)) {
                    AnimatedVisibility(
                        visible = isSearchOpen,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut(),
                    ) {
                        PersonSearchPanel(
                            search = draft,
                            onSearchChange = {
                                onIntent(WorkshopStackholdersIntent.DraftChanged(it))
                            },
                            onSearch = { onIntent(WorkshopStackholdersIntent.ApplySearch) },
                            onClear = { onIntent(WorkshopStackholdersIntent.ClearSearch) },
                            // کد ملی only: a stakeholder row has no insurance number to filter by.
                            showsInsuranceNumber = false,
                        )
                    }
                    WorkshopSectionHeader(
                        title = stringResource(Res.string.workshop_action_stackholders),
                        count = state.list.total,
                    )
                    WorkshopFilterChips(
                        chips = filterChips,
                        onRemove = { index -> onIntent(filters[index].second) },
                    )
                }
            },
        ) { holder, rowModifier -> StackHolderCard(holder, modifier = rowModifier) }
    }
}

@Composable
private fun StackHolderCard(holder: WorkshopStackHolderPR, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    var isExpanded by rememberSaveable(holder.nationalId) { mutableStateOf(false) }

    WorkshopRecordCard(
        modifier = modifier,
        isExpanded = isExpanded,
        onToggle = { isExpanded = !isExpanded },
    ) {
        DetailRow(
            label = stringResource(Res.string.member_full_name),
            value = holder.fullName,
            numeric = false,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.member_national_id),
            value = holder.nationalId,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )

        if (isExpanded) {
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.member_father_name),
                value = holder.fatherName,
                numeric = false,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.stackholder_birth_date),
                value = holder.birthDate,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.stackholder_type),
                // The role's own wording; a code outside the table is printed as it came, as the
                // old app does, rather than hidden.
                value = holder.role?.let { stringResource(it.title) } ?: holder.stackType,
                valueColor = colors.blueText,
                numeric = false,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun WorkshopStackholdersScreenPreview() {
    PreviewRtlThemeContent {
        WorkshopStackholdersContent(
            state = WorkshopStackholdersUiState(
                workshopId = "0968210170",
                list = PagedListState(total = PreviewStakeHolders.size, items = PreviewStakeHolders),
            ),
            workshopName = "آموزشگاه کامپیوتر توکلی-ایمیل",
            onIntent = {},
            onBack = {},
        )
    }
}

/**
 * The design's sample people, run through the real mapper — so the role's wording and the Persian
 * digits come from the code the app runs, and a preview cannot keep looking right after they break.
 */
private val PreviewStakeHolders = persistentListOf(
    WorkshopStackHolderDN(
        nationalId = "4479890882",
        firstName = "حسین",
        lastName = "توکلی کرمانی",
        fatherName = "عزیزالله",
        stackType = "3",
    ),
    WorkshopStackHolderDN(
        nationalId = "0073160997",
        firstName = "مریم",
        lastName = "توکلی",
        fatherName = "حسین",
        stackType = "2",
    ),
).map { it.toPresentation() }.toImmutableList()
