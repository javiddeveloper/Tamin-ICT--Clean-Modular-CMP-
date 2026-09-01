package com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.common.ProvincePR
import com.tamin.taminhamrah.model.contracts.BranchPR
import com.tamin.taminhamrah.ui.components.SegmentedInputField
import com.tamin.taminhamrah.ui.components.animatedErrorBorder
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.employer_info_branch
import taminx.core.core_ui.employer_info_branch_needs_city
import taminx.core.core_ui.employer_info_btn_send_otp
import taminx.core.core_ui.employer_info_city_label
import taminx.core.core_ui.employer_info_city_needs_province
import taminx.core.core_ui.employer_info_email
import taminx.core.core_ui.employer_info_province_label
import taminx.core.core_ui.employer_info_real_code_label
import taminx.core.core_ui.employer_info_real_hint
import taminx.core.core_ui.employer_info_sheet_branch
import taminx.core.core_ui.employer_info_sheet_city
import taminx.core.core_ui.employer_info_sheet_province
import taminx.core.core_ui.ic_tamin_chevron_back

@Composable
fun RealWorkshopFormSection(
    workshopCode: String,
    onWorkshopCodeChanged: (String) -> Unit,
    userEmail: String,
    selectedProvince: ProvincePR?,
    onOpenProvincePicker: () -> Unit,
    selectedCity: CityPR?,
    onOpenCityPicker: () -> Unit,
    selectedBranch: BranchPR?,
    onOpenBranchPicker: () -> Unit,
    workshopCodeError: String?,
    provinceError: String?,
    cityError: String?,
    branchError: String?,
    isSubmitting: Boolean,
    canSubmit: Boolean,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    // Each field is red exactly when it has a message to show underneath, so the border and the
    // reason can never disagree.
    val isCodeError = workshopCodeError != null
    val isProvinceError = provinceError != null
    val isCityError = cityError != null
    val isBranchError = branchError != null

    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        // One card: code, e-mail and the three pickers, as the design draws them
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = CardElevation,
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
            // Code Input
            Column {
                Text(
                    text = stringResource(Res.string.employer_info_real_code_label),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary,
                        fontSize = 11.sp,
                    ),
                    modifier = Modifier.padding(bottom = 5.dp),
                )
                SegmentedInputField(
                    value = workshopCode,
                    onValueChange = onWorkshopCodeChanged,
                    slotCount = WORKSHOP_CODE_SLOTS,
                    error = isCodeError,
                    errorMessage = workshopCodeError,
                )
            }

            // Email (Read-only from Profile)
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(colors.bgPage)
                        .border(1.dp, colors.border, RoundedCornerShape(13.dp))
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Text(
                            text = userEmail.ifBlank { "tamin@tamin.ir" },
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = colors.textMuted,
                        modifier = Modifier.size(15.dp),
                    )
                }
            }

            // Province & City 2-column row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                // Province
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.employer_info_province_label),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textSecondary,
                            fontSize = 11.sp,
                        ),
                        modifier = Modifier.padding(bottom = 5.dp),
                    )
                    SelectChip(
                        text = selectedProvince?.provinceName ?: stringResource(Res.string.employer_info_sheet_province),
                        isSelected = selectedProvince != null,
                        isError = isProvinceError,
                        errorMessage = provinceError,
                        onClick = onOpenProvincePicker,
                    )
                }

                // City (enabled only when province selected)
                val isCityEnabled = selectedProvince != null
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.employer_info_city_label),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textSecondary,
                            fontSize = 11.sp,
                        ),
                        modifier = Modifier.padding(bottom = 5.dp),
                    )
                    SelectChip(
                        text = selectedCity?.cityName ?: stringResource(
                            if (isCityEnabled) Res.string.employer_info_sheet_city else Res.string.employer_info_city_needs_province
                        ),
                        isSelected = selectedCity != null,
                        isEnabled = isCityEnabled,
                        isError = isCityError,
                        errorMessage = cityError,
                        onClick = { if (isCityEnabled) onOpenCityPicker() },
                    )
                }
            }

            // Branch (enabled only when city selected)
            val isBranchEnabled = selectedCity != null
            Column {
                Text(
                    text = stringResource(Res.string.employer_info_branch),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary,
                        fontSize = 11.sp,
                    ),
                    modifier = Modifier.padding(bottom = 5.dp),
                )
                SelectChip(
                    text = selectedBranch?.name ?: stringResource(
                        if (isBranchEnabled) Res.string.employer_info_sheet_branch else Res.string.employer_info_branch_needs_city
                    ),
                    isSelected = selectedBranch != null,
                    isEnabled = isBranchEnabled,
                    isError = isBranchError,
                    errorMessage = branchError,
                    onClick = { if (isBranchEnabled) onOpenBranchPicker() },
                )
            }
        }

        // Notice hint
        Text(
            text = stringResource(Res.string.employer_info_real_hint),
            style = MaterialTheme.typography.bodySmall.copy(
                color = colors.textSecondary,
                lineHeight = 22.sp,
                fontSize = 11.sp,
            ),
            textAlign = TextAlign.Justify,
        )

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
}

@Composable
private fun SelectChip(
    text: String,
    isSelected: Boolean,
    isEnabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    onClick: () -> Unit,
) {
    val colors = LocalTaminColors.current
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
    val bg = if (!isEnabled) colors.bgPage.copy(alpha = 0.5f) else colors.bgPage
    val fg = if (isSelected) colors.textPrimary else colors.textMuted
    val weight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .alpha(if (isEnabled) 1f else 0.6f)
            .clip(RoundedCornerShape(13.dp))
            .background(bg)
            .animatedErrorBorder(
                isError = isError,
                errorColor = colors.dangerText,
                normalColor = colors.border,
                borderWidth = Thickness.border,
                cornerRadius = ChipCorner,
            )
            .clickable(enabled = isEnabled, onClick = onClick)
            .padding(horizontal = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = weight,
                color = fg,
                fontSize = 11.5.sp,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_back),
            contentDescription = null,
            tint = colors.textMuted,
            modifier = Modifier.size(13.dp).rotate(CHEVRON_DOWN_DEGREES),
        )
        }

        FieldErrorText(errorMessage)
    }
}

/**
 * The reason a field is red, printed directly under it. A border alone says where, not why.
 */
@Composable
internal fun FieldErrorText(message: String?) {
    if (message.isNullOrBlank()) return
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = colors.dangerText,
            modifier = Modifier.size(IconSize.small),
        )
        Text(
            text = message,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = colors.dangerText,
            ),
        )
    }
}

/**
 * `ic_tamin_chevron_back` is auto-mirrored, so on a right-to-left page it already draws pointing
 * right; a quarter turn clockwise from there points it down at the list it opens. Turning the
 * other way is what left these carets upside down. Same value core-ui's own `PickerRow` uses.
 */
private const val CHEVRON_DOWN_DEGREES = 90f

private const val WORKSHOP_CODE_SLOTS = 10

private val CardElevation = 6.dp

/** The picker chips share the radius of the fields above them. */
private val ChipCorner = 13.dp
