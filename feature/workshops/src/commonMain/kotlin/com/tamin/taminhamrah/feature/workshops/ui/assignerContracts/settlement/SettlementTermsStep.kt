package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopDocumentsPanel
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormSection
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopPickerField
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopRecordCard
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopTextField
import com.tamin.taminhamrah.feature.workshops.ui.model.SettlementSubjectImageTypes
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachment
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.model.workshop.AssignerPartyPR
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.InputRestriction
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminOptionSheetItem
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminSearchableListSheet
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.assigner_field_national_id
import taminx.core.core_ui.settlement_assigner_materials_value
import taminx.core.core_ui.settlement_budget_row
import taminx.core.core_ui.settlement_build_price
import taminx.core.core_ui.settlement_contractor_address
import taminx.core.core_ui.settlement_drivers_price
import taminx.core.core_ui.settlement_drivers_price_hint
import taminx.core.core_ui.settlement_equipment_price
import taminx.core.core_ui.settlement_equivalent_rial
import taminx.core.core_ui.settlement_execution_price
import taminx.core.core_ui.settlement_foreign_equipment
import taminx.core.core_ui.settlement_installation_price
import taminx.core.core_ui.settlement_insurance_paid
import taminx.core.core_ui.settlement_manual_percent
import taminx.core.core_ui.settlement_mechanical_percent
import taminx.core.core_ui.settlement_owner_drivers_amount
import taminx.core.core_ui.settlement_plan_number
import taminx.core.core_ui.settlement_plan_number_hint
import taminx.core.core_ui.settlement_price_list_owner
import taminx.core.core_ui.settlement_price_list_owner_hint
import taminx.core.core_ui.settlement_services_amount
import taminx.core.core_ui.settlement_subject
import taminx.core.core_ui.settlement_supply_owner
import taminx.core.core_ui.settlement_terms_section
import taminx.core.core_ui.settlement_transport_price

/**
 * «شرایط قرارداد با توجه به موضوع کار» — the موضوع کار, then whatever that subject asks for.
 *
 * Every control is one of the کارگاه forms already draw: the picker field, the typed field, the upload
 * box and the record card. Emits straight into the step's column, so it adds no layout of its own.
 */
