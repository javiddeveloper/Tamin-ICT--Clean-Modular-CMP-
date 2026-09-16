package com.tamin.taminhamrah.feature.workshops.ui.objectionableDebit

import taminx.core.core_ui.obj_form_done_body
import org.jetbrains.compose.resources.getString
import kotlinx.coroutines.flow.Flow
import com.tamin.taminhamrah.ui.components.toast.success
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopCardButton
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopCardButtonTone
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopListScaffold
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopRecordCard
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopScreenShell
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopSectionHeader
import com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.model.workshop.ObjectionKind
import com.tamin.taminhamrah.model.workshop.WorkShopDebtPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import taminx.core.core_ui.objection_pdf_file
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.BackHandler
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.objection_estimate
import taminx.core.core_ui.objection_primary_vote
import taminx.core.core_ui.objection_view
import taminx.core.core_ui.payment_sheet_agreement_row
import taminx.core.core_ui.payment_sheet_debit_number
import taminx.core.core_ui.workshop_action_objection
import taminx.core.core_ui.workshop_debt_amount
import taminx.core.core_ui.workshop_debt_customer_code
import taminx.core.core_ui.workshop_debt_from_date
import taminx.core.core_ui.workshop_debt_notify_date
import taminx.core.core_ui.workshop_debt_remaining
import taminx.core.core_ui.workshop_debt_to_date

/**
 * اعتراض به بدهی.
 *
 * Each row carries one action, and the debt itself decides which: the button is labeled from the
 * objection kind rather than from anything the screen keeps. An already-filed objection is offered
 * for viewing and so reads as a quiet outline; one still to be filed is the design's amber, which
 * is how it marks an action with a deadline on it.
 */
@Composable
fun ObjectionableDebitScreen(
    workshopId: String,
    branchCode: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    workshopName: String = "",
    viewModel: ObjectionableDebitViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(workshopId, branchCode) {
        viewModel.sendIntent(ObjectionableDebitIntent.Open(workshopId, branchCode))
    }

    HandleObjectionableDebitEvents(viewModel.events)

    val onIntent = remember(viewModel) {
        { intent: ObjectionableDebitIntent -> viewModel.sendIntent(intent) }
    }

    ObjectionableDebitContent(
        state = state,
        workshopName = workshopName,
        onIntent = onIntent,
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
fun ObjectionableDebitContent(
    state: ObjectionableDebitUiState,
    workshopName: String,
    onIntent: (ObjectionableDebitIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val workshopCode = remember(state.workshopId) {
        state.workshopId.takeIf { it.isNotBlank() }?.toPersianDigits()
    }
    val safeWorkshopName = remember(workshopName) {
        workshopName.takeIf { it.isNotBlank() }
    }

    // ثبت اعتراض is a page of this screen, not a route: only this ViewModel holds the domain row
    // the objection is filed against.
    // The already-filed objection, rendered by the app's own viewer rather than dropped into
    // Downloads unseen — it saves a copy itself.
    state.viewerPdf?.let { pdf ->
        val onDismissViewer = remember(onIntent) {
            { onIntent(ObjectionableDebitIntent.DismissViewer) }
        }
        TaminPdfViewer(
            fileName = stringResource(Res.string.objection_pdf_file, state.workshopId),
            pdf = pdf,
            downloadFailed = false,
            onRequestDownload = {},
            onDismiss = onDismissViewer,
            title = stringResource(Res.string.workshop_action_objection),
        )
    }

    state.form?.let { form ->
        val onDismissForm = remember(onIntent) {
            { onIntent(ObjectionableDebitIntent.FormDismissed) }
        }
        BackHandler(onBack = onDismissForm)
        ObjectionFormPage(
            form = form,
            workshopName = workshopName,
            workshopCode = workshopCode,
            onIntent = onIntent,
            onBack = onDismissForm,
            modifier = modifier,
        )
        return
    }

    val onLoadMore = remember(onIntent) {
        { onIntent(ObjectionableDebitIntent.LoadMore) }
    }
    val onRetry = remember(onIntent) {
        { onIntent(ObjectionableDebitIntent.Retry) }
    }

    WorkshopScreenShell(
        title = stringResource(Res.string.workshop_action_objection),
        onBack = onBack,
        workshopName = safeWorkshopName,
        workshopCode = workshopCode,
        modifier = modifier,
    ) {
        WorkshopListScaffold(
            state = state.list,
            onLoadMore = onLoadMore,
            onRetry = onRetry,
            key = { it.debitNumber },
            header = {
                WorkshopSectionHeader(
                    title = stringResource(Res.string.workshop_action_objection),
                    count = state.list.items.size,
                )
            },
        ) { debt, rowModifier ->
            ObjectionableDebtCard(
                debt = debt,
                onAction = remember(debt, onIntent) {
                    { onIntent(ObjectionableDebitIntent.RowAction(debt)) }
                },
                modifier = rowModifier,
            )
        }
    }
}

@Composable
private fun ObjectionableDebtCard(
    debt: WorkShopDebtPR,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    var isExpanded by rememberSaveable(debt.debitNumber) { mutableStateOf(false) }
    val onToggle = remember { { isExpanded = !isExpanded } }
    val kind = debt.objectionKind

    WorkshopRecordCard(
        modifier = modifier,
        isExpanded = isExpanded,
        onToggle = onToggle,
        buttons = {
            WorkshopCardButton(
                text = stringResource(kind.label),
                tone = if (kind == ObjectionKind.FILED) {
                    WorkshopCardButtonTone.OUTLINE
                } else {
                    WorkshopCardButtonTone.ALERT
                },
                onClick = onAction,
            )
        },
    ) {
        DetailRow(
            label = stringResource(Res.string.payment_sheet_debit_number),
            value = debt.debitNumberLabel,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.workshop_debt_notify_date),
            value = debt.notifyDate,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.workshop_debt_customer_code),
            value = debt.customerCode,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.workshop_debt_amount),
            value = debt.amount,
            valueColor = colors.orangeText,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )

        if (isExpanded) {
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.workshop_debt_remaining),
                value = debt.remainingAmount,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.workshop_debt_from_date),
                value = debt.fromDate,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.workshop_debt_to_date),
                value = debt.toDate,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.payment_sheet_agreement_row),
                value = debt.agreementRow,
                verticalPadding = WorkshopDimens.cellVerticalPadding,
            )
        }
    }
}

