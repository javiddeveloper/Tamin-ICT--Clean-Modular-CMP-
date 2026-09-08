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
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.common.ProvincePR
import com.tamin.taminhamrah.model.contractFlow.BranchSelectionFormPR
import com.tamin.taminhamrah.model.contracts.BranchPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetType
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.bs_branch
import taminx.core.core_ui.bs_city
import taminx.core.core_ui.bs_province
import taminx.core.core_ui.contract_branch_info_banner
import taminx.core.core_ui.contract_branch_warning_banner

@Composable
fun SelectBranchStepContent(
    branchSelection: BranchSelectionFormPR,
    provinces: List<ProvincePR>,
    cities: List<CityPR>,
    branches: List<BranchPR>,
    isProvincesLoading: Boolean,
    isCitiesLoading: Boolean,
    isBranchesLoading: Boolean,
    onProvinceSelected: (ProvincePR) -> Unit,
    onCitySelected: (CityPR) -> Unit,
    onBranchSelected: (BranchPR) -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
    provincesError: String? = null,
    citiesError: String? = null,
    branchesError: String? = null,
    onRetryProvinces: (() -> Unit)? = null,
    onRetryCities: (() -> Unit)? = null,
    onRetryBranches: (() -> Unit)? = null,
) {
    if (isLoading) {
        SelectBranchStepShimmerSkeleton(modifier = modifier)
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
            // Warning Banner (Top)
            val warningBg = Color(0xFFFFFBEB)
            val warningBorder = Color(0xFFFDE68A)
            val warningText = Color(0xFFB45309)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(bannerShape)
                    .background(warningBg)
                    .border(Thickness.border, warningBorder, bannerShape)
                    .padding(horizontal = Spacing.md, vertical = Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                Icon(
                    imageVector = Icons.Outlined.WarningAmber,
                    contentDescription = null,
                    tint = warningText,
                    modifier = Modifier.size(IconSize.medium),
                )
                Text(
                    text = stringResource(Res.string.contract_branch_warning_banner),
                    style = MaterialTheme.typography.bodySmall,
                    color = warningText,
                    modifier = Modifier.weight(1f),
                )
            }

            // Province selector
            SelectableField(
                label = stringResource(Res.string.bs_province) + " *",
                options = provinces,
                selectedCode = branchSelection.provinceCode,
                selectedName = branchSelection.provinceName,
                optionCode = { it.provinceCode },
                optionName = { it.provinceName },
                isLoading = isProvincesLoading,
                onSelected = onProvinceSelected,
                errorMessage = provincesError,
                onRetry = onRetryProvinces,
                sheetType = TaminBottomSheetType.PROVINCE,
            )

            // City selector
            SelectableField(
                label = stringResource(Res.string.bs_city) + " *",
                options = cities,
                selectedCode = branchSelection.cityCode,
                selectedName = branchSelection.cityName,
                optionCode = { it.cityCode },
                optionName = { it.cityName },
                isLoading = isCitiesLoading,
                enabled = branchSelection.provinceCode.isNotBlank(),
                onSelected = onCitySelected,
                errorMessage = citiesError,
                onRetry = onRetryCities,
                sheetType = TaminBottomSheetType.CITY,
            )

            // Branch selector
            SelectableField(
                label = stringResource(Res.string.bs_branch) + " *",
                options = branches,
                selectedCode = branchSelection.branchCode,
                selectedName = branchSelection.branchName,
                optionCode = { it.code },
                optionName = { it.name },
                isLoading = isBranchesLoading,
                enabled = branchSelection.cityCode.isNotBlank(),
                onSelected = onBranchSelected,
                errorMessage = branchesError,
                onRetry = onRetryBranches,
                sheetType = TaminBottomSheetType.BRANCH,
            )

            // Info Banner (Bottom)
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
                    text = stringResource(Res.string.contract_branch_info_banner),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
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
private fun SelectBranchStepContentPreview() {
    PreviewRtlThemeContent {
        SelectBranchStepContent(
            branchSelection = BranchSelectionFormPR(
                provinceCode = "021",
                provinceName = "تهران",
                cityCode = "021",
                cityName = "تهران",
                branchCode = "001",
                branchName = "شعبه ۱ تهران (شهدای هفتم تیر)",
            ),
            provinces = listOf(ProvincePR(provinceCode = "021", provinceName = "تهران")),
            cities = listOf(CityPR(cityCode = "021", cityName = "تهران", provinceCode = "021")),
            branches = listOf(BranchPR(code = "001", name = "شعبه ۱ تهران (شهدای هفتم تیر)")),
            isProvincesLoading = false,
            isCitiesLoading = false,
            isBranchesLoading = false,
            onProvinceSelected = {},
            onCitySelected = {},
            onBranchSelected = {},
        )
    }
}