@Composable
internal fun SettlementTermsStep(
    contractor: AssignerPartyPR,
    subjects: ImmutableList<TaminOptionSheetItem>,
    isSubjectsLoading: Boolean,
    subject: TaminOptionSheetItem?,
    form: SettlementTermsForm,
    terms: SettlementTerms,
    /** The gross amount less the deduction subjects 04–07 type; ignored by every other subject. */
    remainder: Long,
    isUploading: Boolean,
    errors: ImmutableMap<SettlementField, StringResource>,
    onIntent: (SettlementRequestIntent) -> Unit,
) {
    val subjectLabel = stringResource(Res.string.settlement_subject)
    var isSubjectSheetOpen by rememberSaveable { mutableStateOf(false) }

    WorkshopFormSection(title = stringResource(Res.string.settlement_terms_section))
    SettlementChoiceField(
        label = subjectLabel,
        value = subject?.label,
        error = errors[SettlementField.SUBJECT],
        onClick = {
            // A list that failed to arrive is asked for again rather than opening empty.
            if (subjects.isEmpty()) onIntent(SettlementRequestIntent.LoadSubjects)
            isSubjectSheetOpen = true
        },
    )

    when (form) {
        SettlementTermsForm.PRICE_LIST -> {
            SettlementOptionField(
                label = stringResource(Res.string.settlement_price_list_owner),
                options = PriceListOwnerOptions,
                selectedCode = terms.owner,
                error = errors[SettlementField.OWNER],
                onSelect = { onIntent(SettlementRequestIntent.FieldChanged(SettlementField.OWNER, it)) },
            )
            SettlementHint(stringResource(Res.string.settlement_price_list_owner_hint))
            SettlementTextField(
                field = SettlementField.TEXT1,
                label = stringResource(Res.string.settlement_plan_number),
                value = terms.text1,
                error = errors[SettlementField.TEXT1],
                onIntent = onIntent,
                isDigits = false,
            )
            SettlementHint(stringResource(Res.string.settlement_plan_number_hint))
            SettlementTextField(
                field = SettlementField.TEXT2,
                label = stringResource(Res.string.settlement_budget_row),
                value = terms.text2,
                error = errors[SettlementField.TEXT2],
                onIntent = onIntent,
                isDigits = false,
            )
            SettlementTextField(
                field = SettlementField.AMOUNT1,
                label = stringResource(Res.string.settlement_insurance_paid),
                value = terms.amount1,
                error = errors[SettlementField.AMOUNT1],
                onIntent = onIntent,
            )
            SettlementSubjectImage(image = terms.image, isUploading = isUploading, onIntent = onIntent)
        }

        SettlementTermsForm.MATERIALS_SUPPLY -> {
            SettlementOptionField(
                label = stringResource(Res.string.settlement_supply_owner),
                options = SupplyOwnerOptions,
                selectedCode = terms.owner,
                error = errors[SettlementField.OWNER],
                onSelect = { onIntent(SettlementRequestIntent.FieldChanged(SettlementField.OWNER, it)) },
            )
            if (terms.owner == SHARED_SUPPLY_CODE) {
                SettlementTextField(
                    field = SettlementField.AMOUNT1,
                    label = stringResource(Res.string.settlement_assigner_materials_value),
                    value = terms.amount1,
                    error = errors[SettlementField.AMOUNT1],
                    onIntent = onIntent,
                )
            }
        }

        SettlementTermsForm.MECHANICAL_SHARE -> {
            SettlementTextField(
                field = SettlementField.TEXT1,
                label = stringResource(Res.string.settlement_mechanical_percent),
                value = terms.text1,
                error = errors[SettlementField.TEXT1],
                onIntent = onIntent,
                maxLength = PERCENT_MAX_LENGTH,
            )
            if (terms.text2.isNotBlank()) {
                SettlementFigure(
                    stringResource(Res.string.settlement_manual_percent, terms.text2.toPersianDigits()),
                )
            }
        }

        SettlementTermsForm.DRIVERS, SettlementTermsForm.EQUIPMENT -> {
            val isDrivers = form == SettlementTermsForm.DRIVERS
            SettlementTextField(
                field = SettlementField.AMOUNT1,
                label = stringResource(
                    if (isDrivers) Res.string.settlement_drivers_price else Res.string.settlement_equipment_price,
                ),
                value = terms.amount1,
                error = errors[SettlementField.AMOUNT1],
                onIntent = onIntent,
            )
            if (isDrivers) SettlementHint(stringResource(Res.string.settlement_drivers_price_hint))
            // What is left once the deduction is out; the error line under the field says why when
            // there is nothing left.
            if (terms.amount1.isNotBlank() && remainder > 0) {
                SettlementFigure(
                    stringResource(
                        if (isDrivers) {
                            Res.string.settlement_owner_drivers_amount
                        } else {
                            Res.string.settlement_services_amount
                        },
                        remainder.toPriceFormat(),
                    ),
                )
            }
        }

        SettlementTermsForm.BUILD_COSTS -> {
            WorkshopRecordCard {
                DetailRow(
                    label = stringResource(Res.string.settlement_contractor_address),
                    value = contractor.address,
                    numeric = false,
                    verticalPadding = WorkshopDimens.cellVerticalPadding,
                )
                TaminDivider()
                DetailRow(
                    label = stringResource(Res.string.assigner_field_national_id),
                    value = contractor.nationalId,
                    verticalPadding = WorkshopDimens.cellVerticalPadding,
                )
            }
            SettlementTextField(
                field = SettlementField.AMOUNT1,
                label = stringResource(Res.string.settlement_build_price),
                value = terms.amount1,
                error = errors[SettlementField.AMOUNT1],
                onIntent = onIntent,
            )
            SettlementTextField(
                field = SettlementField.AMOUNT2,
                label = stringResource(Res.string.settlement_transport_price),
                value = terms.amount2,
                error = errors[SettlementField.AMOUNT2],
                onIntent = onIntent,
            )
            SettlementTextField(
                field = SettlementField.AMOUNT3,
                label = stringResource(Res.string.settlement_installation_price),
                value = terms.amount3,
                error = errors[SettlementField.AMOUNT3],
                onIntent = onIntent,
            )
            SettlementTextField(
                field = SettlementField.AMOUNT4,
                label = stringResource(Res.string.settlement_execution_price),
                value = terms.amount4,
                error = errors[SettlementField.AMOUNT4],
                onIntent = onIntent,
            )
        }

        SettlementTermsForm.FOREIGN_EQUIPMENT -> {
            SettlementTextField(
                field = SettlementField.AMOUNT1,
                label = stringResource(Res.string.settlement_foreign_equipment),
                value = terms.amount1,
                error = errors[SettlementField.AMOUNT1],
                onIntent = onIntent,
            )
            SettlementTextField(
                field = SettlementField.AMOUNT2,
                label = stringResource(Res.string.settlement_equivalent_rial),
                value = terms.amount2,
                error = errors[SettlementField.AMOUNT2],
                onIntent = onIntent,
            )
            SettlementSubjectImage(image = terms.image, isUploading = isUploading, onIntent = onIntent)
        }

        SettlementTermsForm.NONE -> Unit
    }

    if (isSubjectSheetOpen) {
        TaminSearchableListSheet(
            title = subjectLabel,
            items = subjects,
            onItemSelected = {
                isSubjectSheetOpen = false
                onIntent(SettlementRequestIntent.SubjectSelected(it))
            },
            onDismiss = { isSubjectSheetOpen = false },
            isLoading = isSubjectsLoading,
        )
    }
}

