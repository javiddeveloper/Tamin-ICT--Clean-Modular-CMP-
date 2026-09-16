package com.tamin.taminhamrah.feature.workshops.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import com.tamin.taminhamrah.feature.workshops.ui.WorkshopConstants
import com.tamin.taminhamrah.feature.workshops.ui.model.PersonSearch
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.ui.components.InputRestriction
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.components.TaminStyledTextField
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.animatedErrorBorder
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.abs_form_err_required
import taminx.core.core_ui.ic_info
import taminx.core.core_ui.ic_tamin_calendar
import taminx.core.core_ui.ic_tamin_chevron_down
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.ic_tamin_search
import taminx.core.core_ui.member_insurance_number
import taminx.core.core_ui.member_national_id
import taminx.core.core_ui.workshop_all_items
import taminx.core.core_ui.workshop_search
import taminx.core.core_ui.workshop_select
import taminx.core.core_ui.workshop_ten_digits

/**
 * The panel every workshop screen searches from: its fields, then جست‌وجو and همهٔ موارد.
 *
 * The design lays the fields out on a two-column grid, so a caller puts a full-width field
 * straight into [fields] and pairs half-width ones inside a [Row] of two weighted slots. That is
 * the whole of the layout contract — anything more would be a grid engine for six fields.
 */
@Composable
fun WorkshopSearchCard(
    onSearch: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    fields: @Composable ColumnScope.() -> Unit,
) {
    val colors = LocalTaminColors.current
    val actionTextStyle = MaterialTheme.typography.bodySmall.copy(
        fontWeight = FontWeight.ExtraBold,
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(WorkshopDimens.panelCorner)
            .padding(WorkshopDimens.panelPadding),
        verticalArrangement = Arrangement.spacedBy(WorkshopDimens.fieldGap),
    ) {
        fields()
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = WorkshopDimens.panelButtonsTopMargin - WorkshopDimens.fieldGap),
            horizontalArrangement = Arrangement.spacedBy(WorkshopDimens.fieldGap),
        ) {
            TaminPrimaryButton(
                text = stringResource(Res.string.workshop_search),
                onClick = onSearch,
                background = colors.buttonGradient,
                height = WorkshopDimens.panelButtonHeight,
                shape = SearchActionShape,
                textStyle = actionTextStyle,
                modifier = Modifier.weight(1f),
            )
            TaminOutlinedButton(
                text = stringResource(Res.string.workshop_all_items),
                onClick = onClear,
                height = WorkshopDimens.panelButtonHeight,
                shape = SearchActionShape,
                borderWidth = WorkshopDimens.panelButtonBorderWidth,
                borderColor = colors.blueBorder,
                containerColor = colors.bgSurface,
                contentColor = colors.blueText,
                textStyle = actionTextStyle,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/**
 * One labeled field of a search panel.
 *
 * The label is its own row above the control rather than a floating Material label, because the
 * design's fields are a flat 44dp box with the caption outside them.
 */

/**
 * Why a control with the error border is wrong, printed under it.
 *
 * The same line [SegmentedInputField] and [TaminTextArea] draw — info glyph, then the reason — so
 * a form built from typed fields, a ten-slot code and a picker reports all three identically.
 */
@Composable
fun WorkshopFieldError(text: String, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_info),
            contentDescription = null,
            tint = colors.dangerText,
            modifier = Modifier.size(IconSize.small),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = colors.dangerText,
        )
    }
}

@Composable
fun WorkshopFieldSlot(
    label: String,
    modifier: Modifier = Modifier,
    isRequired: Boolean = false,
    /** Printed under the control. Null while there is nothing wrong. */
    errorText: String? = null,
    content: @Composable () -> Unit,
) {
    val caption = if (isRequired) "$label *" else label
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(WorkshopDimens.fieldLabelGap),
    ) {
        Text(
            text = caption,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = LocalTaminColors.current.textSecondary,
        )
        content()
        errorText?.let { WorkshopFieldError(text = it) }
    }
}

/**
 * A typed field on a workshop panel or form.
 *
 * A thin adapter over [TaminStyledTextField] rather than a field of its own: that one already
 * carries the label, the placeholder, the character restriction, the length cap and the inline
 * error state, and every other form in the app is drawn with it.
 */
@Composable
fun WorkshopTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Number,
    inputRestriction: InputRestriction = InputRestriction.DigitsOnly,
    maxLength: Int? = null,
    isRequired: Boolean = false,
    isValid: Boolean? = null,
    errorText: String? = null,
    /** Passed through; None, the default, draws the value as typed. */
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    TaminStyledTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = placeholder,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        inputRestriction = inputRestriction,
        maxLength = maxLength,
        isRequired = isRequired,
        isValid = isValid,
        errorText = errorText,
        modifier = modifier,
        visualTransformation = visualTransformation,
    )
}

/**
 * A field whose value is chosen rather than typed — a status, a reason, a date.
 *
 * [isDate] only decides the leading glyph; the tap is the caller's, because a date opens a picker
 * and everything else opens a sheet.
 */
