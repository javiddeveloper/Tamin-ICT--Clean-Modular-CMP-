package com.tamin.taminhamrah.feature.workshops.ui.managementDebit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopDocumentsPanel
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormCheck
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormError
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormFooter
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormNote
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormSection
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormStep
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopReviewGroup
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopReviewRow
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopScreenShell
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopStepper
import com.tamin.taminhamrah.feature.workshops.ui.model.ArticleSixteenDocumentTypes
import com.tamin.taminhamrah.model.workshop.ARTICLE_SIXTEEN_MAX_DOCUMENTS
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.article_43
import taminx.core.core_ui.article_sixteen_action_fix_request
import taminx.core.core_ui.ws_dialog_ok
import taminx.core.core_ui.article_sixteen_executive_notify_date
import taminx.core.core_ui.article_sixteen_form_account_code
import taminx.core.core_ui.article_sixteen_form_address
import taminx.core.core_ui.article_sixteen_form_check
import taminx.core.core_ui.article_sixteen_form_docs_desc
import taminx.core.core_ui.article_sixteen_form_docs_title
import taminx.core.core_ui.article_sixteen_form_employer_name
import taminx.core.core_ui.article_sixteen_form_group_debt
import taminx.core.core_ui.article_sixteen_form_group_workshop
import taminx.core.core_ui.article_sixteen_form_note_result
import taminx.core.core_ui.article_sixteen_form_note_window
import taminx.core.core_ui.article_sixteen_form_review_desc
import taminx.core.core_ui.article_sixteen_form_review_title
import taminx.core.core_ui.article_sixteen_form_step_docs
import taminx.core.core_ui.article_sixteen_form_step_review
import taminx.core.core_ui.article_sixteen_form_submit
import taminx.core.core_ui.article_sixteen_form_title
import taminx.core.core_ui.article_sixteen_form_workshop_name
import taminx.core.core_ui.article_sixteen_proceeding_type
import taminx.core.core_ui.article_sixteen_resubmit_warning
import taminx.core.core_ui.obj_form_period_from
import taminx.core.core_ui.obj_form_period_to
import taminx.core.core_ui.payment_sheet_agreement_row
import taminx.core.core_ui.workshop_branch_code
import taminx.core.core_ui.workshop_code
import taminx.core.core_ui.workshop_debt_amount
import taminx.core.core_ui.ws_form_next

/**
 * درخواست رسیدگی به بدهی ماده ۱۶ — check what is being asked about, then attach the evidence.
 *
 * Like ثبت اعتراض it is a page of its list screen rather than a route, because the submission has
 * to carry the domain debt row that only that screen's ViewModel holds.
 */
@Composable
fun ArticleSixteenFormPage(
    form: ArticleSixteenFormState,
    workshopName: String,
    workshopCode: String?,
    onIntent: (ManagementDebitIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val reviewLabel = stringResource(Res.string.article_sixteen_form_step_review)
    val docsLabel = stringResource(Res.string.article_sixteen_form_step_docs)
    val steps = remember(form.step, reviewLabel) {
        persistentListOf(
            WorkshopFormStep(reviewLabel, isDone = form.step > 1, isCurrent = form.step == 1),
            WorkshopFormStep(docsLabel, isDone = false, isCurrent = form.step == 2),
        )
    }

    WorkshopScreenShell(
        title = stringResource(Res.string.article_sixteen_form_title),
        onBack = onBack,
        workshopName = workshopName.takeIf { it.isNotBlank() },
        workshopCode = workshopCode,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            WorkshopStepper(steps = steps)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.page)
                    .padding(top = Spacing.md, bottom = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                if (form.step == 1) {
                    ReviewStep(form = form, onIntent = onIntent)
                } else {
                    DocumentsStep(form = form, onIntent = onIntent)
                }
            }
        }

        WorkshopFormFooter(
            nextLabel = stringResource(
                if (form.isLastStep) Res.string.article_sixteen_form_submit else Res.string.ws_form_next,
            ),
            onNext = { onIntent(ManagementDebitIntent.FormNext) },
            onPrev = if (form.step > 1) {
                { onIntent(ManagementDebitIntent.FormPrev) }
            } else {
                null
            },
            isBusy = form.isBusy,
        )
    }

    if (form.isResubmitNoticeOpen) {
        val dismiss = { onIntent(ManagementDebitIntent.FormResubmitNoticeDismissed) }
        TaminConfirmationDialog(
            title = stringResource(Res.string.article_sixteen_action_fix_request),
            description = stringResource(Res.string.article_sixteen_resubmit_warning),
            icon = Icons.Outlined.Info,
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(Res.string.ws_dialog_ok),
                    onClick = dismiss,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            dismissButton = {},
            onDismissRequest = dismiss,
        )
    }
}

