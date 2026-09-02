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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.contractFlow.UserInfoFormPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminTextField
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.toPersianDigits
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
    } else {
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
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = colors.blueText,
                    modifier = Modifier.size(IconSize.medium),
                )
                Text(
                    text = stringResource(Res.string.contract_user_info_banner),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f),
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

                val isZipError = userInfo.zipCode.isNotEmpty() && userInfo.zipCode.length != 10
                TaminTextField(
                    value = userInfo.zipCode.toPersianDigits(),
                    onValueChange = onZipCodeChange,
                    label = stringResource(Res.string.contract_field_postal_code_required),
                    isError = isZipError,
                    errorMessage = if (isZipError) stringResource(Res.string.occurrence_field_postal_code_error) else null,
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f),
                )
            }

            // Row 2: Residential Address
            TaminTextField(
                value = userInfo.address,
                onValueChange = onAddressChange,
                label = stringResource(Res.string.contract_field_address_required),
                singleLine = false,
                minLines = 2,
                maxLines = 4,
            )

            // Row 3: Phone Number & Mobile (Readonly with Lock)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                TaminTextField(
                    value = userInfo.phoneNumber.toPersianDigits(),
                    onValueChange = onPhoneNumberChange,
                    label = stringResource(Res.string.contract_field_phone_required),
                    keyboardType = KeyboardType.Phone,
                    modifier = Modifier.weight(1f),
                )

                TaminTextField(
                    value = userInfo.mobileNumber.toPersianDigits(),
                    onValueChange = {},
                    label = stringResource(Res.string.contract_field_mobile_readonly),
                    enabled = false,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = colors.textMuted,
                            modifier = Modifier.size(IconSize.small),
                        )
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

// -------------------------------------------------------------------------
// Previews
// -------------------------------------------------------------------------

@PreviewRtlTheme
@Composable
private fun UserInfoStepContentPopulatedPreview() {
    PreviewRtlThemeContent {
        UserInfoStepContent(
            userInfo = UserInfoFormPR(
                cityCode = "021",
                cityName = "مشهد",
                address = "مشهد، بلوار وکیل‌آباد، نبش وکیل‌آباد ۵۲",
                zipCode = "9187654321",
                phoneNumber = "05832245678",
                mobileNumber = "09143018372",
            ),
            cities = listOf(
                CityPR(cityCode = "021", cityName = "مشهد", provinceCode = "021"),
            ),
            isCitiesLoading = false,
            onCitySelected = {},
            onAddressChange = {},
            onZipCodeChange = {},
            onPhoneNumberChange = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun UserInfoStepContentEmptyPreview() {
    PreviewRtlThemeContent {
        UserInfoStepContent(
            userInfo = UserInfoFormPR(
                mobileNumber = "09143018372",
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
