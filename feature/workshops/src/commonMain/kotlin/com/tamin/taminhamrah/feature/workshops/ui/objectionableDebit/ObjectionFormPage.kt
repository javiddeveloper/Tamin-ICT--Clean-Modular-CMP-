package com.tamin.taminhamrah.feature.workshops.ui.objectionableDebit

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
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormSection
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormTextArea
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopReviewGroup
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopReviewRow
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopScreenShell
import com.tamin.taminhamrah.feature.workshops.ui.model.OBJECTION_MAX_DOCUMENTS
import com.tamin.taminhamrah.feature.workshops.ui.model.ObjectionDocumentTypes
import com.tamin.taminhamrah.model.workshop.ObjectionKind
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_cancel
import taminx.core.core_ui.action_confirm
import taminx.core.core_ui.obj_form_area_hint
import taminx.core.core_ui.obj_form_area_label
import taminx.core.core_ui.obj_form_check_confirm
import taminx.core.core_ui.obj_form_check_deposit
import taminx.core.core_ui.obj_form_confirm_body
import taminx.core.core_ui.obj_form_confirm_title
import taminx.core.core_ui.obj_form_desc_estimate
import taminx.core.core_ui.obj_form_desc_primary_vote
import taminx.core.core_ui.obj_form_group_debt
import taminx.core.core_ui.obj_form_period_from
import taminx.core.core_ui.obj_form_period_to
import taminx.core.core_ui.obj_form_submit
import taminx.core.core_ui.obj_form_title
import taminx.core.core_ui.payment_sheet_agreement_row
import taminx.core.core_ui.payment_sheet_debit_number
import taminx.core.core_ui.workshop_debt_amount
import taminx.core.core_ui.workshop_debt_notify_date

/**
 * ثبت اعتراض به بدهی — one page: the debt being objected to, the grounds, the evidence.
 *
 * It sits inside اعتراض به بدهی rather than behind a route of its own, because only that screen's
 * ViewModel holds the domain row the objection is filed against; routing here would mean fetching
 * the same debt a second time to say the same thing.
 */
@Composable
fun ObjectionFormPage(
    form: ObjectionFormState,
    workshopName: String,
    workshopCode: String?,
    onIntent: (ObjectionableDebitIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val debt = form.debt
    // The six columns the design lists, in its order. Resolved first, then remembered on the
    // debt, so typing in the description does not rebuild them.
    val debtNumberLabel = stringResource(Res.string.payment_sheet_debit_number)
    val agreementRowLabel = stringResource(Res.string.payment_sheet_agreement_row)
    val periodFromLabel = stringResource(Res.string.obj_form_period_from)
    val periodToLabel = stringResource(Res.string.obj_form_period_to)
    val amountLabel = stringResource(Res.string.workshop_debt_amount)
    val notifyDateLabel = stringResource(Res.string.workshop_debt_notify_date)
    val debtRows = remember(
        debt,
        debtNumberLabel,
        agreementRowLabel,
        periodFromLabel,
        periodToLabel,
        amountLabel,
        notifyDateLabel,
    ) {
        persistentListOf(
            WorkshopReviewRow(debtNumberLabel, debt.debitNumberLabel),
            WorkshopReviewRow(agreementRowLabel, debt.agreementRow),
            WorkshopReviewRow(periodFromLabel, debt.fromDate),
            WorkshopReviewRow(periodToLabel, debt.toDate),
            WorkshopReviewRow(amountLabel, debt.amount),
            WorkshopReviewRow(notifyDateLabel, debt.notifyDate),
        )
    }

    WorkshopScreenShell(
        title = stringResource(Res.string.obj_form_title),
        onBack = onBack,
        workshopName = workshopName.takeIf { it.isNotBlank() },
        workshopCode = workshopCode,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.page)
                .padding(top = Spacing.md, bottom = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            WorkshopFormSection(
                title = stringResource(Res.string.obj_form_title),
                description = stringResource(
                    if (debt.objectionKind == ObjectionKind.PRIMARY_VOTE) {
                        Res.string.obj_form_desc_primary_vote
                    } else {
                        Res.string.obj_form_desc_estimate
                    },
                ),
            )

            WorkshopReviewGroup(
                title = stringResource(Res.string.obj_form_group_debt),
                rows = debtRows,
                isOpen = form.isDebtOpen,
                onToggle = {
                    onIntent(ObjectionableDebitIntent.FormDebtOpenChanged(!form.isDebtOpen))
                },
            )

            WorkshopDocumentsPanel(
                attachments = form.attachments,
                types = ObjectionDocumentTypes,
                capacity = OBJECTION_MAX_DOCUMENTS,
                onAdd = { fileName, bytes, typeCode ->
                    onIntent(
                        ObjectionableDebitIntent.FormAddDocument(fileName, bytes, typeCode),
                    )
                },
                onRemove = { index ->
                    onIntent(ObjectionableDebitIntent.FormRemoveDocument(index))
                },
                isUploading = form.isUploading,
                isError = form.isDocumentsError,
            )

            WorkshopFormTextArea(
                label = stringResource(Res.string.obj_form_area_label),
                value = form.description,
                onValueChange = {
                    onIntent(ObjectionableDebitIntent.FormDescriptionChanged(it))
                },
                placeholder = stringResource(Res.string.obj_form_area_hint),
            )

            WorkshopFormCheck(
                label = stringResource(Res.string.obj_form_check_deposit),
                isChecked = form.isDeposit,
                onToggle = {
                    onIntent(ObjectionableDebitIntent.FormDepositChanged(!form.isDeposit))
                },
            )
            WorkshopFormCheck(
                label = stringResource(Res.string.obj_form_check_confirm),
                isChecked = form.isConfirmed,
                onToggle = {
                    onIntent(ObjectionableDebitIntent.FormConfirmedChanged(!form.isConfirmed))
                },
            )

            // The missing-document rule is drawn round the panel; this is what is left.
            form.error
                ?.takeUnless { form.isDocumentsError }
                ?.let { WorkshopFormError(text = stringResource(it)) }
        }

        WorkshopFormFooter(
            nextLabel = stringResource(Res.string.obj_form_submit),
            onNext = { onIntent(ObjectionableDebitIntent.FormSubmit) },
            isBusy = form.isBusy,
        )
    }

    if (form.isConfirmVisible) {
        ObjectionSubmitConfirmDialog(onIntent = onIntent)
    }
}

