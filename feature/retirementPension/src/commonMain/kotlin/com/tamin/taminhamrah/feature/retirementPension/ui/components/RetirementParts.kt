package com.tamin.taminhamrah.feature.retirementPension.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementFormError
import com.tamin.taminhamrah.ui.components.SegmentedInputField
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.TaminTextArea
import com.tamin.taminhamrah.ui.components.animatedErrorBorder
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_alert_circle
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.retirement_pension_error_address_invalid
import taminx.core.core_ui.retirement_pension_error_address_required
import taminx.core.core_ui.retirement_pension_error_address_short
import taminx.core.core_ui.retirement_pension_error_authentication
import taminx.core.core_ui.retirement_pension_error_consent
import taminx.core.core_ui.retirement_pension_error_final_confirm
import taminx.core.core_ui.retirement_pension_error_identity_confirm
import taminx.core.core_ui.retirement_pension_error_identity_documents
import taminx.core.core_ui.retirement_pension_error_phone_length
import taminx.core.core_ui.retirement_pension_error_phone_prefix
import taminx.core.core_ui.retirement_pension_error_phone_required
import taminx.core.core_ui.retirement_pension_error_quit_letter
import taminx.core.core_ui.retirement_pension_error_workshop_address
import taminx.core.core_ui.retirement_pension_error_workshop_code
import taminx.core.core_ui.retirement_pension_error_workshop_confirm
import taminx.core.core_ui.retirement_pension_error_workshop_name

/** Copy for a validation failure. The ViewModel names the reason; the wording lives here. */
@Composable
internal fun RetirementFormError.text(): String = stringResource(
    when (this) {
        RetirementFormError.Consent -> Res.string.retirement_pension_error_consent
        RetirementFormError.Authentication -> Res.string.retirement_pension_error_authentication
        RetirementFormError.PhoneRequired -> Res.string.retirement_pension_error_phone_required
        RetirementFormError.PhonePrefix -> Res.string.retirement_pension_error_phone_prefix
        RetirementFormError.PhoneLength -> Res.string.retirement_pension_error_phone_length
        RetirementFormError.AddressRequired -> Res.string.retirement_pension_error_address_required
        RetirementFormError.AddressShort -> Res.string.retirement_pension_error_address_short
        RetirementFormError.AddressInvalid -> Res.string.retirement_pension_error_address_invalid
        RetirementFormError.IdentityConfirm -> Res.string.retirement_pension_error_identity_confirm
        RetirementFormError.WorkshopName -> Res.string.retirement_pension_error_workshop_name
        RetirementFormError.WorkshopCode -> Res.string.retirement_pension_error_workshop_code
        RetirementFormError.WorkshopAddress -> Res.string.retirement_pension_error_workshop_address
        RetirementFormError.WorkshopConfirm -> Res.string.retirement_pension_error_workshop_confirm
        RetirementFormError.IdentityDocuments -> Res.string.retirement_pension_error_identity_documents
        RetirementFormError.QuitLetter -> Res.string.retirement_pension_error_quit_letter
        RetirementFormError.FinalConfirm -> Res.string.retirement_pension_error_final_confirm
    },
)

/**
 * The scrolling body every step sits in: page padding, the app's jelly overscroll, and the gap the
 * design puts between cards.
 */
@Composable
internal fun RetirementStepColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState(), overscrollEffect = rememberJellyOverscroll())
            .padding(horizontal = Spacing.page)
            .padding(bottom = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.cardGap),
        content = content,
    )
}

/** The design's white card: surface fill, hairline border, card radius, inner padding. */
@Composable
internal fun RetirementCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.xl)
            .padding(horizontal = Spacing.md, vertical = Spacing.smd),
        content = content,
    )
}

/**
 * A tick-box and a paragraph, the whole row tappable.
 *
 * Not Material's `Checkbox`: the design draws a rounded square that fills with the accent, and the
 * surrounding card turns red while the box is the thing standing between the user and the next step.
 */