/** Step one: the debt, and the workshop it belongs to. */
@Composable
private fun ReviewStep(
    form: ArticleSixteenFormState,
    onIntent: (ManagementDebitIntent) -> Unit,
) {
    val debt = form.debt
    val info = form.workshopInfo

    val accountCodeLabel = stringResource(Res.string.article_sixteen_form_account_code)
    val agreementRowLabel = stringResource(Res.string.payment_sheet_agreement_row)
    val periodFromLabel = stringResource(Res.string.obj_form_period_from)
    val periodToLabel = stringResource(Res.string.obj_form_period_to)
    val amountLabel = stringResource(Res.string.workshop_debt_amount)
    val notifyLabel = stringResource(Res.string.article_sixteen_executive_notify_date)
    val proceedingTypeLabel = stringResource(Res.string.article_sixteen_proceeding_type)
    val proceedingTypeText = debt.proceedingType?.let { stringResource(it) }
    val debtRows = remember(debt, accountCodeLabel, proceedingTypeText) {
        val base = persistentListOf(
            WorkshopReviewRow(accountCodeLabel, debt.debitNumberLabel),
            WorkshopReviewRow(agreementRowLabel, debt.agreementRow),
            WorkshopReviewRow(periodFromLabel, debt.fromDate),
            WorkshopReviewRow(periodToLabel, debt.toDate),
            WorkshopReviewRow(amountLabel, debt.amount),
            WorkshopReviewRow(notifyLabel, debt.executiveNotifyDateLabel),
        )
        if (proceedingTypeText != null) {
            base.add(WorkshopReviewRow(proceedingTypeLabel, proceedingTypeText, isNumeric = false))
        } else {
            base
        }
    }

    val employerLabel = stringResource(Res.string.article_sixteen_form_employer_name)
    val nameLabel = stringResource(Res.string.article_sixteen_form_workshop_name)
    val codeLabel = stringResource(Res.string.workshop_code)
    val branchLabel = stringResource(Res.string.workshop_branch_code)
    val addressLabel = stringResource(Res.string.article_sixteen_form_address)
    val workshopRows = remember(info, employerLabel) {
        persistentListOf(
            WorkshopReviewRow(employerLabel, info.employerName, isNumeric = false),
            WorkshopReviewRow(nameLabel, info.workshopName, isNumeric = false),
            WorkshopReviewRow(codeLabel, info.workshopCode),
            WorkshopReviewRow(branchLabel, info.branchCode),
            WorkshopReviewRow(addressLabel, info.address, isNumeric = false),
        )
    }

    WorkshopFormSection(
        title = stringResource(Res.string.article_sixteen_form_review_title),
        description = stringResource(Res.string.article_sixteen_form_review_desc),
    )
    WorkshopReviewGroup(
        title = stringResource(Res.string.article_sixteen_form_group_debt),
        rows = debtRows,
        isOpen = form.isDebtOpen,
        onToggle = { onIntent(ManagementDebitIntent.FormDebtOpenChanged(!form.isDebtOpen)) },
    )
    WorkshopReviewGroup(
        title = stringResource(Res.string.article_sixteen_form_group_workshop),
        rows = workshopRows,
        isOpen = form.isWorkshopOpen,
        onToggle = {
            onIntent(ManagementDebitIntent.FormWorkshopOpenChanged(!form.isWorkshopOpen))
        },
    )
}