@Composable
fun WorkshopPickerField(
    label: String,
    value: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = stringResource(Res.string.workshop_select),
    isDate: Boolean = false,
    icon: DrawableResource? = if (isDate) Res.drawable.ic_tamin_calendar else null,
    isRequired: Boolean = false,
    /** False draws the same animated error border a text field gets; null leaves it neutral. */
    isValid: Boolean? = null,
    /**
     * Why the selection is wrong. Defaulted, because the only way to get a picker wrong is to
     * leave it alone — a caller with a second reason passes its own wording.
     */
    errorText: String? = null,
) {
    val colors = LocalTaminColors.current
    val requiredText = stringResource(Res.string.abs_form_err_required)

    WorkshopFieldSlot(
        label = label,
        modifier = modifier,
        isRequired = isRequired,
        errorText = (errorText ?: requiredText).takeIf { isValid == false },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fieldBox(isError = isValid == false)
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            if (icon != null) {
                Icon(
                    imageVector = vectorResource(icon),
                    contentDescription = null,
                    tint = colors.blueText,
                    modifier = Modifier.size(IconSize.small),
                )
            }
            Text(
                text = value ?: placeholder,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (value != null) colors.textPrimary else colors.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_chevron_down),
                contentDescription = null,
                tint = colors.chevron,
                modifier = Modifier.size(WorkshopDimens.pickerChevronSize),
            )
        }
    }
}

/**
 * What a search is currently narrowed by, each removable.
 *
 * Only shown once a search has been applied — the design lists the values themselves, not
 * "field: value", because on these screens the value already says which field it came from.
 */
@Composable
fun WorkshopFilterChips(
    chips: ImmutableList<String>,
    onRemove: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (chips.isEmpty()) return
    val colors = LocalTaminColors.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(WorkshopDimens.chipGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        chips.forEachIndexed { index, chip ->
            Row(
                modifier = Modifier
                    .clip(FilterChipShape)
                    .background(colors.blueBg)
                    .border(Thickness.border, colors.blueBorder, FilterChipShape)
                    .clickable { onRemove(index) }
                    .padding(horizontal = WorkshopDimens.chipHorizontalPadding, vertical = WorkshopDimens.chipVerticalPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.tabSelector),
            ) {
                Text(
                    text = chip,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.blueText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_cross),
                    contentDescription = null,
                    tint = colors.blueText,
                    modifier = Modifier.size(WorkshopDimens.chipCrossSize),
                )
            }
        }
    }
}

/** The magnifier in the bar that folds a screen's search panel in and out. */
@Composable
fun WorkshopSearchAction(onClick: () -> Unit) {
    TaminTopAppBarButton(
        icon = vectorResource(Res.drawable.ic_tamin_search),
        contentDescription = stringResource(Res.string.workshop_search),
        onClick = onClick,
    )
}

/**
 * The کد ملی / شماره بیمه pair that کارکنان and ذینفعان are both searched by.
 *
 * Both are digits only and held as ASCII: they travel into query parameters, where the Persian
 * digits the cards show would match nothing.
 */
@Composable
fun PersonSearchPanel(
    search: PersonSearch,
    onSearchChange: (PersonSearch) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tenDigits = stringResource(Res.string.workshop_ten_digits)
    WorkshopSearchCard(onSearch = onSearch, onClear = onClear, modifier = modifier) {
        WorkshopTextField(
            label = stringResource(Res.string.member_national_id),
            value = search.nationalId,
            onValueChange = {
                onSearchChange(search.copy(nationalId = it.digitsOnly()))
            },
            placeholder = tenDigits,
            maxLength = WorkshopConstants.NATIONAL_ID_LENGTH,
        )
        WorkshopTextField(
            label = stringResource(Res.string.member_insurance_number),
            value = search.insuranceNumber,
            onValueChange = {
                onSearchChange(
                    search.copy(insuranceNumber = it.digitsOnly()),
                )
            },
            placeholder = tenDigits,
            maxLength = WorkshopConstants.INSURANCE_NUMBER_LENGTH,
        )
    }
}


/** `min-height:44px; padding:0 12px; border-radius:13px; border:1px; background:bg-page`. */
@Composable
private fun Modifier.fieldBox(isError: Boolean = false): Modifier {
    val colors = LocalTaminColors.current
    return this
        .defaultMinSize(minHeight = WorkshopDimens.fieldHeight)
        .clip(FieldBoxShape)
        .background(colors.bgPage)
        // core-ui's own border animation — the one TaminStyledTextField draws — so a picker that
        // is wrong flashes exactly like a text field that is.
        .animatedErrorBorder(
            isError = isError,
            errorColor = colors.dangerText,
            normalColor = colors.border,
            borderWidth = Thickness.border,
            cornerRadius = CornerRadius.listRow,
        )
        .padding(
            horizontal = WorkshopDimens.fieldHorizontalPadding,
            vertical = WorkshopDimens.fieldVerticalPadding,
        )
}

private val SearchActionShape = RoundedCornerShape(CornerRadius.chip)
private val FilterChipShape = RoundedCornerShape(CornerRadius.max)
private val FieldBoxShape = RoundedCornerShape(CornerRadius.listRow)


