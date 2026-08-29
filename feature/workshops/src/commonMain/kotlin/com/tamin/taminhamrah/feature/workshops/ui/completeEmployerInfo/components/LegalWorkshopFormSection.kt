package com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.model.employerInfo.CompanyTypePR
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.components.SegmentedInputField
import com.tamin.taminhamrah.ui.components.animatedErrorBorder
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.employer_info_btn_send_otp
import taminx.core.core_ui.employer_info_ceo_birth_label
import taminx.core.core_ui.employer_info_ceo_birth_picker_title
import taminx.core.core_ui.employer_info_ceo_name_prefix
import taminx.core.core_ui.employer_info_ceo_nid_label
import taminx.core.core_ui.employer_info_ceo_section_title
import taminx.core.core_ui.employer_info_company_type_label
import taminx.core.core_ui.employer_info_email
import taminx.core.core_ui.employer_info_legal_form_title
import taminx.core.core_ui.employer_info_legal_name_prefix
import taminx.core.core_ui.employer_info_legal_nid_label
import taminx.core.core_ui.employer_info_mobile
import taminx.core.core_ui.employer_info_pick_date
import taminx.core.core_ui.employer_info_placeholder_10_digits
import taminx.core.core_ui.employer_info_placeholder_11_digits
import taminx.core.core_ui.employer_info_placeholder_mobile
import taminx.core.core_ui.employer_info_placeholder_tel
import taminx.core.core_ui.employer_info_select_hint
import taminx.core.core_ui.employer_info_tel_label
import taminx.core.core_ui.ic_tamin_chevron_back

private val MOBILE_REGEX = Regex("^09\\d{9}$")
private val EMAIL_REGEX = Regex("^\\S+@\\S+\\.\\S+$")