@Composable
internal fun RetirementConsentRow(
    text: String,
    checked: Boolean,
    isError: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val boxShape = RoundedCornerShape(CornerRadius.md)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.bgSurface, RoundedCornerShape(CornerRadius.cardCompact))
            .animatedErrorBorder(
                isError = isError,
                errorColor = colors.dangerText,
                normalColor = if (checked) colors.blueText else colors.border,
                borderWidth = Thickness.medium,
                cornerRadius = CornerRadius.cardCompact,
            )
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = Spacing.md, vertical = Spacing.smd),
        horizontalArrangement = Arrangement.spacedBy(Spacing.smPlus),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(IconSize.small + Spacing.xs)
                .background(if (checked) colors.blueText else colors.bgSurface, boxShape)
                .animatedErrorBorder(
                    isError = isError && !checked,
                    errorColor = colors.dangerText,
                    normalColor = if (checked) colors.blueText else colors.border,
                    borderWidth = Thickness.medium,
                    cornerRadius = CornerRadius.md,
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (checked) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_check),
                    contentDescription = null,
                    tint = colors.bgSurface,
                    modifier = Modifier.size(Spacing.md),
                )
            }
        }
        TaminText(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
            modifier = Modifier.weight(1f),
        )
    }
}

/** The shared complaint line under a step's form: one icon, one sentence, danger color. */
@Composable
internal fun RetirementErrorLine(
    error: RetirementFormError,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_alert_circle),
            contentDescription = null,
            tint = colors.dangerText,
            modifier = Modifier.size(IconSize.small),
        )
        TaminText(
            text = error.text(),
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = colors.dangerText,
            modifier = Modifier.weight(1f),
        )
    }
}

/** A field's caption, with the design's red asterisk when the field is required. */
@Composable
internal fun RetirementFieldLabel(
    text: String,
    isRequired: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
        TaminText(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = colors.textSecondary,
        )
        if (isRequired) {
            TaminText(
                text = REQUIRED_MARK,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.dangerText,
            )
        }
    }
}

/** Purely typographic, so it stays out of the translated strings. */
private const val REQUIRED_MARK = "*"

/**
 * A required numeric field: caption with asterisk, then the digits in the app's segmented field —
 * the same one the change-mobile screen uses, so a code is typed slot by slot with a hint in each.
 *
 * [placeholderChars] fills the empty slots one character at a time, the way a sample phone number
 * reads; [placeholderText] is for a field whose hint is a phrase rather than a shape. The list is
 * remembered so the argument is the same instance between recompositions, not a fresh one.
 */
@Composable
internal fun RetirementNumericField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    slotCount: Int,
    isError: Boolean,
    modifier: Modifier = Modifier,
    errorMessage: String? = null,
    placeholderChars: String? = null,
    placeholderText: String? = null,
) {
    val slots = placeholderChars?.let { chars ->
        remember(chars, slotCount) { chars.take(slotCount).toList().toImmutableList() }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        RetirementFieldLabel(
            text = label,
            isRequired = true,
            modifier = Modifier.padding(bottom = Spacing.xs),
        )
        SegmentedInputField(
            value = value,
            onValueChange = onValueChange,
            slotCount = slotCount,
            error = isError,
            errorMessage = errorMessage,
            placeholderText = placeholderText,
            placeholders = slots ?: dashedSlots(slotCount),
        )
    }
}

/**
 * A single-line free-text field.
 *
 * Built on [TaminTextArea] rather than [TaminTextField]: the design draws a 46px box with 10px of
 * padding, and Material's outlined field will not go below its own 56dp minimum without clipping
 * the text. `TaminTextArea` is already the app's own box at exactly that padding, and it is what
 * the address fields on these same steps use — so the two now match.
 */
@Composable
internal fun RetirementTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isRequired: Boolean,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
) {
    TaminTextArea(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        placeholder = placeholder.orEmpty(),
        isRequired = isRequired,
        error = isError,
        errorMessage = errorMessage,
        minLines = 1,
        maxLines = 1,
    )
}

/** The field's own default — a dash per slot — kept out of the call site so it is not rebuilt. */
@Composable
private fun dashedSlots(slotCount: Int): ImmutableList<Char> =
    remember(slotCount) { List(slotCount) { SLOT_DASH }.toImmutableList() }

private const val SLOT_DASH = 'ـ'
