package com.tamin.taminhamrah.feature.workshops.ui.objectionableDebit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopDocumentBox
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopDocumentTypeSheet
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
import com.tamin.taminhamrah.ui.theme.Spacing
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.obj_form_area_hint
import taminx.core.core_ui.obj_form_area_label
import taminx.core.core_ui.obj_form_check_confirm
import taminx.core.core_ui.obj_form_check_deposit
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
    var isTypeSheetOpen by remember { mutableStateOf(false) }
    var pendingTypeCode by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val filePicker = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        val typeCode = pendingTypeCode
        pendingTypeCode = null
        if (file == null || typeCode == null) return@rememberFilePickerLauncher
        scope.launch {
            onIntent(
                ObjectionableDebitIntent.FormAddDocument(
                    fileName = file.name,
                    bytes = file.readBytes(),
                    typeCode = typeCode,
                ),
            )
        }
    }

    // The six columns the design lists, in its order. Resolved first, then remembered on the
    // debt, so typing in the description does not rebuild them.
    val debtNumberLabel = stringResource(Res.string.payment_sheet_debit_number)
    val agreementRowLabel = stringResource(Res.string.payment_sheet_agreement_row)
    val periodFromLabel = stringResource(Res.string.obj_form_period_from)
    val periodToLabel = stringResource(Res.string.obj_form_period_to)
    val amountLabel = stringResource(Res.string.workshop_debt_amount)
    val notifyDateLabel = stringResource(Res.string.workshop_debt_notify_date)
    val debtRows = remember(debt, debtNumberLabel) {
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

            WorkshopDocumentBox(
                attachments = form.attachments,
                capacity = OBJECTION_MAX_DOCUMENTS,
                onAdd = { isTypeSheetOpen = true },
                isUploading = form.isUploading,
                onRemove = { index ->
                    onIntent(ObjectionableDebitIntent.FormRemoveDocument(index))
                },
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

            form.error?.let { WorkshopFormError(text = stringResource(it)) }
        }

        WorkshopFormFooter(
            nextLabel = stringResource(Res.string.obj_form_submit),
            isBusy = form.isBusy,
            onNext = { onIntent(ObjectionableDebitIntent.FormSubmit) },
        )
    }

    if (isTypeSheetOpen) {
        WorkshopDocumentTypeSheet(
            types = ObjectionDocumentTypes,
            onDismiss = { isTypeSheetOpen = false },
            onSelect = { type ->
                isTypeSheetOpen = false
                pendingTypeCode = type.code
                filePicker.launch()
            },
        )
    }
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