/**
 * The last word before the objection is filed.
 *
 * The old app put the same modal between the تعهدنامه tick and the API call, and its text is
 * kept verbatim: it is not an "are you sure" but the undertaking the employer is agreeing to —
 * that the branch reviews the documents, that the right to a هیات بدوی hearing survives a
 * rejection, and that the answer comes within a week through پیگیری وضعیت اعتراض.
 */
@Composable
private fun ObjectionSubmitConfirmDialog(
    onIntent: (ObjectionableDebitIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    TaminConfirmationDialog(
        title = stringResource(Res.string.obj_form_confirm_title),
        description = stringResource(Res.string.obj_form_confirm_body),
        confirmButton = {
            TaminFilledButton(
                text = stringResource(Res.string.action_confirm),
                onClick = { onIntent(ObjectionableDebitIntent.FormConfirmAccepted) },
                modifier = Modifier.fillMaxWidth(),
            )
        },
        dismissButton = {
            TaminOutlinedButton(
                text = stringResource(Res.string.action_cancel),
                onClick = { onIntent(ObjectionableDebitIntent.FormConfirmDismissed) },
                modifier = Modifier.fillMaxWidth(),
            )
        },
        onDismissRequest = { onIntent(ObjectionableDebitIntent.FormConfirmDismissed) },
        icon = Icons.Outlined.Info,
        iconTint = colors.orangeText,
        iconBackground = colors.orangeBg,
    )
}

@PreviewRtlTheme
@Composable
private fun ObjectionFormPagePreview() {
    PreviewRtlThemeContent {
        ObjectionFormPage(
            form = ObjectionFormState(
                debt = com.tamin.taminhamrah.model.workshop.WorkShopDebtPR(
                    debitNumber = "0960961008971",
                    debitNumberLabel = "۰۹۶۰۹۶۱۰۰۸۹۷۱",
                    notifyDate = "۱۴۰۵/۰۵/۲۵",
                    amount = "۱۴٬۲۰۳٬۳۱۱",
                    fromDate = "۱۳۹۶/۰۷/۰۱",
                    toDate = "۱۳۹۷/۰۶/۳۱",
                    agreementRow = "۰۹۶۰۰۰۰۲",
                    objectionKind = ObjectionKind.ESTIMATE,
                ),
            ),
            workshopName = "آموزشگاه کامپیوتر توکلی-ایمیل",
            workshopCode = "۰۹۶۸۲۱۰۱۷۰",
            onIntent = {},
            onBack = {},
        )
    }
}
