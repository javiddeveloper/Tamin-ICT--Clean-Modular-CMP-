package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.settlement

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopBannerTone
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopDocumentsPanel
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormBanner
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopTextField
import com.tamin.taminhamrah.feature.workshops.ui.model.SettlementSubjectImageTypes
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachment
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.model.workshop.AssignerPartyPR
import com.tamin.taminhamrah.ui.components.InputRestriction
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminStyledTextField
import com.tamin.taminhamrah.ui.components.ThousandsSeparatorTransformation
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminOptionSheetItem
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminSearchableListSheet
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_calendar
import taminx.core.core_ui.ic_tamin_chevron_down
import taminx.core.core_ui.workshop_select
import taminx.core.core_ui.assigner_field_national_id
import taminx.core.core_ui.settlement_amount_rial
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
import taminx.core.core_ui.settlement_manual_percent_label
import taminx.core.core_ui.settlement_mechanical_percent
import taminx.core.core_ui.settlement_owner_drivers_label
import taminx.core.core_ui.settlement_percent_value
import taminx.core.core_ui.settlement_plan_number
import taminx.core.core_ui.settlement_plan_number_hint
import taminx.core.core_ui.settlement_price_list_owner
import taminx.core.core_ui.settlement_price_list_owner_hint
import taminx.core.core_ui.settlement_services_label
import taminx.core.core_ui.settlement_subject
import taminx.core.core_ui.settlement_subject_none_note
import taminx.core.core_ui.settlement_subject_search_empty
import taminx.core.core_ui.settlement_subject_search_hint
import taminx.core.core_ui.settlement_supply_owner
import taminx.core.core_ui.settlement_transport_price
import taminx.core.core_ui.settlement_value_missing

