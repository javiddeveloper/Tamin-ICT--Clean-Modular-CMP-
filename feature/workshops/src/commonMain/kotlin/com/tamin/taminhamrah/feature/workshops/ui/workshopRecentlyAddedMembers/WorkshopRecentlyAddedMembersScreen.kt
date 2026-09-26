package com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import taminx.core.core_ui.new_member_follow_body
import taminx.core.core_ui.abs_form_done_title
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.WorkshopConstants
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopCardButton
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopCardButtonTone
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopListScaffold
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopPickerField
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopRecordCard
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopScreenShell
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSearchAction
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSearchCard
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSectionHeader
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopTextField
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.feature.workshops.ui.sheets.NewMemberStatusSheet
import com.tamin.taminhamrah.feature.workshops.ui.sheets.labelRes
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.model.workshop.NewMemberRequestStatus
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.BackHandler
import taminx.core.core_ui.abs_form_download
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
import taminx.core.core_ui.abs_form_declaration_file
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_cancel
import taminx.core.core_ui.action_confirm
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
import taminx.core.core_ui.new_member_request_status
import taminx.core.core_ui.new_member_requests_empty
import taminx.core.core_ui.new_member_status
import taminx.core.core_ui.workshop_action_new_member
import taminx.core.core_ui.workshop_empty_list
import taminx.core.core_ui.workshop_ten_digits
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.ic_tamin_workshop_new_member

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

    val onIntent = remember(viewModel) {
        { intent: WorkshopRecentlyAddedMembersIntent -> viewModel.sendIntent(intent) }
    }

    WorkshopRecentlyAddedMembersContent(
        state = state,
        workshopName = workshopName,
        onIntent = onIntent,
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
    val openingPersonalId = state.openingPersonalId
    val workshopCode = remember(state.workshopId) {
        state.workshopId.takeIf { it.isNotBlank() }?.toPersianDigits()
    }

    val onRequestDownload = remember(onIntent) {
        { onIntent(WorkshopRecentlyAddedMembersIntent.FormDownloadDeclaration) }
    }
    val onDismissDeclaration = remember(onIntent) {
        { onIntent(WorkshopRecentlyAddedMembersIntent.DeclarationViewerDismissed) }
    }
    val onDismissForm = remember(onIntent) {
        { onIntent(WorkshopRecentlyAddedMembersIntent.FormDismissed) }
    }
    val onToggleSearch = remember(onIntent, isSearchOpen) {
        { onIntent(WorkshopRecentlyAddedMembersIntent.SearchOpenChanged(!isSearchOpen)) }
    }
    val onLoadMore = remember(onIntent) {
        { onIntent(WorkshopRecentlyAddedMembersIntent.LoadMore) }
    }
    val onApplySearch = remember(onIntent) {
        { onIntent(WorkshopRecentlyAddedMembersIntent.ApplySearch) }
    }
    val onClearSearch = remember(onIntent) {
        { onIntent(WorkshopRecentlyAddedMembersIntent.ClearSearch) }
    }
    val onAddMember = remember(onIntent) {
        { onIntent(WorkshopRecentlyAddedMembersIntent.Edit(NewRegistration)) }
    }
    var isStatusSheetOpen by remember { mutableStateOf(false) }
    val onOpenStatusSheet = remember { { isStatusSheetOpen = true } }
    val onDismissStatusSheet = remember { { isStatusSheetOpen = false } }
    val onSelectStatus = remember(onIntent, draft) {
        { status: NewMemberRequestStatus? ->
            onIntent(WorkshopRecentlyAddedMembersIntent.DraftChanged(draft.copy(status = status)))
            isStatusSheetOpen = false
        }
    }

    // The registration is a page of this screen, not a route: it is created against the workshop
    // this list is already showing.
    // The blank declaration goes through the app's own viewer, which renders it and keeps a copy
    // in Downloads — the same chain every other document in the app uses.
    state.declarationPdf?.let { pdf ->
        TaminPdfViewer(
            fileName = stringResource(Res.string.abs_form_declaration_file),
            pdf = pdf,
            downloadFailed = false,
            onRequestDownload = onRequestDownload,
            onDismiss = onDismissDeclaration,
            title = stringResource(Res.string.abs_form_download),
        )
    }

    state.pendingAction?.let { pending ->
        MemberActionDialog(action = pending.action, onIntent = onIntent)
    }

    state.form?.let { form ->
        BackHandler(onBack = onDismissForm)
        RegistrationFormPage(
            form = form,
            workshopName = workshopName,
            workshopCode = workshopCode,
            onIntent = onIntent,
            onBack = onDismissForm,
            modifier = modifier,
        )
        return
    }

    WorkshopScreenShell(
        title = stringResource(Res.string.workshop_action_new_member),
        onBack = onBack,
        workshopName = workshopName.takeIf { it.isNotBlank() },
        workshopCode = workshopCode,
        action = {
            WorkshopSearchAction(onClick = onToggleSearch)
        },
        modifier = modifier,
    ) {
        WorkshopListScaffold(
            emptyIcon = vectorResource(Res.drawable.ic_tamin_workshop_new_member),
            emptyMessage = stringResource(if (state.applied.isNotEmpty) Res.string.workshop_empty_list else Res.string.new_member_requests_empty),
            state = state.list,
            onLoadMore = onLoadMore,
            key = { it.personalId ?: it.nationalId },
            header = {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.cardGap)) {
                    AnimatedVisibility(
                        visible = isSearchOpen,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut(),
                    ) {
                        WorkshopSearchCard(
                            onSearch = onApplySearch,
                            onClear = onClearSearch,
                        ) {
                            // The old search sheet's order: the state first, then the code.
                            WorkshopPickerField(
                                label = stringResource(Res.string.new_member_request_status),
                                value = draft.status?.let { stringResource(it.labelRes) },
                                onClick = onOpenStatusSheet,
                            )
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
                        onClick = onAddMember,
                        icon = Icons.Default.Add,
                        iconAtStart = true,
                        background = colors.buttonGradient,
                        height = WorkshopDimens.addButtonHeight,
                        shape = AddButtonShape,
                    )

                    WorkshopSectionHeader(
                        title = stringResource(Res.string.workshop_action_new_member),
                        count = state.list.items.size,
                    )
                }
            },
        ) { member, rowModifier ->
            NewMemberCard(
                member = member,
                isOpening = member.personalId != null && member.personalId == openingPersonalId,
                onIntent = onIntent,
                modifier = rowModifier,
            )
        }
    }

    if (isStatusSheetOpen) {
        NewMemberStatusSheet(
            selected = draft.status,
            onDismiss = onDismissStatusSheet,
            onSelect = onSelectStatus,
        )
    }
}

