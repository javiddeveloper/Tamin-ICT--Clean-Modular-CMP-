package com.tamin.taminhamrah.feature.fractionContract.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.feature.fractionContract.ui.preview.FractionContractPreviewData
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.contractFlow.UserInfoFormPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.contractFlow.UserInfoStepContent
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.fraction_contract_mobile_locked_hint
import taminx.core.core_ui.fraction_contract_user_info_section_subtitle
import taminx.core.core_ui.fraction_contract_user_info_section_title

@Composable
internal fun FractionUserInfoStep(
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
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Text(
            text = stringResource(Res.string.fraction_contract_user_info_section_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
        )
        Text(
            text = stringResource(Res.string.fraction_contract_user_info_section_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
        )
        UserInfoStepContent(
            userInfo = userInfo,
            cities = cities,
            isCitiesLoading = isCitiesLoading,
            onCitySelected = onCitySelected,
            onAddressChange = onAddressChange,
            onZipCodeChange = onZipCodeChange,
            onPhoneNumberChange = onPhoneNumberChange,
            isLoading = isLoading,
            hideMobileWhenEmpty = true,
        )
        if (userInfo.showMobile) {
            Text(
                text = stringResource(Res.string.fraction_contract_mobile_locked_hint),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun FractionUserInfoStepPreview() {
    PreviewRtlThemeContent {
        FractionUserInfoStep(
            userInfo = FractionContractPreviewData.userInfo,
            cities = FractionContractPreviewData.cities,
            isCitiesLoading = false,
            onCitySelected = {},
            onAddressChange = {},
            onZipCodeChange = {},
            onPhoneNumberChange = {},
        )
    }
}