@Composable
fun LegalWorkshopFormSection(
    legalNationalId: String,
    onLegalNationalIdChanged: (String) -> Unit,
    isLegalWorkshopInquiring: Boolean,
    legalWorkshopName: String?,
    selectedCompanyType: CompanyTypePR?,
    onOpenCompanyTypePicker: () -> Unit,
    ceoNationalId: String,
    onCeoNationalIdChanged: (String) -> Unit,
    ceoBirthDatePersian: String,
    onCeoBirthDateSelected: (millis: Long, persianDate: String) -> Unit,
    isCeoInquiring: Boolean,
    ceoFullName: String?,
    telephone: String,
    onTelephoneChanged: (String) -> Unit,
    mobile: String,
    onMobileChanged: (String) -> Unit,
    email: String,
    onEmailChanged: (String) -> Unit,
    errorMessage: String?,
    hasAttemptedSubmit: Boolean,
    isSubmitting: Boolean,
    canSubmit: Boolean,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    var showDatePicker by remember { mutableStateOf(false) }

    val isLegalNidError = hasAttemptedSubmit && legalNationalId.filter { it.isDigit() }.length != 11
    val isCompanyTypeError = hasAttemptedSubmit && selectedCompanyType == null
    val isCeoNidError = hasAttemptedSubmit && ceoNationalId.filter { it.isDigit() }.length != 10
    val isCeoBirthError = hasAttemptedSubmit && ceoBirthDatePersian.isBlank()
    val isMobileError = hasAttemptedSubmit && !mobile.filter { it.isDigit() }.matches(MOBILE_REGEX)
    val isEmailError = hasAttemptedSubmit && !email.trim().matches(EMAIL_REGEX)

    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        // Section 1: Legal Workshop Info Card
        Text(
            text = stringResource(Res.string.employer_info_legal_form_title),
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
                fontSize = 12.5.sp,
            ),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(18.dp),
                    ambientColor = colors.shadowSubtle,
                    spotColor = colors.shadowSubtle,
                )
                .clip(RoundedCornerShape(18.dp))
                .background(colors.bgSurface)
                .border(1.dp, colors.border, RoundedCornerShape(18.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            // Legal National ID
            Column {
                Text(
                    text = stringResource(Res.string.employer_info_legal_nid_label),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary,
                        fontSize = 11.sp,
                    ),
                    modifier = Modifier.padding(bottom = 5.dp),
                )
                SegmentedInputField(
                    value = legalNationalId,
                    onValueChange = onLegalNationalIdChanged,
                    slotCount = LEGAL_NATIONAL_ID_SLOTS,
                    error = isLegalNidError,
                )
            }

            // Inquiry Shimmer or Resolved Legal Entity Name Card
            if (isLegalWorkshopInquiring) {
                EmployerInfoLegalInquiryShimmer()
            } else {
                AnimatedVisibility(
                    visible = !legalWorkshopName.isNullOrBlank(),
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(13.dp))
                            .background(colors.blueBg)
                            .border(1.dp, colors.hawkesBlue, RoundedCornerShape(13.dp))
                            .padding(horizontal = 11.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = stringResource(Res.string.employer_info_legal_name_prefix),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = colors.blueText,
                                fontSize = 10.sp,
                            ),
                        )
                        Text(
                            text = legalWorkshopName.orEmpty(),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = colors.blueText,
                                fontSize = 11.5.sp,
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            // Company Type Picker
            Column {
                Text(
                    text = stringResource(Res.string.employer_info_company_type_label),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary,
                        fontSize = 11.sp,
                    ),
                    modifier = Modifier.padding(bottom = 5.dp),
                )
                SelectPickerChip(
                    text = selectedCompanyType?.titleRes?.let { stringResource(it) }
                        ?: stringResource(Res.string.employer_info_select_hint),
                    isSelected = selectedCompanyType != null,
                    isError = isCompanyTypeError,
                    onClick = onOpenCompanyTypePicker,
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Section 2: CEO / Board Member Info Card
        Text(
            text = stringResource(Res.string.employer_info_ceo_section_title),
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
                fontSize = 12.5.sp,
            ),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(18.dp),
                    ambientColor = colors.shadowSubtle,
                    spotColor = colors.shadowSubtle,
                )
                .clip(RoundedCornerShape(18.dp))
                .background(colors.bgSurface)
                .border(1.dp, colors.border, RoundedCornerShape(18.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            // National ID & Birth Date Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                // CEO National ID
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.employer_info_ceo_nid_label),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textSecondary,
                            fontSize = 11.sp,
                        ),
                        modifier = Modifier.padding(bottom = 5.dp),
                    )
                    SegmentedInputField(
                            value = ceoNationalId,
                            onValueChange = onCeoNationalIdChanged,
                            slotCount = CEO_NATIONAL_ID_SLOTS,
                            error = isCeoNidError,
                        )
                }

                // Date of Birth Picker
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.employer_info_ceo_birth_label),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textSecondary,
                            fontSize = 11.sp,
                        ),
                        modifier = Modifier.padding(bottom = 5.dp),
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(13.dp))
                            .background(colors.bgPage)
                            .animatedErrorBorder(
                                isError = isCeoBirthError,
                                errorColor = colors.dangerText,
                                normalColor = colors.border,
                                borderWidth = Thickness.border,
                                cornerRadius = FieldCorner,
                            )
                            .clickable { showDatePicker = true }
                            .padding(horizontal = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DateRange,
                            contentDescription = null,
                            tint = colors.blueText,
                            modifier = Modifier.size(15.dp),
                        )
                        val dateText = ceoBirthDatePersian.ifBlank { stringResource(Res.string.employer_info_pick_date) }
                        val isDateSelected = ceoBirthDatePersian.isNotBlank()
                        Text(
                            text = dateText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (isDateSelected) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (isDateSelected) colors.textPrimary else colors.textMuted,
                                fontSize = 11.5.sp,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            // Inquiry Shimmer or Resolved CEO Name Card
            if (isCeoInquiring) {
                EmployerInfoCeoInquiryShimmer()
            } else {
                AnimatedVisibility(
                    visible = !ceoFullName.isNullOrBlank(),
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(13.dp))
                            .background(colors.greenBg)
                            .border(1.dp, colors.greenBorder, RoundedCornerShape(13.dp))
                            .padding(horizontal = 11.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = stringResource(Res.string.employer_info_ceo_name_prefix),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = colors.greenText,
                                fontSize = 10.sp,
                            ),
                        )
                        Text(
                            text = ceoFullName.orEmpty(),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = colors.greenText,
                                fontSize = 11.5.sp,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            // Phone & Mobile Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                // Fixed Phone
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.employer_info_tel_label),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textSecondary,
                            fontSize = 11.sp,
                        ),
                        modifier = Modifier.padding(bottom = 5.dp),
                    )
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        BasicTextField(
                            value = telephone,
                            onValueChange = { onTelephoneChanged(it.filter { c -> c.isDigit() }.take(11)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary,
                            ),
                            cursorBrush = SolidColor(colors.blueText),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(13.dp))
                                        .background(colors.bgPage)
                                        .border(1.dp, colors.border, RoundedCornerShape(13.dp))
                                        .padding(horizontal = 12.dp),
                                    contentAlignment = Alignment.CenterStart,
                                ) {
                                    if (telephone.isEmpty()) {
                                        Text(
                                            text = stringResource(Res.string.employer_info_placeholder_tel),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = colors.textMuted,
                                            ),
                                        )
                                    }
                                    innerTextField()
                                }
                            },
                        )
                    }
                }

                // Mobile Phone
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.employer_info_mobile),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textSecondary,
                            fontSize = 11.sp,
                        ),
                        modifier = Modifier.padding(bottom = 5.dp),
                    )
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        BasicTextField(
                            value = mobile,
                            onValueChange = { onMobileChanged(it.filter { c -> c.isDigit() }.take(11)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary,
                            ),
                            cursorBrush = SolidColor(colors.blueText),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(13.dp))
                                        .background(colors.bgPage)
                                        .animatedErrorBorder(
                                            isError = isMobileError,
                                            errorColor = colors.dangerText,
                                            normalColor = colors.border,
                                            borderWidth = Thickness.border,
                                            cornerRadius = FieldCorner,
                                        )
                                        .padding(horizontal = 12.dp),
                                    contentAlignment = Alignment.CenterStart,
                                ) {
                                    if (mobile.isEmpty()) {
                                        Text(
                                            text = stringResource(Res.string.employer_info_placeholder_mobile),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = colors.textMuted,
                                            ),
                                        )
                                    }
                                    innerTextField()
                                }
                            },
                        )
                    }
                }
            }

            // Email
            Column {
                Text(
                    text = stringResource(Res.string.employer_info_email),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary,
                        fontSize = 11.sp,
                    ),
                    modifier = Modifier.padding(bottom = 5.dp),
                )
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    BasicTextField(
                        value = email,
                        onValueChange = { onEmailChanged(it.trim()) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary,
                        ),
                        cursorBrush = SolidColor(colors.blueText),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(13.dp))
                                    .background(colors.bgPage)
                                    .animatedErrorBorder(
                                        isError = isEmailError,
                                        errorColor = colors.dangerText,
                                        normalColor = colors.border,
                                        borderWidth = Thickness.border,
                                        cornerRadius = FieldCorner,
                                    )
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                if (email.isEmpty()) {
                                    Text(
                                        text = "info@company.ir",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = colors.textMuted,
                                        ),
                                    )
                                }
                                innerTextField()
                            }
                        },
                    )
                }
            }
        }

        // Error message line
        if (!errorMessage.isNullOrBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = colors.dangerText,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = colors.dangerText,
                        fontSize = 11.sp,
                    ),
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Submit Button
        EmployerInfoSubmitButton(
            text = stringResource(Res.string.employer_info_btn_send_otp),
            icon = Icons.Outlined.Email,
            enabled = canSubmit,
            isSubmitting = isSubmitting,
            onSubmit = onSubmit,
        )
    }

    if (showDatePicker) {
        TaminJalaliDatePicker(
            title = stringResource(Res.string.employer_info_ceo_birth_picker_title),
            onDismiss = { showDatePicker = false },
            onConfirm = { year, month, day ->
                val millis = PersianDateFormatter.toEpochMillis(year, month, day)
                val persianStr = PersianDateFormatter.format(year, month, day)
                onCeoBirthDateSelected(millis, persianStr)
                showDatePicker = false
            },
        )
    }
}

@Composable
private fun SelectPickerChip(
    text: String,
    isSelected: Boolean,
    isError: Boolean = false,
    onClick: () -> Unit,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(colors.bgPage)
            .animatedErrorBorder(
                isError = isError,
                errorColor = colors.dangerText,
                normalColor = colors.border,
                borderWidth = Thickness.border,
                cornerRadius = FieldCorner,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (isSelected) colors.textPrimary else colors.textMuted,
                fontSize = 12.sp,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_back),
            contentDescription = null,
            tint = colors.textMuted,
            modifier = Modifier.size(14.dp).rotate(CHEVRON_DOWN_DEGREES),
        )
    }
}

/**
 * `ic_tamin_chevron_back` is auto-mirrored, so on a right-to-left page it already draws pointing
 * right; a quarter turn clockwise from there points it down at the list it opens. Turning the
 * other way is what left these carets upside down. Same value core-ui's own `PickerRow` uses.
 */
private const val CHEVRON_DOWN_DEGREES = 90f

private const val LEGAL_NATIONAL_ID_SLOTS = 11
private const val CEO_NATIONAL_ID_SLOTS = 10

/** The radius every typed field in this form shares, matching the segmented fields beside them. */
private val FieldCorner = 13.dp
