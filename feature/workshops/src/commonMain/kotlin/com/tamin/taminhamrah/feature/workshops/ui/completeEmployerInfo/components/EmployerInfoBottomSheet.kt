package com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.tamin.taminhamrah.ui.theme.Thickness
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.employer_info_company_type_label
import taminx.core.core_ui.employer_info_sheet_branch
import taminx.core.core_ui.employer_info_sheet_city
import taminx.core.core_ui.employer_info_sheet_province

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
    val title = stringResource(
        when (activeBottomSheet) {
            ActiveBottomSheet.COMPANY_TYPE -> Res.string.employer_info_company_type_label
            ActiveBottomSheet.PROVINCE -> Res.string.employer_info_sheet_province
            ActiveBottomSheet.CITY -> Res.string.employer_info_sheet_city
            ActiveBottomSheet.BRANCH -> Res.string.employer_info_sheet_branch
        }
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        // The design's sheet sits on the page color and holds a white card of rows, rather than
        // being one white surface.
        containerColor = colors.bgPage,
        shape = RoundedCornerShape(topStart = SheetCorner, topEnd = SheetCorner),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = Spacing.smd, bottom = Spacing.smd)
                    .size(width = GrabberWidth, height = GrabberHeight)
                    .clip(RoundedCornerShape(GrabberHeight))
                    .background(colors.chevron),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = Spacing.page)
                .padding(bottom = Spacing.xl),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.md),
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
                        .heightIn(max = 380.dp)
                        .clip(RoundedCornerShape(CornerRadius.xl))
                        .background(colors.bgSurface)
                        .border(
                            width = Thickness.border,
                            color = colors.border,
                            shape = RoundedCornerShape(CornerRadius.xl),
                        ),
                ) {
                    when (activeBottomSheet) {
                        ActiveBottomSheet.COMPANY_TYPE -> {
                            items(COMPANY_TYPES, key = { it.code }) { item ->
                                val isSelected = selectedCompanyType?.code == item.code
                                SheetItemRow(
                                    title = stringResource(item.titleRes),
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
    val weight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
    val dividerColor = colors.divider
    val dividerThickness = Thickness.border

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = RowMinHeight)
            .clickable(onClick = onClick)
            .background(bg)
            .drawBehind {
                val stroke = dividerThickness.toPx()
                drawLine(
                    color = dividerColor,
                    start = Offset(0f, size.height - stroke / 2),
                    end = Offset(size.width, size.height - stroke / 2),
                    strokeWidth = stroke,
                )
            }
            .padding(horizontal = Spacing.smd, vertical = 11.dp),
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

private val SheetCorner = 28.dp
private val GrabberWidth = 40.dp
private val GrabberHeight = 4.dp
private val RowMinHeight = 50.dp
