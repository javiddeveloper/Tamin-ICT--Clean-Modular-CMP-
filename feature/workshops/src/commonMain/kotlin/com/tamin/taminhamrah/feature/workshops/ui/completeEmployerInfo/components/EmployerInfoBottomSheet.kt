package com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.ActiveBottomSheet
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.common.ProvincePR
import com.tamin.taminhamrah.model.contracts.BranchPR
import com.tamin.taminhamrah.model.employerInfo.COMPANY_TYPES
import com.tamin.taminhamrah.model.employerInfo.CompanyTypePR
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployerInfoBottomSheet(
    activeBottomSheet: ActiveBottomSheet?,
    onDismiss: () -> Unit,
    selectedCompanyType: CompanyTypePR?,
    onSelectCompanyType: (CompanyTypePR) -> Unit,
    provinces: ImmutableList<ProvincePR>,
    isProvincesLoading: Boolean = false,
    selectedProvince: ProvincePR?,
    onSelectProvince: (ProvincePR) -> Unit,
    cities: ImmutableList<CityPR>,
    isCitiesLoading: Boolean = false,
    selectedCity: CityPR?,
    onSelectCity: (CityPR) -> Unit,
    branches: ImmutableList<BranchPR>,
    isBranchesLoading: Boolean = false,
    selectedBranch: BranchPR?,
    onSelectBranch: (BranchPR) -> Unit,
) {
    if (activeBottomSheet == null) return

    val colors = LocalTaminColors.current
    val title = when (activeBottomSheet) {
        ActiveBottomSheet.COMPANY_TYPE -> "نوع شرکت"
        ActiveBottomSheet.PROVINCE -> "انتخاب استان"
        ActiveBottomSheet.CITY -> "انتخاب شهر"
        ActiveBottomSheet.BRANCH -> "انتخاب شعبه"
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.bgSurface,
        shape = RoundedCornerShape(topStart = CornerRadius.sheet, topEnd = CornerRadius.sheet),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = Spacing.md),
        ) {
            // Sheet Title
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                ),
                modifier = Modifier.padding(horizontal = Spacing.xl, vertical = Spacing.sm),
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(colors.divider),
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            val isCurrentLoading = when (activeBottomSheet) {
                ActiveBottomSheet.PROVINCE -> isProvincesLoading && provinces.isEmpty()
                ActiveBottomSheet.CITY -> isCitiesLoading && cities.isEmpty()
                ActiveBottomSheet.BRANCH -> isBranchesLoading && branches.isEmpty()
                ActiveBottomSheet.COMPANY_TYPE -> false
            }

            if (isCurrentLoading) {
                EmployerInfoSheetShimmer(
                    itemCount = 6,
                    modifier = Modifier.padding(vertical = Spacing.sm),
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                ) {
                    when (activeBottomSheet) {
                        ActiveBottomSheet.COMPANY_TYPE -> {
                            items(COMPANY_TYPES, key = { it.code }) { item ->
                                val isSelected = selectedCompanyType?.code == item.code
                                SheetItemRow(
                                    title = item.title,
                                    isSelected = isSelected,
                                    onClick = { onSelectCompanyType(item) },
                                )
                            }
                        }
                        ActiveBottomSheet.PROVINCE -> {
                            items(provinces, key = { it.provinceCode }) { item ->
                                val isSelected = selectedProvince?.provinceCode == item.provinceCode
                                SheetItemRow(
                                    title = item.provinceName,
                                    isSelected = isSelected,
                                    onClick = { onSelectProvince(item) },
                                )
                            }
                        }
                        ActiveBottomSheet.CITY -> {
                            items(cities, key = { it.cityCode }) { item ->
                                val isSelected = selectedCity?.cityCode == item.cityCode
                                SheetItemRow(
                                    title = item.cityName,
                                    isSelected = isSelected,
                                    onClick = { onSelectCity(item) },
                                )
                            }
                        }
                        ActiveBottomSheet.BRANCH -> {
                            items(branches, key = { it.code }) { item ->
                                val isSelected = selectedBranch?.code == item.code
                                SheetItemRow(
                                    title = item.name,
                                    isSelected = isSelected,
                                    onClick = { onSelectBranch(item) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SheetItemRow(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val bg = if (isSelected) colors.blueBg else Color.Transparent
    val fg = if (isSelected) colors.blueText else colors.textPrimary
    val weight = if (isSelected) FontWeight.Bold else FontWeight.Medium

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(bg)
            .padding(horizontal = Spacing.xl, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = weight,
                color = fg,
                fontSize = 13.sp,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = colors.blueText,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
