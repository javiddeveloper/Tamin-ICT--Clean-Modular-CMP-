package com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import taminx.core.core_ui.new_member_follow_body
import taminx.core.core_ui.abs_form_done_body
import taminx.core.core_ui.new_member_confirmed
import org.jetbrains.compose.resources.getString
import kotlinx.coroutines.flow.Flow
import com.tamin.taminhamrah.ui.components.toast.success
import com.tamin.taminhamrah.ui.components.toast.info
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.WorkshopConstants
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopCardButton
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopCardButtonTone
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopListScaffold
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopRecordCard
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopScreenShell
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSearchAction
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSearchCard
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSectionHeader
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopTextField
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.BackHandler
import taminx.core.core_ui.abs_form_declaration_file
import com.tamin.taminhamrah.ui.components.rememberPdfSaver
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.new_member_add
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
import taminx.core.core_ui.workshop_ten_digits

/**
 * نام‌نویسی غیرحضوری بیمه‌شده.
 *
 * A drafted registration can still be confirmed, edited or deleted; a submitted one can only be
 * followed. The row shows one set or the other, never both.
 */
@Composable
fun WorkshopRecentlyAddedMembersScreen(
    workshopId: String,
    branchCode: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    workshopName: String = "",
    viewModel: WorkshopRecentlyAddedMembersViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(workshopId, branchCode) {
        viewModel.sendIntent(WorkshopRecentlyAddedMembersIntent.Open(workshopId, branchCode))
    }

    HandleRecentlyAddedMembersEvents(viewModel.events)

    WorkshopRecentlyAddedMembersContent(
        state = state,
        workshopName = workshopName,
        onIntent = viewModel::sendIntent,
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
fun WorkshopRecentlyAddedMembersContent(
    state: WorkshopRecentlyAddedMembersUiState,
    workshopName: String,
    onIntent: (WorkshopRecentlyAddedMembersIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val isSearchOpen = state.isSearchOpen
    val draft = state.draft

    // The registration is a page of this screen, not a route: it is created against the workshop
    // this list is already showing.
    state.form?.let { form ->
        BackHandler { onIntent(WorkshopRecentlyAddedMembersIntent.FormDismissed) }
        RegistrationFormPage(
            form = form,
            workshopName = workshopName,
            workshopCode = state.workshopId.takeIf { it.isNotBlank() }?.toPersianDigits(),
            onIntent = onIntent,
            onBack = { onIntent(WorkshopRecentlyAddedMembersIntent.FormDismissed) },
            modifier = modifier,
        )
        return
    }

    WorkshopScreenShell(
        title = stringResource(Res.string.workshop_action_new_member),
        onBack = onBack,
        workshopName = workshopName.takeIf { it.isNotBlank() },
        workshopCode = state.workshopId.takeIf { it.isNotBlank() }?.toPersianDigits(),
        action = {
            WorkshopSearchAction(
                onClick = {
                    onIntent(WorkshopRecentlyAddedMembersIntent.SearchOpenChanged(!isSearchOpen))
                },
            )
        },
        modifier = modifier,
    ) {
        WorkshopListScaffold(
            state = state.list,
            onLoadMore = { onIntent(WorkshopRecentlyAddedMembersIntent.LoadMore) },
            key = { it.nationalId },
            header = {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.cardGap)) {
                    if (isSearchOpen) {
                        WorkshopSearchCard(
                            onSearch = {
                                onIntent(WorkshopRecentlyAddedMembersIntent.ApplySearch)
                            },
                            onClear = { onIntent(WorkshopRecentlyAddedMembersIntent.ClearSearch) },
                        ) {
                            WorkshopTextField(
                                label = stringResource(Res.string.new_member_national_id),
                                value = draft.nationalId,
                                onValueChange = {
                                    onIntent(
                                        WorkshopRecentlyAddedMembersIntent.DraftChanged(
                                            draft.copy(
                                                nationalId = it.digitsOnly(),
                                            ),
                                        ),
                                    )
                                },
                                placeholder = stringResource(Res.string.workshop_ten_digits),
                                maxLength = WorkshopConstants.NATIONAL_ID_LENGTH,
                            )
                        }
                    }

                    // The design puts «افزودن پرسنل جدید» above the list, not inside a row: it
                    // starts a registration rather than acting on an existing one.
                    TaminPrimaryButton(
                        text = stringResource(Res.string.new_member_add),
                        onClick = {
                            onIntent(WorkshopRecentlyAddedMembersIntent.Edit(NewRegistration))
                        },
                        icon = Icons.Default.Add,
                        iconAtStart = true,
                        background = colors.buttonGradient,
                        height = WorkshopDimens.addButtonHeight,
                        shape = RoundedCornerShape(WorkshopDimens.addButtonCorner),
                    )

                    WorkshopSectionHeader(
                        title = stringResource(Res.string.workshop_action_new_member),
                        count = state.list.items.size,
                    )
                }
            },
        ) { member -> NewMemberCard(member = member, onIntent = onIntent) }
    }
}

@Composable
private fun NewMemberCard(
    member: WorkshopNewMemberPR,
    onIntent: (WorkshopRecentlyAddedMembersIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    var isExpanded by rememberSaveable(member.nationalId) { mutableStateOf(false) }
    val isDraft = member.isDraft

    WorkshopRecordCard(
        modifier = modifier,
        isExpanded = isExpanded,
        onToggle = { isExpanded = !isExpanded },
        buttons = {
            if (isDraft) {
                WorkshopCardButton(
                    text = stringResource(Res.string.new_member_confirm),
                    tone = WorkshopCardButtonTone.SUCCESS,
                    onClick = { onIntent(WorkshopRecentlyAddedMembersIntent.Confirm(member)) },
                )
                WorkshopCardButton(
                    text = stringResource(Res.string.new_member_edit),
                    tone = WorkshopCardButtonTone.OUTLINE,
                    onClick = { onIntent(WorkshopRecentlyAddedMembersIntent.Edit(member)) },
                )
                WorkshopCardButton(
                    text = stringResource(Res.string.new_member_delete),
                    tone = WorkshopCardButtonTone.DANGER,
                    onClick = { onIntent(WorkshopRecentlyAddedMembersIntent.Delete(member)) },
                )
            } else {
                WorkshopCardButton(
                    text = stringResource(Res.string.new_member_follow),
                    tone = WorkshopCardButtonTone.OUTLINE,
                    onClick = { onIntent(WorkshopRecentlyAddedMembersIntent.Follow(member)) },
                )
            }
        },
    ) {
        DetailRow(
            label = stringResource(Res.string.new_member_full_name),
            value = member.fullName,
            numeric = false,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.new_member_national_id),
            value = member.nationalId,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.new_member_status),
            value = member.statusLabel,
            // A draft is unfinished business, which the design says in amber; a submitted one is
            // simply in progress, which it says in blue.
            valueColor = if (isDraft) colors.orangeText else colors.blueText,
            numeric = false,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )

        if (isExpanded) {
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.new_member_birth_date),
                value = member.birthDate,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.new_member_insurance_number),
                value = member.insuranceNumber,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.new_member_register_date),
                value = member.registerDate,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
        }
    }
}

