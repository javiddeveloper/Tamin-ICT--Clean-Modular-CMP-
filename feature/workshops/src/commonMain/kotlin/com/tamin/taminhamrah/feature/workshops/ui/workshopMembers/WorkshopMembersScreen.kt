package com.tamin.taminhamrah.feature.workshops.ui.workshopMembers

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
import com.tamin.taminhamrah.feature.workshops.ui.model.PersonSearch
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.model.workshop.WorkshopMemberPR
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
import taminx.core.core_ui.member_id_card_number
import taminx.core.core_ui.member_insurance_number
import taminx.core.core_ui.member_leaving_date
import taminx.core.core_ui.member_leaving_status
import taminx.core.core_ui.member_national_id
import taminx.core.core_ui.member_nationality
import taminx.core.core_ui.workshop_action_members
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.ic_tamin_workshop_members
import taminx.core.core_ui.workshop_empty_list
import taminx.core.core_ui.workshop_members_empty

/**
 * کارکنان — the insured people registered against one workshop.
 *
 * Three cells identify a person; the rest of their record is behind «جزئیات بیشتر», which is what
 * keeps a long staff list scannable.
 */
@Composable
fun WorkshopMembersScreen(
    workshopId: String,
    branchCode: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    workshopName: String = "",
    viewModel: WorkshopMembersViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(workshopId, branchCode) {
        viewModel.sendIntent(WorkshopMembersIntent.Open(workshopId, branchCode))
    }

    WorkshopMembersContent(
        state = state,
        workshopName = workshopName,
        onIntent = viewModel::sendIntent,
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
fun WorkshopMembersContent(
    state: WorkshopMembersUiState,
    workshopName: String,
    onIntent: (WorkshopMembersIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSearchOpen = state.isSearchOpen
    val draft = state.draft
    // What the list is narrowed by, each removable on its own — the design lists the values.
    val applied = state.applied
    val filters = remember(applied) {
        buildList {
            applied.nationalId.takeIf { it.isNotBlank() }?.let {
                add(it.toPersianDigits() to WorkshopMembersIntent.ReplaceSearch(applied.copy(nationalId = "")))
            }
            applied.insuranceNumber.takeIf { it.isNotBlank() }?.let {
                add(it.toPersianDigits() to WorkshopMembersIntent.ReplaceSearch(applied.copy(insuranceNumber = "")))
            }
        }
    }
    val filterChips = remember(filters) { filters.map { it.first }.toImmutableList() }

    WorkshopScreenShell(
        title = stringResource(Res.string.workshop_action_members),
        onBack = onBack,
        workshopName = workshopName.takeIf { it.isNotBlank() },
        workshopCode = state.workshopId.takeIf { it.isNotBlank() }?.toPersianDigits(),
        action = {
            WorkshopSearchAction(
                onClick = {
                    onIntent(WorkshopMembersIntent.SearchOpenChanged(!isSearchOpen))
                },
            )
        },
        modifier = modifier,
    ) {
        WorkshopListScaffold(
            emptyIcon = vectorResource(Res.drawable.ic_tamin_workshop_members),
            emptyMessage = stringResource(if (applied.isNotEmpty) Res.string.workshop_empty_list else Res.string.workshop_members_empty),
            state = state.list,
            onLoadMore = { onIntent(WorkshopMembersIntent.LoadMore) },
            // No key: a person who left and was taken on again is two rows with the same
            // insurance number and national id, and a repeated key crashes the list.
            header = {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.cardGap)) {
                    AnimatedVisibility(
                        visible = isSearchOpen,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut(),
                    ) {
                        PersonSearchPanel(
                            search = draft,
                            onSearchChange = { onIntent(WorkshopMembersIntent.DraftChanged(it)) },
                            onSearch = { onIntent(WorkshopMembersIntent.ApplySearch) },
                            onClear = { onIntent(WorkshopMembersIntent.ClearSearch) },
                        )
                    }
                    WorkshopSectionHeader(
                        title = stringResource(Res.string.workshop_action_members),
                        // The service's own total, not how much of it has been paged in: a count
                        // that climbs while the user scrolls reads as though the first one was wrong.
                        count = state.list.total,
                    )
                    WorkshopFilterChips(
                        chips = filterChips,
                        onRemove = { index -> onIntent(filters[index].second) },
                    )
                }
            },
        ) { member, rowModifier -> WorkshopMemberCard(member, modifier = rowModifier) }
    }
}

@Composable
private fun WorkshopMemberCard(member: WorkshopMemberPR, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    var isExpanded by rememberSaveable(member.nationalId) { mutableStateOf(false) }
    // «اشتغال» is the one status the design draws in green; everything else is a person who has
    // left, which it draws in red.
    val statusColor = remember(member.isEmployed, colors) {
        if (member.isEmployed) colors.springGreenText else colors.dangerText
    }

    WorkshopRecordCard(
        modifier = modifier,
        isExpanded = isExpanded,
        onToggle = { isExpanded = !isExpanded },
    ) {
        DetailRow(
            label = stringResource(Res.string.member_full_name),
            value = member.fullName,
            numeric = false,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.member_national_id),
            value = member.nationalId,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.member_insurance_number),
            value = member.insuranceNumber,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )

        if (isExpanded) {
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.member_father_name),
                value = member.fatherName,
                numeric = false,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.member_id_card_number),
                value = member.idCardNumber,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.member_nationality),
                value = member.nationality,
                numeric = false,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.member_leaving_status),
                value = member.leavingWorkStatus,
                valueColor = statusColor,
                numeric = false,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.member_leaving_date),
                value = member.leavingWorkDate,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun WorkshopMembersScreenPreview() {
    PreviewRtlThemeContent {
        WorkshopMembersContent(
            state = WorkshopMembersUiState(workshopId = "0968210170", list = PreviewMembers),
            workshopName = "آموزشگاه کامپیوتر توکلی-ایمیل",
            onIntent = {},
            onBack = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun WorkshopMembersSearchedPreview() {
    PreviewRtlThemeContent {
        WorkshopMembersContent(
            state = WorkshopMembersUiState(
                workshopId = "0968210170",
                list = PreviewMembers,
                applied = PersonSearch(nationalId = "4479890882"),
            ),
            workshopName = "آموزشگاه کامپیوتر توکلی-ایمیل",
            onIntent = {},
            onBack = {},
        )
    }
}

/** The design's three sample rows: two at work, one who has left. */
private val PreviewMembers = PagedListState(
    total = 3,
    items = persistentListOf(
        WorkshopMemberPR(
            insuranceNumber = "۰۰۱۰۵۱۷۴۷۵",
            fullName = "حسین توکلی کرمانی",
            nationalId = "۴۴۷۹۸۹۰۸۸۲",
            idCardNumber = "۴",
            fatherName = "عزیزالله",
            nationality = "ایرانی",
            leavingWorkStatus = "اشتغال",
            leavingWorkDate = "—",
            isEmployed = true,
        ),
        WorkshopMemberPR(
            insuranceNumber = "۰۰۱۳۵۴۶۳۱۹",
            fullName = "محمد دربندی",
            nationalId = "۰۰۶۱۶۶۶۵۷۲",
            idCardNumber = "۱۲",
            fatherName = "رحمت‌الله",
            nationality = "ایرانی",
            leavingWorkStatus = "اشتغال",
            leavingWorkDate = "—",
            isEmployed = true,
        ),
        WorkshopMemberPR(
            insuranceNumber = "۰۰۲۱۹۱۱۷۶۴",
            fullName = "محسن داودی",
            nationalId = "۳۷۷۰۱۲۷۹۳۵",
            idCardNumber = "۷",
            fatherName = "اکبر",
            nationality = "ایرانی",
            leavingWorkStatus = "ترک کار",
            leavingWorkDate = "۱۴۰۴/۱۲/۲۹",
            isEmployed = false,
        ),
    ),
)
