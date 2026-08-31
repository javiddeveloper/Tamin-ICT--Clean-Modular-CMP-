package com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.ActiveBottomSheet
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.common.ProvincePR
import com.tamin.taminhamrah.model.contracts.BranchPR
import com.tamin.taminhamrah.model.employerInfo.COMPANY_TYPES
import com.tamin.taminhamrah.model.employerInfo.CompanyTypePR
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheet
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetConfig
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetItem
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetStyle
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetType
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.employer_info_company_type_label
import taminx.core.core_ui.employer_info_sheet_branch
import taminx.core.core_ui.employer_info_sheet_city
import taminx.core.core_ui.employer_info_sheet_province

/**
 * The four pickers, drawn by core-ui's shared [TaminBottomSheet].
 *
 * The design's sheet differs from that component's default look — page-colored, centred title, no
 * close button, rows grouped in a bordered card, and a tap that selects and closes rather than
 * arming a submit button — so those differences are passed as a [TaminBottomSheetStyle]. Every
 * default in that style is the shared sheet's existing appearance, so the twelve other screens
 * using it are untouched.
 */
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

    val title = stringResource(
        when (activeBottomSheet) {
            ActiveBottomSheet.COMPANY_TYPE -> Res.string.employer_info_company_type_label
            ActiveBottomSheet.PROVINCE -> Res.string.employer_info_sheet_province
            ActiveBottomSheet.CITY -> Res.string.employer_info_sheet_city
            ActiveBottomSheet.BRANCH -> Res.string.employer_info_sheet_branch
        }
    )

    // Code and label together: the code decides which row reads as selected, the label is drawn.
    val rows: List<Pair<String, String>> = when (activeBottomSheet) {
        ActiveBottomSheet.COMPANY_TYPE -> COMPANY_TYPES.map { it.code to stringResource(it.titleRes) }
        ActiveBottomSheet.PROVINCE -> provinces.map { it.provinceCode to it.provinceName }
        ActiveBottomSheet.CITY -> cities.map { it.cityCode to it.cityName }
        ActiveBottomSheet.BRANCH -> branches.map { it.code to it.name }
    }

    val selectedCode = when (activeBottomSheet) {
        ActiveBottomSheet.COMPANY_TYPE -> selectedCompanyType?.code
        ActiveBottomSheet.PROVINCE -> selectedProvince?.provinceCode
        ActiveBottomSheet.CITY -> selectedCity?.cityCode
        ActiveBottomSheet.BRANCH -> selectedBranch?.code
    }

    val isLoading = when (activeBottomSheet) {
        ActiveBottomSheet.COMPANY_TYPE -> false
        ActiveBottomSheet.PROVINCE -> isProvincesLoading && provinces.isEmpty()
        ActiveBottomSheet.CITY -> isCitiesLoading && cities.isEmpty()
        ActiveBottomSheet.BRANCH -> isBranchesLoading && branches.isEmpty()
    }

    TaminBottomSheet(
        config = TaminBottomSheetConfig(
            title = title,
            type = activeBottomSheet.sheetType(),
            items = rows.mapIndexed { index, (code, label) ->
                TaminBottomSheetItem(id = index, title = label, isSelected = code == selectedCode)
            },
            singleSelection = true,
            isLoading = isLoading,
        ),
        onDismissRequest = onDismiss,
        onSubmit = { result ->
            val index = result.selectedItemIds.firstOrNull()
            if (index != null) {
                when (activeBottomSheet) {
                    ActiveBottomSheet.COMPANY_TYPE -> COMPANY_TYPES.getOrNull(index)?.let(onSelectCompanyType)
                    ActiveBottomSheet.PROVINCE -> provinces.getOrNull(index)?.let(onSelectProvince)
                    ActiveBottomSheet.CITY -> cities.getOrNull(index)?.let(onSelectCity)
                    ActiveBottomSheet.BRANCH -> branches.getOrNull(index)?.let(onSelectBranch)
                }
            }
        },
        style = TaminBottomSheetStyle(
            containerColor = LocalTaminColors.current.bgPage,
            titleAlignment = Alignment.CenterHorizontally,
            showCloseButton = false,
            showRowIcon = false,
            groupRowsInCard = true,
            selectOnTap = true,
            showSubmitButton = false,
            // نوع شرکت is short and unsearched, but the design still draws it as rows, not chips.
            showRowList = true,
        ),
        loadingContent = {
            EmployerInfoSheetShimmer(
                itemCount = SHEET_SHIMMER_ROWS,
                modifier = Modifier.padding(vertical = Spacing.sm),
            )
        },
    )
}

/** The shared sheet keys its own search affordance off the type; these three are searchable. */
private fun ActiveBottomSheet.sheetType(): TaminBottomSheetType = when (this) {
    ActiveBottomSheet.PROVINCE -> TaminBottomSheetType.PROVINCE
    ActiveBottomSheet.CITY -> TaminBottomSheetType.CITY
    ActiveBottomSheet.BRANCH -> TaminBottomSheetType.BRANCH
    ActiveBottomSheet.COMPANY_TYPE -> TaminBottomSheetType.CUSTOM
}

private const val SHEET_SHIMMER_ROWS = 6