/** Step two: the evidence, the rules that govern it, and the declaration. */
@Composable
private fun DocumentsStep(
    form: ArticleSixteenFormState,
    onIntent: (ManagementDebitIntent) -> Unit,
) {
    WorkshopFormSection(
        title = stringResource(Res.string.article_sixteen_form_docs_title),
        description = stringResource(Res.string.article_sixteen_form_docs_desc),
    )
    WorkshopDocumentsPanel(
        attachments = form.attachments,
        types = ArticleSixteenDocumentTypes,
        capacity = ARTICLE_SIXTEEN_MAX_DOCUMENTS,
        onAdd = { fileName, bytes, typeCode ->
            onIntent(ManagementDebitIntent.FormAddDocument(fileName, bytes, typeCode))
        },
        onRemove = { index -> onIntent(ManagementDebitIntent.FormRemoveDocument(index)) },
        isUploading = form.isUploading,
        isError = form.isDocumentsError,
    )
    WorkshopFormNote(text = stringResource(Res.string.article_sixteen_form_note_window))
    WorkshopFormNote(text = stringResource(Res.string.article_sixteen_form_note_result))
    WorkshopFormCheck(
        label = stringResource(Res.string.article_sixteen_form_check),
        isChecked = form.isConfirmed,
        onToggle = { onIntent(ManagementDebitIntent.FormConfirmedChanged(!form.isConfirmed)) },
    )
    // The missing-document rule is drawn round the panel; this is what is left.
    form.error
        ?.takeUnless { form.isDocumentsError }
        ?.let { WorkshopFormError(text = stringResource(it)) }
}

@PreviewRtlTheme
@Composable
private fun ArticleSixteenFormReviewPreview() {
    PreviewRtlThemeContent {
        ArticleSixteenFormPage(
            form = ArticleSixteenFormState(debt = PreviewDebt, workshopInfo = PreviewInfo),
            workshopName = "آموزشگاه کامپیوتر توکلی-ایمیل",
            workshopCode = "۰۹۶۸۲۱۰۱۷۰",
            onIntent = {},
            onBack = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ArticleSixteenFormDocumentsPreview() {
    PreviewRtlThemeContent {
        ArticleSixteenFormPage(
            form = ArticleSixteenFormState(
                debt = PreviewDebt,
                workshopInfo = PreviewInfo,
                step = 2,
            ),
            workshopName = "آموزشگاه کامپیوتر توکلی-ایمیل",
            workshopCode = "۰۹۶۸۲۱۰۱۷۰",
            onIntent = {},
            onBack = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ArticleSixteenFormResubmitPreview() {
    PreviewRtlThemeContent {
        ArticleSixteenFormPage(
            form = ArticleSixteenFormState(
                debt = PreviewDebt,
                workshopInfo = PreviewInfo,
                isResubmitNoticeOpen = true,
            ),
            workshopName = "آموزشگاه کامپیوتر توکلی-ایمیل",
            workshopCode = "۰۹۶۸۲۱۰۱۷۰",
            onIntent = {},
            onBack = {},
        )
    }
}

private val PreviewDebt =com.tamin.taminhamrah.model.workshop.ArticleSixteenDebtPR(
    debitNumber = "0960961008971",
    debitNumberLabel = "۰۹۶۰۹۶۱۰۰۸۹۷۱",
    executiveNotifyDateLabel = "۱۴۰۵/۰۵/۲۵",
    amount = "۱۴٬۲۰۳٬۳۱۱",
    fromDate = "۱۳۹۶/۰۷/۰۱",
    toDate = "۱۳۹۷/۰۶/۳۱",
    agreementRow = "۰۹۶۰۰۰۰۲",
    proceedingType = Res.string.article_43,
)

private val PreviewInfo = ArticleSixteenWorkshopInfoPR(
    workshopName = "آموزشگاه کامپیوتر توکلی-ایمیل",
    workshopCode = "۰۹۶۸۲۱۰۱۷۰",
    branchCode = "۰۰۱۰",
    employerName = "حسین توکلی کرمانی",
    address = "تهران، خیابان ولیعصر، پلاک ۱۲۴۸، طبقهٔ سوم",
)
