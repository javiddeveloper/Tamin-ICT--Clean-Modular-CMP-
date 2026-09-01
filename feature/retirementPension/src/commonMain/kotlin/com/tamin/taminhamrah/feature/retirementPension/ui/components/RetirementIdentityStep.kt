package com.tamin.taminhamrah.feature.retirementPension.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementFormError
import com.tamin.taminhamrah.model.pension.retirement.RetirementIdentityPR
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.TaminTextArea
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.retirement_pension_field_address
import taminx.core.core_ui.retirement_pension_field_address_placeholder
import taminx.core.core_ui.retirement_pension_field_phone
import taminx.core.core_ui.retirement_pension_field_phone_placeholder
import taminx.core.core_ui.retirement_pension_identity_confirm
import taminx.core.core_ui.retirement_pension_label_birth_date
import taminx.core.core_ui.retirement_pension_label_father_name
import taminx.core.core_ui.retirement_pension_label_gender
import taminx.core.core_ui.retirement_pension_label_id_number
import taminx.core.core_ui.retirement_pension_label_issue_place
import taminx.core.core_ui.retirement_pension_label_mobile
import taminx.core.core_ui.retirement_pension_value_placeholder

/**
 * Step 3 — what the service already knows, plus the two things it does not: a landline and a home
 * address.
 *
 * The read-only block takes [identity] alone, so typing in either field below it cannot recompose
 * the six rows above.
 */
@Composable
internal fun RetirementIdentityStep(
    identity: RetirementIdentityPR?,
    phoneNumber: String,
    address: String,
    isConfirmed: Boolean,
    error: RetirementFormError?,
    onPhoneChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onConfirmedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    RetirementStepColumn(modifier = modifier) {
        RetirementIdentityCard(identity)

        RetirementNumericField(
            value = phoneNumber,
            onValueChange = onPhoneChange,
            label = stringResource(Res.string.retirement_pension_field_phone),
            slotCount = PHONE_SLOT_COUNT,
            isError = error in PHONE_ERRORS,
            errorMessage = error?.takeIf { it in PHONE_ERRORS }?.text(),
            placeholderChars = stringResource(Res.string.retirement_pension_field_phone_placeholder),
        )

        TaminTextArea(
            value = address,
            onValueChange = onAddressChange,
            label = stringResource(Res.string.retirement_pension_field_address),
            placeholder = stringResource(Res.string.retirement_pension_field_address_placeholder),
            isRequired = true,
            error = error in ADDRESS_ERRORS,
            errorMessage = error?.takeIf { it in ADDRESS_ERRORS }?.text(),
            minLines = 2,
            maxLines = 4,
        )

        RetirementConsentRow(
            text = stringResource(Res.string.retirement_pension_identity_confirm),
            checked = isConfirmed,
            isError = error == RetirementFormError.IdentityConfirm,
            onCheckedChange = onConfirmedChange,
        )
        if (error == RetirementFormError.IdentityConfirm) RetirementErrorLine(error)
    }
}

@Composable
private fun RetirementIdentityCard(identity: RetirementIdentityPR?) {
    val placeholder = stringResource(Res.string.retirement_pension_value_placeholder)
    RetirementCard {
        DetailRow(
            label = stringResource(Res.string.retirement_pension_label_father_name),
            value = identity?.fatherName?.ifBlank { placeholder } ?: placeholder,
            numeric = false,
        )
        DetailRow(
            label = stringResource(Res.string.retirement_pension_label_id_number),
            value = identity?.idNumber?.ifBlank { placeholder } ?: placeholder,
        )
        DetailRow(
            label = stringResource(Res.string.retirement_pension_label_gender),
            value = identity?.gender?.ifBlank { placeholder } ?: placeholder,
            numeric = false,
        )
        DetailRow(
            label = stringResource(Res.string.retirement_pension_label_birth_date),
            value = identity?.birthDate?.ifBlank { placeholder } ?: placeholder,
        )
        DetailRow(
            label = stringResource(Res.string.retirement_pension_label_issue_place),
            value = identity?.issuePlace?.ifBlank { placeholder } ?: placeholder,
            numeric = false,
        )
        DetailRow(
            label = stringResource(Res.string.retirement_pension_label_mobile),
            value = identity?.mobileNumber?.ifBlank { placeholder } ?: placeholder,
        )
    }
}

/** Digits in a fixed-line number. */
private const val PHONE_SLOT_COUNT = 11

/**
 * Which failures belong to each field.
 *
 * Top-level `Set`s rather than a `setOf(...)` at the call site: built inline they would be a fresh
 * instance on every recomposition, which is exactly what an unstable argument looks like.
 */
private val PHONE_ERRORS = setOf(
    RetirementFormError.PhoneRequired,
    RetirementFormError.PhonePrefix,
    RetirementFormError.PhoneLength,
)

private val ADDRESS_ERRORS = setOf(
    RetirementFormError.AddressRequired,
    RetirementFormError.AddressShort,
)