@Composable
private fun NewMemberCard(
    member: WorkshopNewMemberPR,
    /** Its documents are being read before «ویرایش» opens the form. */
    isOpening: Boolean,
    onIntent: (WorkshopRecentlyAddedMembersIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    var isExpanded by rememberSaveable(member.nationalId) { mutableStateOf(false) }
    val isDraft = member.isDraft

    val onConfirm = remember(member, onIntent) {
        { onIntent(WorkshopRecentlyAddedMembersIntent.Confirm(member)) }
    }
    val onEdit = remember(member, onIntent) {
        { onIntent(WorkshopRecentlyAddedMembersIntent.Edit(member)) }
    }
    val onDelete = remember(member, onIntent) {
        { onIntent(WorkshopRecentlyAddedMembersIntent.Delete(member)) }
    }
    val onFollow = remember(member, onIntent) {
        { onIntent(WorkshopRecentlyAddedMembersIntent.Follow(member)) }
    }

    WorkshopRecordCard(
        modifier = modifier,
        isExpanded = isExpanded,
        onToggle = { isExpanded = !isExpanded },
        buttons = {
            if (isDraft) {
                WorkshopCardButton(
                    text = stringResource(Res.string.new_member_confirm),
                    tone = WorkshopCardButtonTone.SUCCESS,
                    onClick = onConfirm,
                )
                WorkshopCardButton(
                    text = stringResource(Res.string.new_member_edit),
                    tone = WorkshopCardButtonTone.OUTLINE,
                    onClick = onEdit,
                    isLoading = isOpening,
                )
                WorkshopCardButton(
                    text = stringResource(Res.string.new_member_delete),
                    tone = WorkshopCardButtonTone.DANGER,
                    onClick = onDelete,
                )
            } else {
                WorkshopCardButton(
                    text = stringResource(Res.string.new_member_follow),
                    tone = WorkshopCardButtonTone.OUTLINE,
                    onClick = onFollow,
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

/**
 * «آیا مطمئن هستید؟» before a row is confirmed or deleted, as the old app asked for both — in its
 * warning amber, since neither can be taken back from this list.
 */
@Composable
private fun MemberActionDialog(
    action: MemberAction,
    onIntent: (WorkshopRecentlyAddedMembersIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    val onAccept = remember(onIntent) {
        { onIntent(WorkshopRecentlyAddedMembersIntent.PendingActionAccepted) }
    }
    val onDismiss = remember(onIntent) {
        { onIntent(WorkshopRecentlyAddedMembersIntent.PendingActionDismissed) }
    }
    TaminConfirmationDialog(
        title = stringResource(action.title),
        description = stringResource(action.question),
        confirmButton = {
            TaminFilledButton(
                background = LocalTaminColors.current.buttonGradient,
                text = stringResource(Res.string.action_confirm),
                onClick = onAccept,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        dismissButton = {
            TaminOutlinedButton(
                text = stringResource(Res.string.action_cancel),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        onDismissRequest = onDismiss,
        icon = Icons.Outlined.Info,
        iconTint = colors.orangeText,
        iconBackground = colors.orangeBg,
    )
}

/** An empty registration — what «افزودن پرسنل جدید» opens the form on. */
private val NewRegistration = WorkshopNewMemberPR()
private val AddButtonShape = RoundedCornerShape(WorkshopDimens.addButtonCorner)


@PreviewRtlTheme
@Composable
private fun WorkshopRecentlyAddedMembersScreenPreview() {
    PreviewRtlThemeContent {
        WorkshopRecentlyAddedMembersContent(
            state = PreviewState,
            workshopName = PREVIEW_WORKSHOP_NAME,
            onIntent = {},
            onBack = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun WorkshopRecentlyAddedMembersSearchPreview() {
    PreviewRtlThemeContent {
        WorkshopRecentlyAddedMembersContent(
            state = PreviewState.copy(
                isSearchOpen = true,
                draft = NewMemberSearch(status = NewMemberRequestStatus.UNDER_REVIEW),
            ),
            workshopName = PREVIEW_WORKSHOP_NAME,
            onIntent = {},
            onBack = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun WorkshopRecentlyAddedMembersDeleteQuestionPreview() {
    PreviewRtlThemeContent {
        WorkshopRecentlyAddedMembersContent(
            state = PreviewState.copy(
                pendingAction = PendingMemberAction(PreviewDraft, MemberAction.DELETE),
            ),
            workshopName = PREVIEW_WORKSHOP_NAME,
            onIntent = {},
            onBack = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun WorkshopRecentlyAddedMembersOpeningPreview() {
    PreviewRtlThemeContent {
        WorkshopRecentlyAddedMembersContent(
            state = PreviewState.copy(openingPersonalId = PreviewDraft.personalId),
            workshopName = PREVIEW_WORKSHOP_NAME,
            onIntent = {},
            onBack = {},
        )
    }
}

private const val PREVIEW_WORKSHOP_NAME = "آموزشگاه کامپیوتر توکلی-ایمیل"

private val PreviewDraft = WorkshopNewMemberPR(
    personalId = 1L,
    fullName = "احمد احمدی",
    nationalId = "۲۷۴۱۸۸۰۲۹۸",
    birthDate = "۱۳۷۸/۰۵/۲۶",
    registerDate = "۱۴۰۵/۰۵/۲۰",
    statusLabel = "پیش‌نویس (ثبت نشده)",
    isDraft = true,
)

private val PreviewState = WorkshopRecentlyAddedMembersUiState(
    workshopId = "0968210170",
    list = PagedListState(
        items = persistentListOf(
            PreviewDraft,
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
)

/**
 * What the screen says back, through the app's toast host.
 *
 * «پیگیری درخواست» answers here rather than navigating: the tracking code is the whole answer,
 * and the کارتابل it would otherwise open is a different feature's list.
 */
@Composable
private fun HandleRecentlyAddedMembersEvents(events: Flow<WorkshopRecentlyAddedMembersEvent>) {
    val toaster = LocalToaster.current
    LaunchedEffect(events, toaster) {
        events.collect { event ->
            when (event) {
                is WorkshopRecentlyAddedMembersEvent.ShowServerMessage -> toaster.error(event.message)

                is WorkshopRecentlyAddedMembersEvent.ShowMessage ->
                    toaster.error(getString(event.message))

                is WorkshopRecentlyAddedMembersEvent.Confirmed -> toaster.success(
                    getString(Res.string.new_member_confirmed, event.referenceCode),
                )

                is WorkshopRecentlyAddedMembersEvent.OpenCartable -> toaster.info(
                    getString(Res.string.new_member_follow_body, event.referenceCode),
                )

                WorkshopRecentlyAddedMembersEvent.RegistrationFiled ->
                    toaster.success(getString(Res.string.abs_form_done_title))
            }
        }
    }
}
