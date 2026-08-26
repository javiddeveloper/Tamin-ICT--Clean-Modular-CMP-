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
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderPR
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
import taminx.core.core_ui.member_national_id
import taminx.core.core_ui.stackholder_birth_date
import taminx.core.core_ui.stackholder_type
import taminx.core.core_ui.workshop_action_stackholders

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
            state = state.list,
            onLoadMore = { onIntent(WorkshopStackholdersIntent.LoadMore) },
            key = { it.nationalId },
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
                        )
                    }
                    WorkshopSectionHeader(
                        title = stringResource(Res.string.workshop_action_stackholders),
                        count = state.list.items.size,
                    )
                }
            },
        ) { holder -> StackHolderCard(holder) }
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
                value = holder.stackType,
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
                list = PagedListState(
                    items = persistentListOf(
                        WorkshopStackHolderPR(
                            nationalId = "۴۴۷۹۸۹۰۸۸۲",
                            fullName = "حسین توکلی کرمانی",
                            fatherName = "عزیزالله",
                            birthDate = "۱۳۵۲/۰۴/۱۱",
                            stackType = "کارفرما",
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
