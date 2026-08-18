package com.tamin.taminhamrah.feature.workshops.ui.workshopStackholders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopListScaffold
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopScreenShell
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderPR
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.member_father_name
import taminx.core.core_ui.member_full_name
import taminx.core.core_ui.member_national_id
import taminx.core.core_ui.stackholder_birth_date
import taminx.core.core_ui.stackholder_type
import taminx.core.core_ui.workshop_action_stackholders

/** ذینفعان of one workshop. */
@Composable
fun WorkshopStackholdersScreen(
    workshopId: String,
    branchCode: String,
    onBack: () -> Unit,
    viewModel: WorkshopStackholdersViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(workshopId, branchCode) {
        viewModel.sendIntent(WorkshopStackholdersIntent.Open(workshopId, branchCode))
    }

    WorkshopScreenShell(
        title = stringResource(Res.string.workshop_action_stackholders),
        onBack = onBack,
    ) {
        WorkshopListScaffold(
            state = state.list,
            onLoadMore = { viewModel.sendIntent(WorkshopStackholdersIntent.LoadMore) },
            key = { it.nationalId },
        ) { holder -> StackHolderCard(holder) }
    }
}

@Composable
private fun StackHolderCard(holder: WorkshopStackHolderPR, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.lg)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        DetailRow(
            label = stringResource(Res.string.member_full_name),
            value = holder.fullName,
            numeric = false,
        )
        DetailRow(stringResource(Res.string.member_national_id), holder.nationalId)
        DetailRow(
            label = stringResource(Res.string.member_father_name),
            value = holder.fatherName,
            numeric = false,
        )
        DetailRow(stringResource(Res.string.stackholder_birth_date), holder.birthDate)
        DetailRow(
            label = stringResource(Res.string.stackholder_type),
            value = holder.stackType,
            numeric = false,
        )
    }
}
