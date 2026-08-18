package com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopListScaffold
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopScreenShell
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberPR
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.new_member_birth_date
import taminx.core.core_ui.new_member_confirm
import taminx.core.core_ui.new_member_delete
import taminx.core.core_ui.new_member_edit
import taminx.core.core_ui.new_member_follow
import taminx.core.core_ui.new_member_full_name
import taminx.core.core_ui.new_member_insurance_number
import taminx.core.core_ui.new_member_national_id
import taminx.core.core_ui.new_member_register_date
import taminx.core.core_ui.new_member_status
import taminx.core.core_ui.workshop_action_new_member

/**
 * نام نویسی غیر حضوری بیمه شده.
 *
 * A drafted registration can still be confirmed, edited or deleted; a submitted one can only be
 * followed. The row shows one set or the other, never both.
 */
@Composable
fun WorkshopRecentlyAddedMembersScreen(
    workshopId: String,
    branchCode: String,
    onBack: () -> Unit,
    viewModel: WorkshopRecentlyAddedMembersViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(workshopId, branchCode) {
        viewModel.sendIntent(WorkshopRecentlyAddedMembersIntent.Open(workshopId, branchCode))
    }

    WorkshopScreenShell(
        title = stringResource(Res.string.workshop_action_new_member),
        onBack = onBack,
    ) {
        WorkshopListScaffold(
            state = state.list,
            onLoadMore = { viewModel.sendIntent(WorkshopRecentlyAddedMembersIntent.LoadMore) },
            key = { it.nationalId },
        ) { member ->
            NewMemberCard(
                member = member,
                onIntent = viewModel::sendIntent,
            )
        }
    }
}

@Composable
private fun NewMemberCard(
    member: WorkshopNewMemberPR,
    onIntent: (WorkshopRecentlyAddedMembersIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.lg)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        DetailRow(
            label = stringResource(Res.string.new_member_full_name),
            value = member.fullName,
            numeric = false,
        )
        DetailRow(stringResource(Res.string.new_member_national_id), member.nationalId)
        DetailRow(stringResource(Res.string.new_member_birth_date), member.birthDate)
        DetailRow(stringResource(Res.string.new_member_insurance_number), member.insuranceNumber)
        DetailRow(stringResource(Res.string.new_member_register_date), member.registerDate)
        DetailRow(
            label = stringResource(Res.string.new_member_status),
            value = member.statusLabel,
            numeric = false,
        )

        if (member.isDraft) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                TaminPrimaryButton(
                    text = stringResource(Res.string.new_member_confirm),
                    onClick = { onIntent(WorkshopRecentlyAddedMembersIntent.Confirm(member)) },
                    modifier = Modifier.weight(1f),
                )
                TaminOutlinedButton(
                    text = stringResource(Res.string.new_member_edit),
                    onClick = { onIntent(WorkshopRecentlyAddedMembersIntent.Edit(member)) },
                    modifier = Modifier.weight(1f),
                )
            }
            TaminOutlinedButton(
                text = stringResource(Res.string.new_member_delete),
                onClick = { onIntent(WorkshopRecentlyAddedMembersIntent.Delete(member)) },
            )
        } else {
            TaminPrimaryButton(
                text = stringResource(Res.string.new_member_follow),
                onClick = { onIntent(WorkshopRecentlyAddedMembersIntent.Follow(member)) },
            )
        }
    }
}