/**
 * «شرایط قرارداد با توجه به موضوع کار» — the موضوع کار, then whatever that subject asks for.
 *
 * Laid out as the design lays each subject out: paired fields side by side, what the form works out
 * for the user in a teal read-only field, and what it only reads back in a gray one. Emits straight
 * into the step's column, so it adds no layout of its own.
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
    /** The gross amount itself — with none typed yet, a remainder has nothing to be worked out from. */
    gross: Long,
    isUploading: Boolean,
    errors: ImmutableMap<SettlementField, StringResource>,
    onIntent: (SettlementRequestIntent) -> Unit,
) {
    val subjectLabel = stringResource(Res.string.settlement_subject)
    var isSubjectSheetOpen by rememberSaveable { mutableStateOf(false) }

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
            SettlementFieldPair {
                SettlementTextField(
                    field = SettlementField.TEXT1,
                    label = stringResource(Res.string.settlement_plan_number),
                    value = terms.text1,
                    error = errors[SettlementField.TEXT1],
                    onIntent = onIntent,
                    isDigits = false,
                    modifier = Modifier.weight(1f),
                )
                SettlementTextField(
                    field = SettlementField.TEXT2,
                    label = stringResource(Res.string.settlement_budget_row),
                    value = terms.text2,
                    error = errors[SettlementField.TEXT2],
                    onIntent = onIntent,
                    isDigits = false,
                    modifier = Modifier.weight(1f),
                )
            }
            SettlementHint(stringResource(Res.string.settlement_plan_number_hint))
            SettlementTextField(
                field = SettlementField.AMOUNT1,
                label = stringResource(Res.string.settlement_insurance_paid),
                value = terms.amount1,
                error = errors[SettlementField.AMOUNT1],
                onIntent = onIntent,
            )
            SettlementSubjectImage(
                image = terms.image,
                isUploading = isUploading,
                isError = SettlementField.SUBJECT_IMAGE in errors,
                onIntent = onIntent,
            )
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

        SettlementTermsForm.MECHANICAL_SHARE -> SettlementFieldPair {
            SettlementTextField(
                field = SettlementField.TEXT1,
                label = stringResource(Res.string.settlement_mechanical_percent),
                value = terms.text1,
                error = errors[SettlementField.TEXT1],
                onIntent = onIntent,
                maxLength = PERCENT_MAX_LENGTH,
                groupsThousands = false,
                modifier = Modifier.weight(1f),
            )
            SettlementValueField(
                label = stringResource(Res.string.settlement_manual_percent_label),
                value = stringResource(
                    Res.string.settlement_percent_value,
                    (SETTLEMENT_FULL_SHARE - (terms.text1.toIntOrNull() ?: 0)).toString().toPersianDigits(),
                ),
                isCalculated = true,
                modifier = Modifier.weight(1f),
            )
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
            // What is left once the deduction is out, worked out as it is typed.
            SettlementValueField(
                label = stringResource(
                    if (isDrivers) Res.string.settlement_owner_drivers_label else Res.string.settlement_services_label,
                ),
                value = if (gross > 0) {
                    stringResource(Res.string.settlement_amount_rial, remainder.coerceAtLeast(0).toPriceFormat())
                } else {
                    stringResource(Res.string.settlement_value_missing)
                },
                isCalculated = true,
            )
        }

        SettlementTermsForm.BUILD_COSTS -> {
            SettlementValueField(
                label = stringResource(Res.string.settlement_contractor_address),
                value = contractor.address,
                isCalculated = false,
                numeric = false,
            )
            SettlementValueField(
                label = stringResource(Res.string.assigner_field_national_id),
                value = contractor.nationalId,
                isCalculated = false,
            )
            SettlementFieldPair {
                SettlementTextField(
                    field = SettlementField.AMOUNT1,
                    label = stringResource(Res.string.settlement_build_price),
                    value = terms.amount1,
                    error = errors[SettlementField.AMOUNT1],
                    onIntent = onIntent,
                    modifier = Modifier.weight(1f),
                )
                SettlementTextField(
                    field = SettlementField.AMOUNT2,
                    label = stringResource(Res.string.settlement_transport_price),
                    value = terms.amount2,
                    error = errors[SettlementField.AMOUNT2],
                    onIntent = onIntent,
                    modifier = Modifier.weight(1f),
                )
            }
            SettlementFieldPair {
                SettlementTextField(
                    field = SettlementField.AMOUNT3,
                    label = stringResource(Res.string.settlement_installation_price),
                    value = terms.amount3,
                    error = errors[SettlementField.AMOUNT3],
                    onIntent = onIntent,
                    modifier = Modifier.weight(1f),
                )
                SettlementTextField(
                    field = SettlementField.AMOUNT4,
                    label = stringResource(Res.string.settlement_execution_price),
                    value = terms.amount4,
                    error = errors[SettlementField.AMOUNT4],
                    onIntent = onIntent,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        SettlementTermsForm.FOREIGN_EQUIPMENT -> {
            SettlementFieldPair {
                SettlementTextField(
                    field = SettlementField.AMOUNT1,
                    label = stringResource(Res.string.settlement_foreign_equipment),
                    value = terms.amount1,
                    error = errors[SettlementField.AMOUNT1],
                    onIntent = onIntent,
                    modifier = Modifier.weight(1f),
                )
                SettlementTextField(
                    field = SettlementField.AMOUNT2,
                    label = stringResource(Res.string.settlement_equivalent_rial),
                    value = terms.amount2,
                    error = errors[SettlementField.AMOUNT2],
                    onIntent = onIntent,
                    modifier = Modifier.weight(1f),
                )
            }
            SettlementSubjectImage(
                image = terms.image,
                isUploading = isUploading,
                isError = SettlementField.SUBJECT_IMAGE in errors,
                onIntent = onIntent,
            )
        }

        // Said rather than left blank, so an empty step does not read as a form that failed to load.
        SettlementTermsForm.NONE -> if (subject != null) {
            WorkshopFormBanner(
                text = stringResource(Res.string.settlement_subject_none_note),
                tone = WorkshopBannerTone.SUCCESS,
            )
        }
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
            searchPlaceholder = stringResource(Res.string.settlement_subject_search_hint),
            emptyMessage = stringResource(Res.string.settlement_subject_search_empty),
        )
    }
}

/**
 * A typed field of this form: digits unless it says otherwise.
 *
 * A field with something wrong only turns red; the footer's banner says what, in the design's words.
 * Digits are shown grouped in thousands as they are typed, the way the old app's amount fields read;
 * a number that is not an amount — a letter number, a percentage — passes [groupsThousands] false.
 */
@Composable
internal fun SettlementTextField(
    field: SettlementField,
    label: String,
    value: String,
    error: StringResource?,
    onIntent: (SettlementRequestIntent) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isDigits: Boolean = true,
    isRequired: Boolean = true,
    maxLength: Int = AMOUNT_MAX_LENGTH,
    groupsThousands: Boolean = true,
) {
    WorkshopTextField(
        label = label,
        value = value,
        onValueChange = { onIntent(SettlementRequestIntent.FieldChanged(field, it)) },
        modifier = modifier,
        placeholder = placeholder,
        keyboardType = if (isDigits) KeyboardType.Number else KeyboardType.Text,
        inputRestriction = if (isDigits) InputRestriction.DigitsOnly else InputRestriction.None,
        maxLength = maxLength,
        isRequired = isRequired,
        isValid = if (error != null) false else null,
        visualTransformation = if (isDigits && groupsThousands) {
            ThousandsSeparatorTransformation
        } else {
            VisualTransformation.None
        },
        textStyle = SettlementFieldValueStyle(),
    )
}

/**
 * A value chosen from a sheet, with the required-field treatment every picker here gets.
 *
 * Drawn by the same [TaminStyledTextField] the typed fields use, read-only with a tap overlay, so a
 * picker paired with a typed field — «تاریخ نامه» beside «شماره نامه» — matches its height, label,
 * red star and border instead of being the workshop picker's shorter box. Like the typed fields, a
 * wrong one only turns red; the footer's banner says what.
 */
@Composable
internal fun SettlementChoiceField(
    label: String,
    value: String?,
    error: StringResource?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDate: Boolean = false,
) {
    TaminStyledTextField(
        value = value.orEmpty(),
        onValueChange = {},
        label = label,
        placeholder = stringResource(Res.string.workshop_select),
        leadingIcon = if (isDate) vectorResource(Res.drawable.ic_tamin_calendar) else null,
        trailingIcon = vectorResource(Res.drawable.ic_tamin_chevron_down),
        isValid = if (error != null) false else null,
        readOnly = true,
        isRequired = true,
        onClick = onClick,
        modifier = modifier,
        textStyle = SettlementFieldValueStyle(),
    )
}

/** Two fields side by side, as the design halves a row with `width:calc(50% - …)`. */
@Composable
internal fun SettlementFieldPair(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.Top,
        content = content,
    )
}