/** What the single row action is called: the objection it would file, or viewing the filed one. */
private val ObjectionKind.label
    get() = when (this) {
        ObjectionKind.ESTIMATE -> Res.string.objection_estimate
        ObjectionKind.PRIMARY_VOTE -> Res.string.objection_primary_vote
        ObjectionKind.FILED -> Res.string.objection_view
    }

@PreviewRtlTheme
@Composable
private fun ObjectionableDebitScreenPreview() {
    PreviewRtlThemeContent {
        ObjectionableDebitContent(
            state = ObjectionableDebitUiState(
                workshopId = "0968210170",
                list = PagedListState(
                    items = persistentListOf(
                        WorkShopDebtPR(
                            debitNumber = "0960961008971",
                            debitNumberLabel = "۰۹۶۰۹۶۱۰۰۸۹۷۱",
                            notifyDate = "۱۴۰۵/۰۵/۲۵",
                            customerCode = "۰۹۶۰۰۰۰۲",
                            amount = "۱۴٬۲۰۳٬۳۱۱",
                            remainingAmount = "۱۴٬۲۰۳٬۳۱۱",
                            fromDate = "۱۳۹۶/۰۷/۰۱",
                            toDate = "۱۳۹۷/۰۶/۳۱",
                            agreementRow = "۰۹۶۰۰۰۰۲",
                            objectionKind = ObjectionKind.ESTIMATE,
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
 * What the screen says back.
 *
 * Through the app's toast host rather than a snackbar of its own: a `SnackbarHostState` created
 * here and never hosted anywhere swallows every message, which is what used to happen.
 */
@Composable
private fun HandleObjectionableDebitEvents(events: Flow<ObjectionableDebitEvent>) {
    val toaster = LocalToaster.current
    LaunchedEffect(events, toaster) {
        events.collect { event ->
            when (event) {
                is ObjectionableDebitEvent.ShowServerMessage -> toaster.error(event.message)

                is ObjectionableDebitEvent.ShowMessage ->
                    toaster.error(getString(event.message))

                is ObjectionableDebitEvent.ObjectionFiled -> toaster.success(
                    getString(Res.string.obj_form_done_body, event.referenceCode),
                )
            }
        }
    }
}