/** A typed field of this form: digits unless it says otherwise, and its error printed under it. */
@Composable
internal fun SettlementTextField(
    field: SettlementField,
    label: String,
    value: String,
    error: StringResource?,
    onIntent: (SettlementRequestIntent) -> Unit,
    placeholder: String = "",
    isDigits: Boolean = true,
    isRequired: Boolean = true,
    maxLength: Int = AMOUNT_MAX_LENGTH,
) {
    val errorText = error?.let { stringResource(it) }
    WorkshopTextField(
        label = label,
        value = value,
        onValueChange = { onIntent(SettlementRequestIntent.FieldChanged(field, it)) },
        placeholder = placeholder,
        keyboardType = if (isDigits) KeyboardType.Number else KeyboardType.Text,
        inputRestriction = if (isDigits) InputRestriction.DigitsOnly else InputRestriction.None,
        maxLength = maxLength,
        isRequired = isRequired,
        isValid = if (errorText != null) false else null,
        errorText = errorText,
    )
}

/** A value chosen from a sheet, with the required-field treatment every picker here gets. */
@Composable
internal fun SettlementChoiceField(
    label: String,
    value: String?,
    error: StringResource?,
    onClick: () -> Unit,
    isDate: Boolean = false,
) {
    val errorText = error?.let { stringResource(it) }
    WorkshopPickerField(
        label = label,
        value = value,
        onClick = onClick,
        isDate = isDate,
        isRequired = true,
        isValid = if (errorText != null) false else null,
        errorText = errorText,
    )
}

/** A short fixed list of answers, offered in the app's option sheet with no search to type into. */
@Composable
private fun SettlementOptionField(
    label: String,
    options: ImmutableList<SettlementOption>,
    selectedCode: String,
    error: StringResource?,
    onSelect: (String) -> Unit,
) {
    var isOpen by rememberSaveable { mutableStateOf(false) }
    val selected = remember(options, selectedCode) { options.firstOrNull { it.code == selectedCode } }
    SettlementChoiceField(
        label = label,
        value = selected?.let { stringResource(it.label) },
        error = error,
        onClick = { isOpen = true },
    )
    if (isOpen) {
        // Only composed while the sheet is up, so resolving the three labels here costs nothing else.
        val items = options.map { TaminOptionSheetItem(id = it.code, label = stringResource(it.label)) }
        TaminSearchableListSheet(
            title = label,
            items = items,
            onItemSelected = {
                isOpen = false
                onSelect(it.id)
            },
            onDismiss = { isOpen = false },
            showSearch = false,
        )
    }
}

/** The conditions' own image — one file, so the box asks nothing about its type. */
@Composable
private fun SettlementSubjectImage(
    image: ImmutableList<WorkshopAttachment>,
    isUploading: Boolean,
    onIntent: (SettlementRequestIntent) -> Unit,
) {
    WorkshopDocumentsPanel(
        attachments = image,
        types = SettlementSubjectImageTypes,
        capacity = 1,
        onAdd = { fileName, bytes, _ -> onIntent(SettlementRequestIntent.AddSubjectImage(fileName, bytes)) },
        onRemove = { onIntent(SettlementRequestIntent.RemoveSubjectImage) },
        isUploading = isUploading,
    )
}

/** The old app's explanatory line under a field, in the form's quiet caption color. */
@Composable
internal fun SettlementHint(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = LocalTaminColors.current.textTertiary,
        modifier = modifier,
    )
}

/** A figure the form works out for the user — a total, a remainder, a share. */
@Composable
internal fun SettlementFigure(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = LocalTaminColors.current.greenText,
        modifier = modifier,
    )
}

/** Fifteen digits: a quadrillion rials, far past any پیمان and well inside a `Long`. */
internal const val AMOUNT_MAX_LENGTH = 15

private const val PERCENT_MAX_LENGTH = 3
