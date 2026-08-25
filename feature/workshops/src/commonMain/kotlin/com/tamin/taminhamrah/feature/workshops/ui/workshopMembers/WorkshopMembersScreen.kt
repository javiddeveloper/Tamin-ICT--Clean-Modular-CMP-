package com.tamin.taminhamrah.feature.workshops.ui.workshopMembers

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
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopListScaffold
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopRecordCard
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopScreenShell
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSearchAction
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSectionHeader
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
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
            state = state.list,
            onLoadMore = { onIntent(WorkshopMembersIntent.LoadMore) },
            key = { it.insuranceNumber + it.nationalId },
            header = {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.cardGap)) {
                    if (isSearchOpen) {
                        PersonSearchPanel(
                            search = draft,
                            onSearchChange = { onIntent(WorkshopMembersIntent.DraftChanged(it)) },
                            onSearch = { onIntent(WorkshopMembersIntent.ApplySearch) },
                            onClear = { onIntent(WorkshopMembersIntent.ClearSearch) },
                        )
                    }
                    WorkshopSectionHeader(
                        title = stringResource(Res.string.workshop_action_members),
                        count = state.list.items.size,
                    )
                }
            },
        ) { member -> WorkshopMemberCard(member) }
    }
}

@Composable
private fun WorkshopMemberCard(member: WorkshopMemberPR, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    var isExpanded by rememberSaveable(member.nationalId) { mutableStateOf(false) }
    // «اشتغال» is the one status the design draws in green; everything else is a person who has
    // left, which it draws in red.
    val statusColor = remember(member.leavingWorkStatus, colors) {
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
            state = WorkshopMembersUiState(
                workshopId = "0968210170",
                list = PagedListState(
                    items = persistentListOf(
                        WorkshopMemberPR(
                            insuranceNumber = "۰۰۱۰۵۱۷۴۷۵",
                            fullName = "حسین توکلی کرمانی",
                            nationalId = "۴۴۷۹۸۹۰۸۸۲",
                            idCardNumber = "۴",
                            fatherName = "عزیزالله",
                            nationality = "ایرانی",
                            leavingWorkStatus = "اشتغال",
                        ),
                    ),
                ),
            ),
            workshopName = "آموزشگاه کامپیوتر توکلی-ایمیل",
            onIntent = {},
            onBack = {},
        )
    }
}