/** An empty registration — what «افزودن پرسنل جدید» opens the form on. */
private val NewRegistration = WorkshopNewMemberPR()


@PreviewRtlTheme
@Composable
private fun WorkshopRecentlyAddedMembersScreenPreview() {
    PreviewRtlThemeContent {
        WorkshopRecentlyAddedMembersContent(
            state = WorkshopRecentlyAddedMembersUiState(
                workshopId = "0968210170",
                list = PagedListState(
                    items = persistentListOf(
                        WorkshopNewMemberPR(
                            fullName = "احمد احمدی",
                            nationalId = "۲۷۴۱۸۸۰۲۹۸",
                            birthDate = "۱۳۷۸/۰۵/۲۶",
                            registerDate = "۱۴۰۵/۰۵/۲۰",
                            statusLabel = "پیش‌نویس (ثبت نشده)",
                            isDraft = true,
                        ),
                        WorkshopNewMemberPR(
                            fullName = "زهرا کریمی",
                            nationalId = "۰۰۸۱۴۵۲۳۹۰",
                            birthDate = "۱۳۷۲/۰۲/۱۴",
                            insuranceNumber = "۰۰۴۵۲۱۹۸۷۳",
                            registerDate = "۱۴۰۵/۰۴/۱۱",
                            statusLabel = "ثبت درخواست",
                            isDraft = false,
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

/**
 * What the screen says back, through the app's toast host.
 *
 * «پیگیری درخواست» answers here rather than navigating: the tracking code is the whole answer,
 * and the کارتابل it would otherwise open is a different feature's list.
 */
@Composable
private fun HandleRecentlyAddedMembersEvents(events: Flow<WorkshopRecentlyAddedMembersEvent>) {
    val toaster = LocalToaster.current
    // Where a download belongs is a platform question, so the bytes come up as an event and the
    // device's own saver writes them — the ViewModel never learns what a Downloads folder is.
    val pdfSaver = rememberPdfSaver()
    val declarationFileName = stringResource(Res.string.abs_form_declaration_file)
    LaunchedEffect(events, toaster) {
        events.collect { event ->
            when (event) {
                is WorkshopRecentlyAddedMembersEvent.ShowMessage ->
                    toaster.error(getString(event.message))

                is WorkshopRecentlyAddedMembersEvent.Confirmed -> toaster.success(
                    getString(Res.string.new_member_confirmed, event.referenceCode),
                )

                is WorkshopRecentlyAddedMembersEvent.OpenCartable -> toaster.info(
                    getString(Res.string.new_member_follow_body, event.referenceCode),
                )

                is WorkshopRecentlyAddedMembersEvent.SaveDeclarationForm ->
                    pdfSaver.save(declarationFileName, event.bytes)

                is WorkshopRecentlyAddedMembersEvent.RegistrationFiled -> toaster.success(
                    getString(Res.string.abs_form_done_body, event.referenceCode),
                )
            }
        }
    }
}
