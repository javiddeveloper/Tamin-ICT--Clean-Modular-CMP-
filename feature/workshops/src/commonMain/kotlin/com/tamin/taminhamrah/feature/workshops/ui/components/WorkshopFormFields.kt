package com.tamin.taminhamrah.feature.workshops.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import com.tamin.taminhamrah.feature.workshops.ui.WorkshopConstants
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.feature.workshops.ui.workshopMembers.PersonSearch
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
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
    val actionShape = RoundedCornerShape(CornerRadius.chip)
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
                shape = actionShape,
                textStyle = actionTextStyle,
                modifier = Modifier.weight(1f),
            )
            TaminOutlinedButton(
                text = stringResource(Res.string.workshop_all_items),
                onClick = onClear,
                height = WorkshopDimens.panelButtonHeight,
                shape = actionShape,
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
@Composable
fun WorkshopFieldSlot(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(WorkshopDimens.fieldLabelGap)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = LocalTaminColors.current.textSecondary,
        )
        content()
    }
}

/** A number a search is narrowed by — «از شماره», «تا شماره». Digits only, held as ASCII. */
@Composable
fun WorkshopTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Number,
) {
    val colors = LocalTaminColors.current
    val textStyle = MaterialTheme.typography.bodySmall.copy(color = colors.textPrimary)

    WorkshopFieldSlot(label = label, modifier = modifier) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = textStyle,
            cursorBrush = remember(colors.blueText) { SolidColor(colors.blueText) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth().fieldBox(),
            decorationBox = { field ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(
                            text = placeholder,
                            style = LocalTextStyle.current.merge(textStyle),
                            color = colors.textMuted,
                        )
                    }
                    field()
                }
            },
        )
    }
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
) {
    val colors = LocalTaminColors.current

    WorkshopFieldSlot(label = label, modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fieldBox()
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
    val shape = remember { RoundedCornerShape(CornerRadius.max) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(WorkshopDimens.chipGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        chips.forEachIndexed { index, chip ->
            Row(
                modifier = Modifier
                    .clip(shape)
                    .background(colors.blueBg)
                    .border(Thickness.border, colors.blueBorder, shape)
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
                onSearchChange(search.copy(nationalId = it.digitsOnly().take(WorkshopConstants.NATIONAL_ID_LENGTH)))
            },
            placeholder = tenDigits,
        )
        WorkshopTextField(
            label = stringResource(Res.string.member_insurance_number),
            value = search.insuranceNumber,
            onValueChange = {
                onSearchChange(
                    search.copy(insuranceNumber = it.digitsOnly().take(WorkshopConstants.INSURANCE_NUMBER_LENGTH)),
                )
            },
            placeholder = tenDigits,
        )
    }
}


/** `min-height:44px; padding:0 12px; border-radius:13px; border:1px; background:bg-page`. */
@Composable
private fun Modifier.fieldBox(): Modifier {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.listRow)
    return this
        .defaultMinSize(minHeight = WorkshopDimens.fieldHeight)
        .clip(shape)
        .background(colors.bgPage)
        .border(Thickness.border, colors.border, shape)
        .padding(horizontal = WorkshopDimens.fieldHorizontalPadding, vertical = WorkshopDimens.fieldVerticalPadding)
}

