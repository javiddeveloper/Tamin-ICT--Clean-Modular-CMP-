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
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminSearchableListSheet
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
 * The company-type and branch pickers, drawn by core-ui's shared [TaminBottomSheet].
 *
 * Province and city are searched/paginated against the server and render through
 * [EmployerLocationPickerSheet] ([TaminSearchableListSheet]) instead — see that composable.
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
    branches: ImmutableList<BranchPR>,
    isBranchesLoading: Boolean = false,
    selectedBranch: BranchPR?,
    onSelectBranch: (BranchPR) -> Unit,
) {
    if (activeBottomSheet == null || activeBottomSheet == ActiveBottomSheet.PROVINCE || activeBottomSheet == ActiveBottomSheet.CITY) return

    val title = stringResource(
        when (activeBottomSheet) {
            ActiveBottomSheet.COMPANY_TYPE -> Res.string.employer_info_company_type_label
            ActiveBottomSheet.BRANCH -> Res.string.employer_info_sheet_branch
            ActiveBottomSheet.PROVINCE, ActiveBottomSheet.CITY -> return
        }
    )

    // Code and label together: the code decides which row reads as selected, the label is drawn.
    val rows: List<Pair<String, String>> = when (activeBottomSheet) {
        ActiveBottomSheet.COMPANY_TYPE -> COMPANY_TYPES.map { it.code to stringResource(it.titleRes) }
        ActiveBottomSheet.BRANCH -> branches.map { it.code to it.name }
        ActiveBottomSheet.PROVINCE, ActiveBottomSheet.CITY -> return
    }

    val selectedCode = when (activeBottomSheet) {
        ActiveBottomSheet.COMPANY_TYPE -> selectedCompanyType?.code
        ActiveBottomSheet.BRANCH -> selectedBranch?.code
        ActiveBottomSheet.PROVINCE, ActiveBottomSheet.CITY -> return
    }

    val isLoading = when (activeBottomSheet) {
        ActiveBottomSheet.COMPANY_TYPE -> false
        ActiveBottomSheet.BRANCH -> isBranchesLoading && branches.isEmpty()
        ActiveBottomSheet.PROVINCE, ActiveBottomSheet.CITY -> return
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
                    ActiveBottomSheet.BRANCH -> branches.getOrNull(index)?.let(onSelectBranch)
                    ActiveBottomSheet.PROVINCE, ActiveBottomSheet.CITY -> Unit
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
            highlightSelectedRow = true,
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

/** The shared sheet keys its own search affordance off the type; branch is searchable. */
private fun ActiveBottomSheet.sheetType(): TaminBottomSheetType = when (this) {
    ActiveBottomSheet.BRANCH -> TaminBottomSheetType.BRANCH
    ActiveBottomSheet.COMPANY_TYPE -> TaminBottomSheetType.CUSTOM
    ActiveBottomSheet.PROVINCE, ActiveBottomSheet.CITY -> TaminBottomSheetType.CUSTOM
}

/**
 * Province and city, paginated against the server via [TaminSearchableListSheet]. Neither
 * `proxy/models/province/` nor `special-insured-services/cities` has a working name filter (the
 * city one 404s — see [com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.CompleteEmployerInfoViewModel.cityBaseQuery]),
 * so both search boxes fall back to filtering whatever page has already loaded rather than
 * hitting the server.
 */
@Composable
fun EmployerLocationPickerSheet(
    activeBottomSheet: ActiveBottomSheet?,
    onDismiss: () -> Unit,
    provinces: ImmutableList<ProvincePR>,
    isProvincesLoading: Boolean,
    canLoadMoreProvinces: Boolean,
    isProvincesLoadingMore: Boolean,
    onProvinceLoadMore: () -> Unit,
    onSelectProvince: (ProvincePR) -> Unit,
    cities: ImmutableList<CityPR>,
    isCitiesLoading: Boolean,
    canLoadMoreCities: Boolean,
    isCitiesLoadingMore: Boolean,
    onCityLoadMore: () -> Unit,
    onSelectCity: (CityPR) -> Unit,
) {
    when (activeBottomSheet) {
        ActiveBottomSheet.PROVINCE -> TaminSearchableListSheet(
            title = stringResource(Res.string.employer_info_sheet_province),
            items = provinces,
            itemLabel = { it.provinceName },
            itemKey = { it.provinceCode },
            isLoading = isProvincesLoading,
            canLoadMore = canLoadMoreProvinces,
            isLoadingMore = isProvincesLoadingMore,
            onLoadMore = onProvinceLoadMore,
            onItemSelected = onSelectProvince,
            onDismiss = onDismiss,
        )
        ActiveBottomSheet.CITY -> TaminSearchableListSheet(
            title = stringResource(Res.string.employer_info_sheet_city),
            items = cities,
            itemLabel = { it.cityName },
            itemKey = { it.cityCode },
            isLoading = isCitiesLoading,
            canLoadMore = canLoadMoreCities,
            isLoadingMore = isCitiesLoadingMore,
            onLoadMore = onCityLoadMore,
            onItemSelected = onSelectCity,
            onDismiss = onDismiss,
        )
        ActiveBottomSheet.COMPANY_TYPE, ActiveBottomSheet.BRANCH, null -> Unit
    }
}

private const val SHEET_SHIMMER_ROWS = 6