/**
 * A value the user reads rather than types: teal when the form worked it out, gray when it only
 * reads it back from the پیمان.
 */
@Composable
private fun SettlementValueField(
    label: String,
    value: String,
    isCalculated: Boolean,
    modifier: Modifier = Modifier,
    numeric: Boolean = true,
) {
    val colors = LocalTaminColors.current
    val shape = remember { RoundedCornerShape(ValueFieldCorner) }
    val contentColor = if (isCalculated) colors.tealText else colors.textTertiary
    // Label and box measured as TaminStyledTextField draws its own, because these sit in the same
    // rows as typed fields and a shorter box beside a taller one read as misaligned.
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = ValueFieldLabelSize,
            fontWeight = FontWeight.Bold,
            color = colors.textTertiary,
            modifier = Modifier.padding(bottom = ValueFieldLabelGap),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ValueFieldHeight)
                .clip(shape)
                .background(if (isCalculated) colors.tealBg else colors.bgPage)
                .border(
                    ValueFieldBorder,
                    if (isCalculated) colors.tealText.copy(alpha = WorkshopDimens.cardButtonOutlineAlpha) else colors.border,
                    shape,
                )
                .padding(horizontal = ValueFieldHorizontalPadding),
            // The page's own side for a number too, like every field on this form.
            contentAlignment = Alignment.CenterStart,
        ) {
            if (numeric) {
                NumericText(text = value, style = MaterialTheme.typography.labelLarge, color = contentColor)
            } else {
                Text(text = value, style = MaterialTheme.typography.labelMedium, color = contentColor)
            }
        }
    }
}

/**
 * The value inside a field of this form, a step below the app's default `bodyLarge` — the form packs
 * two fields to a row, and the larger size crowded them.
 */
@Composable
private fun SettlementFieldValueStyle(): TextStyle = MaterialTheme.typography.bodyMedium

// TaminStyledTextField's own measurements, which it keeps as literals rather than theme tokens.
private val ValueFieldHeight = 50.dp
private val ValueFieldCorner = 13.dp
private val ValueFieldBorder = 1.5.dp
private val ValueFieldHorizontalPadding = 14.dp
private val ValueFieldLabelGap = 6.dp
private val ValueFieldLabelSize = 12.5.sp

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
    isError: Boolean,
    onIntent: (SettlementRequestIntent) -> Unit,
) {
    WorkshopDocumentsPanel(
        attachments = image,
        types = SettlementSubjectImageTypes,
        capacity = 1,
        asksForType = false,
        onAdd = { fileName, bytes, _ -> onIntent(SettlementRequestIntent.AddSubjectImage(fileName, bytes)) },
        onRemove = { onIntent(SettlementRequestIntent.RemoveSubjectImage) },
        isUploading = isUploading,
        isError = isError,
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

/** Fifteen digits: a quadrillion rials, far past any پیمان and well inside a `Long`. */
internal const val AMOUNT_MAX_LENGTH = 15

private const val PERCENT_MAX_LENGTH = 3
