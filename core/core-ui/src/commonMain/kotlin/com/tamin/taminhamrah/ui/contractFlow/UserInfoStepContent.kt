package com.tamin.taminhamrah.ui.contractFlow

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.contractFlow.UserInfoFormPR
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_field_address_required
import taminx.core.core_ui.contract_field_mobile_readonly
import taminx.core.core_ui.contract_field_phone_required
import taminx.core.core_ui.contract_field_postal_code_required
import taminx.core.core_ui.contract_field_residence_city
import taminx.core.core_ui.contract_user_info_banner
import taminx.core.core_ui.occurrence_field_postal_code_error

@Composable
fun UserInfoStepContent(
    userInfo: UserInfoFormPR,
    cities: List<CityPR>,
    isCitiesLoading: Boolean,
    onCitySelected: (CityPR) -> Unit,
    onAddressChange: (String) -> Unit,
    onZipCodeChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
) {
    if (isLoading) {
        UserInfoStepShimmerSkeleton(modifier = modifier)
        return
    }

    val colors = LocalTaminColors.current
    val cardShape = RoundedCornerShape(CornerRadius.x2l)
    val bannerShape = RoundedCornerShape(CornerRadius.card)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(colors.bgSurface)
            .border(Thickness.border, colors.border, cardShape)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        // Blue Info Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(bannerShape)
                .background(colors.blueBg)
                .border(Thickness.border, colors.blueText.copy(alpha = 0.20f), bannerShape)
                .padding(horizontal = Spacing.md, vertical = Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = stringResource(Res.string.contract_user_info_banner),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = colors.blueText,
                modifier = Modifier.size(IconSize.medium),
            )
        }

        // Row 1: City Selector & Postal Code
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            SelectableField(
                label = stringResource(Res.string.contract_field_residence_city),
                options = cities,
                selectedCode = userInfo.cityCode,
                selectedName = userInfo.cityName,
                optionCode = { it.cityCode },
                optionName = { it.cityName },
                isLoading = isCitiesLoading,
                onSelected = onCitySelected,
                modifier = Modifier.weight(1f),
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                val isZipError = userInfo.zipCode.isNotEmpty() && userInfo.zipCode.length != 10
                OutlinedTextField(
                    value = userInfo.zipCode,
                    onValueChange = onZipCodeChange,
                    label = { Text(stringResource(Res.string.contract_field_postal_code_required)) },
                    singleLine = true,
                    isError = isZipError,
                    shape = RoundedCornerShape(CornerRadius.lg),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.blueText,
                        unfocusedBorderColor = colors.border,
                        errorBorderColor = colors.dangerText,
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary,
                        cursorColor = colors.blueText,
                        focusedContainerColor = colors.bgSurface,
                        unfocusedContainerColor = colors.bgSurface,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                if (isZipError) {
                    Text(
                        text = stringResource(Res.string.occurrence_field_postal_code_error),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.dangerText,
                        modifier = Modifier.padding(horizontal = Spacing.xs),
                    )
                }
            }
        }

        // Row 2: Residential Address
        OutlinedTextField(
            value = userInfo.address,
            onValueChange = onAddressChange,
            label = { Text(stringResource(Res.string.contract_field_address_required)) },
            minLines = 2,
            maxLines = 4,
            shape = RoundedCornerShape(CornerRadius.lg),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.blueText,
                unfocusedBorderColor = colors.border,
                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary,
                cursorColor = colors.blueText,
                focusedContainerColor = colors.bgSurface,
                unfocusedContainerColor = colors.bgSurface,
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        // Row 3: Phone Number & Mobile (Readonly with Lock)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            OutlinedTextField(
                value = userInfo.phoneNumber,
                onValueChange = onPhoneNumberChange,
                label = { Text(stringResource(Res.string.contract_field_phone_required)) },
                singleLine = true,
                shape = RoundedCornerShape(CornerRadius.lg),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.blueText,
                    unfocusedBorderColor = colors.border,
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary,
                    cursorColor = colors.blueText,
                    focusedContainerColor = colors.bgSurface,
                    unfocusedContainerColor = colors.bgSurface,
                ),
                modifier = Modifier.weight(1f),
            )

            OutlinedTextField(
                value = userInfo.mobileNumber,
                onValueChange = {},
                label = { Text(stringResource(Res.string.contract_field_mobile_readonly)) },
                singleLine = true,
                enabled = false,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = colors.textMuted,
                        modifier = Modifier.size(IconSize.small),
                    )
                },
                shape = RoundedCornerShape(CornerRadius.lg),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = colors.border,
                    disabledTextColor = colors.textSecondary,
                    disabledLabelColor = colors.textMuted,
                    disabledContainerColor = colors.bgPage,
                ),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// -------------------------------------------------------------------------
// Previews
// -------------------------------------------------------------------------

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun UserInfoStepContentPopulatedPreview() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        UserInfoStepContent(
            userInfo = UserInfoFormPR(
                cityCode = "021",
                cityName = "تهران",
                address = "خیابان آزادی، خیابان استاد معین، پلاک ۱۲",
                zipCode = "1234567890",
                phoneNumber = "02166001234",
                mobileNumber = "09121234567",
            ),
            cities = listOf(
                CityPR(cityCode = "021", cityName = "تهران", provinceCode = "021"),
                CityPR(cityCode = "031", cityName = "اصفهان", provinceCode = "031"),
            ),
            isCitiesLoading = false,
            onCitySelected = {},
            onAddressChange = {},
            onZipCodeChange = {},
            onPhoneNumberChange = {},
        )
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun UserInfoStepContentEmptyPreview() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        UserInfoStepContent(
            userInfo = UserInfoFormPR(
                mobileNumber = "09121234567",
            ),
            cities = emptyList(),
            isCitiesLoading = false,
            onCitySelected = {},
            onAddressChange = {},
            onZipCodeChange = {},
            onPhoneNumberChange = {},
        )
    }
}
