package com.tamin.taminhamrah.feature.workshops.ui.workshopMembers

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
import com.tamin.taminhamrah.model.workshop.WorkshopMemberPR
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Spacing
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
import taminx.core.core_ui.member_relation_type
import taminx.core.core_ui.workshop_action_members

/** کارکنان of one workshop. */
@Composable
fun WorkshopMembersScreen(
    workshopId: String,
    branchCode: String,
    onBack: () -> Unit,
    viewModel: WorkshopMembersViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(workshopId, branchCode) {
        viewModel.sendIntent(WorkshopMembersIntent.Open(workshopId, branchCode))
    }

    WorkshopScreenShell(
        title = stringResource(Res.string.workshop_action_members),
        onBack = onBack,
    ) {
        WorkshopListScaffold(
            state = state.list,
            onLoadMore = { viewModel.sendIntent(WorkshopMembersIntent.LoadMore) },
            key = { it.insuranceNumber + it.nationalId },
        ) { member -> WorkshopMemberCard(member) }
    }
}

@Composable
private fun WorkshopMemberCard(member: WorkshopMemberPR, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.lg)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        DetailRow(
            label = stringResource(Res.string.member_full_name),
            value = member.fullName,
            numeric = false,
        )
        DetailRow(stringResource(Res.string.member_insurance_number), member.insuranceNumber)
        DetailRow(stringResource(Res.string.member_national_id), member.nationalId)
        DetailRow(stringResource(Res.string.member_id_card_number), member.idCardNumber)
        DetailRow(
            label = stringResource(Res.string.member_father_name),
            value = member.fatherName,
            numeric = false,
        )
        DetailRow(
            label = stringResource(Res.string.member_nationality),
            value = member.nationality,
            numeric = false,
        )
        DetailRow(
            label = stringResource(Res.string.member_relation_type),
            value = member.relationType,
            numeric = false,
        )
        DetailRow(
            label = stringResource(Res.string.member_leaving_status),
            value = member.leavingWorkStatus,
            numeric = false,
        )
        DetailRow(stringResource(Res.string.member_leaving_date), member.leavingWorkDate)
    }
}
